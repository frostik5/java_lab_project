package db.services.servicesimpl;

import db.dao.daoimpl.InvoiceDAOImpl;
import db.dao.interfaces.InvoiceDAO;
import db.services.interfaces.InvoiceService;
import db.services.interfaces.TicketService;
import pojo.Invoice;
import pojo.Ticket;
import pojo.User;

import java.util.List;
import java.util.Optional;

public final class InvoiceServiceImpl implements InvoiceService {
    private final InvoiceDAO dao = InvoiceDAOImpl.getInstance();

    private final static InvoiceService instance = new InvoiceServiceImpl();
    public static InvoiceService getInstance() {
        return instance;
    }
    private InvoiceServiceImpl(){
    }

    @Override
    public void add(Invoice invoice) {
        dao.add(invoice);
    }

    @Override
    public Optional<Invoice> get(long id) {
        return dao.get(id);
    }

    @Override
    public Optional<Invoice> getInvoiceByUser(long userId, Invoice.InvoiceStatus status) {
        return dao.getInvoiceByUser(userId, status);
    }

    /**
     * Method for getting number of tickets in created invoice of user (exactly bucket)
     *
     * @param user user for whom we check number of tickets in invoice to view in bucket
     * @return number of Ticket in invoice
     */
    @Override
    public int getNumberOfTicketsInInvoice(User user) {
        Invoice invoice;
        int numberOfTicketsInInvoice = 0;
        InvoiceService is = InvoiceServiceImpl.getInstance();
        TicketService ts = TicketServiceImpl.getInstance();
        if (is.getInvoiceByUser(user.getUserId(), Invoice.InvoiceStatus.CREATED).isPresent()) {
            invoice = is.getInvoiceByUser(user.getUserId(), Invoice.InvoiceStatus.CREATED).get();
            List<Ticket> tickets = ts.getTicketsByInvoice(invoice.getInvoiceId());
            numberOfTicketsInInvoice = tickets.size();
        }
        return numberOfTicketsInInvoice;
    }

    @Override
    public void update(Invoice invoice) {
        dao.update(invoice);
    }

    @Override
    public void delete(Invoice invoice) {
        dao.delete(invoice);
    }

    @Override
    public List<Invoice> getAll() {
        return dao.getAll();
    }

    @Override
    public List<Invoice> getAllInvoicesByUserAndStatus(long userId, Invoice.InvoiceStatus status) {
        return dao.getAllInvoicesByUserAndStatus(userId, status);
    }
}
