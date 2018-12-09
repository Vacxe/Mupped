package com.github.jetlibs.mupped.core.generation;

import com.github.jetlibs.mupped.annotations.MapFrom;
import com.github.jetlibs.mupped.annotations.MapTo;
import com.github.jetlibs.mupped.interfaces.Mapper;
import com.squareup.javapoet.*;
import javafx.util.Pair;

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
        Set<? extends Element> mapFromAnnotation = roundEnv.getElementsAnnotatedWith(MapFrom.class);

        List<Pair<Element, Element>> mapTable = buildMapTable(mapToAnnotation, mapFromAnnotation);

        for (Element element : roundEnv.getElementsAnnotatedWith(MapTo.class)) {
            String[] mapTo = element.getAnnotation(MapTo.class).value();

            messager.printMessage(Diagnostic.Kind.NOTE, "Found class: " + element.getSimpleName().toString());
            String fromClassName = element.getSimpleName().toString();
            String toClassName = "B";
            String mapperName = String.format("%sto%sMapper", fromClassName, toClassName);

            TypeSpec.Builder navigatorClass = TypeSpec
                    .classBuilder(mapperName)
                    .addSuperinterface(ParameterizedTypeName.get(ClassName.get(Mapper.class), TypeName.get(element.asType()), TypeName.get(element.asType())))
                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL);

            MethodSpec constructor = MethodSpec
                    .constructorBuilder()
                    .addModifiers(Modifier.PUBLIC)
                    .build();

            MethodSpec mapMethod = MethodSpec
                    .methodBuilder("map")
                    .addModifiers(Modifier.PUBLIC)
                    .addAnnotation(Override.class)
                    .returns(TypeName.get(element.asType()))
                    .addParameter(TypeName.get(element.asType()), "input", Modifier.FINAL)
                    .addCode("return null;")
                    .build();

            navigatorClass.addMethod(mapMethod);
            navigatorClass.addMethod(constructor);

            try {
                JavaFile.builder("com.github.jetlibs.mupped.mappers", navigatorClass.build()).build().writeTo(filer);

                messager.printMessage(Diagnostic.Kind.NOTE, mapperName + " IsGenerated");
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        return false;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        final Set<String> annotataions = new LinkedHashSet<>();
        annotataions.add(MapTo.class.getCanonicalName());
        annotataions.add(MapFrom.class.getCanonicalName());
        return annotataions;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    private List<Pair<Element, Element>>  buildMapTable(Set<? extends Element> mapToAnnotation, Set<? extends Element> mapFromAnnotation){
        List<Pair<Element, Element>> mapList = new ArrayList<>();
        for (Element mapToElement : mapToAnnotation) {
            String[] mapToList = mapToElement.getAnnotation(MapTo.class).value();
            for (String mapTo : mapToList) {
                for (Element mapFromElement : mapFromAnnotation) {
                        if(mapFromElement.toString().startsWith(mapTo)){ // ADD back check
                            mapList.add(new Pair<>(mapToElement, mapFromElement));
                            break;
                        }
                }
            }
        }

        return mapList;
    }
}
