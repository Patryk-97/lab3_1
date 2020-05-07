package pl.com.bottega.ecommerce.sales.domain.invoicing;

import org.junit.Before;
import org.junit.Test;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.ClientData;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.Id;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.Product;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductData;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductType;
import pl.com.bottega.ecommerce.sharedkernel.Money;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

public class BookKeeperIssuanceTests {

    private TaxPolicy taxPolicyMock;
    private BookKeeper bookKeeperMock;
    private InvoiceRequest invoiceRequest;

    @Before
    public void initialize() {
        bookKeeperMock = new BookKeeper(new InvoiceFactory());

        invoiceRequest = new InvoiceRequest(mock(ClientData.class));
        ProductData productData = new Product(mock(Id.class), mock(Money.class), "", ProductType.STANDARD).generateSnapshot();
        invoiceRequest.add(new RequestItem(productData, 0, Money.ZERO));

        taxPolicyMock = mock(TaxPolicy.class);
        when(taxPolicyMock.calculateTax(ProductType.STANDARD, Money.ZERO)).thenReturn(new Tax(Money.ZERO, ""));
    }

    @Test
    public void testIfOnePositionInvoiceReturnsOnePositionInvoice() {
        int invoiceLength = bookKeeperMock.issuance(invoiceRequest, taxPolicyMock).getItems().size();

        assertThat(1, is(invoiceLength));
    }
}
