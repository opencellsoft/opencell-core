package com.opencellsoft.testng.pages.administration;

import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.opencellsoft.testng.pages.BasePage;
import com.opencellsoft.testng.pages.Constants;

/**
 * @author HASSNAA MIFTAH
 *
 */

public class TimersPage extends BasePage {
    /**
     * new button.
     */
    @FindBy(id = "searchForm:buttonNew")
    private WebElement btnNew;
    /**
     * code.
     */
    @FindBy(id = "formId:code_txt")
    private WebElement code;
    /**
     * description.
     */
    @FindBy(id = "formId:description_txt")
    private WebElement description;
    /**
     * hour.
     */
    @FindBy(id = "formId:hour_txt")
    private WebElement hour;
    /**
     * minute.
     */
    @FindBy(id = "formId:minute_txt")
    private WebElement minute;
    /**
     * second.
     */
    @FindBy(id = "formId:second_txt")
    private WebElement second;
    /**
     * year.
     */
    @FindBy(id = "formId:year_txt")
    private WebElement year;
    /**
     * month.
     */
    @FindBy(id = "formId:month_txt")
    private WebElement month;
    /**
     * day of month.
     */
    @FindBy(id = "formId:dayOfMonth_txt")
    private WebElement dayOfMonth;
    /**
     * day of week.
     */
    @FindBy(id = "formId:dayOfWeek_txt")
    private WebElement dayOfWeek;
    /**
     * code To search.
     */
    @FindBy(id = "searchForm:code_txt")
    private WebElement codeToSearch;
    /**
     * search button.
     */
    @FindBy(id = "searchForm:buttonSearch")
    private WebElement searchBtn;
    /**
     * constructor.
     * 
     * @param driver WebDriver
     */
    public TimersPage(WebDriver driver) {
        super(driver);
    }
    /**
     * go to list page.
     * 
     * @param driver WebDriver
     */
    public void gotoListPage(WebDriver driver) {
        WebElement adminstrationMenu = driver.findElement(By.id("menu:automation"));
        moveMouseAndClick(adminstrationMenu);
        WebElement jobsMenu = driver.findElement(By.id("menu:adminJobs"));
        moveMouseAndClick(jobsMenu);
        WebElement timersMenu = driver.findElement(By.id("menu:timers"));
        moveMouseAndClick(timersMenu);
    }
    /**
     * save method .
     * 
     * @param driver WebDriver
     */
    public void gotoSave(WebDriver driver) {
        WebElement btnSave = driver.findElement(By.id("formId:formButtonsCC:saveButton"));
        moveMouseAndClick(btnSave);
    }
    /**
     * fill form with random values .
     * 
     * @param driver WebDriver
     * @param data Map
     */
    public void fillForm(WebDriver driver, Map<String, String> data) {
        moveMouseAndClick(btnNew);
        moveMouseAndClick(code);
        code.sendKeys((String) data.get(Constants.CODE));
        moveMouseAndClick(description);
        description.sendKeys((String) data.get(Constants.CODE));
        moveMouseAndClick(hour);
        hour.sendKeys((String) data.get(Constants.CODE));
        moveMouseAndClick(minute);
        minute.clear();
        minute.sendKeys("45");
        moveMouseAndClick(second);
        second.clear();
        second.sendKeys("50");
        moveMouseAndClick(year);
        year.clear();
        year.sendKeys("2018");
        moveMouseAndClick(month);
        month.clear();
        month.sendKeys("June");
        moveMouseAndClick(dayOfMonth);
        dayOfMonth.clear();
        dayOfMonth.sendKeys("25");
        moveMouseAndClick(dayOfWeek);
        dayOfWeek.clear();
        dayOfWeek.sendKeys("Monday");
    }
    /**
     * search and delete method .
     * 
     * @param driver WebDriver
     * @param data Map
     * @throws InterruptedException 
     */
    public void searchAndDelete(WebDriver driver, Map<String, String> data) throws InterruptedException {
        moveMouseAndClick(codeToSearch);
        codeToSearch.sendKeys((String) data.get(Constants.CODE));
        moveMouseAndClick(searchBtn);
        /**
         * element to delete.
         */
        WebElement elementToDelete = driver
            .findElement(By.id("datatable_results:0:code_id_message_link"));
        moveMouseAndClick(elementToDelete);
        /**
         * delete button.
         */
        WebElement delete = driver.findElement(By.id("formId:formButtonsCC:deletelink"));
        moveMouseAndClick(delete);
        WebElement confirmDelete = driver.findElements(By.className("ui-confirmdialog-yes")).get(1);
        moveMouseAndClick(confirmDelete);
    }
    /**
     * @return the btnNew
     */
    public WebElement getBtnNew() {
        return btnNew;
    }
    /**
     * @param btnNew the btnNew to set
     */
    public void setBtnNew(WebElement btnNew) {
        this.btnNew = btnNew;
    }
    /**
     * @return the code
     */
    public WebElement getCode() {
        return code;
    }
    /**
     * @param code the code to set
     */
    public void setCode(WebElement code) {
        this.code = code;
    }
    /**
     * @return the description
     */
    public WebElement getDescription() {
        return description;
    }
    /**
     * @param description the description to set
     */
    public void setDescription(WebElement description) {
        this.description = description;
    }
    /**
     * @return the hour
     */
    public WebElement getHour() {
        return hour;
    }
    /**
     * @param hour the hour to set
     */
    public void setHour(WebElement hour) {
        this.hour = hour;
    }
    /**
     * @return the minute
     */
    public WebElement getMinute() {
        return minute;
    }
    /**
     * @param minute the minute to set
     */
    public void setMinute(WebElement minute) {
        this.minute = minute;
    }
    /**
     * @return the second
     */
    public WebElement getSecond() {
        return second;
    }
    /**
     * @param second the second to set
     */
    public void setSecond(WebElement second) {
        this.second = second;
    }
    /**
     * @return the year
     */
    public WebElement getYear() {
        return year;
    }
    /**
     * @param year the year to set
     */
    public void setYear(WebElement year) {
        this.year = year;
    }
    /**
     * @return the month
     */
    public WebElement getMonth() {
        return month;
    }
    /**
     * @param month the month to set
     */
    public void setMonth(WebElement month) {
        this.month = month;
    }
    /**
     * @return the dayOfMonth
     */
    public WebElement getDayOfMonth() {
        return dayOfMonth;
    }
    /**
     * @param dayOfMonth the dayOfMonth to set
     */
    public void setDayOfMonth(WebElement dayOfMonth) {
        this.dayOfMonth = dayOfMonth;
    }
    /**
     * @return the dayOfWeek
     */
    public WebElement getDayOfWeek() {
        return dayOfWeek;
    }
    /**
     * @param dayOfWeek the dayOfWeek to set
     */
    public void setDayOfWeek(WebElement dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }
    /**
     * @return the codeToSearch
     */
    public WebElement getCodeToSearch() {
        return codeToSearch;
    }
    /**
     * @param codeToSearch the codeToSearch to set
     */
    public void setCodeToSearch(WebElement codeToSearch) {
        this.codeToSearch = codeToSearch;
    }
    /**
     * @return the searchBtn
     */
    public WebElement getSearchBtn() {
        return searchBtn;
    }
    /**
     * @param searchBtn the searchBtn to set
     */
    public void setSearchBtn(WebElement searchBtn) {
        this.searchBtn = searchBtn;
    }
}
