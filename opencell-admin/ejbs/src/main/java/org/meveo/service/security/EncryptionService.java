package org.meveo.service.security;

import org.meveo.commons.encryption.EncryptionFactory;
import org.meveo.service.crm.impl.AccountEntitySearchService;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.math.BigInteger;
import java.util.List;
import java.util.Objects;

@Stateless
public class EncryptionService {

    public static final String PROCESSED_ALL_TABLES = "all";

    private static final String EMAIL = "email";

    private static final String PHONE = "phone";

    private static final String MOBILE = "mobile";

    private static final String FIRSTNAME = "firstname";

    private static final String LASTNAME = "lastname";

    private static final String IBAN = "iban";

    private static final String BIC = "bic";

    private static int nbItemsCorrectlyProcessed;

    @Inject
    private AccountEntitySearchService accountEntityService;

    public void changeEncAlgoOrKey(String table){
        String processedTable;

        if (table == null)
            return;
        else {
            if (table.equals(PROCESSED_ALL_TABLES))
                processedTable = PROCESSED_ALL_TABLES;
            else
                processedTable = table;
        }

        nbItemsCorrectlyProcessed = 0;
        updateEncryptedValues(processedTable);
    }

    private void updateEncryptedValues(String processedTable){
        // update all encrypted values with new encryption algo or new key for all database
        if (processedTable.equals(PROCESSED_ALL_TABLES)) {
            System.out.println("update encrypted all tables");
            updateEncryptedCFValuesOnAllTables();
            updateEncryptedDataOnAllTables();
            System.out.println("------------finish update encrypted all tables-----------");
        }
        // update all encrypted values with new encryption algo or new key for a particular table
        else {
            System.out.println("update encrypted on particular table");
            updateEncryptedCFValues(processedTable);
            updateEncryptedData(processedTable);
            System.out.println("------------finish update encrypted on particular table-----------");
        }
    }

    private void updateEncryptedCFValuesOnAllTables(){
        // get tables with CF values
        List<String> tablesWithCfValues = getTablesWithCfValues();

        for (String tableName : tablesWithCfValues) {
            updateEncryptedCFValues(tableName);
        }
    }

    private void updateEncryptedCFValues(String tableName){
        assert accountEntityService != null;

        String getEncCFRequest = "SELECT id, CAST(cf_values AS TEXT)\n" +
                "FROM " + tableName + "\n" +
                "WHERE CAST(cf_values AS TEXT) LIKE '%AES%' OR CAST(cf_values AS TEXT) LIKE '%pref%'";

        List<Object[]> entities = accountEntityService.getEntityManager().createNativeQuery(getEncCFRequest)
                .getResultList();

        for (Object[] result : entities) {
            long cfId = ((BigInteger) result[0]).longValue();
            String cfValue = (String) result[1];

            int startIdx = cfValue.contains("\"" + EncryptionFactory.ENCRYPTION_CHECK_STRING) ?
                    cfValue.indexOf("\"" + EncryptionFactory.ENCRYPTION_CHECK_STRING)
                    : cfValue.indexOf("\"" + EncryptionFactory.PREFIX);

            while (startIdx >= 0) {
                int endIdx = startIdx + cfValue.substring(startIdx + 1).indexOf("\"");
                String encryptedText = cfValue.substring(startIdx + 1, endIdx + 1);
                String clearText = EncryptionFactory.decrypt(encryptedText);

                cfValue = cfValue.replace(encryptedText,
                        Objects.requireNonNull(EncryptionFactory.encrypt(clearText)));

                startIdx = cfValue.substring(startIdx + 1).contains("\"" + EncryptionFactory.ENCRYPTION_CHECK_STRING) ?
                        cfValue.indexOf("\"" + EncryptionFactory.ENCRYPTION_CHECK_STRING, startIdx + 1)
                        : cfValue.indexOf("\"" + EncryptionFactory.PREFIX, startIdx + 1);
            }

            String updateEncCFRequest = "UPDATE " + tableName + " SET cf_values='" + cfValue + "' WHERE id = " + cfId;

            int resultStm = accountEntityService.getEntityManager().createNativeQuery(updateEncCFRequest).executeUpdate();

            if (resultStm > 0)
                nbItemsCorrectlyProcessed++;
        }
    }

    private List<String> getTablesWithCfValues() {
        String getTablesWithCFValReq = "SELECT table_name FROM information_schema.COLUMNS WHERE column_name='cf_values'";
        assert accountEntityService != null;

        return accountEntityService.getEntityManager().createNativeQuery(getTablesWithCFValReq).getResultList();
    }

