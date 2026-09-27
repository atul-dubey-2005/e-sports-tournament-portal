<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head><title>Leaderboard</title></head>
<body>
<%@ include file="/WEB-INF/header.jspf" %>
<main>
    <h2>Leaderboard</h2>
    <div class="card">
        <table>
            <thead>
                <tr><th>Rank</th><th>Squad</th><th>Wins</th><th>Losses</th><th>Points</th></tr>
            </thead>
            <tbody>
                <c:forEach var="s" items="${squads}" varStatus="loop">
                    <tr>
                        <td class="rank-${loop.index + 1}">#${loop.index + 1}</td>
                        <td>${s.squadName}</td>
                        <td>${s.wins}</td>
                        <td>${s.losses}</td>
                        <td>${s.points}</td>
                    </tr>
                </c:forEach>
                <c:if test="${empty squads}">
                    <tr><td colspan="5" class="subtle">No squads registered yet.</td></tr>
                </c:if>
            </tbody>
        </table>
    </div>
</main>
</body>
</html>
