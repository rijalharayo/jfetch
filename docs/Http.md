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

* Persistent cookies & session saving

* Default base URLs

All requests are sent using Java's built-in `HttpClient`.

---

## Default Base URL

JFetch allows a default base URL to be configured for requests that use relative URLs.

A default base URL can be set using:

```java
Http.setDefaultBaseUrl(
     "https://api.example.com"
);
```

Once configured, requests can use relative paths:

```java
Response response = Http.get(
     "/users"
);
```

The request will be sent to:

```text
https://api.example.com/users
```

This is useful when an application communicates primarily with a single API.

For example, a JavaFX application can configure its backend URL once:

```java
Http.setDefaultBaseUrl(
     "http://localhost:8080"
);
```

and then make requests using only API paths:

```java
Http.get("/api/users");

Http.get("/api/subjects");

Http.post(
     "/api/assignments",
     RequestBody.json(assignment)
);
```

### Absolute URLs

Setting a default base URL does not prevent requests from being sent to other APIs.

If a request contains an absolute URL, JFetch uses that URL directly:

```java
Http.get(
     "https://api.github.com/users/octocat"
);
```

The configured default base URL is ignored for absolute URLs.

Therefore:

```text
Relative URL
     ↓
Default base URL + relative path

Absolute URL
     ↓
URL used directly
```

For example, if:

```java
Http.setDefaultBaseUrl(
     "https://api.example.com"
);
```

then:

```java
Http.get("/users");
```

produces:

```text
https://api.example.com/users
```

while:

```java
Http.get("https://api.github.com/users/octocat");
```

produces:

```text
https://api.github.com/users/octocat
```

### Base URL Without a Trailing Slash

The default base URL should not end with a trailing slash when using paths that begin with `/`.

Use:

```java
Http.setDefaultBaseUrl(
     "https://api.example.com"
);
```

then:

```java
Http.get("/users");
```

rather than:

```java
Http.setDefaultBaseUrl(
     "https://api.example.com/"
);
```

This keeps URL construction predictable and avoids duplicated slashes.

---

### GET Requests

The simplest GET request can be sent using:

```java
Response response = Http.get(
     "https://api.example.com/users"
);
```

The method returns a `Response` containing the server's response.

A relative URL can also be used when a default base URL is configured:

```java
Response response = Http.get(
     "/users"
);
```

---

### GET with Query Parameters

Query parameters can be supplied using a `Map`:

```java
Map<String, Object> params = Map.of(
     "page", 1,
     "limit", 10
);

Response response = Http.get(
     "/users",
     params
);
```

JFetch URL-encodes the parameter names and values and appends them to the URL.

For example, when the default base URL is:

```text
https://api.example.com
```

the request above produces a URL similar to:

```text
https://api.example.com/users?page=1&limit=10
```

Existing query parameters are preserved:

```java
Map<String, Object> params = Map.of(
     "page", 2
);

Response response = Http.get(
     "/users?sort=name",
     params
);
```

The resulting URL is:

```text
https://api.example.com/users?sort=name&page=2
```

Absolute URLs can still be used:

```java
Response response = Http.get(
     "https://api.example.com/users",
     params
);
```

---

### GET with Headers

Custom request headers can be supplied using `Headers`:

```java
Headers headers = Headers.of(
     "Authorization", "Bearer token",
     "Accept", "application/json"
);

Response response = Http.get(
     "/users",
     headers
);
```

Query parameters and headers can also be used together:

```java
Response response = Http.get(
     "/users",
     params,
     headers
);
```

---

## POST Requests

A POST request can be sent with a JSON request body:

```java
Response response = Http.post(
     "/users",
     RequestBody.json(user)
);
```

JFetch automatically sets the request's `Content-Type` header using the content type provided by the `RequestBody`.

### POST without a Body

A POST request does not require a request body:

```java
Response response = Http.post(
     "/users"
);
```

### POST with Headers

Headers can be supplied without a request body:

```java
Response response = Http.post(
     "/users",
     headers
);
```

Headers can also be combined with a request body:

```java
Response response = Http.post(
     "/users",
     RequestBody.json(user),
     headers
);
```

### POST with Query Parameters

Query parameters can be supplied together with a request body:

```java
Response response = Http.post(
     "/users",
     params,
     RequestBody.json(user)
);
```

All three can be combined:

```java
Response response = Http.post(
     "/users",
     params,
     RequestBody.json(user),
     headers
);
```

The same argument structures can be used with absolute URLs.

---

## PUT Requests

PUT requests use the same API structure as POST requests.

### PUT with a Body

```java
Response response = Http.put(
     "/users/1",
     RequestBody.json(user)
);
```

