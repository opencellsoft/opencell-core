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
package org.meveo.apiv2.admin.service;

import org.apache.commons.lang3.math.NumberUtils;
import org.meveo.api.BaseApi;
import org.meveo.api.exception.BusinessApiException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.apiv2.admin.FilesPagingAndFiltering;
import org.meveo.apiv2.admin.impl.FileMapper;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.base.expressions.ExpressionParser;

import javax.ejb.Stateless;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Paging and filtering interface for files.
 *
 * @author Abdellatif BARI
 * @since 14.1.16
 */
@Stateless
public class FilesApiService extends BaseApi {

    private final FileMapper fileMapper = new FileMapper();

    /**
     * Gets the filter used for searching files
     *
     * @param path         the path of root directory whose files will be searched
     * @param searchConfig Pagination and filtering criteria used for searching files
     * @return the filter used for searching files
     */
    private Predicate<Path> getFilter(Path path, FilesPagingAndFiltering searchConfig) {
        return p -> {
            if (p == path) { //Exclude the root directory
                return false;
            }
            final ArrayList<Boolean> condition = new ArrayList<>();
            if (searchConfig.getFilters() != null && !searchConfig.getFilters().isEmpty()) {
                searchConfig.getFilters().entrySet().stream().forEach(entry -> {
                            ExpressionParser exp = new ExpressionParser(entry.getKey().split(" "));
                            if (!StringUtils.isBlank(exp.getFieldName()) && entry.getValue() != null) {
                                switch (exp.getCondition()) {
                                    case "startsWith":
                                        if (exp.getFieldName().equalsIgnoreCase("name")) {
                                            condition.add(p.getFileName().toString().startsWith((String) entry.getValue()));
                                        }
                                        break;
                                    case "endsWith":
                                        if (exp.getFieldName().equalsIgnoreCase("name")) {
                                            condition.add(p.getFileName().toString().endsWith((String) entry.getValue()));
                                        }
                                        break;
                                    case "likeCriterias":
                                        if (exp.getFieldName().equalsIgnoreCase("name")) {
                                            String regex = ((String) entry.getValue()).replace("*", ".*?");
                                            condition.add(p.getFileName().toString().matches(regex));
                                        }
                                        break;
                                    case "fromRange":
                                        if (exp.getFieldName().equalsIgnoreCase("date")) {
                                            Date date = DateUtils.parseDate(entry.getValue());
                                            if (date != null) {
                                                condition.add(date.getTime() >= p.toFile().lastModified());
                                            }
                                        } else if (exp.getFieldName().equalsIgnoreCase("size")) {
                                            Long size = NumberUtils.toLong((String) entry.getValue(), -1);
                                            if (size > -1) {
                                                condition.add(size >= p.toFile().length());
                                            }
                                        }
                                        break;
                                    default: {
                                        if (StringUtils.isBlank(exp.getCondition())) {
                                            if (exp.getFieldName().equalsIgnoreCase("name")) {
                                                condition.add(p.getFileName().toString().equalsIgnoreCase((String) entry.getValue()));
                                            } else if (exp.getFieldName().equalsIgnoreCase("date")) {
                                                Date date = DateUtils.parseDate(entry.getValue());
                                                if (date != null) {
                                                    condition.add(p.toFile().lastModified() == date.getTime());
                                                }
                                            } else if (exp.getFieldName().equalsIgnoreCase("size")) {
                                                Long size = NumberUtils.toLong((String) entry.getValue(), -1);
                                                if (size > -1) {
                                                    condition.add(p.toFile().length() == size);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                );
            }
            return condition.contains(false) ? false : true;
        };
    }

    /**
     * Gets the files comparator
     *
     * @param searchConfig Pagination and filtering criteria used for searching files
     * @return the files comparator
     */
    private Comparator<Path> getComparator(FilesPagingAndFiltering searchConfig) {
        // default comparator by name.
        Comparator<Path> comparator = Comparator.comparing(Path::getFileName);
        String sortBy = searchConfig.getSortBy();
        if (!StringUtils.isBlank(sortBy)) {
            if (sortBy.equalsIgnoreCase("date")) {
                comparator = Comparator.comparing(p -> p.toFile().lastModified());
            } else if (sortBy.equalsIgnoreCase("size")) {
                comparator = Comparator.comparing(p -> p.toFile().length());
            }
        }
        if ("DESCENDING".equalsIgnoreCase(searchConfig.getSortOrder())) {
            comparator = comparator.reversed();
        }
        return comparator;
    }

    /**
     * Gets the provider directory root
     *
     * @return the provider directory root
     */
    public String getProviderRootDir() {
        return paramBeanFactory.getDefaultChrootDir();
    }

    /**
     * Remove any directory above the provider directory root
     *
     * @param dir the provider directory
     * @return the normalized path
     */
    private String normalizePath(String dir) {
        if (dir == null) {
            throw new BusinessApiException("Invalid parameter, file or directory is null");
        }
        File dirFile = new File(getProviderRootDir() + File.separator + dir);
        Path path = dirFile.toPath();
        path = path.normalize();
        String prefix = getProviderRootDir().replace("./", "");
        if (!path.toString().contains(prefix)) {
            throw new EntityDoesNotExistsException("File does not exists: " + dir);
        }
        return dir;
    }

    /**
     * Search the list files by provided criteria
     *
     * @param searchConfig the search criteria
     * @return the list of files
     * @throws BusinessApiException the business API exception
     */
    public List<org.meveo.apiv2.admin.File> searchFiles(FilesPagingAndFiltering searchConfig) throws BusinessApiException {
        String dir = searchConfig.getDirectory();
        if (!StringUtils.isBlank(dir)) {
            dir = getProviderRootDir() + File.separator + normalizePath(dir);
        } else {
            dir = getProviderRootDir();
        }
        File folder = new File(dir);
        if (folder.isFile()) {
            throw new BusinessApiException("Path " + dir + " is a file.");
        }
        Path path = Paths.get(dir);
        Comparator<Path> comparator = getComparator(searchConfig);
        Predicate<Path> filter = getFilter(path, searchConfig);

        List<org.meveo.apiv2.admin.File> result = new ArrayList<>();
        try {
            result = Files.walk(path, 1)
                    .sorted(comparator)
                    .filter(filter)
                    .skip(searchConfig.getOffset())
                    .limit(searchConfig.getLimit())
                    .map(p -> fileMapper.toResource(p.toFile()))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            log.error("Failed to search files from directory {}", dir, e);
        }
        return result;
    }
}
