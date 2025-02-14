'use strict';

let stopTime = null
let secondGiven = false // onko otettu toinen vihje
let gCurrentCountry = 'Tsekki' // aloitusvaltio
let gNextCountry
let gRoundNumber = 1
let gCoins = 5
let gCrimesStopped = 0
let gDistanceTravelled = 0

const countries = ['Tsekki', 'Saksa', 'Islanti', 'Italia'] // näiden avulla haetaan vihjeet tietokannasta
let correctAnswers = {"saksa": 256, "islanti": 2439, "italia": 3090, "espanja": 799}; // näitä käytetään voiton selvittämisessä

// HTML-elementit
const showGuide = document.getElementById('guide'); // ohjenappula
const button = document.querySelector('#tip') // vihjenappula
const nextLevel = document.querySelector('#fly') // matkustanappula


// hakee 1. vihjeen
async function fetchFirst(list, i) {
    try {
        secondGiven = false
        let country = list[i]
        const answer = await fetch('http://localhost:3000/fetchfirst/' + country)
        const jsonhint = await answer.json()

        const firsthint = jsonhint.first_hint[0]

        const h = document.createElement('h3')
        h.innerText = firsthint
        const part = document.querySelector('.text')
        h.className = 'first_hint'
        h.id = 'firsthint'
        part.appendChild(h)

        document.getElementById('firsthint').innerText = firsthint
    } catch (error) {
        console.log(error.message)
    } finally {
        console.log('fetchFirst asynchronous load complete');
    }
}


// Hakee toisen vihjeen
async function fetchSecond(list, i) {
    try {
        let country = list[i]
        secondGiven = true
        const answer = await fetch('http://localhost:3000/fetchsecond/' + country)
        const jsonhint = await answer.json()

        const secondhint = jsonhint.second_hint[0]

        const h = document.createElement('h3')
        h.innerText = secondhint
        const part = document.querySelector('.text')
        h.className = 'second_hint'
        h.id = 'secondhint'
        part.appendChild(h)
    } catch (error) {
        console.log(error.message)
    } finally {
        console.log('fetchSecond asynchronous load complete');
    }
}

// Funktio 2. vihje napin piilottamiseen, kolikoiden vähentämiseen ja navin kolikkovärin muuttamiseen
async function removeTipButton() {
    gCoins -= 1;
    document.getElementById('coins').innerText = gCoins;
    await gameOver()
    if (gCoins <= 2) {
        document.getElementById('coins').style.color = 'red';
    }
    button.style.display = 'none'
}

// tarkastaa onko peli hävitty, koska kolikot loppu
async function gameOver() {
    if (gCoins <= 0) {
        await winOrLose()
    }
}

// Pyyhkii vihjeboksin
function removeTips() {
    const tiplocation = document.querySelector('.text')
    const tip1 = document.querySelector('#firsthint')
    const tip2 = document.querySelector('#secondhint')
    tiplocation.removeChild(tip1)
    if (secondGiven === true) {
        tiplocation.removeChild(tip2)
    }
}


// Funktio 2. vihje -napin palauttamiseen
async function createTipButton() {
    button.style.display = 'block'
}


// Funktio vihjeiden vaihtamiseen kun siirrytään seuraavalle tasolle
async function levelChange () {
    try {

        removeTips()

        // tarkastaa onko peli päätöksissä eikä siten hae enää vihjeitä
        if (gRoundNumber-1 >= countries.length || gCoins <= 0) {
            button.style.display = 'none'; // poistaa kakkosvihjenappulan
            return;
        }

        // mennään tänne, jos edellisellä roundilla käytetty ekstravihje
        else if (secondGiven === true) {
            await fetchFirst(countries, gRoundNumber-1)
            await createTipButton()
            return;
        }

        await fetchFirst(countries, gRoundNumber-1)

    } catch (error) {
        console.log(error.message)}
}

