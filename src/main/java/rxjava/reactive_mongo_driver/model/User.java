package rxjava.reactive_mongo_driver.model;

import org.bson.Document;

public class User {
    public final String id;
    public final String name;
    public final String login;
    public final Currency currency;

    public User(Document doc) {
        this(
                doc.get("_id").toString(),
                doc.getString("name"),
                doc.getString("login"),
                Currency.valueOf(doc.getString("currency"))
        );
    }

    public User(String id, String name, String login, Currency currency) {
        this.id = id;
        this.name = name;
        this.login = login;
        this.currency = currency;
    }

    public User(String name, String login, Currency currency) {
        this(null, name, login, currency);
    }

    public Document toDoc() {
        return new Document()
                .append("name", name)
                .append("currency", currency.toString())
                .append("login", login);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", login='" + login + '\'' +
                ", cur='" + currency + '\'' +
                "}\n";
    }

    public static boolean checkCurrency(String currency) {
        for (Currency c : Currency.values()) {
            if (c.name().equals(currency)) {
                return true;
            }
        }
        return false;
    }

}
