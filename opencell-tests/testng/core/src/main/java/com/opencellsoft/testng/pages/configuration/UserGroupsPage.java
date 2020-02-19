package com.opencellsoft.testng.pages.configuration;

import com.opencellsoft.testng.pages.BasePage;
import com.opencellsoft.testng.pages.Constants;
import java.util.Map;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * User group page.
 * 
 * @author MIFTAH.
 *
 */

public class UserGroupsPage extends BasePage {
    /**
     * code user new.
     */
    @FindBy(id = "userGroupHierarchyDetail:code_txt")
    private WebElement codeUser;
    /**
     * description user new.
     */
    @FindBy(id = "userGroupHierarchyDetail:description_txt")
    private WebElement descriptionUser;
    /**
     * code child new.
     */
    @FindBy(id = "userGroupHierarchyDetail:code_txt")
    private WebElement codeChild;
    /**
     * description child new.
     */
    @FindBy(id = "userGroupHierarchyDetail:description_txt")
    private WebElement descriptionChild;
    /**
     * add root user button.
     */
    @FindBy(id = "addUserHierarchyRootBtn")
    private WebElement btnAddRoot;
    /**
     * button save.
     */
    @FindBy(id = "userGroupHierarchyDetail:formButtonsCC:saveButtonAjax")
    private WebElement btnSaveuser;
    /**
     * button add child.
     */
    @FindBy(id = "addUserHierarchyLevelBtn")
    private WebElement btnChildAdd;
    /**
     * delete user button.
     */
    @FindBy(id = "deleteUserHierarchyLevellink")
    private WebElement userDelete;

    /**
     * constructor.
     * 
     * @param driver WebDriver
     */
    public UserGroupsPage(final WebDriver driver) {
        super(driver);
    }

    /**
     * Opening user group page.
     * 
     * @param driver user group.
     */
    public void gotoListPage(WebDriver driver) {
        WebElement configurationMenu = driver.findElement(By.id("menu:admin"));
        moveMouse(configurationMenu);

        WebElement usergroupMenu = driver.findElement(By.id("menu:userGroupHierarchy"));
        moveMouseAndClick(usergroupMenu);

    }

    /**
     * Entering root user with data.
     * 
     * @param driver user group page.
     * @param data code user, description user.
     */
    public void fillUserRoot(WebDriver driver, Map<String, String> data) {
        moveMouseAndClick(codeUser);
        codeUser.clear();
        codeUser.sendKeys((String) data.get(Constants.CODE));
        moveMouseAndClick(descriptionUser);
        descriptionUser.clear();
        descriptionUser.sendKeys((String) data.get(Constants.CODE));
    }

    /**
     * Entering child user with data.
     * 
     * @param driver user group page.
     * @param data code user, description user.
     */
    public void fillUserChild(WebDriver driver, Map<String, String> data) {
        moveMouseAndClick(codeChild);
        codeChild.clear();
        codeChild.sendKeys((String) data.get(Constants.CODE_CHILD));
        moveMouseAndClick(descriptionChild);
        descriptionChild.clear();
        descriptionChild.sendKeys((String) data.get(Constants.DESCRIPTION));
    }

    /**
     * Clicking to add user root.
     * 
     * @param driver WebDriver
     */
    public void addUserRoot(WebDriver driver) {
        btnAddRoot.click();
    }

    /**
     * click on save.
     * 
     * @param driver WebDriver
     */
    public void btnSaveUser(WebDriver driver) {
        btnSaveuser.click();
    }

    /**
     * adding child user.
     * 
     * @param driver WebDriver
     */
    public void addChildUser(WebDriver driver) {
        btnChildAdd.click();
    }

    /**
     * Deleting data.
     * 
     * @param driver user group .
     */
    public void deleteUser(WebDriver driver) {
        userDelete.click();
        WebElement yes = driver.findElements(By.className("ui-confirmdialog-yes")).get(1);
        yes.click();
    }

    /**
     * code user setter.
     * 
     * @param codeUser setter
     */
    public void setcodeUser(WebElement codeUser) {
        this.codeUser = codeUser;
    }

    /**
     * code user getter.
     * 
     * @return code user
     */
    public WebElement getcodeUser() {
        return this.codeUser;
    }

    /**
     * description user setter.
     * 
     * @param descriptionUser setter
     */
    public void setdescriptionUser(WebElement descriptionUser) {
        this.descriptionUser = descriptionUser;
    }

    /**
     * description user getter.
     * 
     * @return description user
     */
    public WebElement getdescriptionUser() {
        return this.descriptionUser;
    }

    /**
     * description child setter.
     * 
     * @param descriptionChild setter.
     */
    public void setdescriptionChild(WebElement descriptionChild) {
        this.descriptionChild = descriptionChild;
    }

    /**
     * description child.
     * 
     * @return description child.
     */
    public WebElement getdescriptionChild() {
        return this.descriptionChild;
    }

    /**
     * code child setter.
     * 
     * @param codeChild setter.
     */
    public void setcodeChild(WebElement codeChild) {
        this.codeChild = codeChild;
    }

    /**
     * code child getter.
     * 
     * @return code child
     */
    public WebElement getcodeChild() {
        return this.codeChild;
    }

}
