package pl.com.bottega.ecommerce.sales.domain.invoicing;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.ClientData;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.Id;
import pl.com.bottega.ecommerce.sharedkernel.Money;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

class BookKeeperTest {

    private static BookKeeper bookKeeper;
    private static TaxPolicy taxPolicy;
    private InvoiceRequest invoiceRequest;

    @BeforeAll
    static void init()
    {
        bookKeeper = new BookKeeper(new InvoiceFactory());
        taxPolicy = mock(TaxPolicy.class);
        Money money = new Money(new BigDecimal(10), Money.DEFAULT_CURRENCY);
        when(taxPolicy.calculateTax(Mockito.any(), Mockito.any()))
                .thenReturn(new Tax(money, "description"));
    }

    @BeforeEach
    void fixture()
    {
        invoiceRequest = new InvoiceRequest(new ClientData(Id.generate(), "jan"));
    }

    @Test
    void emptyInvoiceTest()
    {
        Invoice invoice = bookKeeper.issuance(invoiceRequest, taxPolicy);
        assertEquals(0, invoice.getItems().size());
    }

    @Test
    void oneItemInvoiceTest()
    {

    }

    @Test
    void invoiceRequestArgumentNullTest()
    {

    }

    @Test
    void taxPolicyArgumentNullTest()
    {

    }

    @Test
    void calculateTaxNumberOfCallsTest()
    {

    }
}