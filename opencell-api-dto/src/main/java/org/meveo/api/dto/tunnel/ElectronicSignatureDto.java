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

package org.meveo.api.dto.tunnel;

import org.meveo.api.dto.BusinessEntityDto;
import org.meveo.model.tunnel.SignatureMethodEnum;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;


/**
 * The Class ElectronicSignatureDto.
 *
 * @author Ilham CHAFIK
 */
@XmlRootElement(name = "ElectronicSignature")
@XmlAccessorType(XmlAccessType.FIELD)
public class ElectronicSignatureDto extends BusinessEntityDto {

    /** serial version uid. */
    private static final long serialVersionUID = -1734000775632322028L;

    /** The electronic signature */
    private SignatureMethodEnum electronicSignature;

    /** The electronic signature label */
    private String label;

    /** the signature api */
    private String signatureApi;

    /** the signature pop up url to use in IFrame */
    private String popupUrl;

    /** Api to get siganture status */
    private String signatureStatusApi;

    /** Api to get signed file */
    private String getSignedfileApi;

    /**
     * Instantiates the electronic signature.
     */
    public ElectronicSignatureDto() {
    }

    /**
     * Instantiates the electronic signature.
     * @param electronicSignature The electronic signature
     * @param label The signature label
     * @param signatureApi the signature api
     * @param popupUrl the popup url
     * @param signatureStatusApi the signature status api
     * @param getSignedfileApi the get signed file api
     */
    public ElectronicSignatureDto(SignatureMethodEnum electronicSignature, String label, String signatureApi, String popupUrl, String signatureStatusApi, String getSignedfileApi) {
        this.electronicSignature = electronicSignature;
        this.label = label;
        this.signatureApi = signatureApi;
        this.popupUrl = popupUrl;
        this.signatureStatusApi = signatureStatusApi;
        this.getSignedfileApi = getSignedfileApi;
    }

    /**
     * Gets the electronic signature.
     * @return electronicSignature
     */
    public SignatureMethodEnum getElectronicSignature() {
        return electronicSignature;
    }

    /**
     * Sets the electronic signature.
     * @param electronicSignature the electronic signature
     */
    public void setElectronicSignature(SignatureMethodEnum electronicSignature) {
        this.electronicSignature = electronicSignature;
    }

    /**
     * Get the signature label.
     * @return label
     */
    public String getLabel() {
        return label;
    }

    /**
     * Sets the signature label.
     * @param label the signature label
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * Gets the signature api.
     * @return signatureApi
     */
    public String getSignatureApi() {
        return signatureApi;
    }

    /**
     * Sets the signature api.
     * @param signatureApi the signature api
     */
    public void setSignatureApi(String signatureApi) {
        this.signatureApi = signatureApi;
    }

    /**
     * Gets the popup url.
     * @return popupUrl
     */
    public String getPopupUrl() {
        return popupUrl;
    }

    /**
     * Sets the popup url.
     * @param popupUrl the popup url
     */
    public void setPopupUrl(String popupUrl) {
        this.popupUrl = popupUrl;
    }

    /**
     * Gets the signature status api.
     * @return signatureStatusApi
     */
    public String getSignatureStatusApi() {
        return signatureStatusApi;
    }

    /**
     * Sets the signature status api.
     * @param signatureStatusApi the signature status api
     */
    public void setSignatureStatusApi(String signatureStatusApi) {
        this.signatureStatusApi = signatureStatusApi;
    }

    /**
     * Gets get signed file api.
     * @return getSignedfileApi
     */
    public String getGetSignedfileApi() {
        return getSignedfileApi;
    }

    /**
     * Sets get signed file api.
     * @param getSignedfileApi get signed file api
     */
    public void setGetSignedfileApi(String getSignedfileApi) {
        this.getSignedfileApi = getSignedfileApi;
    }
}
