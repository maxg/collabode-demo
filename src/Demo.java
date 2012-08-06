import java.io.*;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

public class Demo {
    
    public static void main(String[] args) throws Exception {
        new GoogleDocsDemo().run();
        new CollabDemo().run();
        new TestDrivenDemo().run();
        driver.quit();
    }
    
    private static final Properties config = new Properties();
    private static final String selenium;
    private static final String collabode;
    private static final DefaultHttpClient admin = new DefaultHttpClient();
    private static final StatusHandler handler = new StatusHandler();
    
    protected static final CollabodeDriver driver;
    
    static {
        try {
            config.load(new FileInputStream("config/demo.properties"));
            selenium = "http://" + config.get("selenium") + "/wd/hub";
            collabode = "http://" + config.get("collabode") + "/";
            adminPost("login/",
                      new BasicNameValuePair("username", config.getProperty("username")),
                      new BasicNameValuePair("password", config.getProperty("password")),
                      new BasicNameValuePair("rusername", null));
            
            WebDriver web;
            if (config.containsKey("selenium")) {
                web = new RemoteWebDriver(new URL(selenium), DesiredCapabilities.firefox());
            } else {
                web = new FirefoxDriver();
            }
            driver = new CollabodeDriver(collabode, web);
            driver.login("robot");
        } catch (IOException ioe) { throw new Error(ioe); }
    }
    
    void run() throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, IOException, DemoException {
        NavigableMap<Integer, Method> steps = new TreeMap<Integer, Method>();
        for (Method m : getClass().getDeclaredMethods()) {
            if (m.isAnnotationPresent(Step.class)) {
                steps.put(m.getAnnotation(Step.class).value(), m);
            }
        }
        
        System.out.println("Running " + getClass().getSimpleName() + " (" + steps.size() + ")");
        
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        for (ListIterator<Integer> it = new ArrayList<Integer>(steps.keySet()).listIterator(); it.hasNext(); ) {
            Method m = steps.get(it.next());
            System.out.print(" " + getClass().getSimpleName() + "." + m.getName() + " >> ");
            String line = in.readLine();
            try {
                Command.valueOf(line).exec(this);
            } catch (SkipException se) {
                break;
            } catch (IllegalArgumentException iae) {
                m.invoke(this);
            }
        }
        
        System.out.println("Done");
    }
    
    void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ie) { }
    }
    
    void sendKeysSlowly(WebElement content, long delay, CharSequence... keysToSend) {
        for (CharSequence seq : keysToSend) {
            for (int ii = 0; ii < seq.length(); ii++) {
                sleep(delay);
                content.sendKeys(Character.toString(seq.charAt(ii)));
            }
        }
    }
    
    String createProject(String project, boolean testDriven) throws IOException {
        String fixture = project + "-demo-" + (int)(System.currentTimeMillis() / 1000 % 100000);
        
        adminPost("",
                  new BasicNameValuePair("projectname", fixture),
                  new BasicNameValuePair("projecttype", testDriven ? "javatdproject" : "javaproject"));
        adminPost(fixture,
                  new BasicNameValuePair("acl", "1"),
                  new BasicNameValuePair("acl_userid", "anyone"),
                  new BasicNameValuePair("acl_permission", "owner"));
        
        File directory = new File("projects/" + project);
        for (File subdir : directory.listFiles()) {
            for (File file : subdir.listFiles()) {
                String content = new Scanner(file).useDelimiter("\\A").next();
                adminPost(fixture + "/" + subdir.getName(),
                          new BasicNameValuePair("filename", file.getName()),
                          new BasicNameValuePair("content", content));
            }
        }
        
        return fixture;
    }
    
    /**
     * Performs a HTTP POST on the admin interface.
     */
    static void adminPost(String path, NameValuePair... parameters) throws IOException {
        System.out.println(path + " -> " + admin.execute(new HttpEntityPost(collabode + path, parameters), handler));
    }
}

@Retention(RetentionPolicy.RUNTIME)
@interface Step {
    int value();
}

class HttpEntityPost extends HttpPost {
    public HttpEntityPost(String url, NameValuePair... parameters) throws UnsupportedEncodingException {
        super(url);
        setEntity(new UrlEncodedFormEntity(Arrays.asList(parameters)));
    }
}

class StatusHandler implements ResponseHandler<Integer> {
    public Integer handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
        EntityUtils.consume(response.getEntity());
        return response.getStatusLine().getStatusCode();
    }
}

enum Command {
    skip {
        public void exec(Demo self) throws DemoException {
            throw new SkipException();
        }
    },
    quit {
        public void exec(Demo self) throws DemoException {
            Demo.driver.quit();
            throw new QuitException();
        }
    };
    public abstract void exec(Demo self) throws DemoException;
}
class DemoException extends Exception { private static final long serialVersionUID = 1L; }
class SkipException extends DemoException { private static final long serialVersionUID = 1L; }
class QuitException extends DemoException { private static final long serialVersionUID = 1L; }
