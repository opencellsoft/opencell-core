package org.meveo.util.serializable;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.codec.binary.Base64;

public class SerializableUtil {

	public static String encode(Object obj){
		String result=null;
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
			result= new String(data64, "UTF8");
		} catch (IOException e) {
		}
		return result;
	}

	public static Object decode(String str){
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