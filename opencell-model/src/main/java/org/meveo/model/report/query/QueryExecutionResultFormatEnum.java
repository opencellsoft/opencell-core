package org.meveo.model.report.query;



public enum QueryExecutionResultFormatEnum {
    CSV(".csv"), EXCEL(".xlsx");
	
	private String extension;
	
	private QueryExecutionResultFormatEnum(String extension) {
		this.extension = extension;
	}
	
	public String getExtension() {
		return this.extension;
	}
}