package org.meveo.admin.action.admin.custom;

public class CustomizedEntity {

	private Long id;
    private String entityName;

    @SuppressWarnings("rawtypes")
    private Class entityClass;

    private Long customEntityId;

    private String description;

    @SuppressWarnings("rawtypes")
    public CustomizedEntity(String entityName, Class entityClass, Long customEntityId, String description) {
        super();
        this.entityName = entityName;
        this.entityClass = entityClass;
        this.customEntityId = customEntityId;
        this.description = description;
    }

    public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getEntityName() {
        return entityName;
    }

    @SuppressWarnings("rawtypes")
    public Class getEntityClass() {
        return entityClass;
    }

    public Long getCustomEntityId() {
        return customEntityId;
    }

    public boolean isCustomEntity() {
        return customEntityId != null;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return String.format("CustomizedEntity [entityName=%s, entityClass=%s, customEntityId=%s]", entityName, entityClass, customEntityId);
    }
}