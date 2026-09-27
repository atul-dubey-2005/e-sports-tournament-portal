package com.tournament.servlet;

import com.tournament.dao.MatchDAO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/admin/updateResult")
public class UpdateResultServlet extends HttpServlet {

    private final MatchDAO matchDAO = new MatchDAO();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("adminUsername") == null) {
            resp.sendRedirect(req.getContextPath() + "/admin/login");
            return;
        }

        try {
            int matchId = Integer.parseInt(req.getParameter("matchId"));
            int score1 = Integer.parseInt(req.getParameter("squad1Score"));
            int score2 = Integer.parseInt(req.getParameter("squad2Score"));

            boolean ok = matchDAO.recordResult(matchId, score1, score2);
            session.setAttribute("flashError", ok ? null :
                    "Could not record result (match may already be completed).");
        } catch (NumberFormatException e) {
            session.setAttribute("flashError", "Invalid score values.");
        }

        resp.sendRedirect(req.getContextPath() + "/admin/dashboard");
    }
}
