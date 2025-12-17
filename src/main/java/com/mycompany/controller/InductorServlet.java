package com.mycompany.controller;

import com.mycompany.model.Inductor;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(name = "InductorServlet", urlPatterns = "/calculate-inductor")
public class InductorServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/views/inductor.html").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String inductanceStr = req.getParameter("inductance");

        double inductance = Double.parseDouble(inductanceStr);
        Inductor inductor = new Inductor(inductance);

        req.setAttribute("result", inductor.description());
        req.getRequestDispatcher("/WEB-INF/views/result.html").forward(req, resp);
    }
}