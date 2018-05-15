package pl.tsm.put.poznan.livescrap;

import com.machinepublishers.jbrowserdriver.JBrowserDriver;
import com.machinepublishers.jbrowserdriver.Settings;
import com.machinepublishers.jbrowserdriver.UserAgent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class WebScraper {

    public static void main(String args[]) throws Exception {
        WebDriver driver = new JBrowserDriver(Settings.builder().userAgent(UserAgent.CHROME).build());
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
        List<WebElement> headers = new ArrayList<>();
        List<WebElement> scores = new ArrayList<>();
        
        driver.get("http://www.livescore.com");
        
        Runnable r = () -> {
            getData(driver, headers, scores);
        };
        
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(r, 0, 1, TimeUnit.MINUTES);

        System.out.println("\nPress ENTER to terminate...");
        System.in.read();
        System.out.println("Waiting for termination...");
        
        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.MINUTES);
        
        driver.quit();
        System.out.println("Bye!\n");
    }

    private static void getData(WebDriver driver, List<WebElement> headers, List<WebElement> scores) {
        try {
            System.out.println("\n" + LocalDateTime.now() + " | START\n");
            
            headers = driver.findElements(By.xpath("//div[@data-type='container']/div[contains(@class, 'row-tall')]"));
            scores = driver.findElements(By.xpath("//div[@data-type='container']/div[contains(@class, 'row-gray')]"));
            
            String url = "jdbc:mysql://localhost/livescrap";
            String truncateHeaders = "TRUNCATE TABLE headers";
            String truncateScores = "TRUNCATE TABLE scores";
            String queryHeaders = "INSERT INTO headers VALUES ";
            String queryScores = "INSERT INTO scores VALUES ";
            String params = "";
            try (Connection conn = DriverManager.getConnection(url, "mysql", "")) {
                Statement st = conn.createStatement();
                for (WebElement e : headers) {
                    String id = e.getAttribute("data-stg-id");
                    List<WebElement> elements = e.findElements(By.xpath(".//div[@class='clear']/div[@class='left']/a"));
                    if (elements.size() > 0) {
                        params = params + "(" + id + ", '" + elements.get(0).getText().replace("'", "''") + "', '"
                                + elements.get(1).getText().replace("'", "''") + "'),";
                    }
                }
                
                params = params.substring(0, params.length() - 1);
                queryHeaders = queryHeaders + params;
                st.executeUpdate(truncateHeaders);
                st.executeUpdate(queryHeaders);
                
                params = "";
                
                for (WebElement e : scores) {
                    String id = e.getAttribute("data-stg-id");
                    List<WebElement> elements = e.findElements(By.xpath(".//div"));
                    params = params + "(" + id + ", '" + elements.get(0).getText().replace("'", "''") + "', '"
                            + elements.get(1).getText().replace("'", "''") + "', '" + elements.get(3).getText().replace("'", "''")
                            + "', '" + elements.get(2).getText() + "'),";
                }
                
                params = params.substring(0, params.length() - 1);
                queryScores = queryScores + params;
                st.executeUpdate(truncateScores);
                st.executeUpdate(queryScores);
                
                driver.navigate().refresh();
                
                System.out.println("\n" + LocalDateTime.now() + " | END\n");
            }
        } catch (SQLException e) {
            Logger.getLogger(WebScraper.class.getName()).log(Level.SEVERE, null, e);
        }
    }
}
