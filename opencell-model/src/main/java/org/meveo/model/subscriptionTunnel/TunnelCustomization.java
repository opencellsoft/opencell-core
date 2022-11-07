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

package org.meveo.model.subscriptionTunnel;

import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.Cache;
import org.meveo.model.BaseEntity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * tunnel customization
 *
 * @author Mohamed Chaouki
 */
@Entity
@Table(name = "tnl_customization")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "tnl_customization_seq"),})
public class TunnelCustomization extends BaseEntity {

    private static final long serialVersionUID = -6831399734977276174L;

    /**
     * Translated rgpd in JSON format with language code as a key and translated description as a value
     */
    @Type(type = "json")
    @Column(name = "rgpd", columnDefinition = "jsonb")
    private Map<String, String> rgpd;

    /**
     * Translated terms & conditions in JSON format with language code as a key and translated description as a value
     */
    @Type(type = "json")
    @Column(name = "terms_conditions", columnDefinition = "jsonb")
    private Map<String, String> termsAndConditions;

    /**
     * Translated order validation message in JSON format with language code as a key and translated description as a value
     */
    @Type(type = "json")
    @Column(name = "order_validation_msg", columnDefinition = "jsonb")
    private Map<String, String> orderValidationMsg;

    /**
     * Translated signature message in JSON format with language code as a key and translated description as a value
     */
    @Type(type = "json")
    @Column(name = "signature_msg", columnDefinition = "jsonb")
    private Map<String, String> signatureMsg;

    /**
     * Translated analytics in JSON format with language code as a key and translated description as a value
     */
    @Type(type = "json")
    @Column(name = "analytics", columnDefinition = "jsonb")
    private Map<String, String> analytics;

    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @ElementCollection(targetClass = ContactMethodEnum.class)
    @CollectionTable(name = "tnl_contact_method", joinColumns = @JoinColumn(name = "tunnel_id"))
    @Column(name = "contact_method")
    @Enumerated(EnumType.STRING)
    private List<ContactMethodEnum> contactMethods = new ArrayList<>();

    @OneToOne
    @JoinColumn(name="theme_id")
    private Theme theme;

    @OneToOne
    @JoinColumn(name="electronic_signature_id")
    private ElectronicSignature electronicSignature;


    public Map<String, String> getRgpd() {
        return rgpd;
    }

    public void setRgpd(Map<String, String> rgpd) {
        this.rgpd = rgpd;
    }

    public Map<String, String> getTermsAndConditions() {
        return termsAndConditions;
    }

    public void setTermsAndConditions(Map<String, String> termsAndConditions) {
        this.termsAndConditions = termsAndConditions;
    }

    public Map<String, String> getOrderValidationMsg() {
        return orderValidationMsg;
    }

    public void setOrderValidationMsg(Map<String, String> orderValidationMsg) {
        this.orderValidationMsg = orderValidationMsg;
    }

    public Map<String, String> getSignatureMsg() {
        return signatureMsg;
    }

    public void setSignatureMsg(Map<String, String> signatureMsg) {
        this.signatureMsg = signatureMsg;
    }

    public Map<String, String> getAnalytics() {
        return analytics;
    }

    public void setAnalytics(Map<String, String> analytics) {
        this.analytics = analytics;
    }

    public List<ContactMethodEnum> getContactMethods() {
        return contactMethods;
    }

    public void setContactMethods(List<ContactMethodEnum> contactMethods) {
        this.contactMethods = contactMethods;
    }

    public Theme getTheme() {
        return theme;
    }

    public void setTheme(Theme theme) {
        this.theme = theme;
    }

    public ElectronicSignature getElectronicSignature() {
        return electronicSignature;
    }

    public void setElectronicSignature(ElectronicSignature electronicSignature) {
        this.electronicSignature = electronicSignature;
    }
}