## Http

`Http` provides static methods for sending HTTP requests using JFetch.

It supports:

* GET
* POST
* PUT
* PATCH
* DELETE
* Synchronous requests
* Asynchronous requests
* URL query parameters
* Custom request headers
* Request bodies

All requests are sent using Java's built-in `HttpClient`.

---

### GET Requests

The simplest GET request can be sent using:

```java
Response response = Http.get(
        "https://api.example.com/users"
);
```

The method returns a `Response` containing the server's response.

### GET with Query Parameters

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

JFetch URL-encodes the parameter names and values and appends them to the URL.

For example, the request above produces a URL similar to:

```text
https://api.example.com/users?page=1&limit=10
```

Existing query parameters are preserved:

```java
Map<String, Object> params = Map.of(
     "page", 2
);

Response response = Http.get(
        "https://api.example.com/users?sort=name",
        params
);
```

The resulting URL is:

```text
https://api.example.com/users?sort=name&page=2
```

### GET with Headers

Custom request headers can be supplied using `Headers`:

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

Query parameters and headers can also be used together:

```java
Response response = Http.get(
        "https://api.example.com/users",
        params,
        headers
);
```

---

## POST Requests

A POST request can be sent with a JSON request body:

```java
Response response = Http.post(
        "https://api.example.com/users",
        RequestBody.json(user)
);
```

JFetch automatically sets the request's `Content-Type` header using the content type provided by the `RequestBody`.

### POST without a Body

A POST request does not require a request body:

```java
Response response = Http.post(
        "https://api.example.com/users"
);
```

### POST with Headers

Headers can be supplied without a request body:

```java
Response response = Http.post(
        "https://api.example.com/users",
        headers
);
```

Headers can also be combined with a request body:

```java
Response response = Http.post(
        "https://api.example.com/users",
        RequestBody.json(user),
        headers
);
```

### POST with Query Parameters

Query parameters can be supplied together with a request body:

```java
Response response = Http.post(
        "https://api.example.com/users",
        params,
        RequestBody.json(user)
);
```

All three can be combined:

```java
Response response = Http.post(
        "https://api.example.com/users",
        params,
        RequestBody.json(user),
        headers
);
```

---

## PUT Requests

PUT requests use the same API structure as POST requests.

### PUT with a Body

```java
Response response = Http.put(
        "https://api.example.com/users/1",
        RequestBody.json(user)
);
```

### PUT without a Body

```java
Response response = Http.put(
        "https://api.example.com/users/1"
);
```

### PUT with Headers

```java
Response response = Http.put(
        "https://api.example.com/users/1",
        headers
);
```

### PUT with Query Parameters

```java
Response response = Http.put(
        "https://api.example.com/users/1",
        params
);
```

Query parameters, a request body, and headers can all be combined:

```java
Response response = Http.put(
        "https://api.example.com/users/1",
        params,
        RequestBody.json(user),
        headers
);
```

---

## PATCH Requests

PATCH requests are useful for partially updating an existing resource.

### PATCH with a Body

```java
Response response = Http.patch(
        "https://api.example.com/users/1",
        RequestBody.json(user)
);
```

### PATCH without a Body

```java
Response response = Http.patch(
        "https://api.example.com/users/1"
);
```

### PATCH with Headers

```java
Response response = Http.patch(
        "https://api.example.com/users/1",
        headers
);
```

### PATCH with Query Parameters

```java
Response response = Http.patch(
        "https://api.example.com/users/1",
        params
);
```

Query parameters, a request body, and headers can all be combined:

```java
Response response = Http.patch(
        "https://api.example.com/users/1",
        params,
        RequestBody.json(user),
        headers
);
```

---

## DELETE Requests

DELETE requests can be sent without a body:

```java
Response response = Http.delete(
        "https://api.example.com/users/1"
);
```

### DELETE with Headers

```java
Response response = Http.delete(
        "https://api.example.com/users/1",
        headers
);
```

### DELETE with Query Parameters

```java
Response response = Http.delete(
        "https://api.example.com/users",
        params
);
```

### DELETE with a Body

JFetch also allows a request body to be supplied with DELETE:

```java
Response response = Http.delete(
        "https://api.example.com/users",
        RequestBody.json(data)
);
```

Query parameters, a request body, and headers can all be combined:

```java
Response response = Http.delete(
        "https://api.example.com/users",
        params,
        RequestBody.json(data),
        headers
);
```

Whether a server accepts a body on a DELETE request depends on the server or API being used.

---

## Asynchronous Requests

JFetch provides asynchronous versions of every HTTP method.

Asynchronous methods return a `CompletableFuture<Response>` instead of waiting for the server response.

### Asynchronous GET

```java
Http.getAsync(
        "https://api.example.com/users"
).thenAccept(response -> {
     System.out.println(response.text());
});
```

### Asynchronous GET with Parameters

```java
Http.getAsync(
        "https://api.example.com/users",
        params
).thenAccept(response -> {
     System.out.println(response.text());
});
```

### Asynchronous GET with Headers

```java
Http.getAsync(
        "https://api.example.com/users",
        headers
).thenAccept(response -> {
     System.out.println(response.text());
});
```

Query parameters and headers can also be combined:

```java
Http.getAsync(
        "https://api.example.com/users",
        params,
        headers
).thenAccept(response -> {
     System.out.println(response.text());
});
```

---

## Asynchronous POST, PUT, PATCH, and DELETE

The asynchronous API follows the same argument structure as the synchronous API.

For example, an asynchronous POST request with a body:

```java
Http.postAsync(
        "https://api.example.com/users",
        RequestBody.json(user)
).thenAccept(response -> {
     System.out.println(response.statusCode());
});
```

With query parameters and headers:

