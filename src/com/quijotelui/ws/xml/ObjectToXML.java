package com.quijotelui.ws.xml;

import java.io.IOException;
import java.io.StringWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

public class ObjectToXML {

    public static byte[] convierteEnXml(Object comprobante) {
        try {
            StringWriter xmlComprobante = new StringWriter();
            JAXBContext context = JAXBContext.newInstance(new Class[]{comprobante.getClass()});
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty("jaxb.formatted.output", Boolean.valueOf(true));
            marshaller.marshal(comprobante, xmlComprobante);
            xmlComprobante.close();
            return xmlComprobante.toString().getBytes("UTF-8");
        } catch (IOException | JAXBException | ClassCastException ex) {
            Logger.getLogger(ObjectToXML.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Se produjo un error al convetir el archivo al formato XML");
            StringWriter xmlError = new StringWriter();                        
            xmlError.write("Se produjo un error al convetir el archivo al formato XML");            
            return xmlError.toString().getBytes();
        }
    }
}