// Hakee kahden valtion välisen välimatkan, johon lisätty mahd. rangaistus
async function penaltyCalculator() {
  try {
      const answer = await fetch(`http://localhost:3000/penaltycalculator/${gCurrentCountry}/${gNextCountry}/${gRoundNumber-1}`)

      return await answer.json()

    } catch (error) {
        console.log(error.message)
    } finally {
        console.log('penaltyCalculator asynchronous load complete');
    }
}

// Kertoo löytyykö syötettyä valtiota tietokannasta
async function countryExists(submittedCountry) {
    try {
        const response = await fetch('http://localhost:3000/checkcountry/' + submittedCountry);
        const result = await response.json();
        if (result.exists) {
            return true;
        } else {
            return false;
        }
    } catch (error) {
        console.log(error.message);
        return false;
    } finally {
        console.log('countryExists asynchronous load complete');
    }
}

//kertoo voittiko pelaaja vai ei ja muokkaa selainta sen mukaan
async function winOrLose() {

    // antaa muuttujan arvoksi true, jos voittoehdot täyttyy
    const conditionsMet = gRoundNumber >= 5 &&
        gCoins >= 9 &&
        gCrimesStopped >= 3 &&
        gDistanceTravelled / (correctAnswers['saksa'] + correctAnswers['italia'] + correctAnswers['espanja'] +
        correctAnswers['islanti']) < 1.30;

    let win = document.getElementById('miau');

    if (conditionsMet) {
        console.log('You win!');
        win.style.width = '110%';
        win.style.height = '110%';
        win.src = "https://i.pinimg.com/736x/1b/a3/9d/1ba39d4dc554fcff6eee2af833a79112.jpg";
        showImage();
        document.getElementById('distance').style.color = '#ffbf00';
        document.getElementById('crimes_stopped').style.color = '#ffbf00';
    } else {
        document.getElementById('distance').style.color = 'red';
        document.getElementById('crimes_stopped').style.color = 'red';
        console.log('You lose!');
        win.style.width = '110%';
        win.style.height = '110%';
        win.src = "https://i.pinimg.com/736x/26/07/a4/2607a4188c8500782fbe4ebb737820de.jpg";
        showImage();
    }
    document.getElementById('enter-country').style.display = 'none'
}

function showImage() {
    document.getElementById('overlay').style.display = 'flex';
}

function closeImage() {
    document.getElementById('overlay').style.display = 'none';
}

// Lataa statsist, jota määritellään pelin alussa. Tein tän, jotta muiden tasojen koodaaminen olisi helppoa.
function loadStats() {
    document.getElementById('current_country').innerText = gCurrentCountry // aloitusvaltio
    document.getElementById('round_number').innerText = gRoundNumber // kierrosnumero
    document.getElementById('distance').innerText = `${gDistanceTravelled} KM` // lennetty matka
    document.getElementById('coins').innerText = gCoins
    document.getElementById('crimes_stopped').innerText = gCrimesStopped
}

// Hakee sen maan local timen, jos sitä on olemassa ja kertoo sen
async function getTheLocalTime(currentCountry) {
    const time = document.getElementById("time_here");
    try {
        const response = await fetch(`http://localhost:3000/getthemunicipality/${currentCountry}`);
        if (response.ok) {
            const data = await response.json();
            time.innerText = `${data.time}:${data.seconds}`
        } else {
            time.style.fontFamily = 'Nova Square'
            time.innerText = "UNAVAILABLE"
            console.log(`Error: ${response.status}`);
        }
    } catch (error) {
        time.style.fontFamily = 'Outfit, Nova Square'
        time.innerText = "UNAVAILABLE"
        console.log(error.message)
    }
}

//Katsoo onko syötetty valtion nimi kenttä tyhjä, jos on se saa sen kentän vilkkumaan punaista
function checkAndHighlight() {
    let queryInput = document.getElementById('query');
    if (queryInput.value.trim() === '') {
        queryInput.classList.add('error');
        setTimeout(() => {
            queryInput.classList.remove('error');
        }, 1000);
    } else {
    }
}

