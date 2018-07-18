package org.meveo.api.dto.billing;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.meveo.api.dto.BaseEntityDto;

/**
 * The Class CdrListDto.
 * 
 * @author anasseh
 */
@XmlRootElement(name = "CdrList")
@XmlAccessorType(XmlAccessType.FIELD)
public class CdrListDto extends BaseEntityDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 8776429565161215766L;

    /** The cdr. */
    private List<String> cdr;

    /** The ip address. */
    @XmlTransient
    private String ipAddress;

    /**
     * Gets the cdr.
     *
     * @return the cdr
     */
    public List<String> getCdr() {
        return cdr;
    }

    /**
     * Sets the cdr.
     *
     * @param cdr the new cdr
     */
    public void setCdr(List<String> cdr) {
        this.cdr = cdr;
    }

    /**
     * Gets the ip address.
     *
     * @return the ip address
     */
    public String getIpAddress() {
        return ipAddress;
    }

    /**
     * Sets the ip address.
     *
     * @param ipAddress the new ip address
     */
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

}