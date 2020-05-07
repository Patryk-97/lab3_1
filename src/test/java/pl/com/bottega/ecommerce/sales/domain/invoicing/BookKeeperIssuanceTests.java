package pl.com.bottega.ecommerce.sales.domain.invoicing;

import org.junit.Before;
import org.junit.Test;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.ClientData;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.Id;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.Product;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductData;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductType;
import pl.com.bottega.ecommerce.sharedkernel.Money;

import java.math.BigDecimal;
import java.math.BigInteger;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

public class BookKeeperIssuanceTests {

    private TaxPolicy taxPolicyMock;
    private BookKeeper bookKeeperMock;
    private InvoiceRequest invoiceRequest;
    private RequestItem requestItem;

    @Before
    public void initialize() {
        bookKeeperMock = new BookKeeper(new InvoiceFactory());

        invoiceRequest = new InvoiceRequest(mock(ClientData.class));
        ProductData productData = new Product(mock(Id.class), mock(Money.class), "", ProductType.STANDARD).generateSnapshot();
        requestItem = new RequestItem(productData, 0, Money.ZERO);

        taxPolicyMock = mock(TaxPolicy.class);
        when(taxPolicyMock.calculateTax(any(), any())).thenReturn(new Tax(Money.ZERO, ""));
    }

    @Test
    public void testIfOnePositionInvoiceReturnsOnePositionInvoice() {
        invoiceRequest.add(requestItem);

        int invoiceLength = bookKeeperMock.issuance(invoiceRequest, taxPolicyMock).getItems().size();

        assertThat(1, is(invoiceLength));
    }

    @Test
    public void testIfTwoPositionInvoiceUsesCalculateTaxMethodTwice() {
        invoiceRequest.add(requestItem);
        invoiceRequest.add(requestItem);

        bookKeeperMock.issuance(invoiceRequest, taxPolicyMock);

        verify(taxPolicyMock, times(2)).calculateTax(any(), any());
    }

    @Test
    public void testIfZeroPositionInvoiceReturnsZeroPositionInvoice() {
        int invoiceLength = bookKeeperMock.issuance(invoiceRequest, taxPolicyMock).getItems().size();

        assertThat(0, is(invoiceLength));
    }

    @Test
    public void testIfZeroPositionInvoiceUsesCalculateTaxMethodZeroTimes() {
        bookKeeperMock.issuance(invoiceRequest, taxPolicyMock);

        verify(taxPolicyMock, times(0)).calculateTax(any(), any());
    }

    @Test
    public void testIfInvoiceIsCalculatedCorrectly() {
        when(taxPolicyMock.calculateTax(any(), any())).thenReturn(new Tax(new Money(BigDecimal.valueOf(2)), ""));

        invoiceRequest.add(requestItem);
        invoiceRequest.add(requestItem);

        Invoice invoice = bookKeeperMock.issuance(invoiceRequest, taxPolicyMock);

        assertThat(new Money(BigDecimal.valueOf(4)), is(invoice.getGros()));
    }
}