    private void updateEncryptedDataOnAllTables(){
        // get tables with ContactInformation
        String[] columnNames = new String[]{PHONE, EMAIL, MOBILE};
        List<String> tablesWithContactInfo = getTablesWithEncryptedColumns(columnNames);
        for (String tableName : tablesWithContactInfo) {
            decryptValues(tableName, columnNames);
        }

        // get tables with Name
        columnNames = new String[]{FIRSTNAME, LASTNAME};
        List<String> tablesWithName = getTablesWithEncryptedColumns(columnNames);
        for (String tableName : tablesWithName) {
            decryptValues(tableName, columnNames);
        }

        // get tables with BankCoordinates
        columnNames = new String[]{IBAN, BIC};
        List<String> tablesWithBankCoordinates = getTablesWithEncryptedColumns(columnNames);
        for (String tableName : tablesWithBankCoordinates) {
            decryptValues(tableName, columnNames);
        }
    }

    private void updateEncryptedData(String table){
        String[] columnNames = new String[]{PHONE, EMAIL, MOBILE};
        List<String> tablesWithContactInfo = getTablesWithEncryptedColumns(columnNames);
        if (tablesWithContactInfo.contains(table))
            decryptValues(table, columnNames);

        columnNames = new String[]{FIRSTNAME, LASTNAME};
        List<String> tablesWithName = getTablesWithEncryptedColumns(columnNames);
        if (tablesWithName.contains(table))
            decryptValues(table, columnNames);

        columnNames = new String[]{IBAN, BIC};
        List<String> tablesWithBankCoordinates = getTablesWithEncryptedColumns(columnNames);
        if (tablesWithBankCoordinates.contains(table))
            decryptValues(table, columnNames);
    }

    private List getTablesWithEncryptedColumns(String[] columnNames) {
        StringBuilder getTablesBd = new StringBuilder("SELECT table_name FROM information_schema.COLUMNS \n" +
                "WHERE COLUMN_NAME IN ('");
        for (String columnName : columnNames)
            getTablesBd.append(columnName).append("','");

        String getTablesWithEncColReq = getTablesBd.substring(0, getTablesBd.length() - 3);
        getTablesWithEncColReq += "')\n" + "GROUP BY table_name\n" + "HAVING COUNT(*) = " + columnNames.length;

        return accountEntityService.getEntityManager()
                .createNativeQuery(getTablesWithEncColReq)
                .getResultList();
    }

    private void decryptValues(String tableName, String[] columnNames) {
        StringBuilder getEncValuesBd = new StringBuilder("SELECT id,");
        for (String columnName : columnNames) {
            getEncValuesBd.append(columnName).append(",");
        }
        String getEncValuesRequest = getEncValuesBd.substring(0, getEncValuesBd.length() - 1);
        getEncValuesRequest += " FROM " + tableName;

        List<Object[]> entities = accountEntityService.getEntityManager().createNativeQuery(getEncValuesRequest)
                .getResultList();

        for (Object[] result : entities) {
            long id = ((BigInteger) result[0]).longValue();

            // build request to update encrypted fields
            StringBuilder updateEncValuesBd = new StringBuilder("UPDATE " + tableName + "\nSET ");
            String updateEncValuesRequest = "";

            for (int i = 0; i < columnNames.length; i++) {
                String encryptedText = (String) result[i+1];

                if (encryptedText != null && (encryptedText.startsWith(EncryptionFactory.ENCRYPTION_CHECK_STRING)
                        || encryptedText.startsWith(EncryptionFactory.PREFIX))) {

                    String clearText = EncryptionFactory.decrypt(encryptedText);

                    assert clearText != null;
                    updateEncValuesBd.append(columnNames[i]).append("='")
                            .append(EncryptionFactory.encrypt(clearText)).append("',");

                    updateEncValuesRequest = updateEncValuesBd.substring(0, updateEncValuesBd.length() - 1);
                    updateEncValuesRequest += "\n" + "WHERE id = " + id;
                }
            }

            int resultStm = accountEntityService.getEntityManager().createNativeQuery(updateEncValuesRequest).executeUpdate();

            if (resultStm > 0)
                nbItemsCorrectlyProcessed++;
        }
    }

    public int getNbItemsCorrectlyProcessed(){
        return nbItemsCorrectlyProcessed;
    }
}
