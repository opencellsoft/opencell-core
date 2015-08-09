package org.meveo.util.serializable;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.meveo.commons.utils.ParamBean;
import org.meveo.model.BusinessEntity;
import org.meveo.model.crm.CustomFieldInstance;
import org.meveo.model.crm.CustomFieldPeriod;
import org.meveo.model.crm.CustomFieldStorageTypeEnum;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.CustomFieldTypeEnum;
import org.meveo.model.crm.wrapper.BaseWrapper;
import org.meveo.model.crm.wrapper.BusinessEntityWrapper;
import org.meveo.model.crm.wrapper.DateWrapper;
import org.meveo.model.crm.wrapper.DoubleWrapper;
import org.meveo.model.crm.wrapper.LongWrapper;
import org.meveo.model.crm.wrapper.StringWrapper;
import org.meveo.service.crm.impl.CustomEntitySearchService;

public class SerializableUtil {
	private static final String COMMA = ",";
	private static ParamBean paramBean=ParamBean.getInstance();
	private static final String pattern = paramBean.getProperty("meveo.dateFormat", "dd/MM/yyyy");
	private static final DateFormat df=new SimpleDateFormat(pattern);

	public static CustomFieldInstance initCustomField(CustomFieldTemplate cf,
			CustomFieldInstance cfi, CustomEntitySearchService searchService) {
		if (cf.getStorageType().equals(CustomFieldStorageTypeEnum.SINGLE)) {
			List<CustomFieldPeriod> periods=cfi.getValuePeriods();
			CustomFieldPeriod cfp=null;
			if(periods!=null&&periods.size()>0){
				cfp = cfi.getValuePeriods().get(0);
			}
			if (cf.isVersionable()) {
				if (CustomFieldTypeEnum.ENTITY.equals(cf.getFieldType())) {
					if (cfp != null) {
						cfp.setBusinessEntity(SerializableUtil.decodeEntitySingle(searchService,cf.getEntityClazz(),cfp.getEntityValue()));
					}
				} 
			}else{//not versionable
				if(CustomFieldTypeEnum.ENTITY.equals(cf.getFieldType())){
					cfi.setBusinessEntity(SerializableUtil.decodeEntitySingle(searchService, cf.getEntityClazz(),cfi.getEntityValue()));
				}
			}
		} else {// list or map
			if (cf.isVersionable()) {
				for (CustomFieldPeriod cfp : cfi.getValuePeriods()) {
					switch (cf.getStorageType()) {
					case LIST:
					case MAP:
						switch (cf.getFieldType()) {
						case STRING:
						case LIST:
						case TEXT_AREA:
							cfp.setStringList((List<StringWrapper>) SerializableUtil
									.decodeStringList(cfp.getEntityValue()));
							break;
						case DATE:
							cfp.setDateList(SerializableUtil.decodeDateList(cfp
									.getEntityValue()));
							break;
						case LONG:
							cfp.setLongList((List<LongWrapper>) SerializableUtil
									.decodeLongList(cfp.getEntityValue()));
							break;
						case DOUBLE:
							cfp.setDoubleList((List<DoubleWrapper>) SerializableUtil
									.decodeDoubleList(cfp.getEntityValue()));
							break;
						case ENTITY:
							cfp.setEntityList(SerializableUtil
									.decodeEntityList(searchService,
											cf.getEntityClazz(),
											cfp.getEntityValue()));
							break;
						}
						break;
					case SINGLE:
						switch(cf.getFieldType()){
						case ENTITY:
							cfp.setBusinessEntity(SerializableUtil
									.decodeEntitySingle(searchService,
											cf.getEntityClazz(),
											cfp.getEntityValue()));
							break;
						default:
								break;
						}
						break;
					default:
						break;
					}
				}
			} else {// not version
				switch (cf.getStorageType()) {
				case LIST:
				case MAP:
					switch (cf.getFieldType()) {
					case STRING:
					case LIST:
					case TEXT_AREA:
						cfi.setStringList((List<StringWrapper>) SerializableUtil
								.decodeStringList(cfi.getEntityValue()));
						break;
					case DATE:
						cfi.setDateList(SerializableUtil.decodeDateList(cfi
								.getEntityValue()));
						break;
					case LONG:
						cfi.setLongList((List<LongWrapper>) SerializableUtil
								.decodeLongList(cfi.getEntityValue()));
						break;
					case DOUBLE:
						cfi.setDoubleList((List<DoubleWrapper>) SerializableUtil
								.decodeDoubleList(cfi.getEntityValue()));
						break;
					case ENTITY:
						cfi.setEntityList(SerializableUtil.decodeEntityList(
								searchService, cf.getEntityClazz(),
								cfi.getEntityValue()));
						break;
					}
					break;
				case SINGLE:
					cfi.setBusinessEntity(SerializableUtil.decodeEntitySingle(
							searchService, cf.getEntityClazz(),
							cfi.getEntityValue()));
					break;
				default:
					break;
				}
			}
		}
		return cfi;
	}

