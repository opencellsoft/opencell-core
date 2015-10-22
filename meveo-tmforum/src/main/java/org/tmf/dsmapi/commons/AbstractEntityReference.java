package org.tmf.dsmapi.commons;

import java.io.Serializable;
import javax.persistence.MappedSuperclass;

/**
 *
 * @author bahman.barzideh
 *
 */
@MappedSuperclass
public abstract class AbstractEntityReference implements Serializable {

    public AbstractEntityReference() {
    }

    public abstract void fetchEntity(Class theClass, int depth);

}
