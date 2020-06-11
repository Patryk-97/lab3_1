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
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductType;
import pl.com.bottega.ecommerce.sharedkernel.Money;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class BookKeeperTest {

    private BookKeeper bookKeeper;
    private InvoiceRequest invoiceRequest;
    private Product unrelevantProduct;
    private RequestItem unrelevantRequestItem;
    @Mock
    private TaxPolicy taxPolicy;

    @Mock
    private ClientData clientData;

    @BeforeEach
    public void setUp() {
        bookKeeper = new BookKeeper(new InvoiceFactory());
        invoiceRequest = new InvoiceRequest(clientData);
        unrelevantProduct = new Product(Id.generate(), Money.ZERO, "", ProductType.STANDARD);
        unrelevantRequestItem = new RequestItem(unrelevantProduct.generateSnapshot(), 1, Money.ZERO);
        Tax tax = new Tax(Money.ZERO, "");
        when(taxPolicy.calculateTax(any(ProductType.class), any(Money.class))).thenReturn(tax);
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
        verify(taxPolicy, times(2)).calculateTax(any(ProductType.class), any(Money.class));
    }

    @Test
    public void invoiceRequestWithoutItemsShouldReturnInvoiceWithoutItems() {
        Invoice invoice = bookKeeper.issuance(invoiceRequest, taxPolicy);
        assertEquals(0, invoice.getItems()
                               .size());
    }
}
