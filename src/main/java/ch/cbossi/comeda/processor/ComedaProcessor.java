package ch.cbossi.comeda.processor;

import static ch.cbossi.comeda.util.ElementFilters.isElementKind;
import static com.google.common.collect.Sets.newHashSet;
import static java.lang.String.format;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Stream.concat;
import static javax.lang.model.element.ElementKind.CLASS;
import static javax.tools.Diagnostic.Kind.ERROR;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

import com.google.auto.service.AutoService;

import ch.cbossi.comeda.generator.ComedaGenerator;
import ch.cbossi.comeda.generator.ControllerClass;

@AutoService(Processor.class)
public class ComedaProcessor extends AbstractProcessor {

  private static final String ERROR_CREATING_URL_CLASS = "Comeda annotation processor: Error creating URLs for '%s': %s";

  private ComedaGenerator generator;
  private Filer filer;
  private Messager messager;

  @Override
  public synchronized void init(final ProcessingEnvironment processingEnv) {
    super.init(processingEnv);
    generator = new ComedaGenerator();
    filer = processingEnv.getFiler();
    messager = processingEnv.getMessager();
  }

  @Override
  public Set<String> getSupportedAnnotationTypes() {
    return newHashSet(Controller.class.getName(), RestController.class.getName());
  }

  @Override
  public SourceVersion getSupportedSourceVersion() {
    return SourceVersion.latestSupported();
  }

  @Override
  public boolean process(final Set<? extends TypeElement> annotations, final RoundEnvironment roundEnv) {
    for (ControllerClass controllerClass : getControllerClasses(roundEnv)) {
      try {
        generator.generateUrlClass(controllerClass).writeTo(filer);
      }
      catch (IOException e) {
        messager.printMessage(ERROR, format(ERROR_CREATING_URL_CLASS, controllerClass.getQualifiedName(), e.getMessage()));
      }
    }
    return false;
  }

  private static List<ControllerClass> getControllerClasses(final RoundEnvironment roundEnv) {
    return concat(roundEnv.getElementsAnnotatedWith(Controller.class).stream(), roundEnv.getElementsAnnotatedWith(RestController.class).stream())
        .filter(isElementKind(CLASS))
        .map(element -> new ControllerClass((TypeElement) element))
        .collect(toList());
  }

}
