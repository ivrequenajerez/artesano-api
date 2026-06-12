package com.artesano.producto.domain;

import com.artesano.sharedkernel.domain.Dinero;

import java.util.Objects;

public final class Producto {

    private final ProductoId id;
    private String nombre;
    private Dinero precioVenta;
    private boolean activo;

    private Producto(
            ProductoId id,
            String nombre,
            Dinero precioVenta,
            boolean activo
    ) {
        this.id = Objects.requireNonNull(id, "El id del producto no puede ser nulo");
        this.nombre = validarNombre(nombre);
        this.precioVenta = Objects.requireNonNull(precioVenta, "El precio de venta no puede ser nulo");
        this.activo = activo;
    }

    public static Producto crear(
            String nombre,
            Dinero precioVenta
    ) {
        return new Producto(
                ProductoId.generar(),
                nombre,
                precioVenta,
                true
        );
    }

    public static Producto reconstruir(
            ProductoId id,
            String nombre,
            Dinero precioVenta,
            boolean activo
    ) {
        return new Producto(
                id,
                nombre,
                precioVenta,
                activo
        );
    }

    public ProductoId id() {
        return id;
    }

    public String nombre() {
        return nombre;
    }

    public Dinero precioVenta() {
        return precioVenta;
    }

    public boolean activo() {
        return activo;
    }

    public void renombrar(String nuevoNombre) {
        this.nombre = validarNombre(nuevoNombre);
    }

    public void cambiarPrecioVenta(Dinero nuevoPrecioVenta) {
        this.precioVenta = Objects.requireNonNull(
                nuevoPrecioVenta,
                "El nuevo precio de venta no puede ser nulo"
        );
    }

    public void activar() {
        this.activo = true;
    }

    public void desactivar() {
        this.activo = false;
    }

    private static String validarNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre del producto no puede estar vacío");
        }

        return nombre.trim();
    }
}