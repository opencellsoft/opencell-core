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
package org.meveo.model.crm;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import org.meveo.model.BaseEntity;
import org.meveo.model.bi.Report;

/**
 * Email entity.
 * 
 * @author Gediminas Ubartas
 * @created 2009.10.05
 */
@Entity
@Table(name = "CRM_EMAIL")
//@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "CRM_EMAIL_SEQ")
public class Email extends BaseEntity {

    private static final long serialVersionUID = 1L;
    @Column(name = "EMAIL")
    private String address;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "REPORT_EMAILS", joinColumns = @JoinColumn(name = "EMAIL_ID"), inverseJoinColumns = @JoinColumn(name = "REPORT_ID"))
    private List<Report> reports;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<Report> getReports() {
        return reports;
    }

    public void setReports(List<Report> reports) {
        this.reports = reports;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((address == null) ? 0 : address.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Email other = (Email) obj;
        if (address == null) {
            if (other.address != null)
                return false;
        } else if (!address.equals(other.address))
            return false;
        return true;
    }

}
