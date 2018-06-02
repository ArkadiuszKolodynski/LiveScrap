package pl.tsm.put.poznan.livescrap;

import com.machinepublishers.jbrowserdriver.JBrowserDriver;
import com.machinepublishers.jbrowserdriver.Settings;
import com.machinepublishers.jbrowserdriver.UserAgent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
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
        WebDriver driver = new JBrowserDriver(Settings.builder().headless(false).userAgent(UserAgent.CHROME).build());
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);

        Runnable r1 = () -> {
            getOtherDaysData(driver);
        };

        Runnable r2 = () -> {
            getTodayData(driver);
        };

        ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);
        executor.scheduleAtFixedRate(r1, 0, 1, TimeUnit.DAYS);
        executor.scheduleAtFixedRate(r2, 1, 1, TimeUnit.MINUTES);

        System.out.println("\nPress ENTER to terminate...");
        System.in.read();
        System.out.println("Waiting for termination...");

        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.MINUTES);
        executor.shutdownNow();

        driver.quit();
        System.out.println("Bye!\n");
    }

    private static void getTodayData(WebDriver driver) {
        try {
            System.out.println("\n" + LocalDateTime.now() + " | START\n");

            LocalDateTime ldt = LocalDateTime.now();
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH);

            driver.get("http://www.livescore.com/soccer/" + dtf.format(ldt));

            List<WebElement> headers;
            List<WebElement> scores;

            headers = driver.findElements(By.xpath("//div[@data-type='container']/div[contains(@class, 'row-tall')]"));
            scores = driver.findElements(By.xpath("//div[@data-type='container']/div[contains(@class, 'row-gray')]"));

            String url = "jdbc:mysql://80.211.242.134:3306/livescrap";
            String truncateHeaders = "DELETE FROM headers WHERE time=1";
            String truncateScores = "DELETE FROM scores WHERE time=1";
            String queryHeaders = "INSERT INTO headers VALUES ";
            String queryScores = "INSERT INTO scores VALUES ";
            String params = "";
            try (Connection conn = DriverManager.getConnection(url, "mysql", "Cvbn4569")) {
                Statement st = conn.createStatement();
                for (WebElement e : headers) {
                    String id = e.getAttribute("data-stg-id");
                    List<WebElement> elements = e.findElements(By.xpath(".//div[@class='clear']/div[@class='left']/a"));
                    if (elements.size() > 0) {
                        params = params + "(" + id + ", '" + elements.get(0).getText().replace("'", "''") + "', '"
                                + elements.get(1).getText().replace("'", "''") + "', " + Days.TODAY.ordinal() + "),";
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
                            + "', " + Days.TODAY.ordinal() + ", '" + elements.get(2).getText() + "'),";
                }

                params = params.substring(0, params.length() - 1);
                queryScores = queryScores + params;
                st.executeUpdate(truncateScores);
                st.executeUpdate(queryScores);

                System.out.println("\n" + LocalDateTime.now() + " | END\n");
            }
        } catch (SQLException e) {
            Logger.getLogger(WebScraper.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    private static void getOtherDaysData(WebDriver driver) {
        try {
            System.out.println("\n" + LocalDateTime.now() + " | START\n");

            for (int i = 0; i < 2; i++) {
                LocalDateTime ldt;
                if (i == 0) {
                    ldt = LocalDateTime.now().minusDays(1);
                } else {
                    ldt = LocalDateTime.now().plusDays(1);
                }
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH);

                driver.get("http://www.livescore.com/soccer/" + dtf.format(ldt));

                List<WebElement> headers;
                List<WebElement> scores;

                headers = driver.findElements(By.xpath("//div[@data-type='container']/div[contains(@class, 'row-tall')]"));
                scores = driver.findElements(By.xpath("//div[@data-type='container']/div[contains(@class, 'row-gray')]"));

                String url = "jdbc:mysql://80.211.242.134:3306/livescrap";
                String truncateHeaders = "DELETE FROM headers WHERE time=" + (i == 0 ? i : i + 1);
                String truncateScores = "DELETE FROM scores WHERE time=" + (i == 0 ? i : i + 1);
                String queryHeaders = "INSERT INTO headers VALUES ";
                String queryScores = "INSERT INTO scores VALUES ";
                String params = "";
                try (Connection conn = DriverManager.getConnection(url, "mysql", "Cvbn4569")) {
                    Statement st = conn.createStatement();
                    for (WebElement e : headers) {
                        String id = e.getAttribute("data-stg-id");
                        List<WebElement> elements = e.findElements(By.xpath(".//div[@class='clear']/div[@class='left']/a"));
                        if (elements.size() > 0) {
                            params = params + "(" + id + ", '" + elements.get(0).getText().replace("'", "''") + "', '"
                                    + elements.get(1).getText().replace("'", "''") + "', " + (i == 0 ? Days.YESTERDAY.ordinal() : Days.TOMMOROW.ordinal()) + "),";
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
                                + "', " + (i == 0 ? Days.YESTERDAY.ordinal() : Days.TOMMOROW.ordinal()) + ", '" + elements.get(2).getText() + "'),";
                    }

                    params = params.substring(0, params.length() - 1);
                    queryScores = queryScores + params;
                    st.executeUpdate(truncateScores);
                    st.executeUpdate(queryScores);
                }

                System.out.println("\n" + LocalDateTime.now() + " | END\n");
            }
        } catch (SQLException e) {
            Logger.getLogger(WebScraper.class.getName()).log(Level.SEVERE, null, e);
        }
    }
}
