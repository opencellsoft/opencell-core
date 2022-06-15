package org.meveo.model.jaxb.customer.bankdetails;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "unstructured", "structured" })
@XmlRootElement(name = "RmtInf")
public class RemittanceInformation {
    @XmlElement(name = "Ustrd")
    protected List<String> unstructured;
    @XmlElement(name = "Strd")
    protected List<Structured> structured;    

    public List<String> getUnstructured() {
        return unstructured;
    }
    public void setUnstructured(List<String> unstructured) {
        this.unstructured = unstructured;
    }
    public List<Structured> getStructured() {
        return structured;
    }
    public void setStructured(List<Structured> structured) {
        this.structured = structured;
    }  
}