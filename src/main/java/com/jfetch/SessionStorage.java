
package com.jfetch;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.CookieManager;
import java.net.HttpCookie;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

/**
 Manages persistent storage of HTTP session cookies.

 Supports saving and restoring cookies between application launches,
 with configurable storage and a platform-specific default location.
 */
public final class SessionStorage {

     private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

     private SessionStorage() {}

     /**
      Returns the configured cookie storage path or the default path.

      The system property takes priority over application.properties
      and jfetch.properties.

      @return the cookie storage path
      */
     public static Path getDefaultPath() {
          String configuredPath = System.getProperty("jfetch.session.path");

          // Uses the properties files if no JVM system property was provided.
          if(configuredPath == null || configuredPath.isBlank()) {
               configuredPath = getConfiguredPath();
          }

          if(configuredPath != null && !configuredPath.isBlank()) {
               // Resolves ${user.home} so the path can be configured portably.
               return Paths.get(
                       configuredPath.replace(
                               "${user.home}",
                               System.getProperty("user.home")
                       )
               ).toAbsolutePath();
          }

          // Selects a default location based on the operating system.
          String os = System.getProperty("os.name", "").toLowerCase();

          if(os.contains("win")) {
               String localAppData = System.getenv("LOCALAPPDATA");

               if (localAppData != null && !localAppData.isBlank()) {
                    return Paths.get(
                         localAppData, "JFetch", "session-cookies.json"
                    );
               }
          }

          if(os.contains("mac")) {
               return Paths.get(
                    System.getProperty("user.home"),
                    "Library", "Application Support",
                    "JFetch", "session-cookies.json"
               );
          }

          // Linux and other Unix-like systems can override this with XDG_DATA_HOME.
          String dataHome = System.getenv("XDG_DATA_HOME");

          if(dataHome != null && !dataHome.isBlank()) {
               return Paths.get(
                    dataHome, "jfetch", "session-cookies.json"
               );
          }

          return Paths.get(
               System.getProperty("user.home"),
               ".local", "share", "jfetch", "session-cookies.json"
          );
     }

     /**
      Saves the current HTTP cookies to the specified file.

      @param path the file where cookies will be stored
      @throws IOException if the cookies cannot be saved
      */
     public static void save(Path path) throws IOException {
          if(path == null) {
               throw new IllegalArgumentException("Cookie path cannot be null");
          }

          CookieManager manager = Http.cookieManager();
          List<StoredCookie> storedCookies = new ArrayList<>();

          // Each cookie is saved with the URI associated with it in the cookie store.
          for(URI uri : manager.getCookieStore().getURIs()) {
               for(HttpCookie cookie : manager.getCookieStore().get(uri)) {
                    StoredCookie stored = new StoredCookie();

                    stored.uri = uri.toString();
                    stored.name = cookie.getName();
                    stored.value = cookie.getValue();
                    stored.comment = cookie.getComment();
                    stored.commentURL = cookie.getCommentURL();
                    stored.discard = cookie.getDiscard();
                    stored.domain = cookie.getDomain();
                    stored.path = cookie.getPath();
                    stored.portlist = cookie.getPortlist();
                    stored.secure = cookie.getSecure();
                    stored.httpOnly = cookie.isHttpOnly();
                    stored.version = cookie.getVersion();

                    // Converts the remaining cookie lifetime to an absolute expiration time.
                    stored.expiresAt = cookie.getMaxAge() > 0
                            ? System.currentTimeMillis()
                              + cookie.getMaxAge() * 1000L
                            : 0L;

                    storedCookies.add(stored);
               }
          }

          // Creates the parent directory if it doesn't exist yet.
          Path absolutePath = path.toAbsolutePath();
          Path parent = absolutePath.getParent();

          if (parent != null) {
               Files.createDirectories(parent);
          }

          // Serializes the cookies as readable JSON.
          OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValue(absolutePath.toFile(), storedCookies);
     }

     /**
      Saves cookies to the configured or default storage file.

      @throws IOException if the cookies cannot be saved
      */
     public static void save() throws IOException {
          save(getDefaultPath());
     }

