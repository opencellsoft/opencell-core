/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */

package org.meveo.api.dto.communication;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BaseEntityDto;
import org.meveo.commons.utils.StringUtils;

/**
 * The Class CommunicationRequestDto.
 * 
 * @author anasseh
 */
@XmlRootElement(name = "CommunicationRequest")
@XmlAccessorType(XmlAccessType.FIELD)
public class CommunicationRequestDto extends BaseEntityDto {

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