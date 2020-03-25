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

package org.meveo.model.notification;

public enum WebHookMethodEnum {
    HTTP_GET(1, "enum.webHookMethodEnum.HTTP_GET"), HTTP_POST(2, "enum.webHookMethodEnum.HTTP_POST"), HTTP_PUT(3, "enum.webHookMethodEnum.HTTP_PUT"), HTTP_DELETE(4,
            "enum.webHookMethodEnum.HTTP_DELETE");

    private Integer id;
    private String label;

    WebHookMethodEnum(Integer id, String label) {
        this.id = id;
        this.label = label;
    }

    public Integer getId() {
        return id;
    }

    public String getLabel() {
        return this.label;
    }

    public static WebHookMethodEnum getValue(Integer id) {
        if (id != null) {
            for (WebHookMethodEnum type : values()) {
                if (id.equals(type.getId())) {
                    return type;
                }
            }
        }
        return null;
    }
}
