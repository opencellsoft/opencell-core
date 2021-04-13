package org.meveo.api.mapper;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.meveo.api.dto.*;
import org.meveo.api.dto.account.BusinessAccountModelDto;
import org.meveo.api.dto.catalog.*;
import org.meveo.api.dto.dwh.BarChartDto;
import org.meveo.api.dto.dwh.LineChartDto;
import org.meveo.api.dto.dwh.MeasurableQuantityDto;
import org.meveo.api.dto.dwh.PieChartDto;
import org.meveo.api.dto.job.JobInstanceDto;
import org.meveo.api.dto.job.TimerEntityDto;
import org.meveo.api.dto.module.MeveoModuleDto;
import org.meveo.api.dto.notification.EmailNotificationDto;
import org.meveo.api.dto.notification.JobTriggerDto;
import org.meveo.api.dto.notification.ScriptNotificationDto;
import org.meveo.api.dto.notification.WebHookDto;
import org.meveo.api.dto.payment.DDRequestBuilderDto;
import org.meveo.api.dto.payment.PaymentGatewayDto;
import org.meveo.api.dto.payment.WorkflowDto;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * A custom deserializer for list of ModuleItems
 *
 * @author Thang Nguyen
 * @since April 13, 2021
 */
public class ModuleItemListDeserializer extends JsonDeserializer<List<BaseEntityDto>> {

