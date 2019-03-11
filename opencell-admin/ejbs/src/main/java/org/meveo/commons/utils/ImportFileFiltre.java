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
package org.meveo.commons.utils;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ImportFileFiltre implements FilenameFilter {
    private String prefix = null;
    private List<String> extensions = null;

    public ImportFileFiltre(String prefix, String ext) {
        this.prefix = prefix;

        if (StringUtils.isBlank(prefix)) {
            this.prefix = null;
        } else {
            this.prefix = prefix.toUpperCase();
        }

        if (!StringUtils.isBlank(ext)) {
            extensions = Arrays.asList(ext.toUpperCase());
        }
    }

    public ImportFileFiltre(String prefix, List<String> extensions) {
        this.prefix = prefix;

        if (StringUtils.isBlank(prefix)) {
            this.prefix = null;
        } else {
            this.prefix = prefix.toUpperCase();
        }

        if (extensions != null) {

            this.extensions = new ArrayList<>();
            for (String ext : extensions) {
                this.extensions.add(ext.toUpperCase());
            }
        }

    }

    public boolean accept(File dir, String name) {

        String upperName = name.toUpperCase();

        if (extensions == null && (prefix == null || "*".equals(prefix) || upperName.startsWith(prefix))) {
            return true;

        } else if (extensions != null) {
            for (String extension : extensions) {
                if ((upperName.endsWith(extension) || "*".equals(extension)) && (prefix == null || "*".equals(prefix) || upperName.startsWith(prefix))) {
                    return true;
                }
            }
        }
        return false;
    }
}