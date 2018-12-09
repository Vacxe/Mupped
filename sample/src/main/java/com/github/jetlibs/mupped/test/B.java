package com.github.jetlibs.mupped.test;

import com.github.jetlibs.mupped.annotations.MapFrom;

public class B {
    private final int integer;

    private final String string;

    @MapFrom("A")
    public B(int integer, String string) {
        this.integer = integer;
        this.string = string;
    }
}
