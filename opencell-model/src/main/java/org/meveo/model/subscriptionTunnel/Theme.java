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

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.BaseEntity;

import javax.persistence.*;
import java.util.Date;

/**
 * theme
 *
 * @author Mohamed Chaouki
 */
@Entity
@Table(name = "tnl_theme")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "tnl_theme_seq"),})
public class Theme extends BaseEntity {

    private static final long serialVersionUID = -6831399734977276174L;

    @OneToOne
    @JoinColumn(name="header")
    private CustomStyle header;

    @OneToOne
    @JoinColumn(name="body")
    private CustomStyle body;

    @OneToOne
    @JoinColumn(name="footer")
    private CustomStyle footer;

    @Column(name = "created_on")
    private Date createdOn;


    public CustomStyle getHeader() {
        return header;
    }

    public void setHeader(CustomStyle header) {
        this.header = header;
    }

    public CustomStyle getBody() {
        return body;
    }

    public void setBody(CustomStyle body) {
        this.body = body;
    }

    public CustomStyle getFooter() {
        return footer;
    }

    public void setFooter(CustomStyle footer) {
        this.footer = footer;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }
}