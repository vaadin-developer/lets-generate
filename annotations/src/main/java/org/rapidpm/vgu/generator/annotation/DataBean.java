package org.rapidpm.vgu.generator.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.CLASS;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(CLASS)
@Target(TYPE)
public @interface DataBean {
  DataBeanType type() default DataBeanType.PLAIN;

  CustomFilter[] customFilters() default {};

}
