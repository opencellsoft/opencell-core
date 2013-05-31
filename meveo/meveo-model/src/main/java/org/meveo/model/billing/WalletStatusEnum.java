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

import org.meveo.model.IdentifiableEnum;

/**
 * Wallet status.
 */
public enum WalletStatusEnum implements IdentifiableEnum {

    ACTIVATED(1, "walletStatus.activated"),
    RESTRICTED(2, "walletStatus.restricted"),
    EXPIRED(3, "walletStatus.expired"),
    SUSPENDED(4, "walletStatus.suspended"),
    TERMINATED(5, "walletStatus.terminated");

    private Integer id;
    private String label;

    private WalletStatusEnum(Integer id, String label) {
        this.id = id;
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public Integer getId() {
        return id;
    }

    public static WalletStatusEnum getValue(Integer id) {
        if (id != null) {
            for (WalletStatusEnum status : values()) {
                if (id.equals(status.getId())) {
                    return status;
                }
            }
        }
        return null;
    }

    public String toString() {
        return label.toString();
    }
}
