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

}
