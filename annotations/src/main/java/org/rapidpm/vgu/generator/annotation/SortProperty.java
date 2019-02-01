package org.rapidpm.vgu.generator.annotation;

import static java.lang.annotation.RetentionPolicy.SOURCE;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(SOURCE)
@Target(ElementType.FIELD)
public @interface SortProperty {

}
