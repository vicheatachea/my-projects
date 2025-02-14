const mongoose = require("mongoose");
const supertest = require("supertest");
const app = require("./app");
const api = supertest(app);
const User = require("../models/userModel");

beforeAll(async () => {
	await User.deleteMany({});
});

afterAll(async () => {
	await User.deleteMany({});
	mongoose.connection.close();
});

describe("User Routes", () => {
	describe("POST /api/user/register", () => {
		test("should create a new user", async () => {
			const newUser = {
				username: "testuser",
				email: "test@example.com",
				password: "password",
				collections: [],
			};

			const result = await api
				.post("/api/user/register")
				.send(newUser)
				.expect(200)
				.expect("Content-Type", /application\/json/);

			expect(result.body).toHaveProperty("token");
		});

		test("should not create a user with an existing email", async () => {
			const newUser = {
				username: "testuser",
				email: "test@example.com",
				password: "password",
				collections: [],
			};

			await api.post("/api/user/register").send(newUser);

			const result = await api
				.post("/api/user/register")
				.send(newUser)
				.expect(422);

			expect(result.body.message).toContain("Email is already in use!");

			const usersAtEnd = await User.find({});
			expect(usersAtEnd).toHaveLength(1);
		});

		test("should not create a user with missing fields", async () => {
			const newUser = {
				username: "testuser",
				email: "",
				password: "password",
				collections: [],
			};

			const result = await api
				.post("/api/user/register")
				.send(newUser)
				.expect(422);

			expect(result.body.message).toContain("All fields are required!");
		});

		test("should not create a user with an invalid email", async () => {
			const newUser = {
				username: "testuser",
				email: "test",
				password: "password",
				collections: [],
			};

			const result = await api
				.post("/api/user/register")
				.send(newUser)
				.expect(422);

			expect(result.body.message).toContain("Email is invalid!");
		});

		test("should not create a user with a short password", async () => {
			const newUser = {
				username: "testuser",
				email: "test@example.com",
				password: "pass",
				collections: [],
			};

			const result = await api
				.post("/api/user/register")
				.send(newUser)
				.expect(422);

			expect(result.body.message).toContain(
				"Password must be at least 8 characters long!"
			);
		});
	});

	describe("POST /api/user/login", () => {
		test("should login a user", async () => {
			const newUser = {
				email: "test@example.com",
				password: "password",
			};

			await api
				.post("/api/user/login")
				.send(newUser)
				.expect(200)
				.expect("Content-Type", /application\/json/);
		});

		test("should not login a user with wrong email", async () => {
			const newUser = {
				email: "test2@example.com",
				password: "password",
			};

			const result = await api
				.post("/api/user/login")
				.send(newUser)
				.expect(404);

			expect(result.body.message).toContain("User not found!");
		});

		test("should not login a user with wrong password", async () => {
			const newUser = {
				email: "test@example.com",
				password: "password2",
			};

			const result = await api
				.post("/api/user/login")
				.send(newUser)
				.expect(401);

			expect(result.body.message).toContain("Invalid password or email!");
		});

		test("should not login a user with missing fields", async () => {
			const newUser = {
				email: "",
				password: "",
			};

			const result = await api
				.post("/api/user/login")
				.send(newUser)
				.expect(400);

			expect(result.body.message).toContain(
				"Email and password are required!"
			);
		});
	});

	describe("GET /api/user/data", () => {
		test("should get user profile", async () => {
			const newUser = {
				email: "test@example.com",
				password: "password",
			};

			const login = await api
				.post("/api/user/login")
				.send(newUser)
				.expect(200)
				.expect("Content-Type", /application\/json/);

			const token = login.body.token;

			const result = await api
				.get("/api/user/data")
				.set("Cookie", [`jwt=${token}`])
				.expect(200)
				.expect("Content-Type", /application\/json/);

			expect(result.body).toHaveProperty("username");
			expect(result.body).toHaveProperty("email");
			expect(result.body).toHaveProperty("collections");
			expect(result.body).toHaveProperty("role");
		});

		test("should not get user profile without token", async () => {
			const result = await api.get("/api/user/data").expect(401);

			expect(result.body.message).toContain("Access token is missing");
		});

		test("should not get user profile with invalid token", async () => {
			const result = await api
				.get("/api/user/data")
				.set("Cookie", ["jwt=invalidtoken"])
				.expect(403);

			expect(result.body.message).toContain("Invalid or expired token");
		});
	});

	describe("PATCH /api/user/update", () => {
		test("should update user profile", async () => {
			const newUser = {
				email: "test@example.com",
				password: "password",
			};

			const login = await api
				.post("/api/user/login")
				.send(newUser)
				.expect(200)
				.expect("Content-Type", /application\/json/);

			const token = login.body.token;

			const updatedUser = {
				username: "updateduser",
				email: "updatedtest@example.fi",
				password: "updatedpassword",
			};

			const result = await api
				.patch("/api/user/update")
				.set("Cookie", [`jwt=${token}`])
				.send(updatedUser)
				.expect(200)
				.expect("Content-Type", /application\/json/);

			expect(result.body.email).toBe(updatedUser.email);
			expect(result.body.username).toBe(updatedUser.username);
			expect(result.body.password).not.toBe(updatedUser.password);
		});

		test("should not update user profile without token", async () => {
			const updatedUser = {
				username: "updateduser",
				email: "updated@example.com",
				password: "updatedpassword",
			};

			const result = await api
				.patch("/api/user/update")
				.send(updatedUser)
				.expect(401);

			expect(result.body.message).toContain("Access token is missing");
		});

		test("should not update user profile with invalid token", async () => {
			const updatedUser = {
				username: "updateduser",
				email: "testupdated@example.com",
			};

			const result = await api
				.patch("/api/user/update")
				.set("Cookie", ["jwt=invalidtoken"])
				.send(updatedUser)
				.expect(403);

			expect(result.body.message).toContain("Invalid or expired token");
		});
	});

	describe("DELETE /api/user/delete", () => {
		test("should delete user profile", async () => {
			const newUser = {
				email: "updatedtest@example.fi",
				password: "updatedpassword",
			};

			const login = await api
				.post("/api/user/login")
				.send(newUser)
				.expect(200)
				.expect("Content-Type", /application\/json/);

			const token = login.body.token;

			await api
				.delete("/api/user/delete")
				.set("Cookie", [`jwt=${token}`])
				.expect(204);
		});

		test("should not delete user profile without token", async () => {
			const result = await api.delete("/api/user/delete").expect(401);

			expect(result.body.message).toContain("Access token is missing");
		});

		test("should not delete user profile with invalid token", async () => {
			const result = await api
				.delete("/api/user/delete")
				.set("Cookie", ["jwt=invalidtoken"])
				.expect(403);

			expect(result.body.message).toContain("Invalid or expired token");
		});

		test("should not delete user profile without user", async () => {
			const newUser = {
				email: " ",
				password: " ",
			};

			const login = await api
				.post("/api/user/login")
				.send(newUser)
				.expect(404)
				.expect("Content-Type", /application\/json/);

			expect(login.body.message).toContain("User not found!");
		});
	});
});
