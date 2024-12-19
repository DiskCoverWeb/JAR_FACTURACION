package javclientsri;

import com.quijotelui.ws.definicion.AutorizacionEstado;
import com.quijotelui.ws.definicion.Estado;
import com.quijotelui.ws.util.ArchivoUtils;
import com.quijotelui.ws.util.AutorizacionComprobantesUtil;
import com.quijotelui.ws.util.AutorizacionComprobantesWs;
import com.quijotelui.ws.xml.LectorXMLPath;
import ec.gob.sri.comprobantes.ws.aut.Autorizacion;
import ec.gob.sri.comprobantes.ws.aut.RespuestaComprobante;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.xpath.XPathConstants;

/**
 *
 * @author jorgequiguango
 */
public class Comprobar {

    String archivoEnviado;
    String destinoAutorizado;
    String destinoNoAutorizado;
    String direccionWebService;
    String Resp[]; 

    /*
    Esté constructor se usa cuando se dispone del archivo enviado
    */
    public Comprobar(String archivoEnviado, String destinoAutorizado, String destinoNoAutorizado, String direccionWebService) {
        this.archivoEnviado = archivoEnviado;
        this.destinoAutorizado = destinoAutorizado;
        this.destinoNoAutorizado = destinoNoAutorizado;
        /*
        *Web Service de Pruevas
        *Recepción
        *https://celcer.sri.gob.ec/comprobantes-electronicos-ws/RecepcionComprobantesOffline?wsdl
        *
        *Autorización
        *https://celcer.sri.gob.ec/comprobantes-electronicos-ws/AutorizacionComprobantesOffline?wsdl
         */
        this.direccionWebService = direccionWebService;
    }

    /*
    Esté constructor se usa cuando se dispone solo de la clave de acceso
    */
    public Comprobar(String destinoAutorizado, String destinoNoAutorizado, String direccionWebService) {
        this.destinoAutorizado = destinoAutorizado;
        this.destinoNoAutorizado = destinoNoAutorizado;
        /*
        *Web Service de Pruevas
        *Recepción
        *https://celcer.sri.gob.ec/comprobantes-electronicos-ws/RecepcionComprobantesOffline?wsdl
        *
        *Autorización
        *https://celcer.sri.gob.ec/comprobantes-electronicos-ws/AutorizacionComprobantesOffline?wsdl
         */
        this.direccionWebService = direccionWebService;
    }
    
