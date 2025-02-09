package me.dineshsutihar.urlshortner;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class RestRoutes {

  @GetMapping("/")
  public String home() {
    return "Welcome to URL Shortner";
  }

  @GetMapping("/shorten")
  public String shortenUrl(@RequestParam String url) {
    return "Shortened URL";
  }

  @GetMapping("/expand")
  public String expandUrl(@RequestParam String url) {
    return "Expanded URL";
  }

  @GetMapping("/stats")
  public String getStats(@RequestParam String url) {
    return "Stats";
  }

}
