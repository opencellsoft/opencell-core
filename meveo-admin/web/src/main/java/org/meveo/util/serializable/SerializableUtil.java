package org.meveo.util.serializable;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
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

/**
 * encode/decode custom field instance or custom field period for list or map
 * list encode for string,date,double,long,entity,selectlist,textarea to values, separated by common
 * map encode for string,date,double,long,entity,selectlist,textarea to values, like label1=encodelist1;label2=encodelist2 ..., which is seperated by simicolon
 *
 */
public class SerializableUtil {
	private static final String COMMA = ",";
	private static final String SIMICOLON=";";
	private static final String EQUAL="=";
	private static ParamBean paramBean=ParamBean.getInstance();
	private static final String pattern = paramBean.getProperty("meveo.dateFormat", "dd/MM/yyyy");
	private static final DateFormat df=new SimpleDateFormat(pattern);

	/**
	 * decode values and store into list or map
	 * @param cf custom field template
	 * @param cfi custom field instance
	 * @param searchService
	 * @return
	 */
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
					String entityValue=cfp.getEntityValue();
					if(StringUtils.isEmpty(entityValue)){
						continue;
					}
					String label=null;
					switch (cf.getStorageType()) {
					case MAP:
						String[] simiColonValues=entityValue.split(EQUAL);
						if(simiColonValues.length!=2){
							break;
						}
						label=simiColonValues[0];
						if(StringUtils.isEmpty(label)){
							break;
						}
						cfp.setLabel(label);
						entityValue=simiColonValues[1];
					case LIST:
						switch (cf.getFieldType()) {
						case STRING:
						case LIST:
						case TEXT_AREA:
							cfp.setStringList((List<StringWrapper>) SerializableUtil
									.decodeStringList(entityValue));
							break;
						case DATE:
							cfp.setDateList(SerializableUtil.decodeDateList(entityValue));
							break;
						case LONG:
							cfp.setLongList((List<LongWrapper>) SerializableUtil
									.decodeLongList(entityValue));
							break;
						case DOUBLE:
							cfp.setDoubleList((List<DoubleWrapper>) SerializableUtil
									.decodeDoubleList(entityValue));
							break;
						case ENTITY:
							cfp.setEntityList(SerializableUtil
									.decodeEntityList(searchService,
											cf.getEntityClazz(),
											entityValue));
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
				
				String str=cfi.getEntityValue();
				if(StringUtils.isEmpty(str)){
					return cfi;
				}
				switch (cf.getStorageType()) {
				case MAP:
					String[] simiValues=str.split(SIMICOLON);
					for(String simiValue:simiValues){
						String[] equalValues=simiValue.split(EQUAL);
						if(equalValues.length!=2){
							continue;
						}
						String label=equalValues[0];
						String value=equalValues[1];
						switch (cf.getFieldType()) {
						case STRING:
						case LIST:
						case TEXT_AREA:
							cfi.addToStringMap(label,decodeStringList(value));
							break;
						case DATE:
							cfi.addToDateMap(label,decodeDateList(value));
							break;
						case LONG:
							cfi.addToLongMap(label,decodeLongList(value));
							break;
						case DOUBLE:
							cfi.addToDoubleMap(label,decodeDoubleList(value));
							break;
						case ENTITY:
							cfi.addToEntityMap(label,decodeEntityList(
									searchService, cf.getEntityClazz(),
									value));
							break;
						}
						break;
					}
					break;
				case LIST:
					switch (cf.getFieldType()) {
					case STRING:
					case LIST:
					case TEXT_AREA:
						cfi.setStringList(SerializableUtil.decodeStringList(cfi.getEntityValue()));
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
					if(CustomFieldTypeEnum.ENTITY.equals(cf.getFieldType())&&cfp.getBusinessEntity()!=null){
						cfp.setEntityValue(SerializableUtil.encodeEntitySingle(cfp
							.getBusinessEntity()));
					}
				}
			} else {
				if(CustomFieldTypeEnum.ENTITY.equals(cf.getFieldType())&&cfi.getBusinessEntity()!=null){
					cfi.setEntityValue(SerializableUtil.encodeEntitySingle(cfi
						.getBusinessEntity()));
				}
			}
		} else {// list or map
			if (cf.isVersionable()) {// version
				for (CustomFieldPeriod cfp : cfi.getValuePeriods()) {
					String result="";
					switch (cf.getStorageType()) {
					case MAP:
						result=cfp.getLabel()+EQUAL;
					case LIST:
						switch (cf.getFieldType()) {
						case STRING:
						case LIST:
						case TEXT_AREA:
							result+=encodeList(cfp.getStringList());
							break;
						case DATE:
							result+=encodeList(cfp.getDateList());
							break;
						case LONG:
							result+=encodeList(cfp.getLongList());
							break;
						case DOUBLE:
							result+=encodeList(cfp.getDoubleList());
							break;
						case ENTITY:
							result+=encodeList(cfp.getEntityList());
							break;
							default:
							break;
						}
						if(StringUtils.isNotEmpty(result)){
							cfp.setEntityValue(result);
						}
						break;
					case SINGLE:
						if(cfp.getBusinessEntity()!=null)
							cfp.setEntityValue(SerializableUtil
									.encodeEntitySingle(cfp.getBusinessEntity()));
						break;
					default:
						break;
					}
				}
			} else {// not version
				String result="";
				switch (cf.getStorageType()) {
				case MAP:
					String label=null;
					StringBuilder sb=new StringBuilder();
					switch (cf.getFieldType()) {
					case STRING:
					case LIST:
					case TEXT_AREA:
						if(cfi.getStringMap()==null||cfi.getStringMap().size()==0){
							return cfi;
						}
						int s=0;
						for(Map.Entry<String, List<StringWrapper>> entry:cfi.getStringMap().entrySet()){
							label=entry.getKey();
							List<StringWrapper> strings=entry.getValue();
							if(s!=0){
								sb.append(SIMICOLON);
							}
							sb.append(label).append(EQUAL).append(encodeList(strings));
							s++;
						}
						result=sb.toString();
						break;
					case DATE:
						if(cfi.getDateMap()==null||cfi.getDateMap().size()==0){
							return cfi;
						}
						int d=0;
						for(Map.Entry<String, List<DateWrapper>> entry:cfi.getDateMap().entrySet()){
							label=entry.getKey();
							List<DateWrapper> dates=entry.getValue();
							if(d!=0){
								sb.append(SIMICOLON);
							}
							sb.append(label).append(EQUAL).append(encodeList(dates));
							d++;
						}
						result=sb.toString();
						break;
					case LONG:
						if(cfi.getLongMap()==null||cfi.getLongMap().size()==0){
							return cfi;
						}
						int l=0;
						for(Map.Entry<String, List<LongWrapper>> entry:cfi.getLongMap().entrySet()){
							label=entry.getKey();
							List<LongWrapper> longs=entry.getValue();
							if(l!=0){
								sb.append(SIMICOLON);
							}
							sb.append(label).append(EQUAL).append(encodeList(longs));
							l++;
						}
						result=sb.toString();
						break;
					case DOUBLE:
						if(cfi.getDoubleMap()==null||cfi.getDoubleMap().size()==0){
							return cfi;
						}
						int dl=0;
						for(Map.Entry<String, List<DoubleWrapper>> entry:cfi.getDoubleMap().entrySet()){
							label=entry.getKey();
							List<DoubleWrapper> doubles=entry.getValue();
							if(dl!=0){
								sb.append(SIMICOLON);
							}
							sb.append(label).append(EQUAL).append(encodeList(doubles));
							dl++;
						}
						result=sb.toString();
						break;
					case ENTITY:
						if(cfi.getEntityMap()==null||cfi.getEntityMap().size()==0){
							return cfi;
						}
						int e=0;
						for(Map.Entry<String, List<BusinessEntityWrapper>> entry:cfi.getEntityMap().entrySet()){
							label=entry.getKey();
							List<BusinessEntityWrapper> entities=entry.getValue();
							if(e!=0){
								sb.append(SIMICOLON);
							}
							sb.append(label).append(EQUAL).append(encodeList(entities));
							e++;
						}
						result=sb.toString();
						break;
						default:
							break;
					}
					if(StringUtils.isNotEmpty(result)){
						cfi.setEntityValue(result);
					}
					break;
				case LIST:
					switch (cf.getFieldType()) {
					case STRING:
					case LIST:
					case TEXT_AREA:
						result+=SerializableUtil.encodeList(cfi.getStringList());
						break;
					case DATE:
						result+=encodeList(cfi.getDateList());
						break;
					case LONG:
						result+=encodeList(cfi.getLongList());
						break;
					case DOUBLE:
						result+=encodeList(cfi.getDoubleList());
						break;
					case ENTITY:
						result+=encodeList(cfi.getEntityList());
						break;
						default:
							break;
					}
					if(StringUtils.isNotEmpty(result)){
						cfi.setEntityValue(result);
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

	/**
	 * encode list into string value
	 * @param values
	 * @return
	 */
	private static String encodeList(List<? extends BaseWrapper> values) {
		if(values==null||values.size()==0){return null;};
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
	 * decode a string value into string list
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