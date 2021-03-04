
//package com.opencellsoft;

import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

class ConnectorCRMMultiService4 {

    /** default folder where stores generated files. */
    private static String path = "C:/tmp/opencelldata/DEMO/imports/";

    /** number of files to be generated. */

    private static int nbFiles = 24;

    private static String CUST_CODE = "JOB_CA";

    private static String CA_CODE = "JOB_CA";

    private static String BA_CODE = "JOB_CA";

    private static String UA_CODE = "JOB_CA";

    private static String SUB_CODE = "JOB_SU";

    private static Boolean CRM_MODE_ONE_FILE = true;

    /**
     * @param args array of input arguments
     * @throws Exception throwing exception if error happens
     */
    public static void main(String[] args) throws Exception {
        if (args.length == 1) {
            path = args[0];
        }

        int from = 1;
        int to = 10;
        int NBsubs = 1;

        int days = 1;
        int cdrsPerDay = 1; 
        int periodBtwCDRs = 1;

        if (args.length == 1) {
            to = Integer.valueOf(args[0]).intValue();
        } else if (args.length == 2) {
            from = Integer.valueOf(args[0]).intValue();
            to = Integer.valueOf(args[1]).intValue();
        } else if (args.length == 3) {
            from = Integer.valueOf(args[0]).intValue();
            to = Integer.valueOf(args[1]).intValue();
            nbFiles = Integer.valueOf(args[2]).intValue();
        } else if (args.length == 4) {
            from = Integer.valueOf(args[0]).intValue();
            to = Integer.valueOf(args[1]).intValue();
            nbFiles = Integer.valueOf(args[2]).intValue();
            path = args[3];
        } else if (args.length == 5) {
            from = Integer.valueOf(args[0]).intValue();
            to = Integer.valueOf(args[1]).intValue();
            nbFiles = Integer.valueOf(args[2]).intValue();
            path = args[3];
            CRM_MODE_ONE_FILE = Boolean.valueOf(args[4]);
        } else if (args.length == 6) {
            from = Integer.valueOf(args[0]).intValue();
            to = Integer.valueOf(args[1]).intValue();
            nbFiles = Integer.valueOf(args[2]).intValue();
            path = args[3];
            CRM_MODE_ONE_FILE = Boolean.valueOf(args[4]);
            days = Integer.valueOf(args[5]);
        } else if (args.length == 7) {
            from = Integer.valueOf(args[0]).intValue();
            to = Integer.valueOf(args[1]).intValue();
            nbFiles = Integer.valueOf(args[2]).intValue();
            path = args[3];
            CRM_MODE_ONE_FILE = Boolean.valueOf(args[4]);
            days = Integer.valueOf(args[5]);
            cdrsPerDay = Integer.valueOf(args[6]);
        } else if (args.length == 8) {
            from = Integer.valueOf(args[0]).intValue();
            to = Integer.valueOf(args[1]).intValue();
            nbFiles = Integer.valueOf(args[2]).intValue();
            path = args[3];
            CRM_MODE_ONE_FILE = Boolean.valueOf(args[4]);
            days = Integer.valueOf(args[5]);
            cdrsPerDay = Integer.valueOf(args[6]);
            periodBtwCDRs = Integer.valueOf(args[7]);
        }

        /*
         * Date date = new Date();
         *
         * long time = date.getTime(); CA_CODE = CA_CODE + time; BA_CODE = BA_CODE + time; UA_CODE = UA_CODE + time; SUB_CODE = SUB_CODE + time;
         */

        genCustomerDD(from, to);
        //genCustomer(from, to);
        //genAccounts(from, to);
        //genSub(from, to, NBsubs);
        //genCDR(from, to, days, cdrsPerDay, periodBtwCDRs);
        /*String footer = "";
        String header = "";
        String fileName = "CDR_" + from + "_" + to + "Test.csv";
        writeFileNormalizedRecord(from, to, days, cdrsPerDay, footer, header, fileName, path);*/
    }

