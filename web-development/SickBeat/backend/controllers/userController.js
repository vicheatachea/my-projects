const User = require("../models/userModel");
const ValidationError = require("../models/errors");
const mongoose = require("mongoose");
const bcrypt = require("bcrypt");
const jwt = require("jsonwebtoken");
require("dotenv").config();

const jwt_secret_key = process.env.JWT_SECRET;

const getUserData = async (req) => {
	try {
		const userId = req.user ? req.user.userId : null;
		const { email } = req.body;

		const query = userId ? { _id: userId } : { email: email };
		const userData = await User.findOne(query).exec();

		return userData || null;
	} catch (error) {
		console.error("Error fetching user data:", error);
		return null;
	}
};

const fetchUserData = async (req, res) => {
	const userData = await getUserData(req);
	if (userData) {
		res.status(200).json(userData);
	} else {
		res.status(404).json({ message: "User not found!" });
	}
};

const registerUser = async (req, res, next) => {
	const { username, email, password } = req.body;
    const data = { username, email, password, collections: [] };
    
	try {
		const newUser = await User.signup(data);
		loginUser(req, res, next, newUser, password);
	} catch (error) {
		if (error instanceof mongoose.Error.ValidationError) {
			return res.status(400).json({
				message: "Invalid input",
				error: error.message,
				errors: error.errors, // Provide detailed validation errors
			});
		} else if (error.code === 11000) {
			// Handle duplicate key error (e.g., email already exists)
			console.error("Duplicate key error:", error.message);
			return res.status(409).json({
				message: "Duplicate key error!",
				error: error.message,
			});
		} else if (error instanceof mongoose.Error.CastError) {
			// Handle cast errors
			return res.status(400).json({
				message: "Invalid data type!",
				error: error.message,
			});
		} else if (error instanceof ValidationError) {
			console.error("Validation Error:", error.message);
			res.status(422).json({
				message: error.message,
			});
		} else {
			console.error("Error:", error); // Log unexpected errors for debugging
			return res.status(500).json({
				message: error.message,
			});
		}
	}
};

const loginUser = async (
	req,
	res,
	next,
	user = null,
	unHashedPassword = null
) => {
	try {
		const { email, password } = user || req.body;

		if (!email || (!password && !unHashedPassword)) {
			return res
				.status(400)
				.json({ message: "Email and password are required!" });
		}

		const userData = user || (await getUserData(req));

		if (!userData) {
			return res.status(404).json({ message: "User not found!" });
		}

		const passwordMatch = await bcrypt.compare(
			unHashedPassword || password,
			userData.password
		);
		const emailMatch = email === userData.email;

		if (!passwordMatch || !emailMatch) {
			return res
				.status(401)
				.json({ message: "Invalid password or email!" });
		}

		const token = jwt.sign({ userId: userData._id }, jwt_secret_key, {
			expiresIn: "1d",
		});

		res.status(200).json({ message: "Login successful", token });
	} catch (error) {
		if (error instanceof ValidationError) {
			console.error("Validation Error:", error.message);
			res.status(422).json({
				message: error.message,
			});
		} else {
			res.status(500).json({
				message: "Internal server error",
				error: error.message,
			});
		}
	}
};

const updateUser = async (req, res) => {
	const { username, email, password, role } = req.body;

	if (!username && !email && !password && !role) {
		return res.status(400).json({ message: "No data to update." });
	}

	const userData = await getUserData(req);

	if (!userData) {
		return res.status(404).json({ message: "User not found!" });
	}

	const userId = userData._id;

	if (!mongoose.Types.ObjectId.isValid(userId)) {
		return res.status(400).json({ message: "Invalid ID!" });
	}

	try {
		const updatedData = {};

		if (username) {
			updatedData.username = username;
		}

		if (email) {
			updatedData.email = email;
		}

		if (password) {
			updatedData.password = password;
		}

		if (role && req.user && req.user.role === "admin") {
			updatedData.role = role;
		}

		const updatedUser = await User.update(userId, updatedData);

		if (updatedUser) {
			res.status(200).json(updatedUser);
		} else {
			res.status(404).json({ message: "User not found!" });
		}
	} catch (error) {
		if (error instanceof mongoose.Error.ValidationError) {
			res.status(400).json({
				message: "Invalid input",
				error: error.message,
			});
		} else if (error instanceof ValidationError) {
			console.error("Validation Error:", error.message);
			res.status(422).json({
				message: error.message,
			});
		} else {
			res.status(500).json({
				message: "Failed to update user!",
				error: error.message,
			});
		}
	}
};

const deleteUser = async (req, res) => {
	const userData = await getUserData(req);

	if (!userData) {
		return res.status(404).json({ message: "User not found" });
	}

	const userId = userData._id;

	try {
		const isDeleted = await User.findOneAndDelete({ _id: userId });
		if (isDeleted) {
			res.status(204).json({ message: "User deleted" });
		} else {
			res.status(404).json({ message: "User not found" });
		}
	} catch (error) {
		if (error instanceof mongoose.Error.ValidationError) {
			res.status(400).json({
				message: "Invalid input",
				error: error.message,
			});
		} else {
			res.status(500).json({
				message: "Failed to delete user",
				error: error.message,
			});
		}
	}
};

// WORK IN PROGRESS
const addPlaylist = async (req, res) => {
	const playlist = req.body;
	const jwt = Cookies.get("jwt");

	if (!jwt) {
		return res.status(401).json({ message: "Unauthorized" });
	}

	const userId = jwt.verify(jwt, jwt_secret_key).userId;

	try {
		const updatedUser = await User.findOneAndUpdate(
			{ _id: userId },
			{ $push: { playlists: playlist } },
			{ new: true }
		);
		if (updatedUser) {
			res.status(200).json(updatedUser);
		} else {
			res.status(404).json({ message: "User not found" });
		}
	} catch (error) {
		if (error instanceof mongoose.Error.ValidationError) {
			res.status(400).json({
				message: "Invalid input",
				error: error.message,
			});
		} else {
			res.status(500).json({
				message: "Failed to update user",
				error: error.message,
			});
		}
	}
};

module.exports = {
	fetchUserData,
	registerUser,
	loginUser,
	updateUser,
	deleteUser,
};
