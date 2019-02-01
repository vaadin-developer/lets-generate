package org.rapidpm.vgu.generator.annotation;

import static java.lang.annotation.RetentionPolicy.SOURCE;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(SOURCE)
@Target(ElementType.ANNOTATION_TYPE)
public @interface CustomFilter {
  Class<?> type() default String.class;

  String name();
}
