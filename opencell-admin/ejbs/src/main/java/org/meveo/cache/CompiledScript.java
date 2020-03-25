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

package org.meveo.cache;

import java.io.Serializable;

import org.meveo.service.script.ScriptInterface;

/**
 * Contains a compiled script class and its instance
 * 
 * @author Andrius Karpavicius
 */
public class CompiledScript implements Serializable {

    private static final long serialVersionUID = -7274285951388062804L;

    /**
     * Compiled script class
     */
    private Class<ScriptInterface> scriptClass;

    /**
     * Instantiated script class
     */
    private ScriptInterface scriptInstance;

    /**
     * Constructor
     * 
     * @param scriptClass Compiled script class
     * @param scriptInstance Instantiated script class
     */
    public CompiledScript(Class<ScriptInterface> scriptClass, ScriptInterface scriptInstance) {
        super();
        this.scriptClass = scriptClass;
        this.scriptInstance = scriptInstance;
    }

    /**
     * @return Compiled script class
     */
    public Class<ScriptInterface> getScriptClass() {
        return scriptClass;
    }

    /**
     * @param scriptClass Compiled script class
     */
    public void setScriptClass(Class<ScriptInterface> scriptClass) {
        this.scriptClass = scriptClass;
    }

    /**
     * @return Instantiated script class
     */
    public ScriptInterface getScriptInstance() {
        return scriptInstance;
    }

    /**
     * @param scriptInstance Instantiated script class
     */
    public void setScriptInstance(ScriptInterface scriptInstance) {
        this.scriptInstance = scriptInstance;
    }

    @Override
    public String toString() {
        return scriptClass.getName();
    }
}