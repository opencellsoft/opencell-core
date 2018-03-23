/*
 * (C) Copyright 2015-2016 Opencell SAS (http://opencellsoft.com/) and contributors.
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * This program is not suitable for any direct or indirect application in MILITARY industry
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.meveo.model.bi;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.BaseEntity;

@Entity
@Table(name = "bi_job_history")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "history_type")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "bi_job_history_seq"), })
public class JobHistory extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Column(name = "history_type", insertable = false, updatable = false, length = 31)
    @Size(max = 31)
    private String type;

    @Column(name = "execution_date")
    private Date executionDate;

    @Column(name = "lines_read")
    private Integer linesRead;

    @Column(name = "lines_inserted")
    private Integer linesInserted;

    @Column(name = "lines_rejected")
    private Integer linesRejected;

    public Date getExecutionDate() {
        return executionDate;
    }

    public void setExecutionDate(Date executionDate) {
        this.executionDate = executionDate;
    }

    public Integer getLinesRead() {
        return linesRead;
    }

    public void setLinesRead(Integer linesRead) {
        this.linesRead = linesRead;
    }

    public Integer getLinesInserted() {
        return linesInserted;
    }

    public void setLinesInserted(Integer linesInserted) {
        this.linesInserted = linesInserted;
    }

    public Integer getLinesRejected() {
        return linesRejected;
    }

    public void setLinesRejected(Integer linesRejected) {
        this.linesRejected = linesRejected;
    }

    @Override
    public int hashCode() {
        int result = 961 + "JobHistory".hashCode() + (executionDate == null ? 0 : executionDate.hashCode());
        result = 31 * result + ((type == null) ? 0 : type.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (!(obj instanceof JobHistory)) {
            return false;
        }

        JobHistory other = (JobHistory) obj;
        if (id != null && other.getId() != null && id.equals(other.getId())) {
            return true;
        }
        if (executionDate == null) {
            if (other.executionDate != null)
                return false;
        } else if (!executionDate.equals(other.executionDate))
            return false;
        if (type == null) {
            if (other.type != null)
                return false;
        } else if (!type.equals(other.type))
            return false;
        return true;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

}
