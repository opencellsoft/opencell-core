package org.meveo.api.payment;

import org.iban4j.BicUtil;
import org.iban4j.IbanFormat;
import org.iban4j.IbanUtil;
import org.meveo.api.BaseApi;

import javax.ejb.Stateless;

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
