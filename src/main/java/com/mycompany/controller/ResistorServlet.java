package com.mycompany.controller;

import com.mycompany.model.Resistor;
import com.mycompany.model.Complex;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(name = "ResistorServlet", urlPatterns = "/resistor")
public class ResistorServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("text/html");
        resp.getWriter().write(
            "<form action='/resistor' method='post'>" +
            "<label>Resistance (Ohms): <input name='resistance' type='number' step='0.01' required /></label><br />" +
            "<label>Frequency (Hz): <input name='frequency' type='number' required /></label><br />" +
            "<button type='submit'>Calculate</button>" +
            "</form>"
        );
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        double resistance = Double.parseDouble(req.getParameter("resistance"));
        double frequency = Double.parseDouble(req.getParameter("frequency"));

        Resistor resistor = new Resistor(resistance);
        Complex impedance = resistor.getImpedance(frequency);

        resp.setContentType("text/html");
        resp.getWriter().write(
            "<h1>Resistor Impedance Result</h1>" +
            "<p>Resistance: " + resistance + " Ohms</p>" +
            "<p>Impedance: " + impedance.toString() + "</p>" +
            "<p>Magnitude: " + impedance.magnitude() + " Ohms</p>" +
            "<a href='/'>Back to Home</a>"
        );
    }
}