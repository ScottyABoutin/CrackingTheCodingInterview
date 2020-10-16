package chap01.arraysAndStrings;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.RandomAccess;
import java.util.Spliterator;
import java.util.StringJoiner;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

/**
 * Resizable-array implementation of the {@code List} interface. Implements all optional list
 * operations, and permits all elements, including {@code null}. It is not thread-safe.
 *
 * @param <E> the type of elements in this list
 */
public final class MyArrayList<E> implements List<E>, RandomAccess {
    private static final int DEFAULT_LENGTH = 10;
    
    private Object[] elements;
    private int size;
    
    private void ensureCapacityWithNewElements(int numNewElements) {
        if (size + numNewElements > elements.length) {
            if (elements.length > (1 << 30)) {
                elements = Arrays.copyOf(elements, Integer.MAX_VALUE);
            } else {
                elements = Arrays.copyOf(elements, elements.length * 2);
            }
        }
    }
    
    /**
     * Constructs an empty list with an initial capacity of ten.
     */
    public MyArrayList() {
        this(DEFAULT_LENGTH);
    }
    
    /**
     * Constructs an empty list with the specified initial capacity.
     * 
     * @param initialCapacity the initial capacity of the list
     * @throws IllegalArgumentException if the specified initial capacity is negative
     */
    public MyArrayList(int initialCapacity) {
        if (initialCapacity < 0) {
            throw new IllegalArgumentException("MyArrayList capacity cannot be negative: " + initialCapacity);
        }
        
        this.elements = new Object[initialCapacity];
    }
    
    /**
     * Constructs a list containing the elements of the specified collection, in the order they are
     * returned by the collection's iterator.
     * 
     * @param c the collection whose elements are to be placed into this list
     * @throws NullPointerException if the specified collection is null
     */
    public MyArrayList(Collection<? extends E> c) {
        this(c.size()); // throws NullPointerException, "this" must be first call
        this.addAll(c);
    }
    
    /**
     * Returns the number of elements in this list. If this list contains more than
     * {@code Integer.MAX_VALUE} elements, returns {@code Integer.MAX_VALUE}.
     * 
     * @return the number of elements in this list
     */
    @Override
    public int size() {
        return size;
    }
    
    /**
     * Returns {@code true} if this list contains no elements.
     * 
     * @return {@code true} if this list contains no elements
     */
    @Override
    public boolean isEmpty() {
        return this.size() == 0;
    }
    
    /**
     * Returns {@code true} if this list contains the specified element. More formally, returns
     * {@code true} if and only if this list contains at least one element e such that
     * {@code Objects.equals(o, e)}.
     * 
     * @param o element whose presence in this list is to be tested
     * @return {@code true} if this list contains the specified element
     */
    @Override
    public boolean contains(Object o) {
        return this.indexOf(o) >= 0;
        
    }
    
    /**
     * Returns an iterator over the elements in this list in proper sequence.
     * 
     * @return an iterator over the elements in this list in proper sequence
     */
    @Override
    public Iterator<E> iterator() {
        return new ArrayIterator();
    }
    
    /**
     * Performs the given action for each element of the Iterable until all elements have been
     * processed or the action throws an exception. Actions are performed in the order of iteration,
     * if that order is specified. Exceptions thrown by the action are relayed to the caller. The
     * behavior of this method is unspecified if the action performs side-effects that modify the
     * underlying source of elements, unless an overriding class has specified a concurrent
     * modification policy.
     * 
     * Implementation Requirements: The default implementation behaves as if:
     * 
     * 
     * for (T t : this) action.accept(t);
     * 
     * @param action The action to be performed for each element
     * @throws NullPointerException if the specified action is {@code null}
     */
    @Override
    public void forEach(Consumer<? super E> action) {
        for (E element : this) {
            action.accept(element);
        }
    }
    
