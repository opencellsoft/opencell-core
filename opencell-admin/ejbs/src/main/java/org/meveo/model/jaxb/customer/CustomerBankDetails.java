package org.meveo.model.jaxb.customer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.SchemaOutputResolver;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;

import org.meveo.model.jaxb.customer.bankdetails.MsgBankEmetteur;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "messageBanqueEmetteur",
        "warnings",
        "errors"
})
@XmlRootElement(name="Document", namespace="urnaaaaaa")
public class CustomerBankDetails {

    /*@XmlAttribute(name = "xmlns:xsi")
    protected String xmlnsXsi;
    
    @XmlAttribute(name = "xmlns")
    protected String xmlns;*/
    
    @XmlElement(required = true)
    protected Errors errors;
    @XmlElement(required = true)
    protected Warnings warnings;
    @XmlElement(name = "AcctSwtchngInfSvcRptV01", required = true)
    protected MsgBankEmetteur messageBanqueEmetteur;
    
    private static final Logger log = LoggerFactory.getLogger(Sellers.class);

    public CustomerBankDetails() {
    }

    /**
     * Gets the value of the errors property.
     * 
     * @return
     *     possible object is
     *     {@link Errors }
     *     
     */
    public Errors getErrors() {
        return errors;
    }

    /**
     * Sets the value of the errors property.
     * 
     * @param value
     *     allowed object is
     *     {@link Errors }
     *     
     */
    public void setErrors(Errors value) {
        this.errors = value;
    }

    /**
     * Gets the value of the warnings property.
     * 
     * @return
     *     possible object is
     *     {@link Warnings }
     *     
     */
    public Warnings getWarnings() {
        return warnings;
    }

    /**
     * Sets the value of the warnings property.
     * 
     * @param value
     *     allowed object is
     *     {@link Warnings }
     *     
     */
    public void setWarnings(Warnings value) {
        this.warnings = value;
    }

    /*public String getXmlnsXsi() {
        return xmlnsXsi;
    }

    public void setXmlnsXsi(String xmlnsXsi) {
        this.xmlnsXsi = xmlnsXsi;
    }

    public String getXmlns() {
        return xmlns;
    }

    public void setXmlns(String xmlns) {
        this.xmlns = xmlns;
    }*/

    public MsgBankEmetteur getMessageBanqueEmetteur() {
        return messageBanqueEmetteur;
    }

    public void setMessageBanqueEmetteur(MsgBankEmetteur messageBanqueEmetteur) {
        this.messageBanqueEmetteur = messageBanqueEmetteur;
    }

    public static void main(String[] args) {
        JAXBContext jaxbContext;
        try {
            jaxbContext = JAXBContext.newInstance(CustomerBankDetails.class);

            jaxbContext.generateSchema(new SchemaOutputResolver() {
                @Override
                public Result createOutput(String namespaceUri,
                        String suggestedFileName) throws IOException {
                    File file = new File("/tmp/import_customer.xsd");
                    StreamResult result = new StreamResult(file);
                    result.setSystemId(file.toURI().toURL().toString());
                    return result;
                }
            });
        } catch (Exception e) {
            // TODO Auto-generated catch block
            log.error("error = {}", e);
        }

    }
}