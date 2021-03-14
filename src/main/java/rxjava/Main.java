package rxjava;

import com.mongodb.rx.client.Success;
import rx.Observable;
import rxjava.reactive_mongo_driver.MongoStorage;
import rxjava.reactive_mongo_driver.model.Currency;
import rxjava.reactive_mongo_driver.model.User;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        MongoStorage storage = new MongoStorage();
//        Observable<User> allUsers = storage.getAllUsers();
//        allUsers.subscribe(Main::getPrintln);

//        User user = new User("AA", "bb", Currency.EURO);
//        Observable<Success> successObservable = storage.saveUser(user);
//        successObservable.subscribe(System.out::println);

        Observable<User> allUsers = storage.getUserByLogin("Vad");
        allUsers.subscribe(Main::getPrintln);
        Thread.sleep(1000);
    }


    private static void getPrintln(User user) {
        System.out.println(user);
    }
}

