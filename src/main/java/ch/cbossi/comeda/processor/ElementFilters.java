package ch.cbossi.comeda.processor;

import java.lang.annotation.Annotation;
import java.util.function.Predicate;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;

final class ElementFilters {

  private ElementFilters() {}

  public static Predicate<Element> isElementKind(final ElementKind kind) {
    return element -> element.getKind().equals(kind);
  }

  public static Predicate<Element> hasAnnotation(final Class<? extends Annotation> annotationType) {
    return element -> element.getAnnotation(annotationType) != null;
  }
}
