package Interfaces;

import java.util.List;

import javax.ejb.Local;

import Entities.Product;
import Entities.TemporaryInvoice;

@Local
public interface ITemporaryInvoiceLocal {

public TemporaryInvoice createTemporaryInvoice(TemporaryInvoice invoice)	;

// Available criterias : Clients , validity , Agent
public List<TemporaryInvoice> groupTemporaryInvoicesByCriteria();

public TemporaryInvoice getMostValuableTemporaryInvoice();

public List<Product> getTemporaryInvoiceProducts(TemporaryInvoice invoice);

public List<TemporaryInvoice> fetchInvoices();
}

