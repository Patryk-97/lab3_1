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
    private RequestItemBuilder requestItemBuilder;
    private InvoiceRequestBuilder invoiceRequestBuilder;

    @Mock
    private TaxPolicy taxPolicy;

    @Mock
    private ClientData clientData;

    @BeforeEach
    public void setUp() {
        bookKeeper = new BookKeeper(new InvoiceFactory());
        invoiceRequest = new InvoiceRequest(clientData);
        productBuilder = new ProductBuilder();
        requestItemBuilder = new RequestItemBuilder();
        invoiceRequestBuilder = new InvoiceRequestBuilder().withClientData(clientData);
        unrelevantProduct = productBuilder.withId(Id.generate())
                                          .withPrice(Money.ZERO)
                                          .withName("")
                                          .withProductType(ProductType.STANDARD)
                                          .build();
        unrelevantRequestItem = requestItemBuilder.withProductData(unrelevantProduct.generateSnapshot())
                                                  .withQuantity(1)
                                                  .withTotalCost(Money.ZERO)
                                                  .build();
        when(taxPolicy.calculateTax(any(), any())).thenReturn(new Tax(Money.ZERO, ""));
    }

    @Test
    public void invoiceRequestWithOneItemShouldReturnInvoiceWithOneItem() {
        invoiceRequest = invoiceRequestBuilder.addRequestItem(unrelevantRequestItem)
                                              .build();
        Invoice invoice = bookKeeper.issuance(invoiceRequest, taxPolicy);
        assertEquals(1, invoice.getItems()
                               .size());
    }

    @Test
    public void invoiceRequestWithTwoItemsShouldCallCalculateTaxMethodTwoTimes() {
        for (int i = 0; i < 2; i++) {
            invoiceRequestBuilder.addRequestItem(unrelevantRequestItem);
        }
        invoiceRequest = invoiceRequestBuilder.build();
        bookKeeper.issuance(invoiceRequest, taxPolicy);
        verify(taxPolicy, times(2)).calculateTax(any(), any());
    }

    @Test
    public void invoiceRequestWithoutItemsShouldReturnInvoiceWithoutItems() {
        invoiceRequest = invoiceRequestBuilder.build();
        Invoice invoice = bookKeeper.issuance(invoiceRequest, taxPolicy);
        assertEquals(0, invoice.getItems()
                               .size());
    }

    @Test
    public void invoiceRequestWithoutItemsShouldNoCallCalculateTaxMethod() {
        invoiceRequest = invoiceRequestBuilder.build();
        bookKeeper.issuance(invoiceRequest, taxPolicy);
        verify(taxPolicy, times(0)).calculateTax(any(), any());
    }

    @Test
    public void invoiceRequestWithFiveItemsShouldReturnInvoiceWithFiveItems() {
        for (int i = 0; i < 5; i++) {
            invoiceRequestBuilder.addRequestItem(unrelevantRequestItem);
        }
        invoiceRequest = invoiceRequestBuilder.build();
        Invoice invoice = bookKeeper.issuance(invoiceRequest, taxPolicy);
        assertEquals(5, invoice.getItems()
                               .size());
    }

    @Test
    public void invoiceRequestWithFiveItemsShouldCallCalculateTaxMethodFiveTimes() {
        for (int i = 0; i < 5; i++) {
            invoiceRequestBuilder.addRequestItem(unrelevantRequestItem);
        }
        invoiceRequest = invoiceRequestBuilder.build();
        bookKeeper.issuance(invoiceRequest, taxPolicy);
        verify(taxPolicy, times(5)).calculateTax(any(), any());
    }
}
