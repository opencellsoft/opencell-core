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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.meveo.model.BusinessEntity;

/**
 * @author Ignas Lelys
 * @created Oct 31, 2010
 * 
 */
@Entity
@Table(name = "RM_SERVICE_PARAM_TEMPLATE")
//@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "RM_SERVICE_PARAM_TEMPLATE_SEQ")
public class ServiceParameterTemplate extends BusinessEntity {

    private static final long serialVersionUID = 1L;

    @Column(name = "VALUE")
    private String value;

    @Column(name = "NAME")
    private String name;

    @Column(name = "CUMULATIVE_PERIODS")
    private Integer cumulativePeriods;

    @Column(name = "DEFAULT_VALUE")
    private String defaultValue;

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public Integer getCumulativePeriods() {
        return cumulativePeriods;
    }

    public void setCumulativePeriods(Integer cumulativePeriods) {
        this.cumulativePeriods = cumulativePeriods;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String toString() {
        return name;
    }

}
