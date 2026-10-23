## SessionStorage

`SessionStorage` manages persistent storage of HTTP session cookies in JFetch.

It allows an application to:

* Save cookies to a file

* Restore cookies when the application starts again

* Clear cookies from memory

* Delete the saved cookie file

* Check whether a saved cookie file exists

* Configure where cookies are stored

`SessionStorage` works with the same cookie store used by [`Http`](#http), so any cookie received by an `Http` request can be saved, and any restored cookie is sent on later `Http` requests.

Cookies are stored as JSON using Jackson.

---

## Relationship with Http

Most applications do not need to call `SessionStorage` directly. `Http` provides cookie methods that delegate to it:

| `Http` method                  | `SessionStorage` method           |
| ------------------------------ | --------------------------------- |
| `Http.saveCookies()`           | `SessionStorage.save()`           |
| `Http.saveCookies(Path)`       | `SessionStorage.save(Path)`       |
| `Http.loadCookies()`           | `SessionStorage.load()`           |
| `Http.loadCookies(Path)`       | `SessionStorage.load(Path)`       |
| `Http.clearCookies()`          | `SessionStorage.clear()`          |
| `Http.clearSavedCookies()`     | `SessionStorage.clearSaved()`     |

`SessionStorage` also provides methods that `Http` does not expose, such as `exists()`, `getDefaultPath()`, and `clear(Path)`.

---

## Basic Usage

Save cookies after signing in:

```java
Http.post(
     "/login",
     RequestBody.json(credentials)
);

SessionStorage.save();
```

Restore them the next time the application starts:

```java
SessionStorage.load();

Http.get("/profile");
```

Sign out and forget the saved session:

```java
SessionStorage.clear();
SessionStorage.clearSaved();
```

---

## Storage Location

Cookies are saved to a single JSON file. The location is chosen using the following order of priority:

```text
1. JVM system property        jfetch.session.path
        ↓
2. application.properties     jfetch.session.path
        ↓
3. jfetch.properties          jfetch.session.path
        ↓
4. Platform default location
```

The first source that provides a non-blank value is used.

### Getting the Path

The path JFetch will use can be retrieved with:

```java
Path path = SessionStorage.getDefaultPath();
```

The returned path is the configured path (made absolute) or the platform default.

### Configuring the Path

#### JVM System Property

```text
-Djfetch.session.path=/path/to/session-cookies.json
```

or in code, before any cookie method is called:

```java
System.setProperty(
     "jfetch.session.path",
     "/path/to/session-cookies.json"
);
```

The system property has the highest priority.

#### Properties Files

JFetch reads `application.properties` and then `jfetch.properties` from the classpath:

```properties
jfetch.session.path=${user.home}/.myapp/session-cookies.json
```

If `application.properties` defines a non-blank value, it is used and `jfetch.properties` is not consulted.

#### The `${user.home}` Placeholder

A configured path can contain `${user.home}`, which is replaced with the user's home directory. This keeps the configuration portable across operating systems:

```properties
jfetch.session.path=${user.home}/.myapp/session.json
```

Only `${user.home}` is replaced. Other placeholders are not supported.

### Platform Default Locations

When no path is configured, JFetch uses a default location that depends on the operating system:

| Platform | Default location                                                     |
| -------- | -------------------------------------------------------------------- |
| Windows  | `%LOCALAPPDATA%\JFetch\session-cookies.json`                         |
| macOS    | `~/Library/Application Support/JFetch/session-cookies.json`          |
| Linux    | `$XDG_DATA_HOME/jfetch/session-cookies.json`                         |
| Linux    | `~/.local/share/jfetch/session-cookies.json` (if `XDG_DATA_HOME` is not set) |

If the operating system is Windows but `LOCALAPPDATA` is not set, JFetch falls back to the Linux/Unix-style location under the user's home directory.

---

## Saving Cookies

### Save to the Default Location

```java
SessionStorage.save();
```

### Save to a Custom File

```java
SessionStorage.save(
     Path.of("session.json")
);
```

When saving:

* Every cookie in the shared cookie store is written to the file.

* Missing parent directories are created automatically.

* The file is written as pretty-printed JSON.

* An existing file at the same path is overwritten.

Both methods throw `IOException` if the cookies cannot be written.

Passing `null` as the path throws `IllegalArgumentException`.

### Expiration Handling

JFetch stores the expiration of each cookie as an absolute timestamp (`expiresAt`, in milliseconds since the epoch):

| Cookie                         | Saved `expiresAt`                         |
| ------------------------------ | ----------------------------------------- |
| Has a positive `Max-Age`       | Current time + `Max-Age`                  |
| No `Max-Age` (session cookie)  | `0`                                       |

A value of `0` means the cookie has no expiration time and is treated as a session cookie when it is restored.

---

## Loading Cookies

### Load from the Default Location

```java
SessionStorage.load();
```

### Load from a Custom File

```java
SessionStorage.load(
     Path.of("session.json")
);
```

When loading:

* Cookies are added to the shared cookie store used by `Http`.

* Cookies already in memory are kept. Loading adds to the store and does not clear it first.

* A cookie with an expiration time in the past is skipped.

* A cookie that has a future expiration time keeps only its remaining lifetime (at least one second).

* A cookie without an expiration time is restored as a session cookie.

* The cookie's attributes (domain, path, secure, HTTP-only, port list, comment, comment URL, discard, and version) are restored.

Passing `null` as the path throws `IllegalArgumentException`.

### Missing Files

If the file does not exist, `load()` returns normally without doing anything. A missing file simply means there is no previously saved session to restore, so no exception is thrown.

This means loading is safe to call on every start:

```java
SessionStorage.load();
```

To find out whether a session was actually saved, check first:

```java
if (SessionStorage.exists()) {

     SessionStorage.load();

}
else {

     // Sign in
}
```

### Invalid Entries

Individual entries that cannot be restored are skipped instead of causing the whole load to fail. An entry is skipped when:

* It is missing its URI, name, or value

* Its URI cannot be parsed

* Its URI is not an absolute `http` or `https` URI

* It has already expired

### Unreadable Files

An `IOException` is thrown if the file exists but cannot be read, or if its content is not valid JSON in the expected format.

---

## Clearing Cookies

There are two different things that can be cleared, and they are independent.

### Clear Cookies from Memory

```java
SessionStorage.clear();
```

Removes every cookie from the shared cookie store. The saved file is not touched.

This only clears cookies on the client. It does not invalidate the session on the server.

### Delete the Saved File

Delete the file at the default location:

```java
SessionStorage.clearSaved();
```

Delete the file at a custom location:

```java
SessionStorage.clear(
     Path.of("session.json")
);
```

If the file does not exist, nothing happens and no exception is thrown. Cookies in memory are not touched.

Passing `null` as the path throws `IllegalArgumentException`. Deletion throws `IOException` if the file exists but cannot be deleted.

### Forget the Session Completely

```java
SessionStorage.clear();
SessionStorage.clearSaved();
```

---

## Checking for a Saved Session

Check the default location:

```java
boolean saved = SessionStorage.exists();
```

Check a custom file:

```java
boolean saved = SessionStorage.exists(
     Path.of("session.json")
);
```

`exists()` returns `true` only if the path points to a regular file. It returns `false` for a missing path, a directory, or a `null` path.

---

## Saved File Format

Cookies are saved as a JSON array. Each element describes one cookie and the URI it was received from:

```json
[
  {
    "uri": "https://api.example.com/login",
    "name": "SESSION",
    "value": "abc123",
    "comment": null,
    "commentURL": null,
    "discard": false,
    "domain": "api.example.com",
    "path": "/",
    "portlist": null,
    "secure": true,
    "httpOnly": true,
    "version": 0,
    "expiresAt": 1798000000000
  }
]
```

| Field        | Description                                                    |
| ------------ | -------------------------------------------------------------- |
| `uri`        | The URI associated with the cookie in the cookie store         |
| `name`       | The cookie name                                                |
| `value`      | The cookie value                                               |
| `comment`    | The cookie comment, if any                                     |
| `commentURL` | The cookie comment URL, if any                                 |
| `discard`    | Whether the cookie should be discarded at the end of a session |
| `domain`     | The domain the cookie applies to                               |
| `path`       | The path the cookie applies to                                 |
| `portlist`   | The port list, if any                                          |
| `secure`     | Whether the cookie is only sent over HTTPS                     |
| `httpOnly`   | Whether the cookie is flagged HTTP-only                        |
| `version`    | The cookie specification version                               |
| `expiresAt`  | Absolute expiry in milliseconds since the epoch, or `0` for a session cookie |

The file format is an implementation detail and may change between versions of JFetch.

---

## Method Summary

| Method                    | Description                                            | Throws                                         |
| ------------------------- | ------------------------------------------------------ | ---------------------------------------------- |
| `getDefaultPath()`        | Returns the configured or platform default path        | `IllegalStateException` (properties unreadable) |
| `save()`                  | Saves cookies to the default path                      | `IOException`                                  |
| `save(Path)`              | Saves cookies to a file                                | `IOException`, `IllegalArgumentException`      |
| `load()`                  | Loads cookies from the default path                    | `IOException`                                  |
| `load(Path)`              | Loads cookies from a file                              | `IOException`, `IllegalArgumentException`      |
| `clear()`                 | Clears cookies from memory                             | None                                           |
| `clear(Path)`             | Deletes a saved cookie file                            | `IOException`, `IllegalArgumentException`      |
| `clearSaved()`            | Deletes the saved cookie file at the default path      | `IOException`                                  |
| `exists()`                | Checks whether the default cookie file exists          | None                                           |
| `exists(Path)`            | Checks whether a cookie file exists                    | None                                           |

---

## Error Handling

| Situation                                        | Result                                         |
| ------------------------------------------------ | ---------------------------------------------- |
| `null` path passed to `save`, `load`, or `clear` | `IllegalArgumentException`                     |
| File cannot be written or deleted                | `IOException`                                  |
| File cannot be read or contains invalid JSON     | `IOException`                                  |
| File to load does not exist                      | Nothing happens (no exception)                 |
| File to delete does not exist                    | Nothing happens (no exception)                 |
| A properties file exists but cannot be read      | `IllegalStateException`                        |
| One cookie entry is invalid or expired           | That entry is skipped                          |

Because the configured path is resolved by `getDefaultPath()`, an unreadable `application.properties` or `jfetch.properties` file causes `IllegalStateException` from any method that uses the default path (`save()`, `load()`, `clearSaved()`, and `exists()`).

---

## Security Considerations

The saved file contains session cookies in plain text. Anyone who can read the file may be able to use the saved session.

* Keep the file in a location only the current user can read, which the platform default locations already are.

* Do not commit the file to version control.

* Delete the file when the user signs out by calling `SessionStorage.clearSaved()`.

* Remember that `clear()` and `clearSaved()` only remove cookies on the client. They do not sign the user out on the server.

---

## Complete Example

The following example restores a saved session when the application starts, signs in only when needed, and removes the session when the user signs out:

```java
public class App {

     public static void main(String[] args) throws Exception {

          Http.setDefaultBaseURL(
               "https://api.example.com"
          );

          if (SessionStorage.exists()) {

               SessionStorage.load();

          }
          else {

               Response response = Http.post(
                    "/login",
                    RequestBody.json(credentials)
               );

               if (response.isSuccess()) {

                    SessionStorage.save();

               }
          }

          Response profile = Http.get("/profile");

          System.out.println(profile.text());
     }

     static void signOut() throws IOException {

          Http.post("/logout");

          SessionStorage.clear();
          SessionStorage.clearSaved();

     }
}
```

With a custom storage location:

```java
Path path = Path.of(
     System.getProperty("user.home"),
     ".myapp",
     "session.json"
);

SessionStorage.save(path);

SessionStorage.load(path);

SessionStorage.clear(path);
```

`SessionStorage` stores and restores the cookies used by [`Http`](#http). For making requests, see [`Http`](#http).