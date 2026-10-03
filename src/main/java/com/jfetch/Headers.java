package com.jfetch;

import java.util.Map;

/**
Represents HTTP request headers.

Headers can be created using key-value pairs and applied
to an HTTP request.
*/
public class Headers {
     private final Map<String, String> headers;

     private Headers(Map<String, String> headers) {
          this.headers = headers;
     }

     /**
     Creates headers from key-value pairs.

     @param values alternating header names and values
     @return the created headers
     @throws IllegalArgumentException if an odd number of values is provided
     */
     public static Headers of(String... values) {
          if (values.length % 2 != 0) {
               throw new IllegalArgumentException("Headers must contain pairs of names and values");
          }

          Map<String, String> headers = new java.util.HashMap<>();

          for (int i = 0; i < values.length; i += 2) {
               headers.put(values[i], values[i + 1]);
          }

          return new Headers(headers);
     }

     /**
     Returns all headers.

     @return the HTTP headers
     */
     public Map<String, String> values() {
          return Map.copyOf(headers);
     }
}