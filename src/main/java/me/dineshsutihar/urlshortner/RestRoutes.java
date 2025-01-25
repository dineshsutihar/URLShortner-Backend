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
    return "Welcome to URL Shortener";
  }

  // Accept Long URL and shorten it
  @PostMapping("/shorten")
  public ResponseEntity<String> shortenUrl(@RequestBody UrlRequest urlRequest) {
    System.out.println(urlRequest);
    String originalUrl = urlRequest.getUrl();
    System.out.println(originalUrl);

    if (originalUrl == null || originalUrl.isEmpty()) {
      return ResponseEntity.status(400).body("Error: URL cannot be empty");
    }

    if (routes.containsValue(originalUrl)) {
      return ResponseEntity.status(409).body("Error: URL already exists");
    }

    String shortenedUrl = generateRandomString(originalUrl);
    routes.put(shortenedUrl, originalUrl);

    return ResponseEntity.status(201).body(shortenedUrl);
  }

  // Short URL redirect to Original URL
  @GetMapping("/{shortCode}")
  public ResponseEntity<Void> expandUrl(@PathVariable String shortCode) {
    String originalUrl = routes.get(shortCode);

    if (originalUrl == null) {
      return ResponseEntity.status(404).build();
    }

    return ResponseEntity.status(301).location(URI.create(originalUrl)).build();
  }

  // Delete Short URL
  @DeleteMapping("/{shortCode}")
  public ResponseEntity<String> deleteUrl(@PathVariable String shortCode) {
    if (!routes.containsKey(shortCode)) {
      return ResponseEntity.status(404).body("Short URL not found");
    }
    routes.remove(shortCode);
    return ResponseEntity.status(200).body("Deleted Successfully");
  }

}
