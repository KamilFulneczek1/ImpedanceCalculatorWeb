/**
 * Controller package.
 *
 * Contains servlets and related web-layer components that handle HTTP requests,
 * produce HTML responses and coordinate with the application model (ImpedanceModel).
 *
 * Responsibilities of this package:
 * - present input forms for component and circuit impedance calculation,
 * - parse and validate request parameters,
 * - call the shared application model to perform calculations,
 * - maintain user-visible history (via the model),
 * - demonstrate cookie usage (read/write/display).
 *
 * All classes in this package are intended to be used by the servlet container
 * and therefore are documented for public usage by the web application.
 *
 * @author Kamil Fulneczek
 * @version 1.2
 */
package com.mycompany.controller;