```java
Http.postAsync(
        "https://api.example.com/users",
        params,
        RequestBody.json(user),
        headers
).thenAccept(response -> {
     System.out.println(response.text());
});
```

The same structure is available for PUT:

```java
Http.putAsync(
        "https://api.example.com/users/1",
        RequestBody.json(user),
        headers
).thenAccept(response -> {
     System.out.println(response.statusCode());
});
```

PATCH:

```java
Http.patchAsync(
        "https://api.example.com/users/1",
        RequestBody.json(user),
        headers
).thenAccept(response -> {
     System.out.println(response.statusCode());
});
```

And DELETE:

```java
Http.deleteAsync(
        "https://api.example.com/users/1",
        headers
).thenAccept(response -> {
     System.out.println(response.statusCode());
});
```

All asynchronous methods return:

```java
CompletableFuture<Response>
```

This allows standard `CompletableFuture` operations such as `thenApply()`, `thenAccept()`, `exceptionally()`, and `thenCompose()` to be used.

---

## Query Parameters

Query parameters are accepted as:

```java
Map<String, ?>
```

This allows values of different types to be supplied:

```java
Map<String, Object> params = Map.of(
     "page", 1,
     "limit", 20,
     "active", true
);
```

Values are converted to strings before being URL-encoded.

For example:

```java
Http.get(
        "https://api.example.com/users",
        params
);
```

produces a URL similar to:

```text
https://api.example.com/users?page=1&limit=20&active=true
```

JFetch uses UTF-8 URL encoding for query parameter names and values.

---

## Request Headers

Custom request headers are supplied through the `Headers` class:

```java
Headers headers = Headers.of(
     "Authorization", "Bearer token",
     "Accept", "application/json"
);
```

They can be used with synchronous and asynchronous requests.

For requests containing a `RequestBody`, JFetch automatically provides the body's content type. Custom headers supplied through `Headers` are then applied to the request.

If a custom header has the same name as an automatically generated header, the supplied header replaces the existing value.

---

## Request Body

HTTP methods that support request bodies accept a `RequestBody`:

```java
RequestBody body = RequestBody.json(user);

Response response = Http.post(
        "https://api.example.com/users",
        body
);
```

Currently, `RequestBody` provides JSON request bodies through:

```java
RequestBody.json(object)
```

See the [`RequestBody`](#requestbody) section for more information.

---

## HTTP Method Summary

JFetch provides synchronous and asynchronous versions of all supported HTTP methods.

| Method | Synchronous     | Asynchronous         | Query Parameters | Headers | Body |
| ------ | --------------- | -------------------- | ---------------- | ------- | ---- |
| GET    | `Http.get()`    | `Http.getAsync()`    | Yes              | Yes     | No   |
| POST   | `Http.post()`   | `Http.postAsync()`   | Yes              | Yes     | Yes  |
| PUT    | `Http.put()`    | `Http.putAsync()`    | Yes              | Yes     | Yes  |
| PATCH  | `Http.patch()`  | `Http.patchAsync()`  | Yes              | Yes     | Yes  |
| DELETE | `Http.delete()` | `Http.deleteAsync()` | Yes              | Yes     | Yes  |

For each method, JFetch provides overloads for the different combinations of URL, query parameters, request body, and headers.

---

## Request Flow

A typical JFetch request follows this structure:

```text
URL
 │
 ├── Query Parameters
 │
 ├── Request Headers
 │
 └── Request Body
        │
        ▼
   HTTP Request
        │
        ▼
 Java HttpClient
        │
        ▼
   Server Response
        │
        ▼
     Response
```

For synchronous requests, JFetch waits for the HTTP response and returns a `Response`.

For asynchronous requests, JFetch immediately returns a `CompletableFuture<Response>` and completes it when the server responds.

---

## Error Handling

Synchronous request methods can throw:

```java
IOException
InterruptedException
```

For example:

```java
try {
     Response response = Http.get(
             "https://api.example.com/users"
     );

     System.out.println(response.text());
}
catch (IOException | InterruptedException e) {
     e.printStackTrace();
}
```

Asynchronous methods do not declare these checked exceptions. Errors that occur while performing the request are propagated through the returned `CompletableFuture`.

They can be handled using `exceptionally()`:

```java
Http.getAsync(
        "https://api.example.com/users"
).thenAccept(response -> {
     System.out.println(response.text());
}).exceptionally(error -> {
     error.printStackTrace();
     return null;
});
```

HTTP error status codes such as `404` or `500` are still returned as normal `Response` objects. They can be checked using the status helper methods:

```java
if (response.isClientError()) {
     System.out.println("Client error");
}

if (response.isServerError()) {
     System.out.println("Server error");
}
```

JFetch does not currently throw an exception solely because the server returned an HTTP error status.

---

## Complete Example

The following example demonstrates query parameters, headers, a JSON request body, and response handling:

```java
Map<String, Object> params = Map.of(
     "notify", true
);

Headers headers = Headers.of(
     "Authorization", "Bearer token",
     "Accept", "application/json"
);

User user = new User(
     "John",
     "john@example.com"
);

Response response = Http.post(
        "https://api.example.com/users",
        params,
        RequestBody.json(user),
        headers
);

System.out.println("Status: " + response.statusCode());

if (response.isSuccess()) {
     System.out.println(response.json());
}
```

For an asynchronous version:

```java
Http.postAsync(
        "https://api.example.com/users",
        params,
        RequestBody.json(user),
        headers
).thenAccept(response -> {
     System.out.println("Status: " + response.statusCode());

     if (response.isSuccess()) {
          System.out.println(response.json());
     }
});
```

`Http` is the main entry point for making requests with JFetch. For response handling, see [`Response`](#response). For JSON request bodies, see [`RequestBody`](#requestbody), and for custom request headers, see [`Headers`](#headers).
