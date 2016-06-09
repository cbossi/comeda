package ch.cbossi.comeda.processor;

import static ch.cbossi.comeda.processor.CompilationTaskBuilder.compilationTask;

import java.io.File;

import javax.tools.JavaCompiler.CompilationTask;

import org.junit.Test;

public class ComedaProcessorTest {

  private static final String TEST_FOLDER = "src/test/resources";
  private static final String TEST_PACKAGE = "ch.cbossi.comeda";

  private static final String OUTPUT_FOLDER = "gen";

  @Test
  public void testProcess() throws Exception {
    CompilationTask task = compilationTask()
        .addSourceFolder(getSourceFolder())
        .packageName(TEST_PACKAGE)
        .addAnnotationProcessor(new ComedaProcessor())
        .addCompilerArguments("-d", OUTPUT_FOLDER) // compiled class files
        .addCompilerArguments("-s", OUTPUT_FOLDER) // generated java files
        .build();
    task.call();
  }

  private static File getSourceFolder() {
    File projectFolder = new File(".");
    return new File(projectFolder, TEST_FOLDER);
  }
}
