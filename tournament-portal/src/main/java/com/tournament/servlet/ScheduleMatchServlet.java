package com.tournament.servlet;

import com.tournament.dao.MatchDAO;
import com.tournament.dao.SquadDAO;
import com.tournament.model.Squad;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

@WebServlet("/admin/scheduleMatch")
public class ScheduleMatchServlet extends HttpServlet {

    private final MatchDAO matchDAO = new MatchDAO();
    private final SquadDAO squadDAO = new SquadDAO();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("adminUsername") == null) {
            resp.sendRedirect(req.getContextPath() + "/admin/login");
            return;
        }

        try {
            int squad1Id = Integer.parseInt(req.getParameter("squad1Id"));
            int squad2Id = Integer.parseInt(req.getParameter("squad2Id"));
            String dateTimeStr = req.getParameter("matchTime"); // yyyy-MM-dd'T'HH:mm from <input type=datetime-local>

            if (squad1Id == squad2Id) {
                req.getSession().setAttribute("flashError", "A squad cannot play itself.");
            } else {
                Squad s1 = squadDAO.getSquadById(squad1Id);
                Squad s2 = squadDAO.getSquadById(squad2Id);

                if (s1 == null || s2 == null) {
                    req.getSession().setAttribute("flashError", "One of the selected squads no longer exists.");
                } else if (!s1.getGame().equals(s2.getGame())) {
                    req.getSession().setAttribute("flashError",
                            "Both squads must play the same game — " + s1.getSquadName() + " plays " +
                            s1.getGame() + ", " + s2.getSquadName() + " plays " + s2.getGame() + ".");
                } else {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
                    Date parsed = sdf.parse(dateTimeStr);
                    boolean ok = matchDAO.scheduleMatch(squad1Id, squad2Id, new Timestamp(parsed.getTime()));
                    req.getSession().setAttribute("flashError", ok ? null : "Could not schedule match.");
                }
            }
        } catch (Exception e) {
            req.getSession().setAttribute("flashError", "Invalid match details submitted.");
        }

        resp.sendRedirect(req.getContextPath() + "/admin/dashboard");
    }
}
