package com.github.vacxe.mupped.test;

import com.github.vacxe.mupped.annotations.Mappable;
import com.github.vacxe.mupped.annotations.MappableConstructor;

@Mappable
public class B {
    private final int integer;
    private final String string;

    @MappableConstructor
    public B(int integer, String string) {
        this.integer = integer;
        this.string = string;
    }
}
