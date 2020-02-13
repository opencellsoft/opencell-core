package com.opencellsoft.testng.pages.configuration;

import com.opencellsoft.testng.pages.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * 
 * @author Hassnaa MIFTAH
 */

public class AppConfigPage extends BasePage {
    /**
     * Code.
     */
    @FindBy(id = "providerFormId:tabView:code_txt")
    private WebElement codeAp;
    /**
     * Description.
     */
    @FindBy(id = "providerFormId:tabView:description")
    private WebElement descriptionAp;
    /**
     * Is enterprise.
     */
    @FindBy(id = "providerFormId:tabView:enterprise")
    private WebElement entrepriseAp;
    /**
     * Rating rounding.
     */

    @FindBy(id = "providerFormId:tabView:rounding_input")
    private WebElement ratingRounding;
    /**
     * Prepaid resrvs exp delay (ms).
     */

    @FindBy(id = "providerFormId:tabView:prepaidReservationExpirationDelayinMillisec_input")
    private WebElement prepaid;
    /**
     * Email.
     */
    @FindBy(id = "providerFormId:tabView:email")
    private WebElement email;
    /**
     * Discounts accounting code.
     */
    @FindBy(id = "providerFormId:tabView:discountAccountingCode")
    private WebElement discount;
    /**
     * Customer info duplication.
     */
    @FindBy(id = "providerFormId:tabView:levelDuplication")
    private WebElement duplication;
    /**
     * Recognize Revenue.
     */

    @FindBy(id = "providerFormId:tabView:recognizeRevenue")
    private WebElement revenue;
    /**
     * Bank information:Bic.
     */

    @FindBy(id = "providerFormId:tabView:bankCoordinates_bic_txt")
    private WebElement bic;
    /**
     * Bank information:Iban.
     */
    @FindBy(id = "providerFormId:tabView:bankCoordinates_iban_txt")
    private WebElement ibic;
    /**
     * Bank information:Ics.
     */
    @FindBy(id = "providerFormId:tabView:bankCoordinates_ics_txt")
    private WebElement ics;
    /**
     * XML Invoice configuration:Display subscriptions.
     */
    @FindBy(id = "providerFormId:tabView:displaySubscriptions")
    private WebElement disSub;
    /**
     * XML Invoice configuration:Display offers.
     */
    @FindBy(id = "providerFormId:tabView:displayOffers")
    private WebElement disOff;
    /**
     * XML Invoice configuration :Display services.
     */
    @FindBy(id = "providerFormId:tabView:displayServices")
    private WebElement disServ;
    /**
     * XML Invoice configuration:Display priceplans.
     */
    @FindBy(id = "providerFormId:tabView:displayPricePlans")
    private WebElement disPp;
    /**
     * XML Invoice configuration:Display edrs.
     */
    @FindBy(id = "providerFormId:tabView:displayEdrs")
    private WebElement disEdrs;
    /**
     * XML Invoice configuration :Display free transactions.
     */
    @FindBy(id = "providerFormId:tabView:displayFreeTx")
    private WebElement disFreeTx;
    /**
     * XML Invoice configuration:Display provider.
     */
    @FindBy(id = "providerFormId:tabView:displayProvider")
    private WebElement disProv;
    /**
     * XML Invoice configuration:Display detail.
     */
    @FindBy(id = "providerFormId:tabView:displayDetail")
    private WebElement disDet;
    /**
     * XML Invoice configuration:Display custom fields as XML.
     */
    @FindBy(id = "providerFormId:tabView:displayCfAsXML")
    private WebElement discassXml;
    /**
     * XML Invoice configuration:Display charges periods.
     */
    @FindBy(id = "providerFormId:tabView:displayChargesPeriods")
    private WebElement disChargeper;
    /**
     * XML Invoice configuration:Display billing cycle.
     */
    @FindBy(id = "providerFormId:tabView:displayBillingCycle")
    private WebElement disBillingCyc;
    /**
     * XML Invoice configuration :Display orders.
     */
    @FindBy(id = "providerFormId:tabView:displayOrders")
    private WebElement disOrders;

    /**
     * construtor.
     * 
     * @param driver WebDriver
     */
    public AppConfigPage(WebDriver driver) {
        super(driver);
    }

