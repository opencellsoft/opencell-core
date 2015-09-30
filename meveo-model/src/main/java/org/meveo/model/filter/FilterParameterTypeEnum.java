package org.meveo.model.filter;

/**
 * @author Edward P. Legaspi
 **/
public enum FilterParameterTypeEnum {

	STRING(1, "filterParameterTypeEnum.string"), 
	DATE(2, "filterParameterTypeEnum.date"), 
	LONG(3, "filterParameterTypeEnum.long"), 
	DOUBLE(4, "filterParameterTypeEnum.double"), 
	LIST(5, "filterParameterTypeEnum.list");

	private Integer id;
	private String label;

	FilterParameterTypeEnum(Integer id, String label) {
		this.id = id;
		this.label = label;
	}

	public Integer getId() {
		return id;
	}

	public String getLabel() {
		return this.label;
	}

	public static FilterParameterTypeEnum getValue(Integer id) {
		if (id != null) {
			for (FilterParameterTypeEnum type : values()) {
				if (id.equals(type.getId())) {
					return type;
				}
			}
		}

		return null;
	}

}
