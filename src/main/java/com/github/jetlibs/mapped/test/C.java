package test;

import core.annotations.ParameterLink;

public class C {
    @ParameterLink("integer")
    private final int anyinteger;

    private final String string;

    public C(int anyinteger, String string) {
        this.anyinteger = anyinteger;
        this.string = string;
    }
}