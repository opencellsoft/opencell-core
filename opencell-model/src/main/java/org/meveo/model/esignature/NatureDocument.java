package org.meveo.model.esignature;

public enum NatureDocument {
	SIGNABLE_DOCUMENT("signable_document"), // default value of NatureDocument
	ATTACHMENT("attachment");
	
	private String value;
	
	NatureDocument(String value){
		this.value = value;
	}
	
	public NatureDocument getValue(NatureDocument natureDocument) {
		return natureDocument != null ? natureDocument : SIGNABLE_DOCUMENT;
	}
	
	public String getValue(){
		return this.value;
	}
}
