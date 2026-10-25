package interpreter.model.adt;
import interpreter.exceptions.ADTException;
import java.util.HashMap;
import java.util.Map;

public class MyDictionary<K, V> implements IDictionary<K, V> {
    private final Map<K, V> map = new HashMap<>();

    @Override public boolean isDefined(K key) { return map.containsKey(key); }

    @Override public V lookup(K key) throws ADTException {
        if (!isDefined(key)) throw new ADTException("Key " + key + " not found");
        return map.get(key);
    }

    @Override public void put(K key, V value) throws ADTException {
        if (isDefined(key)) throw new ADTException("Key " + key + " already exists");
        map.put(key, value);
    }

    @Override public void update(K key, V value) throws ADTException {
        if (!isDefined(key)) throw new ADTException("Key " + key + " not found to update");
        map.put(key, value);
    }

    @Override public String toString() { return map.toString(); }
}