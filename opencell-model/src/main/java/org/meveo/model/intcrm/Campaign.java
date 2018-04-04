package org.meveo.model.intcrm;

import java.util.Date;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

public class Campaign {
    @Temporal(TemporalType.TIMESTAMP)
	@Column(name = "schedule_date")
	private Date scheduleDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "start_date")
	private Date startDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "end_date")
	private Date endDate;
	

	@Enumerated(EnumType.ORDINAL)
	@Column(name = "priority")
	private PriorityEnum priority;


	@Enumerated(EnumType.STRING)
	@Column(name = "status")
	private CampaignStatusEnum status;
}
