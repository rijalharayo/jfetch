package com.jfetch;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.stream.Collectors;
/*
        Provides static methods for sending HTTP requests.

        Supports GET, POST, PUT, PATCH, and DELETE requests,
        with optional URL query parameters and request bodies.
*/
public class Http {

     private static final HttpClient CLIENT = HttpClient.newHttpClient();

     /**
     Sends a GET request to the specified URL.

     @param url the URL to send the request to
     @return the server response
     @throws IOException if an I/O error occurs
     @throws InterruptedException if the request is interrupted
     */
     public static Response get(String url) throws IOException, InterruptedException {
          return send(
                  HttpRequest.newBuilder()
                          .uri(URI.create(url))
                          .GET()
                          .build()
          );
     }

     /**
     Sends a GET request with URL query parameters.

     @param url the URL to send the request to
     @param params the query parameters to append to the URL
     @return the server response
     @throws IOException if an I/O error occurs
     @throws InterruptedException if the request is interrupted
     */
     public static Response get(String url, Map<String, ?> params) throws IOException, InterruptedException {
          return send(
                  HttpRequest.newBuilder()
                          .uri(buildUri(url, params))
                          .GET()
                          .build()
          );
     }

     /**
     Sends a POST request with a request body.

     @param url the URL to send the request to
     @param body the request body to send
     @return the server response
     @throws IOException if an I/O error occurs
     @throws InterruptedException if the request is interrupted
     */
     public static Response post(String url, RequestBody body) throws IOException, InterruptedException {
          return send(
                  HttpRequest.newBuilder()
                          .uri(URI.create(url))
                          .header("Content-Type", body.contentType())
                          .POST(body.publisher())
                          .build()
          );
     }

     /**
     Sends a POST request with URL query parameters and a request body.

     @param url the URL to send the request to
     @param params the query parameters to append to the URL
     @param body the request body to send
     @return the server response
     @throws IOException if an I/O error occurs
     @throws InterruptedException if the request is interrupted
     */
     public static Response post(String url, Map<String, ?> params, RequestBody body) throws IOException, InterruptedException {
          return send(
                  HttpRequest.newBuilder()
                          .uri(buildUri(url, params))
                          .header("Content-Type", body.contentType())
                          .POST(body.publisher())
                          .build()
          );
     }

     /**
     Sends a POST request without a request body.

     @param url the URL to send the request to
     @return the server response
     @throws IOException if an I/O error occurs
     @throws InterruptedException if the request is interrupted
     */
     public static Response post(String url) throws IOException, InterruptedException {
          return send(
                  HttpRequest.newBuilder()
                          .uri(URI.create(url))
                          .POST(HttpRequest.BodyPublishers.noBody())
                          .build()
          );
     }

     /**
     Sends a POST request with URL query parameters but without a request body.

     @param url the URL to send the request to
     @param params the query parameters to append to the URL
     @return the server response
     @throws IOException if an I/O error occurs
     @throws InterruptedException if the request is interrupted
     */
     public static Response post(String url, Map<String, ?> params) throws IOException, InterruptedException {
          return send(
                  HttpRequest.newBuilder()
                          .uri(buildUri(url, params))
                          .POST(HttpRequest.BodyPublishers.noBody())
                          .build()
          );
     }

     /**
     Sends a PUT request with a request body.

     @param url the URL to send the request to
     @param body the request body to send
     @return the server response
     @throws IOException if an I/O error occurs
     @throws InterruptedException if the request is interrupted
     */
     public static Response put(String url, RequestBody body) throws IOException, InterruptedException {
          return send(
                  HttpRequest.newBuilder()
                          .uri(URI.create(url))
                          .header("Content-Type", body.contentType())
                          .PUT(body.publisher())
                          .build()
          );
     }

     /**
     Sends a PUT request with URL query parameters and a request body.

     @param url the URL to send the request to
     @param params the query parameters to append to the URL
     @param body the request body to send
     @return the server response
     @throws IOException if an I/O error occurs
     @throws InterruptedException if the request is interrupted
     */
     public static Response put(String url, Map<String, ?> params, RequestBody body) throws IOException, InterruptedException {
          return send(
                  HttpRequest.newBuilder()
                          .uri(buildUri(url, params))
                          .header("Content-Type", body.contentType())
                          .PUT(body.publisher())
                          .build()
          );
     }

     /**
     Sends a PUT request without a request body.

     @param url the URL to send the request to
     @return the server response
     @throws IOException if an I/O error occurs
     @throws InterruptedException if the request is interrupted
     */
     public static Response put(String url) throws IOException, InterruptedException {
          return send(
                  HttpRequest.newBuilder()
                          .uri(URI.create(url))
                          .PUT(HttpRequest.BodyPublishers.noBody())
                          .build()
          );
     }

     /**
     Sends a PUT request with URL query parameters but without a request body.

     @param url the URL to send the request to
     @param params the query parameters to append to the URL
     @return the server response
     @throws IOException if an I/O error occurs
     @throws InterruptedException if the request is interrupted
     */
     public static Response put(String url, Map<String, ?> params) throws IOException, InterruptedException {
          return send(
                  HttpRequest.newBuilder()
                          .uri(buildUri(url, params))
                          .PUT(HttpRequest.BodyPublishers.noBody())
                          .build()
          );
     }

