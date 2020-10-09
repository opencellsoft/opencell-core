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
package org.meveo.model.billing;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.meveo.model.BusinessEntity;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.ISearchable;

import java.util.Map;

/**
 * Subscription termination rule
 *
 * @author Andrius Karpavicius
 * @author Abdellatif BARI
 * @lastModifiedVersion 7.0
 */
@Entity
@Cacheable
@ExportIdentifier({ "code" })
@Table(name = "billing_subscrip_termin_reason", uniqueConstraints = @UniqueConstraint(columnNames = { "code" }))
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "billing_sub_term_reason_seq"), })
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties({ "auditable", "previousCode", "appendGeneratedCode" })
public class SubscriptionTerminationReason extends BusinessEntity implements ISearchable {

    private static final long serialVersionUID = 8579279870178217508L;

    /**
     * Apply agreement
     */
    @Type(type = "numeric_boolean")
    @Column(name = "apply_agreement")
    private boolean applyAgreement;

    /**
     * Reimburse
     */
    @Type(type = "numeric_boolean")
    @Column(name = "apply_reimbursment")
    private boolean applyReimbursment;

    /**
     * Apply termination charges
     */
    @Type(type = "numeric_boolean")
    @Column(name = "apply_termination_charges")
    private boolean applyTerminationCharges;

    /**
     * Override termination prorata setting of the charge, default value is NO_OVERRIDE.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "override_prorata")
    private OverrideProrataEnum overrideProrata = OverrideProrataEnum.NO_OVERRIDE;

    /**
     * reimburse Oneshots charges.
     */
    @Type(type = "numeric_boolean")
    @Column(name = "reimburse_oneshots")
    private boolean reimburseOneshots;

    /**
     * Translated descriptions in JSON format with language code as a key and translated description as a value
     */
    @Type(type = "json")
    @Column(name = "description_i18n", columnDefinition = "text")
    private Map<String, String> descriptionI18n;

    public boolean isApplyAgreement() {
        return applyAgreement;
    }

    public void setApplyAgreement(boolean applyAgreement) {
        this.applyAgreement = applyAgreement;
    }

    public boolean isApplyReimbursment() {
        return applyReimbursment;
    }

    public void setApplyReimbursment(boolean applyReimbursment) {
        this.applyReimbursment = applyReimbursment;
    }

    public boolean isApplyTerminationCharges() {
        return applyTerminationCharges;
    }

    public void setApplyTerminationCharges(boolean applyTerminationCharges) {
        this.applyTerminationCharges = applyTerminationCharges;
    }

    /**
     * Gets the Override Prorata value.
     *
     * @return the Override Prorata value
     */
    public OverrideProrataEnum getOverrideProrata() {
        if (overrideProrata == null) {
            overrideProrata = OverrideProrataEnum.NO_OVERRIDE;
        }
        return overrideProrata;
    }

    /**
     * Sets the Override Prorata value.
     *
     * @param overrideProrata the Override Prorata value
     */
    public void setOverrideProrata(OverrideProrataEnum overrideProrata) {
        this.overrideProrata = overrideProrata;
    }

    public boolean isReimburseOneshots() {
        return reimburseOneshots;
    }

    public void setReimburseOneshots(boolean reimburseOneshots) {
        this.reimburseOneshots = reimburseOneshots;
    }

    public Map<String, String> getDescriptionI18n() {
        return descriptionI18n;
    }

    public void setDescriptionI18n(Map<String, String> descriptionI18n) {
        this.descriptionI18n = descriptionI18n;
    }

    public String getLocalizedDescription(String lang) {
        if(descriptionI18n != null) {
            return descriptionI18n.getOrDefault(lang, this.description);
        } else {
            return this.description;
        }
    }
}