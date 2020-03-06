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

package org.meveo.model.module;

/**
 * @author Tyshan Shi(tyshan@manaty.net)
 *
 **/
public enum ModuleLicenseEnum {
    APACHE("license.apache"), BSD3_N("license.bsd3_n"), BSD3_R("license.bsd3_r"), BSD2_S("license.bsd2_s"), FREE_BSD("license.free_bsd"), GPL("license.gpl"), AGPL(
            "license.agpl"), LGPL("license.lgpl"), MIT("license.mit"), MOZ("license.moz"), CDDL("license.cddl"), EPL("license.epl"), OPEN("license.open"), COM("license.com");
    private String label;

    private ModuleLicenseEnum(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public static ModuleLicenseEnum getValue(String label) {
        if (label != null) {
            for (ModuleLicenseEnum license : values()) {
                if (label.equals(license.getLabel())) {
                    return license;
                }
            }
        }
        return null;
    }

}
