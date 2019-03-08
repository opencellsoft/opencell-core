package org.meveo.admin.action.audit;

import org.meveo.commons.utils.ReflectionUtils;
import org.meveo.model.AuditableField;
import org.meveo.model.BaseEntity;
import org.meveo.model.audit.AuditableFieldName;
import org.primefaces.model.LazyDataModel;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Named;

/**
 * @author Abdellatif BARI
 * @since 7.0
 */
@Named
@ConversationScoped
public class AuditableFieldListBean extends AuditableFieldBean {

    private static final long serialVersionUID = -2949768843671394990L;

    public LazyDataModel<AuditableField> listAuditableFields(BaseEntity entity, AuditableFieldName fieldName) {
        filters.put("entityClass", ReflectionUtils.getCleanClassName(entity.getClass().getName()));
        filters.put("entityId", entity.getId());
        filters.put("name", fieldName.getFieldName());
        return getLazyDataModel();
    }

    public String getId(BaseEntity entity, AuditableFieldName fieldName) {
        return entity.getClass().getSimpleName() + "_" + entity.getId() + "_" + fieldName.getFieldName();
    }

}
