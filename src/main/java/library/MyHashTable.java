package library;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.StringJoiner;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * A hashtable which maps keys to values. It supports all of the operations of {@code Map},
 * including optional operations. It rejects {@code null} keys and values. It is not thread-safe.
 *
 * @param <K> the type of keys maintained by this map
 * @param <V> the type of mapped values
 */
public final class MyHashTable<K, V> implements Map<K, V> {
    
    private static final int DEFAULT_SIZE = 10;
    
    /**
     * A single key-value entry in the map, notated by "Nodes" that make up a singly linked list.
     * This implementation supports the optional setValue method. A node cannot contain
     * {@code null}. Note that K and V in this inner class do not necessarily correspond to the
     * outer class's type parameters, although they do in practice. Also, since this is a static
     * nested class, K and V in this class have no way of accessing MyHashTable instances and
     * therefore no way of accessing type information of the outer class.
     * 
     * @param <K> the type of key in the map this node belongs to
     * @param <V> the type of mapped values for the map this node belongs to
     */
    private static class Node<K, V> implements Entry<K, V> {
        /*
         * While these are private, they can be accessed via a (Node) reference. References should
         * only use the setters when setting the value to maintain non-nullability.
         */
        private final K key;
        private V value;
        
        /*
         * Non-private for ease of access and manipulation (in practice, not necessary, but
         * communicates the idea).
         */
        Node<K, V> next = null;
        
        /**
         * Creates a Node holding the given non-null key and value.
         * 
         * @throws NullPointerException if either the key or value are null
         */
        Node(K key, V value) {
            this.key = Objects.requireNonNull(key);
            this.value = Objects.requireNonNull(value);
        }
        
        /**
         * Returns the key corresponding to this entry.
         * 
         * @return the key corresponding to this entry
         */
        @Override
        public K getKey() {
            return key;
        }
        
        /**
         * Returns the value corresponding to this entry. If the mapping has been removed from the
         * backing map (such as by a view's iterator's remove operation), the results of this call
         * are undefined.
         * 
         * @return the value corresponding to this entry
         */
        @Override
        public V getValue() {
            return value;
        }
        
        /**
         * Replaces the value corresponding to this entry with the specified value. This writes
         * through to the map. If the mapping has been removed from the backing map (such as by the
         * view's iterator's remove operation), the results of this call are undefined.
         * 
         * @param value new value to be stored in this entry
         * @return old value corresponding to the entry
         * @throws NullPointerException if the specified value is null
         */
        @Override
        public V setValue(V value) {
            Objects.requireNonNull(value);
            
            V oldValue = this.value;
            this.value = value;
            return oldValue;
        }
        
