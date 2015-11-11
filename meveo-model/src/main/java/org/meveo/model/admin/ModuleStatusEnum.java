package org.meveo.model.admin;

public enum ModuleStatusEnum {
	ACTIVATE(1, "meveoModule.activate"), DEACTIVATE(2, "meveoModule.deactivate");

	private Integer id;
	private String label;

	ModuleStatusEnum(Integer id, String label) {
		this.id = id;
		this.label = label;
	}

	public Integer getId() {
		return id;
	}

	public String getLabel() {
		return this.label;
	}

	public static ModuleStatusEnum getValue(Integer id) {
		if (id != null) {
			for (ModuleStatusEnum type : values()) {
				if (id.equals(type.getId())) {
					return type;
				}
			}
		}
		return null;
	}
}
