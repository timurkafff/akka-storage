# Akka HTTP Storage Server

This project is an example of a file storage backend built with **Akka HTTP** (Typed), featuring:
- JWT-based authentication
- CORS support for browser clients
- File upload and download endpoints
- Simple login endpoint with hardcoded credentials

## Project Structure

- `QuickstartApp.scala` — Main entry point, sets up the HTTP server and global CORS.
- `StorageRoutes.scala` — Defines authenticated routes for file upload and download.
- `LoginRoutes.scala` — Defines the login endpoint, returns JWT token.
- `JwtService.scala` — Handles JWT token creation and validation.
- `JsonFormats.scala` — JSON (un)marshalling for API models.
- `Models.scala` — Case classes for API responses.

## Running the Server

1. **Start the server:**
   ```sh
   sbt run
   ```
   The server will start on [http://localhost:8080](http://localhost:8080).

2. **Login to get a JWT token:**
   ```sh
   curl -X POST http://localhost:8080/login -d "username=admin&password=password"
   ```
   The response will contain a JWT token.

3. **Upload a file (authenticated):**
   ```sh
   curl -X POST http://localhost:8080/upload \
     -H "Authorization: Bearer <your_token>" \
     -F "file=@/path/to/your/file.txt"
   ```

4. **Download a file (authenticated):**
   ```sh
   curl -X GET http://localhost:8080/download/<filename> \
     -H "Authorization: Bearer <your_token>" -O
   ```

## CORS

CORS headers are set globally, so you can use this backend from a browser-based frontend (e.g., running on `localhost:5500`).

## Authentication

- All upload/download endpoints require a valid JWT token in the `Authorization: Bearer ...` header.
- The `/login` endpoint uses hardcoded credentials (`admin` / `password`) for demonstration.

## Notes

- Uploaded files are stored in the `storage/` directory.
- This project is for demonstration and learning purposes. Do **not** use hardcoded credentials or secrets in production.