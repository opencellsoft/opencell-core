package org.tmf.dsmapi.catalog.resource.order;

import java.io.Serializable;

import org.tmf.dsmapi.catalog.resource.TimeRange;

public class RelatedParty implements Serializable {

	private String role;
	private String id;
	private String href;
	private String name;
	private TimeRange validFor;
}
