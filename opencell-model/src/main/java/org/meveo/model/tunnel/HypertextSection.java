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
import org.hibernate.annotations.Type;
import org.meveo.model.BusinessEntity;

import javax.persistence.*;
import java.util.List;
import java.util.Map;

/**
 * hypertext section
 *
 * @author Mohamed Chaouki
 */
@Entity
@Table(name = "tnl_hypertext_section")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "tnl_hypertext_section_seq"),})
public class HypertextSection extends BusinessEntity {

    private static final long serialVersionUID = -6831399734977276174L;

    /**
     * Translated label in JSON format with language code as a key and translated description as a value
     */
    @Type(type = "json")
    @Column(name = "label", columnDefinition = "jsonb")
    private Map<String, String> label;

    @OneToMany(mappedBy = "hypertextSection", cascade = CascadeType.ALL)
    private List<HypertextLink> links;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "custom_style_id")
    private CustomStyle customStyle;

    public Map<String, String> getLabel() {
        return label;
    }

    public void setLabel(Map<String, String> label) {
        this.label = label;
    }

    public List<HypertextLink> getLinks() {
        return links;
    }

    public void setLinks(List<HypertextLink> links) {
        this.links = links;
    }

    public CustomStyle getCustomStyle() {
        return customStyle;
    }

    public void setCustomStyle(CustomStyle customStyle) {
        this.customStyle = customStyle;
    }
}