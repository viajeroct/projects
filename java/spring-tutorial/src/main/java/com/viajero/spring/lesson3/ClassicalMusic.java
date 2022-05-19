package com.viajero.spring.lesson3;

import org.springframework.stereotype.Component;

@Component
public class ClassicalMusic implements Music {
    @Override
    public String getSong() {
        return "Classical Song #1";
    }
}
