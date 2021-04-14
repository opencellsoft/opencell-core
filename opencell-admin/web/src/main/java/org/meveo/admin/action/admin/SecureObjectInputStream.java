package org.meveo.admin.action.admin;

import java.io.IOException;
import java.io.InputStream;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.net.Socket;

public class SecureObjectInputStream extends ObjectInputStream {

	public SecureObjectInputStream() throws IOException, SecurityException {
		super();
	}

	public SecureObjectInputStream(InputStream in) throws IOException {
		super(in);
	}

	@Override
	protected Class<?> resolveClass(ObjectStreamClass osc) throws IOException, ClassNotFoundException {
		
		if (!osc.getName().equals(String.class.getName())) {
			throw new InvalidClassException("Unauthorized deserialization", osc.getName());
		}
		return super.resolveClass(osc);
	}
}
