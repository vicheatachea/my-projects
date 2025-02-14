const Collection = require("../models/collectionModel");

// GET all collections
const getAllCollections = async (req, res) => {
    const userId = req.user.userId;

    try {
        const collections = await Collection.find({ userIds: userId }, 'name id type songAmount image');
        res.status(200).json(collections);
    } catch (error) {
        console.log(error.message);
        res.status(500).json({ message: "Server error" });
    }
};

// GET a specific collection by ID
const getCollectionById = async (req, res) => {
    const { id } = req.params;
    const userId = req.user.userId;

    try {
        const collection = await Collection.findOne({ id, userIds: userId }, '-userIds');
        if (!collection) {
            return res.status(404).json({ message: "Collection not found" });
        }
        res.status(200).json(collection);
    } catch (error) {
        console.log(error.message);
        res.status(500).json({ message: "Server error" });
    }
};

// POST a new collection
const createCollection = async (req, res) => {
    const { name, id, type, songAmount, image } = req.body;
    const userId = req.user.userId;

    if (!name || !id || !type || !songAmount || !image) {
        return res.status(400).json({ message: "All fields are required!" });
    }

    try {
        let collection = await Collection.findOne({ id });

        if (collection) {
            // If collection exists, check if user ID is already present
            if (collection.userIds.includes(userId)) {
                return res.status(400).json({ message: "User has already added this collection" });
            } else {
                collection.userIds.push(userId);
                await collection.save();
            }
        } else {
            // If collection does not exist, create a new one
            collection = new Collection({ name, id, type, songAmount, image, userIds: [userId] });
            await collection.save();
        }

        const responseCollection = await Collection.findOne({ id }).select('-userIds');
        res.status(201).json(responseCollection);
    } catch (error) {
        console.log(error.message);
        res.status(500).json({ message: "Server error" });
    }
};

// DELETE a specific collection by ID
const deleteCollection = async (req, res) => {
    const { id } = req.params;
    const userId = req.user.userId;

    try {
        const collection = await Collection.findOne({ id });

        if (!collection) {
            return res.status(404).json({ message: "Collection not found" });
        }

        // Remove user ID from the collection's user array
        collection.userIds = collection.userIds.filter(uid => uid.toString() !== userId.toString());

        if (collection.userIds.length === 0) {
            // If no users are associated with the collection, delete it
            await collection.deleteOne({ id });
        } else {
            // Otherwise, save the updated collection
            await collection.save();
        }

        res.status(200).json({ message: "Collection updated" });
    } catch (error) {
        console.log(error.message);
        res.status(500).json({ message: "Server error" });
    }
};

module.exports = {
    getAllCollections,
    getCollectionById,
    createCollection,
    deleteCollection,
};