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

public class Http {
     // Main client sending requests
     private static final HttpClient CLIENT = HttpClient.newHttpClient();

     // GET request
     public Response get(String url) throws IOException, InterruptedException {
          HttpRequest request = HttpRequest.newBuilder()
                                   .uri(URI.create(url))
                                   .GET()
                                   .build();


          HttpResponse<String> response = CLIENT.send(
                                             request,
                                             HttpResponse.BodyHandlers.ofString()  
                                          );


          return new Response(response);
     }

     // Get requests WITH url params
     public Response get(String url, Map<String, ?> params) throws IOException, InterruptedException {
          URI uri = buildUri(url, params);

          HttpRequest request = HttpRequest.newBuilder()
                                   .uri(uri)
                                   .GET()
                                   .build();


          HttpResponse<String> response = CLIENT.send(
                                             request,
                                             HttpResponse.BodyHandlers.ofString()  
                                          );


          return new Response(response);
     }

     // Builds uri
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