    /**
     * Returns an array containing all of the elements in this list in proper sequence (from first
     * to last element). The returned array will be "safe" in that no references to it are
     * maintained by this list. (In other words, this method must allocate a new array even if this
     * list is backed by an array). The caller is thus free to modify the returned array.
     * 
     * This method acts as bridge between array-based and collection-based APIs.
     * 
     * @return an array containing all of the elements in this list in proper sequence
     */
    @Override
    public Object[] toArray() {
        return Arrays.copyOf(elements, this.size());
    }
    
    /**
     * Returns an array containing all of the elements in this list in proper sequence (from first
     * to last element); the runtime type of the returned array is that of the specified array. If
     * the list fits in the specified array, it is returned therein. Otherwise, a new array is
     * allocated with the runtime type of the specified array and the size of this list. If the list
     * fits in the specified array with room to spare (i.e., the array has more elements than the
     * list), the element in the array immediately following the end of the list is set to null.
     * (This is useful in determining the length of the list only if the caller knows that the list
     * does not contain any null elements.)
     * 
     * Like the toArray() method, this method acts as bridge between array-based and
     * collection-based APIs. Further, this method allows precise control over the runtime type of
     * the output array, and may, under certain circumstances, be used to save allocation costs.
     * 
     * Suppose x is a list known to contain only strings. The following code can be used to dump the
     * list into a newly allocated array of String:
     * 
     * 
     * String[] y = x.toArray(new String[0]);
     * 
     * Note that toArray(new Object[0]) is identical in function to toArray().
     * 
     * @param T the component type of the array to contain the collection
     * @param a the array into which the elements of this list are to be stored, if it is big
     *              enough; otherwise, a new array of the same runtime type is allocated for this
     *              purpose.
     * @return an array containing the elements of this list
     * @throws ArrayStoreException  if the runtime type of the specified array is not a supertype of
     *                                  the runtime type of every element in this list
     * @throws NullPointerException if the specified array is null
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> T[] toArray(T[] a) {
        Objects.requireNonNull(a);
        
        if (a.length < this.size()) {
            return (T[]) Arrays.copyOf(elements, this.size(), a.getClass());
        } else {
            System.arraycopy(elements, 0, a, 0, this.size());
            if (a.length > this.size()) {
                a[this.size()] = null;
            }
            return a;
        }
    }
    
    /**
     * Returns an array containing all of the elements in this collection, using the provided
     * generator function to allocate the returned array. If this collection makes any guarantees as
     * to what order its elements are returned by its iterator, this method must return the elements
     * in the same order.
     * 
     * API Note: This method acts as a bridge between array-based and collection-based APIs. It
     * allows creation of an array of a particular runtime type. Use toArray() to create an array
     * whose runtime type is Object[], or use toArray(T[]) to reuse an existing array. Suppose x is
     * a collection known to contain only strings. The following code can be used to dump the
     * collection into a newly allocated array of String:
     * 
     * String[] y = x.toArray(String[]::new); Implementation Requirements: The default
     * implementation calls the generator function with zero and then passes the resulting array to
     * toArray(T[]).
     * 
     * @param T         the component type of the array to contain the collection
     * @param generator a function which produces a new array of the desired type and the provided
     *                      length
     * @return an array containing all of the elements in this collection
     * @throws ArrayStoreException  if the runtime type of any element in this collection is not
     *                                  assignable to the runtime component type of the generated
     *                                  array
     * @throws NullPointerException if the generator function is null
     */
    @Override
    public <T> T[] toArray(IntFunction<T[]> generator) {
        // TODO this fails if the generator function returns null, but not for a documented reason.
        Objects.requireNonNull(generator);
        
        T[] generatedArray = generator.apply(this.size());
        return this.toArray(generatedArray);
    }
    
    /**
     * Appends the specified element to the end of this list.
     * 
     * @param e element to be appended to this list
     * @return true (as specified by Collection.add(E))
     */
    @Override
    public boolean add(E e) {
        this.add(this.size(), e);
        return true;
    }
    
