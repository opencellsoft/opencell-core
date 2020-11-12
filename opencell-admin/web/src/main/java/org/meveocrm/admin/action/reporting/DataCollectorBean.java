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
package org.meveocrm.admin.action.reporting;

import static java.util.regex.Pattern.CASE_INSENSITIVE;
import static java.util.regex.Pattern.compile;
import static java.util.stream.Collectors.joining;

import org.meveo.admin.action.UpdateMapTypeFieldBean;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.web.interceptor.ActionMethod;
import org.meveo.model.bi.DataCollector;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.dataCollector.DataCollectorService;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.*;

@Named
@ViewScoped
public class DataCollectorBean extends UpdateMapTypeFieldBean<DataCollector> {

    private static final long serialVersionUID = -1644247310764456827L;

    @Inject
    private DataCollectorService dataCollectorService;

    protected Map<String, List<HashMap<String, String>>> mapTypeFieldValues = new HashMap<>();

    public DataCollectorBean() {
        super(DataCollector.class);
    }

    @Override
    public DataCollector initEntity() {
        DataCollector dataCollector = super.initEntity();
        extractMapTypeFieldFromEntity(dataCollector.getAliases(), "aliases");
        return dataCollector;
    }

    @Override
    protected IPersistenceService<DataCollector> getPersistenceService() {
        return dataCollectorService;
    }

    @Override
    @ActionMethod
    public String saveOrUpdate(boolean killConversation) throws BusinessException {
        updateMapTypeFieldInEntity(entity.getAliases(), "aliases");
        validateInputs(entity);
        entity.setSqlQuery(buildQuery());
        return super.saveOrUpdate(killConversation);
    }

    private void validateInputs(DataCollector entity) {
        if (entity.getAliases() == null || entity.getAliases().isEmpty()) {
            throw new BusinessException("Data collector aliases are missing");
        }
    }

    public void addMapTypeFieldValue(String fieldName) {
        if (!mapTypeFieldValues.containsKey(fieldName)) {
            mapTypeFieldValues.put(fieldName, new ArrayList<>());
        }
        mapTypeFieldValues.get(fieldName).add(new HashMap<>());
    }

    public void extractMapTypeFieldFromEntity(Map<String, String> entityField, String fieldName) {

        mapTypeFieldValues.remove(fieldName);

        if (entityField != null) {
            List<HashMap<String, String>> fieldValues = new ArrayList<>();
            mapTypeFieldValues.put(fieldName, fieldValues);
            for (Map.Entry<String, String> setInfo : entityField.entrySet()) {
                HashMap<String, String> value = new HashMap<>();
                value.put("key", setInfo.getKey());
                value.put("value", setInfo.getValue());
                fieldValues.add(value);
            }
        }
    }

    public void updateMapTypeFieldInEntity(Map<String, String> entityField, String fieldName) {
        entityField.clear();

        if (mapTypeFieldValues.get(fieldName) != null) {
            for (HashMap<String, String> valueInfo : mapTypeFieldValues.get(fieldName)) {
                if (valueInfo.get("key") != null && !valueInfo.get("key").isEmpty()) {
                    entityField.put(valueInfo.get("key"), valueInfo.get("value") == null ? "" : valueInfo.get("value"));
                }
            }
        }
    }

    public void removeMapTypeFieldValue(String fieldName, Map<String, String> valueInfo) {
        mapTypeFieldValues.get(fieldName).remove(valueInfo);
    }

    public Map<String, List<HashMap<String, String>>> getMapTypeFieldValues() {
        return mapTypeFieldValues;
    }

    public void setMapTypeFieldValues(Map<String, List<HashMap<String, String>>> mapTypeFieldValues) {
        this.mapTypeFieldValues = mapTypeFieldValues;
    }

    private String buildQuery() {
        String query = prepareQuery(entity.getSqlQuery());
        String columns = entity.getAliases().entrySet()
                .stream()
                .map(entry -> entry.getKey() + " as " + entry.getValue())
                .collect(joining(", "));
        return  "SELECT " + columns + " " + query;
    }

    private String prepareQuery(String query) {
        String sqlQuery = compile("FROM", CASE_INSENSITIVE)
                .matcher(query)
                .replaceAll("FROM");
        return sqlQuery.substring(sqlQuery.indexOf("FROM"));
    }
}