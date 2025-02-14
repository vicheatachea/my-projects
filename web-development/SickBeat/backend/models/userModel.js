const mongoose = require("mongoose");
const validator = require("validator");
const bcrypt = require("bcrypt");
const Collection = require("./collectionModel");
const ValidationError = require("./errors");

const userSchema = new mongoose.Schema(
	{
		username: {
			type: String,
			required: true,
			unique: true,
		},
		email: {
			type: String,
			required: true,
			unique: true,
		},
		password: {
			type: String,
			required: true,
		},
		role: {
			type: String,
			enum: ["admin", "user"],
			default: "user",
		},
		collections: {
			type: [Collection.schema],
			default: [],
		},
	},
	{
		timestamps: true,
	}
);

const isValidEmail = (email) => {
	if (!validator.isEmail(email)) {
		throw new ValidationError("Email is invalid!");
	}

	const emailRegex =
		/^[^\s@]+@[^\s@]+\.(com|org|net|edu|gov|mil|int|us|uk|ca|de|fr|au|jp|cn|in|br|fi)$/;
	return emailRegex.test(email);
};

const hashPassword = async function (password) {
	return await bcrypt.hash(password, 10);
};

const validateUser = async function (data) {
	const { username, email, password } = { ...data };

	if (username && username.length == 0) {
		throw new ValidationError("Username cannot be empty!");
	}

	if (email && email.length == 0) {
		throw new ValidationError("Email cannot be empty!");
	}

	if (email && !isValidEmail(email)) {
		throw new ValidationError("Email is invalid!");
	}

	if (password && !validator.isLength(password, { min: 8 })) {
		throw new ValidationError(
			"Password must be at least 8 characters long!"
		);
	}
};

userSchema.statics.signup = async function (data) {
	const { username, email, password, collection } = { ...data };

	if (!username || !email || !password) {
		throw new ValidationError("All fields are required!");
	}

	await validateUser(data);

	const existingUser = await this.findOne({ email });

	if (existingUser) {
		throw new ValidationError("Email is already in use!");
	}

	const hashedPassword = await hashPassword(password);

	const user = await this.create({
		username,
		email,
		password: hashedPassword,
		collection,
	});

	return user;
};

userSchema.statics.update = async function (userId, updatedData) {
	if (!userId || !updatedData) {
		throw new ValidationError("Missing required fields");
	}

	updatedData = {
		...updatedData,
	};

	await validateUser(updatedData);
	if (updatedData.password) {
		updatedData.password = await hashPassword(updatedData.password);
	}

	const updatedUser = await this.findOneAndUpdate(
		{ _id: userId },
		updatedData,
		{ new: true }
	);

	return updatedUser;
};

const User = mongoose.model("User", userSchema);

module.exports = User;
