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
package org.meveo.model;

import java.math.BigDecimal;

/**
 * Class for balance information for wallet.
 * 
 * @author Ignas
 * @created 2009.10.19
 */
public class Balance {
	
	/** Total balance. */
	private BigDecimal balance;
	
	/** Reserved balance. */
	private BigDecimal reservedBalance;

	public BigDecimal getBalance() {
    	return balance;
    }

	public void setBalance(BigDecimal balance) {
    	this.balance = balance;
    }

	public BigDecimal getReservedBalance() {
    	return reservedBalance;
    }

	public void setReservedBalance(BigDecimal reservedBalance) {
    	this.reservedBalance = reservedBalance;
    }
	
}
