const mongoose = require('mongoose');

const collectionSchema = new mongoose.Schema({
    name: {
        type: String,
        required: true,
    },
    id: {
        type: String,
        required: true,
        unique: true,
    },
    type: {
        type: String,
        required: true,
    },
    songAmount: {
        type: Number,
        required: true,
    },
    image: {
        type: String, // URL of the album cover image
        required: true,
    },
    userIds: {
        type: [mongoose.Schema.Types.ObjectId],
        ref: 'User',
        required: true,
    },
});

const Collection = mongoose.model('Collection', collectionSchema);

module.exports = Collection;