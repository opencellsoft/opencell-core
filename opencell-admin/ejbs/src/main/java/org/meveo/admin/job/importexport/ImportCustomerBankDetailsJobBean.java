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
import org.meveo.model.payments.MandateChangeAction;
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
    private int nbModificationsIgnored;
    private int nbModificationsCreated;
    private String msgModifications;

    private CustomerBankDetailsImportHisto customerBankDetailsImport;

    @Inject
    private ParamBeanFactory paramBeanFactory;

    @Interceptors({ JobLoggingInterceptor.class, PerformanceInterceptor.class })
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void execute(JobExecutionResultImpl result, String parameter) {
        ParamBean paramBean = paramBeanFactory.getInstance();
        String importDir = paramBeanFactory.getChrootDir() + File.separator + "imports" + File.separator + "bank_Mobility" + File.separator;        
        initialiserCompteur();        
        String dirOK = importDir + "output";
        String dirKO = importDir + "reject";        
        List<File> files = getFilesFromInput(paramBean, importDir);
        traitementFiles(result, dirOK, dirKO, files);

        result.setNbItemsToProcess(nbModifications);
        result.setNbItemsCorrectlyProcessed(nbModificationsCreated);
        result.setNbItemsProcessedWithError(nbModificationsError);
        result.setNbItemsProcessedWithWarning(nbModificationsIgnored);
        result.setReport(msgModifications);
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
        nbModificationsIgnored = 0;
        nbModificationsCreated = 0;
        msgModifications = "";
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

        nbModifications = customerBankDetails.getMessageBanqueEmetteur().getModification().size();
        log.debug("nbModifications: {}", nbModifications);
        if (nbModifications == 0) {
            return;
        }

        paymentMethodeDepartArrivee(customerBankDetails);    
        createHistory();
        log.info("end import file ");
    }

    private void paymentMethodeDepartArrivee(Document customerBankDetails) throws CloneNotSupportedException {
        for (Modification newModification : customerBankDetails.getMessageBanqueEmetteur().getModification()) {
            //IBAN du client et BIC dans l'établissement de départ
            String ibanDepart = newModification.getOrgPartyAndAccount().getAccount().getiBAN();
            String bicDepart = newModification.getOrgPartyAndAccount().getAgent().getFinInstnId().getBicFi();
            //IBAN du client et BIC dans l'établissement d'arrivée
            String ibanArrivee = newModification.getUpdatedPartyAndAccount().getAccount().getiBAN();
            String bicArrivee = newModification.getUpdatedPartyAndAccount().getAgent().getFinInstnId().getBicFi();
            log.debug("(ibanDepart: [{}] ibanDepart: [{}] ibanDepart: [{}] ibanDepart: [{}])"
                , ibanDepart, bicDepart, ibanArrivee, bicArrivee);
            List<PaymentMethod> paymentMethods = paymentMethodService.listByIbanAndBicFi(ibanDepart, bicDepart);
            log.debug("paymentMethodsDepart.size(): {}", paymentMethods.size());
            List<PaymentMethod> paymentMethodsArrivee = paymentMethodService.listByIbanAndBicFi(ibanArrivee, bicArrivee);
            log.debug("paymentMethodsArrivee.size(): {}", paymentMethodsArrivee.size());
            dupPmDepartArrivee(ibanDepart, bicDepart, ibanArrivee, bicArrivee, paymentMethods, paymentMethodsArrivee);
        }
    }

    private void dupPmDepartArrivee(String ibanDepart, String bicDepart, String ibanArrivee, String bicArrivee, List<PaymentMethod> paymentMethods, List<PaymentMethod> paymentMethodsArrivee)
            throws CloneNotSupportedException {
        if(paymentMethodsArrivee.isEmpty()) {
            for (PaymentMethod paymentMethod : paymentMethods) {
                dupDDPaymentMethode(ibanArrivee, bicArrivee, paymentMethod);
                nbModificationsCreated++;
                log.debug("(ibanDepart: [{}] ibanDepart: [{}] ibanDepart: [{}] ibanDepart: [{}] - OK)"
                    , ibanDepart, bicDepart, ibanArrivee, bicArrivee);
            }
            if(paymentMethods.isEmpty()) {
                nbModificationsIgnored++;
                msgModifications += "[(Warning) Original bank account (iban=" + ibanDepart + "; bic=" + bicDepart + ") does not exist in opencell]  ";
                log.debug("(Warning) Original bank account iban: [{}] bic: [{}] does not exist in opencell..", ibanDepart, bicDepart);
            }
        }
        else {
            msgModifications += "[(ko) Arrival bank account (iban=" + ibanArrivee + "; bic=" + bicArrivee + ") Already exists in opencell]  ";
            nbModificationsError++;
            log.debug("(ko) Arrival bank account iban: [{}] bic: [{}] Already exists in opencell..", ibanArrivee, bicArrivee);
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

        if (newDDPaymentMethod.getBankCoordinates() != null && ((DDPaymentMethod) paymentMethod).getBankCoordinates() == null) {
            newDDPaymentMethod.setMandateChangeAction(MandateChangeAction.TO_ADVERTISE);
        } else if (newDDPaymentMethod.getBankCoordinates() != null && ((DDPaymentMethod) paymentMethod).getBankCoordinates() != null
                && !newDDPaymentMethod.getBankCoordinates().getIban().equals(((DDPaymentMethod) paymentMethod).getBankCoordinates().getIban())) {
            newDDPaymentMethod.setMandateChangeAction(MandateChangeAction.TO_ADVERTISE);
        }
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
        customerBankDetailsImport.setLinesRejected(nbModificationsIgnored);
        customerBankDetailsImport.setNbCustomerAccountsIgnored(nbModificationsIgnored);
        customerBankDetailsImport.setNbCustomerAccountsError(nbModificationsError);
        customerBankDetailsImport.setNbCustomerAccountsCreated(nbModificationsCreated);
        customerBankDetailsImport.setNbCustomerAccounts(nbModifications);
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