// vaihtaa navin elementtien värei
function navCoinColors() {
    if (gCoins <= 2) {
        document.getElementById('coins').style.color = 'red';
    }
    else if (gCoins > 10) {
        document.getElementById('coins').style.color = '#ffbf00';
    }
}

// Ajaa viken tekemää funktiota, joka kertoo tälle funktiolle onko syötettyä maata olemassa. Jos maata on olemassa
// Sitten siittytään muihin tarkistuks vaiheisiin ja itse pelin päivittämiseen. Vaihetaan värit kans
async function navIfCorrect(correctCountry) {

    const distance = await penaltyCalculator()

    document.getElementById('distance').innerText = `${gDistanceTravelled + distance} KM`; // lennetty matka
    document.getElementById('round_number').innerText = gRoundNumber; // kierrosnumero
    document.getElementById('current_country').innerText = gNextCountry.charAt(0).toUpperCase()
        + gNextCountry.slice(1); // tämänhetkinen valtio isolla alkukirjaimella

    if (gNextCountry === correctCountry) {
        document.getElementById('crimes_stopped').innerText = ` ${gCrimesStopped += 1} `; // rikokset pysäytetty
        gCoins += 2
        document.getElementById('coins').innerText = ` ${gCoins} `; // kolikot kasvaa
        document.getElementById('current_country').style.color = '#ffbf00';

    } else if (gNextCountry !== correctCountry) {
        gCoins -= 1
        document.getElementById('coins').innerText = ` ${gCoins} `; // kolikot vähenee
        document.getElementById('current_country').style.color = 'red';
        await gameOver()
    }

    navCoinColors();
}

// Tyhjentää teksi kentän jokaista uutta kierrosta varten.
function clearTextField() {
    let clearTextField = document.getElementById('query');
    clearTextField.value = "";
}

// Event listenerei

// Tätä tarvitaan, jos halutaan laittaa erilainen viesti, jos veltio imput kenttä on tyhjä.
document.getElementById("query").addEventListener("input", function () {
    this.setCustomValidity("");
});

document.getElementById("query").addEventListener("invalid", function () {
    this.setCustomValidity("Valtion nimi...");
});

showGuide.addEventListener('click', showImage);

// Kun sivu latautuu, 1. tip latautuu sivulle
document.addEventListener('DOMContentLoaded', async function () {
    loadStats();
    // jotta kello lähtee käyntiin ekalla kiekalla kanssa
    if (gRoundNumber === 1) {
        stopTime = setInterval(function() {
                getTheLocalTime('tsekki');
            }, 1000);
    }
    await fetchFirst(countries, gRoundNumber-1)
});

// Hakee toisen vihjeen ja poistaa 2. vihje napin
button.addEventListener('click', async function() {
    await removeTipButton()
    await fetchSecond(countries, gRoundNumber-1)
})

nextLevel.addEventListener('click', async function(event) {
    event.preventDefault()

    checkAndHighlight();

    const submittedCountry = document.getElementById('query').value.toLowerCase()

    if (await countryExists(submittedCountry)) {

        gNextCountry = submittedCountry // päivittää globaalin muuttujan syötettyyn valtioon

        if (stopTime != null) {
            clearInterval(stopTime)
        }

        gRoundNumber++

        if (gRoundNumber === 2) {
            await navIfCorrect("saksa");
        } else if (gRoundNumber === 3) {
            await navIfCorrect("islanti");
        } else if (gRoundNumber === 4) {
            await navIfCorrect("italia");
        } else if (gRoundNumber === 5) {
            await winOrLose()
            await navIfCorrect("espanja");
        }

        if (gNextCountry === "islanti" || gNextCountry === "italia" || gNextCountry === 'espanja') {
        stopTime = setInterval(function() {
            getTheLocalTime("ranska");
        }, 1000);
        } else {
            stopTime = setInterval(function() {
                getTheLocalTime(gNextCountry);
            }, 1000);
        }

        clearTextField();

        await levelChange();

        gCurrentCountry = gNextCountry; // päivittää nykyiseksi valtioksi syötetyn valtion
    }
});