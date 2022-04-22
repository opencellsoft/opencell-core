package org.meveo.service.catalog.impl;

import org.apache.commons.lang3.StringUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.ValidationException;
import org.meveo.api.dto.catalog.PricePlanMatrixLineDto;
import org.meveo.api.dto.catalog.PricePlanMatrixValueDto;
import org.meveo.api.dto.response.catalog.PricePlanMatrixLinesDto;
import org.meveo.model.BaseEntity;
import org.meveo.model.catalog.ColumnTypeEnum;
import org.meveo.model.catalog.PricePlanMatrixColumn;
import org.meveo.model.catalog.PricePlanMatrixVersion;
import org.meveo.model.cpq.Attribute;
import org.meveo.model.cpq.Product;
import org.meveo.model.cpq.enums.AttributeTypeEnum;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.base.BusinessService;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.ws.rs.NotFoundException;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Stateless
public class PricePlanMatrixColumnService extends BusinessService<PricePlanMatrixColumn> {

    @Inject
    private PricePlanMatrixValueService pricePlanMatrixValueService;

    public List<PricePlanMatrixColumn> findByAttributes(List<Attribute> attributes) {
        try {
            return getEntityManager().createNamedQuery("PricePlanMatrixColumn.findByAttributes", PricePlanMatrixColumn.class)
                    .setParameter("attributes", attributes)
                    .getResultList();
        }catch (NoResultException exp){
            return Collections.emptyList();
        }
    }

    public List<PricePlanMatrixColumn> findByProduct(Product product) {
        try {
            return getEntityManager().createNamedQuery("PricePlanMatrixColumn.findByProduct", PricePlanMatrixColumn.class)
                    .setParameter("product", product)
                    .getResultList();
        }catch (NoResultException exp){
            return Collections.emptyList();
        }
    }

    public void removePricePlanColumn(String code) {
        PricePlanMatrixColumn ppmColumn = findByCode(code);
        if(ppmColumn == null)
            return;
        Set<Long> valuesId = ppmColumn.getPricePlanMatrixValues().stream().map(BaseEntity::getId).collect(Collectors.toSet());
        if(!valuesId.isEmpty())
            pricePlanMatrixValueService.remove(valuesId);
        remove(ppmColumn);
    }
    
    public void removePricePlanColumn(Long id) {
        PricePlanMatrixColumn ppmColumn = findById(id);
        if(ppmColumn == null)
            return;
        Set<Long> valuesId = ppmColumn.getPricePlanMatrixValues().stream().map(BaseEntity::getId).collect(Collectors.toSet());
        if(!valuesId.isEmpty())
            pricePlanMatrixValueService.remove(valuesId);
        deleteById(ppmColumn.getId());
    }
    
    public void deleteById(Long id) {
			this.getEntityManager().createNamedQuery("PricePlanMatrixColumn.deleteById")
																			.setParameter("id", id).executeUpdate();
	}
    

	@SuppressWarnings("unchecked")
	public List<PricePlanMatrixColumn> findByCodeAndPlanMaptrixVersion(String code, PricePlanMatrixVersion pricePlanMatrixVersion) {
			return this.getEntityManager().createNamedQuery("PricePlanMatrixColumn.findByVersion")
																			.setParameter("code", code)
																			.setParameter("pricePlanMatrixVersionId", pricePlanMatrixVersion.getId()).getResultList();
	}
	
	public PricePlanMatrixLinesDto populateLinesAndValues(String pricePlanMatrixCode, String data,
			PricePlanMatrixVersion pricePlanMatrixVersion) {
		Scanner scanner = new Scanner(data);
		List<Map.Entry<String, Optional<Attribute> >> columns = new LinkedList<>();
		
		/*
		File format example:
		id(text);Booléen(BOOLEAN);Nombres(LIST_MULTIPLE_NUMERIC);Valeurs(LIST_MULTIPLE_TEXT);description(text);priority(number);priceWithoutTax(number)
		252;true;;3XL|Bleu;;0;20
		*/
		
		// Get columns types
		populateColumns(pricePlanMatrixVersion, scanner, columns);
		
		// populate lines
		List<PricePlanMatrixLineDto> pricePlanMatrixLines = populateLines(pricePlanMatrixCode, pricePlanMatrixVersion,
				scanner, columns);
		scanner.close();
		
		PricePlanMatrixLinesDto pricePlanMatrixLinesDto = new PricePlanMatrixLinesDto();
		pricePlanMatrixLinesDto.setPricePlanMatrixCode(pricePlanMatrixCode);
		pricePlanMatrixLinesDto.setPricePlanMatrixVersion(pricePlanMatrixVersion.getCurrentVersion());
		pricePlanMatrixLinesDto.setPricePlanMatrixLines(pricePlanMatrixLines);
		return pricePlanMatrixLinesDto;
	}

