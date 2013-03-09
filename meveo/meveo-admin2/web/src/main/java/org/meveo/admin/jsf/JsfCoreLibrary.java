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
package org.meveo.admin.jsf;

import com.sun.facelets.tag.AbstractTagLibrary;

/**
 * JsfCoreLibrary.
 * 
 * @author Ignas
 * @created 2009.10.20
 */
public final class JsfCoreLibrary extends AbstractTagLibrary {

	/** Namespace used to import this library in Facelets pages. */
	public static final String NAMESPACE = "http://manaty.net/jsf/core";

	/** Current instance of library. */
	public static final JsfCoreLibrary INSTANCE = new JsfCoreLibrary();

	/**
	 * Creates a new JsfCoreLibrary object.
	 */
	public JsfCoreLibrary() {
		super(NAMESPACE);
		this.addTagHandler("setValueBinding", SetValueBindingHandler.class);
		this.addTagHandler("isBoolean", IsBooleanHandler.class);
		this.addTagHandler("isText", IsTextHandler.class);
		this.addTagHandler("isDate", IsDateHandler.class);
		this.addTagHandler("isEnum", IsEnumHandler.class);
		this.addTagHandler("isFloat", IsFloatHandler.class);
		this.addTagHandler("isDouble", IsDoubleHandler.class);
		this.addTagHandler("isList", IsListHandler.class);
		this.addTagHandler("isInteger", IsIntegerHandler.class);
		this.addTagHandler("isLong", IsLongHandler.class);
		this.addTagHandler("isByte", IsByteHandler.class);
		this.addTagHandler("isShort", IsShortHandler.class);
		this.addTagHandler("isBigDecimal", IsBigDecimalHandler.class);
	}

}
