package ch.cbossi.comeda.processor;

import static com.google.common.base.Strings.isNullOrEmpty;

import javax.lang.model.element.VariableElement;

import org.springframework.web.bind.annotation.PathVariable;

import com.squareup.javapoet.TypeName;

/**
 * Wrapper for a method argument annotated with {@link PathVariable}.
 */
class PathVariableArgument {

  private final VariableElement pathVariableArgument;

  public PathVariableArgument(final VariableElement pathVariableArgument) {
    this.pathVariableArgument = pathVariableArgument;
  }

  public String getName() {
    return pathVariableArgument.getSimpleName().toString();
  }

  public TypeName getTypeName() {
    return TypeName.get(pathVariableArgument.asType());
  }

  public String getPathVariableName() {
    PathVariable pathVariable = getPathVariable();
    return !isNullOrEmpty(pathVariable.value()) ? pathVariable.value() : getName();
  }

  private PathVariable getPathVariable() {
    return pathVariableArgument.getAnnotation(PathVariable.class);
  }

}
