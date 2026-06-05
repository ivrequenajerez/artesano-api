package com.artesano.api.sharedkernel.domain;

public enum UnidadMedida {

    GRAMO("g"), KILOGRAMO("kg"), MILILITRO("ml"), LITRO("l"), UNIDAD("ud");

    private final String simbolo;

    UnidadMedida(String simbolo) {
        this.simbolo = simbolo;
    }

    public String simbolo() {
        return simbolo;
    }
}