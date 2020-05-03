package pl.com.bottega.ecommerce.sales.domain.invoicing;

import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.Id;
import pl.com.bottega.ecommerce.sharedkernel.Money;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.Product;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductType;

public class RequestItemBuilder {

    private static Product sampleProduct = new Product(Id.generate(), Money.ZERO, "Test product", ProductType.STANDARD);

    public static RequestItem buildSampleRequest() {
        return new RequestItem(sampleProduct.generateSnapshot(), 1, Money.ZERO);
    }
}
