package me.dineshsutihar.urlshortner;

import lombok.Data;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import static utils.utils.generateRandomString;

@Data
class UrlRequest {
  private String url;
}

@RestController
public class RestRoutes {

  private final Map<String, String> routes = new HashMap<>();

  @GetMapping("/")
  public String home() {
    return "Welcome to URL Shortner";
  }

  // Accept Long Url and shortened it
  @PostMapping("/shorten")
  public ResponseEntity<String> shortenUrl(@RequestBody UrlRequest urlRequest) {
    System.out.println(urlRequest);
    String originalUrl = urlRequest.getUrl();
    System.out.println(originalUrl);

    if (originalUrl == null || originalUrl.isEmpty()) {
      return ResponseEntity.status(204).body("Error: URL cannot be empty");
    }

    if (routes.containsValue(originalUrl)) {
      return ResponseEntity.status(409).body("Error: URL already exists");
    }

    String shortenedUrl = generateRandomString(originalUrl);

    routes.put(shortenedUrl, originalUrl);

    return ResponseEntity.status(201).body(shortenedUrl);
  }

  // Short Url link to Original Redirection
  @GetMapping("/{shortCode}")
  public ResponseEntity<String> expandUrl(@PathVariable String shortCode) {

    String originalUrl = routes.get(shortCode);

    if (originalUrl == null) {
      return ResponseEntity.status(404).build();
    }

    return ResponseEntity.status(301)
        .location(URI.create(originalUrl))
        .build();

  }

  @DeleteMapping("/{shortCode}")
  public ResponseEntity<String> getStats(@RequestBody String url) {
    String originalUrl = routes.get(url);
    if (originalUrl == null) {
      return ResponseEntity.status(404).build();
    }
    routes.remove(url);
    return ResponseEntity.status(200).body("Deleted Successfully");
  }

  // Helper Methods
  private String getShortenedUrl(String originalUrl) {
    for (Map.Entry<String, String> entry : routes.entrySet()) {
      if (entry.getValue().equals(originalUrl)) {
        return "http://short.url/" + entry.getKey();
      }
    }
    return null;
  }

}
