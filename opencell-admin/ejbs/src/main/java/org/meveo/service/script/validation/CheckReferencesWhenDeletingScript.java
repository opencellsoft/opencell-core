package org.meveo.service.script.validation;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

import org.apache.commons.lang3.StringUtils;
import org.meveo.admin.exception.ExistsRelatedEntityException;
import org.meveo.admin.util.ResourceBundle;
import org.meveo.jpa.EntityManagerWrapper;
import org.meveo.jpa.MeveoJpa;
import org.meveo.model.CustomTableEvent;
import org.meveo.service.script.Script;

/**
 * Script to perform data integrity check during record deletion. <br/>
 * Checks that no other entities or custom tables have a reference to custom table record that is being deleted.
 * 
 * <p/>
 * Configuration steps<br/>
 * <ul>
 * <li>Create a new 'script type' notification. Specify the following values:</li>
 * <ul>
 * <li><b>Classname:</b> CustomEntityInstance</li>
 * <li><b>Event type filter:</b> Removed</li>
 * <li><b>EL filter:</b> #{event.getCetCode().equalsIgnoreCase('custom entity table name')}
 * <li><b>Script instance:</b> CheckReferencesWhenDeletingScript</li>
 * <li><b>Specify the following script parameters:</b>
 * <ul>
 * <li>name - name of the field identifying a record being deleted - usually id, but can be code or any other field</li>
 * <li>value - where the data is being referenced from. In case of multiple references, use comma as a separator. Format: &lt;table name&gt;.&lt;field name&gt;,&lt;table
 * name&gt;.&lt;field name&gt;
 * </ul>
 * <li>e.g. A custom entity or a custom table "Service type", identifiable by a field code and is referenced from another entity "Product set" from a field called "service type
 * code".<br/>
 * Script should contain the following parameters: code=product_set.service_type_code
 * </ul>
 * </ul>
 * 
 * @author Andrius Karpavicius
 *
 */
@Stateless
public class CheckReferencesWhenDeletingScript extends Script {

    private static final long serialVersionUID = 4833474260856937074L;

    @Inject
    private ResourceBundle resourceMessages;

    @Inject
    @MeveoJpa
    private EntityManagerWrapper emWrapper;

    @SuppressWarnings("unchecked")
    @Override
    public void execute(Map<String, Object> initContext) throws ExistsRelatedEntityException {

        // TODO to adapt this script to a custom entity, would need to check that entity is of CustomEntityInstance and then sql checks either code field or CF value field.
        CustomTableEvent ce = (CustomTableEvent) initContext.get(Script.CONTEXT_ENTITY);

        EntityManager em = emWrapper.getEntityManager();

        for (Entry<String, Object> parameter : initContext.entrySet()) {

            // There will be more script parameters and majority do not correspond to custom table fields.
            // Also, not interested in null values, as no reference could be made to them from other tables
            // Also skip invalid configuration where source field is not specified
            Object fieldValue = ce.getValues().get(parameter.getKey());
            if (fieldValue == null || !(parameter.getValue() instanceof String) || StringUtils.isBlank((String) parameter.getValue())) {
                continue;
            }

            String referencedFromFields = (String) parameter.getValue();

            String[] refTableAndFieldItems = referencedFromFields.split(",");
            for (String referencedFromField : refTableAndFieldItems) {

                String[] refTableAndField = referencedFromField.split("\\.");
                // The reference field format is tableName.fieldName
                if (refTableAndField.length != 2 || StringUtils.isBlank(refTableAndField[0]) || StringUtils.isBlank(refTableAndField[1])) {
                    continue;
                }
                String sql = "select id from " + refTableAndField[0].toLowerCase() + " where " + refTableAndField[1].toLowerCase() + "=:refValue";

                String fieldName = parameter.getKey();
                List<Number> records = em.createNativeQuery(sql).setParameter("refValue", fieldValue).setMaxResults(1).getResultList();

                if (!records.isEmpty()) {

                    String referencedRecord = ce.getCetCode() + "/" + (fieldName.equalsIgnoreCase("id") ? ce.getId() : fieldName + "=" + fieldValue);
                    log.error("Failed to delete an entity. Entity {} is referred from at least the following places: {}", referencedRecord, referencedFromField);
                    throw new ExistsRelatedEntityException(resourceMessages.getString("error.delete.entityUsedWDetails", referencedRecord, referencedFromField));
                }
            }
        }
    }
}
