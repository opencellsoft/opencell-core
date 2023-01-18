package org.meveo.service.validation;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.concurrent.CompletableFuture;

import javax.ejb.Stateless;
import javax.inject.Inject;

import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.ParamBeanFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

@Stateless
public class ValidationByNumberCountryService {
    
    @Inject private ParamBeanFactory paramBeanFactory;
    protected static Logger log = LoggerFactory.getLogger(ValidationByNumberCountryService.class);
    
    public boolean getValByValNbCountryCode(String valNb, String countryCode) {
        ParamBean instanceParamBean = paramBeanFactory.getInstance();
        
        boolean valueValideNodeBoolean = false;
        try {            
            HttpClient client = HttpClient.newHttpClient();
            String data = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:urn=\"urn:ec.europa.eu:taxud:vies:services:checkVat:types\">\r\n"
                    + "   <soapenv:Header/>\r\n"
                    + "   <soapenv:Body>\r\n"
                    + "      <urn:checkVat>\r\n"
                    + "         <urn:countryCode>" + countryCode + "</urn:countryCode>\r\n"
                    + "         <urn:vatNumber>" + valNb + "</urn:vatNumber>\r\n"
                    + "      </urn:checkVat>\r\n"
                    + "   </soapenv:Body>\r\n"
                    + "</soapenv:Envelope>";
            String uri = instanceParamBean.getProperty("checkVatService.api", "http://ec.europa.eu/taxation_customs/vies/services/checkVatService");
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(uri))
                    .POST(BodyPublishers.ofString(data))
                    .build();
            CompletableFuture<String> cf = client.sendAsync(request, BodyHandlers.ofString())
            .thenApply(HttpResponse::body);
            String responseStr = cf.get();
            valueValideNodeBoolean = parseXml(responseStr);
        } catch (Exception e) {
            log.error("getValByValNbCountryCode error ", e.getMessage());
            throw new BusinessException(e.getMessage());
        }
        
        return valueValideNodeBoolean;
    }
    
    private static Document convertStringToXMLDocument(String xmlString) 
    {
        //Parser that produces DOM object trees from XML content
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();       
        //API to obtain DOM Document instance
        DocumentBuilder builder = null;
        try
        {
            //Create DocumentBuilder with default configuration
            builder = factory.newDocumentBuilder();            
            //Parse the content to Document object
            Document doc = builder.parse(new InputSource(new StringReader(xmlString)));
            return doc;
        } 
        catch (Exception e) 
        {
            log.error("convertStringToXMLDocument error ", e.getMessage());
        }
        return null;
    }

    public boolean parseXml(String xmlStr) 
    {
      //Use method to convert XML string content to XML Document object
      Document doc = convertStringToXMLDocument(xmlStr);
      String valueValideNodeStr = ""; 

      NodeList docNodes = doc.getFirstChild().getChildNodes();
      if (docNodes.getLength() > 1) {
          Node bodyNode = docNodes.item(1);
          if(bodyNode.getNodeName().contains("Body")) {           
              NodeList bodyNodes = bodyNode.getChildNodes();
              if (bodyNodes.getLength() > 0) {
                  Node checkVatResponseNode = bodyNodes.item(0);
                  if(checkVatResponseNode.getNodeName().contains("checkVatResponse")) {             
                      NodeList checkVatResponseNodes = checkVatResponseNode.getChildNodes();
                      if (checkVatResponseNodes.getLength() > 3) {
                          Node validNode = checkVatResponseNodes.item(3);                         
                          if(validNode.getNodeName().contains("valid")) {             
                              valueValideNodeStr = validNode.getFirstChild().getNodeValue(); 
                          }
                      }                   
                  }  
              }                       
          }   
      }
      boolean valueValideNodeBoolean = false;
      if("true".equals(valueValideNodeStr)) {
          valueValideNodeBoolean = true;
      }
      
      return valueValideNodeBoolean;
    }
}