    /**
     * @param from
     * @param to
     * @param days number of days
     * @param cdrsPerDay how many CDRs are generated per day
     * @param periodBtwCDRs period between CDRs in the same day in minutes (1 by default)
     */
    private static void genCDR(int from, int to, int days, int cdrsPerDay, int periodBtwCDRs) {
        String fileName = "CDR_" + from + "_" + to + "Test.csv";
        String header = "";
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR, 1);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.add(Calendar.DATE, -(days + 36)); // @todo -2 au lieu de 0
        // String record = formatDateWithPattern(calendar.getTime(),
        // "yyyy-MM-dd'T'hh:mm:ss.S'Z'") + ";1;OPENCELL-00-SU@@;UNIT"; //correction a -1
        if (periodBtwCDRs == 0) {
            periodBtwCDRs = 1;
        }

        String record = "";
        int d = 1;
        int i = 1;
        for (d = 1; d <= days; d++) {
            for ( i = 1; i <= cdrsPerDay; i++) {
                record += formatDateWithPattern(calendar.getTime(), "yyyy-MM-dd'T'hh:mm:ss.S'Z'") + ";1;"+SUB_CODE + "0_@@_0;UNIT;PS_SUPPORT" + "\n";
                calendar.add(Calendar.MINUTE, periodBtwCDRs);
            }
            calendar.add(Calendar.DATE, 1);
        }
        String footer = "";
        writeFile(from, to, header, record, footer, fileName, path);
    }

    private static String genNormalizedCDR(int from, int to, int days, int cdrsPerDay) {
        String fileName = "CDR_" + from + "_" + to + "Test.csv";
        String header = "";

        // String record = formatDateWithPattern(calendar.getTime(),
        // "yyyy-MM-dd'T'hh:mm:ss.S'Z'") + ";1;OPENCELL-00-SU@@;UNIT"; //correction a -1

        String record = "";
        Random RDays = new Random();
        for (int d = 1; d <= RDays.nextInt(days+1); d++) {
            Random RCSDsPerDay = new Random();
            for (int i = 1; i <= RCSDsPerDay.nextInt(cdrsPerDay+1); i++) {
                Calendar calendar = Calendar.getInstance();
                Random RH = new Random();
                int H = RH.nextInt(23);
                calendar.set(Calendar.HOUR,H);
                Random RM = new Random();
                calendar.set(Calendar.MINUTE,RM.nextInt(59));
                Random RS = new Random();
                calendar.set(Calendar.SECOND, RS.nextInt(59));
                Random RD = new Random();
                calendar.add(Calendar.DATE, -(RD.nextInt(365) + 2)); // @todo -2 au lieu de 0
                record += formatDateWithPattern(calendar.getTime(), "yyyy-MM-dd'T'hh:mm:ss.S'Z'") + ";1;"+SUB_CODE + "0_@@_0\";UNIT;PS_SUPPORT" + "\n";
                //System.out.println(" record " + record);
            }
            //calendar.add(Calendar.DATE, 1);
        }
        return record;
        //riteFile(from, to, header, record, footer, fileName, path);
    }

    private static void genCDR(int from, int to, int days, int cdrsPerDay) {
        genCDR(from, to, days, cdrsPerDay, 1);
    }

    private static void genCDR(int from, int to, int days) {
        genCDR(from, to, days, 1);
    }

    public static int randBetween(int start, int end) {
        return start + (int)Math.round(Math.random() * (end - start));
    }

    private static void genCDR(int from, int to) {
        genCDR(from, to, 1);
    }

    /**
     * @param from start index
     * @param to end index.
     */
    public static void genCustomerDD(int from, int to) {
        String fileName = "CUSTOMER_" + from + "_" + to + ".xml";
        String header = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?> \n" + "<sellers> \n"
                + "<seller code=\"JOB_SELLER@@\" description=\"JOB_SELLER_@@\" tradingCurrencyCode=\"EUR\" tradingCountryCode=\"FR\" tradingLanguageCode=\"FRA\"> \n"
                //+ "<seller code=\"JOB_SELLER1\" description=\"JOB_SELLER_1\" tradingCurrencyCode=\"EUR\" tradingCountryCode=\"FR\" tradingLanguageCode=\"FRA\"> \n"
                + "<customers>\n";
        String custCode = CUST_CODE + "0_@@_0";

        String record = "<customer ignoreCheck=\"true\" code=\"" + custCode + "\" customerCategory=\"CLIENT\" customerBrand=\"DEFAULT\"  tradingLanguageCode=\"FRA\">\n"
                + " <desCustomer>TEST_@@</desCustomer>\n" + " <name>\n" + " <title>MR</title>\n" + " <firstname>CUST_FIRST_NAME0_@@_0</firstname>\n"
                + " <lastName>CUST_LAST_NAME0_@@_0</lastName>\n" + " </name>\n" + " <customerAccounts>\n"
                + " <customerAccount ignoreCheck=\"true\" creditCategory=\"VIP\" tradingLanguageCode=\"FRA\">\n"
                + " <paymentMethod>DIRECTDEBIT</paymentMethod>\n"
                + " <preferred>true</preferred>\n"
                + " <alias>SEPA</alias>\n"
                + " <mandateIdentification>G</mandateIdentification>\n"
                + " <mandateDate>2020-10-10</mandateDate>\n"
                + " <bankCoordinates>"
                + " <bankCode>12456</bankCode>\n"
                + " <IBAN>FR123456789123456789</IBAN>\n"
                + " <bankName>Some Bank</bankName>\n"
                + " <BIC>BDNFR123456</BIC>\n"
                + " <accountNumber>...</accountNumber>\n"
                + " </bankCoordinates>\n"
                + " <code>" + CA_CODE + "0_@@_0" + "</code>\n"
                + " <tradingLanguageCode>FRA</tradingLanguageCode>\n" + " <description>" + CA_CODE + "0_@@_0" + "</description>\n" + " <name>\n" + " <title>MR</title>\n"
                + " <firstname>JOB_FIRST_NAME0_@@_0</firstname>\n" + " <lastName>JOB_LAST_NAME0_@@_0</lastName>\n" + " <name>JOB_LAST_NAME0_@@_0</name>\n" + " </name>\n"
                + " <tradingCurrencyCode>EUR</tradingCurrencyCode>\n" + (CRM_MODE_ONE_FILE ? "<billingAccounts>\n" + getAccounts() + "</billingAccounts>" : "")
                + " </customerAccount>\n" + " </customerAccounts>\n" + " </customer>\n";

        String footer = "</customers>\n" + "</seller>\n" + "</sellers>";

        writeFile(from, to, header, record, footer, fileName, path);
    }


    /**
     * @param from start index
     * @param to end index.
     */

    // Customers with direct debit
    public static void genCustomer(int from, int to) {
        String fileName = "CUSTOMER_" + from + "_" + to + ".xml";
        String header = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?> \n" + "<sellers> \n"
                + "<seller code=\"JOB_SELLER@@\" description=\"JOB_SELLER_@@\" tradingCurrencyCode=\"EUR\" tradingCountryCode=\"FR\" tradingLanguageCode=\"FRA\"> \n"
                + "<customers>\n";
        String custCode = "JOB_CA" + "0_@@_0";

        String record = "<customer ignoreCheck=\"true\" code=\"" + custCode + "\" customerCategory=\"CLIENT\" customerBrand=\"DEFAULT\"  tradingLanguageCode=\"FRA\">\n"
                + " <desCustomer>TEST_@@</desCustomer>\n" + " <name>\n" + " <title>MR</title>\n" + " <firstname>CUST_FIRST_NAME0_@@_0</firstname>\n"
                + " <lastName>CUST_LAST_NAME0_@@_0</lastName>\n" + " </name>\n" + " <customerAccounts>\n"
                + " <customerAccount ignoreCheck=\"true\" creditCategory=\"VIP\" tradingLanguageCode=\"FRA\">\n" + " <paymentMethods>\n" + " <paymentMethod>\n"
                + " <paymentType>CHECK</paymentType>\n" + "</" +
                ">\n" + " </paymentMethods>\n" + " <code>" + CA_CODE + "0_@@_0" + "</code>\n"
                + " <tradingLanguageCode>FRA</tradingLanguageCode>\n" + " <description>" + CA_CODE + "0_@@_0" + "</description>\n" + " <name>\n" + " <title>MR</title>\n"
                + " <firstname>JOB_FIRST_NAME0_@@_0</firstname>\n" + " <lastName>JOB_LAST_NAME0_@@_0</lastName>\n" + " <name>JOB_LAST_NAME0_@@_0</name>\n" + " </name>\n"
                + " <tradingCurrencyCode>EUR</tradingCurrencyCode>\n" + (CRM_MODE_ONE_FILE ? "<billingAccounts>\n" + getAccounts() + "</billingAccounts>" : "")
                + " </customerAccount>\n" + " </customerAccounts>\n" + " </customer>\n";

        String footer = "</customers>\n" + "</seller>\n" + "</sellers>";

        writeFile(from, to, header, record, footer, fileName, path);
    }

    /**
     * @param from
     * @param to
     */
    public static void genSub(int from, int to, int NBSubs) {

        String fileName = "SUB_" + from + "_" + to + ".xml";
        String header = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?> " + "<subscriptions>\n\n";
        String record = "";
        for (int i = 0; i < NBSubs; i++) {
            record = getSubcriptions();
            // writer.write("\n");
        }
        String footer = "</subscriptions>\n";

        writeFile(from, to, header, record, footer, fileName, path);
    }

    private static String getSubcriptions() {
        String record = "" + "<subscription code=\"" + SUB_CODE + "0_@@_0\"" + " userAccountId=\"" + UA_CODE + "0_@@_0\""
                + " offerCode=\"OF_BASIC\" sellerCode=\"JOB_SELLER@@\">\n"; // @todo
        // offer
        // changed
        String dateFormat = "yyyy-MM-dd";
        // String timeFormat = "yyyy-MM-dd'T'hh:mm:ssZ";
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH,1);
        calendar.add(Calendar.DATE, -2); // @todo -2 au lieu de -1
        String SubDate = formatDateWithPattern(calendar.getTime(), dateFormat);
        String EndAgDate = formatDateWithPattern(calendar.getTime(), dateFormat);
        String formatDateWithPattern = formatDateWithPattern(calendar.getTime(), dateFormat);
        //System.out.println(" date here is : " + formatDateWithPattern);
        String subDate = "<subscriptionDate>"+SubDate+"</subscriptionDate>\n";
        String EndDate = "<endAgreementDate>01/05/2021</endAgreementDate>\n";
        String statusDate = "<status date=\"31/12/2020\">ACTIVE</status>\n";
        String subCh = "<subscriptionCharges>\n" + "<code>CH_OSS</code>\n" + "</subscriptionCharges>\n";
        String TerCh = "<terminationCharges>\n" + "<code>CH_OST</code>\n" + "</terminationCharges>\n";
        String ReccCh = "<recurringCharges>\n" + "<code>CH_REC_BUILD_RUN_ADV</code>\n" + "</recurringCharges>\n";
        String UsgCh = "<usageCharges>\n" + "<code>CH_USG_UNIT</code>\n" + "</usageCharges>\n";
        record = record + subDate + EndDate + statusDate + "<description>" + SUB_CODE + "0_@@_0" + " Description </description>\n"

                + "<services>\n"

                + "<serviceInstance code=\"SE_OSS\">\n" + subDate + statusDate + "<quantity>10.000000000000</quantity>\n"+subCh+ "</serviceInstance>\n"

                + "<serviceInstance code=\"SE_OST\">\n" + subDate + statusDate + "<quantity>1.000000000000</quantity>\n" +TerCh+ "</serviceInstance>\n"

                + "<serviceInstance code=\"SE_REC_ADV\">\n" + subDate + statusDate + "<quantity>1.000000000000</quantity>\n"+ReccCh+ "</serviceInstance>\n"

                + "<serviceInstance code=\"SE_USG_UNIT\">\n" + subDate + statusDate + "<quantity>1.000000000000</quantity>\n"+UsgCh+ "</serviceInstance>\n"

                + "</services>\n"

                //+ "<accesses>\n"
                //+ "<access>\n" + "<accessUserId>"+ SUB_CODE + "0_@@_0" + "</accessUserId>\n" + "</access>\n"
                //+ "</accesses>\n"
                
                + "</subscription>\n";

        return record;

    }

    /**
     * @param from
     * @param to
     */
    public static void genAccounts(int from, int to) {
        String fileName = "ACCOUNT_" + from + "_" + to + ".xml";
        String header = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?> \n" + "<billingAccounts>\n";
        String record = getAccounts();

        String footer = "</billingAccounts>\n";

        writeFile(from, to, header, record, footer, fileName, path);

    }

    /**
     * @return billing count as XML-string
     */
    private static String getAccounts() {
        String record = "" + "<billingAccount customerAccountId=\"" + CA_CODE + "0_@@_0\"" + " code=\"" + CA_CODE + "0_@@_0\""
                + " billingCycle=\"CYC_INV_MT_1\">"
                + " <subscriptionDate>01/12/2020</subscriptionDate>"
                + " <description>" + CA_CODE + "0_@@_0 description" + "</description>"
                +   getName()
                +   getAddress()
                + " <electronicBilling>true</electronicBilling>\n"
                + " <bankCoordinates>"
                + " <accountName>"+ "CA_NAME" + "0_@@_0" +"</accountName>"
                + " <bankCode>12456</bankCode>"
                + " <IBAN>FR123456789123456789</IBAN>"
                + " <bankName>Some Bank</bankName>"
                + " <BIC>BDNFR123456</BIC>"
                + " <accountNumber>...</accountNumber>"
                + " <branchCode>...</branchCode>"
                + " <key>"+ "CA_key" + "0_@@_0" +"</key>"
                + " </bankCoordinates>"
                + " <email>fr.fr@fr.com</email>"
                + "<tradingCountryCode>FR</tradingCountryCode>" + "<tradingLanguageCode>FRA</tradingLanguageCode>" + "<userAccounts>" + "<userAccount code=\""
                + UA_CODE + "0_@@_0\">"
                + "<subscriptionDate>01/12/2020</subscriptionDate>\n"
                + "<description>" + UA_CODE + "0_@@_0" + "</description>\n"
                +   getName()
                +   getAddress()
                + (CRM_MODE_ONE_FILE ? "<subscriptions>\n" + getSubcriptions() + "</subscriptions>\n" : "") + "</userAccount>\n" + "</userAccounts>\n" + "</billingAccount>\n";
        return record;
    }

    /**
     * @return Name as XML-string
     */
    private static String getName() {
        String record = "" + "<name>"
                + " <title>CIE</title>"
                + " <firstName>Nuage SAS</firstName>"
                + " <lastName>Nuage SAS</lastName>"
                +"</name>\n";
        return record;
    }

    /**
     * @return Adress as XML-string
     */
    private static String getAddress() {
        String record = "" + "<address>"
                + " <address1>3 rue passante</address1>"
                + " <address2>Batiment A</address2>"
                + " <address3>Bureau D48</address3>"
                + " <zipCode>75001</zipCode>"
                + " <city>PARIS</city>"
                + " <country>FR</country>"
                +"</address>\n";
        return record;
    }

    /**
     * @param from start index
     * @param to end index
     * @param header header of file's content
     * @param record body of file's content
     * @param footer footer of file's content
     * @param fileName file's name to be generated
     * @param path the path to store generated files
     */
    public static void writeFile(int from, int to, String header, String record, String footer, String fileName, String path) {

        String[] splits = fileName.split("\\.");
        String fName = "";
        String extension = "";
        if (splits.length == 2) {
            fName = splits[0];
            extension = splits[1];
        }

        int range = ((to - from) + 1) / nbFiles;
        System.out.println(" to  : " + to);
        System.out.println(" range  : " + range);
        System.out.println(" nbFiles  : " + nbFiles);
        for (int index = 1; index <= nbFiles; index++) {
            // Generate from index
            int startIndex = from + (index - 1) * range; // The start index (ex : start from 1 to 1000, it takes the value 1)
            int endIndex = from + index * range; // The end index (ex : start from 1 to 1000 it takes the value 1000)
            String finalFileName = fName + "_" + "P_" + index + "." + extension;
            File fichier = new File(path + "/" + finalFileName);
            try {
                fichier.createNewFile();
                final FileWriter writer = new FileWriter(fichier);
                try {
                    // writer.write(header);
                    writer.write(header.replaceAll("@@", "" + index));
                    for (int i = startIndex; i < endIndex; i++) {
                        writer.write(record.replaceAll("@@", "" + i));
                        // writer.write("\n");
                    }
                    writer.write(footer);
                    System.out.println("Done for : " + finalFileName);
                } finally {
                    writer.close();
                }
            } catch (Exception e) {
                System.out.println("Impossible de creer le fichier" + e.getMessage());
            }
        }

        if (from + (nbFiles * range) <= to) {
            String finalFileName = fName + "_" + "P_" + (nbFiles + 1) + "." + extension;
            File fichier = new File(path + "/" + finalFileName);
            try {
                fichier.createNewFile();
                final FileWriter writer = new FileWriter(fichier);
                try {
                    // writer.write(header);
                    writer.write(header.replaceAll("@@", "" + (nbFiles + 1)));
                    for (int i = from + nbFiles * range; i <= to; i++) {
                        writer.write(record.replaceAll("@@", "" + i));
                        writer.write("\n");
                    }
                    writer.write(footer);
                    System.out.println("Done for : " + finalFileName);
                } finally {
                    writer.close();
                }
            } catch (Exception e) {
                System.out.println("Impossible de creer le fichier" + e.getMessage());
            }
        }
    }

    public static void writeFileNormalizedRecord(int from, int to, int days, int cdrsPerDay, String header, String footer, String fileName, String path) {

        String[] splits = fileName.split("\\.");
        String fName = "";
        String extension = "";
        if (splits.length == 2) {
            fName = splits[0];
            extension = splits[1];
        }

        int range = ((to - from) + 1) / nbFiles;
        System.out.println(" to  : " + to);
        System.out.println(" range  : " + range);
        System.out.println(" nbFiles  : " + nbFiles);
        for (int index = 1; index <= nbFiles; index++) {
            // Generate from index
            int startIndex = from + (index - 1) * range; // The start index (ex : start from 1 to 1000, it takes the value 1)
            int endIndex = from + index * range; // The end index (ex : start from 1 to 1000 it takes the value 1000)
            String finalFileName = fName + "_" + "P_" + index + "." + extension;
            File fichier = new File(path + "/" + finalFileName);
            try {
                fichier.createNewFile();
                final FileWriter writer = new FileWriter(fichier);
                try {
                    // writer.write(header);
                    writer.write(header.replaceAll("@@", "" + index));
                    for (int i = startIndex; i < endIndex; i++) {
                        String record  = genNormalizedCDR(from,to,days,cdrsPerDay);
                        System.out.println(" index : " + i);
                        writer.write(record.replaceAll("@@", "" + i));
                        // writer.write("\n");
                    }
                    writer.write(footer);
                    System.out.println("Done for : " + finalFileName);
                } finally {
                    writer.close();
                }
            } catch (Exception e) {
                System.out.println("Impossible de creer le fichier" + e.getMessage());
            }
        }

        if (from + (nbFiles * range) <= to) {
            String finalFileName = fName + "_" + "P_" + (nbFiles + 1) + "." + extension;
            File fichier = new File(path + "/" + finalFileName);
            try {
                fichier.createNewFile();
                final FileWriter writer = new FileWriter(fichier);
                try {
                    // writer.write(header);
                    writer.write(header.replaceAll("@@", "" + (nbFiles + 1)));
                    for (int i = from + nbFiles * range; i <= to; i++) {
                        String record  = genNormalizedCDR(from,to,days,cdrsPerDay);
                        writer.write(record.replaceAll("@@", "" + i));
                        writer.write("\n");
                    }
                    writer.write(footer);
                    System.out.println("Done for : " + finalFileName);
                } finally {
                    writer.close();
                }
            } catch (Exception e) {
                System.out.println("Impossible de creer le fichier" + e.getMessage());
            }
        }
    }

    /**
     * @param value
     * @param pattern
     * @return
     */
    public static String formatDateWithPattern(Date value, String pattern) {
        if (value == null) {
            return "";
        }
        String result = null;
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);

        try {
            result = sdf.format(value);
        } catch (Exception e) {
            System.out.println("Error format" + e.getMessage());
            result = "";
        }

        return result;
    }

}
