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

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.BaseEntity;
import org.meveo.model.ExportIdentifier;

@Entity
@ExportIdentifier({ "code" })
@Table(name = "billing_invoice_template", uniqueConstraints = { @UniqueConstraint(columnNames = { "code" }), @UniqueConstraint(columnNames = { "file_name" }) })
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "billing_invoice_template_seq"), })
public class InvoiceTemplate extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Column(name = "code", nullable = false, length = 255)
    @Size(max = 255)
    @NotNull
    private String code;

    @Column(name = "template_version", nullable = false, length = 255)
    @Size(max = 255)
    @NotNull
    private String templateVersion;

    @Column(name = "validity_start_date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @NotNull
    private Date validityStartDate;

    @Column(name = "validity_end_date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @NotNull
    private Date validityEndDate;

    @Column(name = "file_name", nullable = false, length = 255)
    @Size(max = 255)
    @NotNull
    private String fileName;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTemplateVersion() {
        return templateVersion;
    }

    public void setTemplateVersion(String templateVersion) {
        this.templateVersion = templateVersion;
    }

    public Date getValidityStartDate() {
        return validityStartDate;
    }

    public void setValidityStartDate(Date validityStartDate) {
        this.validityStartDate = validityStartDate;
    }

    public Date getValidityEndDate() {
        return validityEndDate;
    }

    public void setValidityEndDate(Date validityEndDate) {
        this.validityEndDate = validityEndDate;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

}
