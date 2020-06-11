package pl.com.bottega.ecommerce.sales.domain.invoicing;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.ClientData;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.Id;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.Product;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductType;
import pl.com.bottega.ecommerce.sharedkernel.Money;

@ExtendWith(MockitoExtension.class)
public class BookKeeperTest {

    private BookKeeper bookKeeper;
    private InvoiceRequest invoiceRequest;
    private Tax tax;

    @Mock
    private TaxPolicy taxPolicy;

    @Mock
    private ClientData clientData;

    @BeforeEach
    public void setUp() {
        bookKeeper = new BookKeeper(new InvoiceFactory());
        invoiceRequest = new InvoiceRequest(clientData);
        tax = new Tax(Money.ZERO, "");
        when(taxPolicy.calculateTax(any(ProductType.class), any(Money.class))).thenReturn(tax);
    }

    @Test
    public void invoiceRequestWithOneItemShouldReturnInvoiceWithOneItem() {
        Product product = new Product(Id.generate(), Money.ZERO, "", ProductType.STANDARD);
        RequestItem requestItem = new RequestItem(product.generateSnapshot(), 1, Money.ZERO);
        invoiceRequest.add(requestItem);
        Invoice invoice = bookKeeper.issuance(invoiceRequest, taxPolicy);
        assertEquals(1, invoice.getItems()
                               .size());
    }
}
