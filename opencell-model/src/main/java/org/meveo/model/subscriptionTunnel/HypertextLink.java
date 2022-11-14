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
import org.hibernate.annotations.Type;
import org.meveo.model.BusinessEntity;

import javax.persistence.*;
import java.util.Map;

/**
 * hypertext link
 *
 * @author Mohamed Chaouki
 */
@Entity
@Table(name = "tnl_hypertext_link")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "tnl_hypertext_link_seq"),})
public class HypertextLink extends BusinessEntity {

    private static final long serialVersionUID = -6831399734977276174L;

    /**
     * Translated label in JSON format with language code as a key and translated description as a value
     */
    @Type(type = "json")
    @Column(name = "label", columnDefinition = "jsonb")
    private Map<String, String> label;

    @Column(name = "url")
    private String url;

    @Column(name = "icon")
    private String icon;

    @Type(type = "numeric_boolean")
    @Column(name = "display_icon")
    private Boolean displayIcon;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "hypertext_section_id")
    private HypertextSection hypertextSection;

    public Map<String, String> getLabel() {
        return label;
    }

    public void setLabel(Map<String, String> label) {
        this.label = label;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public Boolean getDisplayIcon() {
        return displayIcon;
    }

    public void setDisplayIcon(Boolean displayIcon) {
        this.displayIcon = displayIcon;
    }

    public HypertextSection getHypertextSection() {
        return hypertextSection;
    }

    public void setHypertextSection(HypertextSection hypertextSection) {
        this.hypertextSection = hypertextSection;
    }
}