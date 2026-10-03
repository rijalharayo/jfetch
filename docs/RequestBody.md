## RequestBody

`RequestBody` represents the body of an HTTP request.

It provides a simple way to create request bodies from Java objects. JFetch currently supports creating JSON request bodies through `RequestBody.json()`.

### Creating a JSON Body

Use `RequestBody.json()` to serialize a Java object into JSON:

```java
User user = new User(
     "John",
     "john@example.com"
);

RequestBody body = RequestBody.json(user);
```

The object is serialized using Jackson's `ObjectMapper`.

The resulting body can be passed directly to methods such as `Http.post()`, `Http.put()`, and `Http.patch()`:

```java
Response response = Http.post(
        "https://api.example.com/users",
        RequestBody.json(user)
);
```

JFetch automatically sets the `Content-Type` header to:

```text
application/json
```

### Supported Objects

The object passed to `RequestBody.json()` can be any type that Jackson can serialize.

For example, a Java record:

```java
public record User(
     String name,
     String email
) {
}
```

can be converted into:

```java
RequestBody body = RequestBody.json(
     new User(
          "John",
          "john@example.com"
     )
);
```

which produces JSON similar to:

```json
{
  "name": "John",
  "email": "john@example.com"
}
```

### Accessing the Body

The serialized request body can be accessed using:

```java
body.content();
```

The content type can be accessed using:

```java
body.contentType();
```

The underlying Java HTTP body publisher can be accessed using:

```java
body.publisher();
```

These methods are primarily used internally by JFetch when constructing HTTP requests.

### Serialization Errors

If Jackson cannot serialize the supplied object, `RequestBody.json()` throws an `IllegalArgumentException`:

```java
RequestBody body = RequestBody.json(object);
```

This allows serialization failures to be handled without exposing Jackson's checked `JsonProcessingException` directly through the JFetch API.
