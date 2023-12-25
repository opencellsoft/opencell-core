package org.meveo.model.audit;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.ColumnResult;
import javax.persistence.ConstructorResult;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.SqlResultSetMappings;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.meveo.model.IEntity;

@Entity
@Table(name = "audit_data_log")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = { @Parameter(name = "sequence_name", value = "audit_data_log_seq"),
        @Parameter(name = "increment_size", value = "100") })

@NamedQueries({
        @NamedQuery(name = "AuditDataLogRecord.purgeAuditDataLog", query = "delete from AuditDataLog a where a.created < :purgeDate")})
@NamedNativeQueries({
        @NamedNativeQuery(name = "AuditDataLogRecord.listConvertToAggregate", query = "select id, created, user_name, ref_table, ref_id, tx_id, action, origin, origin_name, data_old #>> '{}' as values_old, data_new #>> '{}' as values_new  from {h-schema}audit_data_log_rec where id<=:maxId order by tx_id, id", resultSetMapping = "AuditDataLogRecordResultMapping"),
        @NamedNativeQuery(name = "AuditDataLogRecord.getConvertToAggregateSummary", query = "SELECT count(distinct a.tx_id), max(a.id), min(a.id) FROM {h-schema}audit_data_log_rec a"),
        @NamedNativeQuery(name = "AuditDataLogRecord.deleteAuditDataLogRecords", query = "delete from {h-schema}audit_data_log_rec where id in :ids"),
        @NamedNativeQuery(name = "AuditDataLogRecord.purgeAuditDataLogRecords", query = "delete from {h-schema}audit_data_log_rec where created < :purgeDate")})

@SqlResultSetMappings({ @SqlResultSetMapping(name = "AuditDataLogRecordResultMapping", classes = @ConstructorResult(targetClass = AuditDataLogRecord.class, columns = { @ColumnResult(name = "id", type = Long.class),
        @ColumnResult(name = "created"), @ColumnResult(name = "user_name"), @ColumnResult(name = "ref_table"), @ColumnResult(name = "ref_id", type = Long.class), @ColumnResult(name = "tx_id", type = Long.class),
        @ColumnResult(name = "action"), @ColumnResult(name = "origin"), @ColumnResult(name = "origin_name"), @ColumnResult(name = "values_old"), @ColumnResult(name = "values_new") })) })
public class AuditDataLog implements Serializable, IEntity {

    private static final long serialVersionUID = -889245153264336966L;

    /**
     * Record identifier
     */
    @Id
    @GeneratedValue(generator = "ID_GENERATOR", strategy = GenerationType.AUTO)
    @Column(name = "id")
    @Access(AccessType.PROPERTY) // Access is set to property so a call to getId() wont trigger hibernate proxy loading
    private Long id;

    /**
     * Entity class. A full class name.
     */
    @Column(name = "entity_class", updatable = false, length = 100, nullable = false)
    private String entityClass;

    /**
     * Entity identifier
     */
    @Column(name = "entity_id", updatable = false, nullable = false)
    private Long entityId;

    /**
     * Transaction identifier
     */
    @Column(name = "tx_id", updatable = false, nullable = false)
    private Long txId;

    /**
     * Record creation timestamp
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created", nullable = false, updatable = false)
    private Date created;

    /**
     * Username of a user that created the record
     */
    @Column(name = "user_name", updatable = false, length = 100)
    private String userName;

    /**
     * CRUD action
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "action", updatable = false)
    private AuditCrudActionEnum action;

    /**
     * Data change origin
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "origin", updatable = false)
    private ChangeOriginEnum origin;

    /**
     * Data change origin name
     */
    @Column(name = "origin_name", updatable = false, length = 250)
    private String originName;

    /**
     * Previous values
     */
    @Type(type = "json")
    @Column(name = "data_old", columnDefinition = "jsonb")
    private Map<String, Object> valuesOld;

    /**
     * Value changes (new values)
     */
    @Type(type = "json")
    @Column(name = "data_new", columnDefinition = "jsonb")
    private Map<String, Object> valuesChanged;

    /**
     * Audit data log record identifiers that were aggregated to this AuditDataLog
     */
    @Transient
    private List<Long> auditDataLogRecords;

    /**
     * A list of AuditDataHierarchy noting a preceding data hierarchy path. Note: List generic type is not specified as to not need to move class to a model module.
     */
    @SuppressWarnings("rawtypes")
    @Transient
    private List precedingDataHierarchy;

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
     * @return Entity class. A full class name.
     */
    public String getEntityClass() {
        return entityClass;
    }

    /**
     * @param entityClass Entity class. A full class name.
     */
    public void setEntityClass(String entityClass) {
        this.entityClass = entityClass;
    }

    /**
     * @param entityClass Entity class
     */
    public void setEntityClassAsClass(@SuppressWarnings("rawtypes") Class entityClass) {
        this.entityClass = entityClass.getName();
    }

    /**
     * @return Entity identifier
     */
    public Long getEntityId() {
        return entityId;
    }

    /**
     * @param entityId Entity identifier
     */
    public void setEntityId(Long entityId) {
        this.entityId = entityId;
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
     * @return Event timestamp
     */
    public Date getCreated() {
        return created;
    }

    /**
     * @param created Event timestamp
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
     * @return Previous values
     */
    public Map<String, Object> getValuesOld() {
        return valuesOld;
    }

    /**
     * @return Previous values
     */
    public Map<String, Object> getValuesOldNullSafe() {
        if (valuesOld == null) {
            valuesOld = new LinkedHashMap<String, Object>();
        }
        return valuesOld;
    }

    /**
     * @param valuesOld Previous values
     */
    public void setValuesOld(Map<String, Object> valuesOld) {
        this.valuesOld = valuesOld;
    }

    /**
     * @return Values changes (new values)
     */
    public Map<String, Object> getValuesChanged() {
        return valuesChanged;
    }

    /**
     * @param valuesChanged Values changes (new values)
     */
    public void setValuesChanged(Map<String, Object> valuesChanged) {
        this.valuesChanged = valuesChanged;
    }

    @Override
    public boolean isTransient() {
        return id == null;
    }

    public List<Long> getAuditDataLogRecords() {
        return auditDataLogRecords;
    }

    public void addAuditDataLogRecord(Long id) {
        if (auditDataLogRecords == null) {
            auditDataLogRecords = new ArrayList<Long>();
        }
        auditDataLogRecords.add(id);
    }

    /**
     * @return A list of AuditDataHierarchy noting a preceding data hierarchy path
     */
    @SuppressWarnings("rawtypes")
    public List getPrecedingDataHierarchy() {
        return precedingDataHierarchy;
    }

    /**
     * @param precedingDataHierarchy A list of AuditDataHierarchy noting a preceding data hierarchy path
     */
    @SuppressWarnings("rawtypes")
    public void setPrecedingDataHierarchy(List precedingDataHierarchy) {
        this.precedingDataHierarchy = precedingDataHierarchy;
    }

    @Override
    public String toString() {
        return "AuditDataLog [entityClass=" + entityClass + ", entityId=" + entityId + ", action=" + action + ", valuesChanged=" + valuesChanged + "]";
    }
}