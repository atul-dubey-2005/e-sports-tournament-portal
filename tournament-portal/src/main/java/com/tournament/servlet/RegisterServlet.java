package com.tournament.servlet;

import com.tournament.dao.SquadDAO;
import com.tournament.model.Player;
import com.tournament.model.Squad;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    // Keep this list in sync with the <select> options in register.jsp
    private static final List<String> ALLOWED_GAMES = Arrays.asList(
            "BGMI", "Free Fire", "Call of Duty Mobile", "Clash of Clans",
            "Clash Royale", "eFootball", "PUBG New State", "Valorant Mobile",
            "Asphalt 9", "Other"
    );

    private final SquadDAO squadDAO = new SquadDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher("/register.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String squadName = trim(req.getParameter("squadName"));
        String leaderName = trim(req.getParameter("leaderName"));
        String email = trim(req.getParameter("email"));
        String phone = trim(req.getParameter("phone"));
        String game = trim(req.getParameter("game"));

        List<String> errors = new ArrayList<>();

        // ---- Validation ----
        if (squadName == null || squadName.length() < 3 || squadName.length() > 50) {
            errors.add("Squad name must be 3-50 characters.");
        }
        if (leaderName == null || leaderName.isEmpty()) {
            errors.add("Leader name is required.");
        }
        if (email == null || !EMAIL_PATTERN.matcher(email).matches()) {
            errors.add("A valid email is required.");
        }
        if (game == null || !ALLOWED_GAMES.contains(game)) {
            errors.add("Please choose a game from the list.");
        }
        if (squadName != null && squadDAO.squadNameExists(squadName)) {
            errors.add("A squad with this name is already registered.");
        }

        // ---- Players (min 2, max 5) ----
        List<Player> players = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            String pName = trim(req.getParameter("playerName" + i));
            String igId = trim(req.getParameter("inGameId" + i));
            String role = trim(req.getParameter("role" + i));
            if (pName != null && !pName.isEmpty() && igId != null && !igId.isEmpty()) {
                players.add(new Player(pName, igId, role == null ? "" : role));
            }
        }
        if (players.size() < 2) {
            errors.add("At least 2 players (with name and in-game ID) are required.");
        }

        if (!errors.isEmpty()) {
            req.setAttribute("errors", errors);
            req.setAttribute("squadName", squadName);
            req.setAttribute("leaderName", leaderName);
            req.setAttribute("email", email);
            req.setAttribute("phone", phone);
            req.setAttribute("game", game);
            req.getRequestDispatcher("/register.jsp").forward(req, resp);
            return;
        }

        Squad squad = new Squad();
        squad.setSquadName(squadName);
        squad.setLeaderName(leaderName);
        squad.setEmail(email);
        squad.setPhone(phone);
        squad.setGame(game);
        squad.setPlayers(players);

        int squadId = squadDAO.registerSquad(squad);

        if (squadId > 0) {
            req.setAttribute("successMessage",
                    "Squad \"" + squadName + "\" registered successfully! Squad ID: " + squadId);
            req.getRequestDispatcher("/register.jsp").forward(req, resp);
        } else {
            req.setAttribute("errors", List.of("Registration failed due to a server error. Please try again."));
            req.getRequestDispatcher("/register.jsp").forward(req, resp);
        }
    }

    private String trim(String s) {
        return s == null ? null : s.trim();
    }
}