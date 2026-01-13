package com.mycompany.controller;

import com.mycompany.model.ImpedanceModel;
import com.mycompany.model.Capacitor;
import com.mycompany.model.Complex;
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
 * Servlet handling impedance calculations for capacitors.
 *
 * Both GET and POST delegate to processRequest to avoid duplication.
 * Writes a sanitized cookie "lastCalc" on successful calculation (does not change HTML output).
 *
 * @author Kamil Fulneczek
 * @version 1.2
 */
@WebServlet(name = "CapacitorServlet", urlPatterns = {"/capacitor"})
public class CapacitorServlet extends HttpServlet {

    private String getContextPath(HttpServletRequest req) {
        return req.getContextPath();
    }

    protected void processRequest(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String capacitanceStr = req.getParameter("capacitance");
        String frequencyStr = req.getParameter("frequency");

        resp.setContentType("text/html;charset=UTF-8");
        PrintWriter out = resp.getWriter();

        String ctx = getContextPath(req);

        if (capacitanceStr == null || frequencyStr == null ||
                capacitanceStr.isEmpty() || frequencyStr.isEmpty()) {
            displayForm(out, ctx);
        } else {
            performCalculation(out, ctx, req, resp, capacitanceStr, frequencyStr);
        }
    }

    private void displayForm(PrintWriter out, String ctx) {
        out.println("<! DOCTYPE html>");
        out.println("<html lang=\"en\">");
        out.println("<head>");
        out.println("    <meta charset=\"UTF-8\">");
        out.println("    <title>Capacitor Impedance Calculator</title>");
        out.println("</head>");
        out.println("<body>");
        out.println("    <h1>Capacitor Impedance Calculator</h1>");
        out.println("    <form action=\"" + ctx + "/capacitor\" method=\"post\">");
        out.println("        <label for=\"capacitance\">Capacitance (Farads):</label>");
        out.println("        <input type=\"text\" name=\"capacitance\" id=\"capacitance\" required><br><br>");
        out.println("        <label for=\"frequency\">Frequency (Hz):</label>");
        out.println("        <input type=\"text\" name=\"frequency\" id=\"frequency\" required><br><br>");
        out.println("        <button type=\"submit\">Calculate</button>");
        out.println("    </form>");
        out.println("    <br><a href=\"" + ctx + "/\">Back to Home</a>");
        out.println("</body>");
        out.println("</html>");
    }

    /**
     * Perform the impedance calculation and display the result.
     * Visible output is unchanged from the original.
     * Cookie writing is sanitized and wrapped in try/catch to avoid breaking response.
     */
    private void performCalculation(PrintWriter out, String ctx, HttpServletRequest req, HttpServletResponse resp, String capacitanceStr, String frequencyStr)
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
        out.println("    <title>Capacitor Impedance Result</title>");
        out.println("</head>");
        out.println("<body>");

        try {
            double capacitance = Double.parseDouble(capacitanceStr);
            double frequency = Double.parseDouble(frequencyStr);

            Capacitor capacitor = new Capacitor(capacitance);
            Complex impedance = model.calculateImpedance(capacitor, frequency);

            out.println("    <h1>Capacitor Impedance Result</h1>");
            out.println("    <p>Capacitance: " + capacitance + " F</p>");
            out.println("    <p>Frequency: " + frequency + " Hz</p>");
            out.println("    <p>Impedance:  " + impedance.toString() + "</p>");
            out.println("    <p>Magnitude: " + String.format("%.6g", impedance.magnitude()) + " Ω</p>");

            // prepare cookie value and sanitize it so it cannot break cookie creation
            String raw = "C:" + capacitanceStr + "@" + frequencyStr;
            String cookieVal = sanitizeCookieValue(raw);
            Cookie c = new Cookie("lastCalc", cookieVal);
            c.setPath(req.getContextPath().isEmpty() ? "/" : req.getContextPath());
            c.setMaxAge(7 * 24 * 60 * 60);
            try {
                resp.addCookie(c);
            } catch (IllegalArgumentException ex) {
                // ignore cookie errors to avoid interrupting response
            }

        } catch (NumberFormatException e) {
            out.println("    <h1>Error</h1>");
            out.println("    <p style=\"color: red;\">Invalid number format:  " + e.getMessage() + "</p>");
        } catch (InvalidCircuitException e) {
            out.println("    <h1>Error</h1>");
            out.println("    <p style=\"color: red;\">Calculation error: " + e.getMessage() + "</p>");
        }

        out.println("    <br><a href=\"" + ctx + "/capacitor\">Calculate Another</a>");
        out.println("    <br><a href=\"" + ctx + "/history\">View History</a>");
        out.println("    <br><a href=\"" + ctx + "/\">Back to Home</a>");
        out.println("</body>");
        out.println("</html>");
    }

    private String sanitizeCookieValue(String s) {
        if (s == null) return "";
        return s.replaceAll("[^A-Za-z0-9@:_\\-\\.]", "_");
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
}