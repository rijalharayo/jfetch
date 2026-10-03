package com.jfetch;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.http.HttpRequest;

/**
    Represents the body of an HTTP request.
     <p>Request bodies can be created from Java objects and serialized
    into formats suitable for sending with an HTTP request.</p>
*/
public class RequestBody {
     private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

     private final String content;
     private final String contentType;

     private RequestBody(String content, String contentType) {
          this.content = content;
          this.contentType = contentType;
     }

     /**
        Creates a JSON request body from a Java object.
       
        <p>The object is serialized using Jackson's {@link ObjectMapper}.
        The object can be any type that Jackson can serialize.</p>
       
        @param object the object to serialize as JSON
        @return a request body containing the serialized JSON
        @throws IllegalArgumentException if the object cannot be serialized
     */
     public static RequestBody json(Object object) {
          try {
               String json = OBJECT_MAPPER.writeValueAsString(object);

               return new RequestBody(json, "application/json");
          } 
          catch (JsonProcessingException e) {
               throw new IllegalArgumentException(
                       "Failed to serialize request body as JSON", e
               );
          }
     }

     /**
       Creates a Java HTTP body publisher from this request body.
      
       @return a body publisher containing this request body's content
     */
     public HttpRequest.BodyPublisher publisher() {
          return HttpRequest.BodyPublishers.ofString(content);
     }

     /**
       Returns the content type of this request body.
      
       @return the MIME type of the request body
     */
     public String contentType() {
          return contentType;
     }

     /**
       Returns the serialized request body.
      
       @return the request body content
     */
     public String content() {
          return content;
     }
}