package com.mycompany.controller;

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
        resp.getWriter().write("<h1>Resistor Impedance Calculator</h1>");
        resp.getWriter().write("<p>Feature under development: Resistor impedance input will be here soon.</p>");
    }
}