    /**
     * go to applicaton configuration.
     * 
     * @param driver webdriver
     */
    public void gotoListPage(WebDriver driver) {

        WebElement configurationMenu = driver.findElement(By.id("menu:admin"));
        moveMouse(configurationMenu);

        WebElement appConfigMenu = driver.findElement(By.id("menu:provider"));
        moveMouseAndClick(appConfigMenu);
    }

    /**
     * fill the form.
     * 
     * @param driver webdriver
     */
    public void fillForm(WebDriver driver) {
        fillWebElements(codeAp, descriptionAp);
        moveMouseAndClick(email);
        email.clear();
        email.sendKeys("opencell@opencell.com");
        fillWebElementNum(ratingRounding);
        fillWebElementNum(prepaid);
        fillWebElementNum(discount);
        moveMouseAndClick(duplication);
        fillWebElementNum(bic);
        fillWebElementNum(ibic);
        fillWebElementNum(ics);

        /**
         * WebElement payMethod = driver
            .findElement(By.cssSelector(".ui-picklist-source > li:nth-child(1)"));

        payMethod.click();
        */

        WebElement payMethodclick = driver.findElements(By.className("ui-picklist-button-add"))
            .get(0);
        moveMouseAndClick(payMethodclick);
        moveMouseAndClick(disSub);
        disOff.click();
        disServ.click();
        disPp.click();
        disEdrs.click();
        disFreeTx.click();
        disProv.click();
        disDet.click();
        discassXml.click();
        disChargeper.click();
        disBillingCyc.click();
        disOrders.click();
        WebElement btnSave = driver
                .findElement(By.id("providerFormId:formButtonsCC:saveButtonAjax"));
        moveMouseAndClick(btnSave);

    }

    /**
     * fillWebElements.
     * 
     * @param webElements WebElement
     */
    private void fillWebElements(WebElement... webElements) {
        for (WebElement webElement : webElements) {
            fillWebElement(webElement);
        }
    }

    /**
     * fill fields ratingRounding,prepaid,discount.
     * 
     * @param webElement WebElement
     */
    private void fillWebElement(WebElement webElement) {
        webElement.click();
        webElement.clear();
        webElement.sendKeys("123456");
    }

    /**
     * fill fields bic,ibic,ics.
     * 
     * @param webElement WebElement
     */
    private void fillWebElementNum(WebElement webElement) {
        webElement.click();
        webElement.clear();
        webElement.sendKeys("12");
    }

    /**
     * @return the codeAp
     */
    public WebElement getCodeAp() {
        return codeAp;
    }

    /**
     * @param codeAp the codeAp to set
     */
    public void setCodeAp(WebElement codeAp) {
        this.codeAp = codeAp;
    }

    /**
     * @return the descriptionAp
     */
    public WebElement getDescriptionAp() {
        return descriptionAp;
    }

    /**
     * @param descriptionAp the descriptionAp to set
     */
    public void setDescriptionAp(WebElement descriptionAp) {
        this.descriptionAp = descriptionAp;
    }

    /**
     * @return the entrepriseAp
     */
    public WebElement getEntrepriseAp() {
        return entrepriseAp;
    }

    /**
     * @param entrepriseAp the entrepriseAp to set
     */
    public void setEntrepriseAp(WebElement entrepriseAp) {
        this.entrepriseAp = entrepriseAp;
    }

    /**
     * @return the ratingRounding
     */
    public WebElement getRatingRounding() {
        return ratingRounding;
    }

    /**
     * @param ratingRounding the ratingRounding to set
     */
    public void setRatingRounding(WebElement ratingRounding) {
        this.ratingRounding = ratingRounding;
    }

    /**
     * @return the prepaid
     */
    public WebElement getPrepaid() {
        return prepaid;
    }

    /**
     * @param prepaid the prepaid to set
     */
    public void setPrepaid(WebElement prepaid) {
        this.prepaid = prepaid;
    }

    /**
     * @return the email
     */
    public WebElement getEmail() {
        return email;
    }

    /**
     * @param email the email to set
     */
    public void setEmail(WebElement email) {
        this.email = email;
    }

    /**
     * @return the discount
     */
    public WebElement getDiscount() {
        return discount;
    }

    /**
     * @param discount the discount to set
     */
    public void setDiscount(WebElement discount) {
        this.discount = discount;
    }