### PUT without a Body

```java
Response response = Http.put(
     "/users/1"
);
```

### PUT with Headers

```java
Response response = Http.put(
     "/users/1",
     headers
);
```

### PUT with Query Parameters

```java
Response response = Http.put(
     "/users/1",
     params
);
```

Query parameters, a request body, and headers can all be combined:

```java
Response response = Http.put(
     "/users/1",
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
     "/users/1",
     RequestBody.json(user)
);
```

### PATCH without a Body

```java
Response response = Http.patch(
     "/users/1"
);
```

### PATCH with Headers

```java
Response response = Http.patch(
     "/users/1",
     headers
);
```

### PATCH with Query Parameters

```java
Response response = Http.patch(
     "/users/1",
     params
);
```

Query parameters, a request body, and headers can all be combined:

```java
Response response = Http.patch(
     "/users/1",
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
     "/users/1"
);
```

### DELETE with Headers

```java
Response response = Http.delete(
     "/users/1",
     headers
);
```

### DELETE with Query Parameters

```java
Response response = Http.delete(
     "/users",
     params
);
```

### DELETE with a Body

JFetch also allows a request body to be supplied with DELETE:

```java
Response response = Http.delete(
     "/users",
     RequestBody.json(data)
);
```

Query parameters, a request body, and headers can all be combined:

```java
Response response = Http.delete(
     "/users",
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
     "/users"
).thenAccept(response -> {

     System.out.println(response.text());

});
```

### Asynchronous GET with Parameters

```java
Http.getAsync(
     "/users",
     params
).thenAccept(response -> {

     System.out.println(response.text());

});
```

### Asynchronous GET with Headers

```java
Http.getAsync(
     "/users",
     headers
).thenAccept(response -> {

     System.out.println(response.text());

});
```

Query parameters and headers can also be combined:

```java
Http.getAsync(
     "/users",
     params,
     headers
).thenAccept(response -> {

     System.out.println(response.text());

});
```

Absolute URLs can be used in the same way:

```java
Http.getAsync(
     "https://api.github.com/users/octocat"
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
     "/users",
     RequestBody.json(user)
).thenAccept(response -> {

     System.out.println(response.statusCode());

});
```

With query parameters and headers:

```java
Http.postAsync(
     "/users",
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
     "/users/1",
     RequestBody.json(user),
     headers
).thenAccept(response -> {

     System.out.println(response.statusCode());

});
```

PATCH:

```java
Http.patchAsync(
     "/users/1",
     RequestBody.json(user),
     headers
).thenAccept(response -> {

     System.out.println(response.statusCode());

});
```

And DELETE:

```java
Http.deleteAsync(
     "/users/1",
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
     "/users",
     params
);
```

with the default base URL:

```text
https://api.example.com
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
     "/users",
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

All methods support both relative URLs and absolute URLs. Relative URLs use the configured default base URL.

---

## Request Flow

A typical JFetch request follows this structure:

```text
URL
 │
 ├── Relative URL?
 │       │
 │       └── Default Base URL
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
          "/users"
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
     "/users"
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

The following example demonstrates configuring a default base URL, query parameters, headers, a JSON request body, and response handling:

```java
Http.setDefaultBaseUrl(
     "https://api.example.com"
);

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
     "/users",
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
     "/users",
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

An absolute URL can still be used when a default base URL is configured:

```java
Http.get(
     "https://api.github.com/users/octocat"
);
```

`Http` is the main entry point for making requests with JFetch. For response handling, see [`Response`](#response). For JSON request bodies, see [`RequestBody`](#requestbody), and for custom request headers, see [`Headers`](#headers).

---

## Cookies and Sessions

`Http` uses a single shared cookie manager, so cookies set by a server (for example a session cookie after logging in) are automatically stored and sent back on later requests.

```java
Http.post(
     "/login",
     RequestBody.json(credentials)
);

