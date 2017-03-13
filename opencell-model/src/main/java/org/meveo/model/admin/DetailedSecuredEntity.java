package org.meveo.model.admin;

public class DetailedSecuredEntity extends SecuredEntity {

	private static final long serialVersionUID = 3675734316027065766L;

	private String description;

	public DetailedSecuredEntity() {
	}

	public DetailedSecuredEntity(SecuredEntity entity) {
		super(entity);
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
