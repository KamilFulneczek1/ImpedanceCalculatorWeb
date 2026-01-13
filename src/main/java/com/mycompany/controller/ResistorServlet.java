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
 * The servlet displays a form for entering resistance and frequency and
 * shows calculated impedance and magnitude. Both GET and POST are handled
 * by delegating to {@link #processRequest(HttpServletRequest, HttpServletResponse)}.
 *
 * This servlet also demonstrates cookies usage: it reads cookies named
 * "lastFrequency", "lastComponent" and "lastValue" and displays a compact
 * informational line; after a successful calculation it writes these cookies
 * so the values are available on subsequent visits.
 *
 * Note: the servlet obtains the shared {@link ImpedanceModel} instance from
 * the servlet context attribute named {@link AppContextListener#MODEL_ATTRIBUTE}.
 *
 * @author Kamil Fulneczek
 * @version 1.2
 */
@WebServlet(name = "ResistorServlet", urlPatterns = {"/resistor"})
public class ResistorServlet extends HttpServlet {

    /**
     * Return the servlet context path to be used when building links or forms.
     *
     * @param req the HttpServletRequest
     * @return the context path string (never null, may be empty)
     */
    private String getContextPath(HttpServletRequest req) {
        return req.getContextPath();
    }

    /**
     * Main dispatcher used for both GET and POST.
     * If required parameters are missing the input form is shown, otherwise
     * the calculation is performed.
     *
     * @param req the HttpServletRequest
     * @param resp the HttpServletResponse
     * @throws ServletException on servlet error (e.g. model missing)
     * @throws IOException on I/O error when writing the response
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
     * Render the input form for resistor calculation.
     * Reads cookies (if present) and prints a single small informational
     * line in the format "Last used: R: 15 | frequency: 40.0 Hz".
     *
     * @param req the HttpServletRequest used for cookie access
     * @param resp the HttpServletResponse used to obtain writer
     * @param ctx the application context path used to build the form action
     * @throws IOException if an I/O error occurs while writing response
     */
    private void displayForm(HttpServletRequest req, HttpServletResponse resp, String ctx) throws IOException {
        PrintWriter out = resp.getWriter();

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
     * Perform the impedance calculation and render the result page.
     * On successful calculation this method also writes three cookies:
     * - lastFrequency: frequency used (encoded)
     * - lastComponent: short component code ("R")
     * - lastValue: provided value for the component
     *
     * @param req the HttpServletRequest used to read context and build cookie path
     * @param resp the HttpServletResponse used to write output and add cookies
     * @param ctx the context path used for links in the result page
     * @param resistanceStr the resistance value as provided by user (string form)
     * @param frequencyStr the frequency value as provided by user (string form)
     * @throws ServletException if the shared ImpedanceModel is not found in ServletContext
     * @throws IOException if an I/O error occurs while writing response
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

            Cookie lastFreq = new Cookie("lastFrequency", URLEncoder.encode(String.valueOf(frequency), StandardCharsets.UTF_8));
            lastFreq.setMaxAge(60 * 60 * 24 * 30);
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
     * Delegate GET to the unified request processor.
     *
     * @param req the HttpServletRequest
     * @param resp the HttpServletResponse
     * @throws ServletException on servlet error
     * @throws IOException on I/O error
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        processRequest(req, resp);
    }

    /**
     * Delegate POST to the unified request processor.
     *
     * @param req the HttpServletRequest
     * @param resp the HttpServletResponse
     * @throws ServletException on servlet error
     * @throws IOException on I/O error
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        processRequest(req, resp);
    }
}