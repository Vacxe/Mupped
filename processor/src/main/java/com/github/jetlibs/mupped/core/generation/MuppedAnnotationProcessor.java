package com.github.jetlibs.mupped.core.generation;

import com.github.jetlibs.mupped.annotations.MapTo;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.util.LinkedHashSet;
import java.util.Set;

public class MuppedAnnotationProcessor extends AbstractProcessor {

    private Messager messager;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        messager = processingEnv.getMessager();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (Element element : roundEnv.getElementsAnnotatedWith(MapTo.class)) {
            messager.printMessage(Diagnostic.Kind.NOTE, "Found class: " + element.getSimpleName().toString());
            if (element.getKind() != ElementKind.CLASS) {
                return true;
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

    @Override public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }
}
