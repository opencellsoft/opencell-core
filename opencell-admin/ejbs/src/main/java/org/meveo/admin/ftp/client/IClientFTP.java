package org.meveo.admin.ftp.client;

import java.util.List;

public interface IClientFTP {
	
	public void setUserName(String userName);
	public void setPassword(String password);
	public int init();
	public void cd(String directory);
	public List<String> ls(String prefix,String suffix);
	public Object get(String fileName);
	public void close();
	

}
