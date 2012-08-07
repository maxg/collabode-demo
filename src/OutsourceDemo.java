import static org.openqa.selenium.Keys.*;

import java.io.IOException;

import org.openqa.selenium.*;

public class OutsourceDemo extends Demo {
    
    private final WebDriver actualDriver = Demo.driver.driver;
    private final String instawork;
    
    private final String project = createProject("bigimg", false);
    
    private String taskURL;
    
    OutsourceDemo(String instawork) throws IOException {
        this.instawork = instawork;
    }
    
    @Step(1) public void setup() {
        actualDriver.get("http://" + instawork);
        actualDriver.findElement(By.partialLinkText("sign in")).click();
        sleep(1000);
        actualDriver.findElement(By.id("submit-login")).click();
    }
    
    @Step(10) public void pickUpTask() {
        actualDriver.navigate().refresh();
        actualDriver.findElement(By.partialLinkText("Quick collaborative programming")).click();
        actualDriver.findElement(By.cssSelector("form button")).click();
        actualDriver.findElement(By.id("start")).click();
        taskURL = actualDriver.getCurrentUrl();
        driver.get(project + "/src/BigImg.java");
    }
    
    @Step(20) public void workOnTask() {
        WebElement content = driver.switchToEditorInner();
        driver.findEditorLine("ArrayList", 1).click();
        content.sendKeys("Scanner scan = new Scanner(page.openStream", ESCAPE, "());");
        driver.findEditorLine("findImages").click();
        content.sendKeys(BACK_SPACE, "throws IOException {");
        driver.switchToPage();
        driver.findElement(By.id("orgimports")).click();
    }
    
    @Step(21) public void sendChat() {
        driver.switchToPage();
        driver.findElement(By.partialLinkText("Chat with")).click();
        for (WebElement chat : driver.findElements(By.className("chattext"))) {
            if (chat.isDisplayed()) {
                chat.sendKeys("What kinds of images do you want to find?", ENTER);
                break;
            }
        }
    }
    
    @Step(30) public void completeTask() {
        WebElement content = driver.switchToEditorInner();
        driver.findEditorLine("Scanner").click();
        content.sendKeys(END, ENTER, "// ...", ENTER, "// ...");
        actualDriver.get(taskURL);
        actualDriver.findElement(By.id("start")).click();
        actualDriver.findElement(By.id("done")).click();
    }
}
