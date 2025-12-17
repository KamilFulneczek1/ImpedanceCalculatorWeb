package com.mycompany.controller;

import com.mycompany.model.Capacitor;
import com.mycompany.model.Complex;
import com.mycompany.model.InvalidCircuitException;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(name = "CapacitorServlet", urlPatterns = "/capacitor")
public class CapacitorServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("text/html");
        resp.getWriter().write(
            "<form action='/capacitor' method='post'>" +
            "<label>Capacitance (Farads): <input name='capacitance' type='number' step='0.000001' required /></label><br />" +
            "<label>Frequency (Hz): <input name='frequency' type='number' required /></label><br />" +
            "<button type='submit'>Calculate</button>" +
            "</form>"
        );
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        double capacitance = Double.parseDouble(req.getParameter("capacitance"));
        double frequency = Double.parseDouble(req.getParameter("frequency"));

        resp.setContentType("text/html");

        try {
            Capacitor capacitor = new Capacitor(capacitance);
            Complex impedance = capacitor.getImpedance(frequency);

            resp.getWriter().write(
                "<h1>Capacitor Impedance Result</h1>" +
                "<p>Capacitance: " + capacitance + " Farads</p>" +
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