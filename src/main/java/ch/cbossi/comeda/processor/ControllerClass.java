package ch.cbossi.comeda.processor;

import static ch.cbossi.comeda.processor.ElementFilters.hasAnnotation;
import static ch.cbossi.comeda.processor.ElementFilters.isElementKind;
import static ch.cbossi.comeda.processor.RequestMappingDecorator.decorate;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;
import static javax.lang.model.element.ElementKind.METHOD;

import java.util.List;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;

import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Wrapper for controller classes.
 */
class ControllerClass {

  private static final String EMPTY = "";

  private final TypeElement controllerClass;

  public ControllerClass(final TypeElement controllerClass) {
    this.controllerClass = controllerClass;
  }

  public TypeElement getElement() {
    return controllerClass;
  }

  public String getName() {
    return controllerClass.getSimpleName().toString();
  }

  public String getPackageName() {
    return ((PackageElement) controllerClass.getEnclosingElement()).getQualifiedName().toString();
  }

  public String getQualifiedName() {
    return controllerClass.getQualifiedName().toString();
  }

  public String getUrl() {
    return ofNullable(controllerClass.getAnnotation(RequestMapping.class))
        .map(requestMapping -> decorate(requestMapping).getUrl())
        .orElse(EMPTY);
  }

  public List<RequestMappingMethod> getRequestMappingMethods() {
    return controllerClass.getEnclosedElements().stream()
        .filter(isElementKind(METHOD))
        .filter(hasAnnotation(RequestMapping.class))
        .map(element -> new RequestMappingMethod((ExecutableElement) element))
        .collect(toList());
  }

}
