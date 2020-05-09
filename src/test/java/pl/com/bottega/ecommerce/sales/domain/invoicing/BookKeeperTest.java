package pl.com.bottega.ecommerce.sales.domain.invoicing;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.ClientData;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.Id;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductData;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductType;
import pl.com.bottega.ecommerce.sharedkernel.Money;

import java.util.Date;

import static org.hamcrest.core.Is.is;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class BookKeeperTest {

    private InvoiceFactory invoiceFactory;
    private InvoiceRequest invoiceRequest;
    private ClientData clientData;
    private TaxPolicy taxPolicy;
    private Money money;
    private RequestItem requestItem;
    private ProductData productData;
    private Tax tax;
    private BookKeeper bookKeeper;



    @Before
    public void init() {
        clientData = new ClientData(Id.generate(), "clientNameStub");
        invoiceRequest = new InvoiceRequest(clientData);
        money = Money.ZERO;
        productData = new ProductData(Id.generate(), money, "productNameStub", ProductType.STANDARD, new Date());
        requestItem = new RequestItem(productData, 0, money);
        invoiceFactory = new InvoiceFactory();
        tax = new Tax(money, "taxStub");
        taxPolicy = mock(TaxPolicy.class);
        bookKeeper = new BookKeeper(invoiceFactory);
    }

    @Test
    public void expectedInvoiceWithOneItem() {
        when(taxPolicy.calculateTax(any(), any())).thenReturn(tax);
        BookKeeper bookKeeper = new BookKeeper(invoiceFactory);
        invoiceRequest.add(requestItem);

        Invoice invoice = bookKeeper.issuance(invoiceRequest, taxPolicy);

        Assert.assertThat(invoice.getItems().size(), is(1));
    }

    @Test
    public void expectedTwoCalculateTaxCalls() {
        when(taxPolicy.calculateTax(any(), any())).thenReturn(tax);
        BookKeeper bookKeeper = new BookKeeper(invoiceFactory);
        invoiceRequest.add(requestItem);
        invoiceRequest.add(requestItem);

        Invoice invoice = bookKeeper.issuance(invoiceRequest, taxPolicy);

        Mockito.verify(taxPolicy, times(2)).calculateTax(any(), any());
    }

    @Test
    public void expectedInvoiceWithNoItems() {
        when(taxPolicy.calculateTax(any(), any())).thenReturn(tax);
        BookKeeper bookKeeper = new BookKeeper(invoiceFactory);

        Invoice invoice = bookKeeper.issuance(invoiceRequest, taxPolicy);

        Assert.assertThat(invoice.getItems().size(), is(0));
    }

}