package com.quijotelui.ws.util;

import com.quijotelui.ws.xml.ObjectToXML;
import ec.gob.sri.comprobantes.ws.RespuestaSolicitud;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.apache.commons.io.FileUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

public class ArchivoUtils {

    public static RespuestaSolicitud enviar(File archivoFirmado, String codDoc, String claveDeAcceso) {
        return EnvioComprobantesWs.obtenerRespuestaEnvio(archivoFirmado, devuelveUrlWs(/*emisor.getTipoAmbiente()*/"1", "RecepcionComprobantesOffline"));
    }

    public static void validarRespuestaEnvio(RespuestaSolicitud respuestaSolicitudEnvio, byte[] archivoFirmado, String nombreArchivo, String directorioDestino, String directorioRechazado) {
        if (respuestaSolicitudEnvio.getEstado().equals("RECIBIDA")) {
            try {
                crearArchivo(archivoFirmado, nombreArchivo, directorioDestino);
            } catch (Exception ex) {
                Logger.getLogger(ArchivoUtils.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else if (respuestaSolicitudEnvio.getEstado().equals("DEVUELTA")) {
            byte[] archivoRespuesta = addMotivosRechazo(respuestaSolicitudEnvio, archivoFirmado);
            crearArchivoDirectorioRechazados(archivoRespuesta, nombreArchivo, directorioRechazado);
           // System.out.println("Error al enviar el comprobante estado :  Revisar la carpeta de rechazados " + respuestaSolicitudEnvio.getEstado());
        }
    }

    public static byte[] addMotivosRechazo(RespuestaSolicitud respuestaRecepcion, byte[] comprobante) {
        try {
            byte[] respuetaRecepcionXML = ObjectToXML.convierteEnXml(respuestaRecepcion);
            Document document = mergeArchivos(comprobante, respuetaRecepcionXML);
            return adjuntarArchivo(document);
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(ArchivoUtils.class.getName()).log(Level.SEVERE, null, ex);

            System.out.println("Se produjo un error al adjuntar los resultados de la respuesta al comprobante enviado");
        }
        return null;
    }

    public static Document mergeArchivos(byte[] comprobante, byte[] respuesta) {
        return merge("*", new byte[][]{comprobante, respuesta});
    }

    public static byte[] adjuntarArchivo(Document document) {
        try {
            DOMSource source = new DOMSource(document);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            StreamResult result = new StreamResult(byteArrayOutputStream);
            TransformerFactory transFactory = TransformerFactory.newInstance();
            Transformer transformer = transFactory.newTransformer();
            transformer.transform(source, result);
            return byteArrayOutputStream.toByteArray();
        } catch (TransformerException ex) {
            java.util.logging.Logger.getLogger(ArchivoUtils.class.getName()).log(Level.SEVERE, null, ex);

            System.out.println("Se produjo un error al adjuntar los resultados de la respuesta al comprobante enviado");
        }
        return null;
    }

    private static Document merge(String exp, byte[]... archivosXML) {
        try {
            XPathFactory xPathFactory = XPathFactory.newInstance();
            XPath xpath = xPathFactory.newXPath();
            XPathExpression expression = xpath.compile(exp);
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            docBuilderFactory.setIgnoringElementContentWhitespace(true);
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(archivosXML[0]);
            Document base = docBuilder.parse(byteArrayInputStream);
            Node results = (Node) expression.evaluate(base, XPathConstants.NODE);
            for (int i = 1; i < archivosXML.length; i++) {
                ByteArrayInputStream byteArrayInputStreamMerge = new ByteArrayInputStream(archivosXML[i]);
                Document merge = docBuilder.parse(byteArrayInputStreamMerge);
                Node nextResults = (Node) expression.evaluate(merge, XPathConstants.NODE);
                results.appendChild(base.importNode(nextResults, true));
            }
            return base;
        } catch (XPathExpressionException | SAXException | IOException | ParserConfigurationException ex) {
            java.util.logging.Logger.getLogger(ArchivoUtils.class.getName()).log(Level.SEVERE, null, ex);

            System.out.println("Se produjo un error al adjuntar los resultados de la respuesta al comprobante enviado");
        }
        return null;
    }

    public static File crearArchivo(byte[] archivo, String nombreArchivo, String directorio) {
        try {

            File directorioDestino = new File(directorio);
            String rutaArchivoDestino = directorioDestino + File.separator + nombreArchivo;
            File archivoDestino = new File(rutaArchivoDestino);
            FileOutputStream fileOutputStream = new FileOutputStream(archivoDestino);
            fileOutputStream.write(archivo);
            fileOutputStream.close();
            return archivoDestino;
        } catch (IOException ex) {
            Logger.getLogger(ArchivoUtils.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Error al mover el archivo al directorio: " + directorio);
        }
        return null;
    }

    public static void crearArchivoDirectorioRechazados(byte[] archivo, String nombreArchivo, String directorio) {
        try {

            File directorioDestino = new File(directorio);
            String rutaDirectorioRechazados = directorioDestino + File.separator;
            File directorioRechazados = new File(rutaDirectorioRechazados);
            if (!directorioRechazados.exists()) {
                directorioRechazados.mkdir();
            }
            String rutaArchivoDestino = rutaDirectorioRechazados + File.separator + nombreArchivo;
            File archivoDestino = new File(rutaArchivoDestino);
            FileOutputStream fileOutputStream = new FileOutputStream(archivoDestino);
            if (fileOutputStream != null) {
                fileOutputStream.write(archivo);
            }
            fileOutputStream.close();
        } catch (IOException ex) {
            Logger.getLogger(ArchivoUtils.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Error al mover el archivo al directorio: ");
        }
    }

    public static byte[] archivoToByte(File file)
            throws IOException {
        String archivo = FileUtils.readFileToString(file, "UTF-8");
        return archivo.getBytes(Charset.forName("UTF-8"));
    }

    public static String devuelveUrlWs(String ambiente, String nombreServicio) {
        StringBuilder url = new StringBuilder();
        String direccionIPServicio = null;

        if (ambiente.equals("1") == true) {
            direccionIPServicio = "https://celcer.sri.gob.ec";
        } else if (ambiente.equals("2") == true) {
            direccionIPServicio = "https://celcer.sri.gob.ec";
        }
        url.append(direccionIPServicio);

        url.append("/comprobantes-electronicos-ws/");
        url.append(nombreServicio);
        url.append("?wsdl");
        System.out.println(url.toString());

        return url.toString();
    }

    public static String obtieneTipoDeComprobante(String claveDeAcceso) {
        String abreviatura = null;
        if ((claveDeAcceso != null) && (claveDeAcceso.length() == 49)) {
            String tipo = claveDeAcceso.substring(8, 10);
            if (tipo.equals(TipoComprobanteEnum.FACTURA.getCodigo())) {
                abreviatura = TipoComprobanteEnum.FACTURA.getDescripcion();
            } else if (tipo.equals(TipoComprobanteEnum.NOTA_DE_DEBITO.getCodigo())) {
                abreviatura = TipoComprobanteEnum.NOTA_DE_DEBITO.getDescripcion();
            } else if (tipo.equals(TipoComprobanteEnum.NOTA_DE_CREDITO.getCodigo())) {
                abreviatura = TipoComprobanteEnum.NOTA_DE_CREDITO.getDescripcion();
            } else if (tipo.equals(TipoComprobanteEnum.GUIA_DE_REMISION.getCodigo())) {
                abreviatura = TipoComprobanteEnum.GUIA_DE_REMISION.getDescripcion();
            } else if (tipo.equals(TipoComprobanteEnum.COMPROBANTE_DE_RETENCION.getCodigo())) {
                abreviatura = TipoComprobanteEnum.COMPROBANTE_DE_RETENCION.getDescripcion();
            }
        }
        return abreviatura;
    }
}
