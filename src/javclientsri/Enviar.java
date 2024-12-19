package javclientsri;

import com.quijotelui.ws.definicion.AutorizacionEstado;
import com.quijotelui.ws.definicion.Estado;
import com.quijotelui.ws.util.ArchivoUtils;
import static com.quijotelui.ws.util.AutorizacionComprobantesUtil.obtieneMensajesAutorizacion;
import com.quijotelui.ws.util.EnvioComprobantesWs;
import ec.gob.sri.comprobantes.ws.RespuestaSolicitud;
import ec.gob.sri.comprobantes.ws.aut.Autorizacion;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.ws.WebServiceException;

public class Enviar {

    String archivoFirmado;
    String destinoEnviado;
    String destinoRechazado;
    String direccionWebService;
    String[] Resp;

    public Enviar(String archivoFirmado, String destinoEnviado, String destinoRechazado, String direccionWebService) {
        this.archivoFirmado = archivoFirmado;
        this.destinoEnviado = destinoEnviado;
        this.destinoRechazado = destinoRechazado;
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

    public String[] executeEnviar() {
        Resp = new String[4];
        RespuestaSolicitud respuestaSolicitudEnvio = new RespuestaSolicitud();

        try {


            File archivoXMLFirmadoFile = new File(this.archivoFirmado);
            String nombreArchivo = archivoXMLFirmadoFile.getName();
            byte[] archivoXMLFirmadoByte = ArchivoUtils.archivoToByte(archivoXMLFirmadoFile);

            respuestaSolicitudEnvio = EnvioComprobantesWs.obtenerRespuestaEnvio(archivoXMLFirmadoFile, this.direccionWebService);
            ArchivoUtils.validarRespuestaEnvio(respuestaSolicitudEnvio, archivoXMLFirmadoByte, nombreArchivo, this.destinoEnviado, this.destinoRechazado);
            //System.out.println(respuestaSolicitudEnvio.getEstado() + " " + "El comprobante fue enviado, está pendiente de autorización");
            String claveAcceso = nombreArchivo.substring(0,49);
            String Estado = respuestaSolicitudEnvio.getEstado();
            
            Resp[0]="1";            
            Resp[1] = respuestaSolicitudEnvio.getEstado();            
            Resp[2] = "El comprobante fue enviado, está pendiente de autorización";            
            Resp[3] = ArchivoUtils.obtieneTipoDeComprobante(claveAcceso);  
            if(Estado.equals("DEVUELTA"))
            {
                  String msj = respuestaSolicitudEnvio.getComprobantes().getComprobante().get(0).getMensajes().getMensaje().get(0).getTipo()+"-"+
                               respuestaSolicitudEnvio.getComprobantes().getComprobante().get(0).getMensajes().getMensaje().get(0).getMensaje()+"-"+
                               respuestaSolicitudEnvio.getComprobantes().getComprobante().get(0).getMensajes().getMensaje().get(0).getInformacionAdicional();      
                Resp[0]="0";                                
                Resp[2] = msj;
            }          
            //return respuestaSolicitudEnvio;            
            return Resp;

        } catch (IOException | WebServiceException ex) {
            Logger.getLogger(Enviar.class.getName()).log(Level.SEVERE, null, ex);
            RespuestaSolicitud respuestaError = new RespuestaSolicitud();
            respuestaError.setEstado("Fallo en la conexión con el web service del SRI");  
                       
            Resp[0]="-1";
            Resp[1] = respuestaSolicitudEnvio.getEstado();
            Resp[2] = "Fallo en la conexión con el web service del SRI";            
            Resp[3] = "";
            
            //return respuestaError;            
            return Resp;
        }
    }

}