    /**
     * @return the duplication
     */
    public WebElement getDuplication() {
        return duplication;
    }

    /**
     * @param duplication the duplication to set
     */
    public void setDuplication(WebElement duplication) {
        this.duplication = duplication;
    }

    /**
     * @return the revenue
     */
    public WebElement getRevenue() {
        return revenue;
    }

    /**
     * @param revenue the revenue to set
     */
    public void setRevenue(WebElement revenue) {
        this.revenue = revenue;
    }

    /**
     * @return the bic
     */
    public WebElement getBic() {
        return bic;
    }

    /**
     * @param bic the bic to set
     */
    public void setBic(WebElement bic) {
        this.bic = bic;
    }

    /**
     * @return the ibic
     */
    public WebElement getIbic() {
        return ibic;
    }

    /**
     * @param ibic the ibic to set
     */
    public void setIbic(WebElement ibic) {
        this.ibic = ibic;
    }

    /**
     * @return the ics
     */
    public WebElement getIcs() {
        return ics;
    }

    /**
     * @param ics the ics to set
     */
    public void setIcs(WebElement ics) {
        this.ics = ics;
    }

    /**
     * @return the disSub
     */
    public WebElement getDisSub() {
        return disSub;
    }

    /**
     * @param disSub the disSub to set
     */
    public void setDisSub(WebElement disSub) {
        this.disSub = disSub;
    }

    /**
     * @return the disOff
     */
    public WebElement getDisOff() {
        return disOff;
    }

    /**
     * @param disOff the disOff to set
     */
    public void setDisOff(WebElement disOff) {
        this.disOff = disOff;
    }

    /**
     * @return the disServ
     */
    public WebElement getDisServ() {
        return disServ;
    }

    /**
     * @param disServ the disServ to set
     */
    public void setDisServ(WebElement disServ) {
        this.disServ = disServ;
    }

    /**
     * @return the disPp
     */
    public WebElement getDisPp() {
        return disPp;
    }

    /**
     * @param disPp the disPp to set
     */
    public void setDisPp(WebElement disPp) {
        this.disPp = disPp;
    }

    /**
     * @return the disEdrs
     */
    public WebElement getDisEdrs() {
        return disEdrs;
    }

    /**
     * @param disEdrs the disEdrs to set
     */
    public void setDisEdrs(WebElement disEdrs) {
        this.disEdrs = disEdrs;
    }

    /**
     * @return the disFreeTx
     */
    public WebElement getDisFreeTx() {
        return disFreeTx;
    }

    /**
     * @param disFreeTx the disFreeTx to set
     */
    public void setDisFreeTx(WebElement disFreeTx) {
        this.disFreeTx = disFreeTx;
    }

    /**
     * @return the disProv
     */
    public WebElement getDisProv() {
        return disProv;
    }

    /**
     * @param disProv the disProv to set
     */
    public void setDisProv(WebElement disProv) {
        this.disProv = disProv;
    }

    /**
     * @return the disDet
     */
    public WebElement getDisDet() {
        return disDet;
    }

    /**
     * @param disDet the disDet to set
     */
    public void setDisDet(WebElement disDet) {
        this.disDet = disDet;
    }

    /**
     * @return the discassXml
     */
    public WebElement getDiscassXml() {
        return discassXml;
    }

    /**
     * @param discassXml the discassXml to set
     */
    public void setDiscassXml(WebElement discassXml) {
        this.discassXml = discassXml;
    }

    /**
     * @return the disChargeper
     */
    public WebElement getDisChargeper() {
        return disChargeper;
    }

    /**
     * @param disChargeper the disChargeper to set
     */
    public void setDisChargeper(WebElement disChargeper) {
        this.disChargeper = disChargeper;
    }

    /**
     * @return the disBillingCyc
     */
    public WebElement getDisBillingCyc() {
        return disBillingCyc;
    }

    /**
     * @param disBillingCyc the disBillingCyc to set
     */
    public void setDisBillingCyc(WebElement disBillingCyc) {
        this.disBillingCyc = disBillingCyc;
    }

    /**
     * @return the disOrders
     */
    public WebElement getDisOrders() {
        return disOrders;
    }

    /**
     * @param disOrders the disOrders to set
     */
    public void setDisOrders(WebElement disOrders) {
        this.disOrders = disOrders;
    }
}
