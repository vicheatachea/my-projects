from machine import ADC, Pin, UART, I2C
from piotimer import Piotimer
import urequests as requests 
from fifo import Fifo
import time
from led import Led
from ssd1306 import SSD1306_I2C
import micropython
import math
import ujson
import utime
import network
import mip
from time import sleep
from umqtt.simple import MQTTClient

micropython.alloc_emergency_exception_buf(200)


# OLED display size for text alignment
OLED_width = 128
OLED_height = 64

# Pins for rotary encoder
ENCODER_A_PIN = 10
ENCODER_B_PIN = 11
ENCODER_BUTTON_PIN = 12


def button_handler(pin):
    fifo.put(1)


i2c = I2C(1, scl=Pin(15), sda=Pin(14), freq=400000)

pin_nr = 27 #Where the Crowtail sensor is connected.
sample_rate = 250 #Sample rate of Crowtail.
threshold_percentage = 0.2 #2% threshold
prev_value = 0 #For peak detection.
index = 0 #For finding peak.
peak_found = False
hr_list = [] #For avarage HR.
ppi_list = [] #For average PPI.
peak_peak_avg = 0 #Variable to track peak_to_peak_average.
display = SSD1306_I2C(128, 64, I2C(1, scl=Pin(15), sda=Pin(14), freq=400000))
current_page = 0  # Current page indicator
update_time = utime.time() + 3  # Initial update time 3 seconds
average_hr = 0 #Inital value of HR
start_time = time.ticks_ms()  # Define start_time each time the function is called
menu_option = 0
values = None
SSID = "KMD657_Group_8"
PASSWORD = "JustYappin13"
BROKER_IP = "192.168.8.253"
message_sent = False


class isr_adc:
    def __init__(self, adc_pin_nr):
        self.av = ADC(adc_pin_nr)  # sensor AD channel
        self.samples = Fifo(500)  # fifo where ISR will put samples

    def handler(self, tid):
        self.samples.put(self.av.read_u16())



def count_sdnn(data_list):
    variance = 0
    mean = sum(data_list) / len(data_list)
    for data in data_list:
        variance += (data - mean) ** 2
    return round(math.sqrt(variance / len(data_list)))


def count_rmssd(data_list):
    squared_diff_sum = 0
    for data in range(len(data_list) - 1):
        successive_diff = data_list[data + 1] - data_list[data]
        squared_diff_sum += successive_diff**2
    mean_squared_diff = squared_diff_sum / len(data_list)
    rmssd = math.sqrt(mean_squared_diff)
    return round(rmssd)


def calc_threshold(threshold_percentage):
    min_value = min(adc.samples.data)
    max_value = max(adc.samples.data)
    amplitude = max_value - min_value
    threshold = max_value - threshold_percentage * amplitude
    return threshold

class RotaryEncoder:
    def __init__(self, in_a, in_b, button):
        self.a = Pin(in_a, mode=Pin.IN, pull=Pin.PULL_UP)
        self.b = Pin(in_b, mode=Pin.IN, pull=Pin.PULL_UP)
        self.button = Pin(button, mode=Pin.IN, pull=Pin.PULL_UP)
        self.value = 0
        self.cumulative_change = 0  
        self.change_threshold = 3  
        self.button_pressed = False
        self.button_last_pressed = 0
        self.a.irq(trigger=Pin.IRQ_RISING | Pin.IRQ_FALLING, handler=self._on_rotate)
        self.button.irq(trigger=Pin.IRQ_FALLING, handler=self._on_press)

    def _on_rotate(self, pin):
        direction = -1 if self.a.value() == self.b.value() else 1
        self.cumulative_change += direction

    def reset_cumulative_change(self):
        self.cumulative_change = 0  

    def _on_press(self, pin):
        current_time = utime.ticks_ms()
        if current_time - self.button_last_pressed > 500:
            if not self.button.value():
                self.button_pressed = True
                self.button_last_pressed = current_time

    def read(self):
        return self.value
    
    def reset_value(self):
        self.value = 0

    def read_button(self):
        if self.button_pressed:
            self.button_pressed = False
            return True
        return False


