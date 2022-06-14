package org.meveo.admin.job.importexport;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.xml.bind.JAXBException;

import org.meveo.admin.job.logging.JobLoggingInterceptor;
import org.meveo.commons.utils.FileUtils;
import org.meveo.commons.utils.ImportFileFiltre;
import org.meveo.commons.utils.JAXBUtils;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.ParamBeanFactory;
import org.meveo.interceptor.PerformanceInterceptor;
import org.meveo.model.admin.CustomerBankDetailsImportHisto;
import org.meveo.model.crm.Provider;
import org.meveo.model.jaxb.customer.bankdetails.Document;
import org.meveo.model.jaxb.customer.bankdetails.Modification;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.model.payments.DDPaymentMethod;
import org.meveo.model.payments.PaymentMethod;
import org.meveo.service.admin.impl.CustomerBankDetailsImportHistoService;
import org.meveo.service.job.JobExecutionService;
import org.meveo.service.payments.impl.PaymentMethodService;
import org.meveo.util.ApplicationProvider;
import org.slf4j.Logger;

@Stateless
public class ImportCustomerBankDetailsJobBean {

    @Inject
    private Logger log;

    @Inject
    private CustomerBankDetailsImportHistoService customerBankDetailsImportHistoService;

    @Inject
    private PaymentMethodService paymentMethodService;
    
    @Inject
    @ApplicationProvider
    protected Provider appProvider;

    @Inject
    private JobExecutionService jobExecutionService;
    
    private int nbModifications;
    private int nbModificationsError;
    private int nbModificationsTerminated;
    private int nbModificationsIgnored;
    private int nbModificationsCreated;

    private CustomerBankDetailsImportHisto customerBankDetailsImport;

    @Inject
    private ParamBeanFactory paramBeanFactory;

    @Interceptors({ JobLoggingInterceptor.class, PerformanceInterceptor.class })
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void execute(JobExecutionResultImpl result) {
        ParamBean paramBean = paramBeanFactory.getInstance();
        String importDir = paramBeanFactory.getChrootDir() + File.separator + "imports" + File.separator + "bank_Mobility" + File.separator;        
        initialiserCompteur();        
        String dirOK = importDir + "output";
        String dirKO = importDir + "reject";        
        List<File> files = getFilesFromInput(paramBean, importDir);
        traitementFiles(result, dirOK, dirKO, files);

        result.setNbItemsToProcess(nbModifications);
        result.setNbItemsCorrectlyProcessed((long)nbModificationsCreated + nbModificationsTerminated + nbModificationsIgnored);
        result.setNbItemsProcessedWithError(nbModificationsError);
    }

    private List<File> getFilesFromInput(ParamBean paramBean, String importDir) {
        String dirIN = importDir + "input";
        String prefix = paramBean.getProperty("importCustomerBankDetails.prefix", "acmt");
        String ext = paramBean.getProperty("importCustomerBankDetails.extension", "");
        File dir = new File(dirIN);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        List<File> files = getFilesToProcess(dir, prefix, ext);
        int numberOfFiles = files.size();
        log.info("InputFiles job to import={}", numberOfFiles);
        return files;
    }

    private void traitementFiles(JobExecutionResultImpl result, String dirOK, String dirKO, List<File> files) {
        for (File file : files) {
            if (!jobExecutionService.isShouldJobContinue(result.getJobInstance().getId())) {
                break;
            }
            File currentFile = null;
            try {
                log.info("InputFiles job {} in progress...", file.getName());
                currentFile = FileUtils.addExtension(file, ".processing");

                importFile(currentFile, file.getName(), result.getJobInstance());
                FileUtils.moveFile(dirOK, currentFile, file.getName());
                log.info("InputFiles job {} done.", file.getName());
            } catch (Exception e) {
                log.error("failed to import Customer Bank Details", e);
                FileUtils.moveFile(dirKO, currentFile, file.getName());
            } finally {
                if (currentFile != null)
                {
                    currentFile.delete();
                }
            }
        }
    }

