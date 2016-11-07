package org.tmf.dsmapi.commons;

import java.io.Serializable;

/**
 * 
 * @author bahman.barzideh
 * 
 */
public abstract class AbstractEntityReference implements Serializable {

    public AbstractEntityReference() {
    }

    public abstract void fetchEntity(Class theClass, int depth);

}
