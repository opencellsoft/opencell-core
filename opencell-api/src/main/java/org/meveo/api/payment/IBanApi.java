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

package org.meveo.api.payment;

import org.iban4j.BicUtil;
import org.iban4j.IbanFormat;
import org.iban4j.IbanUtil;
import org.meveo.api.BaseApi;

import jakarta.ejb.Stateless;

/**
 * @author Mounir Bahije
 * @lastModifiedVersion 5.2
 *
 */
@Stateless
public class IBanApi extends BaseApi {

    /**
     * Validate the IBAN string and the BIC string
     * @param iban
     * @param bic
     */
    public boolean validate(String iban, String bic) {

        boolean result = false ;
        if ((iban == null) && (bic == null)) {
            result = false;

        } else {
            boolean resultIBAN = true;
            boolean resultBIC = true;
            if (iban != null) {
                resultIBAN = validateIBAN(iban);
            }
            if (bic != null) {
                resultBIC = validateBIC(bic);
            }
             result = resultBIC && resultIBAN;

        }

        return result;

    }

    /**
     * validate IBAN
     *
     * @param iban
     * @return
     */
    private boolean validateIBAN(String iban) {

        boolean result = true;
        try {
            IbanUtil.validate(iban);
        }
        catch (Exception e1) {
            try {
                IbanUtil.validate(iban, IbanFormat.Default);
            } catch (Exception e2) {
                log.error("the iban string does not have the appropriate format.", e2);
                result = false;
            }
        }
        return result;

    }

    /**
     * validate BIC
     *
     * @param bic
     * @return
     */
    private boolean validateBIC(String bic) {
        boolean result = true;
        try {
            BicUtil.validate(bic);
        }
        catch (Exception e1) {
                log.error("the bic string does not have the appropriate format.", e1);
                result = false;
        }
        return result;
    }


}
