package org.meveo.api.custom;

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static java.util.Optional.ofNullable;

import org.meveo.api.BaseApi;
import org.meveo.api.dto.custom.GenericCodeDto;
import org.meveo.api.dto.custom.GetGenericCodeResponseDto;
import org.meveo.api.dto.custom.SequenceDto;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.model.admin.CustomGenericEntityCode;
import org.meveo.model.sequence.Sequence;
import org.meveo.model.sequence.SequenceTypeEnum;
import org.meveo.service.admin.impl.CustomGenericEntityCodeService;
import org.meveo.service.admin.impl.SequenceService;
import org.meveo.service.billing.impl.ServiceSingleton;

import javax.ejb.Stateless;
import javax.inject.Inject;

import java.util.Optional;

@Stateless
public class GenericCodeApi extends BaseApi {

    @Inject
    private CustomGenericEntityCodeService customGenericEntityCodeService;

    @Inject
    private ServiceSingleton serviceSingleton;

    @Inject
    private SequenceService sequenceService;

    public void create(GenericCodeDto genericCodeDto) {
        if (genericCodeDto.getSequence() == null) {
            throw new MeveoApiException("Missing sequence");
        }
        String sequenceCode = genericCodeDto.getSequence().getCode();
        if (genericCodeDto.getSequence() != null && genericCodeDto.getSequence().getCode() == null)  {
            throw new MeveoApiException("Missing sequence code");
        }
        Sequence sequence = sequenceService.findByCode(sequenceCode);
        if(sequence == null) {
            createSequence(genericCodeDto.getSequence());
            sequence = sequenceService.findByCode(sequenceCode);
        }
        CustomGenericEntityCode customGenericEntityCode = from(genericCodeDto, sequence);
        customGenericEntityCodeService.create(customGenericEntityCode);
    }

    public void update(GenericCodeDto genericCodeDto) {
        CustomGenericEntityCode customGenericEntityCode = ofNullable(customGenericEntityCodeService.findByClass(genericCodeDto.getEntityClass()))
                .orElseThrow(() -> new MeveoApiException("Generic code does not exist"));
        String sequenceCode = genericCodeDto.getSequence().getCode();
        ofNullable(sequenceService.findByCode(sequenceCode))
                .orElseThrow(() -> new MeveoApiException("Sequence does not exist"));
        customGenericEntityCodeService.update(toEntity(genericCodeDto, customGenericEntityCode));
    }

    private CustomGenericEntityCode toEntity(GenericCodeDto genericCodeDto, CustomGenericEntityCode customGenericEntityCode) {
        Sequence sequence = customGenericEntityCode.getSequence();
        SequenceDto dto = genericCodeDto.getSequence();
        sequence.setSequenceType(SequenceTypeEnum.fromValue(dto.getSequenceType().name()));
        sequence.setSequenceSize(dto.getSize());
        sequence.setSequencePattern(dto.getPattern());
        sequence.setCurrentNumber(dto.getCurrentNumber());
        customGenericEntityCode.setEntityClass(genericCodeDto.getEntityClass());
        customGenericEntityCode.setFormatEL(genericCodeDto.getFormatEL());
        customGenericEntityCode.setSequence(sequence);
        return customGenericEntityCode;
    }

    public void createSequence(SequenceDto sequenceDto) {
        Sequence sequence = SequenceDto.from(sequenceDto);
        sequenceService.create(sequence);
    }

    public void updateSequence(SequenceDto sequenceDto) {
        Sequence sequence = SequenceDto.from(sequenceDto);
        sequenceService.findByCode(sequence.getCode());
        sequenceService.update(sequence);
    }

    public String getGenericCode(GenericCodeDto genericCodeDto) {
        CustomGenericEntityCode customGenericEntityCode = ofNullable(customGenericEntityCodeService
                .findByClass(genericCodeDto.getEntityClass())).orElseThrow(() -> new MeveoApiException("Generic code does not exists"));
        String result;
        if (genericCodeDto.getPrefixOverride() != null) {
          result = serviceSingleton.getGenericCode(customGenericEntityCode, genericCodeDto.getPrefixOverride());
        } else {
            result = serviceSingleton.getGenericCode(customGenericEntityCode);
        }
        return result;
    }

    public Optional<GetGenericCodeResponseDto> find(String entityClass) {
        CustomGenericEntityCode customGenericEntityCode = customGenericEntityCodeService.findByClass(entityClass);
        if(customGenericEntityCode != null) {
            return of(from(customGenericEntityCode));
        }
        return empty();
    }

    public CustomGenericEntityCode from(GenericCodeDto dto, Sequence sequence) {
        CustomGenericEntityCode customGenericEntityCode = new CustomGenericEntityCode();
        customGenericEntityCode.setFormatEL(dto.getFormatEL());
        customGenericEntityCode.setEntityClass(dto.getEntityClass());
        customGenericEntityCode.setSequence(sequence);
        return customGenericEntityCode;
    }

    public GetGenericCodeResponseDto from(CustomGenericEntityCode customGenericEntityCode) {
        GenericCodeDto genericCodeDto = new GenericCodeDto();
        genericCodeDto.setEntityClass(customGenericEntityCode.getEntityClass());
        genericCodeDto.setFormatEL(customGenericEntityCode.getFormatEL());
        genericCodeDto.setSequence(SequenceDto.from(customGenericEntityCode.getSequence()));
        GetGenericCodeResponseDto responseDto = new GetGenericCodeResponseDto();
        responseDto.setGenericCodeDto(genericCodeDto);
        return responseDto;
    }
}