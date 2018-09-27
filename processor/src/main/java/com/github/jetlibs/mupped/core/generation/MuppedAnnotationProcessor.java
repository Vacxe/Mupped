package com.github.jetlibs.mupped.core.generation;

import com.github.jetlibs.mupped.annotations.MapTo;
import com.github.jetlibs.mupped.interfaces.Mapper;
import com.squareup.javapoet.*;
import com.sun.tools.javac.code.Attribute;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.type.MirroredTypeException;
import javax.tools.Diagnostic;
import java.io.IOException;
import java.lang.reflect.Type;
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
        for (Element element : roundEnv.getElementsAnnotatedWith(MapTo.class)) {
            Element fromClass = element;
            List<Attribute> toClasses = Collections.EMPTY_LIST;

            List<? extends AnnotationMirror> annotation = element.getAnnotationMirrors();
            for (AnnotationMirror annotationMirror : annotation) {
                if (annotationMirror.getAnnotationType().toString().equals(MapTo.class.getCanonicalName())) {
                    for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : annotationMirror.getElementValues().entrySet())
                        if ("value".equals(entry.getKey().getSimpleName().toString())) {
                            toClasses =  (List<Attribute>)entry.getValue().getValue();
                            break;
                        }
                }
            }

            for(Attribute toClass : toClasses){
                String fromClassName = element.getSimpleName().toString();
                String toClassName =  "B";
                String mapperName = String.format("%sto%sMapper", fromClassName, toClassName);


                TypeSpec.Builder navigatorClass = TypeSpec
                        .classBuilder(mapperName)
                        .addSuperinterface(ParameterizedTypeName.get(ClassName.get(Mapper.class), TypeName.get(element.asType()), ClassName.get(toClass.type)))
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



        }

        return false;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        final Set<String> annotataions = new LinkedHashSet<>();
        annotataions.add(MapTo.class.getCanonicalName());
        return annotataions;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

}
