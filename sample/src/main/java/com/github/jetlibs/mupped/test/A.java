package com.github.jetlibs.mupped.test;

import com.github.jetlibs.mupped.annotations.MapTo;
import com.github.jetlibs.mupped.annotations.ParameterLink;

@MapTo({"B", "C"})
public class A {
   @ParameterLink("integer")
    private final int integer;

    private final String string;

    public A(int integer, String string) {
        this.integer = integer;
        this.string = string;
    }
}
