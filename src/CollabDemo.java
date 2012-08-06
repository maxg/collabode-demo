import static org.openqa.selenium.Keys.*;

import java.io.IOException;

import org.openqa.selenium.WebElement;

public class CollabDemo extends Demo {
    
    private final String project = createProject("myproject", false);
    
    CollabDemo() throws IOException {
    }
    
    @Step(1) public void setup() {
        driver.login("robot");
        driver.get(project + "/src/HelloWorld.java");
    }
    
    @Step(10) public void startAddingCow() {
        WebElement content = driver.switchToEditorInner();
        driver.findEditorLine("moon", 1).click();
        sendKeysSlowly(content, 200, ENTER, "things.add", ESCAPE, "(\"cow jumping over the");
    }
    
    @Step(11) public void addGreet() {
        WebElement content = driver.switchToEditorInner();
        driver.findEditorLine("HelloWorld", 1).click();
        sendKeysSlowly(content, 50, "public static void greet(String thing) {", ENTER);
        sendKeysSlowly(content, 50, "System.out.println(\"Hello, \" + thing + \"!\");", ENTER);
        sendKeysSlowly(content, 50, BACK_SPACE, "}", ENTER);
    }
    
    @Step(12) public void finishAddingCow() {
        WebElement content = driver.switchToEditorInner();
        driver.findEditorLine("cow").click();
        sendKeysSlowly(content, 200, " moon\");");
    }
    
    @Step(100) public void holdForApplause() {
    }
}
