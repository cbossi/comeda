package ch.cbossi.comeda.processor;

import static java.util.Arrays.asList;
import static java.util.Arrays.stream;
import static java.util.Collections.singleton;
import static java.util.stream.Collectors.toSet;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.Processor;
import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;

public class CompilationTaskBuilder {

  private static final String DEFAULT_PACKAGE_NAME = "";

  private final JavaCompiler compiler;

  private final List<File> sourceFolders;
  private String packageName;
  private boolean includeSubPackages;
  private Set<Kind> fileKinds;
  private OutputStream outputStream;
  private final Set<Processor> annotationProcessors;
  private final List<String> compilerArguments;

  private CompilationTaskBuilder() {
    this.compiler = ToolProvider.getSystemJavaCompiler();

    this.sourceFolders = new ArrayList<>();
    this.packageName = DEFAULT_PACKAGE_NAME;
    this.includeSubPackages = true;
    this.fileKinds = singleton(Kind.SOURCE);
    this.outputStream = System.out;
    this.annotationProcessors = new HashSet<>();
    this.compilerArguments = new ArrayList<>();
  }

  public static CompilationTaskBuilder compilationTask() {
    return new CompilationTaskBuilder();
  }

  public CompilationTaskBuilder addSourceFolder(final File sourceFolder) {
    this.sourceFolders.add(sourceFolder);
    return this;
  }

  public CompilationTaskBuilder packageName(final String packageName) {
    this.packageName = packageName;
    return this;
  }

  public CompilationTaskBuilder includeSubPackages(final boolean includeSubPackages) {
    this.includeSubPackages = includeSubPackages;
    return this;
  }

  public CompilationTaskBuilder fileKinds(final Kind... fileKinds) {
    this.fileKinds = stream(fileKinds).collect(toSet());
    return this;
  }

  public CompilationTaskBuilder outputStream(final OutputStream outputStream) {
    this.outputStream = outputStream;
    return this;
  }

  public CompilationTaskBuilder addAnnotationProcessor(final Processor annotationProcessor) {
    this.annotationProcessors.add(annotationProcessor);
    return this;
  }

  public CompilationTaskBuilder addCompilerArguments(final String... compilerArguments) {
    this.compilerArguments.addAll(asList(compilerArguments));
    return this;
  }

  public CompilationTask build() throws IOException {
    Iterable<JavaFileObject> compilationUnits = getCompilationUnits();
    CompilationTask task = compiler.getTask(new PrintWriter(outputStream), null, null, compilerArguments, null, compilationUnits);
    task.setProcessors(annotationProcessors);
    return task;
  }

  private Iterable<JavaFileObject> getCompilationUnits() throws IOException {
    StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);
    fileManager.setLocation(StandardLocation.SOURCE_PATH, sourceFolders);
    return fileManager.list(StandardLocation.SOURCE_PATH, packageName, fileKinds, includeSubPackages);
  }

}
