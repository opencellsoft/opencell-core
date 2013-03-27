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
package org.meveo.rating.validator;

import java.math.BigDecimal;
import java.text.ParseException;

import org.apache.log4j.Logger;
import org.meveo.core.validator.Validator;
import org.meveo.rating.ticket.RatingTicket;

/**
 * Dunning ticket validator.
 * 
 * @author smichea
 * @created 20.03.2011
 * 
 */
public class RatingValidator implements Validator<RatingTicket> {

	private static final Logger logger = Logger.getLogger(RatingValidator.class);

    /**
     * No Validation yet. All loaded tickets are valid.
     * 
     * @see org.meveo.core.validator.Validator#validate(org.meveo.core.ticket.Ticket)
     */
    public boolean validate(RatingTicket ticket) {
    	boolean result=false;
    	if(ticket.chargeApplication!=null){
    		ticket.providerCode=ticket.chargeApplication.getProvider().getCode();
    		ticket.chargeCode = ticket.chargeApplication.getChargeCode();
    		ticket.amountWithoutTax = ticket.chargeApplication.getAmountWithoutTax();
    		ticket.amount2 = ticket.chargeApplication.getAmountWithoutTax();
    		ticket.applicationDate = ticket.chargeApplication.getApplicationDate();
    		ticket.subscriptionDate = ticket.chargeApplication.getSubscriptionDate();
    		ticket.criteria1 = ticket.chargeApplication.getCriteria1();
    		ticket.criteria2 = ticket.chargeApplication.getCriteria2();
    		ticket.criteria3 = ticket.chargeApplication.getCriteria3();
    		result=true;
		}
		else if(ticket.ratedTransaction!=null){
    		ticket.providerCode=ticket.ratedTransaction.getProvider().getCode();
			ticket.chargeCode = ticket.ratedTransaction.getUsageCode();
			ticket.amountWithoutTax = ticket.ratedTransaction.getUnitPrice1();
			if(ticket.ratedTransaction.getParameter5()!=null && ticket.ratedTransaction.getParameter5().trim().length()>0){
				ticket.amount2=new BigDecimal(ticket.ratedTransaction.getParameter5());
			} else {
				ticket.amount2=null;
				ticket.amountWithoutTax = null;	
			}
			ticket.applicationDate = ticket.ratedTransaction.getUsageDate();
			if(ticket.ratedTransaction.getParameter4()!=null){
			   try {
				   ticket.subscriptionDate = RatingTicket.sdf.parse(ticket.ratedTransaction.getParameter4());
			   } catch (ParseException e) {
				logger.error("Error loading transaction "+ticket.ratedTransaction.getId()+", parameter 4 should represent " +
						"subscription date in standard manaty format : 2001-07-04T12:08:56.235-0700");
			   }
			}
			ticket.criteria1 = ticket.ratedTransaction.getParameter1();
			ticket.criteria2 = ticket.ratedTransaction.getParameter2();
			ticket.criteria3 = ticket.ratedTransaction.getParameter3();
			result=true;
		}
        return result;
    }

}
