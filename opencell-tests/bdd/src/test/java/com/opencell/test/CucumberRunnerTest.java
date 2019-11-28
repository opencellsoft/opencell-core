package com.opencell.test;

import com.opencell.test.utils.CucumberTest;
import cucumber.api.CucumberOptions;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import cucumber.api.junit.Cucumber;

@RunWith(Cucumber.class)
@CucumberOptions(plugin = {"pretty","json:target/cucumber-reports/Cucumber.json",
        "junit:target/cucumber-reports/Cucumber.xml"}, tags = "@adminUrl")
public class CucumberRunnerTest {

}
