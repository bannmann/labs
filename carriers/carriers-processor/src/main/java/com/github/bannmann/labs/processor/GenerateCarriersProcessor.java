package com.github.bannmann.labs.processor;

import static io.toolisticon.aptk.tools.TypeUtils.Generics.createGenericType;
import static java.util.function.Predicate.not;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;

import lombok.extern.slf4j.Slf4j;

import com.github.bannmann.labs.api.GenerateCarriers;
import com.google.common.collect.Sets;
import io.toolisticon.aptk.tools.AbstractAnnotationProcessor;
import io.toolisticon.aptk.tools.ElementUtils;
import io.toolisticon.aptk.tools.MessagerUtils;
import io.toolisticon.aptk.tools.TypeMirrorWrapper;
import io.toolisticon.aptk.tools.TypeUtils;
import io.toolisticon.aptk.tools.corematcher.AptkCoreMatchers;
import io.toolisticon.aptk.tools.fluentfilter.FluentElementFilter;
import io.toolisticon.aptk.tools.fluentvalidator.FluentElementValidator;
import io.toolisticon.aptk.tools.generics.GenericType;
import io.toolisticon.spiap.api.Service;

/**
 * Annotation Processor for {@link com.github.bannmann.labs.api.GenerateCarriers}.
 *
 * This demo processor does some validations and creates a class.
 */

@Service(Processor.class)
@Slf4j
public class GenerateCarriersProcessor extends AbstractAnnotationProcessor
{
    private static final Pattern ID_FIELD_SUFFIX = Pattern.compile("(Id|Identifier)$");

    private final static Set<String> SUPPORTED_ANNOTATIONS = createSupportedAnnotationSet(GenerateCarriers.class);

    /**
     * Stores the class name of "Identifier" in a way that maven-shade-plugin will not alter during relocation.
     */
    private static final String IDENTIFIER_FQCN = String.join(".", "com", "github", "mizool", "core", "Identifier");

    @Override
    public Set<String> getSupportedAnnotationTypes()
    {
        return SUPPORTED_ANNOTATIONS;
    }

    @Override
    public boolean processAnnotations(Set<? extends TypeElement> annotations, RoundEnvironment environment)
    {
        try
        {
            processAnnotations(environment);
        }
        catch (ProcessingErrorException e)
        {
            log.debug("Processing error", e);
            e.getMessageEvoker()
                .run();
        }
        catch (Exception e)
        {
            Debugging.logToDisk(e);

            getMessager().printMessage(Diagnostic.Kind.ERROR,
                getClass().getSimpleName() + " encountered unexpected error, see " + Debugging.LOG_FILE_PATH);
        }

        return true;
    }

    private void processAnnotations(RoundEnvironment environment)
    {
        for (Element element : environment.getElementsAnnotatedWith(GenerateCarriers.class))
        {
            FluentElementValidator.createFluentElementValidator(element)
                .is(AptkCoreMatchers.IS_CLASS)
                .validateAndIssueMessages();

            TypeElement typeElement = (TypeElement) element;

            Set<IdReference> idReferences = obtainIdReferences(typeElement);
            CarrierClassGenerator generator = new CarrierClassGenerator(processingEnv, typeElement);

            // For fields 'foo' and 'bar', create XCarryingFoo, XCarryingBar and XCarryingFooAndBar
            Sets.powerSet(idReferences)
                .stream()
                .filter(not(Set::isEmpty))
                .forEach(generator::generate);
        }
    }

    private Set<IdReference> obtainIdReferences(TypeElement typeElement)
    {
        GenericType identifierOfAnnotatedType = createGenericType(IDENTIFIER_FQCN,
            createGenericType(typeElement.asType()));

        List<VariableElement> fields = ElementUtils.AccessEnclosedElements.getEnclosedFields(typeElement);

        Set<IdReference> idReferences = FluentElementFilter.createFluentElementFilter(fields)
            .applyFilter(AptkCoreMatchers.BY_RAW_TYPE_FQN)
            .filterByOneOf(IDENTIFIER_FQCN)
            .applyInvertedFilter(AptkCoreMatchers.BY_GENERIC_TYPE)
            .filterByOneOf(identifierOfAnnotatedType)
            .getResult()
            .stream()
            .map(this::toIdReference)
            .collect(Collectors.toCollection(LinkedHashSet::new));

        if (idReferences.isEmpty())
        {
            MessagerUtils.error(typeElement,
                GenerateCarriersProcessorMessages.ERROR_NO_IDENTIFIER_FIELDS,
                typeElement.getSimpleName());
        }
        return idReferences;
    }

    private IdReference toIdReference(VariableElement variableElement)
    {
        TypeMirrorWrapper fieldType = TypeMirrorWrapper.wrap(variableElement.asType());
        TypeMirror typeArgument = fieldType.getTypeArguments()
            .get(0);

        String idFieldName = variableElement.getSimpleName()
            .toString();

        String pojoFieldName = ID_FIELD_SUFFIX.matcher(idFieldName)
            .replaceAll("");
        if (pojoFieldName.equals(idFieldName))
        {
            Element typeElement = variableElement.getEnclosingElement();
            throw new ProcessingErrorException(typeElement,
                GenerateCarriersProcessorMessages.ERROR_ILLEGAL_FIELD_NAME,
                typeElement.getSimpleName(),
                idFieldName);
        }

        return IdReference.builder()
            .idFieldName(idFieldName)
            .pojoType(typeArgument)
            .pojoFieldName(pojoFieldName)
            .build();
    }
}
