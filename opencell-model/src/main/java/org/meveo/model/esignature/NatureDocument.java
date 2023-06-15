package org.meveo.model.esignature;

public enum NatureDocument {
	signable_document, // default value of NatureDocument
	attachment;
	
	
	public static String getValue(NatureDocument natureDocument) {
		return natureDocument != null ? natureDocument.toString() : signable_document.toString();
	}
	
}
