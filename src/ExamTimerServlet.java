package com.suma;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * ExamTimerServlet
 * --------------------------------------------------------------------
 * Problem 73 - Simple Exam Timer Simulator (HttpSession)
 *
 * Author : Suma Biradar (USN: 2BL23CS159)
 * Subject: Advanced Java Programming (BCS613D) - Module 4 (Servlets)
 *
 * Concept practiced: HttpSession
 *
 * How it works:
 *   - "Start Exam"  -> stores System.currentTimeMillis() as a session
 *                      attribute named "examStart".
 *   - "Refresh"     -> reads "examStart" from session, computes elapsed
 *                      time, displays as MM:SS. If elapsed > 60 min,
 *                      shows the "Exam time over!" message.
 *   - "End Exam"    -> calls session.invalidate() and shows a final
 *                      summary of the total time taken.
 * --------------------------------------------------------------------
 */
@WebServlet("/ExamTimerServlet")
public class ExamTimerServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    /** Maximum allowed exam duration = 60 minutes (in milliseconds). */
    private static final long EXAM_LIMIT_MS = 60L * 60L * 1000L;

    /**
     * GET handler is used for the "Refresh" link so that the user can
     * simply reload the page to see the running timer update.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Treat a plain GET as a "refresh / show elapsed time" action.
        handleRequest(request, response, "refresh");
    }

    /**
     * POST handler is used by the form buttons (Start / End).
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // The "action" parameter tells us which button the user clicked.
        String action = request.getParameter("action");
        handleRequest(request, response, action);
    }

    /**
     * Central dispatcher. Decides what to do based on the action value
     * (start / refresh / end) and writes the correct HTML response.
     */
    private void handleRequest(HttpServletRequest request,
                               HttpServletResponse response,
                               String action)
            throws IOException {

        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        // Get the session if it exists; do not auto-create one yet.
        HttpSession session = request.getSession(false);

        // ---- Input validation: action must be one of the known values ----
        if (action == null || action.trim().isEmpty()) {
            action = "refresh"; // safe default
        }
        action = action.toLowerCase();

        if (!action.equals("start")
                && !action.equals("refresh")
                && !action.equals("end")) {
            renderError(out, "Invalid action: '" + escape(action)
                    + "'. Please use the buttons on the form.");
            return;
        }

        // -------------------- START EXAM --------------------
        if (action.equals("start")) {
            // Always create a fresh session so a new exam starts cleanly.
            if (session != null) {
                session.invalidate();
            }
            session = request.getSession(true);

            long now = System.currentTimeMillis();
            session.setAttribute("examStart", Long.valueOf(now));

            String startTimeText =
                    new SimpleDateFormat("dd-MM-yyyy hh:mm:ss a").format(new Date(now));

            renderRunning(out, 0L, 0L, startTimeText, false);
            return;
        }

        // -------------------- END EXAM --------------------
        if (action.equals("end")) {
            if (session == null || session.getAttribute("examStart") == null) {
                renderError(out,
                        "No active exam session found. Please click 'Start Exam' first.");
                return;
            }
            long start = ((Long) session.getAttribute("examStart")).longValue();
            long elapsedMs = System.currentTimeMillis() - start;

            // Clear all session data as required by the problem statement.
            session.invalidate();

            renderSummary(out, elapsedMs);
            return;
        }

        // -------------------- REFRESH (show elapsed time) --------------------
        if (session == null || session.getAttribute("examStart") == null) {
            renderError(out,
                    "No exam is currently running. Please go back and click 'Start Exam'.");
            return;
        }

        long start = ((Long) session.getAttribute("examStart")).longValue();
        long elapsedMs = System.currentTimeMillis() - start;

        // Convert elapsed milliseconds into minutes and seconds.
        long secs = elapsedMs / 1000L;
        long mins = secs / 60L;
        secs = secs % 60L;

        boolean timeOver = elapsedMs >= EXAM_LIMIT_MS;
        String startTimeText =
                new SimpleDateFormat("dd-MM-yyyy hh:mm:ss a").format(new Date(start));

        renderRunning(out, mins, secs, startTimeText, timeOver);
    }

    // =================================================================
    //                       HTML RENDERING HELPERS
    // =================================================================

    /** Common <head> + page styling shared by every response page. */
    private void writeHeader(PrintWriter out, String title) {
        out.println("<!DOCTYPE html>");
        out.println("<html lang='en'><head>");
        out.println("<meta charset='UTF-8'>");
        out.println("<title>" + escape(title) + "</title>");
        out.println("<style>");
        out.println("  body{font-family:Arial,Helvetica,sans-serif;");
        out.println("       background:#eef2f7;margin:0;padding:40px;}");
        out.println("  .card{max-width:520px;margin:30px auto;background:#fff;");
        out.println("        border-radius:10px;box-shadow:0 4px 14px rgba(0,0,0,.1);");
        out.println("        padding:30px 35px;}");
        out.println("  h1{color:#1f3a68;margin-top:0;}");
        out.println("  h2{color:#1f3a68;}");
        out.println("  .timer{font-size:54px;font-weight:bold;color:#1f6feb;");
        out.println("         text-align:center;letter-spacing:3px;margin:18px 0;}");
        out.println("  .info{color:#444;margin:6px 0;}");
        out.println("  .alert{background:#ffe8e8;color:#a40000;border:1px solid #f5b5b5;");
        out.println("         padding:12px;border-radius:6px;margin:15px 0;font-weight:bold;}");
        out.println("  .ok{background:#e9f7ec;color:#1d6f3a;border:1px solid #b6e0c1;");
        out.println("      padding:12px;border-radius:6px;margin:15px 0;}");
        out.println("  form{display:inline-block;margin-right:8px;}");
        out.println("  button,a.btn{background:#1f6feb;color:#fff;border:0;padding:10px 18px;");
        out.println("       border-radius:6px;font-size:15px;cursor:pointer;");
        out.println("       text-decoration:none;display:inline-block;margin-top:8px;}");
        out.println("  button.end{background:#c0392b;}");
        out.println("  button:hover,a.btn:hover{opacity:.9;}");
        out.println("  .footer{margin-top:25px;font-size:13px;color:#777;text-align:center;}");
        out.println("</style></head><body><div class='card'>");
    }

    /** Common closing markup for every response page. */
    private void writeFooter(PrintWriter out) {
        out.println("<div class='footer'>Suma Biradar &middot; 2BL23CS159 &middot; "
                + "Problem 73 - Exam Timer (HttpSession)</div>");
        out.println("</div></body></html>");
    }

    /** Page shown right after Start Exam and on every Refresh. */
    private void renderRunning(PrintWriter out,
                               long mins, long secs,
                               String startTimeText,
                               boolean timeOver) {
        writeHeader(out, "Exam in Progress");
        out.println("<h1>Exam in Progress</h1>");
        out.println("<p class='info'><b>Started at:</b> " + escape(startTimeText) + "</p>");

        out.println("<div class='timer'>" +
                String.format("%02d:%02d", Long.valueOf(mins), Long.valueOf(secs)) +
                "</div>");
        out.println("<p class='info' style='text-align:center;'>"
                + "Elapsed time (MM:SS) &middot; refresh to update</p>");

        if (timeOver) {
            out.println("<div class='alert'>&#9888; Exam time over! "
                    + "The 60-minute limit has been reached.</div>");
        } else {
            out.println("<div class='ok'>Exam is running. You have up to 60 minutes.</div>");
        }

        // Refresh uses GET so a simple page reload also works.
        out.println("<a class='btn' href='ExamTimerServlet'>Refresh</a>");

        // End Exam posts so we can call session.invalidate() server-side.
        out.println("<form method='post' action='ExamTimerServlet' "
                + "style='display:inline;'>");
        out.println("  <input type='hidden' name='action' value='end'>");
        out.println("  <button type='submit' class='end'>End Exam</button>");
        out.println("</form>");

        writeFooter(out);
    }

    /** Final summary page shown when the user clicks End Exam. */
    private void renderSummary(PrintWriter out, long elapsedMs) {
        long secs = elapsedMs / 1000L;
        long mins = secs / 60L;
        secs = secs % 60L;

        writeHeader(out, "Exam Ended");
        out.println("<h1>Exam Ended</h1>");
        out.println("<div class='ok'>Your session has been cleared successfully.</div>");
        out.println("<h2>Summary</h2>");
        out.println("<p class='info'><b>Total time taken:</b></p>");
        out.println("<div class='timer'>" +
                String.format("%02d:%02d", Long.valueOf(mins), Long.valueOf(secs)) +
                "</div>");
        out.println("<p class='info' style='text-align:center;'>"
                + "(MM:SS &mdash; total minutes and seconds)</p>");

        out.println("<a class='btn' href='index.html'>Start a New Exam</a>");
        writeFooter(out);
    }

    /** Friendly error page for invalid actions / missing sessions. */
    private void renderError(PrintWriter out, String message) {
        writeHeader(out, "Error");
        out.println("<h1>Something went wrong</h1>");
        out.println("<div class='alert'>" + escape(message) + "</div>");
        out.println("<a class='btn' href='index.html'>Back to Home</a>");
        writeFooter(out);
    }

    /** Very small HTML escaper to keep output safe. */
    private String escape(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }
}
