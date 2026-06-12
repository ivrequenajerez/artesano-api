package com.artesano.sharedkernel.domain;

import java.math.BigDecimal;
import java.util.Objects;

public final class Porcentaje {

    private final BigDecimal valor;

    private Porcentaje(BigDecimal valor) {
        if (valor == null) {
            throw new IllegalArgumentException("El porcentaje no puede ser null");
        }

        this.valor = normalizar(valor);
    }

    public static Porcentaje de(BigDecimal valor) {
        return new Porcentaje(valor);
    }

    public static Porcentaje de(String valor) {
        return new Porcentaje(new BigDecimal(valor));
    }

    public static Porcentaje cero() {
        return new Porcentaje(BigDecimal.ZERO);
    }

    public BigDecimal valor() {
        return valor;
    }

    public BigDecimal comoFactor() {
        return valor.movePointLeft(2);
    }

    public BigDecimal aplicarA(BigDecimal base) {
        if (base == null) {
            throw new IllegalArgumentException("La base no puede ser null");
        }

        return base.multiply(comoFactor());
    }

    public Porcentaje sumar(Porcentaje otro) {
        if (otro == null) {
            throw new IllegalArgumentException("El porcentaje a sumar no puede ser null");
        }

        return new Porcentaje(this.valor.add(otro.valor));
    }

    public Porcentaje restar(Porcentaje otro) {
        if (otro == null) {
            throw new IllegalArgumentException("El porcentaje a restar no puede ser null");
        }

        return new Porcentaje(this.valor.subtract(otro.valor));
    }

    public boolean esCero() {
        return valor.compareTo(BigDecimal.ZERO) == 0;
    }

    public boolean esNegativo() {
        return valor.compareTo(BigDecimal.ZERO) < 0;
    }

    private static BigDecimal normalizar(BigDecimal valor) {
        return valor.stripTrailingZeros();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Porcentaje that)) {
            return false;
        }

        return valor.compareTo(that.valor) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(valor.stripTrailingZeros());
    }

    @Override
    public String toString() {
        return valor.toPlainString() + "%";
    }
}