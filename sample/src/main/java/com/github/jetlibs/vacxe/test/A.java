package com.github.vacxe.mupped.test;

import com.github.vacxe.mupped.annotations.MapTo;

@MapTo({"B", "C"})
public class A {
    public final int integer;
    public final String string;

    public A(int integer, String string) {
        this.integer = integer;
        this.string = string;
    }
}
