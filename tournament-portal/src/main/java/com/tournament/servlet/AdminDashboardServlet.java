package com.tournament.servlet;

import com.tournament.dao.MatchDAO;
import com.tournament.dao.SquadDAO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/admin/dashboard")
public class AdminDashboardServlet extends HttpServlet {

    private final SquadDAO squadDAO = new SquadDAO();
    private final MatchDAO matchDAO = new MatchDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("adminUsername") == null) {
            resp.sendRedirect(req.getContextPath() + "/admin/login");
            return;
        }
        req.setAttribute("squads", squadDAO.getAllSquads());
        req.setAttribute("matches", matchDAO.getAllMatches());
        req.getRequestDispatcher("/admin/dashboard.jsp").forward(req, resp);
    }
}
