package com.tournament.servlet;

import com.tournament.dao.SquadDAO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/leaderboard")
public class LeaderboardServlet extends HttpServlet {

    private final SquadDAO squadDAO = new SquadDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setAttribute("squads", squadDAO.getLeaderboard());
        req.getRequestDispatcher("/leaderboard.jsp").forward(req, resp);
    }
}
