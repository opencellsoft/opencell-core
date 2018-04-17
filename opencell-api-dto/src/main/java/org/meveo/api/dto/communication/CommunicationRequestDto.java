package org.meveo.api.dto.communication;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BaseDto;
import org.meveo.commons.utils.StringUtils;

/**
 * The Class CommunicationRequestDto.
 * 
 * @author anasseh
 */
@XmlRootElement(name = "CommunicationRequest")
@XmlAccessorType(XmlAccessType.FIELD)
public class CommunicationRequestDto extends BaseDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The meveo instance code. */
    @XmlElement(required = true)
    private String meveoInstanceCode;

    /** The mac address. */
    @XmlElement(required = true)
    private String macAddress;

    /** The subject. */
    @XmlElement(required = true)
    private String subject;

    /** The body. */
    private String body;

    /** The additionnal info 1. */
    private String additionnalInfo1;

    /** The additionnal info 2. */
    private String additionnalInfo2;

    /** The additionnal info 3. */
    private String additionnalInfo3;

    /** The additionnal info 4. */
    private String additionnalInfo4;

    /**
     * Instantiates a new communication request dto.
     */
    public CommunicationRequestDto() {
    }

    /**
     * Gets the meveo instance code.
     *
     * @return the meveoInstanceCode
     */
    public String getMeveoInstanceCode() {
        return meveoInstanceCode;
    }

    /**
     * Sets the meveo instance code.
     *
     * @param meveoInstanceCode the meveoInstanceCode to set
     */
    public void setMeveoInstanceCode(String meveoInstanceCode) {
        this.meveoInstanceCode = meveoInstanceCode;
    }

    /**
     * Gets the subject.
     *
     * @return the subject
     */
    public String getSubject() {
        return subject;
    }

    /**
     * Sets the subject.
     *
     * @param subject the subject to set
     */
    public void setSubject(String subject) {
        this.subject = subject;
    }

    /**
     * Gets the body.
     *
     * @return the body
     */
    public String getBody() {
        return body;
    }

    /**
     * Sets the body.
     *
     * @param body the body to set
     */
    public void setBody(String body) {
        this.body = body;
    }

    /**
     * Gets the additionnal info 1.
     *
     * @return the additionnalInfo1
     */
    public String getAdditionnalInfo1() {
        return additionnalInfo1;
    }

    /**
     * Sets the additionnal info 1.
     *
     * @param additionnalInfo1 the additionnalInfo1 to set
     */
    public void setAdditionnalInfo1(String additionnalInfo1) {
        this.additionnalInfo1 = additionnalInfo1;
    }

    /**
     * Gets the additionnal info 2.
     *
     * @return the additionnalInfo2
     */
    public String getAdditionnalInfo2() {
        return additionnalInfo2;
    }

    /**
     * Sets the additionnal info 2.
     *
     * @param additionnalInfo2 the additionnalInfo2 to set
     */
    public void setAdditionnalInfo2(String additionnalInfo2) {
        this.additionnalInfo2 = additionnalInfo2;
    }

    /**
     * Gets the additionnal info 4.
     *
     * @return the additionnalInfo4
     */
    public String getAdditionnalInfo4() {
        return additionnalInfo4;
    }

    /**
     * Sets the additionnal info 4.
     *
     * @param additionnalInfo4 the additionnalInfo4 to set
     */
    public void setAdditionnalInfo4(String additionnalInfo4) {
        this.additionnalInfo4 = additionnalInfo4;
    }

    /**
     * Gets the additionnal info 3.
     *
     * @return the additionnalInfo3
     */
    public String getAdditionnalInfo3() {
        return additionnalInfo3;
    }

    /**
     * Sets the additionnal info 3.
     *
     * @param additionnalInfo3 the additionnalInfo3 to set
     */
    public void setAdditionnalInfo3(String additionnalInfo3) {
        this.additionnalInfo3 = additionnalInfo3;
    }

    /**
     * Checks if is vaild.
     *
     * @return true, if is vaild
     */
    public boolean isVaild() {
        return !StringUtils.isBlank(meveoInstanceCode) && !StringUtils.isBlank(subject);
    }

    /**
     * Gets the mac address.
     *
     * @return the mac address
     */
    public String getMacAddress() {
        return macAddress;
    }

    /**
     * Sets the mac address.
     *
     * @param macAddress the new mac address
     */
    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }
    
    @Override
    public String toString() {
        return "CommunicationRequestDto [meveoInstanceCode=" + meveoInstanceCode + ", macAddress=" + macAddress + ", subject=" + subject + ", body=" + body + ", additionnalInfo1="
                + additionnalInfo1 + ", additionnalInfo2=" + additionnalInfo2 + ", additionnalInfo3=" + additionnalInfo3 + ", additionnalInfo4=" + additionnalInfo4 + "]";
    }
}