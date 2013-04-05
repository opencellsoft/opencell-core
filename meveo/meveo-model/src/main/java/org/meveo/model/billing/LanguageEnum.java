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
package org.meveo.model.billing;


/**
 * @author Mbarek
 * 
 */
public enum LanguageEnum {

    EN(1, "Englais"),
    FR(2, "Francais");

    private Integer id;
    private String label;

    LanguageEnum(Integer id, String label) {
        this.id = id;
        this.label = label;

    }

    public Integer getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    /**
     * Gets enum by its id.
     */
    public static LanguageEnum getValue(Integer id) {
        if (id != null) {
            for (LanguageEnum status : values()) {
                if (id.equals(status.getId())) {
                    return status;
                }
            }
        }
        return null;
    }
}