	public static CustomFieldInstance updateCustomField(CustomFieldTemplate cf) {
		CustomFieldInstance cfi = cf.getInstance();
		if (CustomFieldStorageTypeEnum.SINGLE.equals(cf.getStorageType())) {
			if (cfi.isVersionable()) {
				List<CustomFieldPeriod> periods=cfi.getValuePeriods();
				if(periods!=null&&periods.size()==1){
					CustomFieldPeriod cfp = cfi.getValuePeriods().get(0);
					if(CustomFieldTypeEnum.ENTITY.equals(cf.getFieldType())){
						cfp.setEntityValue(SerializableUtil.encodeEntitySingle(cfp
							.getBusinessEntity()));
					}
				}
			} else {
				if(CustomFieldTypeEnum.ENTITY.equals(cf.getFieldType())){
					cfi.setEntityValue(SerializableUtil.encodeEntitySingle(cfi
						.getBusinessEntity()));
				}
			}
		} else {// list or map
			if (cf.isVersionable()) {// version
				for (CustomFieldPeriod cfp : cfi.getValuePeriods()) {
					switch (cf.getStorageType()) {
					case LIST:
					case MAP:
						switch (cf.getFieldType()) {
						case STRING:
						case LIST:
						case TEXT_AREA:
							cfp.setEntityValue(encodeList(cfp
									.getStringList()));
							break;
						case DATE:
							cfp.setEntityValue(encodeList(cfp
									.getDateList()));
							break;
						case LONG:
							cfp.setEntityValue(encodeList(cfp
									.getLongList()));
							break;
						case DOUBLE:
							cfp.setEntityValue(encodeList(cfp
									.getDoubleList()));
							break;
						case ENTITY:
							cfp.setEntityValue(encodeList(cfp
									.getEntityList()));
							break;
						}
						break;
					case SINGLE:
						cfp.setEntityValue(SerializableUtil
								.encodeEntitySingle(cfp.getBusinessEntity()));
						break;
					default:
						break;
					}
				}
			} else {// not version
				switch (cf.getStorageType()) {
				case LIST:
				case MAP:
					switch (cf.getFieldType()) {
					case STRING:
					case LIST:
					case TEXT_AREA:
						cfi.setEntityValue(SerializableUtil.encodeList(cfi
								.getStringList()));
						break;
					case DATE:
						cfi.setEntityValue(SerializableUtil.encodeList(cfi
								.getDateList()));
						break;
					case LONG:
						cfi.setEntityValue(SerializableUtil.encodeList(cfi
								.getLongList()));
						break;
					case DOUBLE:
						cfi.setEntityValue(SerializableUtil.encodeList(cfi
								.getDoubleList()));
						break;
					case ENTITY:
						cfi.setEntityValue(SerializableUtil.encodeList(cfi
								.getEntityList()));
						break;
					}
					break;
				case SINGLE:
					cfi.setEntityValue(SerializableUtil.encodeEntitySingle(cfi
							.getBusinessEntity()));
					break;
				default:
					break;
				}
			}
		}
		return cfi;
	}

