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
package org.meveo.service.api.dto;

/**
 * Contains information about a customer account
 * 
 * @author Andrius Karpavicius
 * 
 */
public class CustomerAccountDTO extends AccountDTO {

    private static final long serialVersionUID = 2349981215119682262L;
    
    private String customerCode;

    public CustomerAccountDTO(Long id, String code, String externalRef1, String externalRef2, AddressDTO address, String titleCode, String firstName, String lastName,
            String customerCode) {
        super(id, code, externalRef1, externalRef2, address, titleCode, firstName, lastName);

        this.customerCode = customerCode;
    }

    public String getCustomerCode() {
        return customerCode;
    }
}