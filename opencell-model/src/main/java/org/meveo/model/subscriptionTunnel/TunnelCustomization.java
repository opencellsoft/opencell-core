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

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * tunnel customization
 *
 * @author Mohamed Chaouki
 */
@Entity
@Table(name = "tunnel_customization")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "tunnel_customization_seq"),})
public class TunnelCustomization extends BaseEntity {

    private static final long serialVersionUID = -6831399734977276174L;

    @ElementCollection
    private Map<String, String> rgbd = new HashMap<String, String>();

    @ElementCollection
    private Map<String, String> terms = new HashMap<String, String>();

    @ElementCollection
    private Map<String, String> orderValidationMsg = new HashMap<String, String>();

    @ElementCollection
    private Map<String, String> signatureMsg = new HashMap<String, String>();

    @ElementCollection
    private Map<String, String> analytics = new HashMap<String, String>();

    @ElementCollection
    private List<ContactMethodEnum> contactMethods=new ArrayList<>();

    @OneToOne
    private Theme theme;

    @OneToOne
    private ElectronicSignature electronicSignature;


}