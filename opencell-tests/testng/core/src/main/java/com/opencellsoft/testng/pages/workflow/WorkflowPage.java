package com.opencellsoft.testng.pages.workflow;
import java.util.Map;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.opencellsoft.testng.pages.BasePage;
import com.opencellsoft.testng.pages.Constants;

public class WorkflowPage extends BasePage {

    /**
     * new code label.
     */
    @FindBy(id = "counterTemplatId:code_txt")
    private WebElement codeCounters;
    public WorkflowPage(WebDriver driver) {
        super(driver);
        // TODO Auto-generated constructor stub
    }
    public void gotoListPage(WebDriver driver) {
        WebElement workflowsMenu = driver.findElement(By.id("menu:workflows"));
        moveMouse(workflowsMenu);
        WebElement genericWorkflowsMenu = driver.findElement(By.id("menu:genericWorkflows"));
        moveMouseAndClick(genericWorkflowsMenu);
        
    }
    /**
     * entering data.
     * 
     * @param driver WebDriver
     * @param data code, description, choosing 
     */
    public void fillData(WebDriver driver, Map<String, String> data) {
        
      WebElement btnNew = driver.findElement(By.id("searchForm:buttonNew"));
      moveMouseAndClick(btnNew);
      WebElement genericWFCode = driver.findElement(By.id("genericWFFormId:tabView:code_txt"));
      moveMouseAndClick(genericWFCode);
      genericWFCode.clear();
      genericWFCode.sendKeys((String) data.get(Constants.CODE));
      WebElement genericWFDesc = driver.findElement(By.id("genericWFFormId:tabView:description_txt"));
      moveMouseAndClick(genericWFDesc);
      genericWFDesc.clear();
      genericWFDesc.sendKeys((String) data.get(Constants.DESCRIPTION));
      WebElement targetEntityClassList = driver.findElement(By.id("genericWFFormId:tabView:targetEntityClass_txt_input"));
      moveMouseAndClick(targetEntityClassList);
      targetEntityClassList.clear();
      targetEntityClassList.sendKeys("org.meveo.model.payments.CustomerAccount");
      WebElement targetEntityClassItem = driver.findElement(By.xpath("/html/body/div[10]/ul/li/span"));
      moveMouseAndClick(targetEntityClassItem);
      WebElement addNewBtn = driver.findElements(By.className("ui-button-text-only")).get(0);
      moveMouseAndClick(addNewBtn);
      WebElement codeItem1 = driver.findElement(By.id("genericWFFormId:tabView:stCode"));
      moveMouseAndClick(codeItem1);
      codeItem1.sendKeys("Item_1"+(String) data.get(Constants.CODE));
      WebElement saveItem1 = driver.findElements(By.className("ui-button-text-only")).get(1);
      moveMouseAndClick(saveItem1);
      WebElement addNewBtn1 = driver.findElements(By.className("ui-button-text-only")).get(0);
      moveMouseAndClick(addNewBtn1);
      WebElement codeItem2 = driver.findElement(By.id("genericWFFormId:tabView:stCode"));
      moveMouseAndClick(codeItem2);
      codeItem2.sendKeys("Item_2"+(String) data.get(Constants.CODE));
      WebElement saveItem2 = driver.findElements(By.className("ui-button-text-only")).get(1);
      moveMouseAndClick(saveItem2);
      WebElement saveWF = driver.findElement(By.id("genericWFFormId:formButtonsCC:saveButton"));
      moveMouseAndClick(saveWF);
      WebElement transitionsTab = driver.findElements(By.className("ui-state-default")).get(1);
      moveMouseAndClick(transitionsTab);
      WebElement addNewTransition = driver.findElement(By.id("genericWFFormId:tabView:addTransition"));
      moveMouseAndClick(addNewTransition);
      WebElement fromStatusList1 = driver.findElements(By.className("ui-selectonemenu-trigger")).get(1);
      moveMouseAndClick(fromStatusList1);
      WebElement fromStatusElement1 = driver.findElement(By.id("genericWFFormId:tabView:formDunningPlanTransition:fromStatus_txt_1"));
      moveMouseAndClick(fromStatusElement1);
      WebElement fromStatusList2 = driver.findElements(By.className("ui-selectonemenu-trigger")).get(2);
      moveMouseAndClick(fromStatusList2);
      WebElement fromStatusElement2 = driver.findElement(By.id("genericWFFormId:tabView:formDunningPlanTransition:toStatus_txt_2"));
      moveMouseAndClick(fromStatusElement2);
      WebElement conditionEL = driver.findElement(By.id("genericWFFormId:tabView:formDunningPlanTransition:conditionEl_txt"));
      moveMouseAndClick(conditionEL);
      conditionEL.sendKeys("#{true}");
      WebElement description = driver.findElement(By.id("genericWFFormId:tabView:formDunningPlanTransition:description_txt"));
      moveMouseAndClick(description);
      description.sendKeys("#{true}");
      WebElement saveTransition = driver.findElements(By.className("ui-button-text-only")).get(1);
      moveMouseAndClick(saveTransition);
      WebElement saveAll = driver.findElement(By.id("genericWFFormId:formButtonsCC:saveButton"));
      moveMouseAndClick(saveAll);
      WebElement backBtn = driver.findElement(By.id("genericWFFormId:formButtonsCC:backButton"));
      moveMouseAndClick(backBtn);  
      WebElement searchCode = driver.findElement(By.id("searchForm:code_txt"));
      moveMouseAndClick(searchCode); 
      searchCode.clear();
      searchCode.sendKeys((String) data.get(Constants.CODE));
      WebElement lineToDelete = driver.findElement(By.id("datatable_results:0:code_id_message_link"));
      moveMouseAndClick(lineToDelete); 
      WebElement deleteBtn = driver.findElement(By.id("genericWFFormId:formButtonsCC:deletelink"));
      moveMouseAndClick(deleteBtn); 
      WebElement yes = driver.findElements(By.className("ui-confirmdialog-yes")).get(1);
      yes.click();
    }
}
