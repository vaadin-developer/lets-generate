package org.rapidpm.vgu.generator.codegenerator.vaadin;

import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.PROTECTED;
import static javax.lang.model.element.Modifier.PUBLIC;
import java.io.IOException;
import javax.annotation.processing.Filer;
import javax.lang.model.element.Modifier;
import org.rapidpm.vgu.generator.codegenerator.AbstractCodeGenerator;
import org.rapidpm.vgu.generator.codegenerator.ClassNameUtils;
import org.rapidpm.vgu.generator.codegenerator.JPoetUtils;
import org.rapidpm.vgu.generator.model.DataBeanModel;
import org.rapidpm.vgu.generator.model.PropertyModel;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeSpec.Builder;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.ReadOnlyHasValue;
import com.vaadin.flow.data.converter.StringToIntegerConverter;

public class VaadinFormGenerator extends AbstractCodeGenerator {

  @Override
  public void writeCode(Filer filer, DataBeanModel model) throws IOException {
    TypeSpec i18WrapperType = i18Wrapper(model);
    Builder formClassBuilder = TypeSpec.classBuilder(model.getName() + classSuffix())
        .superclass(ParameterizedTypeName.get(
            ClassName.get("com.vaadin.flow.component", "AbstractCompositeField"),
            ClassName.get("com.vaadin.flow.component", "Component"), beanClassName(model),
            JPoetUtils.getBeanClassName(model)))
        .addModifiers(Modifier.PUBLIC).addMethod(constructor(model)).addField(binder(model))
        .addField(FieldSpec.builder(Button.class, "submitButton", PROTECTED).build())
        .addField(FieldSpec.builder(Button.class, "cancelButton", PROTECTED).build())
        .addField(FieldSpec.builder(Button.class, "resetButton", PROTECTED).build())
        .addField(FieldSpec.builder(ClassName.bestGuess("I18NWrapper"), "wrapper", PRIVATE).build())
        .addMethod(setPresentationValue(model)).addField(Component.class, "layout", PROTECTED)
        .addMethod(initLayout(model)).addMethod(getContent()).addMethod(initSubmitButton(model))
        .addMethod(initResetButton(model)).addType(i18WrapperType);

    if (model.getCaptionMethod().isPresent()) {
      formClassBuilder.addField(TypeName.get(Label.class), "captionLabel", PROTECTED)
          .addMethod(initCaptionLabel(model)).addMethod(bindCaption(model));
    }
    for (PropertyModel property : model.getProperties()) {
      formClassBuilder.addField(propertyField(property));
      formClassBuilder.addMethod(bindMethod(property));
      formClassBuilder.addMethod(initField(property));
    }
    writeClass(filer, model, formClassBuilder.build());
  }

  private TypeSpec i18Wrapper(DataBeanModel model) {
    return VaadinUtils.i18Wrapper(localeChangeMethodBuilder -> {
      for (PropertyModel propertyModel : model.getProperties()) {
        String fieldName = fieldName(propertyModel);
        localeChangeMethodBuilder.addStatement("$L.this.$L.setLabel(getTranslation($S))",
            packageName(model) + "." + model.getName() + classSuffix(), fieldName,
            TranslationUtil.captionKey(model, propertyModel));
      }
    });
  }

  private MethodSpec initCaptionLabel(DataBeanModel dataBeanModel) {
    return MethodSpec.methodBuilder("initCaptionLabel").addModifiers(PRIVATE)
        .addStatement("this.captionLabel = new $T()", Label.class).build();
  }

  private MethodSpec initSubmitButton(DataBeanModel dataBeanModel) {
    return MethodSpec.methodBuilder("initSubmitButton").addModifiers(PROTECTED)
        .beginControlFlow("$L.addClickListener(e ->", "submitButton")
        .addStatement("$T newValue = new $T()",
            ClassName.get(dataBeanModel.getPackage(), dataBeanModel.getName()),
            ClassName.get(dataBeanModel.getPackage(), dataBeanModel.getName()))
        .addStatement("boolean valid = binder.writeBeanIfValid(newValue)")
        .beginControlFlow("if(valid)").addStatement("setModelValue(newValue, true)")
        .endControlFlow().endControlFlow(")").build();
  }

  private MethodSpec initResetButton(DataBeanModel model) {
    return MethodSpec.methodBuilder("initResetButton").addModifiers(PROTECTED)
        .beginControlFlow("$L.addClickListener(e ->", "resetButton")
        .addStatement("binder.readBean(getValue())").endControlFlow(")").build();
  }

  private ClassName beanClassName(DataBeanModel model) {
    return ClassName.get(model.getPackage() + "." + packageSuffix(),
        model.getName() + classSuffix());
  }

  private MethodSpec initField(PropertyModel propertyModel) {
    String fieldName = fieldName(propertyModel);
    com.squareup.javapoet.MethodSpec.Builder initFieldMethod =
        MethodSpec.methodBuilder(fieldInitMethodName(propertyModel)).addModifiers(PROTECTED)
            .addStatement("this.$L = new $T()", fieldName, fieldType(propertyModel));
    // .addStatement("this.$L.setLabel($S)", fieldName, propertyModel.getName());
    if (propertyModel.isDisplayReadOnly()) {
      initFieldMethod.addStatement("this.$L.setReadOnly($L)", fieldName, true);
    }
    if (TypeName.INT.equals(TypeName.get(propertyModel.getType()))
        || TypeName.INT.equals(TypeName.get(propertyModel.getType()).box())) {
      initFieldMethod.addStatement("this.$L.setPattern($S)", fieldName, "[0-9]*")
          .addStatement("this.$L.setPreventInvalidInput(true)", fieldName);
    }
    if (TypeName.get(propertyModel.getType()).isPrimitive()) {
      initFieldMethod.addStatement("this.$L.setRequired(true)", fieldName);
    }
    return initFieldMethod.build();
  }

