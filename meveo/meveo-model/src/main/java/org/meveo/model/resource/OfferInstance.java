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
package org.meveo.model.resource;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.meveo.model.BaseEntity;

/**
 * Offer instance.
 * 
 * @author Ignas Lelys
 * @created Apr 18, 2010
 * 
 */
@Entity
@Table(name = "RM_OFFER_INSTANCE")
//@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "OFFER_INSTANCE_SEQ")
public class OfferInstance extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Column(name = "CODE")
    private String code;

    @Column(name = "NAME")
    private String name;

    @Column(name = "TERM_DURATION")
    private Integer termDuration;

    public Integer getTermDuration() {
        return termDuration;
    }

    public void setTermDuration(Integer termDuration) {
        this.termDuration = termDuration;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((code == null) ? 0 : code.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return true;
        if (getClass() != obj.getClass())
            return false;
        OfferInstance other = (OfferInstance) obj;
        if (code == null) {
            if (other.getCode() != null)
                return false;
        } else if (!code.equals(other.getCode()))
            return false;
        return true;
    }

    public String toString() {
        return name;
    }

}
