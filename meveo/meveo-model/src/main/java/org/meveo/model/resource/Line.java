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
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.Index;
import org.meveo.model.AuditableEntity;

/**
 * @author Ignas Lelys
 * @created Oct 31, 2010
 * 
 */
@Entity
@Table(name = "RM_LINE", uniqueConstraints = @UniqueConstraint(columnNames = { "IMSI" }))
//@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "RM_LINE_SEQ")
public class Line extends AuditableEntity {

    private static final long serialVersionUID = 1L;

    // International format, without prefixes (+ or 00) nor spaces. Max lenght =
    // 17
    @Column(name = "MSISDN", length = 17)
    @Index(name = "RM_LINE_IDX1")
    private String msisdn;

    @Column(name = "IMSI", length = 15)
    private String imsi;

    @Column(name = "STATUS")
    @Enumerated(EnumType.STRING)
    private LineStatusEnum status;

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public String getImsi() {
        return imsi;
    }

    public void setImsi(String imsi) {
        this.imsi = imsi;
    }

    public LineStatusEnum getStatus() {
        return status;
    }

    public void setStatus(LineStatusEnum status) {
        this.status = status;
    }

}
