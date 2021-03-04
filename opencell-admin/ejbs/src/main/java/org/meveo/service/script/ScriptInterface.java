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

package org.meveo.service.script;

import java.util.Map;

import org.meveo.admin.exception.BusinessException;

/**
 * Main script interface that all script interfaces must inherit from.
 * 
 * @author Andrius Karpavicius
 * 
 */
public interface ScriptInterface {

    /**
     * Batch processing - method to call at the beginning of script execution - before execute() is called.
     * 
     * @param methodContext Method variables in a form of a map
     * @throws BusinessException business exception.
     */
    default void init(Map<String, Object> methodContext) throws BusinessException {
    }

    /**
     * Main script method. Can be called multiple times when used with init() and finalize() methods or just once if used without them for a single script execution.
     * 
     * @param methodContext Method variables in a form of a map where CONTEXT_ENTITY=entity to process
     * @throws BusinessException business exception.
     */
    default void execute(Map<String, Object> methodContext) throws BusinessException {
    }

    /**
     * Batch processing - method to call at the end of script execution - after execute() is called.
     * 
     * @param methodContext Method variables in a form of a map
     * @throws BusinessException business exception.
     */
    default void terminate(Map<String, Object> methodContext) throws BusinessException {
    }

    /**
     * Get log messages related to script execution (test mode run only)
     * 
     * @return Log messages
     */
    default String getLogMessages() {
        return null;
    }
}