  private String fieldInitMethodName(PropertyModel propertyModel) {
    return "init" + ClassNameUtils.capitalizeFirstLetter(fieldName(propertyModel));
  }

  private MethodSpec initLayout(DataBeanModel model) {
    com.squareup.javapoet.MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("initLayout")
        .addModifiers(PROTECTED).addStatement("submitButton = new Button($S)", "OK")
        .addStatement("resetButton = new Button($S)", "Zurücksetzen")
        .addStatement("cancelButton = new Button($S)", "Abbrechen")
        .addStatement("initSubmitButton()").addStatement("initResetButton()")
        .addStatement("$T buttonLayout = new $T($L, $L, $L)", HorizontalLayout.class,
            HorizontalLayout.class, "submitButton", "cancelButton", "resetButton")
        .addStatement("$T formLayout = new $T()", FormLayout.class, FormLayout.class);
    if (model.getCaptionMethod().isPresent()) {
      methodBuilder.addStatement("formLayout.add($L)", "captionLabel");
    }
    for (PropertyModel property : model.getProperties()) {
      methodBuilder.addStatement("formLayout.add($L)", fieldName(property));
    }
    methodBuilder.addStatement("layout = new $T(formLayout, buttonLayout)", VerticalLayout.class);
    return methodBuilder.build();
  }

  private MethodSpec constructor(DataBeanModel model) {
    com.squareup.javapoet.MethodSpec.Builder constructorBuilder =
        MethodSpec.constructorBuilder().addModifiers(PUBLIC).addStatement("super(null)");
    if (model.getCaptionMethod().isPresent()) {
      constructorBuilder.addStatement("initCaptionLabel()");
      constructorBuilder.addStatement("bindCaptionLabel()");
    }
    for (PropertyModel property : model.getProperties()) {
      constructorBuilder.addStatement(fieldInitMethodName(property) + "()");
      constructorBuilder.addStatement(bindMethodName(property) + "()");
    }
    constructorBuilder.addStatement("initLayout()")
        .addStatement("this.wrapper = new I18NWrapper(layout)");
    return constructorBuilder.build();
  }

  private FieldSpec binder(DataBeanModel model) {
    return FieldSpec
        .builder(ParameterizedTypeName.get(ClassName.get("com.vaadin.flow.data.binder", "Binder"),
            JPoetUtils.getBeanClassName(model)), "binder", PRIVATE)
        .initializer("new Binder<>($T.class)", JPoetUtils.getBeanClassName(model)).build();
  }

  private MethodSpec setPresentationValue(DataBeanModel model) {
    return MethodSpec.methodBuilder("setPresentationValue").addAnnotation(Override.class)
        .addModifiers(PUBLIC)
        .addParameter(JPoetUtils.getBeanClassName(model), "newPresentationValue")
        .addStatement("binder.readBean($L)", "newPresentationValue").returns(TypeName.VOID).build();
  }

  private MethodSpec getContent() {
    return MethodSpec.methodBuilder("getContent").addAnnotation(Override.class).addModifiers(PUBLIC)
        .addStatement("return $L", "wrapper").returns(Component.class).build();
  }

  private FieldSpec propertyField(PropertyModel propertyModel) {
    return FieldSpec.builder(fieldType(propertyModel), fieldName(propertyModel), PROTECTED).build();
  }

  private String fieldName(PropertyModel propertyModel) {
    return propertyModel.getName() + "Field";
  }

  private MethodSpec bindMethod(PropertyModel propertyModel) {
    com.squareup.javapoet.MethodSpec.Builder binMethodBuilder =
        MethodSpec.methodBuilder(bindMethodName(propertyModel)).addModifiers(PROTECTED);
    if (TypeName.get(propertyModel.getType()).equals(TypeName.INT)
        || TypeName.get(propertyModel.getType()).equals(TypeName.INT.box())) {
      Class<?> converterClass = StringToIntegerConverter.class;
      String convertErrorKey = "common.error.converter.int";
      binMethodBuilder.addStatement(
          "binder.forField($L).withConverter(new $T((c) -> getTranslation($S))).bind($S)",
          fieldName(propertyModel), ClassName.get(converterClass), convertErrorKey,
          propertyModel.getName());
    } else {
      binMethodBuilder.addStatement("binder.forField($L).bind($S)", fieldName(propertyModel),
          propertyModel.getName());
    }
    return binMethodBuilder.build();
  }

  private MethodSpec bindCaption(DataBeanModel model) {
    com.squareup.javapoet.MethodSpec.Builder binMethodBuilder =
        MethodSpec.methodBuilder("bindCaptionLabel").addModifiers(PROTECTED);
    binMethodBuilder
        .addStatement("$T<String> hasValue = new $T<>(captionLabel::setText)",
            ReadOnlyHasValue.class, ReadOnlyHasValue.class)
        .addStatement("binder.forField(hasValue).bind($L::$L, null)", model.getName(),
            model.getCaptionMethod().get().getSimpleName().toString());
    return binMethodBuilder.build();
  }

  private String bindMethodName(PropertyModel propertyModel) {
    return "bind" + ClassNameUtils.capitalizeFirstLetter(fieldName(propertyModel));
  }

  @Override
  public String packageSuffix() {
    return "vaadin";
  }

  @Override
  public String classSuffix() {
    return "Form";
  }

  private ClassName fieldType(PropertyModel propertyModel) {
    if (TypeName.get(propertyModel.getType()).equals(TypeName.INT)) {
      return ClassName.get(TextField.class);
      // return ClassName.get(IntegerNumberField.class);
    } else {
      return ClassName.get(TextField.class);
    }
  }
}