start_time = 0
# Rotary Encoder init
encoder = RotaryEncoder(ENCODER_A_PIN, ENCODER_B_PIN, ENCODER_BUTTON_PIN)


def calculate_vertical_positions(num_lines, line_height=12):
    total_height = num_lines * line_height
    start_y = (OLED_height - total_height) // 2
    return [start_y + i * line_height for i in range(num_lines)]


def center_text(text):
    # Calculate the x-coordinate to center the text horizontally
    x = int((OLED_width - len(text) * 8) / 2)
    return x


def calc_heart_rate(peak_index_in_data):
    global average_hr, ppi_list, peak_index
    ms_in_minute = 60000
    period_ms = 4  # 4ms samles with 250Hz. Meaning the time of a single sample in M.S.
    ppi_ms = period_ms * peak_index_in_data
    if 250 <= ppi_ms <= 2000:
        ppi_list.append(ppi_ms)
    if ppi_ms != 0:
        hr = round(ms_in_minute / ppi_ms)
        if 30 <= hr <= 240:
            hr_list.append(hr)
        if len(hr_list) > 4:
            average_hr = round(sum(hr_list) / 5)
            hr_list.clear()


def calc_peak():
    global peak_peak_avg, ppi_list
    if len(ppi_list) > 1:
        peak_peak_avg = round(sum(ppi_list) / len(ppi_list))
        return peak_peak_avg


def handle_encoder_input():
    global menu_option
    global encoder
    if current_page == 0:
        if abs(encoder.cumulative_change) >= encoder.change_threshold:  # Check if threshold is exceeded
            change_direction = 1 if encoder.cumulative_change > 0 else -1
            menu_option += change_direction
            menu_option = max(0, min(menu_option, 2))
            menu_option = display_menu(current_page, menu_option, average_hr)
            encoder.reset_cumulative_change()  # Reset the cumulative change after applying it

# Function to connect to WLAN
def connect_wlan():
    global SSID, PASSWORD, BROKER_IP
    # Connecting to the group WLAN
    wlan = network.WLAN(network.STA_IF)
    wlan.active(True)
    wlan.connect(SSID, PASSWORD)

def connect_mqtt():
    global SSID, PASSWORD, BROKER_IP, MQTTClient
    try:
        mqtt_client = MQTTClient("", BROKER_IP)
        mqtt_client.connect(clean_session=True)
        return mqtt_client

    except:
        pass

def mqttsending(x):
    global SSID, PASSWORD, BROKER_IP, values, MQTTClient
    connect_wlan()
    x = ujson.dumps(x)
    try:
        mqtt_client = connect_mqtt()
        topic = "pico/test"
        message = x
        mqtt_client.publish(topic, message)

    except Exception as e:
        print(f"Failed to send MQTT message: {e}")
        pass
        
        
adc = isr_adc(pin_nr)
tmr = Piotimer(mode=Piotimer.PERIODIC, freq=sample_rate, callback=adc.handler)       

def process_adc_data():
    global peak_found, prev_value, index, adc
    if not adc.samples.empty():
    # Read one value from fifo
        current_value = adc.samples.get()
    # Finding peak
        if current_value > calc_threshold(threshold_percentage):
            if prev_value > current_value and not peak_found:
                peak_index = index - 1
                peak_found = True
                # If true, then the peak has been found
                calc_heart_rate(peak_index)
                index = -1
        else:
            peak_found = False

        prev_value = current_value
        index += 1



start_time = None

def thirty_over():
    global start_time
    if start_time is None:
        start_time = time.time()
        return False
    elif time.time() - start_time >= 30:
        return True
    else:
        return False


history_file = "history.ujson"


def save_data(history):
    data = {"Mean HR": history[0], "Mean PPI": history[1], "RMSSD":history[2], "SDNN":history[3]}
    with open(history_file, "w") as file:
        ujson.dump(data, file)


