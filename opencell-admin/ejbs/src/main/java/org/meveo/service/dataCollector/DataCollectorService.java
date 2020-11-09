package org.meveo.service.dataCollector;

import static java.lang.String.format;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static org.meveo.service.base.ValueExpressionWrapper.evaluateExpression;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.bi.DataCollector;
import org.meveo.model.customEntities.CustomEntityTemplate;
import org.meveo.service.base.BusinessService;
import org.meveo.service.custom.CustomEntityTemplateService;
import org.meveo.service.custom.CustomTableService;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Stateless
public class DataCollectorService extends BusinessService<DataCollector> {

    private static final String META_DATA_QUERY_STRING = "SELECT column_name \nFROM information_schema.columns \nWHERE table_name = :table_name";
    private static final String NAMED_QUERY_NAME = "DataCollector.dataCollectorsBetween";

    @Inject
    private CustomEntityTemplateService customEntityTemplateService;
    @Inject
    private CustomTableService customTableService;

    @Override
    public void create(DataCollector entity) throws BusinessException {
        ofNullable(customEntityTemplateService.findByCode(entity.getCustomTableCode()))
                .orElseThrow(() ->
                        new BusinessException(format("Custom Table with code %s does not exists", entity.getCustomTableCode())));
        ofNullable(entity.getSqlQuery()).orElseThrow(() ->
                        new BusinessException(format("Missing SQL Query")));
        super.create(entity);
    }

    @TransactionAttribute(TransactionAttributeType.NEVER)
    public int executeQuery(String dataCollectorCode) {
        DataCollector dataCollector = ofNullable(findByCode(dataCollectorCode))
                .orElseThrow(() -> new BusinessException(format("Data Collector%s does not exists", dataCollectorCode)));
        List<Map<String, Object>> customTableColumns = tableMetaData(dataCollector.getCustomTableCode().toLowerCase());
        checkAliasesWithCTColumns(dataCollector.getAliases(), customTableColumns);

        CustomEntityTemplate customTable =  customEntityTemplateService.findByCode(dataCollector.getCustomTableCode());

        List<Map<String, Object>> requestResult = executeNativeSelectQuery(dataCollector.getSqlQuery(), null);
        log.info(">>>> Import query result to custom table");
        return customTableService.importData(customTable, requestResult,true);
    }

    private List<Map<String, Object>> tableMetaData(String tableName) {
        return executeNativeSelectQuery(META_DATA_QUERY_STRING, Map.of("table_name", tableName));
    }

    private void checkAliasesWithCTColumns(Map<String, String> queryResultColumns, List<Map<String, Object>> ctColumns) {
        List<String> columns = ctColumns.stream()
                .map(Map::entrySet)
                .flatMap(entries -> entries.stream().map(entry -> (String) entry.getValue()))
                .collect(toList());
        int requestQueryColumnsNumber = queryResultColumns.size();
        if (requestQueryColumnsNumber > columns.size()) {
            throw new BusinessException("Request result columns aliases must be the same as custom table columns");
        }

        for (Map.Entry<String, String> entry : queryResultColumns.entrySet()) {
            if(!columns.contains(entry.getValue())) {
                throw new BusinessException("Request result columns aliases must be the same as custom table columns");
            }
        }
    }

    public List<Map<String, Object>> aggregatedData(String customTableCode, String dataCollectorCode,
                                                    Map<String, String> aggregationFields, List<String> fields) {
        CustomEntityTemplate customEntityTemplate = ofNullable(customEntityTemplateService.findByCode(customTableCode))
                .orElseThrow(() ->
                        new BusinessException(format("Custom Table with code %s does not exists", customTableCode)));
        DataCollector dataCollector = ofNullable(findByCode(dataCollectorCode))
                .orElseThrow(() -> new BusinessException(format("Data Collector%s does not exists", dataCollectorCode)));
        Map<Object, Object> context = new HashMap<>();
        context.put("dataCollector", dataCollector);
        context.put("customEntity", customEntityTemplate);

        String aggregationQuery = buildAggregationQuery(customTableCode, aggregationFields, fields, context);
        return executeNativeSelectQuery(aggregationQuery, null);
    }

    private String buildAggregationQuery(String customTableCode, Map<String, String> aggregationFields,
                                         List<String> fields, Map<Object, Object> context) {
        StringBuilder aggregationQuery = new StringBuilder();
        String aggregation = aggregationFields.entrySet().stream()
                .map(entry -> toQueryField(entry.getKey(), entry.getValue(), context))
                .collect(joining(", "));
        aggregationQuery.append("SELECT ").append(aggregation);
        if(fields != null) {
            addFields(aggregationQuery, fields);
            aggregationQuery.append(" FROM ").append(customTableCode).append(addGroupBy(fields));
        } else {
            aggregationQuery.append(" FROM ").append(customTableCode);
        }
        return aggregationQuery.toString();
    }

    private String toQueryField(String expression, String function, Map<Object, Object> context) {
        String result = evaluateExpression(expression, context, String.class);
        return function + "(" + result + ") as " + result + function.toUpperCase();
    }

    private void addFields(StringBuilder query, List<String> fields) {
        for (String field : fields) {
            query.append(", " + field);
        }
    }

    private String addGroupBy(List<String> fields) {
        return  " GROUP BY " + fields.stream().collect(joining(" , "));
    }

    @TransactionAttribute(TransactionAttributeType.NEVER)
    public Map<String, Integer> execute(Date from, Date to) {
        List<DataCollector> collectors = listByNamedQuery(NAMED_QUERY_NAME, "from", from, "to" , to);
        Map<String, Integer> result = new HashMap<>();
        for (DataCollector dataCollector : collectors) {
            result.put(dataCollector.getCode(), executeQuery(dataCollector.getCode()));
        }
        return result;
    }
}