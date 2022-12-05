package org.meveo.model.jaxb.customer.bankdetails;

import java.util.Date;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "msgId", "creationDateTime", "creator", "assigner", "assignee" })
@XmlRootElement(name = "Assgnmt")
public class Assignment {
    @XmlElement(name = "MsgId", required = true)
    protected String msgId;
    @XmlElement(name = "CreDtTm", required = true)
    protected Date creationDateTime;
    @XmlElement(name = "Cretr")
    protected Creator creator;
    @XmlElement(name = "Assgnr", required = true)
    protected Assigner assigner;
    @XmlElement(name = "Assgne", required = true)
    protected Assignee assignee;

    public String getMsgId() {
        return msgId;
    }
    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }
    public Date getCreationDateTime() {
        return creationDateTime;
    }
    public void setCreationDateTime(Date creationDateTime) {
        this.creationDateTime = creationDateTime;
    }
    public Creator getCreator() {
        return creator;
    }
    public void setCreator(Creator creator) {
        this.creator = creator;
    }
    public Assigner getAssigner() {
        return assigner;
    }
    public void setAssigner(Assigner assigner) {
        this.assigner = assigner;
    }
    public Assignee getAssignee() {
        return assignee;
    }

    public void setAssignee(Assignee assignee) {
        this.assignee = assignee;
    }
}