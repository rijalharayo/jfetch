## Headers

`Headers` represents custom HTTP request headers.

It provides a simple way to create and pass headers to JFetch HTTP requests.

### Creating Headers

Use `Headers.of()` to create a collection of request headers:

```java
Headers headers = Headers.of(
     "Authorization", "Bearer token",
     "Accept", "application/json"
);
```

The arguments are provided as alternating header names and values.

### Using Headers

Headers can be passed to HTTP request methods:

```java
Response response = Http.get(
        "https://api.example.com/users",
        headers
);
```

Headers can also be combined with query parameters:

```java
Map<String, Object> params = Map.of(
     "page", 1,
     "limit", 10
);

Response response = Http.get(
        "https://api.example.com/users",
        params,
        headers
);
```

They can also be used with request bodies:

```java
Response response = Http.post(
        "https://api.example.com/users",
        RequestBody.json(user),
        headers
);
```

### Multiple Headers

Multiple headers can be specified in a single `Headers.of()` call:

```java
Headers headers = Headers.of(
     "Authorization", "Bearer token",
     "Accept", "application/json",
     "X-Client", "JFetch"
);
```

### Invalid Header Arguments

`Headers.of()` expects header names and values in pairs.

Providing an odd number of arguments throws an `IllegalArgumentException`:

```java
Headers headers = Headers.of(
     "Authorization",
     "Bearer token",
     "Accept"
);
```

This is invalid because `"Accept"` does not have a corresponding header value.

### Accessing Header Values

The complete collection of headers can be accessed using:

```java
headers.values();
```

This returns a `Map<String, String>` containing the header names and values.

The returned map is a copy of the internal headers, so modifying it does not modify the `Headers` object.
