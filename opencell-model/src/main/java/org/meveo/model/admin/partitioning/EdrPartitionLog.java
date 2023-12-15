package org.meveo.model.admin.partitioning;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "tech_edr_partition_log")
public class EdrPartitionLog extends AbstractPartitionLog {
}
