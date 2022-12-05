/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */

package org.meveo.admin.action.audit;

import org.meveo.commons.utils.ReflectionUtils;
import org.meveo.model.AuditableField;
import org.meveo.model.BaseEntity;
import org.meveo.model.audit.AuditableFieldNameEnum;
import org.primefaces.model.LazyDataModel;

import jakarta.enterprise.context.ConversationScoped;
import jakarta.inject.Named;

/**
 * @author Abdellatif BARI
 * @since 7.0
 */
@Named
@ConversationScoped
public class AuditableFieldListBean extends AuditableFieldBean {

    private static final long serialVersionUID = -2949768843671394990L;

    public LazyDataModel<AuditableField> listAuditableFields(BaseEntity entity, AuditableFieldNameEnum fieldName) {
        filters.put("entityClass", ReflectionUtils.getCleanClassName(entity.getClass().getName()));
        filters.put("entityId", entity.getId());
        filters.put("name", fieldName.getFieldName());
        return getLazyDataModel();
    }

    public String getId(BaseEntity entity, AuditableFieldNameEnum fieldName) {
        return ReflectionUtils.getCleanClassName(entity.getClass().getSimpleName()) + "_" + entity.getId() + "_" + fieldName.getFieldName();
    }

}