    private void populateColumns(PricePlanMatrixVersion pricePlanMatrixVersion, Scanner scanner, List<Map.Entry<String, Optional<Attribute>>> columns) {
		String line = scanner.nextLine();
		String[] firstLine = line.split(";");
		for (int i = 0; i < firstLine.length; i++) {
			String column = firstLine[i].split("\\[")[0];
			boolean isRange = firstLine[i].split("\\[").length > 1 && firstLine[i].split("\\[")[1].toLowerCase().contains("range");
			if (!(column.equals("id") || column.equals("description") || column.equals("priority") || column.equals("priceWithoutTax"))) {
				List<PricePlanMatrixColumn> pricePlanMatrixColumnList = findByCodeAndPlanMaptrixVersion(column, pricePlanMatrixVersion);
				if (pricePlanMatrixColumnList.isEmpty()) {
					throw new NotFoundException("PricePlanMatrixColumn with code= " + column + " does not exists");
				}
				PricePlanMatrixColumn pricePlanMatrixColumn = pricePlanMatrixColumnList.get(0);
				ColumnTypeEnum columnType = pricePlanMatrixColumn.getAttribute().getAttributeType().getColumnType(isRange);

				AttributeTypeEnum attributeType = pricePlanMatrixColumn.getAttribute().getAttributeType();

				if (attributeType.equals(AttributeTypeEnum.LIST_MULTIPLE_NUMERIC) || attributeType.equals(AttributeTypeEnum.LIST_MULTIPLE_TEXT) || attributeType.equals(
						AttributeTypeEnum.LIST_NUMERIC) || attributeType.equals(AttributeTypeEnum.LIST_TEXT)) {
					columns.add(Map.entry(column + "|List|" + pricePlanMatrixColumn.getRange(), Optional.of(pricePlanMatrixColumn.getAttribute())));

				} else {
					columns.add(Map.entry(column + "|" + columnType.toString() + "|" + pricePlanMatrixColumn.getRange(), Optional.of(pricePlanMatrixColumn.getAttribute())));
				}
			} else {
				columns.add(Map.entry(column, Optional.empty()));
			}
		}
	}
	
	private List<PricePlanMatrixLineDto> populateLines(String pricePlanMatrixCode,
			PricePlanMatrixVersion pricePlanMatrixVersion, Scanner scanner, List<Map.Entry<String, Optional<Attribute>>> columns) {
		String line;
		List<PricePlanMatrixLineDto> pricePlanMatrixLines = new ArrayList<>();
			
		while (scanner.hasNextLine()) {
			
			PricePlanMatrixLineDto pricePlanMatrixLineDto = new PricePlanMatrixLineDto();
			List<PricePlanMatrixValueDto> pricePlanMatrixValueDtoList = new LinkedList<>();
			
			line = scanner.nextLine();
			String[] nextLine = line.split(";");
			
			for(var columnIndex=0; columnIndex < columns.size() ; columnIndex++ ) {
				PricePlanMatrixValueDto pricePlanMatrixValueDto = new PricePlanMatrixValueDto();
				if (columns.get(columnIndex).toString().contains("|")) {
					
					String columnType = columns.get(columnIndex).toString().split("\\|")[1];
					String columnCode = columns.get(columnIndex).toString().split("\\|")[0];
					boolean isRange = ColumnTypeEnum.Range_Date.name().equals(columnType);
					switch (columnType) {
					case "String":
						pricePlanMatrixValueDto.setPpmColumnCode(columnCode);
						pricePlanMatrixValueDto.setStringValue(nextLine[columnIndex]);
						pricePlanMatrixValueDtoList.add(pricePlanMatrixValueDto);
						break;
					case "Long":
						pricePlanMatrixValueDto.setPpmColumnCode(columnCode);
						pricePlanMatrixValueDto.setLongValue(nextLine[columnIndex] == null || nextLine[columnIndex].isEmpty()? null : Long.parseLong(nextLine[columnIndex]));
						pricePlanMatrixValueDtoList.add(pricePlanMatrixValueDto);
						break;
					case "Double":
						pricePlanMatrixValueDto.setPpmColumnCode(columnCode);
						pricePlanMatrixValueDto.setDoubleValue((nextLine[columnIndex] == null || nextLine[columnIndex].isEmpty())? null : Double.parseDouble(convertToDecimalFormat(nextLine[columnIndex])));
						pricePlanMatrixValueDtoList.add(pricePlanMatrixValueDto);
						break;
					case "Boolean":
						pricePlanMatrixValueDto.setPpmColumnCode(columnCode);
						pricePlanMatrixValueDto.setBooleanValue((nextLine[columnIndex] == null || nextLine[columnIndex].isEmpty())? null :Boolean.valueOf(nextLine[columnIndex]));
						pricePlanMatrixValueDtoList.add(pricePlanMatrixValueDto);
						break;
					case "Range_Date":
						extractDateRange(nextLine, columnIndex, pricePlanMatrixValueDto, columnCode, isRange);
						pricePlanMatrixValueDtoList.add(pricePlanMatrixValueDto);
						break;
					case "Range_Numeric":
						pricePlanMatrixValueDto.setPpmColumnCode(columnCode);
						pricePlanMatrixValueDto.setFromDoubleValue((nextLine[columnIndex] == null || nextLine[columnIndex].split("\\|")[0].isEmpty()) ? null : Double.parseDouble(convertToDecimalFormat(nextLine[columnIndex].split("\\|")[0])));
						pricePlanMatrixValueDto.setToDoubleValue(nextLine[columnIndex] != null && nextLine[columnIndex].split("\\|").length>1 ? Double.parseDouble(convertToDecimalFormat(nextLine[columnIndex].split("\\|")[1])) : null);
						pricePlanMatrixValueDtoList.add(pricePlanMatrixValueDto);
						break;
					case "List":
						pricePlanMatrixValueDto.setPpmColumnCode(columnCode);
						pricePlanMatrixValueDto.setStringValue((nextLine[columnIndex] == null || nextLine[columnIndex].isEmpty())? null :nextLine[columnIndex].replace("\\|", ";"));
						pricePlanMatrixValueDtoList.add(pricePlanMatrixValueDto);
						columns.get(columnIndex).getValue().ifPresent(
								attribute -> {
									if(!inAllowedValues(attribute.getAllowedValues(), pricePlanMatrixValueDto.getStringValue()))
										throw new BusinessException("not allowed values");
								}
						);break;
					default:
						break;
					}
					
				}else {
					if (columns.get(columnIndex).getKey().equalsIgnoreCase("priority")) {
						pricePlanMatrixLineDto.setPriority(Integer.parseInt(nextLine[columnIndex]));
					}
					if (columns.get(columnIndex).getKey().equalsIgnoreCase("description")) {
						pricePlanMatrixLineDto.setDescription(nextLine[columnIndex]);
					}
					if (columns.get(columnIndex).getKey().equalsIgnoreCase("PriceWithoutTax")) {
						String val = convertToDecimalFormat(nextLine[columnIndex]);
						try {
							pricePlanMatrixLineDto.setPriceWithoutTax(new BigDecimal(val));
						} catch (Exception e) {
							throw new ValidationException("PriceWithoutTax : Cannot convert value " + val + " to decimal");
						}
					}
				}
			
			}
			pricePlanMatrixLineDto.setPricePlanMatrixCode(pricePlanMatrixCode);
			pricePlanMatrixLineDto.setPricePlanMatrixVersion(pricePlanMatrixVersion.getCurrentVersion());
			pricePlanMatrixLineDto.setPricePlanMatrixValues(pricePlanMatrixValueDtoList);
			pricePlanMatrixLines.add(pricePlanMatrixLineDto);
		}
		return pricePlanMatrixLines;
	}

