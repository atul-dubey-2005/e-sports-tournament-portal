```jsp
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html>
<head>
    <title>Matches</title>

    <style>
        .filter-box {
            display: flex;
            gap: 10px;
            margin-bottom: 20px;
            flex-wrap: wrap;
        }

        .filter-box input,
        .filter-box select,
        .filter-box button {
            padding: 10px 14px;
            border-radius: 6px;
            border: 1px solid #ccc;
            font-size: 14px;
        }

        .filter-box input {
            flex: 1;
            min-width: 220px;
        }

        .filter-box button {
            background: #222;
            color: white;
            cursor: pointer;
        }

        .filter-box button:hover {
            background: #444;
        }
    </style>
</head>

<body>

<%@ include file="/WEB-INF/header.jspf" %>

<main>

    <h2>Match Schedule &amp; Results</h2>

    <!-- Search / Filter -->
    <div class="card filter-box">

        <input type="text"
               id="matchSearch"
               placeholder="Search game or squad name..."
               onkeyup="filterMatches()">

        <select id="statusFilter" onchange="filterMatches()">
            <option value="">All Status</option>
            <option value="COMPLETED">Completed</option>
            <option value="SCHEDULED">Scheduled</option>
        </select>

        <button type="button" onclick="clearFilters()">
            Clear
        </button>

    </div>

    <div class="card">

        <table id="matchesTable">

            <thead>
                <tr>
                    <th>Date/Time</th>
                    <th>Game</th>
                    <th>Matchup</th>
                    <th>Score</th>
                    <th>Status</th>
                </tr>
            </thead>

            <tbody>

                <c:forEach var="m" items="${matches}">

                    <tr class="match-row"
                        data-status="${m.status}">

                        <td>
                            <fmt:formatDate
                                value="${m.matchTime}"
                                pattern="dd MMM yyyy, HH:mm"/>
                        </td>

                        <td>
                            ${m.game}
                        </td>

                        <td>
                            ${m.squad1Name}
                            <span class="subtle">vs</span>
                            ${m.squad2Name}
                        </td>

                        <td>

                            <c:choose>

                                <c:when test="${m.status == 'COMPLETED'}">
                                    ${m.squad1Score} - ${m.squad2Score}
                                </c:when>

                                <c:otherwise>
                                    <span class="subtle">-</span>
                                </c:otherwise>

                            </c:choose>

                        </td>

                        <td>

                            <c:choose>

                                <c:when test="${m.status == 'COMPLETED'}">
                                    <span class="badge badge-completed">
                                        Completed
                                    </span>
                                </c:when>

                                <c:otherwise>
                                    <span class="badge badge-scheduled">
                                        Scheduled
                                    </span>
                                </c:otherwise>

                            </c:choose>

                        </td>

                    </tr>

                </c:forEach>

                <c:if test="${empty matches}">
                    <tr>
                        <td colspan="5" class="subtle">
                            No matches scheduled yet.
                        </td>
                    </tr>
                </c:if>

            </tbody>

        </table>

    </div>

</main>


<script>

function filterMatches() {

    let searchText =
        document.getElementById("matchSearch")
        .value
        .toLowerCase();

    let status =
        document.getElementById("statusFilter")
        .value;

    let rows =
        document.querySelectorAll(".match-row");

    rows.forEach(function(row) {

        let rowText =
            row.innerText.toLowerCase();

        let rowStatus =
            row.getAttribute("data-status");

        let searchMatch =
            rowText.includes(searchText);

        let statusMatch =
            status === "" || rowStatus === status;

        if (searchMatch && statusMatch) {
            row.style.display = "";
        } else {
            row.style.display = "none";
        }

    });
}


function clearFilters() {

    document.getElementById("matchSearch").value = "";
    document.getElementById("statusFilter").value = "";

    filterMatches();
}

</script>

</body>
</html>
```
