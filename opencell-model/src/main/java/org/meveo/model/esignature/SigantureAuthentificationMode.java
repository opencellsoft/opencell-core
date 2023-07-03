package org.meveo.model.esignature;

public enum SigantureAuthentificationMode {

	otp_email, otp_sms, no_otp;
	
	public static String getValue(SigantureAuthentificationMode sigantureAuthentificationMode) {
		return sigantureAuthentificationMode != null ? sigantureAuthentificationMode.toString() : no_otp.toString();
	}
}
