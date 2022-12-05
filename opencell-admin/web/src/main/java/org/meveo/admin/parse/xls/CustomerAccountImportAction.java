/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */
package org.meveo.admin.parse.xls;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import jakarta.enterprise.context.ConversationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import org.jboss.seam.international.status.Messages;
import org.jboss.seam.international.status.builder.BundleKey;
import org.meveo.admin.exception.BusinessException;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.shared.Name;
import org.meveo.service.payments.impl.CustomerAccountService;
//import org.richfaces.event.FileUploadEvent;
//import org.richfaces.model.UploadedFile;
import org.slf4j.Logger;

/**
 * Action for importing Customer Accounts.
 * 
 * @author Gediminas Ubartas
 * @since 2010.11.09
 */
@Named
@ConversationScoped
public class CustomerAccountImportAction implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	protected Logger log;

	@Inject
	private CustomerAccountService customerAccountService;

	private XLSFile xls;

	private List<String[]> importCustomerAccountData;

	private String filename;

	private Integer customerAccountsTotal;

	private Integer imported;

	private Integer failed;
	

    @Inject
    private Messages messages;

	/*
	 * TODO: @Create
	 * 
	 * @Begin(join = true)
	 */
	public void init() {
	}

	/**
	 * Importing uploaded files to database.
	 * 
	 * @return Return view.
	 * 
	 * @throws BusinessException General business exception
	 */
	public String doImport() throws BusinessException {
		if (xls == null) {
		    messages.error(new BundleKey("messages", "customerAccount.import.noFileUploaded"));
			return null;
		}
		List<CustomerAccount> failedImports = customerAccountService
				.importCustomerAccounts(convertData());

		failed = failedImports != null ? failedImports.size() : 0;
		imported = customerAccountsTotal - failed;

		// TODO show failed imports somewhere (probably write to other file)
		messages.info(new BundleKey("messages", "customerAccount.import.success"),customerAccountsTotal, imported, failed);
		return "customerAccounts";
	}

	/**
	 * Converts parsed data to required entity
	 * 
	 * @return List of Customer Accounts
	 */
	// TODO Fix mapping to Customer Account
	public List<CustomerAccount> convertData() {
		List<CustomerAccount> list = new ArrayList<CustomerAccount>();
		for (String[] customerAccountData : importCustomerAccountData) {
			CustomerAccount customerAccount = new CustomerAccount();
			customerAccount.setName(new Name(null, customerAccountData[0],null));
		}
		return list;
	}

	// TODO why synchronized??
//	public synchronized void uploadListener(FileUploadEvent  event) throws Exception {
//
//		UploadedFile item = event.getUploadedFile();
//
//		log.debug("#{currentUser.username} > Start processing uploaded XLS file (name='{}') ..",
//				item);
//
//		// file name validation
//		if (!validateFileNameAndExtention(item)) {
//			return;
//		}
//
//		filename = item.getName();
//		log.debug("#{currentUser.username} > Start parsing uploaded file ..");
//
//		try {
//// TODO			xls = new XLSFile(item.getData());
//			xls.parse();
//			importCustomerAccountData = xls.getContexts();
//			customerAccountsTotal = xls.getContexts().size();
//			imported = 0;
//			failed = 0;
//		} catch (Exception e) {
//			log.error("#{currentUser.username} > Error while parsing uploaded file", e);
//			throw e;
//		}

//		log.debug("#{currentUser.username} > Uploaded file parsed successfully");
//
//		log.debug("#{currentUser.username} > End processing uploaded XML file (name='{}')",
//				item.getName());
//	}

//	private boolean validateFileNameAndExtention(UploadedFile item) {
//		log.debug("#{currentUser.username} > Start uploaded file name and extention validation ..");
//		boolean valid = true;
//
//		if (item != null && item.getName() != null) {
//			int dot = item.getName().lastIndexOf(".");
//			String fileExt = item.getName().substring(dot + 1);
//			if (!fileExt.toUpperCase().equals("XLS") && !fileExt.toUpperCase().equals("TXT")) {
//				log.debug("#{currentUser.username} > File name validation failed!");
//				messages.error(new BundleKey("messages", "import.badFileExtension"));
//				valid = false;
//			}
//		} else {
//			log.debug("#{currentUser.username} > File name validation failed!");
//			messages.error(new BundleKey("messages", "import.fileNotFound"));
//			valid = false;
//		}
//
//		return valid;
//	}

	public String getFilename() {
		return filename;
	}

	public Integer getCustomerAccountsTotal() {
		return customerAccountsTotal;
	}

}
