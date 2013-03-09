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
package org.meveo.admin.exception;

public class BusinessEntityException extends BusinessException {

	private static final long serialVersionUID = 1L;

	private String id;
	
	public BusinessEntityException() {
		super();
	}

	public BusinessEntityException(String message, Throwable cause) {
		super(message, cause);
	}

	public BusinessEntityException(String message) {
		super(message);
	}

	public BusinessEntityException(Throwable cause) {
		super(cause);
	}

	public BusinessEntityException(String message, String id,Throwable cause) {
		super(message, cause);
		this.id=id;
	}

	public BusinessEntityException(String message, String id) {
		super(message);
		this.id=id;
	}

	public BusinessEntityException(Throwable cause, String id) {
		super(cause);
		this.id=id;
	}

	public String getId() {
		return id;
	}
}
