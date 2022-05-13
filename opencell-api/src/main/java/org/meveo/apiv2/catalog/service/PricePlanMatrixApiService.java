
package org.meveo.apiv2.catalog.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.meveo.api.catalog.PricePlanMatrixLineApi;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.response.catalog.ImportResultResponseDto.ImportResultDto;
import org.meveo.api.dto.response.catalog.PricePlanMatrixLinesDto;
import org.meveo.api.exception.BusinessApiException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.apiv2.catalog.ImportPricePlanVersionsDto;
import org.meveo.apiv2.catalog.ImportPricePlanVersionsItem;
import org.meveo.apiv2.catalog.PricePlanMLinesDTO;
import org.meveo.apiv2.ordering.services.ApiService;
import org.meveo.commons.utils.FileUtils;
import org.meveo.commons.utils.ParamBeanFactory;
import org.meveo.commons.utils.StringUtils;
import org.meveo.jpa.EntityManagerWrapper;
import org.meveo.jpa.MeveoJpa;
import org.meveo.model.DatePeriod;
import org.meveo.model.catalog.PricePlanMatrix;
import org.meveo.model.catalog.PricePlanMatrixVersion;
import org.meveo.model.cpq.enums.VersionStatusEnum;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.catalog.impl.PricePlanMatrixColumnService;
import org.meveo.service.catalog.impl.PricePlanMatrixService;
import org.meveo.service.catalog.impl.PricePlanMatrixVersionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PricePlanMatrixApiService implements ApiService<PricePlanMatrix> {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Inject
    private PricePlanMatrixLineApi pricePlanMatrixLineApi;

    @Inject
    private PricePlanMatrixColumnService pricePlanMatrixColumnService;

    @Inject
    private PricePlanMatrixVersionService pricePlanMatrixVersionService;

    @Inject
    private PricePlanMatrixService pricePlanMatrixService;

    @Inject
    private ParamBeanFactory paramBeanFactory;

    @Inject
    @MeveoJpa
    private EntityManagerWrapper emWrapper;

    @Override
    public List<PricePlanMatrix> list(Long offset, Long limit, String sort, String orderBy, String filter) {
        return Collections.emptyList();
    }

    @Override
    public Long getCount(String filter) {
        return null;
    }

    @Override
    public Optional<PricePlanMatrix> findById(Long id) {
        return Optional.empty();
    }

    @Override
    public PricePlanMatrix create(PricePlanMatrix baseEntity) {
        return null;
    }

    @Override
    public Optional<PricePlanMatrix> update(Long id, PricePlanMatrix baseEntity) {
        return Optional.empty();
    }

    @Override
    public Optional<PricePlanMatrix> patch(Long id, PricePlanMatrix baseEntity) {
        return Optional.empty();
    }

    @Override
    public Optional<PricePlanMatrix> delete(Long id) {
        return Optional.empty();
    }

    @Override
    public Optional<PricePlanMatrix> findByCode(String code) {
        return Optional.empty();
    }

    public PricePlanMatrixLinesDto updatePricePlanMatrixLines(PricePlanMLinesDTO pricePlanMLinesDTO, String data, PricePlanMatrixVersion pricePlanMatrixVersion) {
        return pricePlanMatrixColumnService.populateLinesAndValues(pricePlanMLinesDTO.getPricePlanMatrixCode(), data, pricePlanMatrixVersion);
    }

    public List<ImportResultDto> importPricePlanMatrixVersions(ImportPricePlanVersionsDto importPricePlanVersionsDto) {
        if (importPricePlanVersionsDto.getPricePlanVersions() == null || importPricePlanVersionsDto.getPricePlanVersions().isEmpty()) {
            throw new MissingParameterException("pricePlanVersions");
        }

        if (StringUtils.isBlank(importPricePlanVersionsDto.getFileToImport())) {
            throw new MissingParameterException("fileToImport");
        }

        unzipFile(importPricePlanVersionsDto.getFileToImport());

        List<ImportResultDto> resultDtos = new ArrayList<>();

        for (ImportPricePlanVersionsItem importItem : importPricePlanVersionsDto.getPricePlanVersions()) {

            Date newFrom = importItem.getStartDate();
            Date newTo = importItem.getEndDate();
            String newChargeCode = importItem.getChargeCode();

            ImportResultDto resultDto = new ImportResultDto();
            resultDto.setFileName(importItem.getFileName());
            resultDto.setChargeCode(newChargeCode);
            resultDto.setUploadAs(importItem.getStatus());
            resultDto.setStartDate(newFrom);
            resultDto.setEndDate(newTo);

            try {

                if (newFrom == null) {
                    throw new BusinessApiException("The start date name is mandatory");
                }
                if (newFrom != null && newFrom.before(DateUtils.setTimeToZero(new Date()))) {
                    throw new BusinessApiException("Uploaded PV cannot start before today");
                }
                if (newFrom != null && newTo != null && newTo.before(newFrom)) {
                    throw new BusinessApiException("Invalid validity period, the end date must be greather than the start date");
                }

                if (StringUtils.isBlank(importItem.getFileName())) {
                    throw new BusinessApiException("The file name is mandatory");
                }
                String pathName = getTempDir() + File.separator + importItem.getFileName();
                if (!new File(pathName).exists()) {
                    throw new BusinessApiException("The file: '" + pathName + "' does not exist");
                }

                // File name pattern: [Price plan version identifier]_-_[Charge name]_-_[Charge code]_-_[Label of the price version]_-_[Status of price version]_-_[start
                // date]_-_[end date]
                Long pricePlanVersionId = Long.parseLong(importItem.getFileName().split("_-_")[0]);
                PricePlanMatrixVersion ppmvToUpdate = pricePlanMatrixVersionService.findById(pricePlanVersionId);
                if (ppmvToUpdate == null) {
                    throw new EntityDoesNotExistsException(PricePlanMatrixVersion.class, pricePlanVersionId);
                }
                PricePlanMatrix pricePlanMatrix = ppmvToUpdate.getPricePlanMatrix();

                // Check if the charge is changed
                if (StringUtils.isNotBlank(newChargeCode) && !newChargeCode.equals(pricePlanMatrix.getEventCode())) {

                    List<PricePlanMatrix> pricePlanMatrixs = pricePlanMatrixService.listByChargeCode(newChargeCode);
                    if (pricePlanMatrixs.size() == 0) {
                        throw new BusinessApiException("No PricePlanMatrix related to the charge '" + newChargeCode + "'");
                    }
                    if (pricePlanMatrixs.size() > 1) {
                        throw new BusinessApiException("There are several PricePlanMatrix related to this charge");
                    }

                    pricePlanMatrix = pricePlanMatrixs.get(0);
                    ppmvToUpdate.setPricePlanMatrix(pricePlanMatrix);
                    ppmvToUpdate.setCurrentVersion(pricePlanMatrixVersionService.getLastVersion(pricePlanMatrix.getCode()) + 1);
                    pricePlanMatrixVersionService.update(ppmvToUpdate);
                }

                if (importItem.getStatus() == VersionStatusEnum.PUBLISHED) {

                    List<PricePlanMatrixVersion> previousPVs = pricePlanMatrixVersionService.findToDateAfterDateAndVersion(pricePlanMatrix, newFrom,
                        ppmvToUpdate.getCurrentVersion());
                    for (PricePlanMatrixVersion previousPV : previousPVs) {
                        if (previousPV.getId() != pricePlanVersionId && previousPV.getValidity().getFrom() != null && previousPV.getValidity().getFrom().compareTo(newFrom) > 0
                                && previousPV.getValidity().getTo() != null && previousPV.getValidity().getTo().compareTo(newFrom) > 0) {
                            pricePlanMatrixVersionService.remove(previousPV);
                        } else {
                            previousPV.setValidity(new DatePeriod(previousPV.getValidity().getFrom(), newFrom));
                            pricePlanMatrixVersionService.update(previousPV);
                            break;
                        }
                    }

                    if (newTo != null) {
                        List<PricePlanMatrixVersion> nextPVs = pricePlanMatrixVersionService.findFromDateBeforeDateAndVersion(pricePlanMatrix, newTo,
                            ppmvToUpdate.getCurrentVersion());
                        for (PricePlanMatrixVersion nextPV : nextPVs) {
                            if (nextPV.getId() != pricePlanVersionId && nextPV.getValidity().getTo() != null && nextPV.getValidity().getTo().compareTo(newTo) < 0) {
                                pricePlanMatrixVersionService.remove(nextPV);
                            } else {
                                nextPV.setValidity(new DatePeriod(newTo, nextPV.getValidity().getTo()));
                                pricePlanMatrixVersionService.update(nextPV);
                                break;
                            }
                        }
                    } else {
                        List<PricePlanMatrixVersion> nextPVs = pricePlanMatrixVersionService.findFromDateAfterDateAndVersion(pricePlanMatrix, newFrom,
                            ppmvToUpdate.getCurrentVersion());
                        for (PricePlanMatrixVersion nextPV : nextPVs) {
                            pricePlanMatrixVersionService.remove(nextPV);
                        }
                    }
                }

                try (FileInputStream fs = new FileInputStream(pathName);
                        InputStreamReader isr = new InputStreamReader(fs, StandardCharsets.UTF_8);
                        LineNumberReader lnr = new LineNumberReader(isr)) {

                    String header = eliminateBOM(lnr.readLine());

                    if ("id;label;amount".equals(header)) {
                        String firstLine = lnr.readLine();
                        String[] split = firstLine.split(";");
                        ppmvToUpdate.setMatrix(false);
                        ppmvToUpdate.setLabel(split[1]);
                        ppmvToUpdate.setAmountWithoutTax(new BigDecimal(split[2]));
                    } else if (StringUtils.isNotBlank(header)) {
                        String data = new StringBuilder(header).append("\n").append(readAllLines(lnr)).toString();
                        ppmvToUpdate.setMatrix(true);
                        PricePlanMatrixLinesDto pricePlanMatrixLinesDto = pricePlanMatrixColumnService.populateLinesAndValues(pricePlanMatrix.getCode(), data, ppmvToUpdate);
                        pricePlanMatrixLineApi.updatePricePlanMatrixLines(pricePlanMatrix.getCode(), ppmvToUpdate.getCurrentVersion(), pricePlanMatrixLinesDto, false);
                    }

                    DatePeriod validity = new DatePeriod(newFrom, newTo);
                    ppmvToUpdate.setValidity(validity);
                    ppmvToUpdate.setStatus(importItem.getStatus());
                    pricePlanMatrixVersionService.update(ppmvToUpdate);
                } catch (Exception e) {
                    throw e;
                }
            } catch (Exception e) {
                resultDto.setStatus(ActionStatusEnum.FAIL);
                resultDto.setMessage(e.getMessage());
            }
            resultDtos.add(resultDto);
        }

        try {
            FileUtils.deleteDirectory(new File(getTempDir()));
        } catch (IOException e) {
            log.warn(e.getMessage());
        }

        return resultDtos;
    }

    private String readAllLines(LineNumberReader lnr) throws IOException {
        StringBuilder sb = new StringBuilder();
        String lineRead;
        while ((lineRead = lnr.readLine()) != null) {
            sb.append(lineRead).append("\n");
        }
        return sb.toString();
    }

    private String eliminateBOM(String row) {
        if (StringUtils.isNotBlank(row)) {
            // Get the first character
            String bom = row.substring(0, 1);
            if (!bom.equals("i")) { // i for id;
                row = row.substring(1);
            }
        }
        return row;
    }

    private void unzipFile(String fileToImport) {
        File file = new File(getRootDir() + File.separator + fileToImport);
        if (!file.exists()) {
            throw new BusinessApiException("The zipped file does not exist");
        }
        if (!FileUtils.isValidZip(file)) {
            throw new BusinessApiException("The zipped file is invalid!");
        }
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            FileUtils.unzipFile(getTempDir(), fileInputStream);
        } catch (Exception e) {
            throw new BusinessApiException("Error unziping file: " + fileToImport);
        }
    }

    private String getRootDir() {
        return paramBeanFactory.getDefaultChrootDir() + File.separator + "/imports/priceplan_versions";
    }

    private String getTempDir() {
        return getRootDir() + File.separator + "temp";
    }
}