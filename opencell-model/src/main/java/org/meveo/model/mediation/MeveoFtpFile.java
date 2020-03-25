/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */

package org.meveo.model.mediation;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

import org.meveo.model.AuditableEntity;
import org.meveo.model.NotifiableEntity;

/**
 * Record ftp file status when uplod,download,rename,delete
 * 
 * @author Tyshan Shi
 *
 */
@NotifiableEntity
public class MeveoFtpFile extends AuditableEntity implements Serializable {

    private static final long serialVersionUID = -6610759225502996091L;

    private String filename;
    private Long size;
    private Date lastModified;
    private ActionEnum action;
    private static Calendar calendar;
    static {
        calendar = Calendar.getInstance();
    }

    public MeveoFtpFile(String filename, Long size) {
        this.filename = filename;
        this.size = size;
    }

    public MeveoFtpFile(String filename, Long size, Long lastModified) {
        this(filename, size);
        calendar.setTimeInMillis(lastModified);
        this.lastModified = calendar.getTime();
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public Date getLastModified() {
        return lastModified;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }

    public ActionEnum getAction() {
        return action;
    }

    public void setAction(ActionEnum action) {
        this.action = action;
    }

    @Override
    public String toString() {
        return "MeveoFtpFile [filename=" + filename + ", size=" + size + ", lastModified=" + lastModified + ", action=" + action.getLabel() + "]";
    }

}
