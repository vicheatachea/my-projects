const mongoose = require("mongoose");
const supertest = require("supertest");
const app = require("./app");
const api = supertest(app);
const Collection = require("../models/collectionModel");
const User = require("../models/userModel");

let token;

beforeAll(async () => {
    await User.deleteMany({});
    await Collection.deleteMany({});

    const newUser = {
        username: "testuser",
        email: "test@example.com",
        password: "password",
        collections: [],
    };

    const userResponse = await api.post("/api/user/register").send(newUser);
    token = userResponse.body.token;
});

afterAll(async () => {
    await User.deleteMany({});
    await Collection.deleteMany({});
    mongoose.connection.close();
});

describe("Collections API", () => {
    describe("GET /api/collections", () => {
        test("should get all collections for authenticated user", async () => {
            await api
                .get("/api/collections")
                .set("Cookie", [`jwt=${token}`])
                .expect(200)
                .expect("Content-Type", /application\/json/);
        });

        test("should not get collections without token", async () => {
            await api.get("/api/collections").expect(401);
        });
    });

    describe("POST /api/collections", () => {
        test("should create a new collection", async () => {
            const newCollection = {
                name: "Test Collection",
                id: "test-collection",
                type: "album",
                songAmount: 10,
                image: "http://example.com/image.jpg",
            };

            const result = await api
                .post("/api/collections")
                .set("Cookie", [`jwt=${token}`])
                .send(newCollection)
                .expect(201)
                .expect("Content-Type", /application\/json/);

            expect(result.body.name).toBe(newCollection.name);
        });

        test("should not create a collection with missing id", async () => {
            const newCollection = {
                name: "Test Collection",
                id: "",
                type: "album",
                songAmount: 10,
                image: "http://example.com/image.jpg",
            };

            const result = await api
                .post("/api/collections")
                .set("Cookie", [`jwt=${token}`])
                .send(newCollection)
                .expect(400);

            expect(result.body.message).toContain("All fields are required!");
        });

        test("should not create a collection with missing name", async () => {
            const newCollection = {
                name: "",
                id: "test-collection",
                type: "album",
                songAmount: 10,
                image: "http://example.com/image.jpg",
            };

            const result = await api
                .post("/api/collections")
                .set("Cookie", [`jwt=${token}`])
                .send(newCollection)
                .expect(400);

            expect(result.body.message).toContain("All fields are required!");
        });

        test("should not create a collection without token", async () => {
            const newCollection = {
                name: "Test Collection",
                id: "test-collection",
                type: "album",
                songAmount: 10,
                image: "http://example.com/image.jpg",
            };

            await api.post("/api/collections").send(newCollection).expect(401);
        });

        test("should not create a collection that the user already has", async () => {
            const newCollection = {
                name: "Test Collection",
                id: "test-collection2",
                type: "album",
                songAmount: 10,
                image: "http://example.com/image.jpg",
            };

            // Create the collection for the first time
            await api
                .post("/api/collections")
                .set("Cookie", [`jwt=${token}`])
                .send(newCollection)
                .expect(201);

            // Attempt to create the same collection again
            const result = await api
                .post("/api/collections")
                .set("Cookie", [`jwt=${token}`])
                .send(newCollection)
                .expect(400);

            expect(result.body.message).toContain("User has already added this collection");
        });
    });

    describe("GET /api/collections/:id", () => {
        test("should get a specific collection by ID", async () => {
            const collectionId = "test-collection";

            const result = await api
                .get(`/api/collections/${collectionId}`)
                .set("Cookie", [`jwt=${token}`])
                .expect(200)
                .expect("Content-Type", /application\/json/);

            expect(result.body.id).toBe(collectionId);
        });

        test("should not get a collection with invalid ID", async () => {
            const invalidId = "invalid-id";

            const result = await api
                .get(`/api/collections/${invalidId}`)
                .set("Cookie", [`jwt=${token}`])
                .expect(404);

            expect(result.body.message).toContain("Collection not found");
        });

        test("should not get a collection without token", async () => {
            const collectionId = "test-collection";

            await api.get(`/api/collections/${collectionId}`).expect(401);
        });
    });

    describe("DELETE /api/collections/:id", () => {
        test("should delete a specific collection by ID", async () => {
            const collectionId = "test-collection";

            await api
                .delete(`/api/collections/${collectionId}`)
                .set("Cookie", [`jwt=${token}`])
                .expect(200);

            const result = await api
                .get(`/api/collections/${collectionId}`)
                .set("Cookie", [`jwt=${token}`])
                .expect(404);

            expect(result.body.message).toContain("Collection not found");
        });

        test("should not delete a collection with invalid ID", async () => {
            const invalidId = "invalid-id";

            const result = await api
                .delete(`/api/collections/${invalidId}`)
                .set("Cookie", [`jwt=${token}`])
                .expect(404);

            expect(result.body.message).toContain("Collection not found");
        });

        test("should not delete a collection without token", async () => {
            const collectionId = "test-collection";

            await api.delete(`/api/collections/${collectionId}`).expect(401);
        });
    });
});