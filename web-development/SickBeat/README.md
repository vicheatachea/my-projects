
## Backend
[Backend](backend)

## Frontend
[Frontend](frontend)

## Documentation
[Swagger docs](backend/swagger.yaml) - API docs route: /api-docs

## Tests
[Tests](backend/tests) - It does not seem to be possible to do tests to the Spotify endpoint. This is due to the fact that the access token can never be obtained since it only comes from the **/api/spotify/callback** endpoint. This endpoint cannot be accessed during testing because it is a result of a redirect from Spotify's website. This redirect happens when the user accesses **/api/spotify/login** and manually authorizes the application with their credentials.

