package com.github.jetlibs.mupped.interfaces;

public interface Mapper<INPUT,  OUTPUT> {
    OUTPUT map(INPUT input);
}
