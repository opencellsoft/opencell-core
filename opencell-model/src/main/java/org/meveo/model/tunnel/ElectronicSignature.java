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

package org.meveo.model.tunnel;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.BusinessEntity;

import javax.persistence.*;

/**
 * electronic signature
 *
 * @author Mohamed Chaouki
 */
@Entity
@Table(name = "tnl_electronic_signature", uniqueConstraints = @UniqueConstraint(columnNames = { "code" }))
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "tnl_electronic_signature_seq"),})
public class ElectronicSignature extends BusinessEntity {

    private static final long serialVersionUID = -6831399734977276174L;

    @Enumerated(EnumType.STRING)
    @Column(name = "electronic_signature")
    private SignatureMethodEnum electronicSignature;

    @Column(name = "label")
    private String label;

    @Column(name = "signature_api")
    private String signatureApi;

    @Column(name = "popup_url")
    private String popupUrl;

    @Column(name = "signature_status_api")
    private String signatureStatusApi;

    @Column(name = "get_signedfile_api")
    private String getSignedfileApi;


    public SignatureMethodEnum getElectronicSignature() {
        return electronicSignature;
    }

    public void setElectronicSignature(SignatureMethodEnum electronicSignature) {
        this.electronicSignature = electronicSignature;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getSignatureApi() {
        return signatureApi;
    }

    public void setSignatureApi(String signatureApi) {
        this.signatureApi = signatureApi;
    }

    public String getPopupUrl() {
        return popupUrl;
    }

    public void setPopupUrl(String popupUrl) {
        this.popupUrl = popupUrl;
    }

    public String getSignatureStatusApi() {
        return signatureStatusApi;
    }

    public void setSignatureStatusApi(String signatureStatusApi) {
        this.signatureStatusApi = signatureStatusApi;
    }

    public String getGetSignedfileApi() {
        return getSignedfileApi;
    }

    public void setGetSignedfileApi(String getSignedfileApi) {
        this.getSignedfileApi = getSignedfileApi;
    }
}