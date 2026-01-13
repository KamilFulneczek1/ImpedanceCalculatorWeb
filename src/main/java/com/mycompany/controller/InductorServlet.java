package com.mycompany.controller;

import com.mycompany.model.ImpedanceModel;
import com.mycompany.model.Inductor;
import com.mycompany.model.Complex;
import com.mycompany.model.InvalidCircuitException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import jakarta.servlet.http.Cookie;

/**
 * Servlet handling impedance calculations for inductors.
 *
 * This servlet provides access to inductor impedance calculations using
 * the shared ImpedanceModel instance. Both GET and POST requests are handled
 * uniformly without code duplication by delegating to a common processRequest method. 
 *
 * Cookies:
 * - writes cookies "lastFrequency", "lastComponent" and "lastValue" on successful calculation
 * - reads cookies and displays last-used values on the input form
 *
 * @author Kamil Fulneczek
 * @version 1.2
 */
@WebServlet(name = "InductorServlet", urlPatterns = {"/inductor"})
public class InductorServlet extends HttpServlet {

    private String getContextPath(HttpServletRequest req) {
        return req.getContextPath();
    }

    protected void processRequest(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String inductanceStr = req.getParameter("inductance");
        String frequencyStr = req.getParameter("frequency");

        resp.setContentType("text/html;charset=UTF-8");
        String ctx = getContextPath(req);

        if (inductanceStr == null || frequencyStr == null ||
                inductanceStr.isEmpty() || frequencyStr.isEmpty()) {
            displayForm(req, resp, ctx);
        } else {
            performCalculation(req, resp, ctx, inductanceStr, frequencyStr);
        }
    }

    /**
     * Display the input form for inductor impedance calculation.
     *
     * Reads cookies (if present) and displays a single informational line
     * in format: "Last used: L: 0.01 | frequency: 1000.0 Hz".
     *
     * @param req the HttpServletRequest
     * @param resp the HttpServletResponse
     * @param ctx the context path
     * @throws IOException if I/O error occurs
     */
    private void displayForm(HttpServletRequest req, HttpServletResponse resp, String ctx) throws IOException {
        PrintWriter out = resp.getWriter();

        // read cookies
        String lastFreq = null;
        String lastComp = null;
        String lastVal = null;
        Cookie[] cookies = req.getCookies();
        if (cookies != null) {
            for (Cookie c : cookies) {
                if ("lastFrequency".equals(c.getName())) {
                    lastFreq = URLDecoder.decode(c.getValue(), StandardCharsets.UTF_8);
                } else if ("lastComponent".equals(c.getName())) {
                    lastComp = URLDecoder.decode(c.getValue(), StandardCharsets.UTF_8);
                } else if ("lastValue".equals(c.getName())) {
                    lastVal = URLDecoder.decode(c.getValue(), StandardCharsets.UTF_8);
                }
            }
        }

        out.println("<! DOCTYPE html>");
        out.println("<html lang=\"en\">");
        out.println("<head>");
        out.println("    <meta charset=\"UTF-8\">");
        out.println("    <title>Inductor Impedance Calculator</title>");
        out.println("</head>");
        out.println("<body>");
        out.println("    <h1>Inductor Impedance Calculator</h1>");

        if (lastComp != null || lastVal != null || lastFreq != null) {
            StringBuilder info = new StringBuilder("Last used:");
            boolean added = false;
            if (lastComp != null) {
                info.append(" ").append(lastComp);
                if (lastVal != null) {
                    info.append(": ").append(lastVal);
                }
                added = true;
            } else if (lastVal != null) {
                info.append(" ").append(lastVal);
                added = true;
            }
            if (lastFreq != null) {
                if (added) info.append(" |");
                info.append(" frequency: ").append(lastFreq).append(" Hz");
            }
            out.println("    <p style=\"font-size:small;color:gray;\">" + info.toString() + "</p>");
        }

        out.println("    <form action=\"" + ctx + "/inductor\" method=\"post\">");
        out.println("        <label for=\"inductance\">Inductance (Henrys):</label>");
        out.println("        <input type=\"text\" name=\"inductance\" id=\"inductance\" required><br><br>");
        out.println("        <label for=\"frequency\">Frequency (Hz):</label>");
        out.println("        <input type=\"text\" name=\"frequency\" id=\"frequency\" required><br><br>");
        out.println("        <button type=\"submit\">Calculate</button>");
        out.println("    </form>");
        out.println("    <br><a href=\"" + ctx + "/\">Back to Home</a>");
        out.println("</body>");
        out.println("</html>");
    }

    private void performCalculation(HttpServletRequest req, HttpServletResponse resp, String ctx, String inductanceStr, String frequencyStr)
            throws ServletException, IOException {

        ImpedanceModel model = (ImpedanceModel) getServletContext()
                .getAttribute(AppContextListener.MODEL_ATTRIBUTE);

        if (model == null) {
            throw new ServletException("ImpedanceModel not found in ServletContext");
        }

        PrintWriter out = resp.getWriter();

        out.println("<! DOCTYPE html>");
        out.println("<html lang=\"en\">");
        out.println("<head>");
        out.println("    <meta charset=\"UTF-8\">");
        out.println("    <title>Inductor Impedance Result</title>");
        out.println("</head>");
        out.println("<body>");

        try {
            double inductance = Double.parseDouble(inductanceStr);
            double frequency = Double.parseDouble(frequencyStr);

            Inductor inductor = new Inductor(inductance);
            Complex impedance = model.calculateImpedance(inductor, frequency);

            out.println("    <h1>Inductor Impedance Result</h1>");
            out.println("    <p>Inductance:  " + inductance + " H</p>");
            out.println("    <p>Frequency: " + frequency + " Hz</p>");
            out.println("    <p>Impedance:  " + impedance.toString() + "</p>");
            out.println("    <p>Magnitude: " + String.format("%.6g", impedance.magnitude()) + " Ω</p>");

            // set cookies
            Cookie lastFreq = new Cookie("lastFrequency", URLEncoder.encode(String.valueOf(frequency), StandardCharsets.UTF_8));
            lastFreq.setMaxAge(60 * 60 * 24 * 30);
            String path = req.getContextPath();
            lastFreq.setPath(path == null || path.isEmpty() ? "/" : path);
            resp.addCookie(lastFreq);

            Cookie lastComp = new Cookie("lastComponent", URLEncoder.encode("L", StandardCharsets.UTF_8));
            lastComp.setMaxAge(60 * 60 * 24 * 30);
            lastComp.setPath(path == null || path.isEmpty() ? "/" : path);
            resp.addCookie(lastComp);

            Cookie lastVal = new Cookie("lastValue", URLEncoder.encode(String.valueOf(inductance), StandardCharsets.UTF_8));
            lastVal.setMaxAge(60 * 60 * 24 * 30);
            lastVal.setPath(path == null || path.isEmpty() ? "/" : path);
            resp.addCookie(lastVal);

        } catch (NumberFormatException e) {
            out.println("    <h1>Error</h1>");
            out.println("    <p style=\"color: red;\">Invalid number format:  " + e.getMessage() + "</p>");
        } catch (InvalidCircuitException e) {
            out.println("    <h1>Error</h1>");
            out.println("    <p style=\"color: red;\">Calculation error: " + e.getMessage() + "</p>");
        }

        out.println("    <br><a href=\"" + ctx + "/inductor\">Calculate Another</a>");
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
}