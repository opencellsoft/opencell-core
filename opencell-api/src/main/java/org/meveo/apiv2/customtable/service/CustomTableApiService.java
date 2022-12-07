package org.meveo.apiv2.customtable.service;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.apiv2.generic.GenericFieldDetails;
import org.meveo.apiv2.generic.services.GenericFileExportManager;
import org.meveo.model.customEntities.CustomEntityTemplate;
import org.meveo.service.base.NativePersistenceService;
import org.meveo.service.base.ValueExpressionWrapper;
import org.meveo.service.custom.CustomTableService;

@Stateless
public class CustomTableApiService {

	@Inject
	private GenericFileExportManager genericExportManager;
	
    @Inject
    @Named
    private NativePersistenceService nativePersistenceService;
    
    @Inject
	private CustomTableService customTableService;

	public String export(CustomEntityTemplate cet, String fileFormat) throws ClassNotFoundException {
//
//		List<List<Object>> list = (List<List<Object>>) nativePersistenceService
//				.getQuery(entityClass.getCanonicalName(), searchConfig, null)
//				.find(nativePersistenceService.getEntityManager()).stream()
//				.map(ObjectArrays -> Arrays.asList(ObjectArrays)).collect(toList());
//
//		List<GenericFieldDetails> formulaFields = fieldDetails.values().stream()
//				.filter(x -> !StringUtils.isEmpty(x.getFormula())).collect(toList());
//
//		return genericExportManager.export(entityName, list.stream().map(originalLine).collect(toList()), fileFormat,
//				fieldDetails,
//				genericFieldDetails.stream().map(GenericFieldDetails::getName).collect(Collectors.toList()), locale);
		return fileFormat;
	}
}
