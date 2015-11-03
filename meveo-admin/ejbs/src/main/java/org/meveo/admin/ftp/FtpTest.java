package org.meveo.admin.ftp;
import org.apache.commons.vfs.FileContent;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.FileSystemOptions;
import org.apache.commons.vfs.FileType;
import org.apache.commons.vfs.impl.StandardFileSystemManager;
import org.apache.commons.vfs.provider.sftp.SftpFileSystemConfigBuilder;

public class FtpTest {

	private static FileSystemOptions getSftpOptions(boolean isSftp) throws FileSystemException {
		FileSystemOptions opts = new FileSystemOptions();
		if (isSftp) {
			SftpFileSystemConfigBuilder.getInstance().setStrictHostKeyChecking(opts, "no");
			SftpFileSystemConfigBuilder.getInstance().setUserDirIsRoot(opts, true);
			SftpFileSystemConfigBuilder.getInstance().setTimeout(opts, 10000);
		}
		return opts;
	}

	public static void main(String[] args) throws Exception {
		String ftpAddress = "ftp://meveo.admin:meveo.admin@localhost:2121/";
		StandardFileSystemManager manager = null;
		try {
			manager = new StandardFileSystemManager();
			manager.init();
			FileObject fileObject = manager.resolveFile(ftpAddress, getSftpOptions(false));
			FileObject[] fileObjects = fileObjects = fileObject.getChildren();

			if (fileObjects != null) {
				for (FileObject o : fileObjects) {
					if (o.getType() == FileType.FILE) {
						String fileName = o.getName().getBaseName();
						FileContent c = o.getContent();
						long lastModified = c.getLastModifiedTime();
						long size = c.getSize();
						System.out
								.println("read file " + fileName + " ,size=" + size + " ,lastModified=" + lastModified);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			manager.close();
		}
	}
}
