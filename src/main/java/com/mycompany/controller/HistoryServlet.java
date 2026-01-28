package com.mycompany.controller;

import com.mycompany.model.ImpedanceModel;
import com.mycompany.model.CircuitElement;
import com.mycompany.model.Complex;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * Servlet providing access to the calculation history stored in the model.
 *
 * This servlet displays all past impedance calculations performed during
 * the application lifecycle. It also provides functionality to clear the history.
 * Both GET and POST requests are handled uniformly.
 *
 * History data is obtained from the shared {@link ImpedanceModel} instance
 * stored in the servlet context by {@link AppContextListener}.
 *
 * @author Kamil Fulneczek
 * @version 1.0
 */
@WebServlet(name = "HistoryServlet", urlPatterns = {"/history"})
public class HistoryServlet extends HttpServlet {

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
     * If the "action=clear" parameter is present the model history is cleared.
     *
     * @param req the HttpServletRequest
     * @param resp the HttpServletResponse
     * @throws ServletException if model missing or another servlet error occurs
     * @throws IOException if an I/O error occurs while writing response
     */
    protected void processRequest(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        ImpedanceModel model = (ImpedanceModel) getServletContext()
                .getAttribute(AppContextListener.MODEL_ATTRIBUTE);

        if (model == null) {
            throw new ServletException("ImpedanceModel not found in ServletContext");
        }

        String action = req.getParameter("action");
        if ("clear".equals(action)) {
            model.clearHistory();
        }

        String ctx = getContextPath(req);

        resp.setContentType("text/html;charset=UTF-8");
        PrintWriter out = resp.getWriter();

        out.println("<!DOCTYPE html>");
        out.println("<html lang=\"en\">");
        out.println("<head>");
        out.println("    <meta charset=\"UTF-8\">");
        out.println("    <title>Calculation History</title>");
        out.println("</head>");
        out.println("<body>");
        out.println("    <h1>Calculation History</h1>");

        List<CircuitElement> elements = model.getHistoryElements();
        List<Double> frequencies = model.getHistoryFrequencies();
        List<Complex> results = model.getHistoryResults();

        if (elements.isEmpty()) {
            out.println("    <p>No calculations performed yet.</p>");
        } else {
            out.println("    <table border=\"1\">");
            out.println("        <tr>");
            out.println("            <th>No.</th>");
            out.println("            <th>Circuit</th>");
            out.println("            <th>Frequency [Hz]</th>");
            out.println("            <th>Impedance</th>");
            out.println("            <th>Magnitude [Ω]</th>");
            out.println("        </tr>");

            for (int i = 0; i < elements.size(); i++) {
                out.println("        <tr>");
                out.println("            <td>" + (i + 1) + "</td>");
                out.println("            <td>" + elements.get(i).description() + "</td>");
                out.println("            <td>" + frequencies.get(i) + "</td>");
                out.println("            <td>" + results.get(i).toString() + "</td>");
                out.println("            <td>" + String.format("%.6g", results.get(i).magnitude()) + "</td>");
                out.println("        </tr>");
            }

            out.println("    </table>");
            out.println("    <br>");
            out.println("    <form action=\"" + ctx + "/history\" method=\"post\">");
            out.println("        <input type=\"hidden\" name=\"action\" value=\"clear\">");
            out.println("        <button type=\"submit\">Clear History</button>");
            out.println("    </form>");
        }

        out.println("    <br><a href=\"" + ctx + "/\">Back to Home</a>");
        out.println("</body>");
        out.println("</html>");
    }

    /**
     * Delegate GET to processRequest.
     *
     * @param req request
     * @param resp response
     * @throws ServletException on servlet error
     * @throws IOException on I/O error
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        processRequest(req, resp);
    }

    /**
     * Delegate POST to processRequest.
     *
     * @param req request
     * @param resp response
     * @throws ServletException on servlet error
     * @throws IOException on I/O error
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        processRequest(req, resp);
    }
}