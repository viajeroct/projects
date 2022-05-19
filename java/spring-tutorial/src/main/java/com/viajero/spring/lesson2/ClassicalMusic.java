package com.viajero.spring.lesson2;

public class ClassicalMusic implements Music {
    // Restrict simple constructor.
    private ClassicalMusic() {
    }

    // Create fabric-method.
    public static ClassicalMusic getInstance() {
        System.err.println("\t--$> [fabric-method-init]");
        return new ClassicalMusic();
    }

    @Override
    public String getSong() {
        return "Classical Song #1";
    }

    public void initBean() {
        System.err.println("Initialization!");
    }

    public void destroyBean() {
        System.err.println("Destroying!");
    }
}
