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
package org.meveo.model.scripts;

public enum ScriptSourceTypeEnum {
    JAVA(1, "scriptSourceTypeEnum.JAVA");
    // GROOVY(2, "scriptSourceTypeEnum.GROOVY"),
    // JAVA_SCRIPT(3, "scriptSourceTypeEnum.JAVA_SCRIPT"),
    // BEAN_SHELL(4, "scriptSourceTypeEnum.BEAN_SHELL"),
    // SQL(5, "scriptSourceTypeEnum.SQL"),
    // PERL(6, "scriptSourceTypeEnum.PERL");

    private Integer id;
    private String label;

    private ScriptSourceTypeEnum(Integer id, String label) {
        this.id = id;
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public Integer getId() {
        return id;
    }

    public static ScriptSourceTypeEnum getValue(Integer id) {
        if (id != null) {
            for (ScriptSourceTypeEnum status : values()) {
                if (id.equals(status.getId())) {
                    return status;
                }
            }
        }
        return null;
    }

    public String toString() {
        return label.toString();
    }
}
