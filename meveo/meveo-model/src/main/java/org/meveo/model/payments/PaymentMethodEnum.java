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
package org.meveo.model.payments;

/**
 * Payment Enum for Customer Account.
 */
public enum PaymentMethodEnum {

    CHECK(1, "paymentMethod.check"),
    DIRECTDEBIT(2, "paymentMethod.directDebit"),
    TIP(3, "paymentMethod.tip"),
    WIRETRANSFER(4, "paymentMethod.wiretransfer");

    private String label;
    private Integer id;

    PaymentMethodEnum(Integer id, String label) {
        this.label = label;
        this.id = id;
    }

    public String getLabel() {
        return this.label;
    }

    public Integer getId() {
        return this.id;
    }

    public static PaymentMethodEnum getValue(Integer id) {
        if (id != null) {
            for (PaymentMethodEnum status : values()) {
                if (id.equals(status.getId())) {
                    return status;
                }
            }
        }
        return null;
    }
}
