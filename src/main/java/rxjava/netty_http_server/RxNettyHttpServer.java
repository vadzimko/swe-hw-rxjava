package rxjava.netty_http_server;

import io.netty.buffer.ByteBuf;
import io.reactivex.netty.protocol.http.server.HttpServer;
import io.reactivex.netty.protocol.http.server.HttpServerRequest;
import rx.Observable;
import rx.observables.BlockingObservable;
import rxjava.reactive_mongo_driver.MongoStorage;
import rxjava.reactive_mongo_driver.model.Currency;
import rxjava.reactive_mongo_driver.model.Product;
import rxjava.reactive_mongo_driver.model.User;

import java.util.*;

/**
 * user_id and login are unique and are used as unique keys in db
 * <p>
 * examples:
 * curl "localhost:8080/registerUser?login=vadzimko&name=Vadim&currency=RUB"
 * curl "localhost:8080/registerUser?login=dragon228&name=Nagibator&currency=USD"
 * curl "localhost:8080/getProfile?login=dragon228"
 * curl "localhost:8080/getProfile?id=604e4cf4f00e51a6a2a6e82c"
 * curl "localhost:8080/addProduct?title=apple&price=150" -- добавить товар с ценой в рублях
 * curl "localhost:8080/getCatalog?id=123"         -- увидеть с ценами от лица user 123
 * curl "localhost:8080/getCatalog?login=vadzimko" -- увидеть с ценами от лица user vadzimko
 */
public class RxNettyHttpServer {

    private static final MongoStorage storage = new MongoStorage();

    public static void main(final String[] args) {
        HttpServer
                .newServer(8080)
                .start((req, resp) -> {
                    Observable<String> response = handleRequest(req);
                    return resp.writeString(response);
                })
                .awaitShutdown();
    }

    public static String getParam(Map<String, List<String>> queryParams, String paramName) {
        List<String> params = queryParams.getOrDefault(paramName, Collections.emptyList());
        if (params.size() < 1) {
            return null;
        }

        return params.get(0);
    }

    public static Observable<String> errorMessage(String text) {
        return Observable.just("Error: " + text + "\n");
    }

    public static Observable<String> handleRequest(HttpServerRequest<ByteBuf> request) {
        String query = request.getDecodedPath().substring(1);
        Map<String, List<String>> queryParameters = request.getQueryParameters();
        switch (query) {
            case "registerUser":
                return registerUser(queryParameters);
            case "getProfile":
                return getProfile(queryParameters);
            case "getCatalog":
                return getCatalog(queryParameters);
            case "addProduct":
                return addProduct(queryParameters);
            default:
                return errorMessage("Unknown method call");
        }
    }

    public static Observable<String> getProfile(Map<String, List<String>> queryParameters) {
        Observable<User> user = getUserByParams(queryParameters);
        if (user == null) {
            return errorMessage("'login' or 'id' is required");
        }

        return user.map(User::toString);
    }

    public static Observable<User> getUserByParams(Map<String, List<String>> queryParameters) {
        String login = getParam(queryParameters, "login");
        String id = getParam(queryParameters, "id");
        if (login == null && id == null) {
            return null;
        }

        Observable<User> user;
        if (login != null) {
            user = storage.getUserByLogin(login);
        } else {
            user = storage.getUserById(id);
        }

        return user;
    }

    public static Observable<String> registerUser(Map<String, List<String>> queryParameters) {
        String login = getParam(queryParameters, "login");
        String name = getParam(queryParameters, "name");
        String currency = getParam(queryParameters, "currency");
        if (login == null || name == null || currency == null) {
            return errorMessage("'login', 'name', 'currency' are required");
        }

        if (!User.checkCurrency(currency)) {
            return errorMessage("'USD', 'RUB' or 'EURO' are only allowed for currency");
        }

        User user = new User(name, login, Currency.valueOf(currency));
        return storage.saveUser(user).map(Objects::toString);
    }

    public static Observable<String> addProduct(Map<String, List<String>> queryParameters) {
        String title = getParam(queryParameters, "title");
        String priceStr = getParam(queryParameters, "price");
        if (title == null || priceStr == null) {
            return errorMessage("'title' and 'price' are required");
        }

        double price = 0;
        try {
            price = Double.parseDouble(priceStr);
        } catch (NumberFormatException ex) {
            // nothing, common error below
        }

        if (price <= 0) {
            return errorMessage("'price' must be positive number");
        }

        return storage.saveProduct(new Product(title, price)).map(Objects::toString);
    }

    public static Observable<String> getCatalog(Map<String, List<String>> queryParameters) {
        Observable<User> user = getUserByParams(queryParameters);
        if (user == null) {
            return errorMessage("'login' or 'id' is required");
        }

        return getUserCatalog(user);
    }

    public static Observable<String> getUserCatalog(Observable<User> userObservable) {
        Iterator<User> users = userObservable.toBlocking().toIterable().iterator();

        // show price in rubbles by default if such user doesn't exist
        Currency currency = users.hasNext() ? users.next().currency : Currency.RUB;

        return storage
                .getAllProducts()
                .map(p -> p.showWithCurrency(currency));
    }
}
