import static org.openqa.selenium.Keys.*;

import java.net.MalformedURLException;

import org.openqa.selenium.*;

public class GoogleDocsDemo extends Demo {
    
    private WebDriver driver = super.driver.driver;
    
    GoogleDocsDemo() throws MalformedURLException {
    }
    
    @Step(1) public void setup() {
        driver.get("https://docs.google.com/document/d/1Y9tT9wPaF4b-zZ8X2O61RLyvNYrNBS3gmTmfhuan6TM/edit");
    }
    
    @Step(10) public void type() {
        driver.findElement(By.xpath("//span[contains(.,'Goldman')]")).click();
        
        WebElement target = driver.findElement(By.className("docs-texteventtarget-iframe"));
        sendKeysSlowly(target, 10, END, " and Automatic Writing Robot Mark III", ENTER, ENTER);
        sleep(2000);
        sendKeysSlowly(target, 10, "Abstract");
        target.sendKeys(chord(SHIFT, HOME), chord(CONTROL, ALT, "2"));
        sleep(1000);
        sendKeysSlowly(target, 10, END, ENTER, "This paper descibes Collabode");
        sleep(1000);
        sendKeysSlowly(target, 10, END, ", a web-based Java IDE for close collaboration between programmers.");
    }
    
    @Step(100) public void holdForApplause() {
    }
}
