package com.quijotelui.ws.xml;

import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.datatype.DatatypeFactory;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.core.util.QuickWriter;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.xml.PrettyPrintWriter;
import com.thoughtworks.xstream.io.xml.XppDriver;
import ec.gob.sri.comprobantes.ws.aut.Autorizacion;
import ec.gob.sri.comprobantes.ws.aut.Mensaje;
import ec.gob.sri.comprobantes.ws.aut.RespuestaComprobante;
import java.io.Writer;

public class XStreamAutorizacion {

    public static XStream getRespuestaXStream() {
        XStream xstream = new XStream(new XppDriver() {
            public HierarchicalStreamWriter createWriter(Writer out) {
                return new PrettyPrintWriter(out) {
                    protected void writeText(QuickWriter writer, String text) {
                        writer.write(text);
                    }
                };
            }
        });
        xstream.alias("respuesta", RespuestaComprobante.class);
        xstream.alias("autorizacion", Autorizacion.class);
        xstream.alias("fechaAutorizacion", XMLGregorianCalendar.class);
        xstream.alias("mensaje", Mensaje.class);
        xstream.registerConverter(new RespuestaDateConverter());

        return xstream;
    }
}
