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
package org.meveo.model.admin;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.meveo.model.BaseEntity;

@Entity
@Table(name = "ADM_OUDAYA_CONFIGURATION")
//@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "ADM_OUDAYA_CONFIGURATION_SEQ")
public class OudayaConfiguration extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Column(name = "FIELD")
    private String field;

    /** Field value */
    @Column(name = "VALUE")
    private String value;

    /** Field description */
    @Column(name = "DESCRIPTION")
    private String description;

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
