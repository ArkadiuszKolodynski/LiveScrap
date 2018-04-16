package pl.tsm.put.poznan.livescrap;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

public class WebScraper {

    public static void main(String args[]) throws Exception {
        FirefoxOptions options = new FirefoxOptions();
        WebDriver driver = new FirefoxDriver(options);
        List<WebElement> headers;
        try {
            driver.get("http://www.livescore.com");
            headers = driver.findElements(By.xpath("//div[@data-type='container']/div[contains(@class, 'row-tall')]"));
            List<WebElement> scores = driver.findElements(By.xpath("//div[@data-type='container']/div[contains(@class, 'row-gray')]"));
            System.out.println();

            try {
                String url = "jdbc:mysql://80.211.242.134:3306/livescrap";
                String sql = "TRUNCATE TABLE headers";
                String sql1 = "TRUNCATE TABLE scores";
                String query = "INSERT INTO headers VALUES ";
                String query1 = "INSERT INTO scores VALUES ";
                String param = "";
                String param1 = "";
                try (Connection conn = DriverManager.getConnection(url, "mysql", "Cvbn4569")) {
                    Statement st = conn.createStatement();
                    for (WebElement e : headers) {
                        String id = e.getAttribute("data-stg-id");
                        List<WebElement> elements = e.findElements(By.xpath(".//div[@class='clear']/div[@class='left']/a"));
                        param = param + "(" + id + ", '" + elements.get(0).getText().replace("'", "''") + "', '" +
                                elements.get(1).getText().replace("'", "''") + "'),";
                    }
                    for (WebElement e : scores) {
                        String id = e.getAttribute("data-stg-id");
                        List<WebElement> elements = e.findElements(By.xpath(".//div"));
                        param1 = param1 + "(" + id + ", '" + elements.get(0).getText().replace("'", "''") + "', '" +
                                elements.get(1).getText().replace("'", "''") + "', '" + elements.get(3).getText().replace("'", "''") +
                                "', '" + elements.get(2).getText() + "'),";
                    }
                    param = param.substring(0, param.length() - 1);
                    query = query + param;
                    param1 = param1.substring(0, param1.length() - 1);
                    query1 = query1 + param1;
                    st.executeUpdate(sql);
                    st.executeUpdate(query);
                    st.executeUpdate(sql1);
                    st.executeUpdate(query1);
                }
            } catch (SQLException e) {
                System.err.println("Got an exception! ");
                System.err.println(e.getMessage());
            }

        } finally {
            driver.quit();
        }
    }
}