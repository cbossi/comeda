package ch.cbossi.comeda;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import ch.cbossi.comeda.other.TestParam;

@Controller
public class TestController {

  @RequestMapping("default")
  public String withDefault() {
    return null;
  }

  @RequestMapping(value = "url")
  public String withValue() {
    return null;
  }

  @RequestMapping(path = "path")
  public String withPath() {
    return null;
  }

  @RequestMapping("var/{a}/{b}")
  public String withVariables(
      @PathVariable final int a,
      final String other,
      @PathVariable final String b) {
    return null;
  }

  @RequestMapping(path = "path/{a}")
  public String withCustomType(@PathVariable final TestParam a) {
    return null;
  }

  @RequestMapping(path = "path/{varName}")
  public String withCustomName(@PathVariable("varName") final int wrongName) {
    return null;
  }

  public String withoutRequestMapping() {
    return null;
  }
}
