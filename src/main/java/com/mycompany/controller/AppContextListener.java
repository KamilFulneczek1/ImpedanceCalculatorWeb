package com.mycompany.controller;

import com.mycompany.model.ImpedanceModel;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

/**
 * Application context listener that creates a single instance of ImpedanceModel
 * when the application starts and stores it in the ServletContext.
 *
 * This ensures that all servlets share the same model instance throughout
 * the entire application lifecycle, without using static fields.
 *
 * Servlets can retrieve the shared model using:
 * <pre>
 *   ImpedanceModel model = (ImpedanceModel) getServletContext().getAttribute(AppContextListener.MODEL_ATTRIBUTE);
 * </pre>
 *
 * @author Kamil Fulneczek
 * @version 1.0
 */
@WebListener
public class AppContextListener implements ServletContextListener {

    /**
     * Attribute name used to store the ImpedanceModel in ServletContext.
     */
    public static final String MODEL_ATTRIBUTE = "impedanceModel";

    /**
     * Called when the application context is initialized.
     * Creates a single ImpedanceModel instance and stores it in the context.
     *
     * @param sce the ServletContextEvent containing the ServletContext
     */
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext context = sce.getServletContext();
        ImpedanceModel model = new ImpedanceModel();
        context.setAttribute(MODEL_ATTRIBUTE, model);
    }

    /**
     * Called when the application context is destroyed.
     * Removes the ImpedanceModel from the context.
     *
     * @param sce the ServletContextEvent containing the ServletContext
     */
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        ServletContext context = sce.getServletContext();
        context.removeAttribute(MODEL_ATTRIBUTE);
    }
}