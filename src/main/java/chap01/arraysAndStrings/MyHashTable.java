package chap01.arraysAndStrings;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.StringJoiner;

/**
 * This class represents a working implementation of the Map Interface via a hashtable. It supports
 * all of the optional operations. It rejects all null keys and values. It is not thread-safe.
 *
 * @param <K> the type of keys maintained by this map
 * @param <V> the type of mapped values
 */
public final class MyHashTable<K, V> implements Map<K, V> {

    /**
     * This class represents a single entry in the map, notated by "Nodes" that make up a singly
     * linked list. Note that K and V in this inner class do not necessarily correspond to the
     * outer class's type parameters (although they will in practice).
     * 
     * @param <K> the type of key a in the map this node belongs to
     * @param <V> the type of mapped values for the map this node belongs to
     */
    private static class Node<K, V> implements Map.Entry<K, V> {
        final K key;
        V value;
        Node<K, V> next = null;

        /**
         * Creates a Node holding the given key and value.
         * 
         * @throws NullPointerException if either the key or value are null.
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
         * backing map (by the iterator's remove operation), the results of this call are
         * undefined.
         * 
         * @return the value corresponding to this entry
         */
        @Override
        public V getValue() {
            return value;
        }

        /**
         * Replaces the value corresponding to this entry with the specified value. (Writes through
         * to the map.) The behavior of this call is undefined if the mapping has already been
         * removed from the map (by the iterator's remove operation).
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
         * object is also a map entry and the two entries represent the same mapping. More
         * formally, two entries e1 and e2 represent the same mapping if
         * (e1.getKey()==null ? e2.getKey()==null : e1.getKey().equals(e2.getKey())) &&
         * (e1.getValue()==null ? e2.getValue()==null : e1.getValue().equals(e2.getValue()))
         * 
         * This ensures that the equals method works properly across different implementations of
         * the Map.Entry interface.
         * 
         * @param o object to be compared for equality with this map entry
         * @return true if the specified object is equal to this map entry
         */
        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Map.Entry<?, ?>)) {
                return false;
            }
            Map.Entry<?, ?> entry = (Map.Entry<?, ?>) o;
            return Objects.equals(this.key, entry.getKey()) &&
                   Objects.equals(this.value, entry.getValue());
        }

        /**
         * Returns the hash code value for this map entry. The hash code of a map entry e is
         * defined to be (e.getKey()==null   ? 0 : e.getKey().hashCode()) ^
         *               (e.getValue()==null ? 0 : e.getValue().hashCode())
         * This ensures that e1.equals(e2) implies that e1.hashCode()==e2.hashCode() for any two
         * Entries e1 and e2, as required by the general contract of Object.hashCode.
         * 
         * @return the hash code value for this map entry
         */
       @Override
       public int hashCode() {
           return key.hashCode() ^ value.hashCode();
       }

       /**
        * Returns a String representation of this map entry. This implementation returns the
        * string representation of this entry's key followed by the equals character ("=") followed
        * by the string representation of this entry's value.
        * 
        * @return a String representation of this map entry
        */
       @Override
       public String toString() {
           return key + "=" + value;
       }
    }

    private Node<K, V>[] table;
    private int size = 0;

    // Cached collections
    private Set<Entry<K, V>> entrySet;
    private Set<K> keySet;
    private Collection<V> values;

    @SuppressWarnings("unchecked")
    public MyHashTable() {
        table = (Node<K, V>[]) new Node<?, ?>[10];
    }

    /**
     * Returns the number of key-value mappings in this map. If the map contains more than
     * Integer.MAX_VALUE elements, returns Integer.MAX_VALUE.
     * 
     * @return the number of key-value mappings in this map
     */
    @Override
    public int size() {
        return size;
    }

    /**
     * Returns true if this map contains no key-value mappings.
     * 
     * @return true if this map contains no key-value mappings
     */
    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Returns true if this map contains a mapping for the specified key. More formally, returns
     * true if and only if this map contains a mapping for a key k such that
     * Objects.equals(key, k). (There can be at most one such mapping.)
     * 
     * @param key key whose presence in this map is to be tested
     * @return true if this map contains a mapping for the specified key
     * @throws NullPointerException if the specified key is null
     */
    @Override
    public boolean containsKey(Object key) {
        Objects.requireNonNull(key);

        int index = hashToIndex(key);
        for (Node<K, V> node = table[index]; node != null; node = node.next) {
            if (node.key.equals(key)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns true if this map maps one or more keys to the specified value. More formally,
     * returns true if and only if this map contains at least one mapping to a value v such that
     * Objects.equals(value, v).
     * 
     * @param value - value whose presence in this map is to be tested
     * @return true if this map maps one or more keys to the specified value
     * @throws NullPointerException if the specified value is null
     */
    @Override
    public boolean containsValue(Object value) {
        Objects.requireNonNull(value);

        for (Node<K, V> node : table) {
            while (node != null) {
                if (node.value.equals(value)) {
                    return true;
                } else {
                    node = node.next;
                }
            }
        }
        return false;
    }

    /**
     * Returns the value to which the specified key is mapped, or null if this map contains no
     * mapping for the key. More formally, if this map contains a mapping from a key k to a value v
     * such that Objects.equals(key, k), then this method returns v; otherwise it returns null.
     * (There can be at most one such mapping.)
     * 
     * @param key the key whose associated value is to be returned
     * @return the value to which the specified key is mapped, or null if this map contains no
     *         mapping for the key
     * @throws NullPointerException if the specified key is null
     */
    @Override
    public V get(Object key) {
        Objects.requireNonNull(key);

        int index = hashToIndex(key);
        for (Node<K, V> node = table[index]; node != null; node = node.next) {
            if (node.key.equals(key)) {
                return node.value;
            }
        }
        return null;        
    }

    /**
     * Associates the specified value with the specified key in this map. If the map previously
     * contained a mapping for the key, the old value is replaced by the specified value. (A map m
     * is said to contain a mapping for a key k if and only if m.containsKey(k) would return true.)
     * 
     * @param key key with which the specified value is to be associated
     * @param value value to be associated with the specified key
     * @return the previous value associated with key, or null if there was no mapping for key.
     * @throws NullPointerException if the specified key or value is null
     */
    @Override    
    public V put(K key, V value) {
        Objects.requireNonNull(key);
        Objects.requireNonNull(value);

        int index = hashToIndex(key);
        Node<K, V> lastNode = null;
        for (Node<K, V> node = table[index]; node != null; node = node.next) {
            if (node.key.equals(key)) {
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
        size++;
        return null;

    }

    /**
     * Removes the mapping for a key from this map if it is present. More formally, if this map
     * contains a mapping from key k to value v such that Objects.equals(key, k), that mapping is
     * removed. (The map can contain at most one such mapping.) Returns the value to which this map
     * previously associated the key, or null if the map contained no mapping for the key. The map
     * will not contain a mapping for the specified key once the call returns.
     * 
     * @param key key whose mapping is to be removed from the map
     * @return the previous value associated with key, or null if there was no mapping for key.
     * @throws NullPointerException if the specified key is null
     */
    @Override
    public V remove(Object key) {
        Objects.requireNonNull(key);

        int index = hashToIndex(key);
        for (Node<K, V> node = table[index], prevNode = null; node != null; node = node.next) {
            if (node.key.equals(key)) {
                V value = node.value;
                if (prevNode != null) {
                    prevNode.next = node.next;
                }
                size--;
                return value;
            }
        }
        return null;
    }

    /**
     * Copies all of the mappings from the specified map to this map. The effect of this call is
     * equivalent to that of calling put(k, v) on this map once for each mapping from key k to
     * value v in the specified map. The behavior of this operation is undefined if the specified
     * map is modified while the operation is in progress.
     * 
     * @param m mappings to be stored in this map
     * @throws NullPointerException if the specified map is null, or if the specified map contains
     *                              null keys or values
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
     * Returns a Set view of the keys contained in this map. The set is backed by the map, so
     * changes to the map are reflected in the set, and vice-versa. If the map is modified while an
     * iteration over the set is in progress (except through the iterator's own remove operation),
     * the results of the iteration are undefined. The set supports element removal,vwhich removes
     * the corresponding mapping from the map, via the Iterator.remove, Set.remove, removeAll,
     * retainAll, and clear operations. It does not support the add or addAll operations.
     * 
     * @return a set view of the keys contained in this map
     */
    @Override
    public Set<K> keySet() {
        if (keySet == null) {
            keySet = new KeySet<K>();
        }
        return keySet;
    }

    /**
     * The stateless KeySet delegates its calls to the outer HashTable instance.
     * @param <E> the type of keys maintained by this map
     */
    private class KeySet<E> implements Set<E> {
        /**
         * 
         */
        @Override
        public int size() {
            return size;
        }

        @Override
        public boolean isEmpty() {
            return size == 0;
        }

        @Override
        public boolean contains(Object o) {
            return containsKey(o);
        }

        @Override
        public Iterator<E> iterator() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public Object[] toArray() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public <T> T[] toArray(T[] a) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public boolean add(E e) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(Object o) {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public boolean containsAll(Collection<?> c) {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public boolean addAll(Collection<? extends E> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean retainAll(Collection<?> c) {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public void clear() {
            // TODO Auto-generated method stub
            
        }
    }

    /**
     * Returns a Collection view of the values contained in this map. The collection is backed by
     * the map, so changes to the map are reflected in the collection, and vice-versa. If the map
     * is modified while an iteration over the collection is in progress (except through the
     * iterator's own remove operation), the results of the iteration are undefined. The collection
     * supports element removal, which removes the corresponding mapping from the map, via the
     * Iterator.remove, Collection.remove, removeAll, retainAll and clear operations. It does not
     * support the add or addAll operations.
     * 
     * @return a collection view of the values contained in this map
     */
    @Override
    public Collection<V> values() {
        if (values == null) {
            values = new Values<V>();
        }
        return values;
    }

    /**
     *
     * @param <E> @param <E> the type of values maintained by this map
     */
    private class Values<E> implements Collection<E> {

        @Override
        public int size() {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public boolean isEmpty() {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public boolean contains(Object o) {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public Iterator<E> iterator() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public Object[] toArray() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public <T> T[] toArray(T[] a) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public boolean add(E e) {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public boolean remove(Object o) {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public boolean containsAll(Collection<?> c) {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public boolean addAll(Collection<? extends E> c) {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public boolean retainAll(Collection<?> c) {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public void clear() {
            // TODO Auto-generated method stub
            
        }
    }

    /**
     * Returns a Set view of the mappings contained in this map. The set is backed by the map, so
     * changes to the map are reflected in the set, and vice-versa. If the map is modified while an
     * iteration over the set is in progress (except through the iterator's own remove operation,
     * or through the setValue operation on a map entry returned by the iterator) the results of
     * the iteration are undefined. The set supports element removal, which removes the
     * corresponding mapping from the map, via the Iterator.remove, Set.remove, removeAll,
     * retainAll and clear operations. It does not support the add or addAll operations.
     * 
     * @return a set view of the mappings contained in this map
     */
    @Override
    public Set<Entry<K, V>> entrySet() {
        if (entrySet == null) {
            entrySet = new EntrySet<K, V>();
        }
        return entrySet;
    }

    /**
     * 
     * Note that the K and V of this class shadow the outer class: this cannot refer to the K and V
     * types of the parent.
     * 
     * @param <K> the type of keys maintained by this map
     * @param <V> the type of keys maintained by this map
     */
    private class EntrySet<K, V> implements Set<Entry<K, V>> {

        @Override
        public int size() {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public boolean isEmpty() {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public boolean contains(Object o) {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public Iterator<Entry<K, V>> iterator() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public Object[] toArray() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public <T> T[] toArray(T[] a) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public boolean add(Entry<K, V> e) {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public boolean remove(Object o) {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public boolean containsAll(Collection<?> c) {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public boolean addAll(Collection<? extends Entry<K, V>> c) {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public boolean retainAll(Collection<?> c) {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public void clear() {
            // TODO Auto-generated method stub
            
        }
    }

    /**
     * 
     * 
     * @param <E> the type of elements returned by this iterator
     */
    private class Iter<E> implements Iterator<E> {

        /**
         * Returns true if the iteration has more elements. (In other words, returns true if next()
         * would return an element rather than throwing an exception.)
         * 
         * @return true if the iteration has more elements
         */
        @Override
        public boolean hasNext() {
            // TODO Auto-generated method stub
            return false;
        }

        /**
         * Returns the next element in the iteration.
         * 
         * @return the next element in the iteration
         * @throws NoSuchElementException if the iteration has no more elements
         */
        @Override
        public E next() {
            // TODO Auto-generated method stub
            return null;
        }
        
        /**
         * Removes from the underlying collection the last element returned by this iterator. This
         * method can be called only once per call to next(). The behavior of this iterator is
         * unspecified if the underlying collection is modified while the iteration is in progress
         * in any way other than by calling this method.
         * The behavior of an iterator is unspecified if this method is called after a call to the
         * forEachRemaining method.
         * 
         * @throws IllegalStateException if the next method has not yet been called, or the remove
         *                               method has already been called after the last call to the
         *                               next method
         */
        @Override
        public void remove() {
            // TODO Auto-generated method stub
        }

    }

    /**
     * Compares the specified object with this map for equality. Returns true if the given object
     * is also a map and the two maps represent the same mappings. More formally, two maps m1 and
     * m2 represent the same mappings if m1.entrySet().equals(m2.entrySet()). This ensures that the
     * equals method works properly across different implementations of the Map interface.
     * 
     * @param o object to be compared for equality with this map
     * @return true if the specified object is equal to this map
     */

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Map<?, ?>)) {
            return false;
        }
        Map<?, ?> map = (Map<?, ?>)o;
        return this.entrySet().equals(map.entrySet());
    }

    /** Returns the hash code value for this map. The hash code of a map is defined to be the sum
     *  of the hash codes of each entry in the map's entrySet() view. This ensures that
     *  m1.equals(m2) implies that m1.hashCode()==m2.hashCode() for any two maps m1 and m2, as
     *  required by the general contract of Object.hashCode().
     *  
     *  @return the hash code value for this map
     */
    @Override
    public int hashCode() {
        return this.entrySet().hashCode();
    }

    /** Returns a string representation of this map. The string representation consists of a list
     * of key-value mappings in the order returned by the map's entrySet view's iterator, enclosed
     * in braces ("{}"). Adjacent mappings are separated by the characters ", " (comma and space).
     * Each key-value mapping is rendered as the key followed by an equals sign ("=") followed by
     * the associated value. Keys and values are converted to strings as by String.valueOf(Object).
     * 
     * @return a string representation of this map
     */
    @Override
    public String toString() {
        StringJoiner resultJoiner = new StringJoiner(", ", "{", "}");
        for (Entry<K, V> entry : entrySet()) {
            resultJoiner.add(entry.toString());
        }
        return resultJoiner.toString();
    }

    private int hashToIndex(Object obj) {
        int index = obj.hashCode() % table.length;
        if (index == Integer.MIN_VALUE) {
            index = 0;
        }
        if (index < 0) {
            index = -index;
        }
        // TODO rehash if index is OOB (fails on empty table)
        return index;
    }

}
