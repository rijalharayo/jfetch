package com.jfetch;

import java.net.http.HttpHeaders;
import java.net.http.HttpResponse;

/**
 * Represents the response returned by an HTTP request.
 *
 * <p>A {@code Response} contains the HTTP status code, response body,
 * and response headers returned by the server.</p>
 *
 * <p>This class is created by JFetch after an HTTP request has been
 * completed and is intended to provide a simpler interface over
 * Java's {@link HttpResponse}.</p>
 */
public class Response {

     private final int statusCode;
     private final String body;
     private final HttpHeaders headers;

     /**
      * Creates a {@code Response} from Java's HTTP response object.
      *
      * @param response the HTTP response returned by Java's HttpClient
      */
     public Response(HttpResponse<String> response) {
          this.statusCode = response.statusCode();
          this.body = response.body();
          this.headers = response.headers();
     }

     /**
      * Returns the HTTP status code of the response.
      *
      * <p>Common status codes include:</p>
      * <ul>
      *      <li>{@code 200} - OK</li>
      *      <li>{@code 201} - Created</li>
      *      <li>{@code 204} - No Content</li>
      *      <li>{@code 400} - Bad Request</li>
      *      <li>{@code 401} - Unauthorized</li>
      *      <li>{@code 404} - Not Found</li>
      *      <li>{@code 500} - Internal Server Error</li>
      * </ul>
      *
      * @return the HTTP status code
      */
     public int statusCode() {
          return statusCode;
     }

     /**
      * Returns the response body as plain text.
      *
      * <p>The returned value contains the body exactly as received
      * from the server after it has been decoded by the underlying
      * HTTP client.</p>
      *
      * @return the response body as a String
      */
     public String text() {
          return body;
     }

     /**
      * Returns all HTTP headers included in the response.
      *
      * <p>The returned {@link HttpHeaders} object is read-only.</p>
      *
      * @return the response headers
      */
     public HttpHeaders headers() {
          return headers;
     }

     /**
      * Returns the first value of the specified response header.
      *
      * <p>Header names are case-insensitive according to the HTTP
      * specification.</p>
      *
      * <p>If the response does not contain the requested header,
      * this method returns {@code null}.</p>
      *
      * @param name the name of the header to retrieve
      * @return the first header value, or {@code null} if the header
      *         does not exist
      */
     public String header(String name) {
          return headers.firstValue(name).orElse(null);
     }

     /**
      * Determines whether the response has a successful HTTP status.
      *
      * <p>A response is considered successful when its status code
      * is in the range {@code 200-299}.</p>
      *
      * @return {@code true} if the status code is between 200 and 299
      */
     public boolean isSuccess() {
          return statusCode >= 200 && statusCode < 300;
     }

     /**
      * Determines whether the response indicates a client error.
      *
      * <p>Client errors are represented by status codes in the range
      * {@code 400-499}.</p>
      *
      * @return {@code true} if the status code is between 400 and 499
      */
     public boolean isClientError() {
          return statusCode >= 400 && statusCode < 500;
     }

     /**
      * Determines whether the response indicates a server error.
      *
      * <p>Server errors are represented by status codes in the range
      * {@code 500-599}.</p>
      *
      * @return {@code true} if the status code is between 500 and 599
      */
     public boolean isServerError() {
          return statusCode >= 500 && statusCode < 600;
     }
}