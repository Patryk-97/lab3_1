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


@RunWith(MockitoJUnitRunner.class)
public class BookKeeperTest {
    private static InvoiceFactory factory;
    private static BookKeeper bookKeeper;

    @Mock
    private ClientData clientData;
    @InjectMocks
    private InvoiceRequest request;
    @Mock
    private TaxPolicy taxPolicy;

    private static RequestItem item;
    private static Tax tax;
    private static Product product;

    @BeforeClass
    public static void setUp() {
        factory = new InvoiceFactory();
        bookKeeper = new BookKeeper(factory);
        product = new Product(Id.generate(), Money.ZERO, "", ProductType.STANDARD);
        item = new RequestItem(product.generateSnapshot(), 1, Money.ZERO);
        tax = new Tax(Money.ZERO, "");
    }

    @Test
    public void checkIfInvoiceContainsOneItem() {
        Mockito.when(taxPolicy.calculateTax(Mockito.any(), Mockito.any())).thenReturn(tax);
        request.add(item);
        Assert.assertEquals(1, bookKeeper.issuance(request, taxPolicy).getItems().size());
    }

    @Test
    public void checkIfCalculateTaxIsCalledTwoTimes(){
        Mockito.when(taxPolicy.calculateTax(Mockito.any(), Mockito.any())).thenReturn(tax);
        request.add(item); request.add(item);
        bookKeeper.issuance(request, taxPolicy);
        Mockito.verify(taxPolicy, Mockito.times(2)).calculateTax(Mockito.any(), Mockito.any());
    }

    @Test
    public void checkIfInvoiceContainsZeroItems(){
        Mockito.when(taxPolicy.calculateTax(Mockito.any(), Mockito.any())).thenReturn(tax);
        Assert.assertEquals(0, bookKeeper.issuance(request, taxPolicy).getItems().size());
    }

    @Test (expected = NullPointerException.class)
    public void checkIfInvoiceRequestIsNull(){
        Mockito.when(taxPolicy.calculateTax(Mockito.any(), Mockito.any())).thenReturn(tax);
        bookKeeper.issuance(null, taxPolicy);
    }

    @Test
    public void checkIfCalculateTaxIsCalledAtLeastOnce(){
        Mockito.when(taxPolicy.calculateTax(Mockito.any(), Mockito.any())).thenReturn(tax);
        request.add(item); request.add(item);
        bookKeeper.issuance(request, taxPolicy);
        Mockito.verify(taxPolicy, Mockito.atLeastOnce()).calculateTax(Mockito.any(), Mockito.any());
    }

    @Test
    public void checkIfInvoiceContainsFourItems(){
        Mockito.when(taxPolicy.calculateTax(Mockito.any(), Mockito.any())).thenReturn(tax);
        request.add(item); request.add(item); request.add(item); request.add(item);
        Assert.assertEquals(4, bookKeeper.issuance(request, taxPolicy).getItems().size());
    }
}
