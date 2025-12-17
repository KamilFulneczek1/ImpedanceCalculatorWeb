package com.mycompany.controller;

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
        resp.getWriter().write("<h1>Inductor Impedance Calculator</h1>");
        resp.getWriter().write("<p>Feature under development: Inductor impedance input will be here soon.</p>");
    }
}