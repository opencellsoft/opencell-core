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
