package pl.com.bottega.ecommerce.sales.domain.invoicing;

import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.Id;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.Product;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductType;
import pl.com.bottega.ecommerce.sharedkernel.Money;

public class RequestItemFactory {
    private static Product simpleProduct = new Product(Id.generate(), Money.ZERO, "", ProductType.STANDARD);
    public static RequestItem createSimpleRequestItem(){
        return new RequestItem(simpleProduct.generateSnapshot(), 1, Money.ZERO);
    }
}
