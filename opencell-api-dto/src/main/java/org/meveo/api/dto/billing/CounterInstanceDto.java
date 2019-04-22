package org.meveo.api.dto.billing;

import java.util.List;
import java.util.stream.Collectors;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import org.meveo.api.dto.BusinessEntityDto;
import org.meveo.model.billing.CounterInstance;
import org.meveo.model.billing.CounterPeriod;
import org.meveo.model.shared.DateUtils;

/**
 * The Class CounterInstanceDto.
 * 
 * @author anasseh
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class CounterInstanceDto extends BusinessEntityDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -72154111229222183L;

    /**
     * The Counter periods
     */
    private CountersPeriodsDto counterPeriods;

    
    /**
     * Instantiates a new counter instance dto.
     */
    public CounterInstanceDto() {
    }

    /**
     * Instantiates a new counter instance dto.
     *
     * @param counterInstance the counter instance
     */
    public CounterInstanceDto(CounterInstance counterInstance) {
        super(counterInstance);
        fillCounterPeriods(counterInstance.getCounterPeriods());
    }

    private void fillCounterPeriods(List<CounterPeriod> periods) {
        if (periods == null) {
            return;
        }
        List<CounterPeriodDto> counterPeriodDtos = periods.stream().map(cp -> {
            CounterPeriodDto dto = new CounterPeriodDto();
            dto.setCounterType(cp.getCounterType());
            dto.setLevel(cp.getLevel());
            dto.setPeriodEndDate(DateUtils.formatDateWithPattern(cp.getPeriodEndDate(), "yyyy-MM-dd"));
            dto.setPeriodStartDate(DateUtils.formatDateWithPattern(cp.getPeriodStartDate(), "yyyy-MM-dd"));
            dto.setValue(cp.getValue());
            return dto;
        }).collect(Collectors.toList());
        this.counterPeriods = new CountersPeriodsDto(counterPeriodDtos);

    }

    @Override
    public String toString() {
        return "CounterInstanceDto [code=" + code + ", description=" + description + "]";
    }
}