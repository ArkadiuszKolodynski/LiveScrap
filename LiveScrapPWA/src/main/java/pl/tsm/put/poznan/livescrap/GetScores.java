package pl.tsm.put.poznan.livescrap;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
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
    private final String SQL_QUERY;
    private final String SQL_QUERY_SCORES;
    private final ArrayList<Header> headersList;

    public GetScores() {
        this.SQL_QUERY = "SELECT * FROM headers ORDER BY country";
        this.SQL_QUERY_SCORES = "SELECT * FROM scores WHERE id = ?";
        this.headersList = new ArrayList<>();
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try (Connection connection = dataSource.getConnection(); Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(SQL_QUERY)) {
            while (resultSet.next()) {
                headersList.add(new Header(resultSet.getInt("id"), resultSet.getString("country"), resultSet.getString("league")));
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

        request.getRequestDispatcher("/WEB-INF/index.jsp").forward(request, response);
        headersList.clear();
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
