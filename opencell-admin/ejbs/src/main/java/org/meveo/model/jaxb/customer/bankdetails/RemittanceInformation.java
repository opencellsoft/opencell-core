package org.meveo.model.jaxb.customer.bankdetails;

import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

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