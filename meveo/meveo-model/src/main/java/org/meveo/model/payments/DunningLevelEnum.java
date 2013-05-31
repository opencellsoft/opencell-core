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

public enum DunningLevelEnum {

    R0(1, "DunningLevelEnum.R0"),
    R1(2, "DunningLevelEnum.R1"),
    R2(3, "DunningLevelEnum.R2"),
    R3(4, "DunningLevelEnum.R3");

    private Integer id;
    private String label;

    DunningLevelEnum(Integer id, String label) {
        this.id = id;
        this.label = label;
    }

    public Integer getId() {
        return this.id;
    }

    public String getLabel() {
        return this.label;
    }

    public static DunningLevelEnum getValue(Integer id) {
        if (id != null) {
            for (DunningLevelEnum status : values()) {
                if (status.getId().intValue() == id.intValue()) {
                    return status;
                }
            }
        }
        return null;
    }
}
