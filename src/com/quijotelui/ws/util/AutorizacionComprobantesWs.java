package com.quijotelui.ws.util;

import ec.gob.sri.comprobantes.ws.aut.AutorizacionComprobantesOffline;
import ec.gob.sri.comprobantes.ws.aut.AutorizacionComprobantesOfflineService;
import ec.gob.sri.comprobantes.ws.aut.RespuestaComprobante;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.namespace.QName;

public class AutorizacionComprobantesWs {

    private AutorizacionComprobantesOfflineService service;
    public static final String ESTADO_AUTORIZADO = "AUTORIZADO";
    public static final String ESTADO_EN_PROCESO = "EN PROCESO";
    public static final String ESTADO_NO_AUTORIZADO = "NO AUTORIZADO";

    public AutorizacionComprobantesWs(String wsdlLocation) {
        try {
            this.service = new AutorizacionComprobantesOfflineService(new URL(wsdlLocation), new QName("http://ec.gob.sri.ws.autorizacion", "AutorizacionComprobantesOfflineService"));
        } catch (Exception ex) {
            Logger.getLogger(AutorizacionComprobantesWs.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println(ex.getMessage() + " Se ha producido un error ");
        }
    }

    public RespuestaComprobante llamadaWSAutorizacionInd(String claveDeAcceso) {
        RespuestaComprobante response = null;
        try {
            AutorizacionComprobantesOffline port = this.service.getAutorizacionComprobantesOfflinePort();
            response = port.autorizacionComprobante(claveDeAcceso);
        } catch (Exception e) {
            Logger.getLogger(AutorizacionComprobantesWs.class.getName()).log(Level.SEVERE, null, e);
            return response;
        }
        return response;
    }

    public static RespuestaComprobante autorizarComprobante(String claveDeAcceso, String direccionWS) {
        return new AutorizacionComprobantesWs(direccionWS).llamadaWSAutorizacionInd(claveDeAcceso);
    }
}
