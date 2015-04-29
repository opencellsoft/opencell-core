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
package org.meveo.model.billing;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.management.InvalidAttributeValueException;

/**
 * @author Sebastien Michea
 * @created May 20, 2011
 *
 */
public class TIPTest {

	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

	private String codeCreancier = "123456";
	private String codeEtablissementCreancier = "1234";
	private String codeCentre = "12";
	private BankCoordinates coordonneesBancaires = new BankCoordinates();
	private String customerAccountCode = "12223";
	private long invoiceId = 12223;
	private Date invoiceDate = null;
	private Date invoiceDueDate = null;
	private BigDecimal netToPay = null;

	// @Test(groups = { "unit" })
	public void testTIP() {
		coordonneesBancaires.setAccountOwner("Sebastien Michea");
		coordonneesBancaires.setBankCode("12345");
		coordonneesBancaires.setBranchCode("12345");
		coordonneesBancaires.setAccountNumber("1234567890A");
		coordonneesBancaires.setKey("46");
		try {
			invoiceDate = sdf.parse("20110520");
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
		try {
			invoiceDueDate = sdf.parse("20111231");
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
		netToPay = new BigDecimal(106.12);
		TIP tip1 = null;
		try {
			tip1 = new TIP(codeCreancier, codeEtablissementCreancier,
					codeCentre, coordonneesBancaires, customerAccountCode,
					invoiceId, invoiceDate, invoiceDueDate, netToPay);
		} catch (InvalidAttributeValueException e) {

			e.printStackTrace();
		}
		// Assert.assertEquals(tip1.getLigneOptiqueHaute(),
		// "000000000001 SEBASTIEN MICHEA    12345123451234567890A46");
		// Assert.assertEquals(tip1.getLigneOptiqueHaute(),
		// "000000122239 SEBASTIEN MICHEA    12345123451234567890A46");
		// FIXME: test desactivé a re-activer ...
		// Assert.assertEquals(tip1.getLigneOptiqueBasse(),
		// "123456123460 57002011052012223954812    10612");

	}

	// FIXME: explain me how to use testng ...
	public static void main(String[] args) {
		TIPTest tipTest = new TIPTest();
		tipTest.testTIP();
	}
}
