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
package org.meveo.model.mediation;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.meveo.model.BaseEntity;

/**
 * Magic numbers are used to identify if tickets are unique.
 * 
 * @author Ignas Lelys
 * @created 2009.07.13
 */
@Entity
@Table(name = "MEDIATION_MAGIC_NUMBERS")
//@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "MEDIATION_MAGIC_NUMBERS_SEQ")
public class MagicNumber extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Column(name = "ANALYSIS_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date analysisDate;

    @Column(name = "HASH")
    private String hash;

    public Date getAnalysisDate() {
        return analysisDate;
    }

    public void setAnalysisDate(Date analysisDate) {
        this.analysisDate = analysisDate;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

}
