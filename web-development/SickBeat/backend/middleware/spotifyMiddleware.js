const {tokenStorage} = require('../controllers/spotifyController');

function checkTokenValidity(req, res, next) {
    if (!tokenStorage.accessToken) {
        // Replace with res.status().json()
        return res.status(401).send('Access token is missing');
    }

    const accessTokenCreatedAt = tokenStorage.accessTokenCreatedAt;
    const timeDifference = Date.now() - accessTokenCreatedAt;
    const tokenValidity = 3600000; // 1 hour

    if (timeDifference > tokenValidity) {
        // Replace with res.status().json()
        return res.status(401).send('Access token has expired');
    } else if (timeDifference > 0.8 * tokenValidity) {
        res.setHeader('Warning', 'Access token is about to expire');
    }

    next();
}

function errorHandler(error, req, res, next) {
    switch (error.response?.status) {
        case 401:
            return res.status(401).send("Bad or expired token");
        case 403:
            return res.status(403).send('Bad OAuth request');
        case 429:
            const retryAfter = error.response.headers['retry-after'];
            console.warn(`Rate limited. Retry after ${retryAfter} seconds.`);
            return res.status(429).send(`Rate limit exceeded. Retry after ${retryAfter} seconds.`);
        default:
            return res.status(500).send('Internal server error');
    }
}

module.exports = {
    checkTokenValidity,
    errorHandler
};