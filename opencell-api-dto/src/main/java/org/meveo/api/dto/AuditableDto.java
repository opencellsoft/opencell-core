package org.meveo.api.dto;

import java.util.Date;

/**
 * @author Edward P. Legaspi
 * @created 5 Sep 2017
 */
public class AuditableDto extends BaseDto {

	private static final long serialVersionUID = 1040133977061424749L;
	
	private Date created;

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}
	
}
