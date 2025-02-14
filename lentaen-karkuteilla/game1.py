from geopy.distance import geodesic as GD
import mysql.connector


yhteys = mysql.connector.connect(
    host="localhost",
    port=3306,
    database="karkuteilla",
    user="root",
    password="maailmanilmaa",
    autocommit=True,
)


# Peli pelataan game-funktion sisällä
def game(
        airport_name,
        correct_country_name,
        right_distance,
        coins,
        crimes_stopped,
        location_atm,
        player_km,
        rounds_played,
):
    rounds_played += 1

    for x in welcome_to(location_atm):
        print(style.RED + f"Welcome to", x[0] + style.RESET)
        print("")

    stats(player_km, coins, crimes_stopped, rounds_played)

    for x in get_first_tip(airport_name):
        new_line_for_tip(x[0])
        print("")
    print(
        "Saamasi tiedon mukaan sinun pitäisi päättää mihin valtioon matkustat seuraavaksi."
    )
    print("")
    print(
        "Mikäli tietosi perusteella et pysty valitsemaan valtiota, voimme mahdollisuuksien mukaan hankkia sinulle lisää tietoa."
    )
    print("")
    while True:
        player_choise = str(
            input(
                "[1] Matkustan saamani tiedon perusteella, "
                "[2] Yrittäkää kerätä lisää tietoa rikollisen seuraavasta kohteesta: "
            )
        )
        print("")
        if player_choise == "1" or player_choise == "2":
            break

    if player_choise == "1":
        next_country = str(input("Anna valtion nimi: ")).capitalize()
        print("")
        while True:
            answer = if_country_exist(next_country)
            if not answer:
                next_country = input(str("Anna valtion nimi: ")).capitalize()
                print("")
            else:
                break
        if next_country == correct_country_name:
            coins += 2
            crimes_stopped += 1
        distance1 = youre_here(f"{airport_name}")
        distance2 = youre_going(f"{next_country}")

        distance = round(GD(distance1, distance2).km)

        if next_country != correct_country_name:
            crimes_stopped = crimes_stopped
            coins -= 1
            warning(coins)

    if player_choise == "2":
        coins -= 1
        warning(coins)
        stats(player_km, coins, crimes_stopped, rounds_played)
        print("")

        for x in get_second_tip(airport_name):
            new_line_for_tip(x[0])
        next_country = str(input("Anna valtion nimi: ")).capitalize()
        print("")
        while True:
            answer = if_country_exist(next_country)
            if not answer:
                next_country = input(str("Anna valtion nimi: ")).capitalize()
                print("")
            else:
                break

        if next_country == correct_country_name:
            crimes_stopped += 1
            coins += 1
            distance1 = youre_here(f"{airport_name}")
            distance2 = youre_going(f"{next_country}")

            distance = round(GD(distance1, distance2).km)
        else:
            coins -= 1
            warning(coins)
            distance1 = youre_here(f"{airport_name}")
            distance2 = youre_going(f"{next_country}")

            distance = round(GD(distance1, distance2).km)

    penalty = oikea_matka(distance, right_distance)
    player_km += penalty

    return coins, crimes_stopped, round(player_km), next_country, rounds_played


