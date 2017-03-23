package org.meveo.event;

import org.meveo.model.IEntity;

public interface IEvent {

    /**
     * Return an entity that triggered the event
     * 
     * @return Entity that triggered the event
     */
    public IEntity getEntity();
}