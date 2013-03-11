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

import javax.faces.component.UIComponent;

import com.sun.facelets.FaceletContext;
import com.sun.facelets.tag.TagAttribute;
import com.sun.facelets.tag.TagConfig;
import com.sun.facelets.tag.TagHandler;

/**
 * Super class for concrete type handlers.
 * 
 * @author Ignas
 * @created 2009.10.20
 */
public abstract class IsTypeHandler extends TagHandler {
	
    /** ID.  */
    private final TagAttribute id;

    /**
     * Create tag.
     *
     * @param config TagConfig
     */
    public IsTypeHandler(final TagConfig config) {
        super(config);
        this.id = this.getRequiredAttribute("id");
    }

    /**
     * Is the current field a boolean.
     *
     * @param faceletsContext ctx
     * @param aParent parent
     *
     * @throws IOException IOException
     */
    @SuppressWarnings("unchecked")
    public void apply(final FaceletContext faceletsContext, final UIComponent aParent)
        throws IOException {
        /* Get the name of the value binding. */
        String tid = this.id.getValue(faceletsContext);
        Class type = 
          (Class) faceletsContext.getVariableMapper().resolveVariable(tid + "Type")
                                                .getValue(faceletsContext);

        if (isType(type)) {
            this.nextHandler.apply(faceletsContext, aParent);
        }
    }

    /**
     *
     * @param type Type.
     *
     * @return True if this is the correct type.
     */
    @SuppressWarnings("unchecked")
    protected abstract boolean isType(Class type);
}

