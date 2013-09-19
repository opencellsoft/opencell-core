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
package org.meveo.model.catalog;

public enum OneShotChargeTemplateTypeEnum {

    SUBSCRIPTION(1, "oneShotChargeTemplateTypeEnum.subscription"),
    TERMINATION(2, "oneShotChargeTemplateTypeEnum.termination"),
    OTHER(3, "oneShotChargeTemplateTypeEnum.other");

    private Integer id;
    private String label;

    private OneShotChargeTemplateTypeEnum(Integer id, String label) {
        this.id = id;
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public Integer getId() {
        return id;
    }

    public static OneShotChargeTemplateTypeEnum getValue(Integer id) {
        if (id != null) {
            for (OneShotChargeTemplateTypeEnum type : values()) {
                if (id.equals(type.getId())) {
                    return type;
                }
            }
        }
        return null;
    }

    public String toString() {
        return label.toString();
    }

}
