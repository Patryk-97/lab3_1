package pl.com.bottega.ecommerce.sales.domain.invoicing;

import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.Id;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.Product;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductType;
import pl.com.bottega.ecommerce.sharedkernel.Money;

public class RequestItemFactory {
	static private final Product sampleProduct = new Product(Id.generate(), Money.ZERO, "", ProductType.STANDARD);
	
	public static RequestItem getRequestItem() {
		return new RequestItem(sampleProduct.generateSnapshot(), 1, Money.ZERO);
	}
	
	public static RequestItem getRequestItem(int quantity, Money money) {
		return new RequestItem(sampleProduct.generateSnapshot(), quantity, money);
	}
}
