package org.rapidpm.vgu.generator.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.CLASS;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(CLASS)
@Target(FIELD)
/**
 * Indicates that a property is not editable in the ui
 */
public @interface DisplayReadOnly {
}
