package ch.cbossi.comeda.processor;

import static java.util.Optional.empty;

import java.util.Optional;

import org.springframework.web.bind.annotation.RequestMapping;

public class RequestMappingDecorator {

  private static final String EMPTY = "";

  private final RequestMapping requestMapping;

  public static RequestMappingDecorator decorate(final RequestMapping requestMapping) {
    return new RequestMappingDecorator(requestMapping);
  }

  public RequestMappingDecorator(final RequestMapping requestMapping) {
    this.requestMapping = requestMapping;
  }

  public String getUrl() {
    return path().orElse(value().orElse(EMPTY));
  }

  /*
   * If there are multiple paths for the same RequestMapping, only the first one is considered.
   */
  private Optional<String> path() {
    return requestMapping.path().length > 0 ? Optional.of(requestMapping.path()[0]) : empty();
  }

  /*
   * If there are multiple values for the same RequestMapping, only the first one is considered.
   */
  private Optional<String> value() {
    return requestMapping.value().length > 0 ? Optional.of(requestMapping.value()[0]) : empty();
  }

}
