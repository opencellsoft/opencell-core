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

package org.meveo.api.dto.module;

/**
 * @author Edward P. Legaspi
 * @version %I%, %G%
 * @since 4 Apr 2018
 **/
public class ModulePropertyFlagLoader {

    private boolean loadOfferServiceTemplate = true;
    private boolean loadOfferProductTemplate = true;
    private boolean loadServiceChargeTemplate = true;
    private boolean loadProductChargeTemplate = true;
    private boolean loadAllowedDiscountPlan = true;
    private boolean loadOfferProducts = true;

    public boolean isLoadOfferServiceTemplate() {
        return loadOfferServiceTemplate;
    }

    public void setLoadOfferServiceTemplate(boolean loadOfferServiceTemplate) {
        this.loadOfferServiceTemplate = loadOfferServiceTemplate;
    }

    public boolean isLoadOfferProductTemplate() {
        return loadOfferProductTemplate;
    }

    public void setLoadOfferProductTemplate(boolean loadOfferProductTemplate) {
        this.loadOfferProductTemplate = loadOfferProductTemplate;
    }

    public boolean isLoadServiceChargeTemplate() {
        return loadServiceChargeTemplate;
    }

    public void setLoadServiceChargeTemplate(boolean loadServiceChargeTemplate) {
        this.loadServiceChargeTemplate = loadServiceChargeTemplate;
    }

    public boolean isLoadProductChargeTemplate() {
        return loadProductChargeTemplate;
    }

    public void setLoadProductChargeTemplate(boolean loadProductChargeTemplate) {
        this.loadProductChargeTemplate = loadProductChargeTemplate;
    }

    public boolean isLoadAllowedDiscountPlan() {
        return loadAllowedDiscountPlan;
    }

    public void setLoadAllowedDiscountPlan(boolean loadAllowedDiscountPlan) {
        this.loadAllowedDiscountPlan = loadAllowedDiscountPlan;
    }

	/**
	 * @return the loadOfferProducts
	 */
	public boolean isLoadOfferProducts() {
		return loadOfferProducts;
	}

	/**
	 * @param loadOfferProducts the loadOfferProducts to set
	 */
	public void setLoadOfferProducts(boolean loadOfferProducts) {
		this.loadOfferProducts = loadOfferProducts;
	}
    
    
}
