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
package org.meveo.model.crm;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 *
 * @author Mounir Bahije
 * @lastModifiedVersion 5.2
 */
@Embeddable
@Access(AccessType.FIELD)
public class OrderedValue implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column(name = "gui_position", nullable = false, length = 50)
    @Size(max = 50)
    protected String guiPosition;

    @Column(name = "key", nullable = false, length = 50)
    @Size(max = 50)
    protected String key;

    @Column(name = "label", nullable = false, length = 255)
    @Size(max = 255)
    protected String label;

    public String getGuiPosition() {
        return guiPosition;
    }

    public void setGuiPosition(String guiPosition) {
        this.guiPosition = guiPosition;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }


}
