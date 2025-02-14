const express = require('express');
const router = express.Router();
const cors = require('cors');
const cookieParser = require('cookie-parser');

const {
    checkTokenValidity,
    errorHandler
} = require('../middleware/spotifyMiddleware');

const {
    loginUser,
    callbackSpotify,
    refreshToken,
    searchSpotify,
    recommendedGenres,
    newReleases,
    topHits,
    getArtist,
    getTrack,
    getCollection
} = require('../controllers/spotifyController');

router.use(cors())
    .use(cookieParser());

// Make a login request to Spotify's server
router.get('/login', loginUser)

// Callback from login to request access and refresh tokens
router.get('/callback', callbackSpotify)

// Checks if the access token exists or if it is still valid
router.use(checkTokenValidity);

// Refresh the access token (PARTIALLY WORKING)
router.get('/refresh-token', refreshToken)

// Get search data
router.get('/search', searchSpotify)

// Get recommended (random) genres
router.get('/genres', recommendedGenres)

// Get new album releases
router.get('/new-releases', newReleases)

// Get top hits
router.get('/top-hits', topHits)

// Get detailed artist data
router.get('/artist/:id', getArtist);

// Get detailed track data
router.get('/track/:id', getTrack);

// Get detailed album or playlist data
router.get('/collection/:type/:id', getCollection);

// Error handler based on Spotify's documentation
router.use(errorHandler);

module.exports = router;