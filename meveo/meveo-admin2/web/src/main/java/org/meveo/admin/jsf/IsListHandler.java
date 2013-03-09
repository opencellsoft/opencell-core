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

import java.util.List;
import java.util.Set;

import com.sun.facelets.tag.TagConfig;

/**
 * Jsf handler to check if entity field is of list type.
 * 
 * @author Ignas Lelys
 * @created May 15, 2010
 *
 */
public class IsListHandler extends IsTypeHandler {
    
    /**
     * Create tag.
     *
     * @param config TagConfig
     */
    public IsListHandler(final TagConfig config) {
        super(config);
    }
    
    /**
     * Checks if is of List type. It {@link Set} is also considered as List type.
     *
     * @param type Type class.
     *
     * @return True if this is a List.
     */
    @SuppressWarnings("unchecked")
    protected boolean isType(final Class type) {
        if (type == List.class || type == Set.class) {
            return true;
        }
        return false;
    }

}
