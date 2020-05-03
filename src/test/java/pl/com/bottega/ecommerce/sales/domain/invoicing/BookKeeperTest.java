package pl.com.bottega.ecommerce.sales.domain.invoicing;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.Id;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.Product;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductType;
import pl.com.bottega.ecommerce.sharedkernel.Money;

@ExtendWith(MockitoExtension.class) public class BookKeeperTest {

    private static BookKeeper sampleBookKeeper;
    private static InvoiceFactory sampleFactory;
    private static RequestItem sampleItem;
    private static Tax sampleTax;
    private static Product sampleProduct;

    @Mock private TaxPolicy sampleTaxPolicy;
    @InjectMocks private InvoiceRequest sampleRequest;

    @BeforeAll public static void initialization() {
        sampleFactory = new InvoiceFactory();
        sampleBookKeeper = new BookKeeper(sampleFactory);
        sampleProduct = new Product(Id.generate(), Money.ZERO, "Test product", ProductType.STANDARD);
        sampleItem = new RequestItem(sampleProduct.generateSnapshot(), 1, Money.ZERO);
        sampleTax = new Tax(Money.ZERO, "Test tax");
    }

    @Test @DisplayName("Checking whether invoice contains single item. Should return true.")
    public void checkIfInvoiceContainsSingleItemTest() {
        Mockito.when(sampleTaxPolicy.calculateTax(Mockito.any(), Mockito.any())).thenReturn(sampleTax);
        sampleRequest.add(sampleItem);
        Assertions.assertEquals(1, sampleBookKeeper.issuance(sampleRequest, sampleTaxPolicy).getItems().size());

    }

    @Test @DisplayName("Checking whether invoice contains five items. Should return true.")
    public void checkIfInvoiceContainsFiveItemsTest() {
        Mockito.when(sampleTaxPolicy.calculateTax(Mockito.any(), Mockito.any())).thenReturn(sampleTax);
        sampleRequest.add(sampleItem);
        sampleRequest.add(sampleItem);
        sampleRequest.add(sampleItem);
        sampleRequest.add(sampleItem);
        sampleRequest.add(sampleItem);
        Assertions.assertEquals(5, sampleBookKeeper.issuance(sampleRequest, sampleTaxPolicy).getItems().size());

    }

    @Test @DisplayName("Checking whether invoice contains no item. Should return true.") public void checkIfInvoiceContainsNoneItemTest() {
        Assertions.assertEquals(0, sampleBookKeeper.issuance(sampleRequest, sampleTaxPolicy).getItems().size());
    }

    @Test @DisplayName("Checking whether calculateTax method is called two times. Should return true.")
    public void checkIfCalculateTaxIsCalledTwoTimesTest() {
        Mockito.when(sampleTaxPolicy.calculateTax(Mockito.any(), Mockito.any())).thenReturn(sampleTax);
        sampleRequest.add(sampleItem);
        sampleRequest.add(sampleItem);
        sampleBookKeeper.issuance(sampleRequest, sampleTaxPolicy);
        Mockito.verify(sampleTaxPolicy, Mockito.times(2)).calculateTax(Mockito.any(), Mockito.any());
    }

    @Test @DisplayName("Checking whether issuance returns null. Should return NullPointerException.")
    public void checkIfIssuanceReturnsNull() {
        Assertions.assertThrows(NullPointerException.class, () -> sampleBookKeeper.issuance(null, sampleTaxPolicy));
    }

    @Test @DisplayName("Checking whether calculateTax method is called one time. Should return true.")
    public void checkIfCalculateTaxIsCalledOneTimeTest() {
        Mockito.when(sampleTaxPolicy.calculateTax(Mockito.any(), Mockito.any())).thenReturn(sampleTax);
        sampleRequest.add(sampleItem);
        sampleBookKeeper.issuance(sampleRequest, sampleTaxPolicy);
        Mockito.verify(sampleTaxPolicy, Mockito.times(1)).calculateTax(Mockito.any(), Mockito.any());
    }

}