# Afrikka-taso
def if_africa():
    coin1, crime_stopped1, km, location_atm, round_nro = game(
        "Murtala Muhammed International Airport",
        "Etelä-afrikka",
        4768,
        4,
        0,
        "Nigeria",
        0,
        0,
    )

    coin2, crime_stopped2, km1, location_atm1, round_nro1 = game(
        "Cape Town International Airport",
        "Burundi",
        3570,
        coin1,
        crime_stopped1,
        location_atm,
        km,
        round_nro,
    )
    if coin2 > 0:
        coin3, crime_stopped3, km2, location_atm2, round_nro2 = game(
            "Bujumbura International Airport",
            "Sierra Leone",
            4900,
            coin2,
            crime_stopped2,
            location_atm1,
            km1,
            round_nro1,
        )
        if coin3 > 0:
            coin4, crime_stopped4, km3, location_atm3, round_nro3 = game(
                "Lungi International Airport",
                "Egypti",
                5209,
                coin3,
                crime_stopped3,
                location_atm2,
                km2,
                round_nro2,
            )
            end_game(crime_stopped4, coin4, km3, location_atm3, "Egypti", 18444)
            compare_save(crime_stopped4, km3, coin4, user_name)
        else:
            print(
                style.RED
                + """
                 ______                        ____                 
                / ____/___ _____ ___  ___     / __ \_   _____  _____
               / / __/ __ `/ __ `__ \/ _ \   / / / / | / / _ \/ ___/
              / /_/ / /_/ / / / / / /  __/  / /_/ /| |/ /  __/ /    
              \____/\__,_/_/ /_/ /_/\___/   \____/ |___/\___/_/     

              """
                + style.RESET
            )
            print(
                "Sinun HETACOINS on nollilla, jonka takia hävisit tason tässä vaiheessa!"
            )
    else:
        print(
            style.RED
            + """
             ______                        ____                 
            / ____/___ _____ ___  ___     / __ \_   _____  _____
           / / __/ __ `/ __ `__ \/ _ \   / / / / | / / _ \/ ___/
          / /_/ / /_/ / / / / / /  __/  / /_/ /| |/ /  __/ /    
          \____/\__,_/_/ /_/ /_/\___/   \____/ |___/\___/_/     

          """
            + style.RESET
        )
        print("Sinun HETACOINS on nollilla, jonka takia hävisit tason tässä vaiheessa!")
    print("")
    while True:
        print("Haluatko pelata saman tason uudestaan?")
        AF_answer = str(input("[1] KYLLÄ, [2] EN: "))
        print("")
        if AF_answer == "1" or AF_answer == "2":
            break
    return AF_answer


# Amerikat-taso
def if_amerikka():
    coin1, crime_stopped1, km, location_atm, round_nro = game(
        "José Marti International Airport",
        "Chile",
        6360,
        4,
        0,
        "Havanna",
        0,
        0,
    )

    coin2, crime_stopped2, km1, location_atm1, round_nro1 = game(
        "Santiago de Chile Airport",
        "US",
        8969,
        coin1,
        crime_stopped1,
        location_atm,
        km,
        round_nro,
    )
    if coin2 > 0:
        coin3, crime_stopped3, km2, location_atm2, round_nro2 = game(
            "McCarran International Airport",
            "Brasilia",
            9976,
            coin2,
            crime_stopped2,
            location_atm1,
            km1,
            round_nro1,
        )
        if coin3 > 0:
            coin4, crime_stopped4, km3, location_atm3, round_nro3 = game(
                "Galeão International Airport",
                "Kanada",
                8211,
                coin3,
                crime_stopped3,
                location_atm2,
                km2,
                round_nro2,
            )
            end_game(crime_stopped4, coin4, km3, location_atm3, "Kanada", 33515)
            compare_save(crime_stopped4, km3, coin4, user_name)
        else:
            print(
                "Sinun HETACOINS on nollilla, jonka takia hävisit tason tässä vaiheessa!"
            )
    else:
        print("Sinun HETACOINS on nollilla, jonka takia hävisit tason tässä vaiheessa!")
    print("")
    while True:
        print("Haluatko pelata tason uudestaan?")
        US_answer = str(input("[1] KYLLÄ,[2] EN: "))
        print("")
        if US_answer == "1" or US_answer == "2":
            break
    return US_answer


# Aasia-taso
def if_asia():
    coin1, crime_stopped1, km, location_atm, round_nro = game(
        "Kolkata Airport",
        "Nepali",
        639,
        4,
        0,
        "Intia",
        0,
        0,
    )

    coin2, crime_stopped2, km1, location_atm1, round_nro1 = game(
        "Tribhuvan International Airport",
        "Qatar",
        3365,
        coin1,
        crime_stopped1,
        location_atm,
        km,
        round_nro,
    )
    if coin2 > 0:
        coin3, crime_stopped3, km2, location_atm2, round_nro2 = game(
            "Hamad International Airport",
            "Malesia",
            5911,
            coin2,
            crime_stopped2,
            location_atm1,
            km1,
            round_nro1,
        )
        if coin3 > 0:
            coin4, crime_stopped4, km3, location_atm3, round_nro3 = game(
                "Kuala Lumpur International Airport",
                "Etelä-korea",
                4602,
                coin3,
                crime_stopped3,
                location_atm2,
                km2,
                round_nro2,
            )
            end_game(
                crime_stopped4, coin4, km3, location_atm3, "Etelä-korea", 14515
            )  # correct country name puuttuu??
            compare_save(crime_stopped4, km3, coin4, user_name)
        else:
            print(
                "Sinun HETACOINS on nollilla, jonka takia hävisit tason tässä vaiheessa!"
            )
    else:
        print("Sinun HETACOINS on nollilla, jonka takia hävisit tason tässä vaiheessa!")
    print("")
    while True:
        print("Haluatko pelata tason uudestaan?")
        AA_answer = str(input("[1] KYLLÄ,[2] EN: "))
        print("")
        if AA_answer == "1" or AA_answer == "2":
            break
    return AA_answer


