import static org.openqa.selenium.Keys.*;

import java.io.IOException;

import org.openqa.selenium.*;

public class TestDrivenDemo extends Demo {
    
    private final String project = createProject("busfare", true);
    
    TestDrivenDemo() throws IOException {
    }
    
    @Step(1) public void setup() {
        driver.get(project + "/src/MachineAndTests.java");
    }
    
    @Step(10) public void createTest() {
        WebElement content = driver.switchToEditorInner();
        driver.findEditorLine("MachineAndTests", 2).click();
        content.sendKeys("@Test public void testConstructor() {", ENTER);
        content.sendKeys("new Machine(System.in", ESCAPE, "System.out", ESCAPE, ");", ENTER);
        content.sendKeys(BACK_SPACE, "}");
        driver.waitForSync();
        driver.switchToPage().findElement(By.id("forcecommit")).click();
    }
    
    @Step(11) public void markNeedsImpl() {
        WebElement content = driver.switchToEditorInner();
        driver.findEditorLine("testConstructor").click();
        content.sendKeys(HOME, RIGHT, RIGHT, RIGHT, RIGHT, "@NeedsImpl ");
    }
    
    @Step(20) public void anotherTest() {
        WebElement content = driver.switchToEditorInner();
        driver.findEditorLine("@Test", 3).click();
        content.sendKeys(ENTER, "@Test public void testInput() {");
        sleep(2000);
        sendKeysSlowly(content, 200, ENTER, "//.", ESCAPE, ".", ESCAPE, ".");
        sleep(2000);
        content.sendKeys(ESCAPE, ENTER, BACK_SPACE, "}");
        driver.waitForSync();
        driver.get(project + "/src/FooTest.java");
    }
    
    @Step(30) public void markAccepted() {
        driver.findElement(By.cssSelector(".test.needsreview button")).click();
        driver.waitForSync();
    }
}
