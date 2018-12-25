/*
 * (C) Copyright 2015-2016 Opencell SAS (http://opencellsoft.com/) and contributors.
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * This program is not suitable for any direct or indirect application in MILITARY industry
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.meveo.service.generic.wf;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.ejb.Stateless;

import org.meveo.commons.utils.ReflectionUtils;
import org.meveo.model.WorkflowedEntity;
import org.meveo.model.generic.wf.GenericWorkflow;
import org.meveo.service.base.BusinessService;

@Stateless
public class GenericWorkflowService extends BusinessService<GenericWorkflow> {

    static Set<Class<?>> WORKFLOWED_CLASSES = ReflectionUtils.getClassesAnnotatedWith(WorkflowedEntity.class, "org.meveo");

    public List<Class<?>> getAllWorkflowedClass() {
        List<Class<?>> result = new ArrayList<>(WORKFLOWED_CLASSES);
        return result;
    }
}