    private void initialiserCompteur() {
        nbModifications = 0;
        nbModificationsError = 0;
        nbModificationsTerminated = 0;
        nbModificationsIgnored = 0;
        nbModificationsCreated = 0;
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    private void importFile(File file, String fileName, JobInstance jobInstance) throws JAXBException, CloneNotSupportedException {
        createCustomerBankDetailsImport(fileName);

        if (file.length() < 100) {
            createHistory();
            return;
        }

        Document customerBankDetails = (Document) JAXBUtils.unmarshaller(Document.class, file);        
        log.debug("parsing file ok");

        int i = -1;
        nbModifications = customerBankDetails.getMessageBanqueEmetteur().getModification().size();
        if (nbModifications == 0) {
            return;
        }

        int checkJobStatusEveryNr = jobInstance.getJobSpeed().getCheckNb();
        paymentMethodeDepartArrivee(jobInstance, customerBankDetails, i, checkJobStatusEveryNr);    
        
        createHistory();
        log.info("end import file ");
    }

    private void paymentMethodeDepartArrivee(JobInstance jobInstance, Document customerBankDetails, int i, int checkJobStatusEveryNr) throws CloneNotSupportedException {
        for (Modification newModification : customerBankDetails.getMessageBanqueEmetteur().getModification()) {
            if (i % checkJobStatusEveryNr == 0 && !jobExecutionService.isShouldJobContinue(jobInstance.getId())) {
                break;
            }
            //IBAN du client et BIC dans l'établissement de départ
            String ibanDepart = newModification.getOrgPartyAndAccount().getAccount().getiBAN();
            String bicDepart = newModification.getOrgPartyAndAccount().getAgent().getFinInstnId().getBicFi();
            //IBAN du client et BIC dans l'établissement d'arrivée
            String ibanArrivee = newModification.getUpdatedPartyAndAccount().getAccount().getiBAN();
            String bicArrivee = newModification.getUpdatedPartyAndAccount().getAgent().getFinInstnId().getBicFi();
            
            List<PaymentMethod> paymentMethods = paymentMethodService.listByIbanAndBicFi(ibanDepart, bicDepart);
            List<PaymentMethod> paymentMethodsArrivee = paymentMethodService.listByIbanAndBicFi(ibanArrivee, bicArrivee);
            if (paymentMethods != null && paymentMethodsArrivee != null) {
                dupPmDepartArrivee(ibanArrivee, bicArrivee, paymentMethods, paymentMethodsArrivee);
            }
        }
    }

    private void dupPmDepartArrivee(String ibanArrivee, String bicArrivee, List<PaymentMethod> paymentMethods, List<PaymentMethod> paymentMethodsArrivee)
            throws CloneNotSupportedException {
        if(paymentMethodsArrivee.isEmpty()) {
            for (PaymentMethod paymentMethod : paymentMethods) {
                dupDDPaymentMethode(ibanArrivee, bicArrivee, paymentMethod);
                nbModificationsCreated++;
            }
        }
        else {
            nbModificationsError++;
        }
        
        if(paymentMethods.isEmpty()) {
            nbModificationsIgnored++;
        }
    }

    private void createCustomerBankDetailsImport(String fileName) {
        customerBankDetailsImport = new CustomerBankDetailsImportHisto();
        customerBankDetailsImport.setExecutionDate(new Date());
        customerBankDetailsImport.setFileName(fileName);
    }

    private void dupDDPaymentMethode(String ibanArrivee, String bicArrivee, PaymentMethod paymentMethod) throws CloneNotSupportedException {
        DDPaymentMethod dDPaymentMethod = (DDPaymentMethod) paymentMethod;
        DDPaymentMethod newDDPaymentMethod = dDPaymentMethod.copieDDPaymentMethod();              
        newDDPaymentMethod.getBankCoordinates().setIban(ibanArrivee);
        newDDPaymentMethod.getBankCoordinates().setBic(bicArrivee);        
        paymentMethodService.create(newDDPaymentMethod);
        
        paymentMethod.setPreferred(false);            
        paymentMethod.setDisabled(true);
        paymentMethodService.update(paymentMethod);
    }

    /**
     * @throws Exception exception
     */
    private void createHistory() {
        customerBankDetailsImport.setLinesRead(nbModifications);
        customerBankDetailsImport.setLinesInserted(nbModificationsCreated);
        customerBankDetailsImport.setLinesRejected(nbModificationsError);
        customerBankDetailsImport.setNbCustomerAccountsIgnored(nbModificationsIgnored);
        customerBankDetailsImport.setNbCustomerAccountsError(nbModificationsError);
        customerBankDetailsImport.setNbCustomerAccountsCreated(nbModificationsTerminated);
        customerBankDetailsImportHistoService.create(customerBankDetailsImport);
    }

    /**
     * @param dir folder
     * @param prefix prefix file
     * @param ext extension file
     * @return list of file to proceed
     */
    private List<File> getFilesToProcess(File dir, String prefix, String ext) {
        List<File> files = new ArrayList<File>();
        ImportFileFiltre filtre = new ImportFileFiltre(prefix, ext);
        File[] listFile = dir.listFiles(filtre);

        if (listFile == null) {
            return files;
        }

        for (File file : listFile) {
            if (file.isFile()) {
                files.add(file);
                // we just process one file
                return files;
            }
        }
        return files;
    }
}