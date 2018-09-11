package test;

import core.annotations.MapTo;

@MapTo(clazz = {B.class, C.class})
public class A {
    private final int integer;
    private final String string;

    public A(int integer, String string) {
        this.integer = integer;
        this.string = string;
    }
}
