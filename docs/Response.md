## Response

`Response` represents the response returned by an HTTP request.

It provides access to the HTTP status, response body, headers, and JSON parsing methods.

### Status Code

Get the HTTP status code using:

```java
response.statusCode();
```

For example:

```java
Response response = Http.get(
        "https://api.example.com/users"
);

System.out.println(response.statusCode());
```

### Response Body

Get the response body as plain text using:

```java
response.text();
```

For example:

```java
String body = response.text();

System.out.println(body);
```

### JSON Response

JFetch can parse a JSON response into a Jackson `JsonNode` using:

```java
response.json();
```

For example:

```java
Response response = Http.get(
        "https://pokeapi.co/api/v2/pokemon/yveltal"
);

JsonNode pokemon = response.json();

System.out.println(pokemon.get("name").asText());
```

### Mapping JSON to a Java Class

JSON responses can also be mapped directly to a Java class using:

```java
response.json(User.class);
```

For example:

```java
public record User(
     String name,
     String email
) {
}
```

The response can then be mapped using:

```java
User user = response.json(User.class);

System.out.println(user.name());
```

JFetch uses Jackson for JSON deserialization.

If the response contains fields that are not present in the Java class, Jackson's normal deserialization rules apply. To ignore unknown fields, Jackson annotations such as `@JsonIgnoreProperties(ignoreUnknown = true)` can be used on the model class.

### Response Headers

Get all response headers using:

```java
response.headers();
```

For a specific header, use:

```java
response.header("Content-Type");
```

`header()` returns the first value of the specified header, or `null` if the header does not exist.

For example:

```java
String contentType = response.header("Content-Type");

System.out.println(contentType);
```

### Status Helpers

`Response` provides convenience methods for checking common HTTP status categories.

#### Successful Response

```java
response.isSuccess();
```

Returns `true` for status codes from `200` through `299`.

#### Client Error

```java
response.isClientError();
```

Returns `true` for status codes from `400` through `499`.

#### Server Error

```java
response.isServerError();
```

Returns `true` for status codes from `500` through `599`.

For example:

```java
if (response.isSuccess()) {
     System.out.println("Request succeeded");
}

if (response.isClientError()) {
     System.out.println("Client error");
}

if (response.isServerError()) {
     System.out.println("Server error");
}
```

### Complete Example

```java
Response response = Http.get(
        "https://pokeapi.co/api/v2/pokemon/yveltal"
);

System.out.println("Status: " + response.statusCode());
System.out.println("Content-Type: " + response.header("Content-Type"));

if (response.isSuccess()) {
     System.out.println(response.json().get("name").asText());
}
```

### JSON Parsing Errors

If the response body cannot be parsed as JSON, `json()` throws an `IllegalArgumentException`.

Similarly, `json(Class<T>)` throws an `IllegalArgumentException` if the response cannot be mapped to the specified Java class.

This keeps Jackson's checked `JsonProcessingException` from being exposed directly through the JFetch API.
