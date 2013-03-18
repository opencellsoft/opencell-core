/*
 * (C) Copyright 2009-2013 Manaty SARL (http://manaty.net/) and contributors.
 *
 * Licensed under the GNU Public Licence, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.gnu.org/licenses/gpl-2.0.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.meveo.admin.parse.xls;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage.Severity;
import javax.inject.Inject;
import javax.inject.Named;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.service.payments.local.CustomerAccountServiceLocal;
import org.richfaces.event.UploadEvent;
import org.richfaces.model.UploadItem;
import org.slf4j.Logger;

/**
 * Action for importing Customer Accounts.
 * 
 * @author Gediminas Ubartas
 * @created 2010.11.09
 */
@Named
// TODO: @Scope(ScopeType.CONVERSATION)
public class CustomerAccountImportAction implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	protected Logger log;

	@Inject
	private CustomerAccountServiceLocal customerAccountService;

	private XLSFile xls;

	private List<String[]> importCustomerAccountData;

	private String filename;

	private Integer customerAccountsTotal;

	private Integer imported;

	private Integer failed;

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
	 * @throws BusinessException
	 */
	public String doImport() throws BusinessException {
		if (xls == null) {
			// TODO:
			// FacesMessages.instance().addFromResourceBundle(Severity.ERROR,
			// "customerAccount.import.noFileUploaded");
			return null;
		}
		List<CustomerAccount> failedImports = customerAccountService
				.importCustomerAccounts(convertData());

		failed = failedImports != null ? failedImports.size() : 0;
		imported = customerAccountsTotal - failed;

		// TODO show failed imports somewhere (probably write to other file)
		// TODO: FacesMessages.instance().addFromResourceBundle(Severity.INFO,
		// "customerAccount.import.success", new Object[] {
		// customerAccountsTotal, imported, failed });
		return "customerAccounts";
	}

	/**
	 * Converts parsed data to required entity
	 * 
	 * @returns List of Customer Accounts
	 */
	// TODO Fix mapping to Customer Account
	public List<CustomerAccount> convertData() {
		List<CustomerAccount> list = new ArrayList<CustomerAccount>();
		for (String[] customerAccountData : importCustomerAccountData) {
			CustomerAccount customerAccount = new CustomerAccount();
			customerAccount.setName(new org.meveo.model.shared.Name());
			customerAccount.getName().setFirstName(customerAccountData[0]);
		}
		return list;
	}

	/**
	 * Upload listener.
	 * 
	 * @param event
	 *            Upload event.
	 * 
	 * @throws Exception
	 */
	// TODO why synchronized??
	public synchronized void uploadListener(UploadEvent event) throws Exception {

		UploadItem item = event.getUploadItem();

		log.debug("#{currentUser.username} > Start processing uploaded XLS file (name='{0}') ..",
				item);

		// file name validation
		if (!validateFileNameAndExtention(item)) {
			return;
		}

		filename = item.getFileName();
		log.debug("#{currentUser.username} > Start parsing uploaded file ..");

		try {
			xls = new XLSFile(item.getFile());
			xls.parse();
			importCustomerAccountData = xls.getContexts();
			customerAccountsTotal = xls.getContexts().size();
			imported = 0;
			failed = 0;
		} catch (Exception e) {
			log.error("#{currentUser.username} > Error while parsing uploaded file", e);
			throw e;
		}

		log.debug("#{currentUser.username} > Uploaded file parsed successfully");

		log.debug("#{currentUser.username} > End processing uploaded XML file (name='{0}')",
				item.getFileName());
	}

	/**
	 * Validate file name and extension.
	 * 
	 * @param item
	 *            Upload file
	 * @return Null if valid and error code otherwise.
	 */
	private boolean validateFileNameAndExtention(UploadItem item) {
		log.debug("#{currentUser.username} > Start uploaded file name and extention validation ..");
		boolean valid = true;

		if (item != null && item.getFileName() != null) {
			int dot = item.getFileName().lastIndexOf(".");
			String fileExt = item.getFileName().substring(dot + 1);
			if (!fileExt.toUpperCase().equals("XLS") && !fileExt.toUpperCase().equals("TXT")) {
				log.debug("#{currentUser.username} > File name validation failed!");
				// TODO:
				// FacesMessages.instance().addToControlFromResourceBundle("fileUpload",
				// "import.badFileExtension");
				valid = false;
			}
		} else {
			log.debug("#{currentUser.username} > File name validation failed!");
			// TODO:
			// FacesMessages.instance().addToControlFromResourceBundle("fileUpload",
			// "import.fileNotFound");
			valid = false;
		}

		return valid;
	}

	public String getFilename() {
		return filename;
	}

	public Integer getCustomerAccountsTotal() {
		return customerAccountsTotal;
	}

}
