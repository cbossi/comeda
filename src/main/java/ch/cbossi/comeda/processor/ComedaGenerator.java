package ch.cbossi.comeda.processor;

import static ch.cbossi.comeda.processor.Strings.capitalize;
import static ch.cbossi.comeda.processor.Urls.absolute;
import static ch.cbossi.comeda.processor.Urls.concatenate;
import static com.squareup.javapoet.MethodSpec.constructorBuilder;
import static com.squareup.javapoet.MethodSpec.methodBuilder;
import static com.squareup.javapoet.TypeSpec.classBuilder;
import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.lang.model.element.Modifier.STATIC;

import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import ch.cbossi.comeda.url.Url;

class ComedaGenerator {

  private static final String URLS = "Urls";
  private static final String HTTP = "http";

  private static final String CONTROLLER_CLASS_JAVADOC =
      "Provides methods to create URL's for {@link $T}.\n\nThis class is generated by the comeda annotation processor: https://github.com/cbossi/comeda.\n";
  private static final String REQUEST_METHOD_JAVADOC = "URL for {@link $T#$L()}.\n";

  public JavaFile generateUrlClass(final ControllerClass controllerClass) {
    boolean requiresStaticImport = false;

    String generatedClassName = controllerClass.getName() + URLS;
    TypeSpec.Builder generatedClass = classBuilder(generatedClassName)
        .addModifiers(PUBLIC, FINAL)
        .addJavadoc(CONTROLLER_CLASS_JAVADOC, controllerClass.getElement())
        .addMethod(constructorBuilder()
            .addModifiers(PRIVATE)
            .build());

    for (RequestMappingMethod requestMappingMethod : controllerClass.getRequestMappingMethods()) {
      String methodName = requestMappingMethod.getName();
      String url = absolute(concatenate(controllerClass.getUrl(), requestMappingMethod.getMethodUrl()));

      for (String httpMethod : requestMappingMethod.getHttpMethods()) {
        String generatedMethodName = HTTP + httpMethod + capitalize(methodName);
        MethodSpec.Builder generatedMethod = methodBuilder(generatedMethodName)
            .addJavadoc(REQUEST_METHOD_JAVADOC, controllerClass.getElement(), methodName)
            .addModifiers(PUBLIC, STATIC)
            .addStatement("$T url = $S", String.class, url);

        for (PathVariableArgument pathVariableArgument : requestMappingMethod.getPathVariableArguments()) {
          requiresStaticImport = true;
          String argumentName = pathVariableArgument.getName();
          TypeName typeName = pathVariableArgument.getTypeName();
          String pathVariableName = pathVariableArgument.getPathVariableName();

          generatedMethod.addStatement("url = url.replaceFirst($S, valueOf($L))", "\\{" + pathVariableName + "\\}", argumentName);
          generatedMethod.addParameter(ParameterSpec.builder(typeName, argumentName, FINAL).build());
        }
        generatedMethod
            .addStatement("return new $T(url)", Url.class)
            .returns(Url.class);

        generatedClass.addMethod(generatedMethod.build());
      }

    }

    JavaFile.Builder javaFileBuilder = JavaFile.builder(controllerClass.getPackageName(), generatedClass.build());
    if (requiresStaticImport) {
      javaFileBuilder.addStaticImport(String.class, "valueOf");
    }
    return javaFileBuilder.build();
  }

}
