package org.meveo.api.payment;

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
     * Validate the IBAN string
     * 
     * @param iban
     *
     */
    public void validate(String iban) {

        try {
            IbanUtil.validate(iban);
        } catch (Exception e1) {
           try {
               IbanUtil.validate(iban, IbanFormat.Default);
           } catch (Exception e2) {
               log.error("the string does not have the appropriate format.", e2);
               throw e2;
           }
        }

    }

}
