package com.quijotelui.ws.util;

public enum TipoComprobanteEnum {
    FACTURA("01", "FACTURA"), NOTA_DE_CREDITO("04", "NOTA DE CREDITO"), NOTA_DE_DEBITO("05", "NOTA DE DEBITO"), GUIA_DE_REMISION("06", "GUIA DE REMISION"), COMPROBANTE_DE_RETENCION("07", "COMPROBANTE DE RETENCION");

    private String codigo;
    private String descripcion;

    private TipoComprobanteEnum(String code, String descripcion) {
        this.codigo = code;

        this.descripcion = descripcion;
    }

    public String getCodigo() {
        return this.codigo;
    }

    public String getDescripcion() {
        return this.descripcion;
    }

}
