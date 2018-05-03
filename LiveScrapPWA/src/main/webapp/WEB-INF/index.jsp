<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%><%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %><%@page contentType="text/html" pageEncoding="UTF-8"%><!DOCTYPE html>
<html lang="pl">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
        <link rel="stylesheet" href="css/styles.css">
        <link rel="stylesheet" href="css/flag-icon.min.css">
        <link rel="stylesheet" href="https://fonts.googleapis.com/css?family=Pontano+Sans&amp;subset=latin-ext">  
        <title>LiveScrap</title>
    </head>
    <body><jsp:useBean id="now" class="java.util.Date" /><fmt:formatDate var="date" value="${now}" pattern="dd.MM.yy" />
        <nav>
            <a class="navbrand" href="index">
                <strong>LiveScrap</strong>
            </a>
        </nav>
        <div class="container">
        <c:forEach var="h" items="${list}">
            <div class="block">
                <div class="header">
                    <span class="flag-icon flag-icon-${h.getCountryCode()}"></span>
                    <span class="country">
                        <strong>${h.getCountryName()}</strong>
                    </span>
                    <span>-</span>
                    <span class="league">
                        <strong>${h.getLeagueName()}</strong>
                    </span>
                    <span class="date">${date}</span>
                </div><c:out value="${id448}"/><c:set var="scores" value='${requestScope[h.getIdAsString()]}'/>
                <div class="results"><c:forEach var="s" items="${scores}">
                    <div class="result">
                        <span class="minute">${s.getMin()}</span>
                        <span class="home-team">${s.getTeam1()}</span>
                        <span class="score">${s.getScore()}</span>
                        <span class="away-team">${s.getTeam2()}</span>
                    </div></c:forEach>
                </div>
            </div>
        </c:forEach>
        </div>
        <footer><fmt:formatDate var="year" value="${now}" pattern="yyyy" />
            ⓒ ${year}
        </footer>
    </body>
</html>
