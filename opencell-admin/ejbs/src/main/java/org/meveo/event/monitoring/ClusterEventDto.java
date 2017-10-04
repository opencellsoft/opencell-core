package org.meveo.event.monitoring;

import java.io.Serializable;

/**
 * Synchronization between cluster nodes event information.
 * @author Andrius Karpavicius
 *
 */
public class ClusterEventDto implements Serializable {

    private static final long serialVersionUID = -4400683830870993336L;

    /**
     * crud action value.
     *
     */
    public enum CrudActionEnum {
        create, update, remove, enable, disable
    };

    /**
     * Class of an entity to be synchronized.
     */
    private String clazz;

    /**
     * Id of entity to be synchronized.
     */
    private Long id;

    /**
     * Code of entity to be synchronized.
     */
    private String code;

    /**
     * Action that initiated synchronization.
     */
    private CrudActionEnum action;

    /**
     * defaut constructor.
     */
    public ClusterEventDto() {
    }

    /**
     * @param clazz class name
     * @param id id
     * @param code code
     * @param action crud action
     */
    public ClusterEventDto(String clazz, Long id, String code, CrudActionEnum action) {
        super();
        this.clazz = clazz;
        this.id = id;
        this.code = code;
        this.action = action;
    }

    /**
     * @return class
     */
    public String getClazz() {
        return clazz;
    }

    /**
     * @return entity id
     */
    public Long getId() {
        return id;
    }

    /**
     * @return entity code
     */
    public String getCode() {
        return code;
    }

    /**
     * @return crud action.
     */
    public CrudActionEnum getAction() {
        return action;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "ClusterEventDto [clazz=" + clazz + ", idOrCode=" + id + ", action=" + action + "]";
    }
}