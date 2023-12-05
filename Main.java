import java.util.HashMap;
import java.util.Map;

interface InMemoryDB {
    int get(String key);

    void put(String key, int val);

    void beginTransaction();

    void commit() throws IllegalStateException;

    void rollback() throws IllegalStateException;
}

class InMemoryDBImpl implements InMemoryDB {
    private Map<String, Integer> mainState;
    private Map<String, Integer> transactionState;
    private boolean isInTransaction;

    public InMemoryDBImpl() {
        mainState = new HashMap<>();
        transactionState = new HashMap<>();
        isInTransaction = false;
    }

    @Override
    public int get(String key) {
        if (transactionState.containsKey(key)) {
            return transactionState.get(key);
        } 
        else {
            return -1;
        }
    }

    @Override
    public void put(String key, int val) {
        if (!isInTransaction) {
            throw new IllegalStateException("Transaction not in progress");
        }
        mainState.put(key, val);
        System.out.println("Sets value of: " + key + " to " + val);
    }

    @Override
    public void beginTransaction() {
        if (isInTransaction) {
            throw new IllegalStateException("Transaction already in progress");
        }
        transactionState.clear();
        mainState.clear();
        isInTransaction = true;
        System.out.println("Starts new transaction");
    }

    @Override
    public void commit() throws IllegalStateException {
        if (!isInTransaction) {
            throw new IllegalStateException("No open transaction to commit");
        }
        // mainState.putAll(transactionState);
        transactionState.putAll(mainState);
        // mainState.clear();
        isInTransaction = false;
        System.out.println("commits open transaction");
    }

    @Override
    public void rollback() throws IllegalStateException {
        if (!isInTransaction) {
            throw new IllegalStateException("No ongoing transaction to rollback");
        }
        transactionState.clear();
        isInTransaction = false;
        System.out.println("TRANSACION ROLLBACKED");
    }
    
}

public class Main {
    public static void main(String[] args) {
        InMemoryDB inmemoryDB = new InMemoryDBImpl();

        System.out.println(inmemoryDB.get("A")); // should return null

        try {
            inmemoryDB.put("A", 5); // should throw an error because a transaction is not in progress
        } 
        catch (IllegalStateException e) {
            System.out.println("Error: " + e.getMessage());
        }

        inmemoryDB.beginTransaction();
        inmemoryDB.put("A", 5);
        System.out.println(inmemoryDB.get("A")); // should return null

        inmemoryDB.put("A", 6);
        inmemoryDB.commit();
        System.out.println(inmemoryDB.get("A")); // should return 6

        try {
            inmemoryDB.commit(); // should throw an error
        } catch (IllegalStateException e) {
            System.out.println("Error: " + e.getMessage());
        }

        try {
            inmemoryDB.rollback(); // should throw an error
        } catch (IllegalStateException e) {
            System.out.println("Error: " + e.getMessage());
        }

        System.out.println(inmemoryDB.get("B")); // should return null
        inmemoryDB.beginTransaction();
        inmemoryDB.put("B", 10);
        inmemoryDB.rollback();
        System.out.println(inmemoryDB.get("B")); // should return null
    }
}
