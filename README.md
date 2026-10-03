# JFetch

A lightweight, developer-friendly HTTP client for Java.

JFetch provides a simple API for making HTTP requests and working with responses, with built-in JSON support through Jackson.

Built on Java's standard `HttpClient`, JFetch aims to make HTTP requests less verbose and more convenient.

## Features

* Simple API for HTTP requests
* GET, POST, PUT, PATCH, and DELETE
* Synchronous and asynchronous requests
* URL query parameters
* Custom request headers
* JSON request bodies
* JSON response parsing
* JSON-to-Java object mapping
* Built on Java's standard `HttpClient`

## Installation

### Maven

```xml
<dependency>
     <groupId>io.github.rijalharayo</groupId>
     <artifactId>jfetch</artifactId>
     <version>1.0.0</version>
</dependency>
```

JFetch is published on Maven Central.

## Quick Start

### GET Request

```java
import com.jfetch.Http;
import com.jfetch.Response;

public class Main {
     public static void main(String[] args) throws Exception {
          Response response = Http.get(
                  "https://api.example.com/users"
          );

          System.out.println(response.text());
     }
}
```

### JSON Response

JFetch can parse a JSON response into a Jackson `JsonNode`:

```java
Response response = Http.get(
        "https://api.example.com/users"
);

System.out.println(response.json());
```

Or map the response directly to a Java class:

```java
User user = response.json(User.class);
```

### Request Body

Create a JSON request body using `RequestBody.json()`:

```java
User user = new User(
     "John",
     "john@example.com"
);

Response response = Http.post(
        "https://api.example.com/users",
        RequestBody.json(user)
);
```

JFetch automatically sets the request's `Content-Type` to:

```text
application/json
```

### Request Headers

Create custom headers with `Headers.of()`:

```java
Headers headers = Headers.of(
     "Authorization", "Bearer token",
     "Accept", "application/json"
);

Response response = Http.get(
        "https://api.example.com/users",
        headers
);
```

Headers can also be combined with query parameters and request bodies.

### Query Parameters

Query parameters can be supplied using a `Map`:

```java
Map<String, Object> params = Map.of(
     "page", 1,
     "limit", 10
);

Response response = Http.get(
        "https://api.example.com/users",
        params
);
```

JFetch handles URL encoding and appends the parameters to the request URL.

### Asynchronous Requests

JFetch also supports asynchronous requests using `CompletableFuture`:

```java
Http.getAsync(
        "https://api.example.com/users"
).thenAccept(response -> {
     System.out.println(response.text());
});
```

The asynchronous API is available for GET, POST, PUT, PATCH, and DELETE requests.

## Response

`Response` provides access to the HTTP response:

```java
response.statusCode();
response.text();
response.json();
response.json(User.class);
response.header("Content-Type");
response.headers();
```

It also provides convenience methods for checking the response status:

```java
response.isSuccess();
response.isClientError();
response.isServerError();
```

HTTP error responses such as `4xx` and `5xx` are returned as normal `Response` objects rather than being automatically thrown as exceptions.

## Supported Methods

| Method | Sync | Async |
| ------ | ---- | ----- |
| GET    | Yes  | Yes   |
| POST   | Yes  | Yes   |
| PUT    | Yes  | Yes   |
| PATCH  | Yes  | Yes   |
| DELETE | Yes  | Yes   |

All supported methods can optionally use:

* Query parameters
* Request headers
* Request bodies

## Requirements

* Java 21 or newer

## Dependencies

JFetch uses:

* Java's built-in `java.net.http.HttpClient`
* Jackson for JSON serialization and deserialization

## Project Status

JFetch 1.0.0 has been released and published to Maven Central.

The core HTTP functionality currently includes synchronous and asynchronous requests, query parameters, custom headers, JSON request bodies, and JSON response parsing.

JFetch is still under active development, with additional features and improvements planned for future releases.

## License

JFetch is licensed under the MIT License.

See the `LICENSE` file for the full license text.
