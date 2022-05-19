package com.viajero.spring.lesson3;

import org.springframework.stereotype.Component;

@Component("rockMusic")
public class RockMusic implements Music {
    @Override
    public String getSong() {
        return "Rock Song #1";
    }
}
