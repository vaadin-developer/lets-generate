package org.rapidpm.vgu.generator.codegenerator.vaadin;

import static javax.lang.model.element.Modifier.PUBLIC;
import java.util.function.Consumer;
import org.rapidpm.vgu.generator.model.DataBeanModel;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.i18n.LocaleChangeObserver;

public class VaadinUtils {
  private VaadinUtils() {
    throw new IllegalAccessError("Utility class");
  }

  public static TypeSpec i18Wrapper(Consumer<com.squareup.javapoet.MethodSpec.Builder> consumer) {
    com.squareup.javapoet.MethodSpec.Builder localeChangeMethodBuilder =
        MethodSpec.methodBuilder("localeChange").addModifiers(PUBLIC)
            .addParameter(LocaleChangeEvent.class, "event").addAnnotation(Override.class);
    consumer.accept(localeChangeMethodBuilder);

    return TypeSpec.classBuilder("I18NWrapper").superclass(Div.class)
        .addSuperinterface(LocaleChangeObserver.class)
        .addMethod(MethodSpec.constructorBuilder().addParameter(Component.class, "component")
            .addStatement("add(component)").build())
        .addMethod(localeChangeMethodBuilder.build()).build();
  }

  public static ClassName getDataProviderClassName(DataBeanModel model) {
    return ClassName.get(model.getPkg() + ".vaadin", model.getName() + "DataProvider");
  }
}
