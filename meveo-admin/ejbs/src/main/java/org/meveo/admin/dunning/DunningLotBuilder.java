/*
* (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
*
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package org.meveo.admin.dunning;

import java.io.File;

import javax.inject.Inject;

import org.meveo.commons.utils.CsvBuilder;
import org.meveo.commons.utils.NumberUtils;
import org.meveo.commons.utils.ParamBean;
import org.meveo.model.payments.ActionDunning;
import org.meveo.model.payments.DunningLOT;
import org.meveo.model.shared.DateUtils;
import org.meveo.model.shared.Title;
import org.meveo.service.catalog.impl.CatMessagesService;

/**
 * DunningLotBuilder
 * 
 * @author anasseh
 * @created 07.12.2010
 * 
 */
public class DunningLotBuilder {
	
	private static final String DUNNING_LOT_FILENAME = "bayad.dunning.lotFileName";

	private static final String DUNNING_LOT_FILENAME_EXT = "bayad.dunning.lotFileName.extention";
	
	private static final String DECIMAL_FORMAT = "bayad.decimalFormat";
	
	private static final String DUNNING_LOT_OUTPUT_DIR = "bayad.dunning.lotOutputDir";
	
	@Inject
	private CatMessagesService catMessagesService;
	            
	ParamBean paramBean=ParamBean.getInstance();
	
    private DunningLOT dunningLOT;
    private String fileName;

    public DunningLotBuilder(DunningLOT dunningLOT) {
        this.fileName = paramBean.getProperty(DUNNING_LOT_FILENAME,"Dunning") + "_" + dunningLOT.getProvider().getCode() + "_" + dunningLOT.getActionType() + "_"
                + dunningLOT.getId() + paramBean.getProperty(DUNNING_LOT_FILENAME_EXT,".csv");
        this.dunningLOT = dunningLOT;
    }

    public void exportToFile() throws Exception {
        CsvBuilder csv = new CsvBuilder();
        csv.appendValue("ActionType");
        csv.appendValue("ProviderCode");
        csv.appendValue("CustomerAccountId");
        csv.appendValue("CustomerAccountCode");
        csv.appendValue("CustomerAccountDescription");
        csv.appendValue("Title");
        csv.appendValue("FirstName");
        csv.appendValue("LastName");  
        csv.appendValue("InvoiceReference");
        csv.appendValue("Sold");
        csv.appendValue("AmountInvoice");
        csv.appendValue("InvoiceDate");        
        csv.appendValue("CreationDate");
        csv.appendValue("FromLevel");
        csv.appendValue("ToLevel");
        csv.appendValue("LetterTemplate");
        csv.appendValue("Email");
        csv.appendValue("EmailCC");
        csv.appendValue("Address1");
        csv.appendValue("Address2");
        csv.appendValue("Address3");
        csv.appendValue("ZipCode");
        csv.appendValue("City");
        csv.appendValue("State");
        csv.appendValue("Country");
        csv.startNewLine();

        for (ActionDunning actionDunning : dunningLOT.getActions()) {
            String descTitle = null, firstName = null,lastName = null;
            
            if (actionDunning.getCustomerAccount().getName() != null) {
                if (actionDunning.getCustomerAccount().getName().getTitle() != null) {
                    Title title = actionDunning.getCustomerAccount().getName().getTitle();
                    String languageCode = actionDunning.getCustomerAccount().getTradingLanguage().getLanguageCode();
                    String messageCode=catMessagesService.getMessageCode(title);
                    if(languageCode!=null){
                    descTitle = catMessagesService.getMessageDescription(messageCode, languageCode, title.getDescription());	
                    }else{
                     descTitle = actionDunning.getCustomerAccount().getName().getTitle().getDescription();	
                      }
                }      
                firstName = actionDunning.getCustomerAccount().getName().getFirstName();
                lastName =  actionDunning.getCustomerAccount().getName().getLastName();
            }
            csv.appendValue("" + actionDunning.getTypeAction());
            csv.appendValue("" + actionDunning.getCustomerAccount().getProvider().getCode());
            csv.appendValue("" + actionDunning.getCustomerAccount().getId());
            csv.appendValue("" + actionDunning.getCustomerAccount().getCode());
            csv.appendValue("" + actionDunning.getCustomerAccount().getDescription());
            csv.appendValue(getNotNull(descTitle));            
            csv.appendValue(getNotNull(firstName));
            csv.appendValue(getNotNull(lastName)); 
            csv.appendValue(actionDunning.getRecordedInvoice().getReference());
            csv.appendValue(NumberUtils.format(actionDunning.getAmountDue(),paramBean.getProperty(DECIMAL_FORMAT,"#,#00.0#")));
            csv.appendValue(NumberUtils.format(actionDunning.getRecordedInvoice().getAmount(),paramBean.getProperty(DECIMAL_FORMAT,"#,#00.0#")));
            csv.appendValue(DateUtils.formatDateWithPattern(actionDunning.getRecordedInvoice().getInvoiceDate(), "dd/MM/yyyy"));
            csv.appendValue(DateUtils.formatDateWithPattern(actionDunning.getCreationDate(), "dd/MM/yyyy"));
            csv.appendValue("" + actionDunning.getFromLevel());
            csv.appendValue("" + actionDunning.getToLevel());
            csv.appendValue(actionDunning.getActionPlanItem().getLetterTemplate() == null ? "" : getNotNull(actionDunning.getActionPlanItem().getLetterTemplate()
                    .toUpperCase()));
            csv.appendValue(actionDunning.getCustomerAccount().getPrimaryContact()==null? "" : getNotNull(actionDunning.getCustomerAccount().getPrimaryContact().getEmail()));
            csv.appendValue(actionDunning.getCustomerAccount().getExternalRef1());
            csv.appendValue(actionDunning.getCustomerAccount().getAddress() == null ? "" : getNotNull(actionDunning.getCustomerAccount().getAddress().getAddress1()));
            csv.appendValue(actionDunning.getCustomerAccount().getAddress() == null ? "" : getNotNull(actionDunning.getCustomerAccount().getAddress().getAddress2()));
            csv.appendValue(actionDunning.getCustomerAccount().getAddress() == null ? "" : getNotNull(actionDunning.getCustomerAccount().getAddress().getAddress3()));
            csv.appendValue(actionDunning.getCustomerAccount().getAddress() == null ? "" : getNotNull(actionDunning.getCustomerAccount().getAddress().getZipCode()));
            csv.appendValue(actionDunning.getCustomerAccount().getAddress() == null ? "" : getNotNull(actionDunning.getCustomerAccount().getAddress().getCity()));
            csv.appendValue(actionDunning.getCustomerAccount().getAddress() == null ? "" : getNotNull(actionDunning.getCustomerAccount().getAddress().getState()));
            csv.appendValue(actionDunning.getCustomerAccount().getAddress() == null ? "" : getNotNull(actionDunning.getCustomerAccount().getAddress().getCountry()));

            csv.startNewLine();
        }
        csv.toFile(paramBean.getProperty(DUNNING_LOT_OUTPUT_DIR,"/tmp/dunning/out/") + File.separator + fileName);
    }

    public String getFileName() {
        return this.fileName;
    }

    private String getNotNull(String str) {
        if (str == null) {
            str = "";
        }
        return str;
    }
}