def load_data():
    try:
        with open(history_file, "r") as file:
            return ujson.load(file)
    except:
        pass
        print("File not found. Creating a new file...")
        handle_no_data = ["no", "data", "yet", ":("]
        save_data(handle_no_data)  # Create a new file with default values
        return load_data()  # Retry loading data


def display_menu(page, menu_option, display_value):
    global peak_index, start_time, values, message_sent
    display.fill(0)
    lines = []
    y_positions = []

    if page == 0:
        lines = ["Measure HR", "HRV analysis", "History"]
        menu_option = max(0, min(menu_option, len(lines) - 1))
        for i, line in enumerate(lines):
            if i == menu_option:
                line = f"[{line}]"
            display.text(line, center_text(line), 10 + i * 15, 1)
    elif page == 1:
        if 30 <= display_value <= 240:
            lines = [f"{display_value} BPM", "Press the button", "to stop"]
            y_positions = calculate_vertical_positions(len(lines))
            for i, text in enumerate(lines):
                display.text(text, center_text(text), y_positions[i], 1)
        else:
            display.text("MEASURING", 4, 1, 1)
            display.text("________", 4 * 1, 8, 1)
            display.text("LIGHTY HOLD THE", 4, 24, 1)
            display.text("SENSOR", 4, 34, 1)
            
    elif page == 3:
        lines = ["Start", "HRV analysis", "by pressing", "the button ->"]
        y_positions = calculate_vertical_positions(len(lines))
        for i, text in enumerate(lines):
            display.text(text, center_text(text), y_positions[i], 1)
            
    elif page == 4:
        values = [average_hr, calc_peak(), count_rmssd(ppi_list), count_sdnn(ppi_list)]
        save_data(values)
        lines = ["MEAN HR:", "MEAN PPI:", "RMSSD:", "SDNN:"]
        if thirty_over():
            if message_sent == False:
                message_sent = True
                connect_mqtt()
                mqttsending(values)
            for i, text in enumerate(lines):
                line = f"{text} {values[i]}"
                display.text(line, center_text(line), 10 + i * 15, 1)
            
        else: 
            lines = ["Collecting data,", "please wait...", "30s remaining"]
            y_positions = calculate_vertical_positions(len(lines))
            for i, text in enumerate(lines):
                display.text(text, center_text(text), y_positions[i], 1)
        
    elif page == 6:
        saved_data = load_data()
        if saved_data:
            # Assign each value to its respective variable
            mean_hr = saved_data.get("Mean HR", None)
            mean_ppi = saved_data.get("Mean PPI", None)
            rmssd = saved_data.get("RMSSD", None)
            sdnn = saved_data.get("SDNN", None)
            lines = ["MEAN HR:", "MEAN PPI:", "RMSSD:", "SDNN:"]
            saved_values = [mean_hr, mean_ppi, rmssd, sdnn]
            for i, text in enumerate(lines):
                line = f"{text} {saved_values[i]}"
                display.text(line, center_text(line), 10 + i * 15, 1)
        else:
            print("l")


    display.show()
    return menu_option


def handle_menu_logic():
    global encoder, menu_option, current_page, average_hr, update_time, values, message_sent
    if encoder.read_button():
            if current_page == 0:
                if menu_option == 0:
                    current_page = 1
                elif menu_option == 1:
                    current_page = 3
                elif menu_option == 2:
                    current_page = 6
            elif current_page == 1:
                current_page = 0
            elif current_page == 3:
                current_page = 4
            elif current_page == 5:
                current_page = 0
            elif current_page == 4:
                current_page = 0
            elif current_page == 6:
                current_page = 0
            display_menu(current_page,menu_option,average_hr)

    handle_encoder_input()
    
    current_time = utime.time()    
    if current_time >= update_time:
        display_menu(current_page,menu_option, average_hr)
        update_time = current_time + 3  # Update time 3 seconds

while True:
    process_adc_data()
    handle_menu_logic()