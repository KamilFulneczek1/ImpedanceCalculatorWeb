package com.mycompany. controller;

import com.mycompany. model.ImpedanceModel;
import com.mycompany.model. CircuitElement;
import com.mycompany.model.Complex;
import com. mycompany.model.ExpressionParser;
import com. mycompany.model.InvalidCircuitException;

import jakarta.servlet.ServletException;
import jakarta. servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * Servlet handling impedance calculations for complex circuit expressions.
 *
 * This servlet provides access to the full circuit expression parsing functionality
 * using the ExpressionParser class. It supports nested series and parallel connections
 * with any combination of resistors, capacitors, and inductors.
 *
 * Supported expression formats:
 * <ul>
 *   <li>Single component: "R: 100", "C:1e-6", "L:0.01"</li>
 *   <li>Series connection: "series(R:100, C:1e-6)"</li>
 *   <li>Parallel connection: "parallel(R:100, R:200)"</li>
 *   <li>Nested expression: "series(R:100, parallel(C:1e-6, L:0.01), R:50)"</li>
 * </ul>
 *
 * Both GET and POST requests are handled uniformly without code duplication
 * by delegating to a common processRequest method. 
 *
 * @author Kamil Fulneczek
 * @version 1.0
 */
@WebServlet(name = "CircuitServlet", urlPatterns = {"/circuit"})
public class CircuitServlet extends HttpServlet {

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
     * If expression and frequency parameters are provided, performs the calculation.
     * Otherwise, displays the input form.
     *
     * @param req the HttpServletRequest
     * @param resp the HttpServletResponse
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
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
            performCalculation(out, ctx, expression, frequencyStr);
        }
    }

    /**
     * Display the input form for circuit expression impedance calculation.
     *
     * @param out the PrintWriter to write to
     * @param ctx the context path
     */
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
        out.println("        <li>Resistor: R: value (e.g., R:100)</li>");
        out.println("        <li>Capacitor: C:value (e. g., C:1e-6)</li>");
        out.println("        <li>Inductor:  L:value (e.g., L: 0.01)</li>");
        out.println("        <li>Series connection: series(component1, component2, ...)</li>");
        out.println("        <li>Parallel connection: parallel(component1, component2, ...)</li>");
        out.println("    </ul>");
        out.println("    <p>Example: series(R:100, parallel(C:1e-6, L: 0.01), R:50)</p>");
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
     * Perform the impedance calculation and display the result. 
     *
     * @param out the PrintWriter to write to
     * @param ctx the context path
     * @param expression the circuit expression string
     * @param frequencyStr the frequency value as string
     * @throws ServletException if the model is not found
     */
    private void performCalculation(PrintWriter out, String ctx, String expression, String frequencyStr)
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
            out.println("    <p>Impedance:  " + impedance. toString() + "</p>");
            out.println("    <p>Magnitude:  " + String.format("%.6g", impedance. magnitude()) + " Ω</p>");
        } catch (NumberFormatException e) {
            out. println("    <h1>Error</h1>");
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