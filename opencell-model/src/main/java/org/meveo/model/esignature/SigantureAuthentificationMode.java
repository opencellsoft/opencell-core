package org.meveo.model.esignature;

public enum SigantureAuthentificationMode {

	OPT_EMAIL("otp_email"), OPT_SMS("otp_sms"), NO_OPT("no_otp");
	
	
	private String value;
	
	SigantureAuthentificationMode(String value){
		this.value = value;
	}
	
	public SigantureAuthentificationMode getValue(SigantureAuthentificationMode sigantureAuthentificationMode) {
		return sigantureAuthentificationMode != null ? sigantureAuthentificationMode : NO_OPT;
	}
	
	public String getValue(){
		return this.value;
	}
}