# Eurooppa-taso
def if_eurooppa():
    coin1, crime_stopped1, km, location_atm, round_nro = game(
        "Václav Havel Airport Prague", "Saksa", 256, 4, 0, "Praha", 0, 0
    )
    coin2, crime_stopped2, km1, location_atm1, round_nro1 = game(
        "Berlin Brandenburg Airport",
        "Islanti",
        2439,
        coin1,
        crime_stopped1,
        location_atm,
        km,
        round_nro,
    )
    if coin2 > 0:
        coin3, crime_stopped3, km2, location_atm2, round_nro2 = game(
            "Keflavik International Airport",
            "Italia",
            3090,
            coin2,
            crime_stopped2,
            location_atm1,
            km1,
            round_nro1,
        )
        if coin3 > 0:
            coin4, crime_stopped4, km3, location_atm3, round_nro3 = game(
                "Peretola Airport",
                "Espanja",
                799,
                coin3,
                crime_stopped3,
                location_atm2,
                km2,
                round_nro2,
            )
            end_game(crime_stopped4, coin4, km3, location_atm3, "Espanja", 6539)
            compare_save(crime_stopped4, km3, coin4, user_name)
        else:
            print(
                "Sinun HETACOINS on nollilla, jonka takia hävisit tason tässä vaiheessa!"
            )
    else:
        print("Sinun HETACOINS on nollilla, jonka takia hävisit tason tässä vaiheessa!")
    print("")
    while True:
        print("Haluatko pelata tason uudestaan?")
        EU_answer = str(input("[1] KYLLÄ,[2] EN: "))
        print("")
        if EU_answer == "1" or EU_answer == "2":
            break
    return EU_answer


