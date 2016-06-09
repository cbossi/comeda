package ch.cbossi.comeda;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestRestController {

  @RequestMapping("/api")
  public String apiMain(@PathVariable final String key) {
    return null;
  }
}
