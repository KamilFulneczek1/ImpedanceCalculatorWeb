package com.mycompany.controller;

import com.mycompany.model.Inductor;
import com.mycompany.model.Complex;
import com.mycompany.model.InvalidCircuitException;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(name = "InductorServlet", urlPatterns = "/inductor")
public class InductorServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("text/html");
        resp.getWriter().write(
            "<form action='/inductor' method='post'>" +
            "<label>Inductance (Henries): <input name='inductance' type='number' step='0.0001' required /></label><br />" +
            "<label>Frequency (Hz): <input name='frequency' type='number' required /></label><br />" +
            "<button type='submit'>Calculate</button>" +
            "</form>"
        );
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        double inductance = Double.parseDouble(req.getParameter("inductance"));
        double frequency = Double.parseDouble(req.getParameter("frequency"));

        resp.setContentType("text/html");

        try {
            Inductor inductor = new Inductor(inductance);
            Complex impedance = inductor.getImpedance(frequency);
            
            resp.getWriter().write(
                "<h1>Inductor Impedance Result</h1>" +
                "<p>Inductance: " + inductance + " Henries</p>" +
                "<p>Impedance: " + impedance.toString() + "</p>" +
                "<p>Magnitude: " + impedance.magnitude() + " Ohms</p>" +
                "<a href='/'>Back to Home</a>"
            );
        } catch (InvalidCircuitException e) {
            resp.getWriter().write(
                "<h1>Error Calculating Impedance</h1>" +
                "<p>" + e.getMessage() + "</p>" +
                "<a href='/'>Back to Home</a>"
            );
        }
    }
}