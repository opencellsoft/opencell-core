package org.meveo.service.order;

import org.meveo.api.exception.BusinessApiException;
import org.meveo.model.ordering.OpenOrderTemplate;
import org.meveo.model.ordering.OpenOrderTypeEnum;
import org.meveo.model.ordering.Threshold;
import org.meveo.service.base.PersistenceService;

import javax.ejb.Stateless;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static org.meveo.admin.util.CollectionUtil.isNullOrEmpty;

@Stateless
public class OpenOrderTemplateService extends PersistenceService<OpenOrderTemplate> {


    public void create(OpenOrderTemplate entity, List<String> productsOrArticles) {

        checkParameters(entity);
        super.create(entity);
    }

    @Override
    public OpenOrderTemplate update(OpenOrderTemplate entity) {
        checkParameters(entity);
        return super.update(entity);
    }

    private void checkParameters(OpenOrderTemplate openOrderTemplate) {
        checkOpenOrderType(openOrderTemplate);
        checkThresholds(openOrderTemplate.getThresholds());


    }

    private void checkThresholds(List<Threshold> thresholds) {
        if(!isNullOrEmpty(thresholds)){
            thresholds
                    .stream()
                    .filter(threshold -> threshold.getPercentage() >= 1 || threshold.getPercentage() > 100)
                    .findAny()
                    .ifPresent(threshold -> { throw new BusinessApiException("Threshold should be between 1 and 100");});

            List<Threshold> sortedThresholds = thresholds.stream().sorted(Comparator.comparingInt(Threshold::getSequence)).collect(Collectors.toList());

            for(int i=1; i < sortedThresholds.size(); i ++)
            {
                if(thresholds.get(i).getPercentage() < thresholds.get(i-1).getPercentage())
                {
                    throw new BusinessApiException("Threshold sequence and percentage dosn’t match, threshold with high sequence number should contain the highest percentage");
                }
            }
        }

    }

    private void checkOpenOrderType(OpenOrderTemplate openOrderTemplate){
        if(openOrderTemplate.getOpenOrderType() == OpenOrderTypeEnum.ARTICLES && !isNullOrEmpty(openOrderTemplate.getProducts()))
        {
            throw new BusinessApiException("Open order template of type ARTICLE can not be applied on products");
        }

        if(openOrderTemplate.getOpenOrderType() == OpenOrderTypeEnum.PRODUCTS && !isNullOrEmpty(openOrderTemplate.getArticles()))
        {
            throw new BusinessApiException("Open order template of type PRODUCT can not be applied on articles");
        }
    }
}
