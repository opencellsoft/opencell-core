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
package org.meveo.admin.exception;

public class InvalidScriptException extends BusinessException {

    private static final long serialVersionUID = 6185169709422215101L;

    public InvalidScriptException() {
        super();
    }

    public InvalidScriptException(String code, String scriptClassname, Throwable cause) {
        super(scriptClassname + " with code=" + code + " failed to compile or instantiate", cause);
    }

    public InvalidScriptException(String code, String scriptClassname) {
        super(scriptClassname + " with code=" + code + " failed to compile or instantiate");
    }

    public InvalidScriptException(String message) {
        super(message);
    }

    public InvalidScriptException(Throwable cause) {
        super(cause);
    }
}