package com.mycompany.controller;

import com.mycompany.model.ImpedanceModel;
import com.mycompany.model.CircuitElement;
import com.mycompany.model.Complex;
import com.mycompany.model.ExpressionParser;
import com.mycompany.model.InvalidCircuitException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * Servlet handling impedance calculations for complex circuit expressions.
 *
 * This servlet provides access to the full circuit expression parsing functionality
 * using the ExpressionParser class. It supports nested series and parallel connections.
 *
 * Both GET and POST requests are handled uniformly without code duplication
 * by delegating to a common processRequest method.
 *
 * Cookies: after successful evaluation a "lastCalc" cookie is written containing a
 * compact (sanitized) representation of the expression and frequency (no spaces/commas).
 * Cookie handling does not alter the visible HTML output.
 *
 * @author Kamil Fulneczek
 * @version 1.1
 */
@WebServlet(name = "CircuitServlet", urlPatterns = {"/circuit"})
public class CircuitServlet extends HttpServlet {

    private String getContextPath(HttpServletRequest req) {
        return req.getContextPath();
    }

    protected void processRequest(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String expression = req.getParameter("expression");
        String frequencyStr = req.getParameter("frequency");

        resp.setContentType("text/html;charset=UTF-8");
        PrintWriter out = resp.getWriter();

        String ctx = getContextPath(req);

        if (expression == null || frequencyStr == null ||
                expression.isEmpty() || frequencyStr.isEmpty()) {
            displayForm(out, ctx);
        } else {
            performCalculation(out, ctx, req, resp, expression, frequencyStr);
        }
    }

    private void displayForm(PrintWriter out, String ctx) {
        out.println("<! DOCTYPE html>");
        out.println("<html lang=\"en\">");
        out.println("<head>");
        out.println("    <meta charset=\"UTF-8\">");
        out.println("    <title>Circuit Expression Calculator</title>");
        out.println("</head>");
        out.println("<body>");
        out.println("    <h1>Circuit Expression Calculator</h1>");
        out.println("    <p>Enter a circuit expression using the following format:</p>");
        out.println("    <ul>");
        out.println("      <li>Single component: \"R: 100\", \"C:1e-6\", \"L:0.01\"</li>");
        out.println("      <li>Series connection: \"series(R:100, C:1e-6)\"</li>");
        out.println("      <li>Parallel connection: \"parallel(R:100, R:200)\"</li>");
        out.println("      <li>Nested expression: \"series(R:100, parallel(C:1e-6, L:0.01), R:50)\"</li>");
        out.println("    </ul>");
        out.println("    <form action=\"" + ctx + "/circuit\" method=\"post\">");
        out.println("        <label for=\"expression\">Expression:</label><br>");
        out.println("        <input type=\"text\" name=\"expression\" id=\"expression\" size=\"80\" required><br><br>");
        out.println("        <label for=\"frequency\">Frequency (Hz):</label>");
        out.println("        <input type=\"text\" name=\"frequency\" id=\"frequency\" required><br><br>");
        out.println("        <button type=\"submit\">Calculate</button>");
        out.println("    </form>");
        out.println("    <br><a href=\"" + ctx + "/\">Back to Home</a>");
        out.println("</body>");
        out.println("</html>");
    }

    private void performCalculation(PrintWriter out, String ctx, HttpServletRequest req, HttpServletResponse resp, String expression, String frequencyStr)
            throws ServletException {

        ImpedanceModel model = (ImpedanceModel) getServletContext()
                .getAttribute(AppContextListener.MODEL_ATTRIBUTE);

        if (model == null) {
            throw new ServletException("ImpedanceModel not found in ServletContext");
        }

        out.println("<! DOCTYPE html>");
        out.println("<html lang=\"en\">");
        out.println("<head>");
        out.println("    <meta charset=\"UTF-8\">");
        out.println("    <title>Circuit Expression Result</title>");
        out.println("</head>");
        out.println("<body>");

        try {
            double frequency = Double.parseDouble(frequencyStr);

            CircuitElement element = ExpressionParser.parse(expression);
            Complex impedance = model.calculateImpedance(element, frequency);

            out.println("    <h1>Circuit Expression Result</h1>");
            out.println("    <p>Expression: " + element.description() + "</p>");
            out.println("    <p>Frequency: " + frequency + " Hz</p>");
            out.println("    <p>Impedance:  " + impedance.toString() + "</p>");
            out.println("    <p>Magnitude: " + String.format("%.6g", impedance.magnitude()) + " Ω</p>");

            // prepare and sanitize cookie value so it cannot break cookie creation
            String raw = "E:" + expression + "@" + frequencyStr;
            String cookieVal = sanitizeCookieValue(raw);
            Cookie c = new Cookie("lastCalc", cookieVal);
            c.setPath(req.getContextPath().isEmpty() ? "/" : req.getContextPath());
            c.setMaxAge(7 * 24 * 60 * 60);
            try {
                resp.addCookie(c);
            } catch (IllegalArgumentException ex) {
                // cookie contains illegal chars or container rejects it; ignore cookie but continue response
            }

        } catch (NumberFormatException e) {
            out.println("    <h1>Error</h1>");
            out.println("    <p style=\"color: red;\">Invalid number format:  " + e.getMessage() + "</p>");
        } catch (InvalidCircuitException e) {
            out.println("    <h1>Error</h1>");
            out.println("    <p style=\"color: red;\">Calculation error: " + e.getMessage() + "</p>");
        } catch (IllegalArgumentException e) {
            // ExpressionParser.parse may throw IllegalArgumentException for malformed input
            out.println("    <h1>Error</h1>");
            out.println("    <p style=\"color: red;\">Parsing error: " + e.getMessage() + "</p>");
        }

        out.println("    <br><a href=\"" + ctx + "/circuit\">Calculate Another</a>");
        out.println("    <br><a href=\"" + ctx + "/history\">View History</a>");
        out.println("    <br><a href=\"" + ctx + "/\">Back to Home</a>");
        out.println("</body>");
        out.println("</html>");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        processRequest(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        processRequest(req, resp);
    }

    /**
     * Replace characters not safe for Cookie values with underscore.
     * Keeps letters, digits and a small set of safe symbols.
     *
     * @param s input string
     * @return sanitized string safe for Cookie value
     */
    private String sanitizeCookieValue(String s) {
        if (s == null) return "";
        // keep alphanumerics and @ : _ - . and replace everything else with underscore
        return s.replaceAll("[^A-Za-z0-9@:_\\-\\.]", "_");
    }

    /**
     * Remove spaces from a string (kept for backwards compatibility with previous logic).
     *
     * @param s input string
     * @return string without spaces
     */
    private String stripSpaces(String s) {
        return s == null ? "" : s.replaceAll("\\s+", "");
    }
}