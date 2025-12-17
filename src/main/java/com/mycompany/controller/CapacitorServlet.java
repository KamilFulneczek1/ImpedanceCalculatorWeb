package com.mycompany.controller;

import com.mycompany.model.Capacitor;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(name = "CapacitorServlet", urlPatterns = "/calculate-capacitor")
public class CapacitorServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/views/capacitor.html").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String capacitanceStr = req.getParameter("capacitance");

        double capacitance = Double.parseDouble(capacitanceStr);
        Capacitor capacitor = new Capacitor(capacitance);

        req.setAttribute("result", capacitor.description());
        req.getRequestDispatcher("/WEB-INF/views/result.html").forward(req, resp);
    }
}