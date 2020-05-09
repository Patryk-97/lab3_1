package pl.com.bottega.ecommerce.sales.domain.invoicing;

import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.ClientData;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.Id;
import pl.com.bottega.ecommerce.sales.domain.client.Client;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.Product;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductType;
import pl.com.bottega.ecommerce.sharedkernel.Money;

import java.util.ArrayList;
import java.util.List;

public class InvoiceRequestFactory {

    public static class InvoiceRequestBuilder {
        private List<RequestItem> items;
        private ClientData data;

        private InvoiceRequestBuilder() {
            items = new ArrayList<>();
            data = new Client().generateSnapshot();
        }

        public static class RequestItemBuilder {

            private InvoiceRequestBuilder parent;
            private ProductType type = ProductType.STANDARD;
            private String productName = "Template";
            private int quantity = 0;
            private Money price = Money.ZERO;
            private Id id = null;

            public RequestItemBuilder(InvoiceRequestBuilder parentBuilder) {
                parent = parentBuilder;
            }

            public RequestItemBuilder ofId(Id id) {
                this.id = id;
                return this;
            }

            public RequestItemBuilder ofType(ProductType type) {
                this.type = type;
                return this;
            }

            public RequestItemBuilder ofQuantity(int quantity) {
                this.quantity = quantity;
                return this;
            }

            public RequestItemBuilder ofPrice(Money price) {
                this.price = price;
                return this;
            }

            public RequestItemBuilder ofProductName(String productName) {
                this.productName = productName;
                return this;
            }

            public InvoiceRequestBuilder accept() {
                var item = new RequestItem(new Product(this.id == null ? Id.generate() : this.id,
                        this.price,
                        this.productName,
                        this.type).generateSnapshot(), this.quantity, this.price.multiplyBy(this.quantity));
                parent.applyItem(item);
                return parent;
            }

            public InvoiceRequestBuilder reject() {
                return parent;
            }

        }

        private void applyItem(RequestItem item) {
            items.add(item);
        }

        public RequestItemBuilder addItem() {
            return new RequestItemBuilder(this);
        }

        public InvoiceRequest build() {
            var request = new InvoiceRequest(this.data);
            this.items.forEach(request::add);
            return request;
        }

        public InvoiceRequestBuilder attachClientData(ClientData data) {
            this.data = data;
            return this;
        }

    }

    public static InvoiceRequestBuilder builder() {
        return new InvoiceRequestBuilder();
    }
}
