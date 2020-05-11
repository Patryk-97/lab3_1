package pl.com.bottega.ecommerce.sales.domain.invoicing;

import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductData;
import pl.com.bottega.ecommerce.sharedkernel.Money;

public class RequestBuilder {

    private Money money = Money.ZERO;
    private int quantity=0;
    private ProductData productData;

    public RequestBuilder(){

    }

    public RequestBuilder  setMoney(Money money) {
        this.money = money;
        return this;
    }

    public RequestBuilder  setQuantity(int quantity) {
        this.quantity = quantity;
        return this;
    }

    public RequestBuilder  setProductData(ProductData productData) {
        this.productData = productData;
        return this;
    }

    public RequestItem build(){
        return new RequestItem(this.productData, this.quantity, this.money);
    }

}