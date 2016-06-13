package org.meveo.export.parser;

import java.lang.reflect.Field;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.validation.ConstraintViolationException;
import org.apache.commons.lang.reflect.FieldUtils;
import org.jboss.seam.international.status.builder.BundleKey;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.util.XmlUtil;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.IEntity;
import org.meveo.model.admin.User;
import org.meveo.model.filter.Filter;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.filter.FilterService;

/**
 * A {@link DerivedEntityParser} that derives the {@link Filter} and/or {@link org.meveo.model.crm.CustomFieldTemplate}s
 * that are passed into the inputXml field of the {@link Filter} object.
 *
 * @author Tony Alejandro
 */
@DerivedEntityParser(parentEntity = Filter.class, fieldName = "inputXml")
public class FilterInputXMLParser extends BaseDerivedEntityParser {

    @Inject
    private FilterService filterService;

    @Override
    public void deriveEntities(IEntity entity, Field field, User currentUser) {
        try {
            String inputXml = (String) FieldUtils.readDeclaredField(entity, field.getName(), true);
            if (inputXml != null && !StringUtils.isBlank(inputXml)) {
                if (XmlUtil.validate(inputXml)) {
                    Filter filter = filterService.parse(inputXml);
                    Filter filterToBeSaved = (Filter) entity;
                    filterService.validateUnmarshalledFilter(filter);
                    filterService.updateFilterDetails(filter, filterToBeSaved, currentUser);
                    filterService.persistCustomFieldTemplates(filterToBeSaved, currentUser);
                }
            }
        } catch (IllegalAccessException | BusinessException e) {
            log.error("Failed to retrieve inputXml.", e);
        }
    }
}
