# URL Shortening Service
URL Shortener is a Spring Boot + MongoDB service that converts long URLs into short Base62 codes, supports custom-code lookups, tracks access counts, and lets you update/delete mappings. It’s designed as a simple REST API that can be deployed locally or to a cloud environment.

 <img width="766" height="266" alt="Screenshot 2026-01-14 at 1 56 48 pm" src="https://github.com/user-attachments/assets/2be79ae1-d472-43dd-8756-1e062a272587" />

 
## Features
1. Generate a short code for a given long URL (Base62 encoded sequence).

2. Redirect / fetch the original URL by short code.

3. Track and retrieve access statistics (accessCount).

4. Update an existing short URL to point to a new long URL.

5. Delete an existing short URL mapping.




## Tech stack
1. Java + Spring Boot (REST API, dependency injection).

2. Spring Data MongoDB (persistence layer).

3. MongoDB (stores UrlMapping documents).

4. JUnit 5 + Mockito (unit testing).

## API endpoints (example)
### 1. Create short URL
`POST /`

Body
```json
{ "longUrl": "https://example.com/some/very/long/url" }
```

Response
```json
{
  "shortCode": "abc12",
  "longUrl": "https://example.com/very/long/url",
  "accessCount": 0
}
```
### 2. Retrieve Original URL
`GET /{shortCode}`

Behavior: performs HTTP redirect to the requested original URL

### 3. Get access stats
`GET /stats/{shortCode}`
Response
```json
{
  "accessCount": 1,
}
```
### 4. Delete mapping
`DELETE /{shortCode}` → returns true/false

### 5. Update mapping
`PUT /{shortCode}`

Body

```json
{ "oldLongUrl": "https://old-site.com" }
```
Response

```json
{ "newLongUrl": "https://new-site.com" }
```


