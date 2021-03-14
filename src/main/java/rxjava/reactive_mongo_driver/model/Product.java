package rxjava.reactive_mongo_driver.model;

import org.bson.Document;
import rxjava.Exchanger;

public class Product {
    public final String id;
    public final String title;
    public final double price; // in rub

    public Product(Document doc) {
        this(
                doc.get("_id").toString(),
                doc.getString("title"),
                doc.getDouble("price")
        );
    }

    public Product(String id, String title, double price) {
        this.id = id;
        this.title = title;
        this.price = price;
    }

    public Product(String title, double price) {
        this(null, title, price);
    }

    public Document toDoc() {
        return new Document()
                .append("title", title)
                .append("price", price);
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", price='" + price + '\'' +
                "}\n";
    }

    public String showWithCurrency(Currency currency) {
        return "Product{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", price='" + Exchanger.showConverted(currency, price) + '\'' +
                "}\n";
    }

}