# Tarkistetaan voittiko pelaaja vai ei
def end_game(
        crime_stopped4, coin, km, location_atm3, correct_country_name, continent_km
):
    if (
            location_atm3 != correct_country_name
            or crime_stopped4 < 3
            or km / continent_km > 1.30
    ):
        print(
            style.RED + "Pimeys vallitsee, kun seisot hävinneenä lentokentän varjoissa."
        )
        print(
            "Vaikka taistelit parhaasi mukaan, rikolliset pääsivät kerta toisensa jälkeen käsistäsi."
        )
        print(
            "Kansainväliset operaatiot päättyivät katastrofeihin, ja kukaan ei ole turvassa."
        )
        print(
            "Sinut on virallisesti erotettu agenttijoukosta, ja jäät pohtimaan mitä olisit voinut tehdä toisin."
        )
        print(
            "Järjestöt jatkavat rikoksiaan, ja maailma tarvitsee nyt enemmän kuin koskaan sankareita."
        )
        print(
            "Sinun seikkailusi päättyi pettymykseen, mutta ehkä tulet saamaan vielä mahdollisuuden palata taisteluun…"
        )

        print(
            "Olet estänyt vain",
            crime_stopped4,
            "rikosta kaikista rikoksista ja sinulla on vain",
            coin,
            "HETACOINS:ia ja olet matkustanut",
            km,
            "kilometriä." + style.RESET,
            )
    if (
            location_atm3 == correct_country_name
            and 3 <= crime_stopped4
            and km / continent_km < 1.30
            and coin <= 9
    ):
        print(
            style.BLUE
            + "Onnittelut agentti! Olet suorittanut vaarallisen matkasi ympäri maailmaa, ja tulokset ovat selvät."
        )
        print(
            "Kansainväliset rikolliset ovat nyt telkien takana, heidän suunnitelmansa paljastettu ja rikokset estetty."
        )
        print(
            "Sinä ja agenttiryhmäsi onnistuitte, ja maailma on nyt turvallisempi paikka."
        )
        print(
            "Olet saavuttanut legendaarisen maineen agenttien joukossa, voitto on sinun!"
        )
        print()
        print(
            "Olet estänyt näin",
            crime_stopped4,
            "rikosta kaikista rikoksista ja sinulla on",
            coin,
            "HETACOINS:ia ja olet matkustanut",
            km,
            "kilometriä." + style.RESET,
            )
    if (
            location_atm3 == correct_country_name
            and 3 <= crime_stopped4
            and km / continent_km < 1.30
            and coin > 9
    ):
        print(
            style.BLUE
            + "Onnittelut agentti! Olet suorittanut vaarallisen matkasi ympäri maailmaa, ja tulokset ovat selvät."
        )
        print(
            "Kansainväliset rikolliset ovat nyt telkien takana, heidän suunnitelmansa paljastettu ja rikokset estetty."
        )
        print(
            "Sinä ja agenttiryhmäsi onnistuitte, ja maailma on nyt turvallisempi paikka."
        )
        print(
            "Olet saavuttanut legendaarisen maineen agenttien joukossa, voitto on sinun!"
        )
        print()
        print(
            f"Olet estänyt näin {style.GREEN}{crime_stopped4} {style.RESET}{style.BLUE}rikosta kaikista rikoksista ja "
            f"sinulla on loistava määrä"
            f" HETACOINS:ia {style.GREEN}{coin}{style.RESET} {style.BLUE}ja olet matkustanut {style.RESET}{style.GREEN}"
            f"{km}{style.RESET}{style.BLUE} kilometriä." + style.RESET
        )


# Tarkastetaan onko saatu tulos parempi kuin tietokannassa oleva paras tulos ja tallennetaan jos näin on
def compare_save(crime_stopped4, km3, coin4, user_name):
    sql = "select crimes_stopped, km_travelled, coin"
    sql += f" from game where screen_name = '{user_name}'"
    values = execute_sql(sql)[0]
    sql_update = f"update game set crimes_stopped = {crime_stopped4}, km_travelled = {km3}, coin = {coin4} "
    sql_update += f"where screen_name = '{user_name}'"
    if not values[1]:
        execute_command(sql_update)
    elif crime_stopped4 > values[0]:
        execute_command(sql_update)
    elif crime_stopped4 == values[0]:
        if km3 < values[1]:
            execute_command(sql_update)
        elif km3 == values[1]:
            if coin4 > values[2]:
                execute_command(sql_update)
    return


# Jos pelaajan kolikot ovat vähissä, hän saa varoituksen
def warning(coins):
    if coins == 0:
        print("")
    else:
        if 0 < coins < 2:
            print("")
            print(
                style.RED + "VAROITUS, sinulla on alle 2 kolikkoa!"
                            " Jos et pääse rosvon jäljille seuraavalla lentokentällä, olet vaarassa hävitä pelin."
                + style.RESET
            )
    return


# Kysyy halutaanko näyttää parhaat tulokset
def show_scores():
    while True:
        stats = str(
            input(
                "Haluatko tarkistaa parhaat pelituloksesi? Valitse [1] kyllä tai [2] ei: "
            )
        )
        if stats == "1" or stats == "2":
            break
    if stats == "1":
        print()
        selected_cont = str(
            input(
                "Valitse manner: [1] Eurooppa, [2] Amerikat, [3] Aasia, [4] Afrikka tai [5] lopeta: "
            )
        )
        while selected_cont != "5":
            print()
            best_score(user_name, selected_cont)
            selected_cont = str(
                input(
                    "Valitse manner: [1] Eurooppa, [2] Amerikat, [3] Aasia, [4] Afrikka tai [5] lopeta: "
                )
            )
    return


