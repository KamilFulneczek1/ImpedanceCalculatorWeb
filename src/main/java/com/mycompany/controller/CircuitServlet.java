package com.mycompany.controller;

import com.mycompany.model.ImpedanceModel;
import com.mycompany.model.CircuitElement;
import com.mycompany.model.Complex;
import com.mycompany.model.ExpressionParser;
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
 * Servlet handling impedance calculations for complex circuit expressions.
 *
 * This servlet provides access to the full circuit expression parsing functionality
 * using the ExpressionParser class. It supports nested series and parallel connections
 * and displays results page with impedance and magnitude.
 *
 * Cookies are used similarly as in other component servlets; additionally the
 * entire expression is stored in lastValue (encoded).
 *
 * @author Kamil Fulneczek
 * @version 1.2
 */
@WebServlet(name = "CircuitServlet", urlPatterns = {"/circuit"})
public class CircuitServlet extends HttpServlet {

    /**
     * Return application context path.
     *
     * @param req the request
     * @return context path string
     */
    private String getContextPath(HttpServletRequest req) {
        return req.getContextPath();
    }

    /**
     * Unified entry point for GET and POST. Shows form when parameters missing,
     * otherwise parses expression and performs calculation.
     *
     * @param req HTTP request
     * @param resp HTTP response
     * @throws ServletException on servlet errors
     * @throws IOException on I/O errors while writing response
     */
    protected void processRequest(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String expression = req.getParameter("expression");
        String frequencyStr = req.getParameter("frequency");

        resp.setContentType("text/html;charset=UTF-8");
        String ctx = getContextPath(req);

        if (expression == null || frequencyStr == null ||
                expression.isEmpty() || frequencyStr.isEmpty()) {
            displayForm(req, resp, ctx);
        } else {
            performCalculation(req, resp, ctx, expression, frequencyStr);
        }
    }

    /**
     * Render the input form and display cookie info in the format:
     * "Last used: EXPR: series(R:100,...) | frequency: 1000.0 Hz".
     *
     * @param req HTTP request (for cookie retrieval)
     * @param resp HTTP response (for writer)
     * @param ctx application context path
     * @throws IOException if writing response fails
     */
    private void displayForm(HttpServletRequest req, HttpServletResponse resp, String ctx) throws IOException {
        PrintWriter out = resp.getWriter();

        String lastFreq = null;
        String lastComp = null;
        String lastVal = null;
        Cookie[] cookies = req.getCookies();
        if (cookies != null) {
            for (Cookie c : cookies) {
                if (null != c.getName()) switch (c.getName()) {
                    case "lastFrequency":
                        lastFreq = URLDecoder.decode(c.getValue(), StandardCharsets.UTF_8);
                        break;
                    case "lastComponent":
                        lastComp = URLDecoder.decode(c.getValue(), StandardCharsets.UTF_8);
                        break;
                    case "lastValue":
                        lastVal = URLDecoder.decode(c.getValue(), StandardCharsets.UTF_8);
                        break;
                    default:
                        break;
                }
            }
        }

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
        out.println("        <li>Single component: \"R: 100\", \"C:1e-6\", \"L:0.01\"</li>");
        out.println("        <li>Series connection: series(component1, component2, ...)</li>");
        out.println("        <li>Parallel connection: parallel(component1, component2, ...)</li>");
        out.println("    </ul>");
        out.println("    <p>Example: series(R:100, parallel(C:1e-6, L: 0.01), R:50)</p>");

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

        out.println("    <form action=\"" + ctx + "/circuit\" method=\"post\">");
        out.println("        <label for=\"expression\">Circuit Expression:</label><br>");
        out.println("        <input type=\"text\" name=\"expression\" id=\"expression\" size=\"60\" required><br><br>");
        out.println("        <label for=\"frequency\">Frequency (Hz):</label>");
        out.println("        <input type=\"text\" name=\"frequency\" id=\"frequency\" required><br><br>");
        out.println("        <button type=\"submit\">Calculate</button>");
        out.println("    </form>");
        out.println("    <br><a href=\"" + ctx + "/\">Back to Home</a>");
        out.println("</body>");
        out.println("</html>");
    }

    /**
     * Parse expression, compute impedance and set cookies with used parameters.
     *
     * @param req HTTP request
     * @param resp HTTP response
     * @param ctx application context path
     * @param expression textual circuit expression provided by user
     * @param frequencyStr frequency string provided by user
     * @throws ServletException if model is not present
     * @throws IOException if writing response fails
     */
    private void performCalculation(HttpServletRequest req, HttpServletResponse resp, String ctx, String expression, String frequencyStr)
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
        out.println("    <title>Circuit Impedance Result</title>");
        out.println("</head>");
        out.println("<body>");

        try {
            double frequency = Double.parseDouble(frequencyStr);
            CircuitElement element = ExpressionParser.parse(expression);
            Complex impedance = model.calculateImpedance(element, frequency);

            out.println("    <h1>Circuit Impedance Result</h1>");
            out.println("    <p>Expression: " + expression + "</p>");
            out.println("    <p>Parsed circuit:  " + element.description() + "</p>");
            out.println("    <p>Frequency: " + frequency + " Hz</p>");
            out.println("    <p>Impedance:  " + impedance.toString() + "</p>");
            out.println("    <p>Magnitude:  " + String.format("%.6g", impedance.magnitude()) + " Ω</p>");

            Cookie lastFreq = new Cookie("lastFrequency", URLEncoder.encode(String.valueOf(frequency), StandardCharsets.UTF_8));
            lastFreq.setMaxAge(60 * 60 * 24 * 30);
            String path = req.getContextPath();
            lastFreq.setPath(path == null || path.isEmpty() ? "/" : path);
            resp.addCookie(lastFreq);

            Cookie lastComp = new Cookie("lastComponent", URLEncoder.encode("EXPR", StandardCharsets.UTF_8));
            lastComp.setMaxAge(60 * 60 * 24 * 30);
            lastComp.setPath(path == null || path.isEmpty() ? "/" : path);
            resp.addCookie(lastComp);

            Cookie lastVal = new Cookie("lastValue", URLEncoder.encode(expression, StandardCharsets.UTF_8));
            lastVal.setMaxAge(60 * 60 * 24 * 30);
            lastVal.setPath(path == null || path.isEmpty() ? "/" : path);
            resp.addCookie(lastVal);

        } catch (NumberFormatException e) {
            out.println("    <h1>Error</h1>");
            out.println("    <p style=\"color: red;\">Invalid frequency format: " + e.getMessage() + "</p>");
        } catch (IllegalArgumentException e) {
            out.println("    <h1>Error</h1>");
            out.println("    <p style=\"color: red;\">Invalid expression:  " + e.getMessage() + "</p>");
        } catch (InvalidCircuitException e) {
            out.println("    <h1>Error</h1>");
            out.println("    <p style=\"color: red;\">Calculation error: " + e.getMessage() + "</p>");
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
}