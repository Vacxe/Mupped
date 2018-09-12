package com.github.jetlibs.mapped.core.generation;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import java.util.Set;

public class MappedAnotationProcessor extends AbstractProcessor {
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (Element element : roundEnv.getElementsAnnotatedWith(core.annotations.MapTo.class)) {
            if (element.getKind() != ElementKind.CLASS) {
                return true;
            }
            System.out.print(element.getSimpleName().toString());
        }
        return false;
    }
}
