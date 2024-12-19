package com.quijotelui.ws.xml;

import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.datatype.DatatypeFactory;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RespuestaDateConverter implements Converter {

    public static final SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SSS");

    @Override
    public boolean canConvert(Class clazz) {
        return clazz.equals(XMLGregorianCalendar.class);
    }

    @Override
    public void marshal(Object o, HierarchicalStreamWriter writer, MarshallingContext mc) {
        XMLGregorianCalendar i = (XMLGregorianCalendar) o;
        writer.setValue(dateTimeFormat.format(i.toGregorianCalendar().getTime()));
    }

    @Override
    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext uc) {
        Date date = null;
        try {
            date = dateTimeFormat.parse(reader.getValue());
        } catch (ParseException ex) {
            Logger.getLogger(RespuestaDateConverter.class.getName()).log(Level.SEVERE, null, ex);
        }
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(date);

        try {
            // Usando DatatypeFactory para crear un XMLGregorianCalendar
            DatatypeFactory datatypeFactory = DatatypeFactory.newInstance();
            return datatypeFactory.newXMLGregorianCalendar(cal);
        } catch (Exception e) {
            Logger.getLogger(RespuestaDateConverter.class.getName()).log(Level.SEVERE, null, e);
        }
        return null;
    }
}
