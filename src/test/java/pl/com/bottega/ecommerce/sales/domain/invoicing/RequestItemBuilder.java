package pl.com.bottega.ecommerce.sales.domain.invoicing;

import pl.com.bottega.ecommerce.sharedkernel.Money;

import java.math.BigDecimal;

public class RequestItemBuilder {

    private int quantity;
    private Money money = Money.ZERO;

    public RequestItemBuilder withQuantity(int quantity) {
        this.quantity = quantity;
        return this;
    }

    public RequestItemBuilder withMoney(int denomination) {
        this.money = new Money(BigDecimal.valueOf(denomination));
        return this;
    }

    public RequestItem build() {
        return new RequestItem(new ProductDataBuilder().buildAny(), quantity, money);
    }
}