    /*
    Esté constructor se usa cuando se dispone del archivo enviado
    */
    public AutorizacionEstado executeComprobar() {
        RespuestaComprobante respuestaComprobante = null;
        AutorizacionEstado autorizacionEstado = new AutorizacionEstado(new Autorizacion(), Estado.NO_AUTORIZADO);

        try {

            File archivoXMLEnviado = new File(this.archivoEnviado);
            String nombreArchivo = archivoXMLEnviado.getName();

            byte[] enviado = ArchivoUtils.archivoToByte(archivoXMLEnviado);

            LectorXMLPath lectorXMLPath = new LectorXMLPath(enviado, XPathConstants.STRING);
            String claveAccesoComprobante = lectorXMLPath.getClaveAcceso();
            String codDoc = lectorXMLPath.getCodDoc();
            String tipoComprobante = codDoc.substring(1);
            if ((tipoComprobante != null) && (claveAccesoComprobante != null)) {
                try {
                    respuestaComprobante = AutorizacionComprobantesWs.autorizarComprobante(claveAccesoComprobante, this.direccionWebService);
                    if (!respuestaComprobante.getAutorizaciones().getAutorizacion().isEmpty()) {
                        AutorizacionComprobantesUtil autorizacionComprobantesUtil = new AutorizacionComprobantesUtil(respuestaComprobante, nombreArchivo);
                        autorizacionEstado = autorizacionComprobantesUtil.obtenerEstadoAutorizaccion();
                        autorizacionComprobantesUtil.validarRespuestaAutorizacion(autorizacionEstado,
                                this.destinoAutorizado,
                                this.destinoNoAutorizado);
                        System.out.println(nombreArchivo + ArchivoUtils.obtieneTipoDeComprobante(claveAccesoComprobante) + autorizacionEstado.getEstadoAutorizacion().getDescripcion());
                    } else {
                        System.out.println(nombreArchivo + ArchivoUtils.obtieneTipoDeComprobante(claveAccesoComprobante) + Estado.NO_PROCESADO.getDescripcion() + "El archivo no tiene autorizaciones relacionadas");
                    }
                } catch (Exception ex) {
                    if (Estado.NO_AUTORIZADO.equals(autorizacionEstado.getEstadoAutorizacion())) {
                        System.out.println("El comprobante no está Autorizado");
                    }
                    System.out.println(nombreArchivo + ArchivoUtils.obtieneTipoDeComprobante(claveAccesoComprobante) + autorizacionEstado.getEstadoAutorizacion().getDescripcion() + ex.getMessage());
                }
            } else {
                System.out.println("La información <codDoc> y <claveAcceso> son obligatorias para la comprobación de un archivo");
                System.out.println("Error al tratar de enviar el comprobante hacia el SRI: Se ha producido un error ");
            }
        } catch (IOException ex) {
            Logger.getLogger(Comprobar.class.getName()).log(Level.SEVERE, null, ex);
        }
        return autorizacionEstado;

    }
    /*
    Está función se usa cuando se dispone solo de la clave de acceso
    */
    public String[] executeComprobar(String claveAccesoComprobante) {
        Resp = new String[5];
        Resp[0] = "1";
        Resp[1] = claveAccesoComprobante;
        Resp[4]= ArchivoUtils.obtieneTipoDeComprobante(claveAccesoComprobante);
        RespuestaComprobante respuestaComprobante = null;
        AutorizacionEstado autorizacionEstado = new AutorizacionEstado(new Autorizacion(), Estado.NO_AUTORIZADO);

        if ((claveAccesoComprobante != null)) {
            try {
                respuestaComprobante = AutorizacionComprobantesWs.autorizarComprobante(claveAccesoComprobante, this.direccionWebService);
                if (!respuestaComprobante.getAutorizaciones().getAutorizacion().isEmpty()) {
                    AutorizacionComprobantesUtil autorizacionComprobantesUtil = new AutorizacionComprobantesUtil(respuestaComprobante, claveAccesoComprobante + ".xml");
                    autorizacionEstado = autorizacionComprobantesUtil.obtenerEstadoAutorizaccion();
                    String[] RespAut = autorizacionComprobantesUtil.validarRespuestaAutorizacion(autorizacionEstado,
                            this.destinoAutorizado,
                            this.destinoNoAutorizado);
                    Resp[0] = RespAut[0];
                    Resp[2]= RespAut[1];
                    Resp[3]= RespAut[2];
                   // System.out.println(claveAccesoComprobante + " " + ArchivoUtils.obtieneTipoDeComprobante(claveAccesoComprobante) + " " + autorizacionEstado.getEstadoAutorizacion().getDescripcion());
                } else {
                    
                    Resp[0] = "0";
                    Resp[2]= "NO PROCESADO";
                    Resp[3]= "El archivo no tiene autorizaciones relacionadas";
                    
                //    System.out.println(claveAccesoComprobante + " " + ArchivoUtils.obtieneTipoDeComprobante(claveAccesoComprobante) + " " + Estado.NO_PROCESADO.getDescripcion() + "El archivo no tiene autorizaciones relacionadas");
                }
            } catch (Exception ex) {
                if (Estado.NO_AUTORIZADO.equals(autorizacionEstado.getEstadoAutorizacion())) {
                    Resp[0] = "0";
                    Resp[2]= "NO AUTORIZADO";
                    Resp[3]= "El comprobante no está Autorizado";
                   // System.out.println("El comprobante no está Autorizado");
                }
                //System.out.println(claveAccesoComprobante + ArchivoUtils.obtieneTipoDeComprobante(claveAccesoComprobante) + autorizacionEstado.getEstadoAutorizacion().getDescripcion() + ex.getMessage());
            }
        } else {
            System.out.println("La información <codDoc> y <claveAcceso> son obligatorias para la comprobación de un archivo");
            System.out.println("Error al tratar de enviar el comprobante hacia el SRI: Se ha producido un error ");
        }
        //return autorizacionEstado;
        return Resp;
    }

}
