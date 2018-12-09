package com.github.vacxe.mupped.interfaces;

public interface Mapper<INPUT,  OUTPUT> {
    OUTPUT map(INPUT input);
}
