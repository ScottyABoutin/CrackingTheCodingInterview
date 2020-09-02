package chap01.arraysAndStrings;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.StringJoiner;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.function.Predicate;

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
     * linked list. Note that K and V in this inner class do not necessarily correspond to the outer
     * class's type parameters (although they will in practice).
     * 
     * @param <K> the type of key a in the map this node belongs to
     * @param <V> the type of mapped values for the map this node belongs to
     */
    private static class Node<K, V> implements Entry<K, V> {
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
         * backing map (by the iterator's remove operation), the results of this call are undefined.
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
         * object is also a map entry and the two entries represent the same mapping. More formally,
         * two entries e1 and e2 represent the same mapping if (e1.getKey()==null ?
         * e2.getKey()==null : e1.getKey().equals(e2.getKey())) && (e1.getValue()==null ?
         * e2.getValue()==null : e1.getValue().equals(e2.getValue()))
         * 
         * This ensures that the equals method works properly across different implementations of
         * the Map.Entry interface.
         * 
         * @param o object to be compared for equality with this map entry
         * @return true if the specified object is equal to this map entry
         */
        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Entry<?, ?>)) {
                return false;
            }
            Entry<?, ?> entry = (Entry<?, ?>) o;
            return key.equals(entry.getKey()) && value.equals(entry.getValue());
        }
        
        /**
         * Returns the hash code value for this map entry. The hash code of a map entry e is defined
         * to be (e.getKey()==null ? 0 : e.getKey().hashCode()) ^ (e.getValue()==null ? 0 :
         * e.getValue().hashCode()) This ensures that e1.equals(e2) implies that
         * e1.hashCode()==e2.hashCode() for any two Entries e1 and e2, as required by the general
         * contract of Object.hashCode.
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
         * string representation of this entry's value.
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
     * true if and only if this map contains a mapping for a key k such that Objects.equals(key, k).
     * (There can be at most one such mapping.)
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
     * Returns true if this map maps one or more keys to the specified value. More formally, returns
     * true if and only if this map contains at least one mapping to a value v such that
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
     *             mapping for the key
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
     * @param key   key with which the specified value is to be associated
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
        if (keySet == null) {
            keySet = new KeySet();
        }
        return keySet;
    }
    
    /**
     * The stateless KeySet delegates its calls to the outer HashTable instance.
     * 
     * @param <E> the type of keys maintained by this map
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
            return size;
        }
        
        /**
         * Returns true if this set contains no elements.
         * 
         * @return true if this set contains no elements
         */
        @Override
        public boolean isEmpty() {
            return size() == 0;
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
            if (this.isEmpty()) {
                return Collections.emptyIterator();
            } else {
                return new Iter<>(IterType.KEYS);
            }
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
            Object[] array = new Object[size];
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
         * @param <T> the component type of the array to contain the collection
         * @param a   the array into which the elements of this set are to be stored, if it is big
         *                enough; otherwise, a new array of the same runtime type is allocated for
         *                this purpose.
         * @return an array containing all the elements in this set
         * @throws ArrayStoreException  if the runtime type of the specified array is not a
         *                                  supertype of the runtime type of every element in this
         *                                  set
         * @throws NullPointerException if the specified array is null
         */
        @Override
        public <T> T[] toArray(T[] a) {
            Objects.requireNonNull(a);
            
            List<K> list = new ArrayList<>(size);
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
            
            T[] array = generator.apply(size());
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
            // TODO can make more efficient?
            return Spliterators.spliterator(this,
                    Spliterator.DISTINCT | Spliterator.NONNULL | Spliterator.SIZED | Spliterator.SUBSIZED);
        }
        
        // TODO Collection: stream, parallelStream, toArray(IntGenerator), removeIf. Iterable:
        // forEach
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
        if (values == null) {
            values = new Values();
        }
        return values;
    }
    
    /**
     *
     */
    private class Values implements Collection<V> {
        
        /**
         * Returns the number of elements in this collection. If this collection contains more than
         * Integer.MAX_VALUE elements, returns Integer.MAX_VALUE.
         * 
         * @return the number of elements in this collection
         */
        @Override
        public int size() {
            return size;
        }
        
        /**
         * Returns true if this collection contains no elements.
         * 
         * @return true if this collection contains no elements
         */
        @Override
        public boolean isEmpty() {
            return size() == 0;
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
            if (this.isEmpty()) {
                return Collections.emptyIterator();
            } else {
                return new Iter<>(IterType.VALUES);
            }
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
            Object[] array = new Object[size];
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
            
            List<V> list = new ArrayList<>(size);
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
            
            T[] array = generator.apply(size());
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
        
        // TODO Collection: spliterator, stream, parallelStream. Iterable: forEach
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
        if (entrySet == null) {
            entrySet = new EntrySet();
        }
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
            return size;
        }
        
        /**
         * Returns true if this set contains no elements.
         * 
         * @return true if this set contains no elements
         */
        @Override
        public boolean isEmpty() {
            return size() == 0;
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
            if (this.isEmpty()) {
                return Collections.emptyIterator();
            } else {
                return new Iter<>(IterType.ENTRIES);
            }
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
            Object[] array = new Object[size];
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
         * @param <T> the component type of the array to contain the collection
         * @param a   the array into which the elements of this set are to be stored, if it is big
         *                enough; otherwise, a new array of the same runtime type is allocated for
         *                this purpose.
         * @return an array containing all the elements in this set
         * @throws ArrayStoreException  if the runtime type of the specified array is not a
         *                                  supertype of the runtime type of every element in this
         *                                  set
         * @throws NullPointerException if the specified array is null
         */
        @Override
        public <T> T[] toArray(T[] a) {
            List<Entry<K, V>> list = new ArrayList<>(size);
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
            // TODO can make more efficient?
            return Spliterators.spliterator(this,
                    Spliterator.DISTINCT | Spliterator.NONNULL | Spliterator.SIZED | Spliterator.SUBSIZED);
        }
        
        // TODO Collection: spliterator, stream, parallelStream, toArray(IntGenerator), removeIf.
        // Iterable: forEach
        
    }
    
    private enum IterType {
        KEYS, VALUES, ENTRIES;
        
        @Override
        public String toString() {
            return this.name().toLowerCase(Locale.US);
        }
    }
    
    /**
     * 
     * 
     * @param <E> the type of elements returned by this iterator
     */
    private class Iter<E> implements Iterator<E> {
        
        private Node<K, V>[] table = MyHashTable.this.table;
        private int index = 0;
        private Node<K, V> currentNode = null;
        private Node<K, V> nextNode;
        private IterType type;
        
        public Iter(IterType type) {
            this.type = type;
            while (index < table.length && table[index] == null) {
                index++;
            }
            this.nextNode = table[index];
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
         * Returns the next element in the iteration.
         * 
         * @return the next element in the iteration
         * @throws NoSuchElementException if the iteration has no more elements
         */
        @SuppressWarnings("unchecked")
        @Override
        public E next() {
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
            switch (type) {
                case ENTRIES:
                    return (E) currentNode;
                case KEYS:
                    return (E) currentNode.key;
                case VALUES:
                    return (E) currentNode.value;
                default:
                    throw new IllegalStateException("Bad Iterator type");
            }
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
            // Length is 13 + 7 (entries) + 10 + 5 (false)
            StringBuilder builder = new StringBuilder(35);
            builder.append("Iterator for ").append(type).append(": hasNext=").append(this.hasNext());
            return builder.toString();
        }
        
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
