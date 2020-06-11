package pl.com.bottega.ecommerce.sales.domain.invoicing;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.ClientData;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.Id;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.Product;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductBuilder;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductType;
import pl.com.bottega.ecommerce.sharedkernel.Money;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class BookKeeperTest {

    private BookKeeper bookKeeper;
    private InvoiceRequest invoiceRequest;
    private Product unrelevantProduct;
    private RequestItem unrelevantRequestItem;
    private ProductBuilder productBuilder;

    @Mock
    private TaxPolicy taxPolicy;

    @Mock
    private ClientData clientData;

    @BeforeEach
    public void setUp() {
        bookKeeper = new BookKeeper(new InvoiceFactory());
        invoiceRequest = new InvoiceRequest(clientData);
        productBuilder = new ProductBuilder();
        unrelevantProduct = productBuilder.withId(Id.generate())
                                          .withPrice(Money.ZERO)
                                          .withName("")
                                          .withProductType(ProductType.STANDARD)
                                          .build();
        unrelevantRequestItem = new RequestItem(unrelevantProduct.generateSnapshot(), 1, Money.ZERO);
        Tax tax = new Tax(Money.ZERO, "");
        when(taxPolicy.calculateTax(any(), any())).thenReturn(tax);
    }

    @Test
    public void invoiceRequestWithOneItemShouldReturnInvoiceWithOneItem() {
        invoiceRequest.add(unrelevantRequestItem);
        Invoice invoice = bookKeeper.issuance(invoiceRequest, taxPolicy);
        assertEquals(1, invoice.getItems()
                               .size());
    }

    @Test
    public void invoiceRequestWithTwoItemsShouldCallCalculateTaxMethodTwoTimes() {
        invoiceRequest.add(unrelevantRequestItem);
        invoiceRequest.add(unrelevantRequestItem);
        bookKeeper.issuance(invoiceRequest, taxPolicy);
        verify(taxPolicy, times(2)).calculateTax(any(), any());
    }

    @Test
    public void invoiceRequestWithoutItemsShouldReturnInvoiceWithoutItems() {
        Invoice invoice = bookKeeper.issuance(invoiceRequest, taxPolicy);
        assertEquals(0, invoice.getItems()
                               .size());
    }

    @Test
    public void invoiceRequestWithoutItemsShouldNoCallCalculateTaxMethod() {
        bookKeeper.issuance(invoiceRequest, taxPolicy);
        verify(taxPolicy, times(0)).calculateTax(any(), any());
    }

    @Test
    public void invoiceRequestWithFiveItemsShouldReturnInvoiceWithFiveItems() {
        for (int i = 0; i < 5; i++) {
            invoiceRequest.add(unrelevantRequestItem);
        }
        Invoice invoice = bookKeeper.issuance(invoiceRequest, taxPolicy);
        assertEquals(5, invoice.getItems()
                               .size());
    }

    @Test
    public void invoiceRequestWithFiveItemsShouldCallCalculateTaxMethodFiveTimes() {
        for (int i = 0; i < 5; i++) {
            invoiceRequest.add(unrelevantRequestItem);
        }
        bookKeeper.issuance(invoiceRequest, taxPolicy);
        verify(taxPolicy, times(5)).calculateTax(any(), any());
    }
}
