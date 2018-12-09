package com.github.vacxe.mupped.core.generation;

import com.github.vacxe.mupped.annotations.MapTo;
import com.github.vacxe.mupped.annotations.MappableConstructor;
import com.github.vacxe.mupped.annotations.Mappable;
import com.github.vacxe.mupped.interfaces.Mapper;
import com.squareup.javapoet.*;
import com.sun.tools.javac.code.Symbol;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.tools.Diagnostic;
import java.io.IOException;
import java.util.*;

public class MuppedAnnotationProcessor extends AbstractProcessor {

    private Messager messager;
    private Filer filer;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        messager = processingEnv.getMessager();
        filer = processingEnv.getFiler();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Set<? extends Element> mapToAnnotation = roundEnv.getElementsAnnotatedWith(MapTo.class);
        Set<? extends Element> mapConstructors = roundEnv.getElementsAnnotatedWith(MappableConstructor.class);
        Set<? extends Element> mappable = roundEnv.getElementsAnnotatedWith(Mappable.class);

        List<MapRequirement> mapRequirements = prepareMapRequirements(mapToAnnotation, mappable, mapConstructors);

        for (MapRequirement requirement : mapRequirements) {
            Element from = requirement.fromClass;
            Element to = requirement.toClass;

            messager.printMessage(Diagnostic.Kind.NOTE, "Found class: " + from.getSimpleName().toString());
            String fromClassName = from.getSimpleName().toString();
            String toClassName = to.getSimpleName().toString();

            String mapperName = String.format("%sto%sMapper", fromClassName, toClassName);

            TypeSpec.Builder navigatorClass = TypeSpec
                    .classBuilder(mapperName)
                    .addSuperinterface(ParameterizedTypeName.get(ClassName.get(Mapper.class), TypeName.get(from.asType()), TypeName.get(to.asType())))
                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL);

            MethodSpec constructor = MethodSpec
                    .constructorBuilder()
                    .addModifiers(Modifier.PUBLIC)
                    .build();

            MethodSpec mapMethod = MethodSpec
                    .methodBuilder("map")
                    .addModifiers(Modifier.PUBLIC)
                    .addAnnotation(Override.class)
                    .returns(TypeName.get(to.asType()))
                    .addParameter(TypeName.get(from.asType()), "input", Modifier.FINAL)
                    .addCode(buildReturnValue(requirement))
                    .build();

            navigatorClass.addMethod(mapMethod);
            navigatorClass.addMethod(constructor);

            try {
                JavaFile.builder("com.github.vacxe.mupped.mappers", navigatorClass.build()).build().writeTo(filer);

                messager.printMessage(Diagnostic.Kind.NOTE, mapperName + " IsGenerated");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return false;
    }

    private String buildReturnValue(MapRequirement requirement) {
        Symbol.MethodSymbol methodSymbol = (Symbol.MethodSymbol) requirement.toConstructor;
        List<Symbol.VarSymbol> params = methodSymbol.params;

        StringBuilder builder = new StringBuilder();
        builder.append("return new ");
        builder.append(requirement.toClass.getSimpleName().toString());
        builder.append("(");

        for(Symbol.VarSymbol param: params){
            builder.append("input.").append(param.toString()).append(", ");
        }
        builder.delete(builder.length() - 2, builder.length());
        builder.append(")");
        builder.append(";");

        return builder.toString();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        final Set<String> annotations = new LinkedHashSet<>();
        annotations.add(MappableConstructor.class.getCanonicalName());
        annotations.add(MapTo.class.getCanonicalName());
        annotations.add(Mappable.class.getCanonicalName());
        return annotations;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    private List<MapRequirement> prepareMapRequirements(Set<? extends Element> mapToElemnts,
                                                        Set<? extends Element> mappablesElements,
                                                        Set<? extends Element> mapConstructorsElements) {
        List<MapRequirement> mapRequirements = new ArrayList<>();

        for (Element mapFromElements : mapToElemnts) {
            String[] mapToList = mapFromElements.getAnnotation(MapTo.class).value();
            for (String mapTo : mapToList) {
                MapRequirement mapRequirement = new MapRequirement();
                mapRequirement.fromClass = mapFromElements;
                for (Element mapToElement : mappablesElements) {
                    if (mapToElement.toString().endsWith(mapTo)) {
                        mapRequirement.toClass = mapToElement;
                        break;
                    }
                }

                for (Element constructorElement : mapConstructorsElements) {
                    if (constructorElement.toString().startsWith(mapTo)) {
                        mapRequirement.toConstructor = constructorElement;
                        break;
                    }
                }

                if (mapRequirement.toClass != null && mapRequirement.toConstructor != null) {
                    mapRequirements.add(mapRequirement);
                }else {
                    StringBuilder errorString = new StringBuilder();
                    errorString.append("For class: ");
                    errorString.append(mapRequirement.fromClass.getSimpleName().toString());

                    if(mapRequirement.toClass == null){
                        errorString.append("\nMark target class by @Mappable annotation");
                    }
                    if(mapRequirement.toConstructor == null){
                        errorString.append("\nMark target constructor by @MappableConstructor annotation");
                    }

                    messager.printMessage(Diagnostic.Kind.WARNING, errorString) ;
                }
            }
        }

        return mapRequirements;
    }
}
