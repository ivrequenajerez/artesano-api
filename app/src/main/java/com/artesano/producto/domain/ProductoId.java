package com.artesano.producto.domain;

import java.util.Objects;
import java.util.UUID;

public final class ProductoId {

    private final UUID value;

    private ProductoId(UUID value) {
        this.value = Objects.requireNonNull(value, "El id del producto no puede ser nulo");
    }

    public static ProductoId generar() {
        return new ProductoId(UUID.randomUUID());
    }

    public static ProductoId de(UUID value) {
        return new ProductoId(value);
    }

    public static ProductoId de(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("El id del producto no puede estar vacío");
        }

        return new ProductoId(UUID.fromString(value));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProductoId that)) return false;
        return value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public String toString() {
        return value.toString();
    }
}