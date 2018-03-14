package org.meveo.event.monitoring;

import java.io.Serializable;

/**
 * Synchronization between cluster nodes event information.
 * 
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
     * Node that published the information
     */
    private String sourceNode;

    /**
     * Code of provider, that information belonged to
     */
    private String providerCode;

    /**
     * Username that initiated information publication
     */
    private String userName;

    /**
     * Defaut constructor.
     */
    public ClusterEventDto() {
    }

    /**
     * @param clazz class name
     * @param id Id
     * @param code Code
     * @param action Crud action
     * @param sourceNode Node that published the information
     * @param providerCode Code of provider, that information belonged to
     * @param userName Username that initiated information publication
     * 
     */
    public ClusterEventDto(String clazz, Long id, String code, CrudActionEnum action, String sourceNode, String providerCode, String userName) {
        super();
        this.clazz = clazz;
        this.id = id;
        this.code = code;
        this.action = action;
        this.sourceNode = sourceNode;
        this.providerCode = providerCode;
        this.userName = userName;
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
     * @return Node that published the information
     */
    public String getSourceNode() {
        return sourceNode;
    }

    /**
     * @return Code of provider, that information belonged to
     */
    public String getProviderCode() {
        return providerCode;
    }

    /**
     * @return Username that initiated information publication
     */
    public String getUserName() {
        return userName;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "ClusterEventDto [clazz=" + clazz + ", idOrCode=" + id + ", action=" + action + ", sourceNode=" + sourceNode + "]";
    }
}