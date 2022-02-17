package org.meveo.admin.storage;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;
import org.apache.commons.io.IOUtils;
import org.carlspring.cloud.storage.s3fs.S3FileSystem;
import org.carlspring.cloud.storage.s3fs.S3FileSystemProvider;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.ParamBeanFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilder;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

public class StorageFactory {

//    private static InputStream inStream;
//    private static OutputStream outStream;
    private static String storageType;
    private static String bucketName;

    private static S3FileSystem s3FileSystem;

    private static final String NFS = "NFS";
    private static final String S3 = "S3";

    static {
        final String SCALEWAY_ENDPOINT_URL = "https://s3.fr-par.scw.cloud";

        final String SCALEWAY_REGION = "fr-par";

        ParamBean tmpParamBean = ParamBeanFactory.getAppScopeInstance();
        storageType = tmpParamBean.getProperty("storage.type", NFS);
        bucketName = tmpParamBean.getProperty("S3.bucketName", "bucketName");

        S3Client client =
                S3Client.builder().region(Region.of(SCALEWAY_REGION))
                        .endpointOverride(URI.create(SCALEWAY_ENDPOINT_URL))
                        .credentialsProvider(
                                StaticCredentialsProvider.create(
                                        AwsBasicCredentials.create("SCW45136BHFPSPW0CQDG","517313e6-c47f-424b-8d31-972022cf864b")))

                        .build();

        s3FileSystem = new S3FileSystem(new S3FileSystemProvider(), "SCW45136BHFPSPW0CQDG", client, SCALEWAY_ENDPOINT_URL);
    }

    public static Path connectToS3Bucket(String bucketName) {
        return s3FileSystem.getPath("/" + bucketName);
    }

    public static Path getObjectPath(String objectPath) {
        return s3FileSystem.getPath("/" + objectPath);
    }

    public static Path getObjectPath(File file) {
        return s3FileSystem.getPath("/" + file.getPath());
    }

    public static InputStream getInputStream(String fileName) {
        if (storageType.equals(NFS)) {
            try {
                return new FileInputStream(fileName);
            }
            catch (FileNotFoundException e) {
                System.out.println("file not found : " + e.getMessage());
            }
        }
        else if (storageType.equalsIgnoreCase(S3)) {
            InputStream inStream;

            Path objectPath = getObjectPath(fileName);

            try {
                inStream = s3FileSystem.provider().newInputStream(objectPath);

                return inStream;
            }
            catch (IOException e) {
                System.out.println("error message : " + e.getMessage());
            }
        }

        return null;
    }

//    public static FileWriter getFileWriter(File file) {
//        if (storageType.equals(NFS)) {
//            try {
//                return new FileWriter(file, false);
//            }
//            catch (IOException e) {
//                System.out.println("file not found : " + e.getMessage());
//            }
//        }
//        else if (storageType.equalsIgnoreCase(S3)) {
//            OutputStream outStream;
//            FileWriter fileWriter;
//            String fileName = bucketName + file.getPath().substring(1).replace("\\", "/");
//
//            Path objectPath = getObjectPath(fileName);
//
//            try {
//                outStream = s3FileSystem.provider().newOutputStream(objectPath);
//                s3FileSystem.provider().new
//                fileWriter = new FileWriter(outStream);
//                System.out.println("objectPath getPrintWriter Thang : " + objectPath.toString());
//
//                return fileWriter;
//            }
//            catch (IOException e) {
//                System.out.println("error message : " + e.getMessage());
//            }
//        }
//    }

    public static PrintWriter getPrintWriter(File file) {
        if (storageType.equals(NFS)) {
            try {
                return new PrintWriter(file);
            }
            catch (FileNotFoundException e) {
                System.out.println("file not found : " + e.getMessage());
            }
        }
        else if (storageType.equalsIgnoreCase(S3)) {
            PrintWriter printWriter;
            String fileName = bucketName + file.getPath().substring(1).replace("\\", "/");

            Path objectPath = getObjectPath(fileName);

            try {
//                printWriter = new PrintWriter(new BufferedWriter(
//                        new OutputStreamWriter(s3FileSystem.provider().newOutputStream(objectPath), StandardCharsets.US_ASCII)));
                printWriter = new PrintWriter(
                        new OutputStreamWriter(s3FileSystem.provider().newOutputStream(objectPath), StandardCharsets.US_ASCII));

                return printWriter;
            }
            catch (IOException e) {
                System.out.println("error message : " + e.getMessage());
            }
        }

        return null;
    }

