package org.rapidpm.vgu.generator.processor;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedOptions;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic.Kind;
import javax.tools.FileObject;
import javax.tools.JavaFileManager.Location;
import javax.tools.StandardLocation;
import org.rapidpm.dependencies.core.logger.HasLogger;
import org.rapidpm.vgu.generator.annotation.DataBean;
import org.rapidpm.vgu.generator.model.DataBeanModel;
import com.google.auto.service.AutoService;

@SupportedSourceVersion(SourceVersion.RELEASE_8)
@AutoService(Processor.class)
@SupportedOptions({"debug"})
public class TranslationProcessor extends AbstractDataBeanProcessor implements HasLogger {

  private static final Location OUTPUT = StandardLocation.SOURCE_OUTPUT;
  private Map<String, String> translations = new TreeMap<>();

  public TranslationProcessor() {
    translations.put("common.error.converter.int", "Value must be an integer");
  }

  @Override
  public Set<String> getSupportedAnnotationTypes() {
    Set<String> supportedAnnotations = new HashSet<>(Arrays.asList(DataBean.class.getName()));
    return supportedAnnotations;
  }

  @Override
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    logger().info("TranslationProcessor");
    try {
      return processImpl(annotations, roundEnv);
    } catch (Exception e) {
      // We don't allow exceptions of any kind to propagate to the compiler
      StringWriter writer = new StringWriter();
      e.printStackTrace(new PrintWriter(writer));
      fatalError(writer.toString());
      return false;
    }
  }

  private boolean processImpl(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    if (roundEnv.processingOver()) {
      generateConfigFiles();
    } else {
      processAnnotations(annotations, roundEnv);
    }

    return false;
  }

  private void processAnnotations(Set<? extends TypeElement> annotations,
      RoundEnvironment roundEnv) {

    Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(DataBean.class);

    log(annotations.toString());
    log(elements.toString());

    for (Element e : elements) {

      TypeElement typeElement = (TypeElement) e;

      DataBeanModel dataBeanModel = process(typeElement);
      translations.putAll(dataBeanModel.getTranslations());
    }
  }

  private void generateConfigFiles() {
    Filer filer = processingEnv.getFiler();
    String resourceFile = "Translation.properties";
    log("Working on resource file: " + resourceFile);
    try {
      Map<String, String> allServices = new TreeMap<>();
      try {
        // would like to be able to print the full path
        // before we attempt to get the resource in case the behavior
        // of filer.getResource does change to match the spec, but there's
        // no good way to resolve CLASS_OUTPUT without first getting a resource.
        FileObject existingFile = filer.getResource(OUTPUT, "", resourceFile);
        log("Looking for existing resource file at " + existingFile.toUri());
        Map<String, String> oldServices =
            TranslationFiles.readServiceFile(existingFile.openInputStream());
        log("Existing service entries: " + oldServices);
        allServices.putAll(oldServices);
      } catch (IOException e) {
        // According to the javadoc, Filer.getResource throws an exception
        // if the file doesn't already exist. In practice this doesn't
        // appear to be the case. Filer.getResource will happily return a
        // FileObject that refers to a non-existent file but will throw
        // IOException if you try to open an input stream for it.
        log("Resource file did not already exist.");
      }
      allServices.putAll(translations);
      log("New service file contents: " + allServices);
      FileObject fileObject = filer.createResource(OUTPUT, "", resourceFile);
      OutputStream out = fileObject.openOutputStream();
      TranslationFiles.writeServiceFile(allServices, out);
      out.close();
      log("Wrote to: " + fileObject.toUri());
    } catch (IOException e) {
      fatalError("Unable to create " + resourceFile + ", " + e);
      return;
    }

  }

  private void log(String msg) {
    if (processingEnv.getOptions().containsKey("debug")) {
      processingEnv.getMessager().printMessage(Kind.NOTE, msg);
    }
  }

  private void error(String msg, Element element, AnnotationMirror annotation) {
    processingEnv.getMessager().printMessage(Kind.ERROR, msg, element, annotation);
  }

  private void fatalError(String msg) {
    processingEnv.getMessager().printMessage(Kind.ERROR, "FATAL ERROR: " + msg);
  }

  @Override
  public void write(TypeElement typeElement, DataBeanModel dataBeanModel) {
    // NOOP
  }
}
