package org.meveo.model.admin.partitioning;


import org.meveo.model.IEntity;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;
import java.util.Date;

@MappedSuperclass
public class AbstractPartitionLog implements IEntity {
    
    @Id
    @Column(name = "id")
    private Long id;
    
    @Column(name = "partition_name")
    private String partitionName;
    
    @Column(name = "range_from")
    private Date periodFrom;

    @Column(name = "range_to")
    private Date periodTo;

    @Column(name = "purge_date") 
    private Date purgeDate;

    public String getPartitionName() {
        return partitionName;
    }

    public AbstractPartitionLog setPartitionName(String partitionName) {
        this.partitionName = partitionName;
        return this;
    }

    public Date getPeriodFrom() {
        return periodFrom;
    }

    public AbstractPartitionLog setPeriodFrom(Date periodFrom) {
        this.periodFrom = periodFrom;
        return this;
    }

    public Date getPeriodTo() {
        return periodTo;
    }

    public AbstractPartitionLog setPeriodTo(Date periodTo) {
        this.periodTo = periodTo;
        return this;
    }

    public Date getPurgeDate() {
        return purgeDate;
    }

    public AbstractPartitionLog setPurgeDate(Date purgeDate) {
        this.purgeDate = purgeDate;
        return this;
    }

    @Override
    public Serializable getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public boolean isTransient() {
        return false;
    }
}
