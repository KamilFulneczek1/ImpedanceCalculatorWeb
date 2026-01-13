package com.mycompany.controller;

import com.mycompany. model.ImpedanceModel;
import com.mycompany.model. Resistor;
import com.mycompany.model.Complex;
import com. mycompany.model.InvalidCircuitException;

import jakarta.servlet.ServletException;
import jakarta. servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io. IOException;
import java.io.PrintWriter;

/**
 * Servlet handling impedance calculations for resistors. 
 *
 * This servlet provides access to resistor impedance calculations using
 * the shared ImpedanceModel instance.  Both GET and POST requests are handled
 * uniformly without code duplication by delegating to a common processRequest method.
 *
 * @author Kamil Fulneczek
 * @version 1.1
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
        PrintWriter out = resp.getWriter();

        String ctx = getContextPath(req);

        if (resistanceStr == null || frequencyStr == null ||
                resistanceStr.isEmpty() || frequencyStr.isEmpty()) {
            displayForm(out, ctx);
        } else {
            performCalculation(out, ctx, resistanceStr, frequencyStr);
        }
    }

    /**
     * Display the input form for resistor impedance calculation.
     *
     * @param out the PrintWriter to write to
     * @param ctx the context path
     */
    private void displayForm(PrintWriter out, String ctx) {
        out.println("<! DOCTYPE html>");
        out.println("<html lang=\"en\">");
        out.println("<head>");
        out.println("    <meta charset=\"UTF-8\">");
        out.println("    <title>Resistor Impedance Calculator</title>");
        out.println("</head>");
        out.println("<body>");
        out.println("    <h1>Resistor Impedance Calculator</h1>");
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
     * @param out the PrintWriter to write to
     * @param ctx the context path
     * @param resistanceStr the resistance value as string
     * @param frequencyStr the frequency value as string
     * @throws ServletException if the model is not found
     */
    private void performCalculation(PrintWriter out, String ctx, String resistanceStr, String frequencyStr)
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
            out.println("    <p>Impedance:  " + impedance. toString() + "</p>");
            out.println("    <p>Magnitude: " + String.format("%.6g", impedance.magnitude()) + " Ω</p>");
        } catch (NumberFormatException e) {
            out. println("    <h1>Error</h1>");
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