	private static String encodeList(List<? extends BaseWrapper> values) {
		StringBuilder sb = new StringBuilder();
		int i=0;
		for (BaseWrapper wrapper : values) {
			if(wrapper.isEmpty()){
				continue;
			}
			sb.append(i==0?"":COMMA);
			if(wrapper instanceof DateWrapper){
				DateWrapper date=(DateWrapper)wrapper;
				try{
					sb.append(df.format(date.getDateValue()));
				}catch(Exception e){
					sb.append("null");
				}
			}else{
				sb.append(wrapper);
			}
			i++;
		}
		return sb.toString();
	}

	private static String encodeEntitySingle(BusinessEntity value) {
		String result = null;
		try {
			result = String.valueOf(value.getId());
		} catch (Exception e) {
		}
		return result;
	}

	/**
	 * parse a string value into string list
	 * 
	 * @param value
	 * @return
	 */
	private static List<StringWrapper> decodeStringList(String value) {
		List<StringWrapper> result = new ArrayList<StringWrapper>();
		try {
			String[] strings = value.split(COMMA);
			for (String str : strings) {
				result.add(StringWrapper.parse(str));
			}
		} catch (Exception e) {
		}
		return result;
	}

	/**
	 * parse a string value into a date list
	 * 
	 * @param value
	 * @param isList
	 * @return
	 */
	private static List<DateWrapper> decodeDateList(String value) {
		List<DateWrapper> result = new ArrayList<DateWrapper>();
		try {
			String[] strings = value.split(COMMA);
			for (String str : strings) {
				result.add(new DateWrapper(df.parse(str)));
			}
		} catch (Exception e) {
		}
		return result;
	}

	/**
	 * parse a string value into a long list
	 * 
	 * @param value
	 * @return
	 */
	private static List<LongWrapper> decodeLongList(String value) {
		List<LongWrapper> result = new ArrayList<LongWrapper>();
		try {
			String[] strings = value.split(COMMA);
			for (String str : strings) {
				result.add(LongWrapper.parse(str));
			}
		} catch (Exception e) {
		}
		return result;
	}

	/**
	 * parse a string value into a double list
	 * 
	 * @param value
	 * @return
	 */
	private static List<DoubleWrapper> decodeDoubleList(String value) {
		List<DoubleWrapper> result = new ArrayList<DoubleWrapper>();
		try {
			String[] strings = value.split(COMMA);
			for (String str : strings) {
				result.add(DoubleWrapper.parse(str));
			}
		} catch (Exception e) {
		}
		return result;
	}

	/**
	 * parse a string value into a {@link BusinessEntity business entity}
	 * 
	 * @param cfService
	 * @param clazzName
	 * @param str
	 * @return
	 */
	private static BusinessEntity decodeEntitySingle(
			CustomEntitySearchService searchService, String clazzName,
			String str) {
		BusinessEntity businessEntity = null;
		BusinessEntity result = null;
		try {
			businessEntity = BusinessEntityWrapper.parse(str);
			result = searchService.findCustomEntity(clazzName, businessEntity.getId());
		} catch (Exception e) {
		}
		return result;
	}

	/**
	 * parse a string value into a list business entity
	 * 
	 * @param cfService
	 * @param clazzName
	 * @param str
	 * @return
	 */
	private static List<BusinessEntityWrapper> decodeEntityList(
			CustomEntitySearchService searchService, String clazzName,
			String str) {
		List<BusinessEntityWrapper> result = new ArrayList<BusinessEntityWrapper>();
		BusinessEntity temp = null;
		BusinessEntity businessEntity = null;
		try {
			String[] strs = str.split(COMMA);
			for (String value : strs) {
				businessEntity = BusinessEntityWrapper.parse(value);
				if(businessEntity==null||businessEntity.getId()==null){
					continue;
				}
				temp = searchService.findCustomEntity(clazzName, businessEntity.getId());
				if (temp != null) {
					result.add(new BusinessEntityWrapper(temp));
				}
			}
		} catch (Exception e) {
		}
		return result;
	}
}