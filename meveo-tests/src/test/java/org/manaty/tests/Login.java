package org.manaty.tests;

import org.junit.Test;
import org.openqa.selenium.By;

public class Login extends BaseSeleniumTestCase {

	public Login() {
		super();
	}

  @Test
  public void testLoginWebdriver() throws Exception {
    driver.get(baseUrl + url);
    driver.findElement(By.id("loginForm:username")).clear();
    driver.findElement(By.id("loginForm:username")).sendKeys(username);
    driver.findElement(By.id("loginForm:password")).clear();
    driver.findElement(By.id("loginForm:password")).sendKeys(password);
    driver.findElement(By.id("loginForm:submit")).click();
  }

}
