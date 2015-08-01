package org.meveo.util.serializable;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.codec.binary.Base64;
import org.meveo.model.BaseEntity;
import org.meveo.model.BusinessEntity;
import org.meveo.model.crm.BusinessEntityWrapper;
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
	 * decode a base64 string value into a list collection
	 * @param cfService
	 * @param clazzName
	 * @param str
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static List<BusinessEntityWrapper> decodeList(
			CustomEntitySearchService cfService, String clazzName, String str) {
		List<BusinessEntityWrapper> entities = (ArrayList<BusinessEntityWrapper>) decode(str);
		List<BusinessEntityWrapper> result = new ArrayList<BusinessEntityWrapper>();
		BusinessEntity temp = null;
		for (BusinessEntityWrapper wrapper : entities) {
			temp = cfService.findCustomEntity(clazzName, wrapper.getBusinessEntity().getId());
			result.add(new BusinessEntityWrapper(wrapper.getLabel(),temp));
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