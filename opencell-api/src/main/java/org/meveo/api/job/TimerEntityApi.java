package org.meveo.api.job;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.BaseCrudApi;
import org.meveo.api.dto.job.TimerEntityDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.jobs.TimerEntity;
import org.meveo.service.job.TimerEntityService;

@Stateless
public class TimerEntityApi extends BaseCrudApi<TimerEntity, TimerEntityDto> {

    @Inject
    private TimerEntityService timerEntityService;

    @Override
    public TimerEntity create(TimerEntityDto timerEntityDto) throws MeveoApiException, BusinessException {
        if (StringUtils.isBlank(timerEntityDto.getCode()) || StringUtils.isBlank(timerEntityDto.getHour()) || StringUtils.isBlank(timerEntityDto.getMinute())
                || StringUtils.isBlank(timerEntityDto.getSecond()) || StringUtils.isBlank(timerEntityDto.getYear()) || StringUtils.isBlank(timerEntityDto.getMonth())
                || StringUtils.isBlank(timerEntityDto.getDayOfMonth()) || StringUtils.isBlank(timerEntityDto.getDayOfWeek())) {

            if (StringUtils.isBlank(timerEntityDto.getHour())) {
                missingParameters.add("hour");
            }
            if (StringUtils.isBlank(timerEntityDto.getMinute())) {
                missingParameters.add("minute");
            }
            if (StringUtils.isBlank(timerEntityDto.getSecond())) {
                missingParameters.add("second");
            }
            if (StringUtils.isBlank(timerEntityDto.getYear())) {
                missingParameters.add("year");
            }
            if (StringUtils.isBlank(timerEntityDto.getMonth())) {
                missingParameters.add("month");
            }
            if (StringUtils.isBlank(timerEntityDto.getDayOfMonth())) {
                missingParameters.add("dayOfMonth");
            }
            if (StringUtils.isBlank(timerEntityDto.getDayOfWeek())) {
                missingParameters.add("dayOfWeek");
            }

            handleMissingParameters();

        }

        if (timerEntityService.findByCode(timerEntityDto.getCode()) != null) {
            throw new EntityAlreadyExistsException(TimerEntity.class, timerEntityDto.getCode());
        }

        TimerEntity timerEntity = convertTimerEntityFromDTO(timerEntityDto, null);

        timerEntityService.create(timerEntity);

        return timerEntity;
    }

    @Override
    public TimerEntity update(TimerEntityDto timerEntityDto) throws MeveoApiException, BusinessException {

        String timerEntityCode = timerEntityDto.getCode();

        if (StringUtils.isBlank(timerEntityCode)) {
            missingParameters.add("Code");
            handleMissingParameters();
        }

        TimerEntity timerEntity = timerEntityService.findByCode(timerEntityCode);
        if (timerEntity == null) {
            throw new EntityDoesNotExistsException(TimerEntity.class, timerEntityCode);
        }

        timerEntity = convertTimerEntityFromDTO(timerEntityDto, timerEntity);

        timerEntity = timerEntityService.update(timerEntity);

        return timerEntity;
    }

    @Override
    public TimerEntityDto find(String timerEntityCode) throws EntityDoesNotExistsException, MissingParameterException, InvalidParameterException, MeveoApiException {
        TimerEntityDto result = new TimerEntityDto();
        if (StringUtils.isBlank(timerEntityCode)) {
            missingParameters.add("code");
            handleMissingParameters();
        }
        TimerEntity timerEntity = timerEntityService.findByCode(timerEntityCode);
        if (timerEntity == null) {
            throw new EntityDoesNotExistsException(timerEntityCode.getClass(), timerEntityCode);
        }
        result = new TimerEntityDto(timerEntity);

        return result;
    }

    private TimerEntity convertTimerEntityFromDTO(TimerEntityDto dto, TimerEntity timerEntityToUpdate) {
        TimerEntity timerEntity = timerEntityToUpdate;
        if (timerEntityToUpdate == null) {
            timerEntity = new TimerEntity();
            if (dto.isDisabled() != null) {
                timerEntity.setDisabled(dto.isDisabled());
            }
        }

        timerEntity.setCode(StringUtils.isBlank(dto.getUpdatedCode()) ? dto.getCode() : dto.getUpdatedCode());
        timerEntity.setDescription(dto.getDescription());
        timerEntity.setYear(dto.getYear());
        timerEntity.setMonth(dto.getMonth());
        timerEntity.setDayOfMonth(dto.getDayOfMonth());
        timerEntity.setDayOfWeek(dto.getDayOfWeek());
        timerEntity.setHour(dto.getHour());
        timerEntity.setMinute(dto.getMinute());
        timerEntity.setSecond(dto.getSecond());

        return timerEntity;
    }
}