package org.meveo.jpa.event;

import org.hibernate.HibernateException;
import org.hibernate.event.spi.PersistEvent;
import org.hibernate.jpa.event.internal.core.JpaPersistEventListener;
import org.meveo.commons.utils.EjbUtils;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.BusinessEntity;
import org.meveo.service.admin.impl.CustomGenericEntityCodeService;

/**
 * JPA Persist event listener. Auto generate and customize business entity code.
 *
 * @author Abdellatif BARI.
 * @since 7.0
 */
public class CreateEventListener extends JpaPersistEventListener {

    @Override
    public void onPersist(PersistEvent event) throws HibernateException {
        super.onPersist(event);
        final Object entity = event.getObject();
        if (entity instanceof BusinessEntity) {
            try {
                BusinessEntity businessEntity = (BusinessEntity) entity;
                if (StringUtils.isBlank(businessEntity.getCode())) {
                    CustomGenericEntityCodeService customGenericEntityCodeService = (CustomGenericEntityCodeService) EjbUtils.getServiceInterface("CustomGenericEntityCodeService");
                    businessEntity.setCode(customGenericEntityCodeService.getGenericEntityCode(businessEntity));
                }
            } catch (Exception e) {
                throw new HibernateException(e);
            }
        }
    }

}