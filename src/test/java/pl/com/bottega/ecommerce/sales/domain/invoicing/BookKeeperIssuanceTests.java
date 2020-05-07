package pl.com.bottega.ecommerce.sales.domain.invoicing;

import org.junit.Before;
import org.junit.Test;
import pl.com.bottega.ecommerce.sharedkernel.Money;

import java.math.BigDecimal;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

public class BookKeeperIssuanceTests {

    private TaxPolicy taxPolicyMock;
    private BookKeeper bookKeeperMock;
    private RequestItem requestItem;

    @Before
    public void initialize() {
        bookKeeperMock = new BookKeeper(new InvoiceFactory());

        requestItem = new RequestItemBuilder().withQuantity(0).build();

        taxPolicyMock = mock(TaxPolicy.class);
        when(taxPolicyMock.calculateTax(any(), any())).thenReturn(new Tax(Money.ZERO, ""));
    }

    @Test
    public void testIfOnePositionInvoiceReturnsOnePositionInvoice() {
        InvoiceRequest invoiceRequest = new InvoiceRequestBuilder().withItems(1, requestItem).build();

        int invoiceLength = bookKeeperMock.issuance(invoiceRequest, taxPolicyMock).getItems().size();

        assertThat(1, is(invoiceLength));
    }

    @Test
    public void testIfTwoPositionInvoiceUsesCalculateTaxMethodTwice() {
        InvoiceRequest invoiceRequest = new InvoiceRequestBuilder().withItems(2, requestItem).build();

        bookKeeperMock.issuance(invoiceRequest, taxPolicyMock);

        verify(taxPolicyMock, times(2)).calculateTax(any(), any());
    }

    @Test
    public void testIfZeroPositionInvoiceReturnsZeroPositionInvoice() {
        InvoiceRequest invoiceRequest = new InvoiceRequestBuilder().build();

        int invoiceLength = bookKeeperMock.issuance(invoiceRequest, taxPolicyMock).getItems().size();

        assertThat(0, is(invoiceLength));
    }

    @Test
    public void testIfZeroPositionInvoiceUsesCalculateTaxMethodZeroTimes() {
        InvoiceRequest invoiceRequest = new InvoiceRequestBuilder().build();

        bookKeeperMock.issuance(invoiceRequest, taxPolicyMock);

        verify(taxPolicyMock, times(0)).calculateTax(any(), any());
    }

    @Test
    public void testIfInvoiceIsCalculatedCorrectly() {
        when(taxPolicyMock.calculateTax(any(), any())).thenReturn(new Tax(new Money(BigDecimal.valueOf(2)), ""));

        InvoiceRequest invoiceRequest = new InvoiceRequestBuilder().withItems(2, requestItem).build();

        Invoice invoice = bookKeeperMock.issuance(invoiceRequest, taxPolicyMock);

        assertThat(new Money(BigDecimal.valueOf(4)), is(invoice.getGros()));
    }

    @Test
    public void testIfDataFromInvoicePositionsIsUsed() {
        RequestItem requestItemMock = mock(RequestItem.class);
        when(requestItemMock.getProductData()).thenReturn(new ProductDataBuilder().buildAny());
        when(requestItemMock.getTotalCost()).thenReturn(new Money(BigDecimal.ZERO));
        when(requestItemMock.getQuantity()).thenReturn(0);

        InvoiceRequest invoiceRequest = new InvoiceRequestBuilder().withItems(3, requestItemMock).build();

        bookKeeperMock.issuance(invoiceRequest, taxPolicyMock);

        verify(requestItemMock, atLeastOnce()).getTotalCost();
    }
}
