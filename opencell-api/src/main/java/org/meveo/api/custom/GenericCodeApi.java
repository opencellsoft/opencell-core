package org.meveo.api.custom;

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static java.util.Optional.ofNullable;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.meveo.api.dto.custom.SequenceType.ALPHA_UP;
import static org.meveo.api.dto.custom.SequenceType.REGEXP;

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

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;

import java.util.Optional;

@Stateless
public class GenericCodeApi extends BaseApi {

    @Inject
    private CustomGenericEntityCodeService customGenericEntityCodeService;

    @Inject
    private ServiceSingleton serviceSingleton;

    @Inject
    private SequenceService sequenceService;

    /**
     * Create new generic code
     *
     * @param genericCodeDto generic code data.
     */
    public void create(GenericCodeDto genericCodeDto) {
        if (genericCodeDto.getSequence() == null) {
            missingParameters.add("sequence");
        }
        if(genericCodeDto.getEntityClass() == null) {
            missingParameters.add("entity class");
        }
        if (genericCodeDto.getSequence() != null && genericCodeDto.getSequence().getCode() == null)  {
            missingParameters.add("Sequence code");
        }
        handleMissingParameters();
        validateEntityClass(genericCodeDto.getEntityClass());
        String sequenceCode = genericCodeDto.getSequence().getCode();
        Sequence sequence = sequenceService.findByCode(sequenceCode);
        if(sequence == null) {
            sequence = createSequence(genericCodeDto.getSequence());
        }
        CustomGenericEntityCode customGenericEntityCode = from(genericCodeDto, sequence);
        customGenericEntityCodeService.create(customGenericEntityCode);
    }

    private void validateEntityClass(String entityClass) {
        try {
            Class.forName(entityClass);
        } catch (ClassNotFoundException exception) {
            throw new MeveoApiException("No entity class found for the given input: " + entityClass);
        }
    }

    private CustomGenericEntityCode from(GenericCodeDto dto, Sequence sequence) {
        CustomGenericEntityCode customGenericEntityCode = new CustomGenericEntityCode();
        ofNullable(dto.getFormatEL())
                .ifPresent(customGenericEntityCode::setFormatEL);
        customGenericEntityCode.setEntityClass(dto.getEntityClass());
        customGenericEntityCode.setSequence(sequence);
        return customGenericEntityCode;
    }

    /**
     * Update generic code
     *
     * @param genericCodeDto generic code data.
     */
    public void update(GenericCodeDto genericCodeDto) {
        CustomGenericEntityCode customGenericEntityCode = ofNullable(customGenericEntityCodeService.findByClass(genericCodeDto.getEntityClass()))
                .orElseThrow(() -> new MeveoApiException("Generic code does not exist"));
        if(genericCodeDto.getSequence() != null && sequenceService.findByCode(genericCodeDto.getSequence().getCode()) == null) {
            throw new MeveoApiException("Sequence does not exist");
        }
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

    /**
     * Create new sequence
     *
     * @param sequenceDto sequence data.
     * @return created sequence
     */
    public Sequence createSequence(SequenceDto sequenceDto) {
        validateInputs(sequenceDto);
        Sequence sequence = SequenceDto.from(sequenceDto);
        sequenceService.create(sequence);
        return sequence;
    }

    /**
     * Update sequence
     *
     * @param sequenceDto sequence data.
     * @return updated sequence
     */
    public Sequence updateSequence(SequenceDto sequenceDto) {
        validateInputs(sequenceDto);
        Sequence sequence = SequenceDto.from(sequenceDto);
        sequenceService.findByCode(sequence.getCode());
        return sequenceService.update(sequence);
    }

    private void validateInputs(SequenceDto sequenceDto) {
        if(sequenceDto != null) {
            if (isBlank(sequenceDto.getCode())) {
                missingParameters.add("code");
            }
            if (sequenceDto.getSequenceType() == null || isBlank(sequenceDto.getSequenceType().name())) {
                missingParameters.add("Sequence Type");
            }
            if (sequenceDto.getSequenceType() == REGEXP) {
                if(sequenceDto.getPattern() == null) {
                    throw new MeveoApiException("Sequence pattern is required");
                }
                if(sequenceDto.getSize() == null) {
                    throw new MeveoApiException("Sequence size is required");
                }
            }
            if (sequenceDto.getSequenceType() == ALPHA_UP && sequenceDto.getSize() == null) {
                throw  new MeveoApiException("Sequence size is required");
            }
            handleMissingParameters();
        } else {
            throw new MeveoApiException("No data provided, dto is null");
        }
    }

    /**
     * Generate a generic code
     *
     * @param genericCodeDto
     * @return generated code
     */
    public String getGenericCode(GenericCodeDto genericCodeDto) {
        CustomGenericEntityCode customGenericEntityCode = ofNullable(customGenericEntityCodeService
                .findByClass(genericCodeDto.getEntityClass()))
                .orElseThrow(() -> new MeveoApiException("No Generic code associated to entity class : " + genericCodeDto.getEntityClass()));
        String result;
        if (genericCodeDto.getPrefixOverride() != null) {
          result = serviceSingleton.getGenericCode(customGenericEntityCode, genericCodeDto.getPrefixOverride());
        } else {
            result = serviceSingleton.getGenericCode(customGenericEntityCode);
        }
        return result;
    }

    /**
     * Find a generic custom code by associated entity class
     *
     * @param entityClass
     * @return Optional<GetGenericCodeResponseDto>
     */
    public Optional<GetGenericCodeResponseDto> find(String entityClass) {
        CustomGenericEntityCode customGenericEntityCode = customGenericEntityCodeService.findByClass(entityClass);
        if(customGenericEntityCode != null) {
            return of(from(customGenericEntityCode));
        }
        return empty();
    }

    private GetGenericCodeResponseDto from(CustomGenericEntityCode customGenericEntityCode) {
        GenericCodeDto genericCodeDto = new GenericCodeDto();
        genericCodeDto.setEntityClass(customGenericEntityCode.getEntityClass());
        genericCodeDto.setFormatEL(customGenericEntityCode.getFormatEL());
        genericCodeDto.setSequence(SequenceDto.from(customGenericEntityCode.getSequence()));
        GetGenericCodeResponseDto responseDto = new GetGenericCodeResponseDto();
        responseDto.setGenericCodeDto(genericCodeDto);
        return responseDto;
    }
}