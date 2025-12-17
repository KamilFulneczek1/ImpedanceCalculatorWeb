package com.mycompany.controller;

import com.mycompany.model.Resistor;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(name = "ResistorServlet", urlPatterns = "/calculate-resistor")
public class ResistorServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/views/resistor.html").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String resistanceStr = req.getParameter("resistance");

        double resistance = Double.parseDouble(resistanceStr);
        Resistor resistor = new Resistor(resistance);

        req.setAttribute("result", resistor.description());
        req.getRequestDispatcher("/WEB-INF/views/result.html").forward(req, resp);
    }
}