package com.github.jetlibs.mupped.test;


//import com.github.jetlibs.mupped.annotations.ParameterLink;

import com.github.jetlibs.mupped.annotations.ParameterLink;

public class C {
    @ParameterLink("integer")
    private final int anyinteger;

    private final String string;

    public C(int anyinteger, String string) {
        this.anyinteger = anyinteger;
        this.string = string;
    }
}