    /**
     * Removes the first occurrence of the specified element from this list, if it is present. If
     * this list does not contain the element, it is unchanged. More formally, removes the element
     * with the lowest index i such that Objects.equals(o, get(i)) (if such an element exists).
     * Returns true if this list contained the specified element (or equivalently, if this list
     * changed as a result of the call).
     * 
     * @param o element to be removed from this list, if present
     * @return {@code true} if this list contained the specified element
     */
    @Override
    public boolean remove(Object o) {
        for (Iterator<E> iterator = this.iterator(); iterator.hasNext();) {
            E element = iterator.next();
            if (Objects.equals(o, element)) {
                iterator.remove();
                return true;
            }
        }
        return false;
    }
    
    /**
     * Returns true if this list contains all of the elements of the specified collection.
     * 
     * @param c collection to be checked for containment in this list
     * @return true if this list contains all of the elements of the specified collection
     * @throws NullPointerException if the specified collection is null
     */
    @Override
    public boolean containsAll(Collection<?> c) {
        Objects.requireNonNull(c);
        
        for (Object element : c) {
            if (!this.contains(element)) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Appends all of the elements in the specified collection to the end of this list, in the order
     * that they are returned by the specified collection's iterator. The behavior of this operation
     * is undefined if the specified collection is modified while the operation is in progress.
     * (Note that this will occur if the specified collection is this list, and it's nonempty.)
     * 
     * @param c collection containing elements to be added to this list
     * @return true if this list changed as a result of the call
     * @throws NullPointerException if the specified collection is null
     */
    @Override
    public boolean addAll(Collection<? extends E> c) {
        Objects.requireNonNull(c);
        
        return this.addAll(this.size(), c);
    }
    
    /**
     * Inserts all of the elements in the specified collection into this list at the specified
     * position. Shifts the element currently at that position (if any) and any subsequent elements
     * to the right (increases their indices). The new elements will appear in this list in the
     * order that they are returned by the specified collection's iterator. The behavior of this
     * operation is undefined if the specified collection is modified while the operation is in
     * progress. (Note that this will occur if the specified collection is this list, and it's
     * nonempty.)
     * 
     * @param index index at which to insert the first element from the specified collection
     * @param c     collection containing elements to be added to this list
     * @return true if this list changed as a result of the call
     * @throws NullPointerException      if the specified collection is null
     * @throws IndexOutOfBoundsException if the index is out of range (index < 0 || index > size())
     */
    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        Objects.requireNonNull(c);
        Objects.checkIndex(index, this.size());
        
        if (c.isEmpty()) {
            return false;
        }
        
        this.ensureCapacityWithNewElements(c.size());
        Object[] array = c.toArray();
        for (int i = array.length - 1; i >= 0; i--) {
            elements[index + i] = elements[i];
            elements[i] = array[i];
        }
        size += c.size();
        
        return true;
    }
    
    /**
     * Removes from this list all of its elements that are contained in the specified collection.
     * 
     * @param c collection containing elements to be removed from this list
     * @return true if this list changed as a result of the call
     * @throws NullPointerException if the specified collection is null
     */
    @Override
    public boolean removeAll(Collection<?> c) {
        Objects.requireNonNull(c);
        
        return this.removeIf(c::contains);
    }
    
    /**
     * Removes all of the elements of this collection that satisfy the given predicate. Errors or
     * runtime exceptions thrown during iteration or by the predicate are relayed to the caller.
     * Implementation Requirements: The default implementation traverses all elements of the
     * collection using its iterator(). Each matching element is removed using Iterator.remove(). If
     * the collection's iterator does not support removal then an UnsupportedOperationException will
     * be thrown on the first matching element.
     * 
     * @param filter a predicate which returns true for elements to be removed
     * @return true if any elements were removed
     * @throws NullPointerException if the specified filter is null
     */
    @Override
    public boolean removeIf(Predicate<? super E> filter) {
        Objects.requireNonNull(filter);
        
        boolean changed = false;
        // Iterating backwards to lessen the amount of array shifting as possible
        for (ListIterator<E> iterator = this.listIterator(this.size()); iterator.hasPrevious();) {
            E element = iterator.previous();
            if (filter.test(element)) {
                iterator.remove();
                changed = true;
            }
        }
        
        return changed;
    }
    
    /**
     * Retains only the elements in this list that are contained in the specified collection
     * (optional operation). In other words, removes from this list all of its elements that are not
     * contained in the specified collection.
     * 
     * @param c collection containing elements to be retained in this list
     * @return true if this list changed as a result of the call
     * @throws NullPointerException if the specified collection is null
     */
    @Override
    public boolean retainAll(Collection<?> c) {
        Objects.requireNonNull(c);
        Predicate<E> contains = c::contains;
        return this.removeIf(contains.negate());
    }
    
    /**
     * Replaces each element of this list with the result of applying the operator to that element.
     * Errors or runtime exceptions thrown by the operator are relayed to the caller. Implementation
     * Requirements: The default implementation is equivalent to, for this list:
     * 
     * final ListIterator<E> li = list.listIterator(); while (li.hasNext()) {
     * li.set(operator.apply(li.next())); }
     * 
     * If the list's list-iterator does not support the set operation then an
     * UnsupportedOperationException will be thrown when replacing the first element.
     * 
     * @param operator the operator to apply to each element
     * @throws NullPointerException if the specified operator is null
     */
    @Override
    public void replaceAll(UnaryOperator<E> operator) {
        Objects.requireNonNull(operator);
        
        ListIterator<E> listIterator = this.listIterator();
        while (listIterator.hasNext()) {
            E replacableItem = listIterator.next();
            E newItem = operator.apply(replacableItem);
            listIterator.set(newItem);
        }
    }
    
    /**
     * Sorts this list according to the order induced by the specified Comparator. The sort is
     * stable: this method must not reorder equal elements. All elements in this list must be
     * mutually comparable using the specified comparator (that is, c.compare(e1, e2) must not throw
     * a ClassCastException for any elements e1 and e2 in the list).
     * 
     * If the specified comparator is null then all elements in this list must implement the
     * Comparable interface and the elements' natural ordering should be used.
     * 
     * This list must be modifiable, but need not be resizable.
     * 
     * Implementation Requirements: The default implementation obtains an array containing all
     * elements in this list, sorts the array, and iterates over this list resetting each element
     * from the corresponding position in the array. (This avoids the n2 log(n) performance that
     * would result from attempting to sort a linked list in place.) Implementation Note: This
     * implementation is a stable, adaptive, iterative mergesort that requires far fewer than n
     * lg(n) comparisons when the input array is partially sorted, while offering the performance of
     * a traditional mergesort when the input array is randomly ordered. If the input array is
     * nearly sorted, the implementation requires approximately n comparisons. Temporary storage
     * requirements vary from a small constant for nearly sorted input arrays to n/2 object
     * references for randomly ordered input arrays. The implementation takes equal advantage of
     * ascending and descending order in its input array, and can take advantage of ascending and
     * descending order in different parts of the same input array. It is well-suited to merging two
     * or more sorted arrays: simply concatenate the arrays and sort the resulting array.
     * 
     * The implementation was adapted from Tim Peters's list sort for Python ( TimSort). It uses
     * techniques from Peter McIlroy's "Optimistic Sorting and Information Theoretic Complexity", in
     * Proceedings of the Fourth Annual ACM-SIAM Symposium on Discrete Algorithms, pp 467-474,
     * January 1993.
     * 
     * @param c the Comparator used to compare list elements. A null value indicates that the
     *              elements' natural ordering should be used
     * @throws IllegalArgumentException - (optional) if the comparator is found to violate the
     *                                      Comparator contract
     */
    @Override
    public void sort(Comparator<? super E> c) {
        Collections.sort(this, c); // TODO implement sorting?
    }
    
    /**
     * Removes all of the elements from this list. The list will be empty after this call returns.
     */
    @Override
    public void clear() {
        elements = new Object[DEFAULT_LENGTH];
        size = 0;
    }
    
    /**
     * Compares the specified object with this list for equality. Returns true if and only if the
     * specified object is also a list, both lists have the same size, and all corresponding pairs
     * of elements in the two lists are equal. (Two elements e1 and e2 are equal if
     * Objects.equals(e1, e2).) In other words, two lists are defined to be equal if they contain
     * the same elements in the same order. This definition ensures that the equals method works
     * properly across different implementations of the List interface.
     * 
     * @param o the object to be compared for equality with this list
     * @return true if the specified object is equal to this list
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof List<?>)) {
            return false;
        }
        List<?> list = (List<?>) o;
        if (this.size() != list.size()) {
            return false;
        }
        Iterator<E> thisIterator = this.iterator();
        Iterator<?> thatIterator = list.iterator();
        while (thisIterator.hasNext()) {
            E item1 = thisIterator.next();
            Object item2 = thatIterator.next();
            if (!Objects.equals(item1, item2)) {
                return false;
            }
        }
        return true;
        
    }
    
    /**
     * Returns the hash code value for this list. The hash code of a list is defined to be the
     * result of the following calculation:
     * 
     * int hashCode = 1; for (E e : list) hashCode = 31*hashCode + (e==null ? 0 : e.hashCode());
     * 
     * This ensures that list1.equals(list2) implies that list1.hashCode()==list2.hashCode() for any
     * two lists, list1 and list2, as required by the general contract of Object.hashCode().
     * 
     * @return the hash code value for this list
     */
    @Override
    public int hashCode() {
        int hashCode = 1;
        for (E item : this) {
            hashCode = 31 * hashCode + (item == null ? 0 : item.hashCode());
        }
        return hashCode;
    }
    
    /**
     * Returns a string representation of this collection. The string representation consists of a
     * list of the collection's elements in the order they are returned by its iterator, enclosed in
     * square brackets ("[]"). Adjacent elements are separated by the characters ", " (comma and
     * space). Elements are converted to strings as by String.valueOf(Object).
     * 
     * @return a string representation of this collection
     */
    @Override
    public String toString() {
        StringJoiner joiner = new StringJoiner(", ", "[", "]");
        for (E item : this) {
            joiner.add(String.valueOf(item));
        }
        return joiner.toString();
    }
    
    /**
     * Returns the element at the specified position in this list.
     * 
     * @param index index of the element to return
     * @return the element at the specified position in this list
     * @throws IndexOutOfBoundsException if the index is out of range (index < 0 || index >= size())
     */
    @SuppressWarnings("unchecked")
    @Override
    public E get(int index) {
        Objects.checkIndex(index, this.size());
        return (E) elements[index];
    }
    
    /**
     * Replaces the element at the specified position in this list with the specified element.
     * 
     * @param index   index of the element to replace
     * @param element element to be stored at the specified position
     * @return the element previously at the specified position
     * @throws IndexOutOfBoundsException if the index is out of range (index < 0 || index >= size())
     */
    @Override
    public E set(int index, E element) {
        Objects.checkIndex(index, this.size());
        
        @SuppressWarnings("unchecked")
        E original = (E) elements[index];
        elements[index] = element;
        return original;
    }
    
    /**
     * Inserts the specified element at the specified position in this list (optional operation).
     * Shifts the element currently at that position (if any) and any subsequent elements to the
     * right (adds one to their indices).
     * 
     * @param index   index at which the specified element is to be inserted
     * @param element element to be inserted
     * @throws IndexOutOfBoundsException if the index is out of range (index < 0 || index > size())
     */
    @Override
    public void add(int index, E element) {
        Objects.checkIndex(index, this.size() + 1);
        
        this.ensureCapacityWithNewElements(size + 1);
        elements[index] = element;
        size++;
    }
    
    /**
     * Removes the element at the specified position in this list (optional operation). Shifts any
     * subsequent elements to the left (subtracts one from their indices). Returns the element that
     * was removed from the list.
     * 
     * @param index the index of the element to be removed
     * @return the element previously at the specified position
     * @throws IndexOutOfBoundsException if the index is out of range (index < 0 || index >= size())
     */
    @Override
    public E remove(int index) {
        Objects.checkIndex(index, this.size());
        
        @SuppressWarnings("unchecked")
        E removed = (E) elements[index];
        System.arraycopy(this, index + 1, this, index, size - index);
        size--;
        return removed;
    }
    
    /**
     * Returns the index of the first occurrence of the specified element in this list, or -1 if
     * this list does not contain the element. More formally, returns the lowest index i such that
     * Objects.equals(o, get(i)), or -1 if there is no such index.
     * 
     * @param o element to search for
     * @return the index of the first occurrence of the specified element in this list, or -1 if
     *             this list does not contain the element
     */
    @Override
    public int indexOf(Object o) {
        for (int i = 0; i < this.size(); i++) {
            Object item = elements[i];
            if (Objects.equals(o, item)) {
                return i;
            }
        }
        return -1;
    }
    
    /**
     * Returns the index of the last occurrence of the specified element in this list, or -1 if this
     * list does not contain the element. More formally, returns the highest index i such that
     * Objects.equals(o, get(i)), or -1 if there is no such index.
     * 
     * @param o element to search for
     * @return the index of the last occurrence of the specified element in this list, or -1 if this
     *             list does not contain the element
     */
    @Override
    public int lastIndexOf(Object o) {
        for (int i = size - 1; i >= 0; i--) {
            Object item = elements[i];
            if (Objects.equals(o, item)) {
                return i;
            }
        }
        return -1;
    }
    
    /**
     * Returns a list iterator over the elements in this list (in proper sequence).
     * 
     * @return a list iterator over the elements in this list (in proper sequence)
     */
    @Override
    public ListIterator<E> listIterator() {
        return this.listIterator(0);
    }
    
    /**
     * Returns a list iterator over the elements in this list (in proper sequence), starting at the
     * specified position in the list. The specified index indicates the first element that would be
     * returned by an initial call to next. An initial call to previous would return the element
     * with the specified index minus one.
     * 
     * @param index index of the first element to be returned from the list iterator (by a call to
     *                  next)
     * @return a list iterator over the elements in this list (in proper sequence), starting at the
     *             specified position in the list
     * @throws IndexOutOfBoundsException if the index is out of range (index < 0 || index > size())
     */
    @Override
    public ListIterator<E> listIterator(int index) {
        Objects.checkIndex(index, this.size());

        return new ArrayListIterator(index);
    }
    
    enum IteratorState {
        INITIALIZED, REMOVED, MOVED
    }
    
    /**
    *
    */
   private class ArrayIterator implements Iterator<E> {
       Object[] elements = MyArrayList.this.elements;
       int currentIndex;
       int limit;
       boolean removed = true;
       
       ArrayIterator() {
           this(0);
       }
       
       ArrayIterator(int currentIndex) {
           this(currentIndex, MyArrayList.this.size());
       }
       
       ArrayIterator(int currentIndex, int limit) {
           Objects.checkFromToIndex(currentIndex, limit, MyArrayList.this.size());
           
           this.currentIndex = currentIndex;
           this.limit = limit;
       }
       
       /**
        * Returns true if the iteration has more elements. (In other words, returns true if next()
        * would return an element rather than throwing an exception.)
        * 
        * @return true if the iteration has more elements
        */
       @Override
       public boolean hasNext() {
           return currentIndex < limit;
       }
       
       /**
        * Returns the next element in the iteration.
        * 
        * @return the next element in the iteration
        * @throws NoSuchElementException if the iteration has no more elements
        */
       @Override
       public E next() {
           if (!this.hasNext()) {
               throw new NoSuchElementException();
           }
           
           removed = false;
           @SuppressWarnings("unchecked")
           E item = (E) elements[currentIndex];
           currentIndex++;
           return item;
       }
       
       /**
        * Removes from the underlying collection the last element returned by this iterator. This
        * method can be called only once per call to next(). The behavior of an iterator is
        * unspecified if the underlying collection is modified while the iteration is in progress
        * in any way other than by calling this method, unless an overriding class has specified a
        * concurrent modification policy.
        * 
        * The behavior of an iterator is unspecified if this method is called after a call to the
        * forEachRemaining method.
        * 
        * @throws IllegalStateException - if the next method has not yet been called, or the remove
        *                                   method has already been called after the last call to
        *                                   the next method
        */
       @Override
       public void remove() {
           if (removed) {
               throw new IllegalStateException();
           }
           
           MyArrayList.this.remove(currentIndex);
           removed = true;
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
        * Implementation Requirements: The default implementation behaves as if:
        * 
        * 
        * while (hasNext()) action.accept(next());
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
        *
        * Returns a string representation of this iterator that represents what it iterates over.
        * It also textually represents whether the iterator has more elements, although code should
        * use the hasNext() method rather than parsing this String. This format is subject to
        * change in future versions.
        * 
        * @return a string representation of this iterator
        */
       @Override
       public String toString() {
           return String.format("currentIndex: %d, limit: %d", currentIndex, limit);
       }
       
   }
   
   /**
    *
    */
   private class ArrayListIterator extends ArrayIterator implements ListIterator<E> {
       
       ArrayListIterator() {
           this(0);
       }
       
       ArrayListIterator(int currentIndex) {
           this(currentIndex, MyArrayList.this.size());
       }
       
       ArrayListIterator(int currentIndex, int limit) {
           super(currentIndex, limit);
       }
       
       @Override
       public boolean hasPrevious() {
           // TODO Auto-generated method stub
           return false;
       }
       
       @Override
       public E previous() {
           // TODO Auto-generated method stub
           return null;
       }
       
       @Override
       public int nextIndex() {
           // TODO Auto-generated method stub
           return 0;
       }
       
       @Override
       public int previousIndex() {
           // TODO Auto-generated method stub
           return 0;
       }
       
       @Override
       public void set(E e) {
           // TODO Auto-generated method stub
           
       }
       
       @Override
       public void add(E e) {
           // TODO Auto-generated method stub
           
       }
       
   }
    
    /**
     * Returns a view of the portion of this list between the specified fromIndex, inclusive, and
     * toIndex, exclusive. (If fromIndex and toIndex are equal, the returned list is empty.) The
     * returned list is backed by this list, so non-structural changes in the returned list are
     * reflected in this list, and vice-versa. The returned list supports all of the optional list
     * operations supported by this list. This method eliminates the need for explicit range
     * operations (of the sort that commonly exist for arrays). Any operation that expects a list
     * can be used as a range operation by passing a subList view instead of a whole list. For
     * example, the following idiom removes a range of elements from a list:
     * 
     * 
     * list.subList(from, to).clear();
     * 
     * Similar idioms may be constructed for indexOf and lastIndexOf, and all of the algorithms in
     * the Collections class can be applied to a subList. The semantics of the list returned by this
     * method become undefined if the backing list (i.e., this list) is structurally modified in any
     * way other than via the returned list. (Structural modifications are those that change the
     * size of this list, or otherwise perturb it in such a fashion that iterations in progress may
     * yield incorrect results.)
     * 
     * @param fromIndex low endpoint (inclusive) of the subList
     * @param toIndex   high endpoint (exclusive) of the subList
     * @return a view of the specified range within this list
     * @throws IndexOutOfBoundsException for an illegal endpoint index value (fromIndex < 0 ||
     *                                       toIndex > size || fromIndex > toIndex)
     */
    @Override
    public List<E> subList(int fromIndex, int toIndex) {
        Objects.checkFromToIndex(fromIndex, toIndex, this.size());
        
        return new ArraySubList(fromIndex, toIndex);
    }
    
    private class ArraySubList implements List<E> {
        
        private int fromIndex;
        private int toIndex;

        ArraySubList(int fromIndex, int toIndex) {
            this.fromIndex = fromIndex;
            this.toIndex = toIndex;
        }
        
        @Override
        public <T> T[] toArray(IntFunction<T[]> generator) {
            // TODO Auto-generated method stub
            return List.super.toArray(generator);
        }

        @Override
        public boolean removeIf(Predicate<? super E> filter) {
            // TODO Auto-generated method stub
            return List.super.removeIf(filter);
        }

        @Override
        public Stream<E> stream() {
            // TODO Auto-generated method stub
            return List.super.stream();
        }

        @Override
        public Stream<E> parallelStream() {
            // TODO Auto-generated method stub
            return List.super.parallelStream();
        }

        @Override
        public void forEach(Consumer<? super E> action) {
            // TODO Auto-generated method stub
            List.super.forEach(action);
        }

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
        public boolean addAll(int index, Collection<? extends E> c) {
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
        public void replaceAll(UnaryOperator<E> operator) {
            // TODO Auto-generated method stub
            List.super.replaceAll(operator);
        }

        @Override
        public void sort(Comparator<? super E> c) {
            // TODO Auto-generated method stub
            List.super.sort(c);
        }

        @Override
        public void clear() {
            // TODO Auto-generated method stub
            
        }

        @Override
        public E get(int index) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public E set(int index, E element) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public void add(int index, E element) {
            // TODO Auto-generated method stub
            
        }

        @Override
        public E remove(int index) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public int indexOf(Object o) {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public int lastIndexOf(Object o) {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public ListIterator<E> listIterator() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public ListIterator<E> listIterator(int index) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public List<E> subList(int fromIndex, int toIndex) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public Spliterator<E> spliterator() {
            // TODO Auto-generated method stub
            return List.super.spliterator();
        }

        @Override
        public int hashCode() {
            // TODO Auto-generated method stub
            return super.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            // TODO Auto-generated method stub
            return super.equals(obj);
        }

        @Override
        public String toString() {
            // TODO Auto-generated method stub
            return super.toString();
        }
        
    }
    
    /**
     * Creates a Spliterator over the elements in this list. The Spliterator reports
     * Spliterator.SIZED and Spliterator.ORDERED. Implementations should document the reporting of
     * additional characteristic values.
     * 
     * Implementation Requirements: The default implementation creates a late-binding spliterator as
     * follows: If the list is an instance of RandomAccess then the default implementation creates a
     * spliterator that traverses elements by invoking the method get(int). If such invocation
     * results or would result in an IndexOutOfBoundsException then the spliterator will fail-fast
     * and throw a ConcurrentModificationException. If the list is also an instance of AbstractList
     * then the spliterator will use the list's modCount field to provide additional fail-fast
     * behavior. Otherwise, the default implementation creates a spliterator from the list's
     * Iterator. The spliterator inherits the fail-fast of the list's iterator. Implementation Note:
     * The created Spliterator additionally reports Spliterator.SUBSIZED.
     * 
     * @return a Spliterator over the elements in this list
     */
    @Override
    public Spliterator<E> spliterator() {
        // TODO Auto-generated method stub
        return List.super.spliterator();
    }
    
    /**
     * Returns a sequential Stream with this collection as its source. This method should be
     * overridden when the spliterator() method cannot return a spliterator that is IMMUTABLE,
     * CONCURRENT, or late-binding. (See spliterator() for details.)
     * 
     * Implementation Requirements: The default implementation creates a sequential Stream from the
     * collection's Spliterator.
     * 
     * @return a sequential Stream over the elements in this collection
     */
    @Override
    public Stream<E> stream() {
        // TODO Auto-generated method stub
        return List.super.stream();
    }
    
    /**
     * Returns a possibly parallel Stream with this collection as its source. It is allowable for
     * this method to return a sequential stream. This method should be overridden when the
     * spliterator() method cannot return a spliterator that is IMMUTABLE, CONCURRENT, or
     * late-binding. (See spliterator() for details.)
     * 
     * Implementation Requirements: The default implementation creates a parallel Stream from the
     * collection's Spliterator.
     * 
     * @return a possibly parallel Stream over the elements in this collection
     */
    @Override
    public Stream<E> parallelStream() {
        // TODO Auto-generated method stub
        return List.super.parallelStream();
    }
    
}

// TODO ArrayList methods? Vector methods?
// TODO ConcurrentModificationException, serialization, cloneable (legacy)
