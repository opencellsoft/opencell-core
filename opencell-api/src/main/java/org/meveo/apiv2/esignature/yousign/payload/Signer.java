package org.meveo.apiv2.esignature.yousign.payload;

import java.util.ArrayList;
import java.util.List;

public class Signer {
	
	
	private Info info;
	private String signature_level;
	private String signature_authentication_mode;
	
	private List<Fields> fields = new ArrayList<>();
	
	public Signer(String first_name, String last_name, String email, String phone_number, String locale, String signature_level, String signature_authentication_mode){
		this.info = new Info();
		info.email = email;
		info.first_name = first_name;
		info.phone_number = phone_number;
		info.locale = locale;
		info.last_name = last_name;
		this.signature_level = signature_level;
		this.signature_authentication_mode = signature_authentication_mode;
	}
	
	
	public Info getInfo() {
		return info;
	}
	
	public void setInfo(Info info) {
		this.info = info;
	}
	
	public String getSignature_level() {
		return signature_level;
	}
	
	public void setSignature_level(String signature_level) {
		this.signature_level = signature_level;
	}
	
	public String getSignature_authentication_mode() {
		return signature_authentication_mode;
	}
	
	public void setSignature_authentication_mode(String signature_authentication_mode) {
		this.signature_authentication_mode = signature_authentication_mode;
	}
	
	private static class Info {
		private String first_name;
		private String last_name;
		private String email;
		private String phone_number;
		private String locale;
	}
	
	public void addFields(String docId, int page, int width, int x, int y){
		this.fields.add(new Fields(docId, page, width, x, y));
	}
	public static class Fields {
		private String document_id;
		private String type = "signature";
		private int page;
		private int width;
		private int x;
		private int y;
		
		public Fields(){}
		public Fields(String document_id, int page, int width, int x, int y) {
			this.document_id = document_id;
			this.page = page;
			this.width = width;
			this.x = x;
			this.y = y;
		}
		
		public String getDocument_id() {
			return document_id;
		}
		
		public void setDocument_id(String document_id) {
			this.document_id = document_id;
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
