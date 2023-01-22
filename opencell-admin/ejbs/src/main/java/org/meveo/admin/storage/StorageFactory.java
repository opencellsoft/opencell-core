package org.meveo.admin.storage;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRXmlDataSource;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.carlspring.cloud.storage.s3fs.S3FileSystem;
import org.carlspring.cloud.storage.s3fs.S3FileSystemProvider;
import org.meveo.admin.job.SortingFilesEnum;
import org.meveo.commons.keystore.KeystoreManager;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.ParamBeanFactory;
import org.meveo.commons.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CopyObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.NoSuchBucketException;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.model.S3Object;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilder;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
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
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


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

    /**
     * init StorageFactory.
     *
     * required configuration parameters : endpointUrl, region, accessKeyId, secretAccessKey, bucketName
     */
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
                    S3Client.builder().forcePathStyle(true).region(Region.of(region))
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

    /**
     * get S3Client instance
     *
     * @return S3Client
     */
    public static S3Client getS3Client() {
        return s3FileSystem.getClient();
    }

    /**
     * get bucket name
     *
     * @return bucketName bucket name
     */
    public static String getBucketName() {
        return bucketName;
    }

    /**
     * get path of object in S3
     *
     * @param objectPath String
     * @return Path object
     */
    public static Path getObjectPath(String objectPath) {
        return s3FileSystem.getPath("/" + objectPath);
    }

    /**
     * get inputStream based on a filename S3
     *
     * @param fileName String
     * @return InputStream
     */
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

    /**
     * get inputStream based on array of bytes
     *
     * @param bytes array of bytes
     * @return InputStream
     */
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

    /**
     * get reader based on file, to read data from a file
     *
     * @param file a file
     * @return Reader
     */
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
            String fileName = formatObjectKey(bucketName + File.separator + file.getPath());

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

    /**
     * get buffer reader to read data from a file
     *
     * @param file a file
     * @return BufferReader
     */
    public static Reader getBufferedReader(File file) {
        if (storageType.equals(NFS)) {
            try {
                return new BufferedReader(new FileReader(file));
            }
            catch (FileNotFoundException e) {
                log.error("File not found exception in getReader: {}", e.getMessage());
            }
        }
        else if (storageType.equalsIgnoreCase(S3)) {
            InputStream inStream;
            String fileName = formatObjectKey(bucketName + File.separator + file.getPath());

            Path objectPath = getObjectPath(fileName);

            try {
                inStream = s3FileSystem.provider().newInputStream(objectPath);

                return new BufferedReader(new InputStreamReader(inStream));
            }
            catch (IOException e) {
                log.error("IOException message in getReader : {}", e.getMessage());
            }
        }

        return null;
    }

    /**
     * get reader based on file, to read data from a file
     *
     * @param file String filename of the file
     * @return Reader
     */
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

    /**
     * get writer based on file, used to write character-oriented data to a file.
     *
     * @param file String filename of the file
     * @return Writer
     */
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

    /**
     * get writer based on file, used to write character-oriented data to a file.
     *
     * @param file the file
     * @return Writer
     */
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

    /**
     * check existence of a file on File System or S3.
     *
     * @param fileName String the filename
     * @return true if file exists, false otherwise
     */
    public static boolean exists(String fileName) {
        if (storageType.equals(NFS)) {
            return new File(fileName).exists();
        }
        else if (storageType.equalsIgnoreCase(S3)) {
            String objectKey = formatObjectKey(fileName);
            log.debug("check existence of an object based in fileName on S3 at key {}", objectKey);

            try {
                s3FileSystem.getClient()
                        .headObject(HeadObjectRequest.builder().bucket(bucketName).key(objectKey).build());

                return true;
            } catch (NoSuchKeyException e) {
                return false;
            }
        }

        return false;
    }

    /**
     * check existence of a file on File System or S3.
     *
     * @param file the file
     * @return true if file exists, false otherwise
     */
    public static boolean exists(File file) {
        if (storageType.equals(NFS)) {
            return file.exists();
        }
        else if (storageType.equalsIgnoreCase(S3)) {
            String objectKey = formatObjectKey(file.getPath());
            log.debug("check existence of an object based on file on S3 at key {}", objectKey);

            try {
                s3FileSystem.getClient()
                        .headObject(HeadObjectRequest.builder().bucket(bucketName).key(objectKey).build());

                return true;
            } catch (NoSuchKeyException e) {
                return false;
            }
        }

        return false;
    }

    /**
     * check existence of a directory on File System or S3.
     *
     * @param directory the directory
     * @return true if directory exists, false otherwise
     */
    public static boolean existsDirectory(File directory) {
        if (storageType.equals(NFS)) {
            return directory.exists();
        }
        else if (storageType.equalsIgnoreCase(S3)) {
            if (! directory.getPath().isBlank()) {
                String objectKey = formatObjectKey(directory.getPath()) + "/";
                log.debug("check existence of a directory on S3 at key {}", objectKey);

                try {
                    s3FileSystem.getClient()
                            .headObject(HeadObjectRequest.builder().bucket(bucketName).key(objectKey).build());

                    return true;
                } catch (NoSuchKeyException e) {
                    return false;
                }
            }

            return true;
        }

        return false;
    }

    /**
     * create a new directory on File System or S3.
     *
     * @param directory the directory
     */
    public static void mkdirs(File directory) {
        if (storageType.equals(NFS)) {
            directory.mkdirs();
        }
        else if (storageType.equalsIgnoreCase(S3)) {
            String objectKey = formatObjectKey(directory.getPath()) + "/";
            log.debug("create a directory in S3 at key {}", objectKey);

            while (objectKey != null) {
                if (! existsDirectory(new File(objectKey))) {
                    putObject(objectKey, RequestBody.empty());
                }
                else {
                    break;
                }

                String parentPath = new File(objectKey).getParent();
                if (parentPath != null) {
                    objectKey = formatObjectKey(parentPath) + "/";
                }
                else {
                    objectKey = null;
                }
            }
        }
    }

    /**
     * delete a directory on File System or S3.
     *
     * @param srcDir the directory
     */
    public static void deleteDirectory(File srcDir) {
        if (storageType.equals(NFS)) {
            try {
                FileUtils.deleteDirectory(srcDir);
            } catch (IOException e) {
                log.error("IOException message {}", e.getMessage());
            }
        }
        else if (storageType.equalsIgnoreCase(S3)) {
            Set<String> setKeys = listAllSubFoldersAndFiles(srcDir);

            // remove all sub-folders and files objects
            for (String key : setKeys) {
                log.debug("object on S3 to remove at the key {}", key);
                deleteObject(key);
            }
        }
    }

    /**
     * create a new empty file on File System or S3.
     *
     * @param file the file
     */
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

    /**
     * get PrintWriter of a file on File System or S3.
     *
     * @param file the file
     * @return PrintWriter object
     */
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

    /**
     * get InputStream of a file.
     *
     * @param file the file
     * @return InputStream object
     */
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
            String objectKey = formatObjectKey(file.getPath());
            log.debug("objectKey in getInputStream {}", objectKey);

            GetObjectRequest request = GetObjectRequest.builder().bucket(bucketName).key(objectKey).build();

            return s3FileSystem.getClient().getObject(request);
        }

        return null;
    }

    /**
     * create a directory.
     *
     * @param file the file
     */
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

    /**
     * get OutputStream of a file.
     *
     * @param file the file
     * @return OutputStream object
     */
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

    /**
     * get OutputStream of a file.
     *
     * @param fileName the filename
     * @return OutputStream object
     */
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

    /**
     * Writes bytes to a file.
     * @param   path
     *          the path to the file
     * @param   bytes
     *          the byte array with the bytes to write
     * @param   options
     *          options specifying how the file is opened
     */
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

    /**
     * Parse the content of the given file as an XML document
     * and return a new DOM {@link Document} object.
     *
     * @param file The file containing the XML to parse.
     *
     * @return Document object
     */
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

    /**
     * Marshal to XML File
     *
     * @param marshaller The Marshaller object.
     *
     * @param   obj
     *          the object to be marshalled
     * @param   file
     *          the file to it the object will be marshalled
     */
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

    /**
     * format object key with bucketName
     * and return a string of object key.
     *
     * @param filePath The file containing the XML to parse.
     *
     * @return String
     */
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

    /**
     * format object key without bucketName
     * and return a string of object key.
     *
     * @param filePath The file containing the XML to parse.
     *
     * @return String
     */
    public static String formatObjectKey(String filePath){
        String objectKey = "";

        if (! filePath.isBlank()) {
            if (filePath.charAt(0) == '.' && filePath.charAt(1) == '/') {
                objectKey += filePath.substring(2).replace("\\", "/");
            }
            else if (filePath.charAt(0) == '.' && filePath.charAt(1) == '\\'){
                objectKey += filePath.substring(2).replace("\\", "/");
            }
            else {
                objectKey += filePath.replace("\\", "/");
            }
        }

        return objectKey;
    }

    /**
     * export report to a pdf file
     *
     * @param jasperPrint the JasperPrint object.
     *
     * @param fileName String
     */
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

    /**
     * creates a data source of type JRXmlDataSource from a file
     *
     * @param file a file.
     *
     * @return JRXmlDataSource JRXmlDataSource object
     */
    public static JRXmlDataSource getJRXmlDataSource(File file) {
        if (storageType.equals(NFS)) {
            try {
                return new JRXmlDataSource(file);
            } catch (JRException e) {
                log.error("JRException : {}", e.getMessage());
            }
        }
        else if (storageType.equalsIgnoreCase(S3)) {
            try {
                return new JRXmlDataSource(getInputStream(file));
            } catch (JRException e) {
                log.error("JRException in getJRXmlDataSource : {}", e.getMessage());
            }
        }

        return null;
    }

    /**
     * list all files inside of a directory with extensions
     *
     * @param sourceDirectory a string of source directory.
     * @param extensions      list of file extensions.
     * @param sortingOption       the sorting option
     * @return a file arrays inside of the source directory
     */
    public static File[] listFiles(String sourceDirectory, final List<String> extensions, String sortingOption) {
        File[] files = null;
        if (storageType.equals(NFS)) {
            files = org.meveo.commons.utils.FileUtils.listFiles(sourceDirectory, extensions);
        }
        else if (storageType.equalsIgnoreCase(S3)) {
            String objectKey = formatObjectKey(sourceDirectory);
            log.debug("list files in S3 bucket at directory {} with extension", objectKey);

            final ListObjectsV2Request objectRequest =
                    ListObjectsV2Request.builder()
                            .bucket(bucketName)
                            .prefix(objectKey)
                            .build();

            ListObjectsV2Response listObjects = s3FileSystem.getClient().listObjectsV2(objectRequest);

            List<File> listFiles = new ArrayList<>();

            for (S3Object object : listObjects.contents()) {
                if (object.size() > 0) {
                    listFiles.add(new File(object.key()));
                }
            }

            files = listFiles.toArray(new File[0]);
        }

        return sortFiles(files, sortingOption);
    }

    /**
     * move an object from a directory to an other one
     *
     * @param srcKey source key of object.
     * @param destKey destination key of object.
     *
     */
    public static void moveFileOrObject(String srcKey, String destKey) {
        if (storageType.equals(NFS)) {
            try {
                Files.move(Paths.get(srcKey), Paths.get(destKey), StandardCopyOption.ATOMIC_MOVE);
            } catch (IOException e) {
                log.error("IOException while moving file : {}", e.getMessage());
            }
        }
        else if (storageType.equalsIgnoreCase(S3)) {
            log.debug("move object from source key {} to destination key {}", srcKey, destKey);
            // copy object from srckey to destKey
            CopyObjectRequest copyObjRequest = CopyObjectRequest.builder()
                    .sourceBucket(bucketName).sourceKey(srcKey)
                    .destinationBucket(bucketName).destinationKey(destKey)
                    .build();

            try {
                s3FileSystem.getClient().copyObject(copyObjRequest);
            }
            catch (NoSuchKeyException e) {
                log.error("NoSuchKeyException while copying object in addExtension method : {}", e.getMessage());
            }

            log.debug("delete old object at source key {}", srcKey);
            // delete old object
            DeleteObjectRequest deleteObjRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName).key(srcKey)
                    .build();

            try {
                s3FileSystem.getClient().deleteObject(deleteObjRequest);
            }
            catch (NoSuchKeyException e) {
                log.error("NoSuchKeyException while deleting object in addExtension method : {}", e.getMessage());
            }
        }
    }

    /**
     * rename a file in FS, or object in S3
     *
     * @param srcFile file whose name/extension needs to be changed/modified.
     * @param destFile new file after modification.
     *
     * @return true if name is successfully renamed, false otherwise
     */
    public static boolean renameTo(File srcFile, File destFile) {
        if (storageType.equals(NFS)) {
            return srcFile.renameTo(destFile);
        }
        else if (storageType.equalsIgnoreCase(S3)) {
            String srcKey = formatObjectKey(srcFile.getPath());
            String destKey = formatObjectKey(destFile.getPath());
            log.debug("rename key object in S3 bucket from source key {} to destination key {}", srcKey, destKey);

            moveFileOrObject(srcKey, destKey);

            return true;
        }

        return false;
    }

    /**
     * Tests whether the file denoted by this abstract pathname is a directory.
     *
     * @param directory the directory
     * @return true if file is directory, false otherwise
     */
    public static boolean isDirectory(File directory) {
        if (storageType.equals(NFS)) {
            return directory.isDirectory();
        }
        else if (storageType.equalsIgnoreCase(S3)) {
            String objectKey = formatObjectKey(directory.getPath()) + "/";
            log.debug("check if object is a directory in S3 bucket at key {}", objectKey);

            if (! exists(objectKey))
                return false;

            HeadObjectRequest request = HeadObjectRequest.builder().bucket(bucketName).key(objectKey).build();

            HeadObjectResponse response = s3FileSystem.getClient().headObject(request);

            return response.contentLength() <= 0;
        }

        return false;
    }

    /**
     * Tests if the file is a valid zip file.
     *
     * @param file the file
     * @return true if file is a valid zip file, false otherwise
     */
    public static boolean isValidZip(File file) {
        if (storageType.equals(NFS)) {
            return org.meveo.commons.utils.FileUtils.isValidZip(file);
        }
        else if (storageType.equalsIgnoreCase(S3)) {
            if (! FilenameUtils.getExtension(file.getName()).equals("zip"))
                return false;

            return exists(file);
        }

        return false;
    }

    /**
     * list all files inside of a directory
     *
     * @param sourceDirectory a source directory.
     * @param filter FilenameFilter.
     *
     * @return a file arrays inside of the source directory
     */
    public static File[] listFiles(File sourceDirectory, FilenameFilter filter) {
        if (storageType.equals(NFS)) {
            return sourceDirectory.listFiles(filter);
        }
        else if (storageType.equalsIgnoreCase(S3)) {
            String objectKey = formatObjectKey(sourceDirectory.getPath());
            log.debug("list files in S3 bucket at directory {} with FilenameFilter ", objectKey);

            final ListObjectsV2Request objectRequest =
                    ListObjectsV2Request.builder()
                            .bucket(bucketName)
                            .prefix(objectKey)
                            .build();

            ListObjectsV2Response listObjects = s3FileSystem.getClient().listObjectsV2(objectRequest);

            List<File> files = new ArrayList<>();

            for (S3Object object : listObjects.contents()){
                if (object.size() > 0) {
                    files.add(new File(object.key()));
                }
            }

            return files.toArray(new File[0]);
        }

        return null;
    }

    /**
     * list all files inside of a directory
     *
     * @param sourceDirectory a source directory.
     *
     * @return a file arrays inside of the source directory
     */
    public static String[] list(File sourceDirectory) {
        if (storageType.equals(NFS)) {
            return sourceDirectory.list();
        }
        else if (storageType.equalsIgnoreCase(S3)) {
            String objectKey = formatObjectKey(sourceDirectory.getPath()) + "/";
            log.debug("list files in S3 bucket at objectKey {}", objectKey);

            final ListObjectsV2Request objectRequest =
                    ListObjectsV2Request.builder()
                            .bucket(bucketName)
                            .prefix(objectKey)
                            .build();

            ListObjectsV2Response listObjects = s3FileSystem.getClient().listObjectsV2(objectRequest);

            Set<String> files = new HashSet<>();

            String patternStr = "^" + objectKey + "([^/\n]+/?)";

            Pattern pattern = Pattern.compile(patternStr);

            List<S3Object> s3Objects = listObjects.contents();

            for (S3Object obj : s3Objects) {
                Matcher matcher = pattern.matcher(obj.key());
                if (matcher.find() && matcher.group(1) != null) {
                    files.add(matcher.group(1));
                }
            }

            return files.toArray(new String[0]);
        }

        return null;
    }

    /**
     * Sort the list of files
     *
     * @param files         the files tob sorted
     * @param sortingOption the sorting option
     * @return the sorted list of files
     */
    public static File[] sortFiles(File[] files, String sortingOption) {
        if (files != null && files.length > 0 && !StringUtils.isBlank(sortingOption)) {
            if (SortingFilesEnum.ALPHA.name().equals(sortingOption)) {
                Arrays.sort(files, (a, b) -> a.getName().compareToIgnoreCase(b.getName()));
            } else if (SortingFilesEnum.CREATION_DATE.name().equals(sortingOption)) {
                Arrays.sort(files, (a, b) -> Long.compare(a.lastModified(), b.lastModified()));
            }
        }
        return files;
    }

    /**
     * delete a file on File System or an object on S3
     *
     * @param file a file to delete.
     *
     */
    public static void delete(File file) {
        if (storageType.equals(NFS)) {
            file.delete();
        }
        else if (storageType.equalsIgnoreCase(S3)) {
            deleteObject(file.getPath());
        }
    }

    /**
     * put an object on S3
     *
     * @param objectKey an objectKey.
     * @param body RequestBody.
     *
     */
    public static void putObject(String objectKey, RequestBody body) {
        PutObjectRequest request = PutObjectRequest.builder().bucket(bucketName)
                .key(objectKey).build();

        s3FileSystem.getClient().putObject(request, body);
    }

    /**
     * delete an object on S3
     *
     * @param filePath a file path.
     *
     */
    public static void deleteObject(String filePath) {
        String objectKey = StorageFactory.formatObjectKey(filePath);

        log.debug("delete object with key {} in S3 bucket", objectKey);

        DeleteObjectRequest request = DeleteObjectRequest.builder().bucket(bucketName)
                .key(objectKey).build();

        s3FileSystem.getClient().deleteObject(request);
    }

    /**
     * check if storage type is S3
     *
     * @return true if S3 is used, false otherwise
     */
    public static boolean isS3Activated() {
        return storageType.equals(S3);
    }

    /**
     * list sub-files and sub-folders inside of a directory
     *
     * @param sourceDirectory a source directory.
     *
     * @return a file arrays inside of the source directory
     */
    public static Map<String, Date> listSubFoldersAndFiles(File sourceDirectory){
        String sourceDir = formatObjectKey(sourceDirectory.getPath() + "/");
        log.debug("list files and directories in S3 bucket at directory {} ", sourceDir);

        final ListObjectsV2Request objectRequest = ListObjectsV2Request.builder()
                .bucket(bucketName)
                .prefix(sourceDir)
                .build();

        ListObjectsV2Response listObjects = s3FileSystem.getClient().listObjectsV2(objectRequest);

        String patternStr = "^" + sourceDir + "([^/\n]+/?).*";

        Pattern pattern = Pattern.compile(patternStr);

        List<S3Object> s3Objects = listObjects.contents();

        Map<String, Date> result = new HashMap<>();

        for (S3Object obj : s3Objects) {
            Matcher matcher = pattern.matcher(obj.key());
            if (matcher.find()) {
                result.put(matcher.group(1), Date.from(obj.lastModified()));
            }
        }

        return result;
    }

    /**
     * list all nested sub-files and sub-folders inside of a directory
     *
     * @param sourceDirectory a source directory.
     *
     * @return a file arrays inside of the source directory
     */
    public static Set<String> listAllSubFoldersAndFiles(File sourceDirectory){
        String sourceDir = formatObjectKey(sourceDirectory.getPath() + "/");
        log.debug("list all nested files and directories in S3 bucket at source directory {} ", sourceDir);

        final ListObjectsV2Request objectRequest = ListObjectsV2Request.builder()
                .bucket(bucketName)
                .prefix(sourceDir)
                .build();

        ListObjectsV2Response listObjects = s3FileSystem.getClient().listObjectsV2(objectRequest);

        String patternStr = "^" + sourceDir + "([^/\n]*/?)*";

        Pattern pattern = Pattern.compile(patternStr);

        List<S3Object> s3Objects = listObjects.contents();

        Set<String> setKeys = new HashSet<>();

        for (S3Object obj : s3Objects) {
            Matcher matcher = pattern.matcher(obj.key());
            if (matcher.matches()) {
                setKeys.add(obj.key());
            }
        }

        return setKeys;
    }

    /**
     * get length of a file on File System or length of an object on S3
     *
     * @param file a file.
     *
     * @return a file arrays inside of the source directory
     */
    public static long length(File file) {
        if (storageType.equals(NFS)) {
            return file.length();
        }
        else if (storageType.equalsIgnoreCase(S3)) {
            String objectKey = formatObjectKey(file.getPath());
            log.debug("get length of object on S3 at key {}", objectKey);

            HeadObjectRequest request = HeadObjectRequest.builder().bucket(bucketName).key(objectKey).build();

            HeadObjectResponse response = s3FileSystem.getClient().headObject(request);

            return response.contentLength();
        }

        return 0L;
    }

}