    public static InputStream getInputStream(File file) {
        if (storageType.equals(NFS)) {
            try {
                return new FileInputStream(file);
            }
            catch (FileNotFoundException e) {
                System.out.println("file not found : " + e.getMessage());
            }
        }
        else if (storageType.equalsIgnoreCase(S3)) {
            InputStream inStream;
            String fileName = bucketName + file.getPath().substring(1).replace("\\", "/");

            Path objectPath = getObjectPath(fileName);

            try {
                inStream = s3FileSystem.provider().newInputStream(objectPath);

                return inStream;
            }
            catch (IOException e) {
                System.out.println("error message : " + e.getMessage());
            }
        }

        return null;
    }

    public static OutputStream getOutputStream(String fileName) {
        if (storageType.equals(NFS)) {
            try {
                return new FileOutputStream(fileName);
            }
            catch (FileNotFoundException e) {
                System.out.println("file not found : " + e.getMessage());
            }
        }
        else if (storageType.equalsIgnoreCase(S3)) {
            OutputStream outStream;
            fileName = bucketName + fileName.substring(1).replace("\\", "/");

            Path bucketPath = getObjectPath(fileName);

            try {
                outStream = s3FileSystem.provider().newOutputStream(bucketPath);

                return outStream;
            }
            catch (IOException e) {
                System.out.println("error message : " + e.getMessage());
            }
        }

        return null;
    }

    public static Document parse(DocumentBuilder db, File file) {
        if (storageType.equals(NFS)) {
            try {
                return db.parse(file);
            }
            catch (SAXException | IOException e) {
                System.out.println("error message : " + e.getMessage());
            }
        }
        else if (storageType.equalsIgnoreCase(S3)) {
            InputStream inStream;
            String fileName = bucketName + file.getPath().substring(1).replace("\\", "/");

            Path objectPath = getObjectPath(fileName);

            try {
                inStream = s3FileSystem.provider().newInputStream(objectPath);

                return db.parse(inStream);
            }
            catch (IOException | SAXException e) {
                System.out.println("error message : " + e.getMessage());
            }
        }

        return null;
    }

    public static void marshal(Marshaller marshaller, Object obj, File file) {
        if (storageType.equals(NFS)) {
            try {
                marshaller.marshal(obj, file);
            }
            catch (JAXBException e) {
                System.out.println("marshaller exception : " + e.getMessage());
            }
        }
        else if (storageType.equalsIgnoreCase(S3)) {
            OutputStream outStream;
            String fullFileName;
            String filePath = file.getPath();
            if (filePath.charAt(0) == '.') {
                fullFileName = bucketName + filePath.substring(1).replace("\\", "/");
            }
            else {
                if (filePath.charAt(1) == '\\')
                    fullFileName = bucketName + filePath.replace("\\", "/");
                else
                    fullFileName = bucketName + '/' + filePath.replace("\\", "/");
            }

            Path bucketPath = getObjectPath(fullFileName);

            try {
                outStream = s3FileSystem.provider().newOutputStream(bucketPath);

                marshaller.marshal(obj, outStream);

                outStream.close();
            }
            catch (JAXBException | IOException e) {
                System.out.println("IO Exception or JAXBException in marshal method : " + e.getMessage());
            }
        }
    }

    public static void uploadJasperTemplate(File file) throws IOException {
        InputStream inStream = new FileInputStream(file);
        OutputStream outStream = getOutputStream(file.getPath());
        IOUtils.copy(inStream, outStream);
        inStream.close();
        assert outStream != null;
        outStream.close();
    }

    public static void exportReportToPdfFile(JasperPrint jasperPrint, String fileName) {
        if (storageType.equals(NFS)) {
            try {
                JasperExportManager.exportReportToPdfFile(jasperPrint, fileName);
            }
            catch (JRException e) {
                System.out.println("failed to generate PDF file : " + e.getMessage());
            }
        }
        else if (storageType.equalsIgnoreCase(S3)) {
            OutputStream outStream;
            Path bucketPath = getObjectPath(fileName);

            try {
                outStream = s3FileSystem.provider().newOutputStream(bucketPath);
                JasperExportManager.exportReportToPdfStream(jasperPrint, outStream);
            }
            catch (IOException | JRException e) {
                System.out.println("error message : " + e.getMessage());
            }
        }
    }

}
