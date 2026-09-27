<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head><title>Admin Dashboard</title></head>
<body>
<%@ include file="/WEB-INF/header.jspf" %>
<main>
    <div style="display:flex;justify-content:space-between;align-items:center;">
        <h2>Admin Dashboard</h2>
        <a class="btn" style="background:#3a2020;" href="${pageContext.request.contextPath}/admin/logout">Log Out</a>
    </div>

    <c:if test="${not empty sessionScope.flashError}">
        <div class="error-box">${sessionScope.flashError}</div>
        <c:remove var="flashError" scope="session"/>
    </c:if>

    <div class="card">
        <h2 style="font-size:1.05rem;">Schedule a Match</h2>
        <p class="subtle" style="margin-bottom:8px;">Both squads must play the same game — Squad 2's list narrows automatically once you pick Squad 1.</p>
        <form method="post" action="${pageContext.request.contextPath}/admin/scheduleMatch">
            <label>Squad 1</label>
            <select name="squad1Id" id="squad1Select" required onchange="filterSquad2()">
                <option value="">Choose squad 1</option>
                <c:forEach var="s" items="${squads}">
                    <option value="${s.squadId}" data-game="${s.game}">${s.squadName} (${s.game})</option>
                </c:forEach>
            </select>
            <label>Squad 2</label>
            <select name="squad2Id" id="squad2Select" required>
                <option value="">Choose squad 1 first</option>
                <c:forEach var="s" items="${squads}">
                    <option value="${s.squadId}" data-game="${s.game}" style="display:none;">${s.squadName} (${s.game})</option>
                </c:forEach>
            </select>
            <label>Match Date &amp; Time</label>
            <input type="datetime-local" name="matchTime" required>
            <button type="submit">Schedule Match</button>
        </form>
    </div>

    <div class="card">
        <h2 style="font-size:1.05rem;">Matches &mdash; Enter Results</h2>
        <table>
            <thead><tr><th>Date/Time</th><th>Game</th><th>Matchup</th><th>Status</th><th>Update Result</th></tr></thead>
            <tbody>
                <c:forEach var="m" items="${matches}">
                    <tr>
                        <td><fmt:formatDate value="${m.matchTime}" pattern="dd MMM, HH:mm"/></td>
                        <td>${m.game}</td>
                        <td>${m.squad1Name} vs ${m.squad2Name}</td>
                        <td>
                            <c:choose>
                                <c:when test="${m.status == 'COMPLETED'}">
                                    <span class="badge badge-completed">${m.squad1Score} - ${m.squad2Score}</span>
                                </c:when>
                                <c:otherwise><span class="badge badge-scheduled">Scheduled</span></c:otherwise>
                            </c:choose>
                        </td>
                        <td>
                            <c:if test="${m.status != 'COMPLETED'}">
                                <form method="post" action="${pageContext.request.contextPath}/admin/updateResult" style="display:flex;gap:6px;align-items:center;">
                                    <input type="hidden" name="matchId" value="${m.matchId}">
                                    <input type="number" name="squad1Score" min="0" style="width:60px;" required>
                                    <span>-</span>
                                    <input type="number" name="squad2Score" min="0" style="width:60px;" required>
                                    <button type="submit" style="margin-top:0;padding:8px 14px;">Save</button>
                                </form>
                            </c:if>
                        </td>
                    </tr>
                </c:forEach>
                <c:if test="${empty matches}">
                    <tr><td colspan="5" class="subtle">No matches yet.</td></tr>
                </c:if>
            </tbody>
        </table>
    </div>

    <div class="card">
        <h2 style="font-size:1.05rem;">Registered Squads</h2>
        <table>
            <thead><tr><th>Squad</th><th>Game</th><th>Leader</th><th>Email</th><th>W</th><th>L</th><th>Pts</th></tr></thead>
            <tbody>
                <c:forEach var="s" items="${squads}">
                    <tr>
                        <td>${s.squadName}</td>
                        <td>${s.game}</td>
                        <td>${s.leaderName}</td>
                        <td>${s.email}</td>
                        <td>${s.wins}</td>
                        <td>${s.losses}</td>
                        <td>${s.points}</td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </div>

    <script>
        function filterSquad2() {
            var squad1 = document.getElementById('squad1Select');
            var squad2 = document.getElementById('squad2Select');
            var selectedGame = squad1.options[squad1.selectedIndex].getAttribute('data-game');
            var selectedSquad1Value = squad1.value;

            for (var i = 0; i < squad2.options.length; i++) {
                var opt = squad2.options[i];
                if (!opt.value) continue; // keep the placeholder option as-is
                var matches = opt.getAttribute('data-game') === selectedGame && opt.value !== selectedSquad1Value;
                opt.style.display = matches ? '' : 'none';
            }
            squad2.value = '';
        }
    </script>
</main>
</body>
</html>