     /**
      Loads cookies from the specified file into the HTTP cookie store.

      @param path the file containing saved cookies
      @throws IOException if the cookies cannot be loaded
      */
     public static void load(Path path) throws IOException {
          if(path == null) {
               throw new IllegalArgumentException("Cookie path cannot be null");
          }

          // A missing file means there is no previously saved session to restore.
          if(!Files.isRegularFile(path)) {
               return;
          }

          List<StoredCookie> storedCookies = OBJECT_MAPPER.readValue(
               path.toFile(),
               new TypeReference<List<StoredCookie>>() {}
          );

          CookieManager manager = Http.cookieManager();

          for(StoredCookie stored : storedCookies) {
               // Skips incomplete entries instead of trying to restore invalid cookies.
               if (stored.uri == null || stored.name == null
                       || stored.value == null) {
                    continue;
               }

               URI uri;

               try {
                    uri = URI.create(stored.uri);

                    // Only accepts HTTP and HTTPS origins for restored cookies.
                    if(!uri.isAbsolute()
                            || (!"http".equalsIgnoreCase(uri.getScheme())
                            && !"https".equalsIgnoreCase(uri.getScheme()))) {
                         continue;
                    }
               } 
               catch (IllegalArgumentException e) {
                    continue;
               }

               // Skips cookies whose saved expiration time has already passed.
               if(stored.expiresAt > 0
                       && stored.expiresAt <= System.currentTimeMillis()) {
                    continue;
               }

               HttpCookie cookie = new HttpCookie(
                       stored.name, stored.value
               );

               // Restores the cookie attributes used for matching and transmission.
               cookie.setComment(stored.comment);
               cookie.setCommentURL(stored.commentURL);
               cookie.setDiscard(stored.discard);
               cookie.setDomain(stored.domain);
               cookie.setPath(stored.path);
               cookie.setPortlist(stored.portlist);
               cookie.setSecure(stored.secure);
               cookie.setHttpOnly(stored.httpOnly);
               cookie.setVersion(stored.version);

               if(stored.expiresAt > 0) {
                    // Restores the remaining lifetime, not the original lifetime.
                    long remainingSeconds = Math.max(
                            1L,
                            (stored.expiresAt - System.currentTimeMillis())
                                    / 1000L
                    );

                    cookie.setMaxAge(remainingSeconds);
               }
               else {
                    // A non-positive Max-Age represents a session cookie here.
                    cookie.setMaxAge(-1);
               }

               // Adds valid cookies back into the same store used by Http.
               if(!cookie.hasExpired()) {
                    manager.getCookieStore().add(uri, cookie);
               }
          }
     }

     /**
      Loads cookies from the configured or default storage file.

      @throws IOException if the cookies cannot be loaded
      */
     public static void load() throws IOException {
          load(getDefaultPath());
     }

     /**
      Clears all cookies from the HTTP cookie store.

      This does not invalidate sessions on the server.
      */
     public static void clear() {
          Http.cookieManager().getCookieStore().removeAll();
     }

     /**
      Deletes the saved cookie file at the specified path.

      @param path the file to delete
      @throws IOException if the file cannot be deleted
      */
     public static void clear(Path path) throws IOException {
          if(path == null) {
               throw new IllegalArgumentException("Cookie path cannot be null");
          }

          // Removes the file without failing if it doesn't exist.
          Files.deleteIfExists(path);
     }

     /**
      Deletes the saved cookie file at the configured or default path.

      @throws IOException if the file cannot be deleted
      */
     public static void clearSaved() throws IOException {
          clear(getDefaultPath());
     }

     /**
      Determines whether a saved cookie file exists.

      @param path the file to check
      @return true if the file exists
      */
     public static boolean exists(Path path) {
          return path != null && Files.isRegularFile(path);
     }

     /**
      Determines whether the default cookie file exists.

      @return true if the file exists
      */
     public static boolean exists() {
          return exists(getDefaultPath());
     }

     // Reads the configured path from application.properties or jfetch.properties.
     private static String getConfiguredPath() {
          Properties properties = new Properties();

          for (String resource : new String[] {"application.properties", "jfetch.properties"}) {
               try(var input = SessionStorage.class.getClassLoader().getResourceAsStream(resource)) {

                    if(input != null) {
                         properties.load(input);

                         String path = properties.getProperty(
                                 "jfetch.session.path"
                         );

                         if(path != null && !path.isBlank()) {
                              return path.trim();
                         }
                    }
               }
               catch(IOException e) {
                    throw new IllegalStateException(
                         "Failed to read " + resource, e
                    );
               }
          }

          return null;
     }

     @JsonAutoDetect(
          fieldVisibility = JsonAutoDetect.Visibility.ANY,
          getterVisibility = JsonAutoDetect.Visibility.NONE,
          isGetterVisibility = JsonAutoDetect.Visibility.NONE,
          setterVisibility = JsonAutoDetect.Visibility.NONE
     )
     private static final class StoredCookie {
          public String uri;
          public String name;
          public String value;
          public String comment;
          public String commentURL;
          public boolean discard;
          public String domain;
          public String path;
          public String portlist;
          public boolean secure;
          public boolean httpOnly;
          public int version;
          public long expiresAt;
     }
}
