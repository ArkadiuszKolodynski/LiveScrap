package pl.tsm.put.poznan.livescrap;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

public class GetScores extends HttpServlet {

    @Resource(name = "jdbc/MariaDB")
    private DataSource dataSource;
    private String SQL_QUERY;
    private String SQL_QUERY_SCORES;
    private final ArrayList<Header> headersList;

    public GetScores() {
        this.headersList = new ArrayList<>();
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        SQL_QUERY = "SELECT * FROM headers WHERE time = & ORDER BY country";
        SQL_QUERY_SCORES = "SELECT * FROM scores WHERE id = ? AND time = &";
        
        String i = request.getParameter("id");
        if (i != null) {
            SQL_QUERY = SQL_QUERY.replace("&", i);
            SQL_QUERY_SCORES = SQL_QUERY_SCORES.replace("&", i);
        } else {
            SQL_QUERY = SQL_QUERY.replace("&", "1");
            SQL_QUERY_SCORES = SQL_QUERY_SCORES.replace("&", "1");
        }

        Map<String, String> countries = new HashMap<>();
        for (String iso : Locale.getISOCountries()) {
            Locale l = new Locale("", iso);
            countries.put(l.getDisplayCountry(Locale.ENGLISH), iso);
        }
        countries.put("England", "gb");
        countries.put("Republic of Korea", "kr");
        countries.put("FYR Macedonia", "mk");
        countries.put("Northern Ireland", "gb");
        countries.put("Scotland", "gb");
        countries.put("Wales", "gb");
        countries.put("USA", "us");

        try (Connection connection = dataSource.getConnection(); Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(SQL_QUERY)) {
            while (resultSet.next()) {
                headersList.add(new Header(resultSet.getInt("id"), countries.get(resultSet.getString("country")), resultSet.getString("country"), resultSet.getString("league")));
            }
            connection.close();
        } catch (SQLException ex) {
            Logger.getLogger(GetScores.class.getName()).log(Level.SEVERE, null, ex);
        }
        request.setAttribute("list", headersList);

        for (Header e : headersList) {
            try (Connection connection = dataSource.getConnection(); PreparedStatement statement = connection.prepareStatement(SQL_QUERY_SCORES);) {
                ArrayList<Score> scores = new ArrayList<>();
                statement.setString(1, e.getIdAsString());
                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    scores.add(new Score(resultSet.getInt("id"), resultSet.getString("minn"), resultSet.getString("team1"), resultSet.getString("team2"),
                            resultSet.getString("sco")));
                }
                connection.close();
                request.setAttribute(e.getIdAsString(), scores);
            } catch (SQLException ex) {
                Logger.getLogger(GetScores.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        response.setContentType("text/html;charset=UTF-8");
        request.getRequestDispatcher("/WEB-INF/index.jsp").include(request, response);
        headersList.clear();
        SQL_QUERY = "";
        SQL_QUERY_SCORES = "";
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }

}
