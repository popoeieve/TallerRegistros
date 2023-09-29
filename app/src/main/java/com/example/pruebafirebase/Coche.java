package com.example.pruebafirebase;

public class Coche {
    private String nombre,matricula,fechaCompra,piezasPreITV,piezasPostITV;
    private boolean esComprado,esDiagnosisPreITV,pedirRespuestospreITV,pedirRepuestosPostITV,tieneChapaYPintura, tieneMecanicaPostITV,esMecanicaPreITV,tieneCitaITV,haPasadoITV,esLimpiado,tieneFotosVenta,esAnuncioSubido,esVendido;
    private double precioCompra;

    public Coche() {
        // Constructor vacío necesario para Firebase
    }

    public Coche(String matricula, String nombre) {
        this.nombre = nombre;
        this.matricula = matricula;
    }

    public Coche(String nombre, String matricula, String fechaCompra, String piezasPreITV, String piezasPostITV, boolean esComprado, boolean esDiagnosisPreITV, boolean pedirRespuestospreITV, boolean pedirRepuestosPostITV, boolean tieneChapaYPintura, boolean tieneMecanicaPostITV, boolean esMecanicaPreITV, boolean tieneCitaITV, boolean haPasadoITV, boolean esLimpiado, boolean tieneFotosVenta, boolean esAnuncioSubido, boolean esVendido, double precioCompra) {
        this.nombre = nombre;
        this.matricula = matricula;
        this.fechaCompra = fechaCompra;
        this.piezasPreITV = piezasPreITV;
        this.piezasPostITV = piezasPostITV;
        this.esComprado = esComprado;
        this.esDiagnosisPreITV = esDiagnosisPreITV;
        this.pedirRespuestospreITV = pedirRespuestospreITV;
        this.pedirRepuestosPostITV = pedirRepuestosPostITV;
        this.tieneChapaYPintura = tieneChapaYPintura;
        this.tieneMecanicaPostITV = tieneMecanicaPostITV;
        this.esMecanicaPreITV = esMecanicaPreITV;
        this.tieneCitaITV = tieneCitaITV;
        this.haPasadoITV = haPasadoITV;
        this.esLimpiado = esLimpiado;
        this.tieneFotosVenta = tieneFotosVenta;
        this.esAnuncioSubido = esAnuncioSubido;
        this.esVendido = esVendido;
        this.precioCompra = precioCompra;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public String getFechaCompra() {
        return fechaCompra;
    }

    public void setFechaCompra(String fechaCompra) {
        this.fechaCompra = fechaCompra;
    }

    public String getPiezasPreITV() {
        return piezasPreITV;
    }

    public void setPiezasPreITV(String piezasPreITV) {
        this.piezasPreITV = piezasPreITV;
    }

    public String getPiezasPostITV() {
        return piezasPostITV;
    }

    public void setPiezasPostITV(String piezasPostITV) {
        this.piezasPostITV = piezasPostITV;
    }

    public boolean isEsComprado() {
        return esComprado;
    }

    public void setEsComprado(boolean esComprado) {
        this.esComprado = esComprado;
    }

    public boolean isEsDiagnosisPreITV() {
        return esDiagnosisPreITV;
    }

    public void setEsDiagnosisPreITV(boolean esDiagnosisPreITV) {
        this.esDiagnosisPreITV = esDiagnosisPreITV;
    }

    public boolean isPedirRespuestospreITV() {
        return pedirRespuestospreITV;
    }

    public void setPedirRespuestospreITV(boolean pedirRespuestospreITV) {
        this.pedirRespuestospreITV = pedirRespuestospreITV;
    }

    public boolean isPedirRepuestosPostITV() {
        return pedirRepuestosPostITV;
    }

    public void setPedirRepuestosPostITV(boolean pedirRepuestosPostITV) {
        this.pedirRepuestosPostITV = pedirRepuestosPostITV;
    }

    public boolean isTieneChapaYPintura() {
        return tieneChapaYPintura;
    }

    public void setTieneChapaYPintura(boolean tieneChapaYPintura) {
        this.tieneChapaYPintura = tieneChapaYPintura;
    }

    public boolean isTieneMecanicaPostITV() {
        return tieneMecanicaPostITV;
    }

    public void setTieneMecanicaPostITV(boolean tieneMecanicaPostITV) {
        this.tieneMecanicaPostITV = tieneMecanicaPostITV;
    }

    public boolean isEsMecanicaPreITV() {
        return esMecanicaPreITV;
    }

    public void setEsMecanicaPreITV(boolean esMecanicaPreITV) {
        this.esMecanicaPreITV = esMecanicaPreITV;
    }

    public boolean isTieneCitaITV() {
        return tieneCitaITV;
    }

    public void setTieneCitaITV(boolean tieneCitaITV) {
        this.tieneCitaITV = tieneCitaITV;
    }

    public boolean isHaPasadoITV() {
        return haPasadoITV;
    }

    public void setHaPasadoITV(boolean haPasadoITV) {
        this.haPasadoITV = haPasadoITV;
    }

    public boolean isEsLimpiado() {
        return esLimpiado;
    }

    public void setEsLimpiado(boolean esLimpiado) {
        this.esLimpiado = esLimpiado;
    }

    public boolean isTieneFotosVenta() {
        return tieneFotosVenta;
    }

    public void setTieneFotosVenta(boolean tieneFotosVenta) {
        this.tieneFotosVenta = tieneFotosVenta;
    }

    public boolean isEsAnuncioSubido() {
        return esAnuncioSubido;
    }

    public void setEsAnuncioSubido(boolean esAnuncioSubido) {
        this.esAnuncioSubido = esAnuncioSubido;
    }

    public boolean isEsVendido() {
        return esVendido;
    }

    public void setEsVendido(boolean esVendido) {
        this.esVendido = esVendido;
    }

    public double getPrecioCompra() {
        return precioCompra;
    }

    public void setPrecioCompra(double precioCompra) {
        this.precioCompra = precioCompra;
    }
}