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
package org.meveo.model.shared;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;

@Embeddable
public class InterBankTitle implements Serializable, Cloneable {

	private static final long serialVersionUID = 1L;

	@Column(name = "CREDITOR_CODE")
	private String codeCreancier;

	@Column(name = "FINANCIAL_INSTITUTION_CODE")
	protected String codeEtablissementCreancier;

	@Column(name = "CENTER_CODE")
	protected String codeCentre;

	@Column(name = "NNE")
	protected String nne;

	@Embedded
	private Address adresseTSA;

	public InterBankTitle() {

	}

	public InterBankTitle(String codeCreancier, String codeEtablissementCreancier,
			String codeCentre, String nne, Address adresseTSA) {
		this.codeCreancier = codeCreancier;
		this.codeEtablissementCreancier = codeEtablissementCreancier;
		this.codeCentre = codeCentre;
		this.nne = nne;
		this.adresseTSA = adresseTSA;
	}

	public String getCodeCreancier() {
		return codeCreancier;
	}

	public void setCodeCreancier(String codeCreancier) {
		this.codeCreancier = codeCreancier;
	}

	public String getCodeEtablissementCreancier() {
		return codeEtablissementCreancier;
	}

	public void setCodeEtablissementCreancier(String codeEtablissementCreancier) {
		this.codeEtablissementCreancier = codeEtablissementCreancier;
	}

	public String getCodeCentre() {
		return codeCentre;
	}

	public void setCodeCentre(String codeCentre) {
		this.codeCentre = codeCentre;
	}

	public String getNne() {
		return nne;
	}

	public void setNne(String nne) {
		this.nne = nne;
	}

	public Address getAdresseTSA() {
		return adresseTSA;
	}

	public void setAdresseTSA(Address adresseTSA) {
		this.adresseTSA = adresseTSA;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
}