// The session cookie is sent automatically
Http.get("/profile");
```

Cookies are accepted using the `ACCEPT_ORIGINAL_SERVER` policy. This means a cookie is only stored when it comes from the server that the request was originally sent to. Cookies set by third-party servers are rejected.

Cookies are kept in memory by default and are lost when the application exits. JFetch can save them to disk and load them again, which lets an application stay signed in between runs.

### Saving Cookies

Cookies can be saved to the configured or default storage location:

```java
Http.saveCookies();
```

They can also be saved to a custom file:

```java
Http.saveCookies(
     Path.of("session.cookies")
);
```

Both methods throw `IOException` if the cookies cannot be saved.

### Loading Cookies

Cookies can be loaded from the configured or default storage location:

```java
Http.loadCookies();
```

They can also be loaded from a custom file:

```java
Http.loadCookies(
     Path.of("session.cookies")
);
```

Both methods throw `IOException` if the cookies cannot be loaded.

### Clearing Cookies

Cookies can be cleared in two separate ways.

Clear the cookies held in memory:

```java
Http.clearCookies();
```

Delete the saved cookie file:

```java
Http.clearSavedCookies();
```

These two operations are independent:

| Method                     | Clears memory | Deletes saved file |
| -------------------------- | ------------- | ------------------ |
| `Http.clearCookies()`      | Yes           | No                 |
| `Http.clearSavedCookies()` | No            | Yes                |

To fully sign out and forget the session, call both:

```java
Http.clearCookies();
Http.clearSavedCookies();
```

`clearSavedCookies()` throws `IOException` if the file cannot be deleted.

### Cookie Methods Summary

| Method                | Description                                        | Throws        |
| --------------------- | -------------------------------------------------- | ------------- |
| `saveCookies()`       | Saves cookies to the configured/default location   | `IOException` |
| `saveCookies(Path)`   | Saves cookies to a custom file                     | `IOException` |
| `loadCookies()`       | Loads cookies from the configured/default location | `IOException` |
| `loadCookies(Path)`   | Loads cookies from a custom file                   | `IOException` |
| `clearCookies()`      | Clears cookies from memory                         | None          |
| `clearSavedCookies()` | Deletes the saved cookie file                      | `IOException` |

### Example: Persistent Login

```java
Http.setDefaultBaseURL(
     "https://api.example.com"
);

try {

     // Restore a previous session, if one exists
     Http.loadCookies();

}
catch (IOException e) {

     // No saved session yet, so sign in
     Http.post(
          "/login",
          RequestBody.json(credentials)
     );

     Http.saveCookies();

}
```

---

## Shared HTTP Client

All requests, synchronous and asynchronous, are sent through one shared `HttpClient` with a single shared cookie manager. Because of this:

* Cookies are shared across every request made through `Http`.
* The default base URL is global to the application, not to a single request.
* Setting the default base URL again replaces the previous value for all later requests.

---

## URL Utility Methods

`Http` exposes two helper methods used internally to build request URLs. They can also be called directly.

### resolveUrl

Resolves a URL against the default base URL:

```java
Http.setDefaultBaseURL(
     "https://api.example.com"
);

Http.resolveUrl("/users");
// https://api.example.com/users

Http.resolveUrl("https://api.github.com/users/octocat");
// https://api.github.com/users/octocat
```

Rules:

* If no default base URL has been set, the URL is returned unchanged.
* If the URL is absolute, it is returned unchanged.
* Otherwise the URL is appended directly to the default base URL.

The base URL and the relative path are joined by simple concatenation. No slash is added or removed. A relative path should therefore start with `/`, and the base URL should not end with one.

### isAbsoluteUrl

Checks whether a URL is absolute:

```java
Http.isAbsoluteUrl("https://api.example.com/users"); // true
Http.isAbsoluteUrl("http://localhost:8080/users");   // true
Http.isAbsoluteUrl("/users");                        // false
```

A URL is treated as absolute only if it starts with `http://` or `https://`.

---

## Content-Type Behavior

The `Content-Type` header is set automatically only when a `RequestBody` is supplied. It comes from the body's content type.

Requests sent without a body (for example `Http.post("/users")`, `Http.put("/users/1")`, or `Http.patch("/users/1")`) do not add a `Content-Type` header. GET requests never send a body.

---

## Response Bodies

Every response body is read as a `String`, for both synchronous and asynchronous requests. The `Response` object is created from this string body.

---

## Query Parameter Encoding Details

* Parameter names and values are UTF-8 URL-encoded.
* Values are converted using `String.valueOf()`, so a `null` value is sent as the text `null`.
* If the URL already contains a `?`, new parameters are joined with `&`. Otherwise `?` is used.
* If the `params` map is `null` or empty, the URL is used unchanged.
* Parameters are added in the iteration order of the supplied `Map`. `Map.of(...)` does not guarantee a stable order, so use a `LinkedHashMap` when parameter order matters.

```java
Map<String, Object> params = new LinkedHashMap<>();
params.put("page", 1);
params.put("limit", 10);

Http.get("/users", params);
// /users?page=1&limit=10
```

---

## Null Headers

Passing `null` as the `Headers` argument is allowed. No custom headers are applied and the request is sent normally.

---

## Async Requests and Cookies

Asynchronous requests use the same shared cookie manager as synchronous requests. Cookies stored by a synchronous login request are sent on later asynchronous requests, and the reverse also applies.