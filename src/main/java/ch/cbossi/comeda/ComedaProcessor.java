package ch.cbossi.comeda;

import static ch.cbossi.comeda.Strings.capitalize;
import static com.google.common.base.Strings.isNullOrEmpty;
import static com.squareup.javapoet.MethodSpec.constructorBuilder;
import static com.squareup.javapoet.MethodSpec.methodBuilder;
import static com.squareup.javapoet.TypeSpec.classBuilder;
import static java.lang.String.format;
import static java.util.Collections.singleton;
import static java.util.stream.Collectors.toList;
import static javax.lang.model.element.ElementKind.CLASS;
import static javax.lang.model.element.ElementKind.METHOD;
import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.lang.model.element.Modifier.STATIC;
import static javax.tools.Diagnostic.Kind.ERROR;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

@AutoService(Processor.class)
public class ComedaProcessor extends AbstractProcessor {

  private static final String ERROR_CREATING_URL_CLASS = "Comeda annotation processor: Error reating URLs for '%s'.";
  private static final String URLS = "Urls";
  private static final String URL = "Url";
  private static final String URL_SEPARATOR = "/";
  private static final String EMPTY = "";

  private static final String CONTROLLER_CLASS_JAVADOC = "Provides methods to create URL's for {@link $T}.\n\nThis class is generated by the comeda annotation processor.\n";
  private static final String REQUEST_METHOD_JAVADOC = "URL for {@link $T#$L()}.\n";

  private Filer filer;
  private Messager messager;

  @Override
  public synchronized void init(final ProcessingEnvironment processingEnv) {
    super.init(processingEnv);
    filer = processingEnv.getFiler();
    messager = processingEnv.getMessager();
  }

  @Override
  public Set<String> getSupportedAnnotationTypes() {
    return singleton(Controller.class.getName());
  }

  @Override
  public SourceVersion getSupportedSourceVersion() {
    return SourceVersion.latestSupported();
  }

  @Override
  public boolean process(final Set<? extends TypeElement> annotations, final RoundEnvironment roundEnv) {
    for (TypeElement controllerClass : getControllerClasses(roundEnv)) {
      boolean addStaticImport = false;

      List<MethodSpec> methods = new ArrayList<>();
      for (ExecutableElement requestMappingMethod : getRequestMappingMethods(controllerClass)) {
        String controllerMethodName = requestMappingMethod.getSimpleName().toString();
        String basicMethodName = controllerMethodName + URL;
        RequestMapping requestMapping = requestMappingMethod.getAnnotation(RequestMapping.class);
        String url = getUrl(requestMapping);

        RequestMethod[] requestMethods = requestMapping.method().length > 0 ? requestMapping.method() : new RequestMethod[] { GET };
        for (RequestMethod requestMethod : requestMethods) {
          String httpMethod = capitalize(requestMethod.name());
          String methodName = basicMethodName + httpMethod;

          MethodSpec.Builder methodBuilder = methodBuilder(methodName)
              .addJavadoc(REQUEST_METHOD_JAVADOC, controllerClass, controllerMethodName)
              .addModifiers(PUBLIC, STATIC)
              .addStatement("$T url = $S", String.class, url);

          List<ParameterSpec> arguments = new ArrayList<>();
          List<? extends VariableElement> pathVariableArguments = getPathVariableArguments(requestMappingMethod);
          if (!pathVariableArguments.isEmpty()) {
            addStaticImport = true;
          }
          for (VariableElement pathVariableArgument : pathVariableArguments) {
            PathVariable pathVariable = pathVariableArgument.getAnnotation(PathVariable.class);
            TypeName typeName = TypeName.get(pathVariableArgument.asType());
            String argumentName = pathVariableArgument.getSimpleName().toString();
            String pathVariableName = !isNullOrEmpty(pathVariable.value()) ? pathVariable.value() : argumentName;

            ParameterSpec parameterSpec = ParameterSpec.builder(typeName, argumentName, FINAL)
                .build();
            arguments.add(parameterSpec);
            methodBuilder.addStatement("url = url.replaceFirst($S, valueOf($L))", "{" + pathVariableName + "}", argumentName);
          }

          methodBuilder
              .addStatement("return url")
              .addParameters(arguments)
              .returns(TypeName.get(String.class));
          methods.add(methodBuilder.build());
        }

      }

      String className = controllerClass.getSimpleName() + URLS;

      MethodSpec constructor = constructorBuilder()
          .addModifiers(PRIVATE)
          .build();

      TypeSpec typeSpec = classBuilder(className)
          .addModifiers(PUBLIC, FINAL)
          .addJavadoc(CONTROLLER_CLASS_JAVADOC, controllerClass)
          .addMethod(constructor)
          .addMethods(methods)
          .build();
      String packageName = ((PackageElement) controllerClass.getEnclosingElement()).getQualifiedName().toString();
      try {
        JavaFile.Builder javaFileBuilder = JavaFile.builder(packageName, typeSpec);
        if (addStaticImport) {
          javaFileBuilder.addStaticImport(String.class, "valueOf");
        }
        javaFileBuilder.build()
            .writeTo(filer);
      }
      catch (IOException e) {
        messager.printMessage(ERROR, format(ERROR_CREATING_URL_CLASS, controllerClass.getQualifiedName()), controllerClass);
      }
    }
    return false;
  }

  private static String getUrl(final RequestMapping requestMapping) {
    String path = requestMapping.path().length > 0 ? requestMapping.path()[0] : EMPTY;
    String value = requestMapping.value().length > 0 ? requestMapping.value()[0] : EMPTY;
    String url = !path.isEmpty() ? path : !value.isEmpty() ? value : EMPTY;
    return url.startsWith(URL_SEPARATOR) ? url : URL_SEPARATOR + url;
  }

  private static List<TypeElement> getControllerClasses(final RoundEnvironment roundEnv) {
    return roundEnv.getElementsAnnotatedWith(Controller.class).stream()
        .filter(element -> element.getKind().equals(CLASS))
        .map(element -> (TypeElement) element)
        .collect(toList());
  }

  private static List<ExecutableElement> getRequestMappingMethods(final TypeElement controllerClass) {
    return controllerClass.getEnclosedElements().stream()
        .filter(element -> element.getKind().equals(METHOD))
        .filter(element -> element.getAnnotation(RequestMapping.class) != null)
        .map(element -> (ExecutableElement) element)
        .collect(toList());
  }

  private static List<? extends VariableElement> getPathVariableArguments(final ExecutableElement requestMethod) {
    return requestMethod.getParameters().stream()
        .filter(argument -> argument.getAnnotation(PathVariable.class) != null)
        .collect(toList());
  }

}
