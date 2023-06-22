package org.meveo.apiv2.esignature.yousign.payload;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Signer {
	
	
	private Info info;
	@SerializedName("signature_level")
	private String signatureLevel;
	@SerializedName("signature_authentication_mode")
	private String signatureAuthenticationMode;
	
	private List<Fields> fields = new ArrayList<>();
	
	public Signer(String firstName, String lastName, String email, String phoneNumber, String locale, String signatureLevel, String signatureAuthenticationMode){
		this.info = new Info();
		info.email = email;
		info.firstName = firstName;
		info.phoneNumber = phoneNumber;
		info.locale = locale;
		info.lastName = lastName;
		this.signatureLevel = signatureLevel;
		this.signatureAuthenticationMode = signatureAuthenticationMode;
	}
	
	
	public Info getInfo() {
		return info;
	}
	
	public void setInfo(Info info) {
		this.info = info;
	}
	
	public String getSignatureLevel() {
		return signatureLevel;
	}
	
	public void setSignatureLevel(String signatureLevel) {
		this.signatureLevel = signatureLevel;
	}
	
	public String getSignatureAuthenticationMode() {
		return signatureAuthenticationMode;
	}
	
	public void setSignatureAuthenticationMode(String signatureAuthenticationMode) {
		this.signatureAuthenticationMode = signatureAuthenticationMode;
	}
	
	private static class Info {
		@SerializedName("first_name")
		String firstName;
		@SerializedName("last_name")
		String lastName;
		String email;
		@SerializedName("phone_number")
		String phoneNumber;
		String locale;
	}
	
	public void addFields(String docId, int page, int width, int x, int y){
		this.fields.add(new Fields(docId, page, width, x, y));
	}
	public static class Fields {
		@SerializedName("document_id")
		private String documentId;
		private String type = "signature";
		private int page;
		private int width;
		private int x;
		private int y;
		
		public Fields(){}
		public Fields(String documentId, int page, int width, int x, int y) {
			this.documentId = documentId;
			this.page = page;
			this.width = width;
			this.x = x;
			this.y = y;
		}
		
		public String getDocumentId() {
			return documentId;
		}
		
		public void setDocumentId(String document_id) {
			this.documentId = documentId;
		}
		
		public String getType() {
			return type;
		}
		
		public void setType(String type) {
			this.type = type;
		}
		
		public int getPage() {
			return page;
		}
		
		public void setPage(int page) {
			this.page = page;
		}
		
		public int getWidth() {
			return width;
		}
		
		public void setWidth(int width) {
			this.width = width;
		}
		
		public int getX() {
			return x;
		}
		
		public void setX(int x) {
			this.x = x;
		}
		
		public int getY() {
			return y;
		}
		
		public void setY(int y) {
			this.y = y;
		}
	}
}
