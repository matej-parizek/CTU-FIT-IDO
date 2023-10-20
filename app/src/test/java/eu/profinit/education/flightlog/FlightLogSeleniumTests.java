package eu.profinit.education.flightlog;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import eu.profinit.education.flightlog.configuration.WebDriverConfiguration;
import lombok.extern.slf4j.Slf4j;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, classes = WebDriverConfiguration.class)
@Slf4j
@ActiveProfiles("inttest")
public class FlightLogSeleniumTests {

    @Autowired
    public WebDriver webDriver;

    @Value("${application.url}")
    private String baseUrl;

    @Before
    public void setUp() {
        webDriver.get(baseUrl);
    }

    @After
    public void tearDown() {
        webDriver.quit();
    }

    //    @Ignore("Test is not implemented")
    @Test
    public void testAddNewFlight() throws Exception {
        // wait till the application is fully loaded
        new WebDriverWait(webDriver, 5).until(ExpectedConditions.numberOfElementsToBe(By.xpath("//tbody//tr"), 1));
        // get the number of flights already registered, there should already be one
        int numberOfRegisteredFlightsBefore = webDriver.findElements(By.xpath("//tbody//tr")).size();
        assertEquals("There is a one flight at the start of the application", 1, numberOfRegisteredFlightsBefore);

        // open new flight tab
        webDriver.findElement(By.xpath("/html/body/div/div/ul/li[2]/a")).click();

        // fill in information about a flight and confirm it
        webDriver.findElement(By.xpath("//*[@id=\"takeoffTime\"]")).sendKeys("27100020221830");
        webDriver.findElement(By.xpath("/html/body/router-view/div/form/compose[2]/compose/div[1]/div/div/label[3]")).click();

        // let's wait till the pilots will be loaded into the form
        new WebDriverWait(webDriver, 30)
            .until(ExpectedConditions.presenceOfNestedElementsLocatedBy(
                By.xpath("/html/body/router-view/div/form/compose[1]/div[2]/compose/div[3]/div/select"),
                By.tagName("option")));
        webDriver.findElement(By.xpath("/html/body/router-view/div/form/div[2]/div/button")).click();

        // let's wait till the data will be processed and saved
        new WebDriverWait(webDriver, 60).until(ExpectedConditions.alertIsPresent());
        webDriver.switchTo().alert().accept();

        // move to a list of all flights
        webDriver.findElement(By.xpath("/html/body/div/div/ul/li[1]/a")).click();
        // wait till records will appear on the page
        new WebDriverWait(webDriver, 5)
            .until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//tbody//tr")));

        // get a list of created flights
        int numberOfRegisteredFlightsAfter = webDriver.findElements(By.xpath("//tbody//tr")).size();

        // perform necessarry checks
        assertEquals("There should be only one new flight created.", 1,
            numberOfRegisteredFlightsAfter - numberOfRegisteredFlightsBefore);
    }
}