# Tulostaa parhaat tulokset
def best_score(user_name, selected_cont):
    sql = "select crimes_stopped, km_travelled, coin"
    sql += f" from game where screen_name = '{user_name}' and continent = '{selected_cont}'"
    values = execute_sql(sql)
    if not values:
        print("Tasoa ei ole vielä pelattu.")
        print()
    else:
        values = values[0]
        print("Parhaat pelituloksesi ovat:")
        print()
        print(f"{'Rikoksia pysäytetty:':<30s}{values[0]:<20d}")
        print(f"{'Kilometrejä lennetty:':<30s}{values[1]:<20d}")
        print(f"{'Kolikoita pelin lopussa:':<30s}{values[2]:<20d}")
        print()
    return


# Tulostaa pelaajan statsit pelin aikana
def stats(player_kilometers, coins, crimes_stopped, rounds_played):
    print("")
    print(
        style.BLUE
        + f" K.M. : {player_kilometers}       "
        + style.RESET
        + style.BLUE
        + f"  HETACOINS: {coins}       "
        + style.RESET
        + style.BLUE
        + f"  Rikokset pysäytetty: {crimes_stopped}       "
        + style.RESET
        + style.BLUE
        + f"  Kierros: {rounds_played}       "
        + style.RESET
    )
    print("")
    print("")


# Tarkastaa valtion olemassaolon tietokannasta
def if_country_exist(next_country):
    sql = "select name from country "
    sql += f"where name = '{next_country}'"
    answer = execute_sql(sql)
    return answer


# Tulostaa pelin ohjeet
def game_instructions():
    print(
        style.YELLOW + "\nHetacoins: "
                       "\nPelin alussa pelaajalla on 4 kolikkoa. Lentäminen maksaa 1 kolikon, ja lisävihje maksaa 1 kolikon. "
                       "\nJos lennät oikeaan kohteeseen ilman lisävihjettä, saat 2 kolikkoa lisää. Jos lennät oikeaan kohteeseen "
                       "\nlisävihjeen kanssa, saat 1 kolikon. Jos kolikot loppuu, häviät pelin.\n"
    )
    print(
        "Kilometrit: "
        "\nLennetyt kilometrit + mahdolliset kilometri rangaistukset. Kilometri rangaistuksen saa, "
        "\njos lentää väärään kohteeseen. Rangaistus on matkan pituudesta riippuen (lennetty matka - oikea matka) * 2 "
        "\ntai (oikeamatka - lennetty matka) * 2, ja tämä lisätään kilometrimäärään.\n"
    )
    print(
        "Lentäminen: \nLennät kohteeseen valitsemalla valtion, johon saatu vihje viittaa."
        " \nSaat ilmoituksen mille lentokentälle lensit, ja pysäytettyjen rikosten määrä kasvaa "
        "\njos olit ajoissa pysäyttämäsää rikoksen, eli lensit oikein.\n"
    )
    print(
        "Voittaminen: "
        "\nVoitat pelin jos pysäytit vähintään 3 rikosta, eli lensit oikein kolmesti,"
        " \nsekä päädyit oikealle lentokentälle. Jos kilometrisi ylittävät oikein lennetyt kilometrit 30%,"
        " \ntuhlasit kilometrejä ja et lentänyt ympäristöystävällisesti, eli häviät pelin."
        " \nJos kolikkosi ovat 10 tai yli, lensit taloudellisesti vastuullisesti, ja saat tästä ekstra maininnan!"
        + style.RESET
    )
    return


# Värit
class style:
    BLACK = "\033[30m"
    RED = "\033[31m"
    GREEN = "\033[32m"
    YELLOW = "\033[33m"
    BLUE = "\033[34m"
    MAGENTA = "\033[35m"
    CYAN = "\033[36m"
    WHITE = "\033[37m"
    UNDERLINE = "\033[4m"
    RESET = "\033[0m"


# Hakee tietokannasta dataa
def execute_sql(sql):
    cursor = yhteys.cursor()
    cursor.execute(sql)
    ans = cursor.fetchall()
    return ans


# Suorittaa komennon tietokannassa
def execute_command(sql):
    kursori = yhteys.cursor()
    kursori.execute(sql)
    return


# Kertoo missä pelaaja sijaitsee tällä hetkellä
def youre_here(airport_name):
    sql2 = "SELECT latitude_deg, longitude_deg from airport"
    sql2 += f" Where name = '{airport_name}'"
    location = execute_sql(sql2)
    return location


