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
package org.meveo.model.audit;

import java.io.Serializable;
import java.util.Date;

/**
 * Tracks entity raw (table level) data change history
 *
 * @author Andrius Karpavicius
 * @since 14.0
 */
public class AuditDataLogRecord implements Serializable {

    private static final long serialVersionUID = -7263546632393279781L;

    /**
     * Record identifier
     */
    private Long id;

    /**
     * Record creation timestamp
     */
    private Date created;

    /**
     * Username of a user that created the record
     */
    private String userName;

    /**
     * Reference table name
     */
    private String referenceTable;

    /**
     * Reference identifier
     */
    private Long referenceId;

    /**
     * Transaction identifier
     */
    private Long txId;

    /**
     * CRUD action
     */
    private AuditCrudActionEnum action;

    /**
     * Data change origin
     */
    private ChangeOriginEnum origin;

    /**
     * Data change origin name
     */
    private String originName;

    /**
     * Previous data (only changed fields)
     */
    private String dataOld;

    /**
     * Current data (only changed fields)
     */
    private String dataNew;

    public AuditDataLogRecord() {
    }

    public AuditDataLogRecord(Long id, Date created, String userName, String referenceTable, Long referenceId, Long txId, String action, String origin, String originName, String dataOld, String dataNew) {
        super();
        this.id = id;
        this.created = created;
        this.userName = userName;
        this.referenceTable = referenceTable;
        this.referenceId = referenceId;
        this.txId = txId;
        this.action = AuditCrudActionEnum.valueOf(action);
        this.origin = ChangeOriginEnum.valueOf(origin);
        this.originName = originName;
        this.dataOld = dataOld;
        this.dataNew = dataNew;
    }

    /**
     * @return Record identifier
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id Record identifier
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return Record creation timestamp
     */
    public Date getCreated() {
        return created;
    }

    /**
     * @param created Record creation timestamp
     */
    public void setCreated(Date created) {
        this.created = created;
    }

    /**
     * @return Username of a user that created the record
     */
    public String getUserName() {
        return userName;
    }

    /**
     * @param userName Username of a user that created the record
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * @return Reference table name
     */
    public String getReferenceTable() {
        return referenceTable;
    }

    /**
     * @param referenceTable Reference table name
     */
    public void setReferenceTable(String referenceTable) {
        this.referenceTable = referenceTable;
    }

    /**
     * @return Reference identifier
     */
    public Long getReferenceId() {
        return referenceId;
    }

    /**
     * @param referenceId Reference identifier
     */
    public void setReferenceId(Long referenceId) {
        this.referenceId = referenceId;
    }

    /**
     * @return Transaction identifier
     */
    public Long getTxId() {
        return txId;
    }

    /**
     * @param txId Transaction identifier
     */
    public void setTxId(Long txId) {
        this.txId = txId;
    }

    /**
     * @return CRUD action
     */
    public AuditCrudActionEnum getAction() {
        return action;
    }

    /**
     * @param action CRUD action
     */
    public void setAction(AuditCrudActionEnum action) {
        this.action = action;
    }

    /**
     * @return Data change origin
     */
    public ChangeOriginEnum getOrigin() {
        return origin;
    }

    /**
     * @param origin Data change origin
     */
    public void setOrigin(ChangeOriginEnum origin) {
        this.origin = origin;
    }

    /**
     * @return Data change origin name
     */
    public String getOriginName() {
        return originName;
    }

    /**
     * @param originName Data change origin name
     */
    public void setOriginName(String originName) {
        this.originName = originName;
    }

    /**
     * @return Previous data (only changed fields)
     */
    public String getDataOld() {
        return dataOld;
    }

    /**
     * @param dataOld Previous data (only changed fields)
     */
    public void setDataOld(String dataOld) {
        this.dataOld = dataOld;
    }

    /**
     * @return Current data (only changed fields)
     */
    public String getDataNew() {
        return dataNew;
    }

    /**
     * @param dataNew Current data (only changed fields)
     */
    public void setDataNew(String dataNew) {
        this.dataNew = dataNew;
    }

    /**
     * @return Changed data, In case of DELETE action, its the old data, otherwise its the current/new data is returned.
     */
    public String getDataChanges() {
        if (action == AuditCrudActionEnum.DELETE) {
            return dataOld;
        } else {
            return dataNew;
        }
    }

    @Override
    public String toString() {
        return "AuditDataLogRecord [id=" + id + ", referenceTable=" + referenceTable + ", referenceId=" + referenceId + "]";
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (!(obj instanceof AuditDataLogRecord)) {
            return false;
        }

        AuditDataLogRecord other = (AuditDataLogRecord) obj;

        if (id != null && other.getId() != null && id.equals(other.getId())) {
            return true;
        }

        return false;

    }
}