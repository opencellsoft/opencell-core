package org.meveo.util.serializable;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.codec.binary.Base64;
import org.meveo.model.BaseEntity;
import org.meveo.model.BusinessEntity;
import org.meveo.service.crm.impl.CustomEntitySearchService;

public class SerializableUtil {


	/**
	 * encode an object into base64 string value
	 * @param obj
	 * @return
	 */
	public static String encode(Object obj) {
		String result = null;
		byte[] data = null;
		ByteArrayOutputStream bao = null;
		GZIPOutputStream gzout = null;
		ObjectOutputStream out = null;
		try {
			bao = new ByteArrayOutputStream();
			gzout = new GZIPOutputStream(bao);
			out = new ObjectOutputStream(gzout);
			out.writeObject(obj);
			out.flush();
			out.close();
			out = null;
			gzout.close();
			gzout = null;
			data = bao.toByteArray();
			bao.close();
			bao = null;
			byte[] data64 = Base64.encodeBase64(data);
			result = new String(data64, "UTF8");
		} catch (IOException e) {
		}
		return result;
	}

	/**
	 * decode a base64 string value into {@link org.meveo.model.BusinessEntity a business entity}
	 * @param cfService
	 * @param clazzName
	 * @param str
	 * @return
	 */
	public static BusinessEntity decodeSingle(
			CustomEntitySearchService cfService, String clazzName, String str) {
		BaseEntity entity = (BaseEntity) decode(str);
		return cfService.findCustomEntity(clazzName, entity.getId());
	}

	/**
	 * decode a base64 string value into a set collection
	 * @param cfService
	 * @param clazzName
	 * @param str
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Set<BusinessEntity> decodeList(
			CustomEntitySearchService cfService, String clazzName, String str) {
		Set<BusinessEntity> entities = (HashSet<BusinessEntity>) decode(str);
		Set<BusinessEntity> result = new HashSet<BusinessEntity>();
		BusinessEntity temp = null;
		for (BusinessEntity entity : entities) {
			temp = cfService.findCustomEntity(clazzName, entity.getId());
			result.add(temp);
		}
		return result;
	}

	/**
	 * decode a base64 string value into a map collection
	 * @param cfService
	 * @param clazzName
	 * @param str
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, BusinessEntity> decodeMap(
			CustomEntitySearchService cfService, String clazzName, String str) {
		Map<String, BusinessEntity> entities = (Map<String, BusinessEntity>) decode(str);
		Map<String, BusinessEntity> result = new HashMap<String, BusinessEntity>();
		BusinessEntity temp = null;
		for (Map.Entry<String, BusinessEntity> entry : entities.entrySet()) {
			temp = cfService.findCustomEntity(clazzName, entry.getValue()
					.getId());
			result.put(entry.getKey(), temp);
		}
		return result;
	}

	/**
	 * decode a base64 string value into an object
	 * @param str
	 * @return
	 */
	public static Object decode(String str) {
		byte[] data = Base64.decodeBase64(str);
		Object obj = null;
		ByteArrayInputStream bai = null;
		GZIPInputStream gis = null;
		ObjectInputStream ois = null;
		try {
			bai = new ByteArrayInputStream(data);
			gis = new GZIPInputStream(bai);
			ois = new ObjectInputStream(gis);
			obj = (Object) ois.readObject();
			ois.close();
			ois = null;
			gis.close();
			gis = null;
			bai.close();
			bai = null;
		} catch (Exception e) {
		}
		return obj;
	}
}