	private boolean inAllowedValues(Set<String> allowedValues, String value)
	{
		if(value != null && !value.isEmpty() && allowedValues!= null && !allowedValues.isEmpty())
		{
			List<String> values = Arrays.asList(value.split("\\|"));
			return allowedValues.containsAll(values);
		}

		return true;

	}

	private void extractDateRange(String[] nextLine, int columnIndex, PricePlanMatrixValueDto pricePlanMatrixValueDto, String columnCode, boolean isRange) {
		pricePlanMatrixValueDto.setPpmColumnCode(columnCode);
		String value = Optional.ofNullable(nextLine[columnIndex]).orElse(null);
		if (isRange) {
			String dateLeftPart = value.split("\\|")[0];
			String dateRightPart = value.split("\\|").length>1?value.split("\\|")[1]:null;
			Date fromDateValue = validateAndReturnDate(dateLeftPart);
			Date toDateValue = validateAndReturnDate(dateRightPart);
			pricePlanMatrixValueDto.setFromDateValue(fromDateValue);
			pricePlanMatrixValueDto.setToDateValue(toDateValue);
		} else {
			Date date = validateAndReturnDate(value);
			pricePlanMatrixValueDto.setDateValue(date);
		}
	}

	private Date validateAndReturnDate(String value) {
		Date dateValue = StringUtils.isNotBlank(value) ? DateUtils.parseDate(value) : null;
		if (dateValue != null) {
			Integer year = DateUtils.getYearFromDate(dateValue);
			if (!(year >= 1900 && year <= 2100))
				throw new ValidationException("Wrong date " + value + " ==> The allowed date range is 1900 as min and 2100 as max");
		}
		return dateValue;
	}
	
	private String convertToDecimalFormat(String str) {
        str = str.replace(" ", "");
        int commaPos = str.indexOf(',');
        int dotPos = str.indexOf('.');
        if (commaPos > 0 && dotPos > 0) {
            if (commaPos < dotPos) {
                str = str.replace(",", "");
            } else {
                str = str.replace(".", "");
                str = str.replace(",", ".");
            }
        } else {
            str = str.replace(",", ".");
        }
		return str;
	}
}
