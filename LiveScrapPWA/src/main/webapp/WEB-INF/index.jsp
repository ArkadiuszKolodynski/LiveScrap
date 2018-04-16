<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%><%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %><%@page contentType="text/html" pageEncoding="UTF-8"%><!DOCTYPE html>
<html lang="pl">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>LiveScrap</title>
    </head>
    <body><jsp:useBean id="now" class="java.util.Date" /><fmt:formatDate var="year" value="${now}" pattern="dd-MM-yyyy" />
        <h1>Hello World!</h1>
        <h2>${year}</h2>
        <c:forEach var="h" items="${list}">
        <div>
            <div>
                <span><strong>${h.getCountryName()} ${h.getLeagueName()}</strong></span>
            </div><c:out value="${id448}"/><c:set var="scores" value='${requestScope[h.getIdAsString()]}'/>
            <c:forEach var="s" items="${scores}">
            <div>
                <span>${s.getMin()} ${s.getTeam1()} ${s.getScore()} ${s.getTeam2()}</span>
            </div>
            </c:forEach>
        </div><br>
        </c:forEach>
    </body>
</html>
