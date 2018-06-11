package org.meveo.service.custom;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.meveo.model.catalog.Calendar;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.Provider;

/**
 * Defines how CF value changes should be propagated downwards in case of custom field value inheritance
 * 
 * @author Andrius Karpavicius
 */
public class CfValueAccumulatorRule implements Serializable {

    private static final long serialVersionUID = -8234247955830282876L;

    /**
     * Custom field code
     */
    private String cfCode;

    /**
     * Is field versionable
     */
    private boolean versionable;

    /**
     * Calendar used to version custom field value periods
     */
    private Calendar calendar;

    /**
     * Where to cf values should be propagated to. Key is an entity class and value is a list of entity classes that should receive updated CF value
     */
    private Map<Class<?>, List<Class<?>>> propagateTo = new HashMap<>();

    /**
     * Where CF values are being accumulated from. Key is an entity class and value is a list of fields and field type (entity clases) that entity should inherit CF values from.
     */
    private Map<Class<?>, List<CfValueAccumulatorPath>> accumulateFrom = new HashMap<>();

    /**
     * Construct a Custom field value accumulator rule
     */
    public CfValueAccumulatorRule() {

    }

    /**
     * Construct a Custom field value accumulator rule for given Custom field templates
     * 
     * @param cftList Custom field templates
     */
    public CfValueAccumulatorRule(List<CustomFieldTemplate> cftList) {

        CustomFieldTemplate cft = cftList.get(0);
        cfCode = cft.getCode();
        versionable = cft.isVersionable();
        calendar = cft.getCalendar();

        Map<Class<?>, CustomFieldTemplate> cftsByClass = cftList.stream().collect(Collectors.toMap(x -> CfValueAccumulator.appliesToMap.get(x.getAppliesTo()), x -> x));

        for (Class<?> clazz : CfValueAccumulator.topPropagateToClasses) {
            constructAccumulationRules(cftsByClass, clazz, null, null, null);
        }

    }

    /**
     * Construct propagation and accumulation paths for a given list of custom fields
     * 
     * @param cftsByClass Custom fields mapped to an entity class they correspond to
     * @param entityClazz Entity class for which rule should be produced if CFT is found
     * @param previousClazzInHierarchy Previous entity class in entity inheritance hierarchy
     * @param lastEntityClassWithCft Last entity class that had CFT
     * @param accumulationPath Accumulation path from the lastEntityClassWithCft
     */
    private void constructAccumulationRules(Map<Class<?>, CustomFieldTemplate> cftsByClass, Class<?> entityClazz, Class<?> previousClazzInHierarchy,
            Class<?> lastEntityClassWithCft, String accumulationPath) {

        // Current entity class has CFT
        if (cftsByClass.containsKey(entityClazz)) {

            // Store propagation and accumulation rule if not a topmost entity class in hierarchy
            if (lastEntityClassWithCft != null) {
                if (!propagateTo.containsKey(lastEntityClassWithCft)) {
                    propagateTo.put(lastEntityClassWithCft, new ArrayList<>());
                }
                propagateTo.get(lastEntityClassWithCft).add(entityClazz);

                if (!accumulateFrom.containsKey(entityClazz)) {
                    accumulateFrom.put(entityClazz, new ArrayList<>());
                }

                // For provider path will be always null
                String path = null;
                if (!Provider.class.equals(lastEntityClassWithCft) && CfValueAccumulator.treeAcumulateFrom.get(entityClazz).get(previousClazzInHierarchy) != null) {
                    path = CfValueAccumulator.treeAcumulateFrom.get(entityClazz).get(previousClazzInHierarchy) + (accumulationPath != null ? "." + accumulationPath : "");
                }
                accumulateFrom.get(entityClazz).add(new CfValueAccumulatorPath(lastEntityClassWithCft, path));
            }
            lastEntityClassWithCft = entityClazz;
            accumulationPath = null;

        } else if (previousClazzInHierarchy != null) {
            accumulationPath = CfValueAccumulator.treeAcumulateFrom.get(entityClazz).get(previousClazzInHierarchy) == null ? null
                    : (CfValueAccumulator.treeAcumulateFrom.get(entityClazz).get(previousClazzInHierarchy) + (accumulationPath != null ? "." + accumulationPath : ""));
        }

        // Keep going down the propagation hierarchy unless it is the same class and the loop will continue forever
        if (entityClazz.equals(previousClazzInHierarchy)) {
            return;
        }
        if (CfValueAccumulator.treePropagateTo.containsKey(entityClazz)) {
            for (Class<?> nextPropagateTo : CfValueAccumulator.treePropagateTo.get(entityClazz)) {
                constructAccumulationRules(cftsByClass, nextPropagateTo, entityClazz, lastEntityClassWithCft, accumulationPath);
            }
        }
    }

    /**
     * 
     * @return Custom field code
     */
    public String getCfCode() {
        return cfCode;
    }

    /**
     * 
     * @param cfCode Custom field code
     */
    public void setCfCode(String cfCode) {
        this.cfCode = cfCode;
    }

    /**
     * 
     * @return Is field versionable
     */
    public boolean isVersionable() {
        return versionable;
    }

    /**
     * 
     * @param versionable Is field versionable
     */
    public void setVersionable(boolean versionable) {
        this.versionable = versionable;
    }

    /**
     * 
     * @return Calendar used to version custom field value periods
     */
    public Calendar getCalendar() {
        return calendar;
    }

    /**
     * 
     * @param calendar Calendar used to version custom field value periods
     */
    public void setCalendar(Calendar calendar) {
        this.calendar = calendar;
    }

    /**
     * 
     * @return Where to cf values should be propagated to. Key is an entity class and value is a list of entity classes that should receive updated CF value
     */
    public Map<Class<?>, List<Class<?>>> getPropagateTo() {
        return propagateTo;
    }

    /**
     * 
     * @param propagateTo Where to cf values should be propagated to. Key is an entity class and value is a list of entity classes that should receive updated CF value
     */
    public void setPropagateTo(Map<Class<?>, List<Class<?>>> propagateTo) {
        this.propagateTo = propagateTo;
    }

    /**
     * 
     * @return Where CF values are being accumulated from. Key is an entity class and value is a list of fields and field type (entity clases) that entity should inherit CF values
     *         from.
     */
    public Map<Class<?>, List<CfValueAccumulatorPath>> getAcumulateFrom() {
        return accumulateFrom;
    }

    /**
     * 
     * @param acumulateFrom Where CF values are being accumulated from. Key is an entity class and value is a list of fields and field type (entity clases) that entity should
     *        inherit CF values from.
     */
    public void setAcumulateFrom(Map<Class<?>, List<CfValueAccumulatorPath>> acumulateFrom) {
        this.accumulateFrom = acumulateFrom;
    }

    @Override
    public String toString() {
        return "CfValueAccumulatorRule [cfCode=" + cfCode + ", versionable=" + versionable + ", calendar=" + (calendar != null ? calendar.getCode() : "null") + ", propagateTo="
                + propagateTo + ", accumulateFrom=" + accumulateFrom + "]";
    }
}