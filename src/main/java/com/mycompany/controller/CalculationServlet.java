package com.mycompany.controller;

import com.mycompany.entities.Calculation;
import com.mycompany.entities.Component;
import jakarta.annotation.Resource;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import jakarta.transaction.UserTransaction;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

@WebServlet(name = "CalculationServlet", urlPatterns = {"/CalculationService", "/sekret/*"})
public class CalculationServlet extends HttpServlet {

    private static final String SECRET_TOKEN = "costam";

    @PersistenceContext(unitName = "my_persistence_unit")
    private EntityManager em;

    @Resource
    private UserTransaction userTransaction;

    private String stackTraceToHtml(Throwable t) {
        if (t == null) return "";
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        t.printStackTrace(pw);
        pw.flush();
        String s = sw.toString();
        return "<pre style='background:#fee;padding:8px;border:1px solid #f00;white-space:pre-wrap;'>" + escapeHtml(s) + "</pre>";
    }

    private String escapeHtml(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    String computeResultPlaceholder(Calculation calc) {
        double sum = 0.0;
        if (calc.getComponents() != null) {
            for (Component c : calc.getComponents()) {
                if (c.getValue() != null) sum += c.getValue();
            }
        }
        return "Sum of component values = " + sum;
    }

    @SuppressWarnings("unchecked")
    private List<Calculation> findCalculationsTransactional() throws Exception {
        userTransaction.begin();
        try {
            Query q = em.createQuery("SELECT c FROM Calculation c");
            List<Calculation> list = q.getResultList();
            userTransaction.commit();
            return list;
        } catch (Exception e) {
            try { userTransaction.rollback(); } catch (Exception ex) { }
            throw e;
        }
    }

    @SuppressWarnings("unchecked")
    private List<Object[]> queryTableTransactional(String tableName) throws Exception {
        userTransaction.begin();
        try {
            List<Object[]> rows = em.createNativeQuery("SELECT * FROM " + tableName).getResultList();
            userTransaction.commit();
            return rows;
        } catch (Exception e) {
            try { userTransaction.rollback(); } catch (Exception ex) { }
            throw e;
        }
    }

    private String getToken(HttpServletRequest request) {
        String pi = request.getPathInfo();
        if (pi == null || pi.length() <= 1) return null;
        String token = pi.substring(1);
        int slash = token.indexOf('/');
        if (slash >= 0) token = token.substring(0, slash);
        return token;
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String servletPath = request.getServletPath();
        String errorHtml = null;

        if ("/sekret".equals(servletPath)) {
            String token = getToken(request);
            if (token == null || !token.equals(SECRET_TOKEN)) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            List<Object[]> calcRows = null;
            List<Object[]> compRows = null;
            try {
                calcRows = queryTableTransactional("CALCULATION");
            } catch (Throwable t) {
                errorHtml = stackTraceToHtml(t);
            }
            try {
                compRows = queryTableTransactional("COMPONENT");
            } catch (Throwable t) {
                errorHtml = (errorHtml == null) ? stackTraceToHtml(t) : errorHtml + stackTraceToHtml(t);
            }

            response.setContentType("text/html;charset=UTF-8");
            try (PrintWriter out = response.getWriter()) {
                out.println("<!DOCTYPE html><html><head><meta charset='utf-8'><title>Secret DB Viewer</title>");
                out.println("<style>table{border-collapse:collapse}td,th{border:1px solid #666;padding:6px}</style></head><body>");
                out.println("<h2>Secret DB Viewer</h2>");
                out.println("<p><em>Ta strona nie jest linkowana w UI. Dostęp tylko po dokładnym URL.</em></p>");

                if (errorHtml != null) {
                    out.println("<h3>Wystąpił wyjątek podczas odczytu tabel:</h3>");
                    out.println(errorHtml);
                }

                out.println("<h3>Table: CALCULATION</h3>");
                if (calcRows == null || calcRows.isEmpty()) {
                    out.println("<p>No rows or table missing.</p>");
                } else {
                    out.println("<table><thead><tr>");
                    int cols = calcRows.get(0).length;
                    for (int i = 0; i < cols; i++) out.println("<th>col" + i + "</th>");
                    out.println("</tr></thead><tbody>");
                    for (Object[] row : calcRows) {
                        out.println("<tr>");
                        for (Object v : row) out.println("<td>" + (v == null ? "" : escapeHtml(v.toString())) + "</td>");
                        out.println("</tr>");
                    }
                    out.println("</tbody></table>");
                }

                out.println("<h3>Table: COMPONENT</h3>");
                if (compRows == null || compRows.isEmpty()) {
                    out.println("<p>No rows or table missing.</p>");
                } else {
                    out.println("<table><thead><tr>");
                    int cols = compRows.get(0).length;
                    for (int i = 0; i < cols; i++) out.println("<th>col" + i + "</th>");
                    out.println("</tr></thead><tbody>");
                    for (Object[] row : compRows) {
                        out.println("<tr>");
                        for (Object v : row) out.println("<td>" + (v == null ? "" : escapeHtml(v.toString())) + "</td>");
                        out.println("</tr>");
                    }
                    out.println("</tbody></table>");
                }

                out.println("</body></html>");
            }
            return;
        }


        String name = request.getParameter("name");
        String freqStr = request.getParameter("frequency");
        String[] compTypes = request.getParameterValues("compType");
        String[] compValues = request.getParameterValues("compValue");

        if (name != null && !name.isBlank() && freqStr != null && !freqStr.isBlank()) {
            Calculation calc = new Calculation();
            calc.setName(name);
            try {
                calc.setFrequency(Double.parseDouble(freqStr));
            } catch (NumberFormatException e) {
                calc.setFrequency(null);
            }

            if (compTypes != null && compValues != null) {
                int n = Math.min(compTypes.length, compValues.length);
                for (int i = 0; i < n; i++) {
                    String t = compTypes[i];
                    String vStr = compValues[i];
                    Double v = null;
                    try {
                        v = Double.parseDouble(vStr);
                    } catch (Exception ex) {
                        v = null;
                    }
                    if (t != null && !t.isBlank()) {
                        Component c = new Component();
                        c.setType(t);
                        c.setValue(v);
                        calc.addComponent(c);
                    }
                }
            }

            calc.setResult(computeResultPlaceholder(calc));

            try {
                userTransaction.begin();
                em.persist(calc);
                userTransaction.commit();
            } catch (Throwable t) {
                try { userTransaction.rollback(); } catch (Exception ex) { }
                errorHtml = stackTraceToHtml(t);
            }
        }

        List<Calculation> list = null;
        try {
            list = findCalculationsTransactional();
        } catch (Throwable t) {
            errorHtml = (errorHtml == null) ? stackTraceToHtml(t) : errorHtml + stackTraceToHtml(t);
        }

        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            out.println("<!DOCTYPE html><html><head><title>Calculation Service</title></head><body>");
            out.println("<h2>Create Calculation</h2>");
            if (errorHtml != null) {
                out.println("<div style='background:#fee;border:1px solid #f00;padding:8px;margin:8px;'><strong>Wystąpił błąd:</strong>");
                out.println(errorHtml);
                out.println("</div>");
            }

            out.println("<form method='post' action='CalculationService'>");
            out.println("Name: <input name='name'/><br/>");
            out.println("Frequency (Hz): <input name='frequency'/><br/>");
            out.println("Components (add multiple by repeating fields):<br/>");
            out.println("Type: <input name='compType'/> Value: <input name='compValue'/><br/>");
            out.println("<input type='submit' value='Create'/>");
            out.println("</form>");

            out.println("<p><a href='sekret/" + SECRET_TOKEN + "'>Prywatny DB Viewer</a></p>");

            out.println("<h2>Existing Calculations</h2>");
            if (list == null || list.isEmpty()) {
                out.println("<p>No calculations found.</p>");
            } else {
                int calcIndex = 1;
                for (Calculation c : list) {
                    out.println("<div style='margin-bottom:12px;'>");
                    out.println("<strong>#" + calcIndex + " — " + escapeHtml(c.getName()) + "</strong><br/>");
                    out.println("DB id: " + (c.getId() == null ? "<em>n/a</em>" : escapeHtml(c.getId().toString())) + "<br/>");
                    out.println("Frequency: " + (c.getFrequency() == null ? "<em>n/a</em>" : c.getFrequency()) + " Hz<br/>");
                    out.println("Result: " + escapeHtml(c.getResult()) + "<br/>");
                    out.println("Components:<ul>");
                    if (c.getComponents() != null && !c.getComponents().isEmpty()) {
                        int compIndex = 1;
                        for (Component comp : c.getComponents()) {
                            out.println("<li>" + compIndex + ". " + escapeHtml(comp.getType()) + " — " +
                                    (comp.getValue() == null ? "<em>n/a</em>" : escapeHtml(comp.getValue().toString())) + "</li>");
                            compIndex++;
                        }
                    } else {
                        out.println("<li><em>brak komponentów</em></li>");
                    }
                    out.println("</ul>");
                    out.println("</div>");
                    calcIndex++;
                }
            }

            out.println("</body></html>");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
}