package rxjava.reactive_mongo_driver;

import com.mongodb.rx.client.*;
import org.bson.Document;
import rx.Observable;
import rxjava.reactive_mongo_driver.model.Product;
import rxjava.reactive_mongo_driver.model.User;

public class MongoStorage {

    private final MongoClient client = createMongoClient();

    public Observable<Success> saveUser(User user) {
        return getUserCollection().insertOne(user.toDoc());
    }

    public Observable<Success> saveProduct(Product product) {
        return getProductCollection().insertOne(product.toDoc());
    }

    public Observable<User> getUserByLogin(String login) {
        return getUserCollection().find().toObservable()
                .filter(doc -> doc.get("login").equals(login))
                .map(User::new);
    }

    public Observable<User> getUserById(String id) {
        return getUserCollection().find().toObservable()
                .filter(doc -> doc.get("_id").toString().equals(id))
                .map(User::new);
    }

    public Observable<User> getAllUsers() {
        return getUserCollection().find().toObservable().map(User::new);
    }

    public Observable<Product> getAllProducts() {
        return getProductCollection().find().toObservable().map(Product::new);
    }

    public MongoCollection<Document> getProductCollection() {
        return getDatabase().getCollection("product");
    }

    public MongoCollection<Document> getUserCollection() {
        return getDatabase().getCollection("user");
    }

    private MongoDatabase getDatabase() {
        return client.getDatabase("rxtest");
    }

    private MongoClient createMongoClient() {
        return MongoClients.create("mongodb://localhost:27017");
    }

}

