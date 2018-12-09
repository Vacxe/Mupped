package com.github.jetlibs.mupped.test;

import com.github.jetlibs.mupped.annotations.MapFrom;

public class C {
    private final int integer;

    private final String string;

    @MapFrom("A")
    public C(int integer, String string) {
        this.integer = integer;
        this.string = string;
    }
}