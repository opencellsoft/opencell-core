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

import java.io.IOException;

import javax.el.ValueExpression;
import javax.faces.component.UIComponent;

import com.sun.facelets.FaceletContext;
import com.sun.facelets.tag.TagAttribute;
import com.sun.facelets.tag.TagConfig;
import com.sun.facelets.tag.TagHandler;

/**
 * Maps value binding and type so other tags can access it.
 * 
 * @author Ignas
 * @created 2009.10.20
 */
public class SetValueBindingHandler extends TagHandler {
	
	/**   The name of the new variable that this tag defines. */
    private final TagAttribute var;

    /**   The actual value binding expression. */
    private final TagAttribute valueBinding;

    /**
     * Constructor. Set up the attributes for this tag.
     *
     * @param config TagConfig
     */
    public SetValueBindingHandler(final TagConfig config) {
        super(config);
        /* Define var and valueBinding attributes. */
        this.var = this.getRequiredAttribute("var");
        this.valueBinding = this.getRequiredAttribute("valueBinding");
    }

	/**
     * Apply.
     *
     * @param faceletsContext faceletsContext
     * @param parent parent
     *
     * @throws IOException IOException
     */
    @SuppressWarnings("unchecked")
    public void apply(final FaceletContext faceletsContext, final UIComponent parent) {
    	/* Create the ValueExpression from the valueBinding attribute. */
        ValueExpression valueExpression =
            this.valueBinding.getValueExpression(faceletsContext, Object.class);

        /* Get the name of the new value. */
        String tvar = this.var.getValue(faceletsContext);
        Class type = valueExpression.getType(faceletsContext);

        /* Put the value binding into the FaceletsContext where
         * we can retrieve it from other components.
         */
        faceletsContext.setAttribute(tvar, valueExpression);

        /* Cache the type so we don't have to look it
         * up in each tag. */
        faceletsContext.setAttribute(tvar + "Type", type);
    }


}
