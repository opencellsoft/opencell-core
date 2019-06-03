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