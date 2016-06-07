package ch.cbossi.comeda;

import java.lang.annotation.Annotation;
import java.util.function.Predicate;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;

public final class ElementFilters {

  private ElementFilters() {}

  static Predicate<Element> isElementKind(final ElementKind kind) {
    return element -> element.getKind().equals(kind);
  }

  static Predicate<Element> hasAnnotation(final Class<? extends Annotation> annotationType) {
    return element -> element.getAnnotation(annotationType) != null;
  }
}