# Kertoo minne pelaaja lentää seuraavaksi
def youre_going(next_country):
    sql = "select latitude_deg, longitude_deg"
    sql += " from airport where airport.iso_country"
    sql += " = (select iso_country from country"
    sql += f" where country.name = '{next_country}')"
    location = execute_sql(sql)
    return location


# Laskee rangaistuksen
def oikea_matka(distance, right_distance):
    if right_distance > distance:
        penalty = (right_distance - distance) * 2 + right_distance
    if distance > right_distance:
        penalty = (distance - right_distance) * 2 + right_distance
    if distance == right_distance:
        penalty = right_distance
    return penalty


# Poistaa pelaajan tiedot tietokannasta
def delete_old_user(user_name):
    sql = f"delete from game where screen_name = '{user_name}'"
    execute_command(sql)
    return


# Lisää uuden pelaajan tietokantaan
def add_new_user(user_name, player_lvl):
    sql = f"insert into game (screen_name, continent)"
    sql += f" values ('{user_name}', '{player_lvl}')"
    execute_command(sql)
    return


def plane_art():
    return print(
        style.CYAN
        + """
               ``+*:.
               =@@@@#.
                +@@@@@@..          .-:.
                 :@@@@@@%:   .%@=..*@@@:.
                  .*@@@@@@@:::#@@@**#@@@@@@@@@:.
                    :@@@@@@@@@@@@@@@@@@@@@@@@#-.
                     .-@@@@@@@@@@@@@@@@@=...
                      .%@@@@@@@@@%#.
                    :@@@@@@@@@@@@@:
                    :%@@@@@@@@@@@@@#.
                     .:@@@@@#.%@@@@@@+
                   .@#.@@@@@:   #@@@@@@.
                   :@@@@@@@+     .#@@@@@@@@@@@@*.
                    .=@@@@@.       .+@@@@@@@#=..
                      -@@@=         .@@@@@@.
                      +@@#          .@@@--@@.
                      #@@-          .@@+.
                      .:+           .%@. """
        + style.RESET
    )


def ascii_eu():
    print(
        """
    ______                                        
   / ____/_  ___________  ____  ____  ____  ____ _
  / __/ / / / / ___/ __ \/ __ \/ __ \/ __ \/ __ `/
 / /___/ /_/ / /  / /_/ / /_/ / /_/ / /_/ / /_/ / 
/_____/\__,_/_/   \____/\____/ .___/ .___/\__,_/  
                            /_/   /_/             
"""
    )


def ascii_aa():
    print(
        """
    ___              _      
   /   | ____ ______(_)___ _
  / /| |/ __ `/ ___/ / __ `/
 / ___ / /_/ (__  ) / /_/ / 
/_/  |_\__,_/____/_/\__,_/  

"""
    )


def ascii_us():
    print(
        """
    ___                        _ __         __ 
   /   |  ____ ___  ___  _____(_) /______ _/ /_
  / /| | / __ `__ \/ _ \/ ___/ / //_/ __ `/ __/
 / ___ |/ / / / / /  __/ /  / / ,< / /_/ / /_  
/_/  |_/_/ /_/ /_/\___/_/  /_/_/|_|\__,_/\__/  

"""
    )


def ascii_af():
    print(
        """
    ___    ____     _ __   __        
   /   |  / __/____(_) /__/ /______ _
  / /| | / /_/ ___/ / //_/ //_/ __ `/
 / ___ |/ __/ /  / / ,< / ,< / /_/ / 
/_/  |_/_/ /_/  /_/_/|_/_/|_|\__,_/  

"""
    )


# Jakaa vihjeen ettei se mene terminaalin ulkopuolelle
def new_line_for_tip(ans):
    chars = 165
    for i in range(0, len(ans), chars):
        print(style.MAGENTA + ans[i : i + chars], style.RESET)
    return


# Hakee ensimmäisen vihjeen
def get_first_tip(airport_name):
    sql = "Select tip_1 From airport "
    sql += f" where Name = '{airport_name}'"
    tulos = execute_sql(sql)
    return tulos


# Hakee toisen vihjeen
def get_second_tip(airport_name):
    sql = "Select tip_2 From airport "
    sql += f" where name = '{airport_name}'"
    tulos = execute_sql(sql)
    return tulos


