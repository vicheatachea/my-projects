# Project Backend

## General Setup

In the same path as this file ([project-backend](.)), create a `.env` file and add the following code:

```env
PORT=your-port-number
MONGO_URI=your-mongodb-uri
JWT_SECRET=your-jwt-secret
```

Replace `your-port-number` with the port number you want the server to run on, `your-mongodb-uri` with the URI of your MongoDB database and `your-jwt-secret` with a secret key for JWT.

## Spotify Setup

Head to the [Spotify Developer Dashboard](https://developer.spotify.com/dashboard) and create a new application.
- Make sure to add `http://localhost:4000/api/spotify/callback` as a redirect URI.
- Under "Which API/SDKs are you planning to use?" select `Web API`.

After creating the application, head to **Settings** and copy the `Client ID` and `Client Secret`.

In the `.env` file, add the following code:

```env
CLIENT_ID=your-client-id
CLIENT_SECRET=your-client-secret
```

Replace the `your-client-id` and `your-client-secret` with your own credentials.
