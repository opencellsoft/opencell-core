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
package org.meveo.model.shared;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import jakarta.validation.constraints.Size;

/**
 * Inter bank information
 * 
 * @author Andrius Karpavicius
 */
@Embeddable
public class InterBankTitle implements Serializable, Cloneable {

    private static final long serialVersionUID = 1L;

    /**
     * Creditor code
     */
    @Column(name = "creditor_code", length = 255)
    @Size(max = 255)
    private String codeCreancier;

    /**
     * Financial institution code
     */
    @Column(name = "financial_institution_code", length = 255)
    @Size(max = 255)
    protected String codeEtablissementCreancier;

    /**
     * Center code
     */
    @Column(name = "center_code", length = 255)
    @Size(max = 255)
    protected String codeCentre;

    /**
     * NNE
     */
    @Column(name = "nne", length = 255)
    @Size(max = 255)
    protected String nne;

    /**
     * TSA address
     */
    @Embedded
    private Address adresseTSA;

    public InterBankTitle() {

    }

    public InterBankTitle(String codeCreancier, String codeEtablissementCreancier, String codeCentre, String nne, Address adresseTSA) {
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
