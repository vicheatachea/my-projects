import game
from flask import Flask, Response
from flask_cors import CORS
import json
from flask import jsonify
import requests

app = Flask(__name__)
cors = CORS(app)
app.config['CORS_HEADERS'] = 'Content-Type'


def get_the_local_time(municipality):
    try:
        response = requests.get(f'https://timeapi.io/api/Time/current/zone?timeZone=Europe/{municipality}')
        if response.status_code == 200:
            data = response.json()
            return json.dumps(data)
        else:
            print(f"Error: {response.status_code}")
    except requests.exceptions.RequestException as e:
        print(f"Error: {e}")


@app.route('/getthemunicipality/<country>')
def getthemunicipality(country):
    result = game.get_municipality(country)
    municipality = result[0][0]
    response_data = get_the_local_time(municipality)
    status = 200
    return Response(status=status, response=response_data, mimetype='application/json')

# hakee tipit tietokannasta
@app.route('/fetchfirst/<country>')
def fetchfirst(country):
    first_hint = game.get_first_tip(country)
    result = {"first_hint": first_hint}
    resultjson = json.dumps(result)
    status = 200
    return Response(status=status, response=resultjson, mimetype='application/json')

@app.route('/fetchsecond/<country>')
def fetchsecond(country):
    second_hint = game.get_second_tip(country)
    result = {"second_hint": second_hint}
    resultjson = json.dumps(result)
    status = 200
    return Response(status=status, response=resultjson, mimetype='application/json')

@app.route('/penaltycalculator/<currentcountry>/<nextcountry>/<int:roundnumber>')
def penaltycalculator(currentcountry, nextcountry, roundnumber):
     penalty = game.calculate_distance(currentcountry, nextcountry, game.right_distances, roundnumber)
     resultjson = json.dumps(penalty)
     status = 200
     return Response(status=status, response=resultjson, mimetype='application/json')

# yhdistää game:n ja hakee tietokannasta next country
@app.route('/checkcountry/<nextcountry>')
def checkcountry(nextcountry):

    exists = game.if_country_exist(nextcountry)

    if exists:
        return jsonify({"exists": True})
    elif exists == '' or exists is False:
        return jsonify({"exists": False})

if __name__ == '__main__':
    app.run(use_reloader=True, host='127.0.0.1', port=3000)
