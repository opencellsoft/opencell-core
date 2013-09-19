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
package org.meveo.model.billing;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.management.InvalidAttributeValueException;

import org.meveo.model.billing.TIP;
import org.meveo.model.billing.BankCoordinates;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author Sebastien Michea
 * @created May 20, 2011
 *
 */
public class TIPTest {

	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	
    private String codeCreancier="123456";
	private String codeEtablissementCreancier="1234";
	private String codeCentre="12";
	private BankCoordinates coordonneesBancaires=new BankCoordinates();
	private String customerAccountCode="12223";
	private long invoiceId=12223;
	private Date invoiceDate=null;
	private Date invoiceDueDate=null;
	private BigDecimal netToPay= null;

	@Test(groups = { "unit" })
    public void testTIP() {
		coordonneesBancaires.setAccountOwner("Sebastien Michea");
		coordonneesBancaires.setBankCode("12345");
		coordonneesBancaires.setBranchCode("12345");
		coordonneesBancaires.setAccountNumber("1234567890A");
		coordonneesBancaires.setKey("46");
		try {
			invoiceDate=sdf.parse("20110520");
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
		try {
			invoiceDueDate=sdf.parse("20111231");
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
		netToPay= new BigDecimal(106.12);
    	TIP tip1=null;
		try {
			tip1 = new TIP(codeCreancier, codeEtablissementCreancier, codeCentre,
					coordonneesBancaires, customerAccountCode, invoiceId,
					invoiceDate, invoiceDueDate, netToPay);
		} catch (InvalidAttributeValueException e) {
			
			e.printStackTrace();
		}
		Assert.assertEquals(tip1.getLigneOptiqueHaute(), "000000000001 SEBASTIEN MICHEA    12345123451234567890A46");
		//Assert.assertEquals(tip1.getLigneOptiqueHaute(), "000000122239 SEBASTIEN MICHEA    12345123451234567890A46");
    	//FIXME: test desactivé a re-activer ...
    	//Assert.assertEquals(tip1.getLigneOptiqueBasse(), "123456123460 57002011052012223954812    10612");
    	
    }
	
	//FIXME: explain me how to use testng ...
	public static void main(String[] args){
		TIPTest tipTest = new TIPTest();
		tipTest.testTIP();
	}
}
