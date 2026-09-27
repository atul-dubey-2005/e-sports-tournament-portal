<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head><title>Register Squad</title></head>
<body>
<%@ include file="/WEB-INF/header.jspf" %>
<main>
    <h2>Register Your Squad</h2>

    <c:if test="${not empty successMessage}">
        <div class="success-box">${successMessage}</div>
    </c:if>

    <c:if test="${not empty errors}">
        <div class="error-box">
            <c:forEach var="e" items="${errors}"><div>${e}</div></c:forEach>
        </div>
    </c:if>

    <div class="card">
        <form method="post" action="${pageContext.request.contextPath}/register">
            <label>Squad Name</label>
            <input type="text" name="squadName" value="${squadName}" required minlength="3" maxlength="50">

            <label>Squad Leader Name</label>
            <input type="text" name="leaderName" value="${leaderName}" required>

            <label>Email</label>
            <input type="email" name="email" value="${email}" required>

            <label>Phone</label>
            <input type="text" name="phone" value="${phone}">

            <label>Game</label>
            <select name="game" required>
                <option value="" ${empty game ? 'selected' : ''} disabled>Choose a game</option>
                <option value="BGMI" ${game == 'BGMI' ? 'selected' : ''}>BGMI</option>
                <option value="Free Fire" ${game == 'Free Fire' ? 'selected' : ''}>Free Fire</option>
                <option value="Call of Duty Mobile" ${game == 'Call of Duty Mobile' ? 'selected' : ''}>Call of Duty Mobile</option>
                <option value="Clash of Clans" ${game == 'Clash of Clans' ? 'selected' : ''}>Clash of Clans</option>
                <option value="Clash Royale" ${game == 'Clash Royale' ? 'selected' : ''}>Clash Royale</option>
                <option value="eFootball" ${game == 'eFootball' ? 'selected' : ''}>eFootball</option>
                <option value="PUBG New State" ${game == 'PUBG New State' ? 'selected' : ''}>PUBG New State</option>
                <option value="Valorant Mobile" ${game == 'Valorant Mobile' ? 'selected' : ''}>Valorant Mobile</option>
                <option value="Asphalt 9" ${game == 'Asphalt 9' ? 'selected' : ''}>Asphalt 9</option>
                <option value="Other" ${game == 'Other' ? 'selected' : ''}>Other</option>
            </select>

            <h2 style="margin-top:28px;font-size:1.1rem;">Players (2 to 5)</h2>
            <c:forEach var="i" begin="1" end="5">
                <div class="player-row" style="margin-top:10px;">
                    <div>
                        <label>Player ${i} Name</label>
                        <input type="text" name="playerName${i}">
                    </div>
                    <div>
                        <label>In-Game ID</label>
                        <input type="text" name="inGameId${i}">
                    </div>
                    <div>
                        <label>Role</label>
                        <input type="text" name="role${i}" placeholder="IGL / Sniper / Support...">
                    </div>
                </div>
            </c:forEach>

            <button type="submit">Register Squad</button>
        </form>
    </div>
</main>
</body>
</html>