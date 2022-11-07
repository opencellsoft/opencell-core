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
import java.util.HashMap;
import java.util.Map;

/**
 * hypertext link
 *
 * @author Mohamed Chaouki
 */
@Entity
@Table(name = "tnl_hypertext_link")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "hypertext_link_seq"),})
public class HypertextLink extends BaseEntity {

    private static final long serialVersionUID = -6831399734977276174L;


    private String url;

    private String icon;

    private Boolean displayIcon;

    @ElementCollection
    private Map<String, String> label = new HashMap<String, String>();


    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "hypertext_section_id")
    private HypertextSection hypertextSection;

    public HypertextSection getHypertextSection() {
        return hypertextSection;
    }

    public void setHypertextSection(HypertextSection hypertextSection) {
        this.hypertextSection = hypertextSection;
    }
}