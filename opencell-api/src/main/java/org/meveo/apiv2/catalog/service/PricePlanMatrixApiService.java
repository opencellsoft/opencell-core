
package org.meveo.apiv2.catalog.service;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.response.catalog.ImportResultResponseDto.ImportResultDto;
import org.meveo.api.dto.response.catalog.PricePlanMatrixLinesDto;
import org.meveo.api.exception.BusinessApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.apiv2.catalog.ImportPricePlanVersionsDto;
import org.meveo.apiv2.catalog.ImportPricePlanVersionsItem;
import org.meveo.apiv2.catalog.PricePlanMLinesDTO;
import org.meveo.apiv2.ordering.services.ApiService;
import org.meveo.commons.utils.FileUtils;
import org.meveo.commons.utils.ParamBeanFactory;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.catalog.PricePlanMatrix;
import org.meveo.model.catalog.PricePlanMatrixVersion;
import org.meveo.service.catalog.impl.PricePlanMatrixColumnService;
import org.meveo.service.catalog.impl.PricePlanMatrixVersionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PricePlanMatrixApiService implements ApiService<PricePlanMatrix> {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Inject
    private PricePlanMatrixColumnService pricePlanMatrixColumnService;

    @Inject
    private PricePlanMatrixVersionService pricePlanMatrixVersionService;

    @Inject
    private ParamBeanFactory paramBeanFactory;

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

        //Fix issue when getting import root directory
        String importRootDir = paramBeanFactory.getDefaultChrootDir() + File.separator + "imports/priceplan_versions";
        String importTempDir = importRootDir + File.separator + "temp" + System.currentTimeMillis();
        File zipFile = new File(importRootDir + File.separator + importPricePlanVersionsDto.getFileToImport());
        unzipFile(importTempDir, zipFile);

        List<ImportResultDto> resultDtos = new ArrayList<>();
        for (ImportPricePlanVersionsItem importItem : importPricePlanVersionsDto.getPricePlanVersions()) {
            ImportResultDto resultDto = new ImportResultDto(importItem);
            try {
                pricePlanMatrixVersionService.importPricePlanVersion(importTempDir, importItem);
            } catch (Exception e) {
                log.error(e.getMessage());
                resultDto.setStatus(ActionStatusEnum.FAIL);
                resultDto.setMessage(e.getMessage());
            }
            resultDtos.add(resultDto);
        }

        try {
            FileUtils.deleteDirectory(new File(importTempDir));
            zipFile.delete();
        } catch (Exception e) {
            log.warn(e.getMessage());
        }

        return resultDtos;
    }

    private void unzipFile(String importTempDir, File zipFile) {
        if (!zipFile.exists()) {
            throw new BusinessApiException("The zipped file does not exist");
        }
        if (!FileUtils.isValidZip(zipFile)) {
            throw new BusinessApiException("The zipped file is invalid!");
        }
        try (FileInputStream fileInputStream = new FileInputStream(zipFile)) {
            FileUtils.unzipFile(importTempDir, fileInputStream);
        } catch (Exception e) {
            throw new BusinessApiException("Error when unziping file");
        }
    }
}