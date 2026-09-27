<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="com.tournament.dao.SquadDAO, com.tournament.dao.MatchDAO, com.tournament.model.Squad, java.util.List" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
    // Pull live stats for the hero + top-squads preview. Falls back to empty
    // lists gracefully if the DB isn't reachable yet, so the page still renders.
    List<Squad> topSquads = java.util.Collections.emptyList();
    int matchCount = 0;
    try {
        topSquads = new SquadDAO().getLeaderboard();
        matchCount = new MatchDAO().getAllMatches().size();
    } catch (Exception ex) {
        // leave defaults; page still renders without live data
    }
    request.setAttribute("topSquads", topSquads);
    request.setAttribute("squadCount", topSquads.size());
    request.setAttribute("matchCount", matchCount);
    request.setAttribute("leader", topSquads.isEmpty() ? null : topSquads.get(0));
%>
<!DOCTYPE html>
<html>
<head><title>Arena Tournament Portal</title></head>
<body>
<div class="bg-fx"><div class="bg-orb cyan"></div><div class="bg-orb magenta"></div></div>
<%@ include file="/WEB-INF/header.jspf" %>
<main>

    <section class="hero">
        <div>
            <span class="hero-tag reveal reveal-1">Registration is open for Season 1</span>
            <h1 class="reveal reveal-2">Register your squad.<br>Climb the ranks.</h1>
            <p class="lead reveal reveal-3">Sign your squad up, get your matches scheduled, and watch your points update live as results come in — no spreadsheets, no group chats.</p>
            <div class="hero-actions reveal reveal-4">
                <a class="btn" href="${pageContext.request.contextPath}/register">Register Squad</a>
                <a class="btn btn-ghost" href="${pageContext.request.contextPath}/leaderboard">View Leaderboard</a>
            </div>
            <div class="stat-row reveal reveal-5">
                <div>
                    <span class="stat-num" data-count="${squadCount}">0</span>
                    <span class="stat-label">Squads registered</span>
                </div>
                <div>
                    <span class="stat-num" data-count="${matchCount}">0</span>
                    <span class="stat-label">Matches played</span>
                </div>
                <div>
                    <span class="stat-num">3 pts</span>
                    <span class="stat-label">Awarded per win</span>
                </div>
            </div>
        </div>

        <div class="rank-card clip-panel reveal reveal-3">
            <c:choose>
                <c:when test="${not empty leader}">
                    <div class="eyebrow">Current leader</div>
                    <div class="squad-name">${leader.squadName}</div>
                    <div class="points-row">
                        <span class="mini-pts">${leader.points}</span>
                        <span class="points-unit">points</span>
                    </div>
                    <div class="wl">
                        <span>Wins <b>${leader.wins}</b></span>
                        <span>Losses <b>${leader.losses}</b></span>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="eyebrow">Current leader</div>
                    <div class="empty">No squads registered yet — be the first to claim the top spot.</div>
                </c:otherwise>
            </c:choose>
        </div>
    </section>

    <section>
        <h2>How it works</h2>
        <div class="steps">
            <div class="step-panel clip-panel">
                <span class="step-num">01</span>
                <h3>Register your squad</h3>
                <p>Add your leader details and 2–5 players with in-game IDs. Takes about two minutes.</p>
            </div>
            <div class="step-panel clip-panel">
                <span class="step-num">02</span>
                <h3>Play your matches</h3>
                <p>Admins schedule your matches and enter results as they happen — check the schedule anytime.</p>
            </div>
            <div class="step-panel clip-panel">
                <span class="step-num">03</span>
                <h3>Climb the leaderboard</h3>
                <p>Wins earn 3 points, losses earn 1. Standings update the moment a result is recorded.</p>
            </div>
        </div>
    </section>

    <c:if test="${not empty topSquads}">
        <section class="top-squads-section">
            <h2>Top squads</h2>
            <div class="top-squads-row">
                <c:forEach var="s" items="${topSquads}" begin="0" end="2" varStatus="loop">
                    <div class="mini-squad-card clip-panel">
                        <span class="mini-rank rank-${loop.index + 1}">#${loop.index + 1}</span>
                        <div class="mini-info">
                            <div class="mini-name">${s.squadName}</div>
                            <div class="mini-pts">${s.points} points (${s.wins}W / ${s.losses}L)</div>
                        </div>
                    </div>
                </c:forEach>
            </div>
        </section>
    </c:if>

    <section class="final-cta clip-panel">
        <h2>Ready to compete?</h2>
        <p>Registration takes two minutes. Your squad could be next season's top seed.</p>
        <a class="btn" href="${pageContext.request.contextPath}/register">Register Squad</a>
    </section>

</main>

<script>
(function () {
    var reduceMotion = window.matchMedia && window.matchMedia('(prefers-reduced-motion: reduce)').matches;
    var nums = document.querySelectorAll('.stat-num[data-count]');

    nums.forEach(function (el) {
        var target = parseInt(el.getAttribute('data-count'), 10) || 0;
        if (reduceMotion || target === 0) {
            el.textContent = target;
            return;
        }
        var start = null;
        var duration = 900;
        function step(ts) {
            if (start === null) start = ts;
            var progress = Math.min((ts - start) / duration, 1);
            var eased = 1 - Math.pow(1 - progress, 3);
            el.textContent = Math.round(eased * target);
            if (progress < 1) requestAnimationFrame(step);
        }
        requestAnimationFrame(step);
    });
})();
</script>
</body>
</html>
