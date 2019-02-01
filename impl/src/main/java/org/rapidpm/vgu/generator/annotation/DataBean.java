package org.rapidpm.vgu.generator.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.SOURCE;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(SOURCE)
@Target(TYPE)
public @interface DataBean {
  DataBeanType type() default DataBeanType.PLAIN;

  CustomFilter[] customFilters() default {};

}