    @Override
    public List<BaseEntityDto> deserialize(JsonParser jp, DeserializationContext ctx) throws IOException {
        List<BaseEntityDto> moduleItemsList = new ArrayList<>();
        ObjectCodec codec = jp.getCodec();
        JsonNode node = codec.readTree(jp);
        ObjectMapper mapper = new ObjectMapper();
        if ( node.isArray() ) {
            ArrayNode arrayNode = (ArrayNode) node;

            for (int i = 0; i < arrayNode.size(); i++) {
                ObjectNode objectNode = (ObjectNode) arrayNode.get(i);

                if ( objectNode.has("customEntityTemplate") )
                    moduleItemsList.add(mapper.readValue(String.valueOf(objectNode.get("customEntityTemplate")), CustomEntityTemplateDto.class));
                else if ( objectNode.has("customFieldTemplate") )
                    moduleItemsList.add(mapper.readValue(String.valueOf(objectNode.get("customFieldTemplate")), CustomFieldTemplateDto.class));
                else if ( objectNode.has("filter") )
                    moduleItemsList.add(mapper.readValue(String.valueOf(objectNode.get("filter")), FilterDto.class));
                else if ( objectNode.has("jobInstance") )
                    moduleItemsList.add(mapper.readValue(String.valueOf(objectNode.get("jobInstance")), JobInstanceDto.class));
                else if ( objectNode.has("script") )
                    moduleItemsList.add(mapper.readValue(String.valueOf(objectNode.get("script")), ScriptInstanceDto.class));
                else if ( objectNode.has("notification") )
                    moduleItemsList.add(mapper.readValue(String.valueOf(objectNode.get("notification")), ScriptNotificationDto.class));
                else if ( objectNode.has("timerEntity") )
                    moduleItemsList.add(mapper.readValue(String.valueOf(objectNode.get("timerEntity")), TimerEntityDto.class));
                else if ( objectNode.has("emailNotif") )
                    moduleItemsList.add(mapper.readValue(String.valueOf(objectNode.get("emailNotif")), EmailNotificationDto.class));
                else if ( objectNode.has("jobTrigger") )
                    moduleItemsList.add(mapper.readValue(String.valueOf(objectNode.get("jobTrigger")), JobTriggerDto.class));
                else if ( objectNode.has("webhookNotif") )
                    moduleItemsList.add(mapper.readValue(String.valueOf(objectNode.get("webhookNotif")), WebHookDto.class));
                else if ( objectNode.has("counter") )
                    moduleItemsList.add(mapper.readValue(String.valueOf(objectNode.get("counter")), CounterTemplateDto.class));
                else if ( objectNode.has("businessAccountModel") )
                    moduleItemsList.add(mapper.readValue(String.valueOf(objectNode.get("businessAccountModel")), BusinessAccountModelDto.class));
                else if ( objectNode.has("businessServiceModel") )
                    moduleItemsList.add(mapper.readValue(String.valueOf(objectNode.get("businessServiceModel")), BusinessServiceModelDto.class));
                else if ( objectNode.has("businessProductModel") )
                    moduleItemsList.add(mapper.readValue(String.valueOf(objectNode.get("businessProductModel")), BusinessProductModelDto.class));
                else if ( objectNode.has("businessOfferModel") )
                    moduleItemsList.add(mapper.readValue(String.valueOf(objectNode.get("businessOfferModel")), BusinessOfferModelDto.class));
                else if ( objectNode.has("subModule") )
                    moduleItemsList.add(mapper.readValue(String.valueOf(objectNode.get("subModule")), MeveoModuleDto.class));
                else if ( objectNode.has("measurableQuantity") )
                    moduleItemsList.add(mapper.readValue(String.valueOf(objectNode.get("measurableQuantity")), MeasurableQuantityDto.class));
                else if ( objectNode.has("pieChart") )
                    moduleItemsList.add(mapper.readValue(String.valueOf(objectNode.get("pieChart")), PieChartDto.class));
                else if ( objectNode.has("lineChart") )
                    moduleItemsList.add(mapper.readValue(String.valueOf(objectNode.get("lineChart")), LineChartDto.class));
                else if ( objectNode.has("barChart") )
                    moduleItemsList.add(mapper.readValue(String.valueOf(objectNode.get("barChart")), BarChartDto.class));
                else if ( objectNode.has("recurringChargeTemplate") )
                    moduleItemsList.add(mapper.readValue(String.valueOf(objectNode.get("recurringChargeTemplate")), RecurringChargeTemplateDto.class));
                else if ( objectNode.has("usageChargeTemplate") )
                    moduleItemsList.add(mapper.readValue(String.valueOf(objectNode.get("usageChargeTemplate")), UsageChargeTemplateDto.class));
                else if ( objectNode.has("oneShotChargeTemplate") )
                    moduleItemsList.add(mapper.readValue(String.valueOf(objectNode.get("oneShotChargeTemplate")), OneShotChargeTemplateDto.class));
                else if ( objectNode.has("productChargeTemplate") )
                    moduleItemsList.add(mapper.readValue(String.valueOf(objectNode.get("productChargeTemplate")), ProductChargeTemplateDto.class));
                else if ( objectNode.has("counterTemplate") )
                    moduleItemsList.add(mapper.readValue(String.valueOf(objectNode.get("counterTemplate")), CounterTemplateDto.class));
                else if ( objectNode.has("pricePlanMatrix") )
                    moduleItemsList.add(mapper.readValue(String.valueOf(objectNode.get("pricePlanMatrix")), PricePlanMatrixDto.class));
                else if ( objectNode.has("entityCustomAction") )
                    moduleItemsList.add(mapper.readValue(String.valueOf(objectNode.get("entityCustomAction")), EntityCustomActionDto.class));
                else if ( objectNode.has("workflow") )
                    moduleItemsList.add(mapper.readValue(String.valueOf(objectNode.get("workflow")), WorkflowDto.class));
                else if ( objectNode.has("offerTemplate") )
                    moduleItemsList.add(mapper.readValue(String.valueOf(objectNode.get("offerTemplate")), OfferTemplateDto.class));
                else if ( objectNode.has("productTemplate") )
                    moduleItemsList.add(mapper.readValue(String.valueOf(objectNode.get("productTemplate")), ProductTemplateDto.class));
                else if ( objectNode.has("bundleTemplate") )
                    moduleItemsList.add(mapper.readValue(String.valueOf(objectNode.get("bundleTemplate")), BundleTemplateDto.class));
                else if ( objectNode.has("serviceTemplate") )
                    moduleItemsList.add(mapper.readValue(String.valueOf(objectNode.get("serviceTemplate")), ServiceTemplateDto.class));
                else if ( objectNode.has("offerTemplateCategory") )
                    moduleItemsList.add(mapper.readValue(String.valueOf(objectNode.get("offerTemplateCategory")), OfferTemplateCategoryDto.class));
                else if ( objectNode.has("paymentGateway") )
                    moduleItemsList.add(mapper.readValue(String.valueOf(objectNode.get("paymentGateway")), PaymentGatewayDto.class));
                else if ( objectNode.has("ddRequestBuilder") )
                    moduleItemsList.add(mapper.readValue(String.valueOf(objectNode.get("ddRequestBuilder")), DDRequestBuilderDto.class));
            }
        }

        return moduleItemsList;
    }

    public BaseEntityDto deserialize(JsonParser jsonParser, JsonNode node, ObjectMapper mapper, BaseEntityDto aDto) throws IOException {
        if ( node.isObject() ) {
            ObjectNode objectNode = (ObjectNode) node;
            Iterator<Map.Entry<String, JsonNode>> fieldsIterator = objectNode.fields();
            while (fieldsIterator.hasNext()) {
                Map.Entry<String, JsonNode> field = fieldsIterator.next();
                final String key = field.getKey();
                final JsonNode value = field.getValue();

                if ( key.equals("emailNotif") ) {
                    mapper.readValue(String.valueOf(value), EmailNotificationDto.class);
                }

                if (value.isContainerNode()) {
                    deserialize(jsonParser, value, mapper, aDto);
                }
            }
        }
        else if ( node.isArray() ) {
            ArrayNode arrayNode = (ArrayNode) node;

            for (int i = 0; i < arrayNode.size(); i++) {
                deserialize(jsonParser, arrayNode.get(i), mapper, aDto);
            }
        }

        return null;
    }
}
