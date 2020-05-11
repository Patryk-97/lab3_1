package pl.com.bottega.ecommerce.sales.domain.invoicing;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.ClientData;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.Id;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.Product;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductType;
import pl.com.bottega.ecommerce.sharedkernel.Money;

import java.math.BigDecimal;
import java.util.Currency;


@RunWith(MockitoJUnitRunner.class)
public class BookKeeperTest {
    private static InvoiceFactory factory;
    private static BookKeeper bookKeeper;

    @Mock
    private TaxPolicy taxPolicy;
    private static Tax tax;

    @BeforeClass
    public static void setUp() {
        factory = new InvoiceFactory();
        bookKeeper = new BookKeeper(factory);
        tax = new Tax(new Money(new BigDecimal(10), Currency.getInstance("EUR")), "Tax");
    }

    @Test
    public void checkIfInvoiceContainsOneItem() {
        Mockito.when(taxPolicy.calculateTax(Mockito.any(ProductType.class), Mockito.any(Money.class))).thenReturn(tax);
        InvoiceRequestBuilder invoiceRequestBuilder = InvoiceRequestBuilder.invoiceRequest();
        invoiceRequestBuilder.withRequestItem(RequestItemFactory.createSimpleRequestItem());
        Assert.assertEquals(1, bookKeeper.issuance(invoiceRequestBuilder.build(), taxPolicy).getItems().size());
    }

    @Test
    public void checkIfCalculateTaxIsCalledTwoTimes(){
        Mockito.when(taxPolicy.calculateTax(Mockito.any(ProductType.class), Mockito.any(Money.class))).thenReturn(tax);
        InvoiceRequestBuilder invoiceRequestBuilder = InvoiceRequestBuilder.invoiceRequest();
        invoiceRequestBuilder.withRequestItem(RequestItemFactory.createSimpleRequestItem());
        invoiceRequestBuilder.withRequestItem(RequestItemFactory.createSimpleRequestItem());
        bookKeeper.issuance(invoiceRequestBuilder.build(), taxPolicy);
        Mockito.verify(taxPolicy, Mockito.times(2)).calculateTax(Mockito.any(ProductType.class), Mockito.any(Money.class));
    }

    @Test
    public void checkIfInvoiceContainsZeroItems(){
        Mockito.when(taxPolicy.calculateTax(Mockito.any(ProductType.class), Mockito.any(Money.class))).thenReturn(tax);
        InvoiceRequestBuilder invoiceRequestBuilder = InvoiceRequestBuilder.invoiceRequest();
        Assert.assertEquals(0, bookKeeper.issuance(invoiceRequestBuilder.build(), taxPolicy).getItems().size());
    }

    @Test (expected = NullPointerException.class)
    public void checkIfInvoiceRequestIsNull(){
        Mockito.when(taxPolicy.calculateTax(Mockito.any(ProductType.class), Mockito.any(Money.class))).thenReturn(tax);
        bookKeeper.issuance(null, taxPolicy);
    }

    @Test
    public void checkIfCalculateTaxIsCalledAtLeastOnce(){
        Mockito.when(taxPolicy.calculateTax(Mockito.any(ProductType.class), Mockito.any(Money.class))).thenReturn(tax);
        InvoiceRequestBuilder invoiceRequestBuilder = InvoiceRequestBuilder.invoiceRequest();
        invoiceRequestBuilder.withRequestItem(RequestItemFactory.createSimpleRequestItem());
        invoiceRequestBuilder.withRequestItem(RequestItemFactory.createSimpleRequestItem());
        bookKeeper.issuance(invoiceRequestBuilder.build(), taxPolicy);
        Mockito.verify(taxPolicy, Mockito.atLeastOnce()).calculateTax(Mockito.any(), Mockito.any());
    }

    @Test
    public void checkIfInvoiceReturnsProperTax(){
        Mockito.when(taxPolicy.calculateTax(Mockito.any(ProductType.class), Mockito.any(Money.class))).thenReturn(tax);
        InvoiceRequestBuilder invoiceRequestBuilder = InvoiceRequestBuilder.invoiceRequest();
        invoiceRequestBuilder.withRequestItem(RequestItemFactory.createSimpleRequestItem());
        Assert.assertEquals(tax.getAmount(), bookKeeper.issuance(invoiceRequestBuilder.build(), taxPolicy).getItems().get(0).getTax().getAmount());
        Assert.assertEquals(tax.getDescription(), bookKeeper.issuance(invoiceRequestBuilder.build(), taxPolicy).getItems().get(0).getTax().getDescription());
    }
}
