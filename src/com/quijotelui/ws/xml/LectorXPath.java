package com.quijotelui.ws.xml;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class LectorXPath
{
  private String xmlFile;
  private Document xmlDocument;
  private XPath xPath;
  
  public LectorXPath(String xmlFile)
  {
    this.xmlFile = xmlFile;
    inicializar();
  }
  
  private void inicializar()
  {
    try
    {
      this.xmlDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(this.xmlFile);
      
      this.xPath = XPathFactory.newInstance().newXPath();
    }
    catch (IOException | SAXException | ParserConfigurationException ex)
    {
      Logger.getLogger(LectorXPath.class.getName()).log(Level.SEVERE, null, ex);
    }
  }
}
