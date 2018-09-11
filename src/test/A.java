package test;

import core.annotations.MapTo;
import core.annotations.ParameterLink;

@MapTo({B.class, C.class})
public class A {
    @ParameterLink("integer")
    private final int integer;

    private final String string;

    public A(int integer, String string) {
        this.integer = integer;
        this.string = string;
    }
}
