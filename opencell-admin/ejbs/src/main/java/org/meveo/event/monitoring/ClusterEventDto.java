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
     * Cluster event action value.
     *
     */
    public enum CrudActionEnum {
        create, update, remove, enable, disable, execute, stop;
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
     * Additional information about the action
     */
    private String additionalInfo;

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
     * @param additionalInfo Additional information about the action
     * 
     */
    public ClusterEventDto(String clazz, Long id, String code, CrudActionEnum action, String sourceNode, String providerCode, String userName, String additionalInfo) {
        super();
        this.clazz = clazz;
        this.id = id;
        this.code = code;
        this.action = action;
        this.sourceNode = sourceNode;
        this.providerCode = providerCode;
        this.userName = userName;
        this.additionalInfo = additionalInfo;
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
     * @return Additional information about the action
     */
    public String getAdditionalInfo() {
        return additionalInfo;
    }

    /**
     * @param additionalInfo Additional information about the action
     */
    public void setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "ClusterEventDto [clazz=" + clazz + ", idOrCode=" + id + ", action=" + action + ", sourceNode=" + sourceNode + ", additionalInfo=" + additionalInfo + "]";
    }
}