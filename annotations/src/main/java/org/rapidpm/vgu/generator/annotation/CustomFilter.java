package org.rapidpm.vgu.generator.annotation;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.RetentionPolicy.CLASS;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;;

@Retention(CLASS)
@Target(ANNOTATION_TYPE)
public @interface CustomFilter {
  Class<?> type() default String.class;

  String name();

  boolean defaultFilter() default false;

  boolean sort() default true;
}
