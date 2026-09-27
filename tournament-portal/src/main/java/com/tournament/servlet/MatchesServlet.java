package com.tournament.servlet;

import com.tournament.dao.MatchDAO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/matches")
public class MatchesServlet extends HttpServlet {

    private final MatchDAO matchDAO = new MatchDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setAttribute("matches", matchDAO.getAllMatches());
        req.getRequestDispatcher("/matches.jsp").forward(req, resp);
    }
}
