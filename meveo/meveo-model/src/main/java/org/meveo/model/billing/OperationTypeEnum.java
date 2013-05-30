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

public enum OperationTypeEnum {

	CREDIT(1, "operationTypeEnum.credit"), 
	DEBIT(2, "operationTypeEnum.debit");

	private String label;
	private Integer id;

	OperationTypeEnum(Integer id, String label) {
		this.label = label;
		this.id = id;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public static OperationTypeEnum getValue(Integer id) {
		if (id != null) {
			for (OperationTypeEnum type : values()) {
				if (id.equals(type.id)) {
					return type;
				}
			}
		}
		return null;
	}

}
