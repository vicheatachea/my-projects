const express = require("express");
const router = express.Router();
const authenticate = require("../middleware/authenticate");
const {
    getAllCollections,
    getCollectionById,
    createCollection,
    deleteCollection,
} = require("../controllers/collectionController");

// GET all collections
router.get("/", authenticate, getAllCollections);

// GET a specific collection by ID
router.get("/:id", authenticate, getCollectionById);

// POST a new collection
router.post("/", authenticate, createCollection);

// DELETE a specific collection by ID
router.delete("/:id", authenticate, deleteCollection);

module.exports = router;