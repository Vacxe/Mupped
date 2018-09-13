package com.github.jetlibs.mupped.core.generation;

import com.github.jetlibs.mupped.annotations.MapTo;
import com.google.auto.service.AutoService;

import javax.annotation.processing.*;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import java.util.LinkedHashSet;
import java.util.Set;


//@SupportedSourceVersion(SourceVersion.RELEASE_6)
@AutoService(Processor.class)
public class MuppedAnnotationProcessor extends AbstractProcessor {


    private Messager messager;


    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        messager = processingEnv.getMessager();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
  //      throw new RuntimeException("Blyat'");
        for (Element element : roundEnv.getElementsAnnotatedWith(MapTo.class)) {
            if (element.getKind() != ElementKind.CLASS) {
              //  messager.printMessage(Diagnostic.Kind.NOTE, element.getSimpleName().toString());
                return true;
            }
        }

        return false;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> annotataions = new LinkedHashSet<>();
        annotataions.add(MapTo.class.getCanonicalName());
        return annotataions;
    }

//    @Override public SourceVersion getSupportedSourceVersion() {
//        return SourceVersion.latestSupported();
//    }
}
