package com.mycompany.controller;

import com.mycompany.model.ImpedanceModel;
import com.mycompany.model.Resistor;
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
 * Servlet handling impedance calculations for resistors. 
 *
 * This servlet provides access to resistor impedance calculations using
 * the shared ImpedanceModel instance.  Both GET and POST requests are handled
 * uniformly without code duplication by delegating to a common processRequest method.
 *
 * Cookies:
 * - writes cookies "lastFrequency", "lastComponent" and "lastValue" on successful calculation
 * - reads cookies and displays last-used values on the input form
 *
 * @author Kamil Fulneczek
 * @version 1.2
 */
@WebServlet(name = "ResistorServlet", urlPatterns = {"/resistor"})
public class ResistorServlet extends HttpServlet {

    /**
     * Get the context path for building URLs.
     *
     * @param req the HttpServletRequest
     * @return context path string
     */
    private String getContextPath(HttpServletRequest req) {
        return req.getContextPath();
    }

    /**
     * Process the request for both GET and POST methods. 
     *
     * If resistance and frequency parameters are provided, performs the calculation. 
     * Otherwise, displays the input form.
     *
     * @param req the HttpServletRequest
     * @param resp the HttpServletResponse
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String resistanceStr = req.getParameter("resistance");
        String frequencyStr = req.getParameter("frequency");

        resp.setContentType("text/html;charset=UTF-8");

        String ctx = getContextPath(req);

        if (resistanceStr == null || frequencyStr == null ||
                resistanceStr.isEmpty() || frequencyStr.isEmpty()) {
            displayForm(req, resp, ctx);
        } else {
            performCalculation(req, resp, ctx, resistanceStr, frequencyStr);
        }
    }

    /**
     * Display the input form for resistor impedance calculation.
     *
     * Reads cookies (if present) and displays a small informational line
     * with last used component/frequency/value. The rest of the form layout remains unchanged.
     *
     * @param req the HttpServletRequest (used to read cookies)
     * @param resp the HttpServletResponse (used to obtain writer)
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
        out.println("    <title>Resistor Impedance Calculator</title>");
        out.println("</head>");
        out.println("<body>");
        out.println("    <h1>Resistor Impedance Calculator</h1>");

        // new unified cookie display format: "Last used: R: 15 | frequency: 40.0 Hz"
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

        out.println("    <form action=\"" + ctx + "/resistor\" method=\"post\">");
        out.println("        <label for=\"resistance\">Resistance (Ohms):</label>");
        out.println("        <input type=\"text\" name=\"resistance\" id=\"resistance\" required><br><br>");
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
     *
     * On successful calculation, three cookies are written:
     * - lastFrequency: frequency used for calculation
     * - lastComponent: "R" for resistor
     * - lastValue: numeric value provided for resistor
     *
     * @param req the HttpServletRequest
     * @param resp the HttpServletResponse
     * @param ctx the context path
     * @param resistanceStr the resistance value as string
     * @param frequencyStr the frequency value as string
     * @throws ServletException if the model is not found
     * @throws IOException if an I/O error occurs
     */
    private void performCalculation(HttpServletRequest req, HttpServletResponse resp, String ctx, String resistanceStr, String frequencyStr)
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
        out.println("    <title>Resistor Impedance Result</title>");
        out.println("</head>");
        out.println("<body>");

        try {
            double resistance = Double.parseDouble(resistanceStr);
            double frequency = Double.parseDouble(frequencyStr);

            Resistor resistor = new Resistor(resistance);
            Complex impedance = model.calculateImpedance(resistor, frequency);

            out.println("    <h1>Resistor Impedance Result</h1>");
            out.println("    <p>Resistance: " + resistance + " Ω</p>");
            out.println("    <p>Frequency: " + frequency + " Hz</p>");
            out.println("    <p>Impedance:  " + impedance.toString() + "</p>");
            out.println("    <p>Magnitude: " + String.format("%.6g", impedance.magnitude()) + " Ω</p>");

            // set cookies (URL-encoded to avoid spaces/commas)
            Cookie lastFreq = new Cookie("lastFrequency", URLEncoder.encode(String.valueOf(frequency), StandardCharsets.UTF_8));
            lastFreq.setMaxAge(60 * 60 * 24 * 30); // 30 days
            String path = req.getContextPath();
            lastFreq.setPath(path == null || path.isEmpty() ? "/" : path);
            resp.addCookie(lastFreq);

            Cookie lastComp = new Cookie("lastComponent", URLEncoder.encode("R", StandardCharsets.UTF_8));
            lastComp.setMaxAge(60 * 60 * 24 * 30);
            lastComp.setPath(path == null || path.isEmpty() ? "/" : path);
            resp.addCookie(lastComp);

            Cookie lastVal = new Cookie("lastValue", URLEncoder.encode(String.valueOf(resistance), StandardCharsets.UTF_8));
            lastVal.setMaxAge(60 * 60 * 24 * 30);
            lastVal.setPath(path == null || path.isEmpty() ? "/" : path);
            resp.addCookie(lastVal);

        } catch (NumberFormatException e) {
            out.println("    <h1>Error</h1>");
            out.println("    <p style=\"color: red;\">Invalid number format: " + e.getMessage() + "</p>");
        } catch (InvalidCircuitException e) {
            out.println("    <h1>Error</h1>");
            out.println("    <p style=\"color: red;\">Calculation error: " + e.getMessage() + "</p>");
        }

        out.println("    <br><a href=\"" + ctx + "/resistor\">Calculate Another</a>");
        out.println("    <br><a href=\"" + ctx + "/history\">View History</a>");
        out.println("    <br><a href=\"" + ctx + "/\">Back to Home</a>");
        out.println("</body>");
        out.println("</html>");
    }

    /**
     * Handle GET requests by delegating to processRequest. 
     *
     * @param req the HttpServletRequest
     * @param resp the HttpServletResponse
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        processRequest(req, resp);
    }

    /**
     * Handle POST requests by delegating to processRequest. 
     *
     * @param req the HttpServletRequest
     * @param resp the HttpServletResponse
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        processRequest(req, resp);
    }
}