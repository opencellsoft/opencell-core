package org.meveo.model.jaxb.customer.bankdetails;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "assignment", "modification" })
@XmlRootElement(name = "AcctSwtchngInfSvcRptV01")
public class MsgBankEmetteur {
    @XmlElement(name = "Assgnmt", required = true)
    protected Assignment assignment;    
    @XmlElement(name = "Mod", required = true)
    protected List<Modification> modification;
    
    public List<Modification> getModification() {
        return modification;
    }
    public void setModification(List<Modification> modification) {
        this.modification = modification;
    }
    public Assignment getAssignment() {
        return assignment;
    }
    public void setAssignment(Assignment assignment) {
        this.assignment = assignment;
    }
}