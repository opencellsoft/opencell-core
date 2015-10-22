package org.tmf.dsmapi.catalog.resource.order;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

public class ProductOrder implements Serializable {

	private String id;
	private String href;
	private String externalld;
	private String priority;
	private String description;
	private String category;
	private String state;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date orderDate;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date completionDate;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date requestedStartDate;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date requestedCompletionDate;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date expectedCompletionDate;
	private String notificationContact;
	
	private List<Note> note;
	private List<RelatedParty> relatedParty;
	private List<OrderItem> orderItem;
	

}