# Toivottaa pelaajan tervettulleeksi lentokentälle
def welcome_to(name):
    sql = f"select airport.name from airport,country where airport.iso_country = country.iso_country and "
    sql += f"country.name = '{name}'"
    tulos = execute_sql(sql)
    print("")
    return tulos


# Vaikeustason valinta
def difficulty_lvl():
    while True:
        print("")
        print("Haluatko pelata pelin [1] helpolla vai [2] vaikealla vaikeustasolla?")
        print("")
        difficulty_level = input(
            str("[1] Eurooppa tai Amerikat, [2] Aasia tai Afrikka: ")
        )
        print("")
        if difficulty_level == "1" or difficulty_level == "2":
            break
    if difficulty_level == "1":
        while True:
            easy_level = input(
                "Valitse vaikeustason manner: [1] Eurooppa tai [2] Amerikat: "
            )
            if easy_level == "1" or easy_level == "2":
                break

        if easy_level == "1":
            print("")
            ascii_eu()
        if easy_level == "2":
            print("")
            ascii_us()

    if difficulty_level == "2":
        while True:
            easy_level = input(
                "Valitse vaikeustason manner: [3] Aasia tai [4] Afrikka: "
            )
            if easy_level == "3" or easy_level == "4":
                break

        if easy_level == "3":
            print("")
            ascii_aa()

        if easy_level == "4":
            print("")
            ascii_af()

    return easy_level


# Tarkastaa onko pelaaja uusi vai vanha pelaaja
def new_or_player(user_name):
    sql = f"Select screen_name From game where screen_name = '{user_name}'"
    ans = execute_sql(sql)
    return ans


print("")
print("")
print("Tervetuloa pelaamaan Lentäen Karkuteillä!")
print("")
plane_art()
print("")
user_name = str(input(style.RED + "Anna käyttäjätunnus: " + style.RESET))

if new_or_player(user_name):
    print("")
    print(f"Tervetuloa takaisin {style.GREEN}{user_name}{style.RESET}!")
    print("")
    while True:
        new_old_game = str(
            input(
                "[1] Jatka peliä vanhoilla tiedoilla vai [2] nollaa vanhat tiedot ja aloita kokonaan uusi peli?: "
            )
        )
        if new_old_game == "1" or new_old_game == "2":
            break
    if new_old_game == "1":
        show_scores()
    elif new_old_game == "2":
        delete_old_user(user_name)
        print()
        print(
            "Tervetuloa" + style.GREEN + f" {user_name}" + style.RESET,
            "uuteen peliin\n",
            )
        while True:
            print()
            instructions = input(
                "Haluatko tarkemman ohjeen pelin pelaamiseen? Valitse [1] kyllä tai [2] ei: "
            )
            if instructions == "1" or instructions == "2":
                break
        if instructions == "1":
            game_instructions()

if not new_or_player(user_name):
    print("")
    print("Tervetuloa" + style.GREEN + f" {user_name}" + style.RESET, "uuteen peliin\n")
    while True:
        instructions = input(
            "Haluatko tarkemman ohjeen pelin pelaamiseen? Valitse [1] kyllä tai [2] ei: "
        )
        if instructions == "1" or instructions == "2":
            break
    if instructions == "1":
        game_instructions()


new_or_player(user_name)

while True:
    player_lvl = difficulty_lvl()
    add_new_user(user_name, player_lvl)
    if player_lvl == "1":
        while True:
            print("")
            again = if_eurooppa()
            if again == "2":
                break
    if player_lvl == "2":
        while True:
            again = if_amerikka()
            if again == "2":
                break
    if player_lvl == "3":
        while True:
            again = if_asia()
            if again == "2":
                break
    if player_lvl == "4":
        while True:
            again = if_africa()
            if again == "2":
                break
    while True:
        new_lvl = str(input("[1] Pelaa toista tasoa vai [2] lopeta peli: "))
        if new_lvl == "1" or new_lvl == "2":
            break
    if new_lvl == "1":
        print("")
        print("Mitä tasoa haluat pelata seuraavaksi?")
        continue
    if new_lvl == "2":
        break

# miau