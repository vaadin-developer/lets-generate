package org.rapidpm.vgu.generator.codegenerator.vaadin;

import java.util.Comparator;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.stream.StreamSupport;
import org.rapidpm.dependencies.core.logger.HasLogger;
import org.rapidpm.vgu.generator.processor.VaadinDataBeanAnnotationProcessor;
import com.squareup.javapoet.TypeName;

public class FieldCreatorFactory implements HasLogger {

  private ServiceLoader<FieldCreator> fieldCreatorLoader;

  public FieldCreatorFactory() {
    fieldCreatorLoader = ServiceLoader.load(FieldCreator.class,
        VaadinDataBeanAnnotationProcessor.class.getClassLoader());

    logger().info(() -> {
      StringBuilder sb = new StringBuilder("Available fieldCreators:\n");
      for (FieldCreator fc : fieldCreatorLoader) {
        sb.append("\t");
        sb.append(fc.getClass().getName());
        sb.append("\n");
      }
      return sb.toString();
    });

  }

  public Optional<FieldCreator> getFieldCreator(TypeName typeName, FieldType fieldType) {
    return StreamSupport.stream(fieldCreatorLoader.spliterator(), false)
        .filter(creator -> creator.isResponsibleFor(typeName))
        .sorted(Comparator
            .comparingInt(fieldCreator -> ((FieldCreator) fieldCreator).getPriority(fieldType)).reversed()).findFirst();
  }
}
