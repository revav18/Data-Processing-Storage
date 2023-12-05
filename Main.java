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
    private Map<String, Integer> mainMap;
    private Map<String, Integer> transactionMap;
    private boolean inTransaction;

    public InMemoryDBImpl() {
        mainMap = new HashMap<>();
        transactionMap = new HashMap<>();
        inTransaction = false;
    }

    @Override
    public int get(String key) {
        if (transactionMap.containsKey(key)) {
            return transactionMap.get(key);
        } 
        else {
            return -1;
        }
    }

    @Override
    public void put(String key, int val) {
        if (!inTransaction) {
            throw new IllegalStateException("Transaction is not in progress");
        }
        mainMap.put(key, val);
        System.out.println("Sets value of: " + key + " to " + val);
    }

    @Override
    public void beginTransaction() {
        if (inTransaction) {
            throw new IllegalStateException("Transaction is already in progress");
        }
        transactionMap.clear();
        mainMap.clear();
        inTransaction = true;
        System.out.println("Transaction started");
    }

    @Override
    public void commit() throws IllegalStateException {
        if (!inTransaction) {
            throw new IllegalStateException("No open transaction to commit");
        }
        // mainMap.putAll(transactionMap);
        transactionMap.putAll(mainMap);
        // mainMap.clear();
        inTransaction = false;
        System.out.println("Transaction committed");
    }

    @Override
    public void rollback() throws IllegalStateException {
        if (!inTransaction) {
            throw new IllegalStateException("No transaction to rollback");
        }
        transactionMap.clear();
        inTransaction = false;
        System.out.println("Transaction Rollbacked");
    }
    
}

public class Main {
    public static void main(String[] args) {
        InMemoryDB inmemoryDB = new InMemoryDBImpl();
        System.out.println(inmemoryDB.get("A")); 

        try {
            inmemoryDB.put("A", 5); 
        } 
        catch (IllegalStateException e) {
            System.out.println("Error: " + e.getMessage());
        }

        inmemoryDB.beginTransaction();
        inmemoryDB.put("A", 5);
        System.out.println(inmemoryDB.get("A"));

        inmemoryDB.put("A", 6);
        inmemoryDB.commit();
        System.out.println(inmemoryDB.get("A")); 

        try {
            inmemoryDB.commit(); 
        } catch (IllegalStateException e) {
            System.out.println("Error: " + e.getMessage());
        }

        try {
            inmemoryDB.rollback(); 
        } catch (IllegalStateException e) {
            System.out.println("Error: " + e.getMessage());
        }

        System.out.println(inmemoryDB.get("B")); 
        inmemoryDB.beginTransaction();
        inmemoryDB.put("B", 10);
        inmemoryDB.rollback();
        System.out.println(inmemoryDB.get("B")); 
    }
}
