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