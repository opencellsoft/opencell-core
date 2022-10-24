package org.meveo.admin.storage;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;
import org.apache.commons.io.IOUtils;
import org.carlspring.cloud.storage.s3fs.S3FileSystem;
import org.carlspring.cloud.storage.s3fs.S3FileSystemProvider;
import org.meveo.commons.keystore.KeystoreManager;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.ParamBeanFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.NoSuchBucketException;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.model.S3Object;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilder;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;


/**
 * StorageFactory
 *
 * @author Thang Nguyen
 * @author Wassim Drira
 * @author Andrius Karpavicius
 * @lastModifiedVersion 12.0.0
 *
 */

public class StorageFactory {

    private static String storageType;
    private static String bucketName;

    private static S3FileSystem s3FileSystem;

    private static final String NFS = "FileSystem";
    private static final String S3 = "S3";

    /** Logger. */
    protected static Logger log = LoggerFactory.getLogger(StorageFactory.class);

    public void init() {
        ParamBean tmpParamBean = ParamBeanFactory.getAppScopeInstance();
        storageType = tmpParamBean.getProperty("storage.type", NFS);

        if (storageType.equalsIgnoreCase(S3)) {
            String endpointUrl = tmpParamBean.getProperty("S3.endpointUrl", "endPointUrl");
            String region = tmpParamBean.getProperty("S3.region", "region");
            bucketName = tmpParamBean.getProperty("S3.bucketName", "bucketName");

            String accessKeyId;
            String secretAccessKey;
            boolean credInKeystore = tmpParamBean.getPropertyAsBoolean("S3.credential.in.keystore", false);
            if (credInKeystore) {
                // get accessKeyId and secretAccessKey from the Keystore
                accessKeyId = KeystoreManager.retrieveCredential("S3.accessKeyId");
                secretAccessKey = KeystoreManager.retrieveCredential("S3.secretAccessKey");
            }
            else {
                // get accessKeyId and secretAccessKey from System Settings
                accessKeyId = tmpParamBean.getProperty("S3.accessKeyId", "accessKeyId");
                secretAccessKey = tmpParamBean.getProperty("S3.secretAccessKey", "secretAccessKey");
            }

            S3Client client =
                    S3Client.builder().region(Region.of(region))
                    .endpointOverride(URI.create(endpointUrl))
                            .credentialsProvider(
                                    StaticCredentialsProvider.create(
                                            AwsBasicCredentials.create(accessKeyId, secretAccessKey)))
                            .build();

            boolean validParameters = false;

            try {
                log.info("check configuration parameters of S3 bucket");

                client.headBucket(HeadBucketRequest.builder()
                        .bucket(bucketName)
                        .build());

                validParameters = true;
            }
            catch (NoSuchBucketException e) {
                log.error("NoSuchBucketException exception message : {}", e.getMessage());
            }
            catch (S3Exception e) {
                log.error("S3Exception exception message : {}", e.getMessage());
                log.error("Failed to connect to S3 repository. Check all your S3 configuration parameters : region {}, " +
                        " endpointUrl {}, accessKeyId, and secretAccessKey", region, endpointUrl);
            }

            if (validParameters) {
                log.info("S3 parameters are correctly configured");
            }
            else {
                log.error("S3 parameters are not correctly configured");
                throw S3Exception.builder().build();
            }

            s3FileSystem = new S3FileSystem(new S3FileSystemProvider(), accessKeyId, client, endpointUrl);
        }
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
                log.error("file not found : {} ", e.getMessage());
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
                log.error("IOException message in getInputStream(String) : {}", e.getMessage());
            }
        }

        return null;
    }

    public static InputStream getInputStream(byte[] bytes) {
        if (storageType.equals(NFS)) {
            return new ByteArrayInputStream(bytes);
        }
        else if (storageType.equalsIgnoreCase(S3)) {
            Path objectPath = getObjectPath("");

            try (InputStream inStream = s3FileSystem.provider().newInputStream(objectPath)) {
                inStream.read(bytes);

                return inStream;
            }
            catch (IOException e) {
                log.error("IOException message in getInputStream(byte) : {}", e.getMessage());
            }
        }

        return null;
    }

    public static Reader getReader(File file) {
        if (storageType.equals(NFS)) {
            try {
                return new FileReader(file);
            }
            catch (FileNotFoundException e) {
                log.error("File not found exception in getReader: {}", e.getMessage());
            }
        }
        else if (storageType.equalsIgnoreCase(S3)) {
            InputStream inStream;
            InputStreamReader inputStreamReader;
            String fileName = bucketName + file.getPath().substring(1).replace("\\", "/");

            Path objectPath = getObjectPath(fileName);

            try {
                inStream = s3FileSystem.provider().newInputStream(objectPath);

                inputStreamReader = new InputStreamReader(inStream, StandardCharsets.US_ASCII);

                return inputStreamReader;
            }
            catch (IOException e) {
                log.error("IOException message in getReader : {}", e.getMessage());
            }
        }

        return null;
    }

    public static Reader getReader(String file) {
        if (storageType.equals(NFS)) {
            try {
                return new FileReader(file);
            }
            catch (FileNotFoundException e) {
                log.error("File not found exception : {}", e.getMessage());
            }
        }
        else if (storageType.equalsIgnoreCase(S3)) {
            InputStream inStream;
            InputStreamReader inputStreamReader;
            String fileName = bucketName + file.substring(1).replace("\\", "/");

            Path objectPath = getObjectPath(fileName);

            try {
                inStream = s3FileSystem.provider().newInputStream(objectPath);

                inputStreamReader = new InputStreamReader(inStream, StandardCharsets.US_ASCII);

                return inputStreamReader;
            }
            catch (IOException e) {
                log.error("IOException message in getReader : {}", e.getMessage());
            }
        }

        return null;
    }

    public static Writer getWriter(String file) {
        if (storageType.equals(NFS)) {
            try {
                return new FileWriter(file);
            }
            catch (IOException e) {
                log.error("IO exception : {}", e.getMessage());
            }
        }
        else if (storageType.equalsIgnoreCase(S3)) {
            OutputStream outStream;
            OutputStreamWriter outputStreamWriter;
            String fileName = bucketName + file.substring(1).replace("\\", "/");

            Path objectPath = getObjectPath(fileName);

            try {
                outStream = s3FileSystem.provider().newOutputStream(objectPath);

                outputStreamWriter = new OutputStreamWriter(outStream, StandardCharsets.US_ASCII);

                return outputStreamWriter;
            }
            catch (IOException e) {
                log.error("IOException message in getWriter : {}", e.getMessage());
            }
        }

        return null;
    }

    public static Writer getWriter(File file) {
        if (storageType.equals(NFS)) {
            try {
                return new FileWriter(file);
            }
            catch (IOException e) {
                log.error("File not found exception : {}", e.getMessage());
            }
        }
        else if (storageType.equalsIgnoreCase(S3)) {
            OutputStream outStream;
            OutputStreamWriter outputStreamWriter;
            String fileName = bucketName + file.getPath().substring(1).replace("\\", "/");

            Path objectPath = getObjectPath(fileName);

            try {
                outStream = s3FileSystem.provider().newOutputStream(objectPath);

                outputStreamWriter = new OutputStreamWriter(outStream, StandardCharsets.US_ASCII);

                return outputStreamWriter;
            }
            catch (IOException e) {
                log.error("IOException message in getWriter : {}", e.getMessage());
            }
        }

        return null;
    }

    public static void deleteFile(File file) {
        if (storageType.equals(NFS)) {
            file.delete();
        }
        else if (storageType.equalsIgnoreCase(S3)) {
            String fullObjectKey = formatFullObjectKey(file.toString());

            Path objectPath = getObjectPath(fullObjectKey);

            try {
                s3FileSystem.provider().delete(objectPath);
            }
            catch (IOException e) {
                log.error("IOException message : {}", e.getMessage());
            }
        }
    }

    public static boolean exists(String fileName) {
        if (storageType.equals(NFS)) {
            return new File(fileName).exists();
        }
        else if (storageType.equalsIgnoreCase(S3)) {
            String objectKey = formatObjectKey(fileName);

            ListObjectsV2Response response =
                    s3FileSystem.getClient().listObjectsV2(ListObjectsV2Request.builder().bucket(bucketName).prefix(objectKey).build());

            for (S3Object object : response.contents()) {
                if (object.key().equals(objectKey))
                    return true;
            }

            return false;
        }

        return false;
    }

    public static boolean exists(File file) {
        if (storageType.equals(NFS)) {
            return file.exists();
        }
        else if (storageType.equalsIgnoreCase(S3)) {
            String objectKey = formatObjectKey(file.getPath());

            ListObjectsV2Response response =
                    s3FileSystem.getClient().listObjectsV2(ListObjectsV2Request.builder().bucket(bucketName).prefix(objectKey).build());

            for (S3Object object : response.contents()) {
                if (object.key().equals(objectKey))
                    return true;
            }

            return false;
        }

        return false;
    }

    public static void createNewFile(File file) {
        if (storageType.equals(NFS)) {
            try {
                file.createNewFile();
            }
            catch (IOException e) {
                log.error("IO Exception : {}", e.getMessage());
            }
        }
        else if (storageType.equalsIgnoreCase(S3)) {
            OutputStream outStream;
            String fileName = bucketName + file.getPath().substring(1).replace("\\", "/");

            Path bucketPath = getObjectPath(fileName);

            try {
                outStream = s3FileSystem.provider().newOutputStream(bucketPath);

                outStream.close();
            }
            catch (IOException e) {
                log.error("IOException message in createNewFile : {}", e.getMessage());
            }
        }
    }

    public static PrintWriter getPrintWriter(File file) {
        if (storageType.equals(NFS)) {
            try {
                return new PrintWriter(file);
            }
            catch (FileNotFoundException e) {
                log.error("file not found in getPrintWriter : {}", e.getMessage());
            }
        }
        else if (storageType.equalsIgnoreCase(S3)) {
            PrintWriter printWriter;
            String fileName = bucketName + file.getPath().substring(1).replace("\\", "/");

            Path objectPath = getObjectPath(fileName);

            try {
                printWriter = new PrintWriter(
                        new OutputStreamWriter(s3FileSystem.provider().newOutputStream(objectPath), StandardCharsets.US_ASCII));

                return printWriter;
            }
            catch (IOException e) {
                log.error("IOException message in getPrintWriter : {}", e.getMessage());
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
                log.error("file not found in getInputStream : {}", e.getMessage());
            }
        }
        else if (storageType.equalsIgnoreCase(S3)) {
            InputStream inStream;
            String fullObjectKey = formatFullObjectKey(file.getPath());
            Path objectPath = getObjectPath(fullObjectKey);

            try {
                inStream = s3FileSystem.provider().newInputStream(objectPath);

                return inStream;
            }
            catch (IOException e) {
                log.error("IOException message in getInputStream(File) : {}", e.getMessage());
            }
        }

        return null;
    }

    public static void createDirectory(File file) {
        if (storageType.equals(NFS)) {
            file.mkdirs();
        }
        else if (storageType.equalsIgnoreCase(S3)) {
            String fullObjectKey = formatFullObjectKey(file.toString());

            Path objectPath = getObjectPath(fullObjectKey);

            try {
                s3FileSystem.provider().createDirectory(objectPath);
            }
            catch (IOException e) {
                log.error("IOException message : {}", e.getMessage());
            }
        }

    }

    public static OutputStream getOutputStream(File file) {
        if (storageType.equals(NFS)) {
            try {
                return new FileOutputStream(file);
            }
            catch (FileNotFoundException e) {
                log.error("file not found : {}", e.getMessage());
            }
        }
        else if (storageType.equalsIgnoreCase(S3)) {
            OutputStream outStream;
            String fullObjectKey = formatFullObjectKey(file.getPath());

            Path objectPath = getObjectPath(fullObjectKey);

            try {
                outStream = s3FileSystem.provider().newOutputStream(objectPath);

                return outStream;
            }
            catch (IOException e) {
                log.error("IOException message in getOutputStream : {}", e.getMessage());
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
                log.error("file not found : {}", e.getMessage());
            }
        }
        else if (storageType.equalsIgnoreCase(S3)) {
            OutputStream outStream;
            String fullObjectKey = formatFullObjectKey(fileName);

            Path bucketPath = getObjectPath(fullObjectKey);

            try {
                outStream = s3FileSystem.provider().newOutputStream(bucketPath);

                return outStream;
            }
            catch (IOException e) {
                log.error("IOException message in getOutputStream : {}", e.getMessage());
            }
        }

        return null;
    }

    public static void write(Path path, byte[] bytes, OpenOption... options) {
        if (storageType.equals(NFS)) {
            try {
                Files.write(path, bytes, options);
            }
            catch (IOException e) {
                log.error("IOException exception : {}", e.getMessage());
            }
        }
        else if (storageType.equals(S3)) {
            OutputStream outStream;
            String fullObjectKey = formatFullObjectKey(path.toString());

            Path bucketPath = getObjectPath(fullObjectKey);

            try {
                outStream = Files.newOutputStream(bucketPath);

                outStream.write(bytes);

                outStream.close();
            }
            catch (IOException e) {
                log.error("IOException message in write : {}", e.getMessage());
            }

        }
    }

    public static Document parse(DocumentBuilder db, File file) {
        if (storageType.equals(NFS)) {
            try {
                return db.parse(file);
            }
            catch (SAXException | IOException e) {
                log.error("IOException or SAXException message in parse : {}", e.getMessage());
            }
        }
        else if (storageType.equalsIgnoreCase(S3)) {
            InputStream inStream;
            String fullObjectKey = formatFullObjectKey(file.getPath());

            Path objectPath = getObjectPath(fullObjectKey);

            try {
                inStream = s3FileSystem.provider().newInputStream(objectPath);

                return db.parse(inStream);
            }
            catch (IOException | SAXException e) {
                log.error("IOException or SAXException message in parse : {}", e.getMessage());
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
                log.error("marshaller exception : {}", e.getMessage());
            }
        }
        else if (storageType.equalsIgnoreCase(S3)) {
            OutputStream outStream;
            String fullObjectKey = formatFullObjectKey(file.getPath());

            Path bucketPath = getObjectPath(fullObjectKey);

            try {
                outStream = s3FileSystem.provider().newOutputStream(bucketPath);

                marshaller.marshal(obj, outStream);

                outStream.close();
            }
            catch (JAXBException | IOException e) {
                log.error("IO Exception or JAXBException in marshal method : {}", e.getMessage());
            }
        }
    }

    public static String formatFullObjectKey(String filePath){
        String fullObjectKey = bucketName;
        if (filePath.charAt(0) == '.') {
            fullObjectKey += filePath.substring(1).replace("\\", "/");
        }
        else {
            if (filePath.charAt(1) == '\\')
                fullObjectKey += filePath.replace("\\", "/");
            else
                fullObjectKey += '/' + filePath.replace("\\", "/");
        }

        return fullObjectKey;
    }

    public static String formatObjectKey(String filePath){
        String objectKey = "";

        if (filePath.charAt(0) == '.' && filePath.charAt(1) == '/') {
            objectKey += filePath.substring(2).replace("\\", "/");
        }
        else if (filePath.charAt(0) == '.' && filePath.charAt(1) == '\\'){
            objectKey += filePath.substring(2).replace("\\", "/");
        }

        return objectKey;
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
                log.error("failed to generate PDF file : {}", e.getMessage());
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
                log.error("error message : {}", e.getMessage());
            }
        }
    }

}
