package com.quijotelui.ws.util;

import ec.gob.sri.comprobantes.ws.Comprobante;
import ec.gob.sri.comprobantes.ws.Mensaje;
import ec.gob.sri.comprobantes.ws.RecepcionComprobantesOffline;
import ec.gob.sri.comprobantes.ws.RecepcionComprobantesOfflineService;
import ec.gob.sri.comprobantes.ws.RespuestaSolicitud;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.namespace.QName;
import javax.xml.ws.WebServiceException;

public class EnvioComprobantesWs {

    private static RecepcionComprobantesOfflineService service;

    public EnvioComprobantesWs(String wsdlLocation)
            throws MalformedURLException, WebServiceException {
        try {
            URL url = new URL(wsdlLocation);
            QName qname = new QName("http://ec.gob.sri.ws.recepcion", "RecepcionComprobantesOfflineService");
            service = new RecepcionComprobantesOfflineService(url, qname);
        } catch (WebServiceException ex) {
            Logger.getLogger(EnvioComprobantesWs.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public RespuestaSolicitud enviarComprobante(File xmlFile) {
        RespuestaSolicitud response = null;
        try {
            RecepcionComprobantesOffline port = service.getRecepcionComprobantesOfflinePort();
            response = port.validarComprobante(ArchivoUtils.archivoToByte(xmlFile));
        } catch (IOException | WebServiceException e) {
            Logger.getLogger(EnvioComprobantesWs.class.getName()).log(Level.SEVERE, null, e);
            response = new RespuestaSolicitud();
            response.setEstado(e.getMessage());
            return response;
        }
        return response;
    }

    public static RespuestaSolicitud obtenerRespuestaEnvio(File archivo, String urlWsdl) {
        RespuestaSolicitud respuesta = new RespuestaSolicitud();
        EnvioComprobantesWs cliente = null;
        try {
            cliente = new EnvioComprobantesWs(urlWsdl);
        } catch (MalformedURLException | WebServiceException ex) {
            Logger.getLogger(EnvioComprobantesWs.class.getName()).log(Level.SEVERE, null, ex);
            respuesta.setEstado(ex.getMessage());
            return respuesta;
        }
        respuesta = cliente.enviarComprobante(archivo);

        return respuesta;
    }
}
