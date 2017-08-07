package controller;

import db.service.InvoiceService;
import pojo.Invoice;
import pojo.User;
import utils.ServletUtils;
import utils.SessionUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.Optional;
import java.util.ResourceBundle;

//Заглушка для страницы оплаты

@WebServlet(urlPatterns = {"/invoicePay"})
public class PayInvoiceServlet extends HttpServlet {
    private static InvoiceService is = new InvoiceService();

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ResourceBundle err = (ResourceBundle) getServletContext().getAttribute("errors");
        HttpSession httpSession = request.getSession();
        Cookie[] cookies = request.getCookies();
        SessionUtils.checkCookie(cookies, request, httpSession);
        User user = (User) httpSession.getAttribute("user");
        httpSession.setAttribute("lastServletPath", request.getServletPath());
        request.setCharacterEncoding("UTF-8");

        Optional<Invoice> invoiceOptional = is.getInvoiceByUser(user.getUserId(),
                Invoice.InvoiceStatus.CREATED);
        String[] ticketsIds = request.getParameterValues("ticketId");
        String[] passengerNames = request.getParameterValues("passengerName");
        String[] passports = request.getParameterValues("passport");
        String[] luggages = request.getParameterValues("lugBox");

        if (ServletUtils.isEmptyWhilePayAndSave(ticketsIds, passengerNames, passports, luggages)) {
            request.setAttribute("setFields", err.getString("setFields"));
            request.setAttribute("changesSaved", err.getString("changesSaved"));
            request.getRequestDispatcher("/bucket").forward(request, response);
        } else if (invoiceOptional.isPresent()) {
            Invoice invoice = invoiceOptional.get();
            invoice.setInvoiceStatus(Invoice.InvoiceStatus.PAYED);
            is.update(invoice);
            int ticketsInBucket = ServletUtils.getNumberOfTicketsInInvoice(user);
            httpSession.setAttribute("ticketsInBucket", ticketsInBucket);
            httpSession.setAttribute("invoiceView", null);
            request.getRequestDispatcher("/WEB-INF/pages/invoiceSuccess.jsp").forward(request, response);
        } else {
            request.getRequestDispatcher("/bucket").forward(request, response);
        }
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}