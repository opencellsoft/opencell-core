/*
* (C) Copyright 2009-2013 Manaty SARL (http://manaty.net/) and contributors.
*
* Licensed under the GNU Public Licence, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.gnu.org/licenses/gpl-2.0.txt
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.meveo.model.payments;

/**
 * Credit Category Enum for Customer Account.
 */
public enum CreditCategoryEnum {

    PART_C(1, "creditCategory.part_c"),
    PART_M(2, "creditCategory.part_m"),
    PRO(3, "creditCategory.pro"),
    VIP(4, "creditCategory.vip"),
    NEWCUSTOMER(5, "creditCategory.newCustomer");

    private Integer id;
    private String label;

    CreditCategoryEnum(Integer id, String label) {
        this.id = id;
        this.label = label;
    }

    public Integer getId() {
        return this.id;
    }

    public String getLabel() {
        return this.label;
    }

    public static CreditCategoryEnum getValue(Integer id) {
        if (id != null) {
            for (CreditCategoryEnum status : values()) {
                if (id.equals(status.getId())) {
                    return status;
                }
            }
        }
        return null;
    }
}