     /**
     Sends a PATCH request with a request body.

     @param url the URL to send the request to
     @param body the request body to send
     @return the server response
     @throws IOException if an I/O error occurs
     @throws InterruptedException if the request is interrupted
     */
     public static Response patch(String url, RequestBody body) throws IOException, InterruptedException {
          return send(
                  HttpRequest.newBuilder()
                          .uri(URI.create(url))
                          .header("Content-Type", body.contentType())
                          .method("PATCH", body.publisher())
                          .build()
          );
     }

     /**
     Sends a PATCH request with URL query parameters and a request body.

     @param url the URL to send the request to
     @param params the query parameters to append to the URL
     @param body the request body to send
     @return the server response
     @throws IOException if an I/O error occurs
     @throws InterruptedException if the request is interrupted
     */
     public static Response patch(String url, Map<String, ?> params, RequestBody body) throws IOException, InterruptedException {
          return send(
                  HttpRequest.newBuilder()
                          .uri(buildUri(url, params))
                          .header("Content-Type", body.contentType())
                          .method("PATCH", body.publisher())
                          .build()
          );
     }

     /**
     Sends a PATCH request without a request body.

     @param url the URL to send the request to
     @return the server response
     @throws IOException if an I/O error occurs
     @throws InterruptedException if the request is interrupted
     */
     public static Response patch(String url) throws IOException, InterruptedException {
          return send(
                  HttpRequest.newBuilder()
                          .uri(URI.create(url))
                          .method("PATCH", HttpRequest.BodyPublishers.noBody())
                          .build()
          );
     }

     /**
     Sends a PATCH request with URL query parameters but without a request body.

     @param url the URL to send the request to
     @param params the query parameters to append to the URL
     @return the server response
     @throws IOException if an I/O error occurs
     @throws InterruptedException if the request is interrupted
     */
     public static Response patch(String url, Map<String, ?> params) throws IOException, InterruptedException {
          return send(
                  HttpRequest.newBuilder()
                          .uri(buildUri(url, params))
                          .method("PATCH", HttpRequest.BodyPublishers.noBody())
                          .build()
          );
     }

     /**
     Sends a DELETE request.

     @param url the URL to send the request to
     @return the server response
     @throws IOException if an I/O error occurs
     @throws InterruptedException if the request is interrupted
     */
     public static Response delete(String url) throws IOException, InterruptedException {
          return send(
                  HttpRequest.newBuilder()
                          .uri(URI.create(url))
                          .DELETE()
                          .build()
          );
     }

     /**
     Sends a DELETE request with URL query parameters.

     @param url the URL to send the request to
     @param params the query parameters to append to the URL
     @return the server response
     @throws IOException if an I/O error occurs
     @throws InterruptedException if the request is interrupted
     */
     public static Response delete(String url, Map<String, ?> params) throws IOException, InterruptedException {
          return send(
                  HttpRequest.newBuilder()
                          .uri(buildUri(url, params))
                          .DELETE()
                          .build()
          );
     }

     /**
     Sends a DELETE request with a request body.

     @param url the URL to send the request to
     @param body the request body to send
     @return the server response
     @throws IOException if an I/O error occurs
     @throws InterruptedException if the request is interrupted
     */
     public static Response delete(String url, RequestBody body) throws IOException, InterruptedException {
          return send(
                  HttpRequest.newBuilder()
                          .uri(URI.create(url))
                          .header("Content-Type", body.contentType())
                          .method("DELETE", body.publisher())
                          .build()
          );
     }

     /**
     Sends a DELETE request with URL query parameters and a request body.

     @param url the URL to send the request to
     @param params the query parameters to append to the URL
     @param body the request body to send
     @return the server response
     @throws IOException if an I/O error occurs
     @throws InterruptedException if the request is interrupted
     */
     public static Response delete(
             String url,
             Map<String, ?> params,
             RequestBody body
     ) throws IOException, InterruptedException {
          return send(
                  HttpRequest.newBuilder()
                          .uri(buildUri(url, params))
                          .header("Content-Type", body.contentType())
                          .method("DELETE", body.publisher())
                          .build()
          );
     }

     /**
     Sends the prepared request through the shared HTTP client.

     @param request the HTTP request to send
     @return the server response
     @throws IOException if an I/O error occurs
     @throws InterruptedException if the request is interrupted
     */
     private static Response send(HttpRequest request) throws IOException, InterruptedException {
          HttpResponse<String> response = CLIENT.send(
                  request,
                  HttpResponse.BodyHandlers.ofString()
          );

          return new Response(response);
     }

     /**
     Builds a URI by appending encoded query parameters to the URL.

     @param url the base URL
     @param params the query parameters to append
     @return the resulting URI
     */
     private static URI buildUri(String url, Map<String, ?> params) {
          if (params == null || params.isEmpty()) {
               return URI.create(url);
          }

          String query = params.entrySet()
                  .stream()
                  .map(entry ->
                          URLEncoder.encode(
                                  entry.getKey(),
                                  StandardCharsets.UTF_8
                          )
                          + "=" +
                          URLEncoder.encode(
                                  String.valueOf(entry.getValue()),
                                  StandardCharsets.UTF_8
                          )
                  )
                  .collect(Collectors.joining("&"));

          String separator = url.contains("?") ? "&" : "?";

          return URI.create(url + separator + query);
     }
}