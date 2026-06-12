package com.artesano.sharedkernel.domain;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Currency;
import java.util.Objects;

public final class Dinero {

    // Variables
    private static final MathContext CONTEXTO_CALCULO = MathContext.DECIMAL128;
    private static final Currency EUR = Currency.getInstance("EUR");
    private final Currency moneda;
    private final BigDecimal importe;

    // Constructores
    private Dinero(BigDecimal importe, Currency moneda) {
        if (importe == null) {
            throw new IllegalArgumentException("El importe no puede ser null");
        }
        if (moneda == null) {
            throw new IllegalArgumentException("La moneda no puede ser null");
        }

        this.importe = normalizar(importe);
        this.moneda = moneda;
    }

    public static Dinero of(BigDecimal importe, Currency moneda) {
        return new Dinero(importe, moneda);
    }

    public static Dinero of(String importe, Currency moneda) {
        return new Dinero(new BigDecimal(importe), moneda);
    }

    public static Dinero euros(BigDecimal importe) {
        return new Dinero(importe, EUR);
    }

    public static Dinero euros(String importe) {
        return new Dinero(new BigDecimal(importe), EUR);
    }

    // Métodos
    public Dinero sumar(Dinero otro) {
        validarMismaMoneda(otro);
        return new Dinero(this.importe.add(otro.importe), this.moneda);
    }

    public Dinero restar(Dinero otro) {
        validarMismaMoneda(otro);
        return new Dinero(this.importe.subtract(otro.importe), this.moneda);
    }

    public Dinero multiplicar(BigDecimal factor) {
        if (factor == null) {
            throw new IllegalArgumentException("El factor no puede ser null");
        }

        return new Dinero(this.importe.multiply(factor), this.moneda);
    }

    public Dinero dividir(BigDecimal divisor) {
        if (divisor == null) {
            throw new IllegalArgumentException("El divisor no puede ser null");
        }
        if (divisor.compareTo(BigDecimal.ZERO) == 0) {
            throw new IllegalArgumentException("El divisor no puede ser cero");
        }

        return new Dinero(this.importe.divide(divisor, CONTEXTO_CALCULO), this.moneda);
    }

    public boolean esCero() {
        return importe.compareTo(BigDecimal.ZERO) == 0;
    }

    public boolean esNegativo() {
        return importe.compareTo(BigDecimal.ZERO) < 0;
    }

    private void validarMismaMoneda(Dinero otro) {
        if (otro == null) {
            throw new IllegalArgumentException("El dinero a operar no puede ser null");
        }

        if (!this.moneda.equals(otro.moneda)) {
            throw new IllegalArgumentException("No se pueden operar importes con monedas distintas");
        }
    }

    private static BigDecimal normalizar(BigDecimal valor) {
        return valor.stripTrailingZeros();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Dinero dinero)) {
            return false;
        }

        return importe.compareTo(dinero.importe) == 0 && moneda.equals(dinero.moneda);
    }

    @Override
    public int hashCode() {
        return Objects.hash(importe.stripTrailingZeros(), moneda);
    }

    @Override
    public String toString() {
        return importe.toPlainString() + " " + moneda.getCurrencyCode();
    }

    public BigDecimal importe() {
        return importe;
    }

    public Currency moneda() {
        return moneda;
    }
}
