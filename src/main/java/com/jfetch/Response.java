package com.jfetch;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.http.HttpHeaders;
import java.net.http.HttpResponse;

/*
     Represents the response returned by an HTTP request.

     Provides access to the response status, body, headers,
     and methods for parsing JSON responses.
*/
public class Response {
     private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

     private final int statusCode;
     private final String body;
     private final HttpHeaders headers;

     /**
     Creates a Response from Java's HTTP response object.

     @param response the HTTP response returned by Java's HttpClient
     */
     public Response(HttpResponse<String> response) {
          this.statusCode = response.statusCode();
          this.body = response.body();
          this.headers = response.headers();
     }

     /**
     Returns the HTTP status code of the response.

     @return the HTTP status code
     */
     public int statusCode() {
          return statusCode;
     }

     /**
     Returns the response body as plain text.

     @return the response body as a String
     */
     public String text() {
          return body;
     }

     /**
     Parses the response body as JSON.

     @return the response body as a Jackson JsonNode
     @throws IllegalArgumentException if the response body is not valid JSON
     */
     public JsonNode json() {
          try {
               return OBJECT_MAPPER.readTree(body);
          } 
          catch (JsonProcessingException e) {
               throw new IllegalArgumentException(
                       "Failed to parse response body as JSON", e
               );
          }
     }

     /**
     Parses the response body as JSON and maps it to a Java class.

     @param type the class to map the JSON response to
     @param <T> the type of the returned object
     @return the response body mapped to the specified type
     @throws IllegalArgumentException if the response body cannot be mapped
     */
     public <T> T json(Class<T> type) {
          try {
               return OBJECT_MAPPER.readValue(body, type);
          } 
          catch (JsonProcessingException e) {
               throw new IllegalArgumentException(
                       "Failed to map response body to " + type.getName(), e
               );
          }
     }

     /**
     Returns all HTTP headers included in the response.

     @return the response headers
     */
     public HttpHeaders headers() {
          return headers;
     }

     /**
     Returns the first value of the specified response header.

     @param name the name of the header to retrieve
     @return the first header value, or null if the header does not exist
     */
     public String header(String name) {
          return headers.firstValue(name).orElse(null);
     }

     /**
     Determines whether the response has a successful HTTP status.

     @return true if the status code is between 200 and 299
     */
     public boolean isSuccess() {
          return statusCode >= 200 && statusCode < 300;
     }

     /**
     Determines whether the response indicates a client error.

     @return true if the status code is between 400 and 499
     */
     public boolean isClientError() {
          return statusCode >= 400 && statusCode < 500;
     }

     /**
     Determines whether the response indicates a server error.

     @return true if the status code is between 500 and 599
     */
     public boolean isServerError() {
          return statusCode >= 500 && statusCode < 600;
     }
}