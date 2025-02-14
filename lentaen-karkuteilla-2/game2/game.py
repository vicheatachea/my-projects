from geopy.distance import geodesic as GD
import mysql.connector


connection = mysql.connector.connect(
    host="localhost",
    port=3306,
    database="karkuteilla",
    user="root",
    password="rotallaonvaljaat",
    autocommit=True,
)

# Järjestyksessä Tsekki-Saksa, Saksa-Islanti, Islanti-Italia, Italia-Espanja
right_distances = {1: 256, 2: 2439, 3: 3090, 4: 799}


# Tarkastaa valtion olemassaolon tietokannasta
def if_country_exist(nextcountry):
    sql = "select name from country "
    sql += f"where name = '{nextcountry}'"
    result = execute_sql(sql)
    return bool(result)

# Hakee tietokannasta dataa
def execute_sql(sql):
    cursor = connection.cursor()
    cursor.execute(sql)
    result = cursor.fetchall()
    return result


# Suorittaa komennon tietokannassa
def execute_command(sql):
    cursor = connection.cursor()
    cursor.execute(sql)
    return


# Kertoo missä pelaaja sijaitsee tällä hetkellä
def youre_here(current_country):
    sql = "select latitude_deg, longitude_deg"
    sql += " from airport where airport.iso_country"
    sql += " = (select iso_country from country"
    sql += f" where country.name = '{current_country}')"
    location = execute_sql(sql)
    return location


# Kertoo minne pelaaja lentää seuraavaksi
def youre_going(next_country):
    sql = "select latitude_deg, longitude_deg"
    sql += " from airport where airport.iso_country"
    sql += " = (select iso_country from country"
    sql += f" where country.name = '{next_country}')"
    location = execute_sql(sql)
    return location


# Laskee rangaistuksen, ohjelmisto 2 vaihdettu nimi englanniksi
def right_distance(distance, right_distance):
    if right_distance > distance:
        penalty = (right_distance - distance) * 2 + right_distance
    elif distance > right_distance:
        penalty = (distance - right_distance) * 2 + right_distance
    elif distance == right_distance:
        penalty = right_distance
    return penalty


# Hakee ensimmäisen vihjeen
def get_first_tip(country_name):
    sql = f"select tip_1 from airport, country where country.name = '{country_name}' and airport.iso_country = country.iso_country"
    result = execute_sql(sql)
    return result

# Hakee toisen vihjeen
def get_second_tip(airport_name):
    sql = f"select tip_2 from airport, country where country.name = '{airport_name}' and airport.iso_country = country.iso_country"
    result = execute_sql(sql)
    return result

# Laskee valtion välisen välimatkan ja palauttaa välimatkan, johon lisätty mahdollinen rangaistus
def calculate_distance(current_country, next_country, right_distances, roundnumber):
    distance1 = youre_here(current_country)
    distance2 = youre_going(next_country)
    distance = round(GD(distance1, distance2).km)
    penalty = right_distance(distance, right_distances[roundnumber])
    return penalty

def get_municipality(country):
    sql = "select municipality from airport, country where airport.iso_country = country.iso_country"
    sql += f" and country.name = '{country}'"
    result = execute_sql(sql)
    return result