        /**
         * Compares the specified object with this entry for equality. Returns true if the given
         * object is also a map entry and the two entries represent the same mapping.
         * 
         * This ensures that the equals method works properly across different implementations of
         * the Map.Entry interface.
         * 
         * @param o object to be compared for equality with this map entry
         * @return true if the specified object is a map entry with an equal key and an equal value
         */
        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof Entry<?, ?>)) {
                return false;
            }
            Entry<?, ?> entry = (Entry<?, ?>) o;
            // This key and value cannot be null, so use them for equals method call
            return key.equals(entry.getKey()) && value.equals(entry.getValue());
        }
        
        /**
         * Returns the hash code value for this map entry. The hash code of a map entry e is defined
         * to be (e.getKey()==null ? 0 : e.getKey().hashCode()) ^ (e.getValue()==null ? 0 :
         * e.getValue().hashCode()).
         * 
         * @return the hash code value for this map entry
         */
        @Override
        public int hashCode() {
            return key.hashCode() ^ value.hashCode();
        }
        
        /**
         * Returns a String representation of this map entry. This implementation returns the string
         * representation of this entry's key followed by the equals character ("=") followed by the
         * string representation of this entry's value. This format is subject to change in future
         * versions.
         * 
         * @return a String representation of this map entry
         */
        @Override
        public String toString() {
            return key + "=" + value;
        }
    }
    
    private Node<K, V>[] table;
    // This can be negative: this means the number of elements has rolled over. May need plan for
    // more than 2^32 elements.
    // Option 2: long. Probably going to go that way
    private int size = 0;
    
    private Set<Entry<K, V>> entrySet = new EntrySet();
    private Set<K> keySet = new KeySet();
    private Collection<V> values = new ValuesCollection();
    
    /**
     * Creates a new non-rehashable hashtable with a capacity of {@value DEFAULT_SIZE} buckets. This
     * is for testing purposes: this will be changed to support rehashing, and will be rebalanced at
     * that time.
     */
    @SuppressWarnings("unchecked")
    public MyHashTable() {
        table = (Node<K, V>[]) new Node<?, ?>[DEFAULT_SIZE];
    }
    
    /**
     * Constructs a new non-rehashable hashtable with the same mappings as the given Map.
     * 
     * @param m the map whose mappings are to be placed in this map
     * @throws NullPointerException if the specified map is null
     */
    public MyHashTable(Map<? extends K, ? extends V> m) {
        this();
        Objects.requireNonNull(m);
        
        // TODO reimplement to put all without changing size, and set size only at the end to avoid
        // iterating for each item (m.size()) (putAll could also do this)
        this.putAll(m);
    }
    
    // ------------------------------------------------------------------------
    // size operations
    // ------------------------------------------------------------------------
    
    /**
     * Returns the number of key-value mappings in this map. If the map contains more than
     * {@code Integer.MAX_VALUE} elements, returns {@code Integer.MAX_VALUE}.
     * 
     * @return the number of key-value mappings in this map
     */
    @Override
    public int size() {
        return (size < 0) ? Integer.MAX_VALUE : size;
    }
    
    /**
     * Returns {@code true} if this map contains no key-value mappings.
     * 
     * @return {@code true} if this map contains no key-value mappings
     */
    @Override
    public boolean isEmpty() {
        return size == 0;
    }
    
    // ------------------------------------------------------------------------
    // single element operations
    // ------------------------------------------------------------------------
    
    /**
     * Returns {@code true} if this map contains a mapping for the specified key. More formally,
     * returns {@code true} if and only if this map contains a mapping for a key k such that
     * {@code Objects.equals(key, k)}. (There can be at most one such mapping.)
     * 
     * @param key key whose presence in this map is to be tested
     * @return {@code true} if this map contains a mapping for the specified key
     * @throws NullPointerException if the specified key is {@code null}
     */
    @Override
    public boolean containsKey(Object key) {
        Objects.requireNonNull(key);
        
        int index = hashToIndex(key);
        for (Node<K, V> node = table[index]; node != null; node = node.next) {
            if (node.getKey().equals(key)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Returns {@code true} if this map maps one or more keys to the specified value. More formally,
     * returns {@code true} if and only if this map contains at least one mapping to a value v such
     * that {@code Objects.equals(value, v)}.
     * 
     * @param value value whose presence in this map is to be tested
     * @return {@code true} if this map maps one or more keys to the specified value
     * @throws NullPointerException if the specified value is {@code null}
     */
    @Override
    public boolean containsValue(Object value) {
        Objects.requireNonNull(value);
        
        for (Node<K, V> node : table) {
            while (node != null) {
                if (node.getValue().equals(value)) {
                    return true;
                } else {
                    node = node.next;
                }
            }
        }
        return false;
    }
    
    /**
     * Returns the value to which the specified key is mapped, or {@code null} if this map contains
     * no mapping for the key. More formally, if this map contains a mapping from a key k to a value
     * v such that {@code Objects.equals(key, k)}, then this method returns v; otherwise it returns
     * {@code null}. (There can be at most one such mapping.)
     * 
     * @param key the key whose associated value is to be returned
     * @return the value to which the specified key is mapped, or {@code null} if this map contains
     *             no mapping for the key
     * @throws NullPointerException if the specified key is {@code null}
     */
    @Override
    public V get(Object key) {
        Objects.requireNonNull(key);
        
        int index = hashToIndex(key);
        for (Node<K, V> node = table[index]; node != null; node = node.next) {
            if (node.getKey().equals(key)) {
                return node.getValue();
            }
        }
        return null;
    }
    
    /**
     * Associates the specified value with the specified key in this map. If the map previously
     * contained a mapping for the key, the old value is replaced by the specified value. (A map m
     * is said to contain a mapping for a key k if and only if {@code m.containsKey(k)} would return
     * {@code true}.)
     * 
     * @param key   key with which the specified value is to be associated
     * @param value value to be associated with the specified key
     * @return the previous value associated with the key, or {@code null} if there was no mapping
     *             for the key
     * @throws NullPointerException if the specified key or value is {@code null}
     */
    @Override
    public V put(K key, V value) {
        Objects.requireNonNull(key);
        Objects.requireNonNull(value);
        
        int index = hashToIndex(key);
        Node<K, V> lastNode = null;
        for (Node<K, V> node = table[index]; node != null; node = node.next) {
            if (node.getKey().equals(key)) {
                return node.setValue(value);
            }
            lastNode = node;
        }
        
        Node<K, V> node = new Node<>(key, value);
        if (lastNode == null) {
            table[index] = node;
        } else {
            lastNode.next = node;
        }
        // Since using integer overflow, -1 represents 2^32 elements.
        if (size != -1) {
            size++;
        }
        return null;
    }
    
    /**
     * Removes the mapping for a key from this map if it is present. More formally, if this map
     * contains a mapping from key k to value v such that {@code Objects.equals(key, k)}, that
     * mapping is removed. (The map can contain at most one such mapping.) Returns the value to
     * which this map previously associated the key, or {@code null} if the map contained no mapping
     * for the key. The map will not contain a mapping for the specified key once the call returns.
     * 
     * @param key key whose mapping is to be removed from the map
     * @return the previous value associated with key, or {@code null} if there was no mapping for
     *             key.
     * @throws NullPointerException if the specified key is {@code null}
     */
    @Override
    public V remove(Object key) {
        Objects.requireNonNull(key);
        
        int index = hashToIndex(key);
        for (Node<K, V> node = table[index], prevNode = null; node != null; prevNode = node, node = node.next) {
            if (node.getKey().equals(key)) {
                V value = node.getValue();
                if (prevNode == null) {
                    table[index] = null;
                } else {
                    prevNode.next = node.next;
                }
                size--;
                return value;
            }
        }
        return null;
    }
    
    /**
     * Determines the index that the supplied object will be stored at.
     * 
     * @param obj the object being stored - never {@code null}
     * @return an index that fits in the table
     */
    private int hashToIndex(Object obj) {
        // Table.length must not be 0, or this will throw ArithmeticException
        int index = obj.hashCode() % table.length;
        if (index < 0) {
            index = -index;
        }
        // TODO rehash if index is OOB (fails on empty table)
        return index;
    }
    
    // ------------------------------------------------------------------------
    // multiple element operations
    // ------------------------------------------------------------------------
    
    /**
     * Copies all of the mappings from the specified map to this map. The effect of this call is
     * equivalent to that of calling put(k, v) on this map once for each mapping from key k to value
     * v in the specified map. The behavior of this operation is undefined if the specified map is
     * modified while the operation is in progress.
     * 
     * @param m mappings to be stored in this map
     * @throws NullPointerException if the specified map is null, or if the specified map contains
     *                                  null keys or values
     */
    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        Objects.requireNonNull(m);
        
        m.forEach(this::put);
    }
    
    /**
     * Removes all of the mappings from this map. The map will be empty after this call returns.
     */
    @Override
    public void clear() {
        for (int i = 0; i < table.length; i++) {
            table[i] = null;
        }
        size = 0;
    }
    
    /**
     * Compares the specified object with this map for equality. Returns true if the given object is
     * also a map and the two maps represent the same mappings. More formally, two maps m1 and m2
     * represent the same mappings if m1.entrySet().equals(m2.entrySet()). This ensures that the
     * equals method works properly across different implementations of the Map interface.
     * 
     * @param o object to be compared for equality with this map
     * @return true if the specified object is equal to this map
     */
    
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Map<?, ?>)) {
            return false;
        }
        Map<?, ?> map = (Map<?, ?>) o;
        return this.entrySet().equals(map.entrySet());
    }
    
    /**
     * Returns the hash code value for this map. The hash code of a map is defined to be the sum of
     * the hash codes of each entry in the map's entrySet() view. This ensures that m1.equals(m2)
     * implies that m1.hashCode()==m2.hashCode() for any two maps m1 and m2, as required by the
     * general contract of Object.hashCode().
     * 
     * @return the hash code value for this map
     */
    @Override
    public int hashCode() {
        return this.entrySet().hashCode();
    }
    
    /**
     * Returns a string representation of this map. The string representation consists of a list of
     * key-value mappings in the order returned by the map's entrySet view's iterator, enclosed in
     * braces ("{}"). Adjacent mappings are separated by the characters ", " (comma and space). Each
     * key-value mapping is rendered as the key followed by an equals sign ("=") followed by the
     * associated value. Keys and values are converted to strings as by Object.toString(), since
     * this is guaranteed to contain no null elements.
     * 
     * @return a string representation of this map
     */
    @Override
    public String toString() {
        StringJoiner joiner = new StringJoiner(", ", "{", "}");
        for (Entry<K, V> entry : this.entrySet()) {
            joiner.add(entry.toString());
        }
        return joiner.toString();
    }
    
    /**
     * Returns the value to which the specified key is mapped, or defaultValue if this map contains
     * no mapping for the key. Implementation Requirements: The default implementation makes no
     * guarantees about synchronization or atomicity properties of this method. Any implementation
     * providing atomicity guarantees must override this method and document its concurrency
     * properties.
     * 
     * @param key          the key whose associated value is to be returned
     * @param defaultValue the default mapping of the key
     * @return the value to which the specified key is mapped, or defaultValue if this map contains
     *             no mapping for the key
     * @throws NullPointerException if the specified key is null
     */
    @Override
    public V getOrDefault(Object key, V defaultValue) {
        V result = this.get(key);
        return (result == null) ? defaultValue : result;
    }
    
    /**
     * Performs the given action for each entry in this map until all entries have been processed or
     * the action throws an exception. Unless otherwise specified by the implementing class, actions
     * are performed in the order of entry set iteration (if an iteration order is specified.)
     * Exceptions thrown by the action are relayed to the caller. Implementation Requirements: The
     * default implementation is equivalent to, for this map:
     * 
     * for (Map.Entry<K, V> entry : map.entrySet()) action.accept(entry.getKey(), entry.getValue());
     * 
     * The default implementation makes no guarantees about synchronization or atomicity properties
     * of this method. Any implementation providing atomicity guarantees must override this method
     * and document its concurrency properties.
     * 
     * @param action The action to be performed for each entry
     * @throws NullPointerException            if the specified action is null
     * @throws ConcurrentModificationException if an entry is found to be removed during iteration
     */
    @Override
    public void forEach(BiConsumer<? super K, ? super V> action) {
        Objects.requireNonNull(action);
        
        for (Entry<K, V> entry : this.entrySet()) {
            action.accept(entry.getKey(), entry.getValue());
        }
    }
    
    /**
     * Replaces each entry's value with the result of invoking the given function on that entry
     * until all entries have been processed or the function throws an exception. Exceptions thrown
     * by the function are relayed to the caller. Implementation Requirements: The default
     * implementation is equivalent to, for this map:
     * 
     * for (Map.Entry<K, V> entry : map.entrySet()) entry.setValue(function.apply(entry.getKey(),
     * entry.getValue()));
     * 
     * The default implementation makes no guarantees about synchronization or atomicity properties
     * of this method. Any implementation providing atomicity guarantees must override this method
     * and document its concurrency properties.
     * 
     * @param function the function to apply to each entry
     * @throws NullPointerException            if the specified function is null, or the specified
     *                                             replacement value is null
     * @throws ConcurrentModificationException if an entry is found to be removed during iteration
     */
    @Override
    public void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {
        // TODO more efficient: use internals
        Objects.requireNonNull(function);
        
        for (Entry<K, V> entry : this.entrySet()) {
            V newValue = function.apply(entry.getKey(), entry.getValue());
            entry.setValue(Objects.requireNonNull(newValue));
        }
    }
    
    /**
     * If the specified key is not already associated with a value (or is mapped to null) associates
     * it with the given value and returns null, else returns the current value. Implementation
     * Requirements: The default implementation is equivalent to, for this map:
     * 
     * V v = map.get(key); if (v == null) v = map.put(key, value);
     * 
     * return v;
     * 
     * The default implementation makes no guarantees about synchronization or atomicity properties
     * of this method. Any implementation providing atomicity guarantees must override this method
     * and document its concurrency properties.
     * 
     * @param key   key with which the specified value is to be associated
     * @param value value to be associated with the specified key
     * @return the previous value associated with the specified key, or null if there was no mapping
     *             for the key.
     * @throws NullPointerException if the specified key or value is null
     */
    @Override
    public V putIfAbsent(K key, V value) {
        // TODO more efficient: use internals
        Objects.requireNonNull(key);
        Objects.requireNonNull(value);
        
        V storedValue = this.get(key);
        if (storedValue == null) {
            this.put(key, value);
        }
        return storedValue;
    }
    
    /**
     * Removes the entry for the specified key only if it is currently mapped to the specified
     * value. Implementation Requirements: The default implementation is equivalent to, for this
     * map:
     * 
     * if (map.containsKey(key) && Objects.equals(map.get(key), value)) { map.remove(key); return
     * true; } else return false;
     * 
     * The default implementation makes no guarantees about synchronization or atomicity properties
     * of this method. Any implementation providing atomicity guarantees must override this method
     * and document its concurrency properties.
     * 
     * @param key   key with which the specified value is associated
     * @param value value expected to be associated with the specified key
     * @return true if the value was removed
     * @throws NullPointerException if the specified key or value is null
     */
    @Override
    public boolean remove(Object key, Object value) {
        // TODO more efficient: use internals
        Objects.requireNonNull(key);
        Objects.requireNonNull(value);
        
        V storedValue = this.get(key);
        if (storedValue.equals(value)) {
            this.remove(key);
            return true;
        } else {
            return false;
        }
    }
    
    /**
     * Replaces the entry for the specified key only if currently mapped to the specified value.
     * Implementation Requirements: The default implementation is equivalent to, for this map:
     * 
     * if (map.containsKey(key) && Objects.equals(map.get(key), value)) { map.put(key, newValue);
     * return true; } else return false;
     * 
     * The default implementation does not throw NullPointerException for maps that do not support
     * null values if oldValue is null unless newValue is also null. The default implementation
     * makes no guarantees about synchronization or atomicity properties of this method. Any
     * implementation providing atomicity guarantees must override this method and document its
     * concurrency properties.
     * 
     * @param key      key with which the specified value is associated
     * @param oldValue value expected to be associated with the specified key
     * @param newValue value to be associated with the specified key
     * @return true if the value was replaced
     * @throws NullPointerException if a specified key, oldValue, or newValue is null
     */
    @Override
    public boolean replace(K key, V oldValue, V newValue) {
        // TODO more efficient: use internals
        Objects.requireNonNull(key);
        Objects.requireNonNull(oldValue);
        Objects.requireNonNull(newValue);
        
        V storedValue = this.get(key);
        // Also returns false on null
        if (oldValue.equals(storedValue)) {
            this.put(key, newValue);
            return true;
        } else {
            return false;
        }
    }
    
    /**
     * Replaces the entry for the specified key only if it is currently mapped to some value.
     * Implementation Requirements: The default implementation is equivalent to, for this map:
     * 
     * if (map.containsKey(key)) { return map.put(key, value); } else return null;
     * 
     * The default implementation makes no guarantees about synchronization or atomicity properties
     * of this method. Any implementation providing atomicity guarantees must override this method
     * and document its concurrency properties.
     * 
     * @param key   key with which the specified value is associated
     * @param value value to be associated with the specified key
     * @return the previous value associated with the specified key, or null if there was no mapping
     *             for the key.
     * @throws NullPointerException if the specified key or value is null
     */
    @Override
    public V replace(K key, V value) {
        // TODO more efficient: use internals
        Objects.requireNonNull(key);
        Objects.requireNonNull(value);
        
        if (this.containsKey(key)) {
            return this.put(key, value);
        } else {
            return null;
        }
    }
    
    /**
     * If the specified key is not already associated with a value (or is mapped to null), attempts
     * to compute its value using the given mapping function and enters it into this map unless
     * null. If the mapping function returns null, no mapping is recorded. If the mapping function
     * itself throws an (unchecked) exception, the exception is rethrown, and no mapping is
     * recorded. The most common usage is to construct a new object serving as an initial mapped
     * value or memoized result, as in:
     * 
     * map.computeIfAbsent(key, k -> new Value(f(k)));
     * 
     * Or to implement a multi-value map, Map<K,Collection<V>>, supporting multiple values per key:
     * 
     * map.computeIfAbsent(key, k -> new HashSet<V>()).add(v);
     * 
     * The mapping function should not modify this map during computation.
     * 
     * Implementation Requirements: The default implementation is equivalent to the following steps
     * for this map, then returning the current value or null if now absent:
     * 
     * if (map.get(key) == null) { V newValue = mappingFunction.apply(key); if (newValue != null)
     * map.put(key, newValue); }
     * 
     * The default implementation makes no guarantees about detecting if the mapping function
     * modifies this map during computation and, if appropriate, reporting an error. Non-concurrent
     * implementations should override this method and, on a best-effort basis, throw a
     * ConcurrentModificationException if it is detected that the mapping function modifies this map
     * during computation. Concurrent implementations should override this method and, on a
     * best-effort basis, throw an IllegalStateException if it is detected that the mapping function
     * modifies this map during computation and as a result computation would never complete.
     * 
     * The default implementation makes no guarantees about synchronization or atomicity properties
     * of this method. Any implementation providing atomicity guarantees must override this method
     * and document its concurrency properties. In particular, all implementations of subinterface
     * ConcurrentMap must document whether the mapping function is applied once atomically only if
     * the value is not present.
     * 
     * @param key             key with which the specified value is to be associated
     * @param mappingFunction the mapping function to compute a value
     * @return the current (existing or computed) value associated with the specified key, or null
     *             if the computed value is null
     * @throws NullPointerException if the specified key or the mappingFunction is null
     */
    @Override
    public V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction) {
        // TODO more efficient: use internals
        Objects.requireNonNull(key);
        Objects.requireNonNull(mappingFunction);
        
        V value = this.get(key);
        if (value == null) {
            value = mappingFunction.apply(key);
            if (value != null) {
                this.put(key, value);
            }
        }
        return value;
    }
    
    /**
     * If the value for the specified key is present and non-null, attempts to compute a new mapping
     * given the key and its current mapped value. If the remapping function returns null, the
     * mapping is removed. If the remapping function itself throws an (unchecked) exception, the
     * exception is rethrown, and the current mapping is left unchanged.
     * 
     * The remapping function should not modify this map during computation.
     * 
     * Implementation Requirements: The default implementation is equivalent to performing the
     * following steps for this map, then returning the current value or null if now absent:
     * 
     * if (map.get(key) != null) { V oldValue = map.get(key); V newValue =
     * remappingFunction.apply(key, oldValue); if (newValue != null) map.put(key, newValue); else
     * map.remove(key); }
     * 
     * The default implementation makes no guarantees about detecting if the remapping function
     * modifies this map during computation and, if appropriate, reporting an error. Non-concurrent
     * implementations should override this method and, on a best-effort basis, throw a
     * ConcurrentModificationException if it is detected that the remapping function modifies this
     * map during computation. Concurrent implementations should override this method and, on a
     * best-effort basis, throw an IllegalStateException if it is detected that the remapping
     * function modifies this map during computation and as a result computation would never
     * complete.
     * 
     * The default implementation makes no guarantees about synchronization or atomicity properties
     * of this method. Any implementation providing atomicity guarantees must override this method
     * and document its concurrency properties. In particular, all implementations of subinterface
     * ConcurrentMap must document whether the remapping function is applied once atomically only if
     * the value is not present.
     * 
     * @param key               key with which the specified value is to be associated
     * @param remappingFunction the remapping function to compute a value
     * @return the new value associated with the specified key, or null if none
     * @throws NullPointerException if the specified key or the remappingFunction is null
     */
    @Override
    public V computeIfPresent(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        // TODO more efficient: use internals
        Objects.requireNonNull(key);
        Objects.requireNonNull(remappingFunction);
        
        V oldValue = this.get(key);
        if (oldValue == null) {
            return null;
        }
        
        V newValue = remappingFunction.apply(key, oldValue);
        if (newValue == null) {
            this.remove(key);
        } else {
            this.put(key, newValue);
        }
        return newValue;
    }
    
    /**
     * Attempts to compute a mapping for the specified key and its current mapped value (or null if
     * there is no current mapping). For example, to either create or append a String msg to a value
     * mapping:
     * 
     * map.compute(key, (k, v) -> (v == null) ? msg : v.concat(msg)) (Method merge() is often
     * simpler to use for such purposes.) If the remapping function returns null, the mapping is
     * removed (or remains absent if initially absent). If the remapping function itself throws an
     * (unchecked) exception, the exception is rethrown, and the current mapping is left unchanged.
     * 
     * The remapping function should not modify this map during computation.
     * 
     * Implementation Requirements: The default implementation is equivalent to performing the
     * following steps for this map, then returning the current value or null if absent:
     * 
     * V oldValue = map.get(key); V newValue = remappingFunction.apply(key, oldValue); if (oldValue
     * != null) { if (newValue != null) map.put(key, newValue); else map.remove(key); } else { if
     * (newValue != null) map.put(key, newValue); else return null; }
     * 
     * The default implementation makes no guarantees about detecting if the remapping function
     * modifies this map during computation and, if appropriate, reporting an error. Non-concurrent
     * implementations should override this method and, on a best-effort basis, throw a
     * ConcurrentModificationException if it is detected that the remapping function modifies this
     * map during computation. Concurrent implementations should override this method and, on a
     * best-effort basis, throw an IllegalStateException if it is detected that the remapping
     * function modifies this map during computation and as a result computation would never
     * complete.
     * 
     * The default implementation makes no guarantees about synchronization or atomicity properties
     * of this method. Any implementation providing atomicity guarantees must override this method
     * and document its concurrency properties. In particular, all implementations of subinterface
     * ConcurrentMap must document whether the remapping function is applied once atomically only if
     * the value is not present.
     * 
     * @param key               key with which the specified value is to be associated
     * @param remappingFunction the remapping function to compute a value
     * @return the new value associated with the specified key, or null if none
     * @throws NullPointerException if the specified key or the remappingFunction is null
     */
    @Override
    public V compute(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        // TODO more efficient: use internals
        Objects.requireNonNull(key);
        Objects.requireNonNull(remappingFunction);
        
        V oldValue = this.get(key);
        V newValue = remappingFunction.apply(key, oldValue);
        if (newValue != null) {
            this.put(key, newValue);
        } else if (oldValue != null) {
            this.remove(key);
        }
        return newValue;
    }
    
    /**
     * If the specified key is not already associated with a value or is associated with null,
     * associates it with the given non-null value. Otherwise, replaces the associated value with
     * the results of the given remapping function, or removes if the result is null. This method
     * may be of use when combining multiple mapped values for a key. For example, to either create
     * or append a String msg to a value mapping:
     * 
     * map.merge(key, msg, String::concat)
     * 
     * If the remapping function returns null, the mapping is removed. If the remapping function
     * itself throws an (unchecked) exception, the exception is rethrown, and the current mapping is
     * left unchanged.
     * 
     * The remapping function should not modify this map during computation.
     * 
     * Implementation Requirements: The default implementation is equivalent to performing the
     * following steps for this map, then returning the current value or null if absent:
     * 
     * V oldValue = map.get(key); V newValue = (oldValue == null) ? value :
     * remappingFunction.apply(oldValue, value); if (newValue == null) map.remove(key); else
     * map.put(key, newValue);
     * 
     * The default implementation makes no guarantees about detecting if the remapping function
     * modifies this map during computation and, if appropriate, reporting an error. Non-concurrent
     * implementations should override this method and, on a best-effort basis, throw a
     * ConcurrentModificationException if it is detected that the remapping function modifies this
     * map during computation. Concurrent implementations should override this method and, on a
     * best-effort basis, throw an IllegalStateException if it is detected that the remapping
     * function modifies this map during computation and as a result computation would never
     * complete.
     * 
     * The default implementation makes no guarantees about synchronization or atomicity properties
     * of this method. Any implementation providing atomicity guarantees must override this method
     * and document its concurrency properties. In particular, all implementations of subinterface
     * ConcurrentMap must document whether the remapping function is applied once atomically only if
     * the value is not present.
     * 
     * Parameters:
     * 
     * @param key               key with which the resulting value is to be associated
     * @param value             the non-null value to be merged with the existing value associated
     *                              with the key or, if no existing value or a null value is
     *                              associated with the key, to be associated with the key
     * @param remappingFunction the remapping function to recompute a value if present
     * @return the new value associated with the specified key, or null if no value is associated
     *             with the key
     * @throws NullPointerException if the specified key, value, or remappingFunction is null
     */
    @Override
    public V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
        // TODO more efficient: use internals
        Objects.requireNonNull(key);
        Objects.requireNonNull(value);
        Objects.requireNonNull(remappingFunction);
        
        V oldValue = this.get(key);
        if (oldValue == null) {
            this.put(key, value);
            return value;
        }
        
        V newValue = remappingFunction.apply(oldValue, value);
        if (newValue == null) {
            this.remove(key);
            return null;
        } else {
            this.put(key, newValue);
            return newValue;
        }
    }
    
    // ------------------------------------------------------------------------
    // collection views
    // ------------------------------------------------------------------------
    // TODO inspect a base class to implement typical functions
    /**
     * Returns a Set view of the keys contained in this map. The set is backed by the map, so
     * changes to the map are reflected in the set, and vice-versa. If the map is modified while an
     * iteration over the set is in progress (except through the iterator's own remove operation),
     * the results of the iteration are undefined. The set supports element removal, which removes
     * the corresponding mapping from the map, via the Iterator.remove, Set.remove, removeAll,
     * retainAll, and clear operations. It does not support the add or addAll operations.
     * 
     * @return a set view of the keys contained in this map
     */
    @Override
    public Set<K> keySet() {
        return keySet;
    }
    
    /**
     * The stateless KeySet that delegates its calls to the outer HashTable instance.
     */
    private class KeySet implements Set<K> {
        /**
         * Returns the number of elements in this set (its cardinality). If this set contains more
         * than Integer.MAX_VALUE elements, returns Integer.MAX_VALUE.
         * 
         * @return the number of elements in this set (its cardinality)
         */
        @Override
        public int size() {
            return MyHashTable.this.size();
        }
        
        /**
         * Returns true if this set contains no elements.
         * 
         * @return true if this set contains no elements
         */
        @Override
        public boolean isEmpty() {
            return this.size() == 0;
        }
        
        /**
         * Returns true if this set contains the specified element. More formally, returns true if
         * and only if this set contains an element e such that Objects.equals(o, e).
         * 
         * @param o element whose presence in this set is to be tested
         * @return true if this set contains the specified element
         * @throws NullPointerException if the specified element is null
         */
        @Override
        public boolean contains(Object o) {
            Objects.requireNonNull(o);
            
            return MyHashTable.this.containsKey(o);
        }
        
        /**
         * Returns an iterator over the elements in this set. The elements are returned in no
         * particular order
         * 
         * @return an iterator over the elements in this set
         */
        @Override
        public Iterator<K> iterator() {
            return new KeyIterator();
        }
        
        /**
         * Performs the given action for each element of the Iterable until all elements have been
         * processed or the action throws an exception. Actions are performed in the order of
         * iteration, if that order is specified. Exceptions thrown by the action are relayed to the
         * caller. The behavior of this method is unspecified if the action performs side-effects
         * that modify the underlying source of elements, unless an overriding class has specified a
         * concurrent modification policy.
         * 
         * Implementation Requirements: The default implementation behaves as if:
         * 
         * for (T t : this) action.accept(t);
         * 
         * @param action The action to be performed for each element
         * @throws NullPointerException if the specified action is null
         */
        @Override
        public void forEach(Consumer<? super K> action) {
            Objects.requireNonNull(action);
            
            for (K key : this) {
                action.accept(key);
            }
        }
        
        /**
         * Returns an array containing all of the elements in this set, in no particular order. The
         * returned array will be "safe" in that no references to it are maintained by this set. (In
         * other words, this method must allocate a new array even if this set is backed by an
         * array). The caller is thus free to modify the returned array. This method acts as bridge
         * between array-based and collection-based APIs.
         * 
         * @return an array containing all the elements in this set
         */
        @Override
        public Object[] toArray() {
            Object[] array = new Object[this.size()];
            int i = 0;
            for (K key : this) {
                array[i] = key;
                i++;
            }
            return array;
        }
        
        /**
         * Returns an array containing all of the elements in this set; the runtime type of the
         * returned array is that of the specified array. If the set fits in the specified array, it
         * is returned therein. Otherwise, a new array is allocated with the runtime type of the
         * specified array and the size of this set. If this set fits in the specified array with
         * room to spare (i.e., the array has more elements than this set), the element in the array
         * immediately following the end of the set is set to null. (This is useful in determining
         * the length of this set since the caller knows that this set does not contain any null
         * elements.) This set makes no guarantees as to what order its elements are returned by its
         * iterator. Like the toArray() method, this method acts as bridge between array-based and
         * collection-based APIs. Further, this method allows precise control over the runtime type
         * of the output array, and may, under certain circumstances, be used to save allocation
         * costs. Suppose x is a set known to contain only strings. The following code can be used
         * to dump the set into a newly allocated array of String: String[] y = x.toArray(new
         * String[0]); Note that toArray(new Object[0]) is identical in function to toArray().
         * 
         * @param T the component type of the array to contain the collection
         * @param a the array into which the elements of this set are to be stored, if it is big
         *              enough; otherwise, a new array of the same runtime type is allocated for
         *              this purpose.
         * @return an array containing all the elements in this set
         * @throws ArrayStoreException  if the runtime type of the specified array is not a
         *                                  supertype of the runtime type of every element in this
         *                                  set
         * @throws NullPointerException if the specified array is null
         */
        @Override
        public <T> T[] toArray(T[] a) {
            Objects.requireNonNull(a);
            
            List<K> list = new ArrayList<>(this.size());
            for (K key : this) {
                list.add(key);
            }
            return list.toArray(a);
        }
        
        /**
         * Returns an array containing all of the elements in this collection, using the provided
         * generator function to allocate the returned array. If this collection makes any
         * guarantees as to what order its elements are returned by its iterator, this method must
         * return the elements in the same order. This method acts as a bridge between array-based
         * and collection-based APIs. It allows creation of an array of a particular runtime type.
         * Use toArray() to create an array whose runtime type is Object[], or use toArray(T[]) to
         * reuse an existing array. Suppose x is a collection known to contain only strings. The
         * following code can be used to dump the collection into a newly allocated array of String:
         * 
         * String[] y = x.toArray(String[]::new); The default implementation calls the generator
         * function with zero and then passes the resulting array to toArray(T[]).
         * 
         * @param T         the component type of the array to contain the collection
         * @param generator a function which produces a new array of the desired type and the
         *                      provided length
         * @return an array containing all of the elements in this collection
         * @throws ArrayStoreException  if the runtime type of any element in this collection is not
         *                                  assignable to the runtime component type of the
         *                                  generated array
         * @throws NullPointerException if the generator function is null
         */
        @Override
        public <T> T[] toArray(IntFunction<T[]> generator) {
            Objects.requireNonNull(generator);
            
            T[] array = generator.apply(this.size());
            return this.toArray(array);
        }
        
        /**
         * Adds the specified element to this set if it is not already present (optional operation).
         * This method is not supported by this implementation.
         * 
         * @implSpec This implementation throws an UnsupportedOperationException.
         * @param e element to be added to this set
         * @return nothing, as an exception is always thrown
         * @throws UnsupportedOperationException always
         */
        @Override
        public boolean add(K e) {
            throw new UnsupportedOperationException();
        }
        
        /**
         * Removes the specified element from this set if it is present. More formally, removes an
         * element e such that Objects.equals(o, e), if this set contains such an element. Returns
         * true if this set contained the element (or equivalently, if this set changed as a result
         * of the call). (This set will not contain the element once the call returns.)
         * 
         * @param o object to be removed from this set, if present
         * @return true if this set contained the specified element
         * @throws NullPointerException if the specified element is null
         */
        @Override
        public boolean remove(Object o) {
            Objects.requireNonNull(o);
            
            return MyHashTable.this.remove(o) != null;
        }
        
        /**
         * Returns true if this set contains all of the elements of the specified collection. If the
         * specified collection is also a set, this method returns true if it is a subset of this
         * set.
         * 
         * @param c collection to be checked for containment in this set
         * @return true if this set contains all of the elements of the specified collection
         * @throws NullPointerException if the specified collection contains one or more null
         *                                  elements, or if the specified collection is null
         */
        @Override
        public boolean containsAll(Collection<?> c) {
            Objects.requireNonNull(c);
            
            boolean missingElement = false;
            for (Object element : c) {
                Objects.requireNonNull(element);
                if (!MyHashTable.this.containsKey(element)) {
                    missingElement = true;
                }
            }
            return !missingElement;
        }
        
        /**
         * Adds all of the elements in the specified collection to this set if they're not already
         * present (optional operation). If the specified collection is also a set, the addAll
         * operation effectively modifies this set so that its value is the union of the two sets.
         * This behavior is not supported by this implementation.
         * 
         * @param c collection containing elements to be added to this set
         * @return nothing: this implementation does not support this method
         * @throws UnsupportedOperationException always
         */
        @Override
        public boolean addAll(Collection<? extends K> c) {
            throw new UnsupportedOperationException();
        }
        
        /**
         * Retains only the elements in this set that are contained in the specified collection. In
         * other words, removes from this set all of its elements that are not contained in the
         * specified collection. If the specified collection is also a set, this operation
         * effectively modifies this set so that its value is the intersection of the two sets.
         * 
         * @param c collection containing elements to be retained in this set
         * @return true if this set changed as a result of the call
         * @throws NullPointerException if the specified collection is null
         */
        @Override
        public boolean retainAll(Collection<?> c) {
            Objects.requireNonNull(c);
            
            boolean modified = false;
            for (Iterator<K> iterator = this.iterator(); iterator.hasNext();) {
                K element = iterator.next();
                if (!c.contains(element)) {
                    iterator.remove();
                    modified = true;
                }
            }
            return modified;
        }
        
        /**
         * Removes from this set all of its elements that are contained in the specified collection.
         * If the specified collection is also a set, this operation effectively modifies this set
         * so that its value is the asymmetric set difference of the two sets.
         * 
         * @param c collection containing elements to be removed from this set
         * @return true if this set changed as a result of the call
         * @throws NullPointerException if the specified collection is null
         */
        @Override
        public boolean removeAll(Collection<?> c) {
            Objects.requireNonNull(c);
            
            boolean modified = false;
            for (Iterator<K> iterator = this.iterator(); iterator.hasNext();) {
                K element = iterator.next();
                if (c.contains(element)) {
                    iterator.remove();
                    modified = true;
                }
            }
            return modified;
        }
        
        /**
         * Removes all of the elements of this collection that satisfy the given predicate. Errors
         * or runtime exceptions thrown during iteration or by the predicate are relayed to the
         * caller. Implementation Requirements: The default implementation traverses all elements of
         * the collection using its iterator(). Each matching element is removed using
         * Iterator.remove(). If the collection's iterator does not support removal then an
         * UnsupportedOperationException will be thrown on the first matching element.
         * 
         * @param filter a predicate which returns true for elements to be removed
         * @return true if any elements were removed
         * @throws NullPointerException if the specified filter is null
         */
        @Override
        public boolean removeIf(Predicate<? super K> filter) {
            Objects.requireNonNull(filter);
            
            boolean changed = false;
            for (Iterator<K> iterator = this.iterator(); iterator.hasNext();) {
                K key = iterator.next();
                if (filter.test(key)) {
                    iterator.remove();
                    changed = true;
                }
            }
            return changed;
        }
        
        /**
         * Removes all of the elements from this set. The set will be empty after this call returns.
         */
        @Override
        public void clear() {
            MyHashTable.this.clear();
        }
        
        /**
         * Compares the specified object with this set for equality. Returns true if the specified
         * object is also a set, the two sets have the same size, and every member of the specified
         * set is contained in this set (or equivalently, every member of this set is contained in
         * the specified set). This definition ensures that the equals method works properly across
         * different implementations of the set interface.
         * 
         * @param o object to be compared for equality with this set
         * @return true if the specified object is equal to this set
         */
        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof Set<?>)) {
                return false;
            }
            Set<?> other = (Set<?>) o;
            return this.size() == other.size() && this.containsAll(other);
        }
        
        /**
         * Returns the hash code value for this set. The hash code of a set is defined to be the sum
         * of the hash codes of the elements in the set, where the hash code of a null element is
         * defined to be zero. This ensures that s1.equals(s2) implies that
         * s1.hashCode()==s2.hashCode() for any two sets s1 and s2, as required by the general
         * contract of Object.hashCode().
         * 
         * @return the hash code value for this set
         */
        @Override
        public int hashCode() {
            return this.stream().mapToInt(Object::hashCode).sum();
        }
        
        /**
         * Returns a string representation of this collection. The string representation consists of
         * a list of the collection's elements in the order they are returned by its iterator,
         * enclosed in square brackets ("[]"). Adjacent elements are separated by the characters ",
         * " (comma and space). Elements are converted to strings by toString(), since this
         * collection will contain no null elements.
         * 
         * While this is not required by the Set interface, it makes this much easier to debug and
         * use.
         */
        @Override
        public String toString() {
            StringJoiner joiner = new StringJoiner(", ", "[", "]");
            for (K key : this) {
                joiner.add(key.toString());
            }
            return joiner.toString();
        }
        
        /**
         * Creates a Spliterator over the elements in this set. The Spliterator reports
         * Spliterator.DISTINCT, Spliterator.SIZED, Spliterator.SUBSIZED, and Spliterator.NONNULL.
         * This implementation creates a late-binding spliterator from the set's Iterator.
         * 
         * @return a Spliterator over the elements in this set
         */
        @Override
        public Spliterator<K> spliterator() {
            return Spliterators.spliterator(this,
                    Spliterator.DISTINCT | Spliterator.NONNULL | Spliterator.SIZED | Spliterator.SUBSIZED);
        }
        
        /**
         * Returns a sequential Stream with this collection as its source. This method should be
         * overridden when the spliterator() method cannot return a spliterator that is IMMUTABLE,
         * CONCURRENT, or late-binding. (See spliterator() for details.)
         * 
         * @return a sequential Stream over the elements in this collection
         */
        @Override
        public Stream<K> stream() {
            return StreamSupport.stream(this.spliterator(), false);
        }
        
        /**
         * Returns a possibly parallel Stream with this collection as its source. It is allowable
         * for this method to return a sequential stream. This method should be overridden when the
         * spliterator() method cannot return a spliterator that is IMMUTABLE, CONCURRENT, or
         * late-binding. (See spliterator() for details.)
         * 
         * @return a possibly parallel Stream over the elements in this collection
         */
        
        @Override
        public Stream<K> parallelStream() {
            return StreamSupport.stream(this.spliterator(), true);
        }
    }
    
    /**
     * Returns a Collection view of the values contained in this map. The collection is backed by
     * the map, so changes to the map are reflected in the collection, and vice-versa. If the map is
     * modified while an iteration over the collection is in progress (except through the iterator's
     * own remove operation), the results of the iteration are undefined. The collection supports
     * element removal, which removes the corresponding mapping from the map, via the
     * Iterator.remove, Collection.remove, removeAll, retainAll and clear operations. It does not
     * support the add or addAll operations.
     * 
     * @return a collection view of the values contained in this map
     */
    @Override
    public Collection<V> values() {
        return values;
    }
    
    /**
     * The stateless ValuesCollection that delegates its calls to the outer HashTable instance.
     */
    private class ValuesCollection implements Collection<V> {
        
        /**
         * Returns the number of elements in this collection. If this collection contains more than
         * Integer.MAX_VALUE elements, returns Integer.MAX_VALUE.
         * 
         * @return the number of elements in this collection
         */
        @Override
        public int size() {
            return MyHashTable.this.size();
        }
        
        /**
         * Returns true if this collection contains no elements.
         * 
         * @return true if this collection contains no elements
         */
        @Override
        public boolean isEmpty() {
            return this.size() == 0;
        }
        
        /**
         * Returns true if this collection contains the specified element. More formally, returns
         * true if and only if this collection contains at least one element e such that
         * Objects.equals(o, e).
         * 
         * @param o element whose presence in this collection is to be tested
         * @return true if this collection contains the specified element
         * @throws NullPointerException if the specified element is null and this collection does
         *                                  not permit null elements
         */
        @Override
        public boolean contains(Object o) {
            Objects.requireNonNull(o);
            
            return MyHashTable.this.containsValue(o);
        }
        
        /**
         * Returns an iterator over the elements in this collection. There are no guarantees
         * concerning the order in which the elements are returned (unless this collection is an
         * instance of some class that provides a guarantee).
         * 
         * @return an Iterator over the elements in this collection
         */
        @Override
        public Iterator<V> iterator() {
            return new ValueIterator();
        }
        
        /**
         * Performs the given action for each element of the Iterable until all elements have been
         * processed or the action throws an exception. Actions are performed in the order of
         * iteration, if that order is specified. Exceptions thrown by the action are relayed to the
         * caller. The behavior of this method is unspecified if the action performs side-effects
         * that modify the underlying source of elements, unless an overriding class has specified a
         * concurrent modification policy.
         * 
         * Implementation Requirements: The default implementation behaves as if:
         * 
         * for (T t : this) action.accept(t);
         * 
         * @param action The action to be performed for each element
         * @throws NullPointerException if the specified action is null
         */
        @Override
        public void forEach(Consumer<? super V> action) {
            Objects.requireNonNull(action);
            
            for (V value : this) {
                action.accept(value);
            }
        }
        
        /**
         * Returns an array containing all of the elements in this collection. If this collection
         * makes any guarantees as to what order its elements are returned by its iterator, this
         * method must return the elements in the same order. The returned array's runtime component
         * type is Object. The returned array will be "safe" in that no references to it are
         * maintained by this collection. (In other words, this method must allocate a new array
         * even if this collection is backed by an array). The caller is thus free to modify the
         * returned array.
         * 
         * This method acts as a bridge between array-based and collection-based APIs. It returns an
         * array whose runtime type is Object[]. Use toArray(T[]) to reuse an existing array, or use
         * toArray(IntFunction) to control the runtime type of the array.
         * 
         * @return an array, whose runtime component type is Object, containing all of the elements
         *             in this collection
         */
        @Override
        public Object[] toArray() {
            Object[] array = new Object[this.size()];
            int i = 0;
            for (V value : this) {
                array[i] = value;
                i++;
            }
            return array;
        }
        
        /**
         * Returns an array containing all of the elements in this collection; the runtime type of
         * the returned array is that of the specified array. If the collection fits in the
         * specified array, it is returned therein. Otherwise, a new array is allocated with the
         * runtime type of the specified array and the size of this collection. If this collection
         * fits in the specified array with room to spare (i.e., the array has more elements than
         * this collection), the element in the array immediately following the end of the
         * collection is set to null. (This is useful in determining the length of this collection
         * only if the caller knows that this collection does not contain any null elements.)
         * 
         * If this collection makes any guarantees as to what order its elements are returned by its
         * iterator, this method must return the elements in the same order.
         * 
         * API Note: This method acts as a bridge between array-based and collection-based APIs. It
         * allows an existing array to be reused under certain circumstances. Use toArray() to
         * create an array whose runtime type is Object[], or use toArray(IntFunction) to control
         * the runtime type of the array. Suppose x is a collection known to contain only strings.
         * The following code can be used to dump the collection into a previously allocated String
         * array:
         * 
         * String[] y = new String[SIZE]; ... y = x.toArray(y); The return value is reassigned to
         * the variable y, because a new array will be allocated and returned if the collection x
         * has too many elements to fit into the existing array y.
         * 
         * Note that toArray(new Object[0]) is identical in function to toArray().
         * 
         * @param T the component type of the array to contain the collection
         * @param a the array into which the elements of this collection are to be stored, if it is
         *              big enough; otherwise, a new array of the same runtime type is allocated for
         *              this purpose. Returns: an array containing all of the elements in this
         *              collection Throws:
         * @throws ArrayStoreException  if the runtime type of any element in this collection is not
         *                                  assignable to the runtime component type of the
         *                                  specified array
         * 
         * @throws NullPointerException if the specified array is null
         */
        @Override
        public <T> T[] toArray(T[] a) {
            Objects.requireNonNull(a);
            
            List<V> list = new ArrayList<>(this.size());
            for (V value : this) {
                list.add(value);
            }
            return list.toArray(a);
        }
        
        /**
         * Returns an array containing all of the elements in this collection, using the provided
         * generator function to allocate the returned array. If this collection makes any
         * guarantees as to what order its elements are returned by its iterator, this method must
         * return the elements in the same order. This method acts as a bridge between array-based
         * and collection-based APIs. It allows creation of an array of a particular runtime type.
         * Use toArray() to create an array whose runtime type is Object[], or use toArray(T[]) to
         * reuse an existing array. Suppose x is a collection known to contain only strings. The
         * following code can be used to dump the collection into a newly allocated array of String:
         * 
         * String[] y = x.toArray(String[]::new); The default implementation calls the generator
         * function with zero and then passes the resulting array to toArray(T[]).
         * 
         * @param T         the component type of the array to contain the collection
         * @param generator a function which produces a new array of the desired type and the
         *                      provided length
         * @return an array containing all of the elements in this collection
         * @throws ArrayStoreException  if the runtime type of any element in this collection is not
         *                                  assignable to the runtime component type of the
         *                                  generated array
         * @throws NullPointerException if the generator function is null
         */
        @Override
        public <T> T[] toArray(IntFunction<T[]> generator) {
            Objects.requireNonNull(generator);
            
            T[] array = generator.apply(this.size());
            return this.toArray(array);
        }
        
        /**
         * Ensures that this collection contains the specified element (optional operation). Returns
         * true if this collection changed as a result of the call. (Returns false if this
         * collection does not permit duplicates and already contains the specified element.)
         * Collections that support this operation may place limitations on what elements may be
         * added to this collection. In particular, some collections will refuse to add null
         * elements, and others will impose restrictions on the type of elements that may be added.
         * Collection classes should clearly specify in their documentation any restrictions on what
         * elements may be added.
         * 
         * If a collection refuses to add a particular element for any reason other than that it
         * already contains the element, it must throw an exception (rather than returning false).
         * This preserves the invariant that a collection always contains the specified element
         * after this call returns.
         * 
         * This implementation does not support add, and always throws an
         * UnsupportedOperationException.
         * 
         * @param e element whose presence in this collection is to be ensured
         * @return nothing: this implementation always throws an exception
         * @throws UnsupportedOperationException always
         */
        @Override
        public boolean add(V e) {
            throw new UnsupportedOperationException();
        }
        
        /**
         * Removes a single instance of the specified element from this collection, if it is present
         * (optional operation). More formally, removes an element e such that Objects.equals(o, e),
         * if this collection contains one or more such elements. Returns true if this collection
         * contained the specified element (or equivalently, if this collection changed as a result
         * of the call).
         * 
         * @param o element to be removed from this collection, if present
         * @return true if an element was removed as a result of this call
         * @throws NullPointerException if the specified element is null
         */
        @Override
        public boolean remove(Object o) {
            Objects.requireNonNull(o);
            
            for (Iterator<V> iterator = this.iterator(); iterator.hasNext();) {
                V value = iterator.next();
                if (value.equals(o)) {
                    iterator.remove();
                    return true;
                }
            }
            return false;
        }
        
        /**
         * Returns true if this collection contains all of the elements in the specified collection.
         * 
         * @param c collection to be checked for containment in this collection
         * @return true if this collection contains all of the elements in the specified collection
         * @throws NullPointerException if the specified collection contains one or more null
         *                                  elements, or if the specified collection is null.
         */
        @Override
        public boolean containsAll(Collection<?> c) {
            Objects.requireNonNull(c);
            
            boolean missingElement = false;
            for (Object element : c) {
                Objects.requireNonNull(element);
                if (!MyHashTable.this.containsValue(element)) {
                    missingElement = true;
                }
            }
            return !missingElement;
        }
        
        /**
         * Adds all of the elements in the specified collection to this collection (optional
         * operation). The behavior of this operation is undefined if the specified collection is
         * modified while the operation is in progress. (This implies that the behavior of this call
         * is undefined if the specified collection is this collection, and this collection is
         * nonempty.)
         * 
         * This implementation does not support this method, and always throws an
         * UnsupportedOperationException.
         * 
         * @param c collection containing elements to be added to this collection
         * @return nothing: this impelmentation always throws an exception.
         * @throws UnsupportedOperationException always
         */
        @Override
        public boolean addAll(Collection<? extends V> c) {
            throw new UnsupportedOperationException();
        }
        
        /**
         * Removes all of this collection's elements that are also contained in the specified
         * collection. After this call returns, this collection will contain no elements in common
         * with the specified collection.
         * 
         * @param c collection containing elements to be removed from this collection
         * @return true if this collection changed as a result of the call
         * @throws NullPointerException if the specified collection is null
         */
        @Override
        public boolean removeAll(Collection<?> c) {
            Objects.requireNonNull(c);
            
            boolean changed = false;
            for (Iterator<V> iterator = this.iterator(); iterator.hasNext();) {
                V value = iterator.next();
                if (c.contains(value)) {
                    iterator.remove();
                    changed = true;
                }
            }
            return changed;
        }
        
        /**
         * Removes all of the elements of this collection that satisfy the given predicate. Errors
         * or runtime exceptions thrown during iteration or by the predicate are relayed to the
         * caller. Implementation Requirements: The default implementation traverses all elements of
         * the collection using its iterator(). Each matching element is removed using
         * Iterator.remove(). If the collection's iterator does not support removal then an
         * UnsupportedOperationException will be thrown on the first matching element.
         * 
         * @param filter a predicate which returns true for elements to be removed
         * @return true if any elements were removed
         * @throws NullPointerException if the specified filter is null
         */
        @Override
        public boolean removeIf(Predicate<? super V> filter) {
            Objects.requireNonNull(filter);
            
            boolean changed = false;
            for (Iterator<V> iterator = this.iterator(); iterator.hasNext();) {
                V value = iterator.next();
                if (filter.test(value)) {
                    iterator.remove();
                    changed = true;
                }
            }
            return changed;
        }
        
        /**
         * Retains only the elements in this collection that are contained in the specified
         * collection. In other words, removes from this collection all of its elements that are not
         * contained in the specified collection.
         * 
         * @param c collection containing elements to be retained in this collection
         * @return true if this collection changed as a result of the call
         * @throws NullPointerException if the specified collection is null
         */
        @Override
        public boolean retainAll(Collection<?> c) {
            Objects.requireNonNull(c);
            
            boolean changed = false;
            for (Iterator<V> iterator = this.iterator(); iterator.hasNext();) {
                V value = iterator.next();
                if (!c.contains(value)) {
                    iterator.remove();
                    changed = true;
                }
            }
            return changed;
        }
        
        /**
         * Removes all of the elements from this collection. The collection will be empty after this
         * method returns.
         */
        @Override
        public void clear() {
            MyHashTable.this.clear();
        }
        
        /**
         * Compares the specified object with this collection for equality. While the Collection
         * interface adds no stipulations to the general contract for the Object.equals, programmers
         * who implement the Collection interface "directly" (in other words, create a class that is
         * a Collection but is not a Set or a List) must exercise care if they choose to override
         * the Object.equals. It is not necessary to do so, and the simplest course of action is to
         * rely on Object's implementation, but the implementor may wish to implement a "value
         * comparison" in place of the default "reference comparison." (The List and Set interfaces
         * mandate such value comparisons.)
         * 
         * The general contract for the Object.equals method states that equals must be symmetric
         * (in other words, a.equals(b) if and only if b.equals(a)). The contracts for List.equals
         * and Set.equals state that lists are only equal to other lists, and sets to other sets.
         * Thus, a custom equals method for a collection class that implements neither the List nor
         * Set interface must return false when this collection is compared to any list or set. (By
         * the same logic, it is not possible to write a class that correctly implements both the
         * Set and List interfaces.)
         * 
         * @param o object to be compared for equality with this collection
         * @return true if the specified object is equal to this collection
         */
        /*
         * The only guarantee here is that there should only be one instance of this class
         * instantiated at any time.Since this implementation cannot guarantee that other collection
         * implementations will be equal to it, rely on Object's implementation, which tests for
         * reference equality.
         */
        @Override
        public boolean equals(Object o) {
            return super.equals(o);
        }
        
        /**
         * Returns the hash code value for this collection. While the Collection interface adds no
         * stipulations to the general contract for the Object.hashCode method, programmers should
         * take note that any class that overrides the Object.equals method must also override the
         * Object.hashCode method in order to satisfy the general contract for the Object.hashCode
         * method. In particular, c1.equals(c2) implies that c1.hashCode()==c2.hashCode().
         * 
         * @return the hash code value for this collection
         */
        @Override
        public int hashCode() {
            return super.hashCode();
        }
        
        /**
         * Returns a string representation of this collection. The string representation consists of
         * a list of the collection's elements in the order they are returned by its iterator,
         * enclosed in square brackets ("[]"). Adjacent elements are separated by the characters ",
         * " (comma and space). Elements are converted to strings by toString(), since this
         * collection will contain no null elements.
         * 
         * While this is not required by the Collection interface, it makes this much easier to
         * debug and use.
         */
        @Override
        public String toString() {
            StringJoiner joiner = new StringJoiner(", ", "[", "]");
            for (V value : this) {
                joiner.add(value.toString());
            }
            return joiner.toString();
        }
        
        /**
         * Creates a Spliterator over the elements in this collection. Implementations should
         * document characteristic values reported by the spliterator. Such characteristic values
         * are not required to be reported if the spliterator reports Spliterator.SIZED and this
         * collection contains no elements. The default implementation should be overridden by
         * subclasses that can return a more efficient spliterator. In order to preserve expected
         * laziness behavior for the stream() and parallelStream() methods, spliterators should
         * either have the characteristic of IMMUTABLE or CONCURRENT, or be late-binding. If none of
         * these is practical, the overriding class should describe the spliterator's documented
         * policy of binding and structural interference, and should override the stream() and
         * parallelStream() methods to create streams using a Supplier of the spliterator, as in:
         * 
         * Stream<E> s = StreamSupport.stream(() -> spliterator(), spliteratorCharacteristics)
         * 
         * These requirements ensure that streams produced by the stream() and parallelStream()
         * methods will reflect the contents of the collection as of initiation of the terminal
         * stream operation.
         * 
         * Specified by: spliterator in interface Iterable<E> Implementation Requirements: The
         * default implementation creates a late-binding spliterator from the collection's Iterator.
         * The spliterator inherits the fail-fast properties of the collection's iterator. The
         * created Spliterator reports Spliterator.SIZED.
         * 
         * Implementation Note: The created Spliterator additionally reports Spliterator.SUBSIZED.
         * If a spliterator covers no elements then the reporting of additional characteristic
         * values, beyond that of SIZED and SUBSIZED, does not aid clients to control, specialize or
         * simplify computation. However, this does enable shared use of an immutable and empty
         * spliterator instance (see Spliterators.emptySpliterator()) for empty collections, and
         * enables clients to determine if such a spliterator covers no elements.
         * 
         * @return a Spliterator over the elements in this collection
         */
        @Override
        public Spliterator<V> spliterator() {
            return Spliterators.spliterator(this, Spliterator.NONNULL | Spliterator.SIZED | Spliterator.SUBSIZED);
        }
        
        /**
         * Returns a sequential Stream with this collection as its source. This method should be
         * overridden when the spliterator() method cannot return a spliterator that is IMMUTABLE,
         * CONCURRENT, or late-binding. (See spliterator() for details.)
         * 
         * @return a sequential Stream over the elements in this collection
         */
        @Override
        public Stream<V> stream() {
            return StreamSupport.stream(this.spliterator(), false);
        }
        
        /**
         * Returns a possibly parallel Stream with this collection as its source. It is allowable
         * for this method to return a sequential stream. This method should be overridden when the
         * spliterator() method cannot return a spliterator that is IMMUTABLE, CONCURRENT, or
         * late-binding. (See spliterator() for details.)
         * 
         * @return a possibly parallel Stream over the elements in this collection
         */
        
        @Override
        public Stream<V> parallelStream() {
            return StreamSupport.stream(this.spliterator(), true);
        }
    }
    
    /**
     * Returns a Set view of the mappings contained in this map. The set is backed by the map, so
     * changes to the map are reflected in the set, and vice-versa. If the map is modified while an
     * iteration over the set is in progress (except through the iterator's own remove operation, or
     * through the setValue operation on a map entry returned by the iterator) the results of the
     * iteration are undefined. The set supports element removal, which removes the corresponding
     * mapping from the map, via the Iterator.remove, Set.remove, removeAll, retainAll and clear
     * operations. It does not support the add or addAll operations.
     * 
     * @return a set view of the mappings contained in this map
     */
    @Override
    public Set<Entry<K, V>> entrySet() {
        return entrySet;
    }
    
    /**
     * 
     */
    private class EntrySet implements Set<Entry<K, V>> {
        
        /**
         * Returns the number of elements in this set (its cardinality). If this set contains more
         * than Integer.MAX_VALUE elements, returns Integer.MAX_VALUE.
         * 
         * @return the number of elements in this set (its cardinality)
         */
        @Override
        public int size() {
            return MyHashTable.this.size();
        }
        
        /**
         * Returns true if this set contains no elements.
         * 
         * @return true if this set contains no elements
         */
        @Override
        public boolean isEmpty() {
            return this.size() == 0;
        }
        
        /**
         * Returns true if this set contains the specified element. More formally, returns true if
         * and only if this set contains an element e such that Objects.equals(o, e).
         * 
         * @param o element whose presence in this set is to be tested
         * @return true if this set contains the specified element
         * @throws NullPointerException if the specified element is null
         */
        @Override
        public boolean contains(Object o) {
            Objects.requireNonNull(o);
            if (!(o instanceof Entry<?, ?>)) {
                return false;
            }
            Entry<?, ?> entry = (Entry<?, ?>) o;
            Object key = Objects.requireNonNull(entry.getKey());
            Object value = Objects.requireNonNull(entry.getValue());
            V storedValue = MyHashTable.this.get(key);
            return value.equals(storedValue);
        }
        
        /**
         * Returns an iterator over the elements in this set. The elements are returned in no
         * particular order
         * 
         * @return an iterator over the elements in this set
         */
        @Override
        public Iterator<Entry<K, V>> iterator() {
            return new EntryIterator();
        }
        
        /**
         * Performs the given action for each element of the Iterable until all elements have been
         * processed or the action throws an exception. Actions are performed in the order of
         * iteration, if that order is specified. Exceptions thrown by the action are relayed to the
         * caller. The behavior of this method is unspecified if the action performs side-effects
         * that modify the underlying source of elements, unless an overriding class has specified a
         * concurrent modification policy.
         * 
         * Implementation Requirements: The default implementation behaves as if:
         * 
         * for (T t : this) action.accept(t);
         * 
         * @param action The action to be performed for each element
         * @throws NullPointerException if the specified action is null
         */
        @Override
        public void forEach(Consumer<? super Entry<K, V>> action) {
            Objects.requireNonNull(action);
            
            for (Entry<K, V> entry : this) {
                action.accept(entry);
            }
        }
        
        /**
         * Returns an array containing all of the elements in this set, in no particular order. The
         * returned array will be "safe" in that no references to it are maintained by this set. (In
         * other words, this method must allocate a new array even if this set is backed by an
         * array). The caller is thus free to modify the returned array. This method acts as bridge
         * between array-based and collection-based APIs.
         * 
         * @return an array containing all the elements in this set
         */
        @Override
        public Object[] toArray() {
            Object[] array = new Object[this.size()];
            int i = 0;
            for (Entry<K, V> entry : this) {
                array[i] = entry;
                i++;
            }
            return array;
        }
        
        /**
         * Returns an array containing all of the elements in this set; the runtime type of the
         * returned array is that of the specified array. If the set fits in the specified array, it
         * is returned therein. Otherwise, a new array is allocated with the runtime type of the
         * specified array and the size of this set. If this set fits in the specified array with
         * room to spare (i.e., the array has more elements than this set), the element in the array
         * immediately following the end of the set is set to null. (This is useful in determining
         * the length of this set since the caller knows that this set does not contain any null
         * elements.) This set makes no guarantees as to what order its elements are returned by its
         * iterator. Like the toArray() method, this method acts as bridge between array-based and
         * collection-based APIs. Further, this method allows precise control over the runtime type
         * of the output array, and may, under certain circumstances, be used to save allocation
         * costs. Suppose x is a set known to contain only strings. The following code can be used
         * to dump the set into a newly allocated array of String: String[] y = x.toArray(new
         * String[0]); Note that toArray(new Object[0]) is identical in function to toArray().
         * 
         * @param T the component type of the array to contain the collection
         * @param a the array into which the elements of this set are to be stored, if it is big
         *              enough; otherwise, a new array of the same runtime type is allocated for
         *              this purpose.
         * @return an array containing all the elements in this set
         * @throws ArrayStoreException  if the runtime type of the specified array is not a
         *                                  supertype of the runtime type of every element in this
         *                                  set
         * @throws NullPointerException if the specified array is null
         */
        @Override
        public <T> T[] toArray(T[] a) {
            List<Entry<K, V>> list = new ArrayList<>(this.size());
            for (Entry<K, V> entry : this) {
                list.add(entry);
            }
            return list.toArray(a);
        }
        
        /**
         * Returns an array containing all of the elements in this collection, using the provided
         * generator function to allocate the returned array. If this collection makes any
         * guarantees as to what order its elements are returned by its iterator, this method must
         * return the elements in the same order. This method acts as a bridge between array-based
         * and collection-based APIs. It allows creation of an array of a particular runtime type.
         * Use toArray() to create an array whose runtime type is Object[], or use toArray(T[]) to
         * reuse an existing array. Suppose x is a collection known to contain only strings. The
         * following code can be used to dump the collection into a newly allocated array of String:
         * 
         * String[] y = x.toArray(String[]::new); The default implementation calls the generator
         * function with zero and then passes the resulting array to toArray(T[]).
         * 
         * @param T         the component type of the array to contain the collection
         * @param generator a function which produces a new array of the desired type and the
         *                      provided length
         * @return an array containing all of the elements in this collection
         * @throws ArrayStoreException  if the runtime type of any element in this collection is not
         *                                  assignable to the runtime component type of the
         *                                  generated array
         * @throws NullPointerException if the generator function is null
         */
        @Override
        public <T> T[] toArray(IntFunction<T[]> generator) {
            Objects.requireNonNull(generator);
            
            T[] array = generator.apply(this.size());
            return this.toArray(array);
        }
        
        /**
         * Adds the specified element to this set if it is not already present (optional operation).
         * This method is not supported by this implementation.
         * 
         * @implSpec This implementation throws an UnsupportedOperationException.
         * @param e element to be added to this set
         * @return nothing, as an exception is always thrown
         * @throws UnsupportedOperationException always
         */
        @Override
        public boolean add(Entry<K, V> e) {
            throw new UnsupportedOperationException();
        }
        
        /**
         * Removes the specified element from this set if it is present. More formally, removes an
         * element e such that Objects.equals(o, e), if this set contains such an element. Returns
         * true if this set contained the element (or equivalently, if this set changed as a result
         * of the call). (This set will not contain the element once the call returns.)
         * 
         * @param o object to be removed from this set, if present
         * @return true if this set contained the specified element
         * @throws NullPointerException if the specified element is null
         */
        @Override
        public boolean remove(Object o) {
            Objects.requireNonNull(o);
            
            if (!(o instanceof Entry<?, ?>)) {
                return false;
            }
            Entry<?, ?> entry = (Entry<?, ?>) o;
            Object key = Objects.requireNonNull(entry.getKey());
            Object value = Objects.requireNonNull(entry.getValue());
            V storedValue = MyHashTable.this.get(key);
            if (value.equals(storedValue)) {
                return MyHashTable.this.remove(key) != null;
            } else {
                return false;
            }
            
        }
        
        /**
         * Returns true if this set contains all of the elements of the specified collection. If the
         * specified collection is also a set, this method returns true if it is a subset of this
         * set.
         * 
         * @param c collection to be checked for containment in this set
         * @return true if this set contains all of the elements of the specified collection
         * @throws NullPointerException if the specified collection contains one or more null
         *                                  elements, or if the specified collection is null
         */
        @Override
        public boolean containsAll(Collection<?> c) {
            Objects.requireNonNull(c);
            
            for (Object element : c) {
                if (!(element instanceof Entry<?, ?>)) {
                    return false;
                }
                Entry<?, ?> entry = (Entry<?, ?>) element;
                Object key = Objects.requireNonNull(entry.getKey());
                Object value = Objects.requireNonNull(entry.getValue());
                V storedValue = MyHashTable.this.get(key);
                if (!storedValue.equals(value)) {
                    return false;
                }
            }
            return true;
        }
        
        /**
         * Adds all of the elements in the specified collection to this set if they're not already
         * present (optional operation). If the specified collection is also a set, the addAll
         * operation effectively modifies this set so that its value is the union of the two sets.
         * This behavior is not supported by this implementation.
         * 
         * @param c collection containing elements to be added to this set
         * @return nothing: this implementation does not support this method
         * @throws UnsupportedOperationException always
         */
        @Override
        public boolean addAll(Collection<? extends Entry<K, V>> c) {
            throw new UnsupportedOperationException();
        }
        
        /**
         * Retains only the elements in this set that are contained in the specified collection. In
         * other words, removes from this set all of its elements that are not contained in the
         * specified collection. If the specified collection is also a set, this operation
         * effectively modifies this set so that its value is the intersection of the two sets.
         * 
         * @param c collection containing elements to be retained in this set
         * @return true if this set changed as a result of the call
         * @throws NullPointerException if the specified collection is null
         */
        @Override
        public boolean retainAll(Collection<?> c) {
            Objects.requireNonNull(c);
            
            boolean modified = false;
            for (Iterator<Entry<K, V>> iterator = this.iterator(); iterator.hasNext();) {
                Entry<K, V> entry = iterator.next();
                if (!c.contains(entry)) {
                    iterator.remove();
                    modified = true;
                }
            }
            return modified;
        }
        
        /**
         * Removes from this set all of its elements that are contained in the specified collection.
         * If the specified collection is also a set, this operation effectively modifies this set
         * so that its value is the asymmetric set difference of the two sets.
         * 
         * @param c collection containing elements to be removed from this set
         * @return true if this set changed as a result of the call
         * @throws NullPointerException if the specified collection is null
         */
        @Override
        public boolean removeAll(Collection<?> c) {
            Objects.requireNonNull(c);
            
            boolean modified = false;
            for (Iterator<Entry<K, V>> iterator = this.iterator(); iterator.hasNext();) {
                Entry<K, V> entry = iterator.next();
                if (c.contains(entry)) {
                    iterator.remove();
                    modified = true;
                }
            }
            return modified;
        }
        
        /**
         * Removes all of the elements of this collection that satisfy the given predicate. Errors
         * or runtime exceptions thrown during iteration or by the predicate are relayed to the
         * caller. Implementation Requirements: The default implementation traverses all elements of
         * the collection using its iterator(). Each matching element is removed using
         * Iterator.remove(). If the collection's iterator does not support removal then an
         * UnsupportedOperationException will be thrown on the first matching element.
         * 
         * @param filter a predicate which returns true for elements to be removed
         * @return true if any elements were removed
         * @throws NullPointerException if the specified filter is null
         */
        @Override
        public boolean removeIf(Predicate<? super Entry<K, V>> filter) {
            Objects.requireNonNull(filter);
            
            boolean changed = false;
            for (Iterator<Entry<K, V>> iterator = this.iterator(); iterator.hasNext();) {
                Entry<K, V> entry = iterator.next();
                if (filter.test(entry)) {
                    iterator.remove();
                    changed = true;
                }
            }
            return changed;
        }
        
        /**
         * Removes all of the elements from this set. The set will be empty after this call returns.
         */
        @Override
        public void clear() {
            MyHashTable.this.clear();
        }
        
        /**
         * Compares the specified object with this set for equality. Returns true if the specified
         * object is also a set, the two sets have the same size, and every member of the specified
         * set is contained in this set (or equivalently, every member of this set is contained in
         * the specified set). This definition ensures that the equals method works properly across
         * different implementations of the set interface.
         * 
         * @param o object to be compared for equality with this set
         * @return true if the specified object is equal to this set
         */
        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof Set<?>)) {
                return false;
            }
            Set<?> other = (Set<?>) o;
            return this.size() == other.size() && this.containsAll(other);
        }
        
        /**
         * Returns the hash code value for this set. The hash code of a set is defined to be the sum
         * of the hash codes of the elements in the set, where the hash code of a null element is
         * defined to be zero. This ensures that s1.equals(s2) implies that
         * s1.hashCode()==s2.hashCode() for any two sets s1 and s2, as required by the general
         * contract of Object.hashCode().
         * 
         * @return the hash code value for this set
         */
        @Override
        public int hashCode() {
            return this.stream().mapToInt(Object::hashCode).sum();
        }
        
        @Override
        public String toString() {
            StringJoiner joiner = new StringJoiner(", ", "[", "]");
            for (Entry<K, V> entry : this) {
                joiner.add(entry.toString());
            }
            return joiner.toString();
        }
        
        /**
         * Creates a Spliterator over the elements in this set. The Spliterator reports
         * Spliterator.DISTINCT, Spliterator.SIZED, Spliterator.SUBSIZED, and Spliterator.NONNULL.
         * This implementation creates a late-binding spliterator from the set's Iterator.
         * 
         * @return a Spliterator over the elements in this set
         */
        @Override
        public Spliterator<Entry<K, V>> spliterator() {
            return Spliterators.spliterator(this,
                    Spliterator.DISTINCT | Spliterator.NONNULL | Spliterator.SIZED | Spliterator.SUBSIZED);
        }
        
        /**
         * Returns a sequential Stream with this collection as its source. This method should be
         * overridden when the spliterator() method cannot return a spliterator that is IMMUTABLE,
         * CONCURRENT, or late-binding. (See spliterator() for details.)
         * 
         * @return a sequential Stream over the elements in this collection
         */
        @Override
        public Stream<Entry<K, V>> stream() {
            return StreamSupport.stream(this.spliterator(), false);
        }
        
        /**
         * Returns a possibly parallel Stream with this collection as its source. It is allowable
         * for this method to return a sequential stream. This method should be overridden when the
         * spliterator() method cannot return a spliterator that is IMMUTABLE, CONCURRENT, or
         * late-binding. (See spliterator() for details.)
         * 
         * @return a possibly parallel Stream over the elements in this collection
         */
        
        @Override
        public Stream<Entry<K, V>> parallelStream() {
            return StreamSupport.stream(this.spliterator(), true);
        }
    }
    
    // -------------------------------------------------------------------------
    // Iterators
    // -------------------------------------------------------------------------
    
    /**
     * An iterator over the nodes of this hashtable.
     * 
     * @param <E> the type of elements returned by this iterator
     */
    private abstract class HashIterator<E> implements Iterator<E> {
        private Node<K, V>[] table = MyHashTable.this.table;
        private int index = 0;
        private Node<K, V> currentNode = null;
        private Node<K, V> nextNode;
        
        /**
         * Creates an instance of an iterator that iterates over the outer hashtable's table. This
         * does not account for an empty HashTable:
         */
        private HashIterator() {
            while (index < table.length && table[index] == null) {
                index++;
            }
            if (index == table.length) {
                this.nextNode = null;
            } else {
                this.nextNode = table[index];
            }
        }
        
        /**
         * Returns true if the iteration has more elements. (In other words, returns true if next()
         * would return an element rather than throwing an exception.)
         * 
         * @return true if the iteration has more elements
         */
        @Override
        public boolean hasNext() {
            return nextNode != null;
        }
        
        /**
         * Returns the next node in the iteration.
         * 
         * @return the next node in the iteration
         * @throws NoSuchElementException if the iteration has no more elements
         */
        public final Node<K, V> nextNode() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            currentNode = nextNode;
            nextNode = currentNode.next;
            while (nextNode == null && index < table.length) {
                index++;
                if (index < table.length) {
                    nextNode = table[index];
                }
            }
            return currentNode;
        }
        
        /**
         * Removes from the underlying collection the last element returned by this iterator. This
         * method can be called only once per call to next(). The behavior of this iterator is
         * unspecified if the underlying collection is modified while the iteration is in progress
         * in any way other than by calling this method. The behavior of an iterator is unspecified
         * if this method is called after a call to the forEachRemaining method.
         * 
         * @throws IllegalStateException if the next method has not yet been called, or the remove
         *                                   method has already been called after the last call to
         *                                   the next method
         */
        @Override
        public void remove() {
            if (currentNode == null) {
                throw new IllegalStateException();
            }
            MyHashTable.this.remove(currentNode.key);
            currentNode = null;
        }
        
        /**
         * Performs the given action for each remaining element until all elements have been
         * processed or the action throws an exception. Actions are performed in the order of
         * iteration, if that order is specified. Exceptions thrown by the action are relayed to the
         * caller. The behavior of an iterator is unspecified if the action modifies the collection
         * in any way (even by calling the remove method or other mutator methods of Iterator
         * subtypes), unless an overriding class has specified a concurrent modification policy.
         * 
         * Subsequent behavior of an iterator is unspecified if the action throws an exception.
         * 
         * @param action The action to be performed for each element
         * @throws NullPointerException if the specified action is null
         */
        @Override
        public void forEachRemaining(Consumer<? super E> action) {
            Objects.requireNonNull(action);
            
            while (this.hasNext()) {
                action.accept(this.next());
            }
        }
        
        /**
         * Returns a string representation of this iterator that represents what it iterates over.
         * It also textually represents whether the iterator has more elements, although code should
         * use the hasNext() method rather than parsing this String. This format is subject to
         * change in future versions.
         * 
         * @return a string representation of this iterator
         */
        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("Iterator for ").append(this.type()).append(": hasNext=").append(this.hasNext());
            return builder.toString();
        }
        
        public abstract String type();
    }
    
    private final class KeyIterator extends HashIterator<K> {
        
        @Override
        public K next() {
            return this.nextNode().getKey();
        }
        
        @Override
        public String type() {
            return "keys";
        }
    }
    
    private final class ValueIterator extends HashIterator<V> {
        
        @Override
        public V next() {
            return this.nextNode().getValue();
        }
        
        @Override
        public String type() {
            return "values";
        }
    }
    
    private final class EntryIterator extends HashIterator<Entry<K, V>> {
        @Override
        public Entry<K, V> next() {
            return this.nextNode();
        }
        
        @Override
        public String type() {
            return "entries";
        }
    }
}
// TODO clean up JavaDocs, more efficient implementations, rehashing, ConcurrentModificationException
// TODO possibly refactor (after efficient implementations) to reuse patterns (replacing entry values, etc)
// TODO stretch goal: serialization, cloneable, elements() + keys() -> Enumeration (legacy, but an interesting exercise)
// TODO Spliterator implementations
// TODO possible base class for collection views
