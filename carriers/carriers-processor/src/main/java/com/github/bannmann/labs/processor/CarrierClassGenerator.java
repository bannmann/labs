package com.github.bannmann.labs.processor;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.processing.Generated;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import io.toolisticon.aptk.tools.ElementUtils;

@Slf4j
class CarrierClassGenerator
{
    private static final AnnotationSpec GENERATED_ANNOTATION_SPEC = AnnotationSpec.builder(Generated.class)
        .addMember("value", "\"" + CarrierClassGenerator.class.getName() + "\"")
        .build();

    private final ProcessingEnvironment processingEnvironment;
    private final TypeElement originalPojo;
    private final String packageName;

    public CarrierClassGenerator(ProcessingEnvironment processingEnvironment, TypeElement originalPojo)
    {
        this.processingEnvironment = processingEnvironment;
        this.originalPojo = originalPojo;

        PackageElement packageElement = (PackageElement) originalPojo.getEnclosingElement();
        packageName = packageElement.getQualifiedName()
            .toString();
    }

    public void generate(Set<IdReference> idReferences)
    {
        String className = makeCarrierClassName(idReferences);
        TypeSpec typeSpec = buildCarrierType(className, idReferences);
        JavaFile javaFile = JavaFile.builder(packageName, typeSpec)
            .build();
        writeClass(javaFile, originalPojo);
    }

    private String makeCarrierClassName(Set<IdReference> idReferences)
    {
        return String.format("%sCarrying%s",
            originalPojo.getSimpleName(),
            idReferences.stream()
                .map(IdReference::getPojoFieldName)
                .map(Names::capitalize)
                .collect(Collectors.joining("And")));
    }

    private TypeSpec buildCarrierType(String className, Set<IdReference> idReferences)
    {
        var typeSpec = TypeSpec.classBuilder(className)
            .addModifiers(Modifier.PUBLIC)
            .addAnnotation(GENERATED_ANNOTATION_SPEC)
            .addAnnotation(Value.class)
            .addAnnotation(Builder.class)
            .addJavadoc("@see $T", originalPojo);

        ClassName carrierClassName = makeCarrierClassName(className);

        String originalPojoParameterName = makeVariableName(originalPojo);
        var methodBuilder = MethodSpec.methodBuilder("from")
            .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
            .returns(carrierClassName)
            .addParameter(ParameterSpec.builder(TypeName.get(originalPojo.asType()), originalPojoParameterName)
                .addAnnotation(NonNull.class)
                .build())
            .addCode("\n");

        List<VariableElement> originalPojoFields = ElementUtils.AccessEnclosedElements.getEnclosedFields(originalPojo);
        for (VariableElement field : originalPojoFields)
        {
            TypeName referencedType = TypeName.get(field.asType());

            String fieldName = field.getSimpleName()
                .toString();

            var fieldSpecBuilder = FieldSpec.builder(referencedType,
                fieldName,
                field.getModifiers()
                    .toArray(new Modifier[]{}));

            if (idReferences.stream()
                .map(IdReference::getIdFieldName)
                .anyMatch(name -> name.equals(fieldName)))
            {
                fieldSpecBuilder.addAnnotation(NonNull.class);
            }

            typeSpec.addField(fieldSpecBuilder.build());
        }

        for (IdReference idReference : idReferences)
        {
            TypeName referencedType = ClassName.get(idReference.getPojoType());

            typeSpec.addField(FieldSpec.builder(referencedType,
                    idReference.getPojoFieldName(),
                    Modifier.PRIVATE,
                    Modifier.FINAL)
                .addAnnotation(NonNull.class)
                .build());

            String parameterName = idReference.getPojoFieldName();
            methodBuilder.addParameter(ParameterSpec.builder(referencedType, parameterName)
                .addAnnotation(NonNull.class)
                .build());

            methodBuilder.addCode(String.format(
                "if (!%s.getId().equals(%s.%s())) {\n  throw new IllegalArgumentException(\"%s\");\n}\n",
                parameterName,
                originalPojoParameterName,
                idReference.getIdGetterName(),
                parameterName));
        }

        methodBuilder.addCode("\nreturn builder()\n");

        for (VariableElement field : originalPojoFields)
        {
            String pojoFieldName = field.getSimpleName()
                .toString();
            String pojoFieldGetter = "get" + Names.capitalize(pojoFieldName);
            methodBuilder.addCode(String.format("  .%s(%s.%s())\n",
                pojoFieldName,
                originalPojoParameterName,
                pojoFieldGetter));
        }

        for (IdReference idReference : idReferences)
        {
            String builderMethod = idReference.getPojoFieldName();
            String parameterName = idReference.getPojoFieldName();
            methodBuilder.addCode(String.format("  .%s(%s)\n", builderMethod, parameterName));
        }

        methodBuilder.addCode("  .build();");

        typeSpec.addMethod(methodBuilder.build());

        return typeSpec.build();
    }

    private ClassName makeCarrierClassName(String carrierClassSimpleName)
    {
        String packageName = ClassName.get(originalPojo)
            .packageName();
        return ClassName.get(packageName, carrierClassSimpleName);
    }

    private String makeVariableName(TypeElement typeElement)
    {
        String name = typeElement.getSimpleName()
            .toString();
        return Names.uncapitalize(name);
    }

    private void writeClass(JavaFile carrierClass, Element originatingElement)
    {
        try
        {
            carrierClass.writeTo(processingEnvironment.getFiler());
        }
        catch (IOException e)
        {
            throw new ProcessingErrorException(e,
                originatingElement,
                GenerateCarriersProcessorMessages.ERROR_COULD_NOT_CREATE_CLASS,
                carrierClass.toJavaFileObject()
                    .getName(),
                e.getMessage());
        }
    }
}
