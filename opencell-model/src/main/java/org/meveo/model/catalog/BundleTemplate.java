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

package org.meveo.model.catalog;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;

import org.meveo.model.CustomFieldEntity;

/**
 * @author Edward P. Legaspi
 * @author Abdellatif BARI
 * @lastModifiedVersion 7.0
 */
@Entity
@CustomFieldEntity(cftCodePrefix = "BundleTemplate")
@DiscriminatorValue("BUNDLE")
@NamedQueries({ @NamedQuery(name = "BundleTemplate.countActive", query = "SELECT COUNT(*) FROM BundleTemplate WHERE disabled=false"),
        @NamedQuery(name = "BundleTemplate.countDisabled", query = "SELECT COUNT(*) FROM BundleTemplate WHERE disabled=true"),
        @NamedQuery(name = "BundleTemplate.countExpiring", query = "SELECT COUNT(*) FROM BundleTemplate WHERE :nowMinus1Day<validity.to and validity.to > NOW()") })
public class BundleTemplate extends ProductTemplate {

    private static final long serialVersionUID = -4295608354238684804L;

    @OneToMany(mappedBy = "bundleTemplate", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BundleProductTemplate> bundleProducts = new ArrayList<BundleProductTemplate>();

    public List<BundleProductTemplate> getBundleProducts() {
        return bundleProducts;
    }

    public void setBundleProducts(List<BundleProductTemplate> bundleProducts) {
        this.bundleProducts = bundleProducts;
    }

    public void addBundleProductTemplate(BundleProductTemplate bundleProductTemplate) {
        if (getBundleProducts() == null) {
            bundleProducts = new ArrayList<BundleProductTemplate>();
        }
        bundleProductTemplate.setBundleTemplate(this);

        bundleProducts.add(bundleProductTemplate);
    }

}
