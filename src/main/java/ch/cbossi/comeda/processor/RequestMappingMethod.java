package ch.cbossi.comeda.processor;

import static ch.cbossi.comeda.processor.ElementFilters.hasAnnotation;
import static ch.cbossi.comeda.processor.RequestMappingDecorator.decorate;
import static ch.cbossi.comeda.processor.Strings.capitalize;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.util.List;

import javax.lang.model.element.ExecutableElement;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Wrapper for a method annotated with {@link RequestMapping}.
 */
class RequestMappingMethod {

  private static final RequestMethod[] DEFAULT_HTTP_METHODS = new RequestMethod[] { GET };

  private final ExecutableElement requestMappingMethod;

  public RequestMappingMethod(final ExecutableElement requestMappingMethod) {
    this.requestMappingMethod = requestMappingMethod;
  }

  public String getName() {
    return requestMappingMethod.getSimpleName().toString();
  }

  public String getMethodUrl() {
    return decorate(getRequestMapping()).getUrl();
  }

  public List<String> getHttpMethods() {
    RequestMapping requestMapping = getRequestMapping();
    RequestMethod[] httpMethods = requestMapping.method().length > 0 ? requestMapping.method() : DEFAULT_HTTP_METHODS;
    return stream(httpMethods)
        .map(requestMethod -> capitalize(requestMethod.name().toLowerCase()))
        .collect(toList());
  }

  private RequestMapping getRequestMapping() {
    return requestMappingMethod.getAnnotation(RequestMapping.class);
  }

  public List<PathVariableArgument> getPathVariableArguments() {
    return requestMappingMethod.getParameters().stream()
        .filter(hasAnnotation(PathVariable.class))
        .map(parameter -> new PathVariableArgument(parameter))
        .collect(toList());
  }
}
