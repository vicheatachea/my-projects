var tileSize = 20,
    fadeFactor = 0.05,
    canvas,
    ctx,
    columns = [],
    maxStackHeight;

function init() {
  canvas = document.getElementById('canvas');
  ctx = canvas.getContext('2d');

  initMatrix();

  // aloita looppi
  tick();
}

function initMatrix() {
  maxStackHeight = Math.ceil(canvas.height / tileSize);

  // jaa sivu kolumneihin
  for (let i = 0; i < canvas.width / tileSize; ++i) {
    var column = {};
    column.x = i * tileSize;
    column.stackHeight = 10 + Math.random() * maxStackHeight;
    column.stackCounter = 0;
    columns.push(column);
  }
}

function draw() {
  // lisää feidaantuva musta boksi kolumnin päälle häivyttääksesi vanhemmat kolumnit
  ctx.fillStyle = "rgba( 0 , 0 , 0 , " + fadeFactor + " )";
  ctx.fillRect(0, 0, canvas.width, canvas.height);

  // valitse fontti
  ctx.font = (tileSize - 2) + "px monospace";
  ctx.fillStyle = "#00FF41";
  for (let i = 0; i < columns.length; ++i) {
    // valitse randomilla ascii merkki
    var randomCharacter = String.fromCharCode(33 + Math.floor(Math.random() * 94));
    ctx.fillText(randomCharacter, columns[i].x, columns[i].stackCounter * tileSize + tileSize);

    // jos kolumni on max korkeudessa, valitse uusi satunnainen korkeus ja nollaa
    if (++columns[i].stackCounter >= columns[i].stackHeight) {
      columns[i].stackHeight = 10 + Math.random() * maxStackHeight;
      columns[i].stackCounter = 0;
    }
  }
}

// MAIN LOOP
function tick() {
  draw();
  setTimeout(tick, 50);
}