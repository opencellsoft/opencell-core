package org.meveo.api.dto.crm;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.meveo.api.dto.account.AccountDto;
import org.meveo.model.communication.CommunicationPolicy;
import org.meveo.model.communication.Message;
import org.meveo.model.communication.contact.Contact;
import org.meveo.model.intcrm.ContactGroup;

public class ContactDto extends AccountDto {

	private static final long serialVersionUID = -7359294810585172609L;

	private String email;

	private String assistantName;

	private String assistantPhone;

	private String position;

	private String company;
	
	private String mobile;
	
	private String phone;
	
	private String websiteUrl;
	
	private String importedFrom;

	private String importedBy;
	
	private String socialIdentifier;

	private boolean isVip;

	private boolean isProspect;

	private boolean agreedToUA;

	private CommunicationPolicy contactPolicy;

//	private List<Message> messages;
	
//	private AddressBookDto addressBook;
	
    private List<ContactGroupDto> contactGroup = new ArrayList<ContactGroupDto>();

	public ContactDto () {
		
	}
	
	public ContactDto(Contact contact) {
		super(contact);
		email = contact.getEmail();
		assistantName = contact.getAssistantName();
		assistantPhone = contact.getAssistantPhone();
		position = contact.getPosition();
		company = contact.getCompany();
		phone = contact.getPhone();
		mobile = contact.getMobile();
		websiteUrl = contact.getWebsiteUrl();
		importedBy = contact.getImportedBy();
		importedFrom = contact.getImportedFrom();
		socialIdentifier = contact.getSocialIdentifier();
		isVip = contact.isVip();
		isProspect = contact.isProspect();
		agreedToUA = contact.isAgreedToUA();
//		messages = contact.getMessages();
		
//		addressBook = new AddressBookDto(contact.getAddressBook());
		
		Set<ContactGroup> contactGroups = contact.getContactGroup();
		
		if(contactGroups != null) {
			List<ContactGroupDto> contactGroupDto = new ArrayList<ContactGroupDto>();
			
			for(ContactGroup cg : contactGroups) {
				ContactGroupDto cgd = new ContactGroupDto(cg);
				contactGroupDto.add(cgd);
			}
			this.setContactGroup(contactGroupDto);
		}
		
		
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getAssistantName() {
		return assistantName;
	}

	public void setAssistantName(String assistantName) {
		this.assistantName = assistantName;
	}

	public String getAssistantPhone() {
		return assistantPhone;
	}

	public void setAssistantPhone(String assistantPhone) {
		this.assistantPhone = assistantPhone;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	
	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getWebsiteUrl() {
		return websiteUrl;
	}

	public void setWebsiteUrl(String websiteUrl) {
		this.websiteUrl = websiteUrl;
	}

	public String getImportedFrom() {
		return importedFrom;
	}

	public void setImportedFrom(String importedFrom) {
		this.importedFrom = importedFrom;
	}

	public String getImportedBy() {
		return importedBy;
	}

	public void setImportedBy(String importedBy) {
		this.importedBy = importedBy;
	}

	public boolean isVip() {
		return isVip;
	}

	public void setVip(boolean isVip) {
		this.isVip = isVip;
	}

	public boolean isProspect() {
		return isProspect;
	}

	public void setProspect(boolean isProspect) {
		this.isProspect = isProspect;
	}

	public boolean isAgreedToUA() {
		return agreedToUA;
	}

	public void setAgreedToUA(boolean agreedToUA) {
		this.agreedToUA = agreedToUA;
	}

	public CommunicationPolicy getContactPolicy() {
		return contactPolicy;
	}

	public void setContactPolicy(CommunicationPolicy contactPolicy) {
		this.contactPolicy = contactPolicy;
	}

//	public List<Message> getMessages() {
//		return messages;
//	}
//
//	public void setMessages(List<Message> messages) {
//		this.messages = messages;
//	}

	public String getSocialIdentifier() {
		return socialIdentifier;
	}

	public void setSocialIdentifier(String socialIdentifier) {
		this.socialIdentifier = socialIdentifier;
	}

//	public AddressBookDto getAddressBook() {
//		return addressBook;
//	}
//
//	public void setAddressBook(AddressBookDto addressBook) {
//		this.addressBook = addressBook;
//	}

	public List<ContactGroupDto> getContactGroup() {
		return contactGroup;
	}

	public void setContactGroup(List<ContactGroupDto> contactGroup) {
		this.contactGroup = contactGroup;
	}
}
