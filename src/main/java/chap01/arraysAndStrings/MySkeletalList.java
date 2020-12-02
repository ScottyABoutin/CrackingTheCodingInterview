package chap01.arraysAndStrings;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.RandomAccess;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

/**
 * This class provides a skeletal implementation of the {@link java.util.List} interface to minimize
 * the effort required to implement this interface backed by a "random access" data store (such as
 * an array). For sequential access data (such as a linked list), {@link MySkeletalSequentialList}
 * should be used in preference to this class.
 * <p>
 * To implement an unmodifiable list, the programmer needs only to extend this class and provide
 * implementations for the {@link #get(int)} and {@link #size()} methods.
 * <p>
 * To implement a modifiable list, the programmer must additionally override the
 * {@link #set(int, Object)} method (which otherwise throws an
 * {@code UnsupportedOperationException}). If the list is variable-size the programmer must
 * additionally override the {@link #add(int, Object)} and {@link #remove(int)} methods.
 * <p>
 * The programmer should generally provide a void (no argument) and collection constructor, as per
 * the recommendation in the {@link java.util.Collection} interface specification.
 * <p>
 * Unlike the other abstract collection implementations, the programmer does <em>not</em> have to
 * provide an iterator implementation; the iterator and list iterator are implemented by this class,
 * on top of the "random access" methods: {@link #get(int)}, {@link #set(int, Object)},
 * {@link #add(int, Object)} and {@link #remove(int)}.
 * <p>
 * The documentation for each non-abstract method in this class describes its implementation in
 * detail. Each of these methods may be overridden if the collection being implemented admits a more
 * efficient implementation.
 *
 * @param E the type of elements in this list
 */
public abstract class MySkeletalList<E> extends MySkeletalCollection<E> implements List<E> {
    
    /**
     * The number of times this list has been <em>structurally modified</em>. Structural
     * modifications are those that change the size of the list, or otherwise perturb it in such a
     * fashion that iterations in progress may yield incorrect results.
     * <p>
     * This field is used by the iterator and list iterator implementation returned by the
     * {@code iterator} and {@code listIterator} methods. If the value of this field changes
     * unexpectedly, the iterator (or list iterator) will throw a
     * {@code ConcurrentModificationException} in response to the {@code next}, {@code remove},
     * {@code previous}, {@code set}, or {@code add} operations. This provides <em>fail-fast</em>
     * behavior, rather than non-deterministic behavior in the face of concurrent modification
     * during iteration.
     * <p>
     * <strong>Use of this field by subclasses is optional.</strong> If a subclass wishes to provide
     * fail-fast iterators (and list iterators), then it merely has to increment this field in its
     * {@code add(int, E)} and {@code remove(int)} methods (and any other methods that it overrides
     * that result in structural modifications to the list). A single call to {@code add(int, E)} or
     * {@code remove(int)} must add no more than one to this field, or the iterators (and list
     * iterators) will throw bogus {@code ConcurrentModificationException}s. If an implementation
     * does not wish to provide fail-fast iterators, this field may be ignored.
     */
    protected transient int modCount = 0;
    
    /**
     * Sole constructor. (For invocation by subclass constructors, typically implicit.)
     */
    protected MySkeletalList() {
    }
    
    /**
     * Appends the specified element to the end of this list (optional operation).
     * <p>
     * Lists that support this operation may place limitations on what elements may be added to this
     * list. In particular, some lists will refuse to add null elements, and others will impose
     * restrictions on the type of elements that may be added. List classes should clearly specify
     * in their documentation any restrictions on what elements may be added.
     * 
     * @implSpec This implementation calls {@code add(size(), e)}.
     * <p>
     * Note that this implementation throws an {@code UnsupportedOperationException} unless
     * {@link #add(int, Object)} is overridden.
     * @param e element to be appended to this list
     * @return {@code true} (as specified by {@link java.util.Collection#add(Object)})
     * @throws UnsupportedOperationException if the {@code add} operation is not supported by this
     *     list
     * @throws ClassCastException if the class of the specified element prevents it from being added
     *     to this list
     * @throws NullPointerException if the specified element is null and this list does not permit
     *     null elements
     * @throws IllegalArgumentException if some property of this element prevents it from being
     *     added to this list
     */
    @Override
    public boolean add(E e) {
        this.add(this.size(), e);
        return true;
    }
    
    /**
     * Returns the element at the specified position in this list.
     * 
     * @param index index of the element to return
     * @return the element at the specified position in this list
     * @throws IndexOutOfBoundsException if the index is out of range
     *     {@code (index < 0 || index >= size())}
     */
    @Override
    public abstract E get(int index);
    
    /**
     * Replaces the element at the specified position in this list with the specified element
     * (optional operation).
     * 
     * @implSpec This implementation always throws an {@code UnsupportedOperationException}.
     * @param index index of the element to replace
     * @param element element to be stored at the specified position
     * @return the element previously at the specified position
     * @throws UnsupportedOperationException if the {@code set} operation is not supported by this
     *     list
     * @throws ClassCastException if the class of the specified element prevents it from being added
     *     to this list
     * @throws NullPointerException if the specified element is null and this list does not permit
     *     null elements
     * @throws IllegalArgumentException if some property of the specified element prevents it from
     *     being added to this list
     * @throws IndexOutOfBoundsException if the index is out of range
     *     {@code (index < 0 || index >= size())}
     */
    @Override
    public E set(int index, E element) {
        throw new UnsupportedOperationException();
    }
    
    /**
     * Inserts the specified element at the specified position in this list (optional operation).
     * Shifts the element currently at that position (if any) and any subsequent elements to the
     * right (adds one to their indices).
     * 
     * @implSpec This implementation always throws an {@code UnsupportedOperationException}.
     * @param index index at which the specified element is to be inserted
     * @param element element to be inserted
     * @throws UnsupportedOperationException if the add operation is not supported by this list
     * @throws ClassCastException if the class of the specified element prevents it from being added
     *     to this list
     * @throws NullPointerException if the specified element is null and this list does not permit
     *     null elements
     * @throws IllegalArgumentException if some property of the specified element prevents it from
     *     being added to this list
     * @throws IndexOutOfBoundsException if the index is out of range {@code (index < 0 ||
     * index > size()) }
     */
    @Override
    public void add(int index, E element) {
        throw new UnsupportedOperationException();
    }
    
    /**
     * Removes the element at the specified position in this list (optional operation). Shifts any
     * subsequent elements to the left (subtracts one from their indices). Returns the element that
     * was removed from the list.
     * 
     * @implSpec This implementation always throws an {@code UnsupportedOperationException}.
     * @param index the index of the element to be removed
     * @return the element previously at the specified position
     * @throws UnsupportedOperationException if the {@code remove} operation is not supported by
     *     this list
     * @throws IndexOutOfBoundsException if the index is out of range
     *     {@code (index < 0 || index >= size())}
     */
    @Override
    public E remove(int index) {
        throw new UnsupportedOperationException();
    }
    
    /**
     * Returns the index of the first occurrence of the specified element in this list, or -1 if
     * this list does not contain the element. More formally, returns the lowest index {@code i}
     * such that {@code Objects.equals(o, get(i))}, or -1 if there is no such index.
     * 
     * @implSpec This implementation first gets a list iterator (with {@code listIterator()}). Then,
     * it iterates over the list until the specified element is found or the end of the list is
     * reached.
     * @param o element to search for
     * @return the index of the first occurrence of the specified element in this list, or -1 if
     * this list does not contain the element
     * @throws ClassCastException if the type of the specified element is incompatible with this
     *     list (optional)
     * @throws NullPointerException if the specified element is null and this list does not permit
     *     null elements (optional)
     */
    @Override
    public int indexOf(Object o) {
        ListIterator<E> iterator = this.listIterator();
        while (iterator.hasNext()) {
            if (Objects.equals(o, iterator.next())) {
                return iterator.previousIndex();
            }
        }
        return -1;
    }
    
    /**
     * Returns the index of the last occurrence of the specified element in this list, or -1 if this
     * list does not contain the element. More formally, returns the highest index {@code i} such
     * that {@code Objects.equals(o, get(i))}, or -1 if there is no such index.
     * 
     * @implSpec This implementation first gets a list iterator that points to the end of the list
     * (with {@code listIterator(size())}). Then, it iterates backwards over the list until the
     * specified element is found, or the beginning of the list is reached.
     * @param o element to search for
     * @return the index of the last occurrence of the specified element in this list, or -1 if this
     * list does not contain the element
     * @throws ClassCastException if the type of the specified element is incompatible with this
     *     list (optional)
     * @throws NullPointerException if the specified element is null and this list does not permit
     *     null elements (optional)
     */
    @Override
    public int lastIndexOf(Object o) {
        ListIterator<E> iterator = this.listIterator(this.size());
        while (iterator.hasPrevious()) {
            if (Objects.equals(o, iterator.previous())) {
                return iterator.nextIndex();
            }
        }
        return -1;
    }
    
    /**
     * Removes all of the elements from this list (optional operation). The list will be empty after
     * this call returns.
     * 
     * @implSpec This implementation calls {@code removeRange(0,
     * size())}.
     * <p>
     * Note that this implementation throws an {@code UnsupportedOperationException} unless
     * {@code remove(int index)} or {@code removeRange(int fromIndex, int toIndex)} is overridden.
     * @throws UnsupportedOperationException if the {@code clear} operation is not supported by this
     *     list
     */
    @Override
    public void clear() {
        this.removeRange(0, this.size());
    }
    
    /**
     * Inserts all of the elements in the specified collection into this list at the specified
     * position (optional operation). Shifts the element currently at that position (if any) and any
     * subsequent elements to the right (increases their indices). The new elements will appear in
     * this list in the order that they are returned by the specified collection's iterator. The
     * behavior of this operation is undefined if the specified collection is modified while the
     * operation is in progress. (Note that this will occur if the specified collection is this
     * list, and it's nonempty.)
     * 
     * @implSpec This implementation gets an iterator over the specified collection and iterates
     * over it, inserting the elements obtained from the iterator into this list at the appropriate
     * position, one at a time, using {@code add(int, E)}. Many implementations will override this
     * method for efficiency.
     * <p>
     * Note that this implementation throws an {@code UnsupportedOperationException} unless
     * {@code add(int, E)} is overridden.
     * @param index index at which to insert the first element from the specified collection
     * @param c collection containing elements to be added to this list
     * @return {@code true} if this list changed as a result of the call
     * @throws UnsupportedOperationException if the {@code addAll} operation is not supported by
     *     this list
     * @throws ClassCastException if the class of an element of the specified collection prevents it
     *     from being added to this list
     * @throws NullPointerException if the specified collection contains one or more null elements
     *     and this list does not permit null elements, or if the specified collection is null
     * @throws IllegalArgumentException if some property of an element of the specified collection
     *     prevents it from being added to this list
     * @throws IndexOutOfBoundsException if the index is out of range
     *     {@code (index < 0 || index > size())}
     */
    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        MySkeletalList.indexCheckIncludeSize(index, this.size());
        boolean changed = false;
        for (E e : c) {
            this.add(index, e);
            index++;
            changed = true;
        }
        return changed;
    }
    
    /**
     * Returns an iterator over the elements in this list in proper sequence.
     * 
     * @implSpec This implementation returns a straightforward implementation of the iterator
     * interface, relying on the backing list's {@code size()}, {@code get(int)}, and
     * {@code remove(int)} methods.
     * <p>
     * Note that the iterator returned by this method will throw an
     * {@code UnsupportedOperationException} in response to its {@code remove} method unless the
     * list's {@code remove(int)} method is overridden.
     * <p>
     * This implementation can be made to throw runtime exceptions in the face of concurrent
     * modification, as described in the specification for the (protected) {@code modCount} field.
     * @returns an iterator over the elements in this list in proper sequence
     */
    @Override
    public Iterator<E> iterator() {
        return new Iter();
    }
    
    /**
     * Returns a list iterator over the elements in this list (in proper sequence).
     * 
     * @implSpec This implementation returns {@code listIterator(0)}.
     * @return a list iterator over the elements in this list (in proper sequence) See Also:
     * listIterator(int)
     */
    @Override
    public ListIterator<E> listIterator() {
        return this.listIterator(0);
    }
    
    /**
     * Returns a list iterator over the elements in this list (in proper sequence), starting at the
     * specified position in the list. The specified index indicates the first element that would be
     * returned by an initial call to {@link java.util.ListIterator#next()}. An initial call to
     * {@link java.util.ListIterator#previous()} would return the element with the specified index
     * minus one.
     * 
     * @implSpec This implementation returns a straightforward implementation of the
     * {@code ListIterator} interface that extends the implementation of the {@code Iterator}
     * interface returned by the {@code iterator()} method. The {@code ListIterator} implementation
     * relies on the backing list's {@code get(int)}, {@code set(int, E)}, {@code add(int, E)} and
     * {@code remove(int)} methods.
     * <p>
     * Note that the list iterator returned by this implementation will throw an
     * {@code UnsupportedOperationException} in response to its {@code remove}, {@code set}, and
     * {@code add} methods unless the list's {@code remove(int)}, {@code set(int, E)}, and
     * {@code add(int, E)} methods are overridden.
     * <p>
     * This implementation can be made to throw runtime exceptions in the face of concurrent
     * modification, as described in the specification for the (protected) {@code modCount} field.
     * @param index index of the first element to be returned from the list iterator (by a call to
     *     {@link java.util.ListIterator#next})
     * @return a list iterator over the elements in this list (in proper sequence), starting at the
     * specified position in the list
     * @throws IndexOutOfBoundsException if the index is out of range
     *     {@code (index < 0 || index > size())}
     */
    @Override
    public ListIterator<E> listIterator(int index) {
        MySkeletalList.indexCheckIncludeSize(index, this.size());
        return new ListIter(index);
    }
    
    /**
     * This class fulfills the implementation described by the {@code iterator} method.
     * 
     * @see MySkeletalList#iterator()
     */
    private class Iter extends MySkeletalIterator<E> implements Iterator<E> {
        
        /**
         * Index of element to be returned by subsequent call to next.
         */
        int cursor = 0;
        
        /**
         * The {@code modCount} value that the iterator believes that the backing {@code List}
         * should have. If this expectation is violated, the iterator has detected concurrent
         * modification.
         */
        int expectedModCount = modCount;
        
        /**
         * Creates an {@code Iter} object that sets up the cursor before the first element in the
         * list.
         */
        public Iter() {
            // Default values shown on the fields for ease of understanding.
        }
        
        /**
         * Throws a {@code ConcurrentModificationException} if this detects a change with what is
         * expected.
         * 
         * @throws ConcurrentModificationException if this iterator detects a change with what is
         *     expected.
         */
        void checkForComodification() {
            if (expectedModCount != modCount) {
                throw new ConcurrentModificationException();
            }
        }
        
        /**
         * Returns {@code true} if the iteration has more elements. (In other words, returns
         * {@code true} if {@code next()} would return an element rather than throwing an
         * exception.)
         * 
         * @return {@code true} if the iteration has more elements
         */
        @Override
        public boolean hasNext() {
            return cursor < MySkeletalList.this.size();
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
            checkForComodification();
            try {
                E item = MySkeletalList.this.get(cursor);
                cursor++;
                state = State.MOVED_FORWARDS;
                return item;
            } catch (IndexOutOfBoundsException e) {
                checkForComodification();
                throw new NoSuchElementException();
            }
        }
        
        /**
         * Removes from the underlying collection the last element returned by this iterator. This
         * method can be called only once per call to {@code next()}.
         * <p>
         * The behavior of an iterator is unspecified if the underlying collection is modified while
         * the iteration is in progress in any way other than by calling this method, unless an
         * overriding class has specified a concurrent modification policy.
         * <p>
         * The behavior of an iterator is unspecified if this method is called after a call to the
         * {@code forEachRemaining} method.
         * 
         * @implNote This implementation is optimized for ListIter to use this without modification.
         * @throws IllegalStateException if the {@code next} method has not yet been called, or the
         *     {@code remove} method has already been called after the last call to the {@code next}
         *     method
         */
        @Override
        public void remove() {
            if (state.cannotModify()) {
                throw new IllegalStateException();
            }
            checkForComodification();
            if (state == State.MOVED_BACKWARDS) {
                cursor--;
            }
            try {
                MySkeletalList.this.remove(cursor);
                state = State.MODIFIED;
                expectedModCount = modCount;
            } catch (IndexOutOfBoundsException e) {
                throw new ConcurrentModificationException();
            }
        }
        
    }
    
    /**
     * This class fulfills the implementation described by the {@code listIterator(int index)}
     * method.
     * 
     * @see MySkeletalList#listIterator(int)
     */
    private class ListIter extends Iter implements ListIterator<E> {
        
        /**
         * Creates a list iterator from the specified index. The index indicates the first element
         * that would be returned by an initial call to {@link #next()}.
         * 
         * @param startingIndex The index that indicates the first element that would be returned by
         *     a call to {@link #next()}
         */
        ListIter(int startingIndex) {
            cursor = startingIndex;
        }
        
        /**
         * Returns {@code true} if this list iterator has more elements when traversing the list in
         * the reverse direction. (In other words, returns {@code true} if {@code previous()} would
         * return an element rather than throwing an exception.)
         * 
         * @return {@code true} if the list iterator has more elements when traversing the list in
         * the reverse direction
         */
        @Override
        public boolean hasPrevious() {
            return cursor > 0;
        }
        
        /**
         * Returns the previous element in the list and moves the cursor position backwards. This
         * method may be called repeatedly to iterate through the list backwards, or intermixed with
         * calls to {@code next()} to go back and forth. (Note that alternating calls to
         * {@code next} and {@code previous} will return the same element repeatedly.)
         * 
         * @return the previous element in the list
         * @throws NoSuchElementException if the iteration has no previous element
         */
        @Override
        public E previous() {
            if (!this.hasPrevious()) {
                throw new NoSuchElementException();
            }
            checkForComodification();
            try {
                cursor--;
                E item = MySkeletalList.this.get(cursor);
                state = State.MOVED_BACKWARDS;
                return item;
            } catch (IndexOutOfBoundsException e) {
                checkForComodification();
                throw new NoSuchElementException();
            }
        }
        
        /**
         * Returns the index of the element that would be returned by a subsequent call to
         * {@code next()}. (Returns list size if the list iterator is at the end of the list.)
         * 
         * @return the index of the element that would be returned by a subsequent call to
         * {@code next}, or list size if the list iterator is at the end of the list
         */
        @Override
        public int nextIndex() {
            return cursor;
        }
        
        /**
         * Returns the index of the element that would be returned by a subsequent call to
         * {@code previous()}. (Returns -1 if the list iterator is at the beginning of the list.)
         * 
         * @return the index of the element that would be returned by a subsequent call to
         * {@code previous}, or -1 if the list iterator is at the beginning of the list
         */
        @Override
        public int previousIndex() {
            return cursor - 1;
        }
        
        /**
         * Replaces the last element returned by {@code next()} or {@code previous()} with the
         * specified element. This call can be made only if neither {@code remove()} nor
         * {@code add(E)} have been called after the last call to {@code next} or {@code previous}.
         * 
         * @param e the element with which to replace the last element returned by {@code next} or
         *     {@code previous}
         * @throws UnsupportedOperationException if the {@code set} operation is not supported by
         *     this list iterator
         * @throws ClassCastException if the class of the specified element prevents it from being
         *     added to this list
         * @throws IllegalArgumentException if some aspect of the specified element prevents it from
         *     being added to this list
         * @throws IllegalStateException if neither {@code next} nor {@code previous} have been
         *     called, or {@code remove} or {@code add} have been called after the last call to
         *     {@code next} or {@code previous}
         */
        @Override
        public void set(E e) {
            if (state.cannotModify()) {
                throw new IllegalStateException();
            }
            checkForComodification();
            try {
                MySkeletalList.this.set(cursor, e);
                expectedModCount = modCount;
            } catch (IndexOutOfBoundsException ex) {
                throw new ConcurrentModificationException();
            }
        }
        
        /**
         * Inserts the specified element into the list. The element is inserted immediately before
         * the element that would be returned by {@code next()}, if any, and after the element that
         * would be returned by {@code previous()}, if any. (If the list contains no elements, the
         * new element becomes the sole element on the list.) The new element is inserted before the
         * implicit cursor: a subsequent call to {@code next} would be unaffected, and a subsequent
         * call to {@code previous} would return the new element. (This call increases by one the
         * value that would be returned by a call to {@code nextIndex} or {@code previousIndex}.)
         * 
         * @param e the element to insert
         * @throws UnsupportedOperationException if the {@code add} method is not supported by this
         *     list iterator
         * @throws ClassCastException if the class of the specified element prevents it from being
         *     added to this list
         * @throws IllegalArgumentException if some aspect of this element prevents it from being
         *     added to this list
         */
        @Override
        public void add(E e) {
            checkForComodification();
            try {
                MySkeletalList.this.add(cursor, e);
                cursor++;
                state = State.MODIFIED;
                expectedModCount = modCount;
            } catch (IndexOutOfBoundsException ex) {
                throw new ConcurrentModificationException();
            }
        }
        
    }
    
    /**
     * Returns a view of the portion of this list between the specified {@code fromIndex},
     * inclusive, and {@code toIndex}, exclusive. (If {@code fromIndex} and {@code toIndex} are
     * equal, the returned list is empty.) The returned list is backed by this list, so
     * non-structural changes in the returned list are reflected in this list, and vice-versa. The
     * returned list supports all of the optional list operations supported by this list.
     * <p>
     * This method eliminates the need for explicit range operations (of the sort that commonly
     * exist for arrays). Any operation that expects a list can be used as a range operation by
     * passing a subList view instead of a whole list. For example, the following idiom removes a
     * range of elements from a list:
     * 
     * <pre>
     *     {@code list.subList(from, to).clear();}
     * </pre>
     * <p>
     * Similar idioms may be constructed for {@code indexOf} and {@code lastIndexOf}, and all of the
     * algorithms in the {@code Collections} class can be applied to a subList.
     * <p>
     * The semantics of the list returned by this method become undefined if the backing list (i.e.,
     * this list) is <em>structurally modified</em> in any way other than via the returned list.
     * (Structural modifications are those that change the size of this list, or otherwise perturb
     * it in such a fashion that iterations in progress may yield incorrect results.)
     * 
     * @implSpec This implementation returns a list that subclasses {@code MySkeletalList}. The
     * subclass stores, in private fields, the size of the subList (which can change over its
     * lifetime), and the expected {@code modCount} value of the backing list. There are two
     * variants of the subclass, one of which implements {@code RandomAccess}. If this list
     * implements {@code RandomAccess} the returned list will be an instance of the subclass that
     * implements {@code RandomAccess}.
     * <p>
     * The subclass's {@code set(int, E)}, {@code get(int)}, {@code add(int, E)},
     * {@code remove(int)}, {@code addAll(int,
     * Collection)} and {@code removeRange(int, int)} methods all delegate to the corresponding
     * methods on the backing abstract list, after bounds-checking the index and adjusting for the
     * offset. The {@code addAll(Collection<?> c)} method merely returns {@code addAll(size, c)}.
     * <p>
     * The {@code listIterator(int)} method returns a "wrapper object" over a list iterator on the
     * backing list, which is created with the corresponding method on the backing list. The
     * {@code iterator} method merely returns {@code listIterator()}, and the {@code size} method
     * merely returns the subclass's {@code size} field.
     * <p>
     * All methods first check to see if the actual {@code modCount} of the backing list is equal to
     * its expected value, and throw a {@code ConcurrentModificationException} if it is not.
     * @param fromIndex low endpoint (inclusive) of the subList
     * @param toIndex high endpoint (exclusive) of the subList
     * @return a view of the specified range within this list
     * @throws IndexOutOfBoundsException if an endpoint index value is out of range
     *     {@code (fromIndex < 0 || toIndex > size)}
     * @throws IllegalArgumentException if the endpoint indices are out of order
     *     {@code (fromIndex > toIndex)}
     */
    @Override
    public List<E> subList(int fromIndex, int toIndex) {
        rangeCheckIncludeSize(fromIndex, toIndex, this.size());
        
        if (this instanceof RandomAccess) {
            return new RandomAccessSubList<>(this, fromIndex, toIndex);
        } else {
            return new SubList<>(this, fromIndex, toIndex);
        }
    }
    
    /**
     * This class fulfills the implementation described by the {@code subList} method. Note that
     * structural changes must be observed in a recursive fashion: a list (even another sublist)
     * must see its own sublist's structural changes even if two sublists of a list cannot see each
     * others' changes, or see the changes of the original list. This can be visualized as a tree,
     * with the original list as the root and children "sublists" underneath the root that determine
     * visibility. Changes in a "sublist" node push up to its parent nodes.
     * 
     * @see MySkeletalList#subList(int, int)
     */
    private static class SubList<E> extends MySkeletalList<E> {
        /**
         * The original list. All calls to a sublist are delegated to the root.
         */
        final MySkeletalList<E> root;
        /**
         * The SubList that created this list, or {@code null} if this was created by a
         * {@code MySkeletalList}.
         */
        private final SubList<E> parent;
        /**
         * The offset from 0 that operations should take place on the root list.
         */
        private final int offset;
        /**
         * The total size of this list.
         */
        private int size;
        
        /**
         * Creates a SubList with the given root, and spanning across the range of the given
         * indices. This constructor is called from the {@link MySkeletalList#subList(int, int)}
         * method directly.
         * 
         * @param root the list that created this sublist
         * @param fromIndex index of the first element (inclusive) of the sublist relative to the
         *     root list
         * @param toIndex index of the last element (exclusive) of the sublist relative to the root
         *     list
         */
        SubList(MySkeletalList<E> root, int fromIndex, int toIndex) {
            this(root, null, fromIndex, toIndex);
        }
        
        /**
         * Creates a SubList with the given root, a parent sublist, and spanning across the range of
         * the given indices. This constructor is called from the {@link SubList#subList(int, int)}
         * method if the parent is not {@code null}, and called from
         * {@link MySkeletalList#subList(int, int)} indirectly if the parent is {@code null}.
         * 
         * @param root the list that this sublist eventually derives from
         * @param parent the sublist that this sublist derives from, or {@code null} if this derives
         *     from a {@code MySkeletalList}
         * @param fromIndex index of the first element (inclusive) of the sublist relative to the
         *     root list
         * @param toIndex index of the last element (exclusive) of the sublist relative to the root
         *     list
         */
        SubList(MySkeletalList<E> root, SubList<E> parent, int fromIndex, int toIndex) {
            this.root = root;
            this.parent = parent;
            this.offset = fromIndex;
            this.size = toIndex - fromIndex;
            this.modCount = root.modCount;
        }
        
        /**
         * {@inheritDoc}
         * 
         * @implSpec This implementation delegates to the {@link MySkeletalList#set(int, Object)}
         * method of the root list, adjusting the index by the offset provided at construction.
         */
        @Override
        public E set(int index, E element) {
            Objects.checkIndex(index, size);
            checkForComodification();
            return root.set(index + offset, element);
        }
        
        /**
         * {@inheritDoc}
         * 
         * @implSpec This implementation delegates to the {@link MySkeletalList#get(int)} method of
         * the root list, adjusting the index by the offset provided at construction.
         */
        @Override
        public E get(int index) {
            Objects.checkIndex(index, size);
            checkForComodification();
            return root.get(index + offset);
        }
        
        /**
         * {@inheritDoc}
         * 
         * @implSpec This implementation delegates to the {@link MySkeletalList#add(int, Object)}
         * method of the root list, adjusting the index by the offset provided at construction.
         */
        @Override
        public void add(int index, E element) {
            Objects.checkIndex(index, size);
            checkForComodification();
            root.add(index + offset, element);
            updateStateWithSizeChangeOf(1);
        }
        
        /**
         * {@inheritDoc}
         * 
         * @implSpec This implementation delegates to the {@link MySkeletalList#remove(int)} method
         * of the root list, adjusting the index by the offset provided at construction.
         */
        @Override
        public E remove(int index) {
            Objects.checkIndex(index, size);
            checkForComodification();
            E removed = root.remove(index + offset);
            updateStateWithSizeChangeOf(-1);
            return removed;
        }
        
        /**
         * {@inheritDoc}
         * 
         * @implSpec This implementation delegates this sublist's {@link #addAll(int, Collection)},
         * providing this sublist's {@link #size()} as the index.
         */
        @Override
        public boolean addAll(Collection<? extends E> c) {
            return this.addAll(size, c);
        }
        
        /**
         * {@inheritDoc}
         * 
         * @implSpec This implementation delegates to the
         * {@link MySkeletalList#addAll(int, Collection)} method of the root list, adjusting the
         * index by the offset provided at construction.
         */
        @Override
        public boolean addAll(int index, Collection<? extends E> c) {
            Objects.checkIndex(index, size);
            if (c.isEmpty()) {
                return false;
            }
            checkForComodification();
            boolean changed = root.addAll(index + offset, c);
            if (!changed) {
                throw new ConcurrentModificationException();
            }
            updateStateWithSizeChangeOf(c.size());
            return changed;
        }
        
        /**
         * {@inheritDoc}
         * 
         * @implSpec This method delegates to this sublist's {@link #listIterator()} method.
         */
        @Override
        public Iterator<E> iterator() {
            return this.listIterator();
        }
        
        /**
         * {@inheritDoc}
         * 
         * @implSpec This implementation returns a "wrapper object" over a list iterator on the
         * backing list, which is created with the corresponding method on the backing list.
         */
        @Override
        public ListIterator<E> listIterator(int index) {
            MySkeletalList.indexCheckIncludeSize(index, size);
            checkForComodification();
            
            /**
             * This {@code ListIterator} delegates all calls to a backing {@code ListIterator}
             * provided by the root {@code MySkeletalList}.
             */
            class SubListIterator extends MySkeletalIterator<E> implements ListIterator<E> {
                
                /**
                 * The backing {@code ListIterator} constructed from the root list that all calls
                 * are delegated to.
                 */
                private ListIterator<E> rootIterator;
                
                /**
                 * Creates a {@code SubListIterator} starting at this sublist's index (or, the root
                 * list's index + the offset of the sublist).
                 * 
                 * @param index index of the first element to be returned from the list iterator (by
                 *     a call to {@link java.util.ListIterator#next})
                 */
                SubListIterator(int index) {
                    this.rootIterator = root.listIterator(index + offset);
                }
                
                /**
                 * Returns {@code true} if this list iterator has more elements when traversing the
                 * list in the forward direction. (In other words, returns true if next() would
                 * return an element rather than throwing an exception.)
                 * 
                 * @return {@code true} if the list iterator has more elements when traversing the
                 * list in the forward direction
                 */
                @Override
                public boolean hasNext() {
                    return this.nextIndex() < size;
                }
                
                /**
                 * Returns the next element in the list and advances the cursor position. This
                 * method may be called repeatedly to iterate through the list, or intermixed with
                 * calls to previous() to go back and forth. (Note that alternating calls to next
                 * and previous will return the same element repeatedly.)
                 * 
                 * @return the next element in the list
                 * @throws NoSuchElementException if the iteration has no next element
                 */
                @Override
                public E next() {
                    if (this.hasNext()) {
                        return rootIterator.next();
                    } else {
                        throw new NoSuchElementException();
                    }
                }
                
                /**
                 * Returns {@code true} if this list iterator has more elements when traversing the
                 * list in the reverse direction. (In other words, returns true if previous() would
                 * return an element rather than throwing an exception.)
                 * 
                 * @return {@code true} if the list iterator has more elements when traversing the
                 * list in the reverse direction
                 */
                @Override
                public boolean hasPrevious() {
                    return this.previousIndex() >= 0;
                }
                
                /**
                 * Returns the previous element in the list and moves the cursor position backwards.
                 * This method may be called repeatedly to iterate through the list backwards, or
                 * intermixed with calls to next() to go back and forth. (Note that alternating
                 * calls to next and previous will return the same element repeatedly.)
                 * 
                 * @return the previous element in the list
                 * @throws NoSuchElementException if the iteration has no previous element
                 */
                @Override
                public E previous() {
                    if (this.hasPrevious()) {
                        return rootIterator.previous();
                    } else {
                        throw new NoSuchElementException();
                    }
                }
                
                /**
                 * Returns the index of the element that would be returned by a subsequent call to
                 * next(). (Returns list size if the list iterator is at the end of the list.)
                 * 
                 * @return the index of the element that would be returned by a subsequent call to
                 * next, or list size if the list iterator is at the end of the list
                 */
                @Override
                public int nextIndex() {
                    return rootIterator.nextIndex() - offset;
                }
                
                /**
                 * Returns the index of the element that would be returned by a subsequent call to
                 * previous(). (Returns -1 if the list iterator is at the beginning of the list.)
                 * 
                 * @return the index of the element that would be returned by a subsequent call to
                 * previous, or -1 if the list iterator is at the beginning of the list
                 */
                @Override
                public int previousIndex() {
                    return rootIterator.previousIndex() - offset;
                }
                
                /**
                 * Removes from the list the last element that was returned by next() or previous().
                 * This call can only be made once per call to next or previous. It can be made only
                 * if add(E) has not been called after the last call to next or previous.
                 * 
                 * @throws if neither next nor previous have been called, or remove or add have been
                 *     called after the last call to next or previous
                 */
                @Override
                public void remove() {
                    rootIterator.remove();
                    updateStateWithSizeChangeOf(-1);
                }
                
                /**
                 * Replaces the last element returned by next() or previous() with the specified
                 * element. This call can be made only if neither remove() nor add(E) have been
                 * called after the last call to next or previous.
                 * 
                 * @param e the element with which to replace the last element returned by next or
                 *     previous
                 * @throws IllegalStateException if neither next nor previous have been called, or
                 *     remove or add have been called after the last call to next or previous
                 */
                @Override
                public void set(E e) {
                    rootIterator.set(e);
                }
                
                /**
                 * Inserts the specified element into the list. The element is inserted immediately
                 * before the element that would be returned by next(), if any, and after the
                 * element that would be returned by previous(), if any. (If the list contains no
                 * elements, the new element becomes the sole element on the list.) The new element
                 * is inserted before the implicit cursor: a subsequent call to next would be
                 * unaffected, and a subsequent call to previous would return the new element. (This
                 * call increases by one the value that would be returned by a call to nextIndex or
                 * previousIndex.)
                 * 
                 * @param e the element to insert
                 * @throws IllegalArgumentException if some aspect of this element prevents it from
                 *     being added to this list
                 */
                @Override
                public void add(E e) {
                    rootIterator.add(e);
                    updateStateWithSizeChangeOf(1);
                }
                
                /**
                 * Returns a string representation of this iterator that represents what it iterates
                 * over. Its format is undefined.
                 * 
                 * @return a string representation of this iterator
                 */
                @Override
                public String toString() {
                    return "current index = " + (this.nextIndex() - 1);
                }
                
            }
            
            return new SubListIterator(index);
        }
        
        /**
         * {@inheritDoc}
         * 
         * @implSpec This implementation delegates to the
         * {@link MySkeletalList#removeRange(int, int)} method of the root list, adjusting the
         * indices by the offset provided at construction.
         */
        @Override
        protected void removeRange(int fromIndex, int toIndex) {
            MySkeletalList.rangeCheckIncludeSize(fromIndex, toIndex, size);
            if (fromIndex - toIndex == 0) {
                return;
            }
            checkForComodification();
            root.removeRange(fromIndex + offset, toIndex + offset);
            updateStateWithSizeChangeOf(fromIndex - toIndex);
        }
        
        /**
         * {@inheritDoc}
         * 
         * @implSpec This implementation returns the backing "size" field, as specified by the
         * {@link MySkeletalList#subList(int, int)} specification.
         */
        @Override
        public int size() {
            return size;
        }
        
        /**
         * {@inheritDoc}
         * 
         * @implSpec This implementation returns a sublist that fits within this given sublist,
         * passing itself as the parent of the newly created sublist. This ensures that this sublist
         * will see any changes the newly created sublist makes.
         */
        @Override
        public List<E> subList(int fromIndex, int toIndex) {
            MySkeletalList.rangeCheckIncludeSize(fromIndex, toIndex, this.size());
            return new SubList<>(root, this, fromIndex, toIndex);
        }
        
        /**
         * Throws a {@code ConcurrentModificationException} if this detects a change with what is
         * expected.
         * 
         * @throws ConcurrentModificationException if this iterator detects a change with what is
         *     expected.
         */
        private void checkForComodification() {
            if (root.modCount != this.modCount) {
                throw new ConcurrentModificationException();
            }
        }
        
        /**
         * Updates the state of the sublist based off of the number of elements that were added or
         * removed. The change parameter is positive to represent a number of elements that were
         * added, and is negative to represent a number of elements that were removed. This should
         * still be called if the list was modified without changing the number of elements: this
         * would take in 0 instead.
         * 
         * @implSpec This implementation changes the state of this sublist and every ancestor
         * sublist of this. It changes the size field by the change provided, and resets the
         * modCount to the root list's.
         * @param change The number of elements that were added. If positive, represents a number of
         *     elements that were added. If negative, the absolute value of this number is how many
         *     elements were removed. IF 0, no elements were added or removed, but the state of the
         *     list may have changed.
         */
        private void updateStateWithSizeChangeOf(int change) {
            for (SubList<E> subList = this; subList != null; subList = subList.parent) {
                this.size += change;
                this.modCount = root.modCount;
            }
        }
        
    }
    
    /**
     * This class fulfills the implementation described by the {@code subList} method. For any
     * RandomAccess list, its sublists will also implement RandomAccess.
     */
    private static class RandomAccessSubList<E> extends SubList<E> implements RandomAccess {
        
        /**
         * Creates a RandomAcecssSubList with the given root, and spanning across the range of the
         * given indices. This constructor is called from the
         * {@link MySkeletalList#subList(int, int)} method directly.
         * 
         * @param root the list that created this sublist
         * @param fromIndex index of the first element (inclusive) of the sublist relative to the
         *     root list
         * @param toIndex index of the last element (exclusive) of the sublist relative to the root
         *     list
         */
        RandomAccessSubList(MySkeletalList<E> root, int fromIndex, int toIndex) {
            super(root, fromIndex, toIndex);
        }
        
        /**
         * Creates a RandomAccessSubList with the given root, a parent sublist, and spanning across
         * the range of the given indices. This constructor is called from the
         * {@link RandomAccessSubList#subList(int, int)} method if the parent is not {@code null},
         * and called from {@link MySkeletalList#subList(int, int)} indirectly if the parent is
         * {@code null}.
         * 
         * @param root the list that this sublist eventually derives from
         * @param parent the sublist that this sublist derives from, or {@code null} if this derives
         *     from a {@code MySkeletalList}
         * @param fromIndex index of the first element (inclusive) of the sublist relative to the
         *     root list
         * @param toIndex index of the last element (exclusive) of the sublist relative to the root
         *     list
         */
        RandomAccessSubList(MySkeletalList<E> root, RandomAccessSubList<E> parent, int fromIndex, int toIndex) {
            super(root, parent, fromIndex, toIndex);
        }
        
        /**
         * {@inheritDoc}
         * 
         * @implSpec This implementation returns a sublist that fits within this given sublist and
         * that implements {@code RandomAccess}, passing itself as the parent of the newly created
         * sublist. This ensures that this sublist will see any changes the newly created sublist
         * makes, and that all sublists implement {@code RandomAccess}.
         */
        @Override
        public List<E> subList(int fromIndex, int toIndex) {
            MySkeletalList.rangeCheckIncludeSize(fromIndex, toIndex, this.size());
            return new RandomAccessSubList<>(root, this, fromIndex, toIndex);
        }
        
    }
    
    /**
     * Compares the specified object with this list for equality. Returns {@code true} if and only
     * if the specified object is also a list, both lists have the same size, and all corresponding
     * pairs of elements in the two lists are <em>equal</em>. (Two elements {@code e1} and
     * {@code e2} are <em>equal</em> if {@code (e1==null ? e2==null : e1.equals(e2))}.) In other
     * words, two lists are defined to be equal if they contain the same elements in the same order.
     * 
     * @implSpec This implementation first checks if the specified object is this list. If so, it
     * returns {@code true}; if not, it checks if the specified object is a list. If not, it returns
     * {@code false}; if so, it iterates over both lists, comparing corresponding pairs of elements.
     * If any comparison returns {@code false}, this method returns {@code false}. If either
     * iterator runs out of elements before the other it returns {@code false} (as the lists are of
     * unequal length); otherwise it returns {@code true} when the iterations complete.
     * @param o the object to be compared for equality with this list
     * @returns {@code true} if the specified object is equal to this list
     * @see Object#hashCode()
     * @see HashMap
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof List<?>)) {
            return false;
        }
        Iterator<E> thisIterator = this.iterator();
        Iterator<?> thatIterator = ((List<?>) o).iterator();
        while (thisIterator.hasNext() && thatIterator.hasNext()) {
            var thisObject = thisIterator.next();
            var thatObject = thatIterator.next();
            if (!Objects.equals(thisObject, thatObject)) {
                return false;
            }
        }
        return !(thisIterator.hasNext() || thatIterator.hasNext());
    }
    
    /**
     * Returns the hash code value for this list.
     * 
     * @implSpec This implementation uses exactly the code that is used to define the list hash
     * function in the documentation for the {@link java.util.List#hashCode()} method.
     * @return the hash code value for this list
     * @see Object.equals(java.lang.Object)
     * @see System.identityHashCode(java.lang.Object)
     */
    @Override
    public int hashCode() {
        int hashCode = 1;
        for (E e : this) {
            hashCode = 31 * hashCode + Objects.hashCode(e);
        }
        return hashCode;
    }
    
    /**
     * Removes from this list all of the elements whose index is between {@code fromIndex},
     * inclusive, and {@code toIndex}, exclusive. Shifts any succeeding elements to the left
     * (reduces their index). This call shortens the list by {@code (toIndex - fromIndex)} elements.
     * (If {@code toIndex==fromIndex}, this operation has no effect.)
     * <p>
     * This method is called by the {@code clear} operation on this list and its subLists.
     * Overriding this method to take advantage of the internals of the list implementation can
     * <em>substantially</em> improve the performance of the {@code clear} operation on this list
     * and its subLists.
     * 
     * @implSpec This implementation gets a list iterator positioned before {@code fromIndex}, and
     * repeatedly calls {@code ListIterator.next} followed by {@code ListIterator.remove} until the
     * entire range has been removed. <strong>Note: if {@code ListIterator.remove} requires linear
     * time, this implementation requires quadratic time.</strong>
     * @paramfromIndex index of first element to be removed
     * @param toIndex index after last element to be removed
     * @throws IndexOutOfBoundsException if an endpoint index value is out of range
     *     {@code (fromIndex < 0 || toIndex > size)}
     * @throws IllegalArgumentException if the endpoint indices are out of order
     *     {@code (fromIndex > toIndex)}
     */
    protected void removeRange(int fromIndex, int toIndex) {
        MySkeletalList.rangeCheckIncludeSize(fromIndex, toIndex, this.size());
        if (fromIndex - toIndex == 0) {
            return;
        }
        ListIterator<E> iterator = this.listIterator(fromIndex);
        for (int i = 0, range = toIndex - fromIndex; i < range; i++) {
            iterator.next();
            iterator.remove();
        }
    }
    
    /**
     * Replaces each element of this list with the result of applying the operator to that element.
     * Errors or runtime exceptions thrown by the operator are relayed to the caller.
     * 
     * @implSpec The implementation is equivalent to, for this {@code list}:
     * 
     * <pre>
     * {@code 
     * final ListIterator<E> li = list.listIterator();
     * while (li.hasNext()) {
     *   li.set(operator.apply(li.next()));
     * }
     * }
     * </pre>
     * <p>
     * If the list's list-iterator does not support the set operation then an
     * {@code UnsupportedOperationException} will be thrown when replacing the first element.
     * @param operator the operator to apply to each element
     * @throws UnsupportedOperationException if this list is unmodifiable. Implementations may throw
     *     this exception if an element cannot be replaced or if, in general, modification is not
     *     supported
     * @throws NullPointerException if the specified operator is null or if the operator result is a
     *     null value and this list does not permit null elements (optional)
     */
    @Override
    public void replaceAll(UnaryOperator<E> operator) {
        ListIterator<E> iterator = this.listIterator();
        while (iterator.hasNext()) {
            E replacement = operator.apply(iterator.next());
            iterator.set(replacement);
        }
    }
    
    /**
     * Sorts this list according to the order induced by the specified {@code Comparator}. The sort
     * is <em>stable</em>: this method must not reorder equal elements.
     * <p>
     * All elements in this list must be <em>mutually comparable</em> using the specified comparator
     * (that is, {@code c.compare(e1, e2)} must not throw a {@code ClassCastException} for any
     * elements {@code e1} and {@code e2} in the list).
     * <p>
     * If the specified comparator is {@code null} then all elements in this list must implement the
     * {@code Comparable} interface and the elements' natural ordering should be used.
     * <p>
     * This list must be modifiable, but need not be resizable.
     * 
     * @implSpec This implementation obtains an array containing all elements in this list, sorts
     * the array, and iterates over this list resetting each element from the corresponding position
     * in the array. (This avoids the n<sup>2</sup> log(n) performance that would result from
     * attempting to sort a linked list in place.)
     * @implNote This implementation is a stable, adaptive, iterative mergesort that requires far
     * fewer than n lg(n) comparisons when the input array is partially sorted, while offering the
     * performance of a traditional mergesort when the input array is randomly ordered. If the input
     * array is nearly sorted, the implementation requires approximately n comparisons. Temporary
     * storage requirements vary from a small constant for nearly sorted input arrays to n/2 object
     * references for randomly ordered input arrays.
     * <p>
     * The implementation takes equal advantage of ascending and descending order in its input
     * array, and can take advantage of ascending and descending order in different parts of the
     * same input array. It is well-suited to merging two or more sorted arrays: simply concatenate
     * the arrays and sort the resulting array.
     * <p>
     * The implementation was adapted from Tim Peters's list sort for Python ( TimSort). It uses
     * techniques from Peter McIlroy's "Optimistic Sorting and Information Theoretic Complexity", in
     * Proceedings of the Fourth Annual ACM-SIAM Symposium on Discrete Algorithms, pp 467-474,
     * January 1993.
     * @param c the {@ode Comparator} used to compare list elements. A {@code null} value indicates
     *     that the elements' natural ordering should be used
     * @throws ClassCastException if the list contains elements that are not <em>mutually
     *     comparable</em> using the specified comparator
     * @throws UnsupportedOperationException if the list's list-iterator does not support the set
     *     operation
     * @throws IllegalArgumentException (optional) if the comparator is found to violate the
     *     Comparator contract
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public void sort(Comparator<? super E> c) {
        Object[] array = this.toArray();
        Arrays.sort(array, (Comparator) c);
        ListIterator<E> iterator = this.listIterator();
        for (Object o : array) {
            iterator.next();
            iterator.set((E) o);
        }
    }
    
    /**
     * Creates a {@code Spliterator} over the elements in this list.
     * <p>
     * The Spliterator reports {@link Spliterator.SIZED} and {@link Spliterator.ORDERED}.
     * Implementations should document the reporting of additional characteristic values.
     * 
     * @implSpec This implementation creates a <em>late-binding</em> spliterator as follows:
     * <ul>
     * <li>If the list is an instance of {@code RandomAccess} then this implementation creates a
     * spliterator that traverses elements by invoking the method {@link List#get(int)}. If such
     * invocation results or would result in an {@code IndexOutOfBoundsException} then the
     * spliterator will <em>fail-fast</em> and throw a {@code ConcurrentModificationException}. If
     * the list is also an instance of {@code MySkeletalList} then the spliterator will use the
     * list's {@code modCount} field to provide additional <em>fail-fast</em> behavior.
     * <li>Otherwise, this implementation creates a spliterator from the list's Iterator. The
     * spliterator inherits the fail-fast of the list's iterator.
     * </ul>
     * @implNote The created {@code Spliterator} additionally reports {@link Spliterator.SUBSIZED}.
     * @return a {@code Spliterator} over the elements in this list
     */
    @Override
    public Spliterator<E> spliterator() {
        if (this instanceof RandomAccess) {
            return new RandomAccessSpliterator<>(this);
        } else {
            return Spliterators.spliterator(this, Spliterator.ORDERED);
        }
    }
    
    /**
     * This {@code Spliterator} is an object for traversing and partitioning elements of a
     * {@code MySkeletalList} that is also {@code RandomAccess}. It may traverse elements
     * individually ({@link #tryAdvance(Consumer)}) or sequentially in bulk
     * ({@link #forEachRemaining(Consumer)}). It traverses elements via {@link List#get(int)} rather
     * than an iterator due to a {@code RandomAccess} list not needing an {@code Iterator} to get
     * the next element to achieve {@code O(1)} access time.
     * <p>
     * This may also partition off some of its elements (using {@link #trySplit()}) as another
     * Spliterator, to be used in possibly-parallel operations. This uses an index-based
     * split-by-two algorithm to split off child {@code Spliterator}s.
     * <p>
     * This {@code Spliterator} has the characteristics of {@code ORDERED}, {@code SIZED}, and
     * {@code SUBSIZED}. {@code ORDERED} indicates that the elements are traversed in ascending
     * index order on {@link #forEachRemaining(Consumer)}, that the next element by index is
     * traversed on {@link #tryAdvance(Consumer)}, and that {@link #trySplit()} splits a strict
     * prefix of elements. {@code SIZED} indicates that the value returned from
     * {@link #estimateSize()} (and, subsequently, {@link #getExactSizeIfKnown()}) returns a finite
     * size that, in absence of structural source modification, represents the exact count of
     * elements that would be encountered b a complete traversal. {@code SUBSIZED} indicates that
     * any child {@code Spliterator} created from {@link #trySplit()} will also report {@code SIZED}
     * (and therefore, {@code SUBSIZED}).
     * <p>
     * This spliterator is <em>late-binding</em>, meaning that it binds to the source of elements at
     * the point of first traversal, first split, or first query for estimated size, rather than at
     * the time the Spliterator is created. If access results in an
     * {@code IndexOutOfBoundsException} then a {@code ConcurrentModificationException} is thrown
     * instead (since the list has been structurally modified while traversing). This will also
     * attempt to detect structural modification by using the {@code modCount} field from the list
     * to fail fast if that list is an instance of a {@code MySkeletalList}. This adheres to the
     * best-effort structural modification that can be detected on a {@code Spliterator} that is
     * neither {@code IMMUTABLE} nor {@code CONCURRENT}.
     * 
     * @param <T> the type of elements returned by this Spliterator
     */
    private static class RandomAccessSpliterator<T> implements Spliterator<T> {
        
        /**
         * The default value that {@code fence} is set to. The fence cannot be -1 since it must be
         * the size + 1, which is positive or {@code Integer.MIN_VALUE}.
         */
        private static final int UNINITIALIZED_FENCE = -1;
        
        /**
         * The list that this {@code Spliterator} iterates on
         */
        private final MySkeletalList<T> list;
        /**
         * Current index, advanced on split or traversal
         */
        private int index;
        /**
         * One past the greatest index this Spliterator will operate on
         */
        private int fence = UNINITIALIZED_FENCE;
        /**
         * The mod count that is compared to detect concurrent modification.
         */
        private int expectedModCount = 0;
        
        /**
         * Creates a {@code RandomAccessSpliterator} that traverses over the provided
         * {@code RandomAccess List}. It does not initialize the fence or the expectedModCount
         * (these are lazily initialized). This constructor is called from the
         * {@link MySkeletalList#spliterator()} method directly.
         * 
         * @param list the list that this will be traversed over
         */
        RandomAccessSpliterator(MySkeletalList<T> list) {
            this.list = list;
            this.index = 0;
        }
        
        /**
         * Creates a {@code Spliterator} from the given parent Spliterator. It initializes the fence
         * and the expectedModCount since the parent has already bound to the source by the time
         * this is being constructed. This constructor is called from {@link #trySplit()}.
         * 
         * @param parent the spliterator that is being split up with this one being constructed
         * @param low index of the first element (inclusive) that will be traversed
         * @param fence index of the last element (exclusive) that will be traversed
         */
        private RandomAccessSpliterator(RandomAccessSpliterator<T> parent, int low, int fence) {
            this.list = parent.list;
            this.index = low;
            this.fence = fence;
            this.expectedModCount = parent.expectedModCount;
        }
        
        /**
         * Fetches the fence (one past the greatest index), binding this spliterator to the list if
         * it hasn't been bound yet. This must be called before any method that would manipulate the
         * state of the Spliterator, and should generally be used rather than accessing
         * {@code fence} directly.
         * 
         * @return the value of the fence (one past the greatest index)
         */
        private int getFence() {
            if (fence == UNINITIALIZED_FENCE) {
                fence = list.size();
                this.expectedModCount = list.modCount;
            }
            return fence;
        }
        
        /**
         * Gets the item at the list's index. Throws a {@cde ConcurrentModificationException} if the
         * index is out of bounds (this would be due to a structural modification of the list).
         * 
         * @param index the index of the element to return
         * @return the element at the specified position in the list
         * @throws ConcurrentModificationException if the index is out of bounds of the list due to
         *     concurrent structural modification
         */
        private T getFromList(int index) {
            try {
                return list.get(index);
            } catch (IndexOutOfBoundsException e) {
                throw new ConcurrentModificationException();
            }
        }
        
        /**
         * Helps this Spliterator fail-fast if it detects the backing list has been modified during
         * traversal.
         * 
         * @throws ConcurrentModificationException if this detects the backing list has been
         *     modified
         */
        private void checkForComodification() {
            if (list.modCount != expectedModCount) {
                throw new ConcurrentModificationException();
            }
        }
        
        /**
         * If a remaining element exists, performs the given action on it, returning {@code true};
         * else returns {@code false}. Because this Spliterator is {@link Spliterator#ORDERED} the
         * action is performed on the next element in encounter order. Exceptions thrown by the
         * action are relayed to the caller.
         * 
         * @param action The action
         * @return {@code false} if no remaining elements existed upon entry to this method, else
         * {@code true}.
         * @throws NullPointerException if the specified action is null
         */
        @Override
        public boolean tryAdvance(Consumer<? super T> action) {
            Objects.requireNonNull(action);
            
            if (index < this.getFence()) {
                action.accept(this.getFromList(index));
                index++;
                checkForComodification();
                return true;
            } else {
                return false;
            }
        }
        
        /**
         * Performs the given action for each remaining element, sequentially in the current thread,
         * until all elements have been processed or the action throws an exception. Because this
         * Spliterator is {@link Spliterator#ORDERED}, actions are performed in encounter order.
         * Exceptions thrown by the action are relayed to the caller.
         * 
         * @param action The action
         * @throws NullPointerException if the specified action is null
         */
        @Override
        public void forEachRemaining(Consumer<? super T> action) {
            Objects.requireNonNull(action);
            for (int limit = this.getFence(); index < limit; index++) {
                action.accept(this.getFromList(index));
            }
            checkForComodification();
        }
        
        /**
         * If this spliterator can be partitioned, returns a Spliterator covering elements, that
         * will, upon return from this method, not be covered by this Spliterator. Since this
         * Spliterator is {@link Spliterator#ORDERED}, the returned Spliterator must cover a strict
         * prefix of the elements.
         * <p>
         * For any spliterator, repeated calls to {@code trySplit()} must eventually return
         * {@code null}. Since this spliterator is {@code SIZED} and {@code SUBSIZED}, upon non-null
         * return {@link #estimateSize()} for this spliterator before splitting must be equal to the
         * sum of estimateSize() for this and the returned Spliterator after splitting.
         * <p>
         * This method may return {@code null} for any reason, including emptiness, inability to
         * split after traversal has commenced, data structure constraints, and efficiency
         * considerations.
         * 
         * @implSpec This method splits the list in half, returning a Spliterator that operates on
         * the first half of elements being operated on by this.
         * @return a {@code Spliterator} covering some portion of the elements, or {@code null} if
         * this spliterator cannot be split
         */
        @Override
        public Spliterator<T> trySplit() {
            int mid = (this.getFence() + index) / 2;
            if (index >= mid) {
                return null;
            } else {
                int low = index;
                index = mid;
                return new RandomAccessSpliterator<>(this, low, mid);
            }
        }
        
        /**
         * Returns the number of elements that would be encountered by a
         * {@link #forEachRemaining(Consumer)} traversal.
         * <p>
         * Since this Spliterator is both {@code SIZED} and {@code SUBSIZED}, when this has not yet
         * been partially traversed or split, or when this Spliterator is
         * {@link Spliterator#SUBSIZED} and has not yet been partially traversed, this estimate must
         * be an accurate count of elements that would be encountered by a complete traversal.
         * 
         * @return the exact number of elements that would be encountered by a complete traversal of
         * this {@code Spliterator}
         */
        @Override
        public long estimateSize() {
            return (long) (this.getFence() - index);
        }
        
        /**
         * Convenience method that returns {@link #estimateSize()} since this Spliterator is
         * {@link Spliterator#SIZED}.
         * 
         * @return the exact size
         */
        @Override
        public long getExactSizeIfKnown() {
            return this.estimateSize();
        }
        
        /**
         * Returns a set of characteristics of this Spliterator and its elements. The result is
         * represented as ORed values from {@link Spliterator#ORDERED}, {@link Spliterator#SIZED},
         * and {@link Spliterator#SUBSIZED}.
         * 
         * @return a representation of characteristics: {@code ORDERED}, {@code SIZED}, and
         * {@code SUBSIZED}
         */
        @Override
        public int characteristics() {
            return Spliterator.ORDERED | Spliterator.SIZED | Spliterator.SUBSIZED;
        }
        
        /**
         * Returns {@code true} if this Spliterator's {@link #characteristics()}) contain all of the
         * given characteristics.
         * 
         * @param characteristics the characteristics to check for
         * @return {@code true} if all the specified characteristics are present, else {@code false}
         */
        @Override
        public boolean hasCharacteristics(int characteristics) {
            return (this.characteristics() & characteristics) == characteristics;
        }
        
        /**
         * If this Spliterator's source is {@link Spliterator#SORTED} by a {@link Comparator},
         * returns that {@code Comparator}. If the source is {@code SORTED} in natural order,
         * returns {@code null}. Otherwise, if the source is not {@code SORTED}, throws
         * {@link IllegalStateException}.
         * 
         * @return nothing, this always throws an {@code IllegalStateException}
         * @throws IllegalStateException always since this spliterator does not report a
         *     characteristic of {@code SORTED}.
         */
        @Override
        public Comparator<? super T> getComparator() {
            throw new IllegalStateException();
        }
    }
    
    /**
     * Throws an {@code IndexOutOfBoundsException} if the provided index is not between 0 and the
     * provided size, both inclusive.
     * 
     * @param index the index of the element being checked
     * @param size the size of the boundary of the list (inclusive)
     * @throws IndexOutOfBoundsException if the index is out of range {@code (index < 0 ||
     * index > size()) }
     */
    private static void indexCheckIncludeSize(int index, int size) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException(index);
        }
    }
    
    /**
     * Throws an exception if the provided indicies do not fit within 0 and the size, inclusive, or
     * if {@code fromIndex > toIndex}.
     * 
     * @param fromIndex low endpoint (inclusive) of the range
     * @param toIndex high endpoint (exclusive) of the range
     * @param size the size of the boundary of the list (inclusive)
     * @throws IndexOutOfBoundsException if an endpoint index value is out of range
     *     {@code (fromIndex < 0 || toIndex > size)}
     * @throws IllegalArgumentException if the endpoint indices are out of order
     *     {@code (fromIndex > toIndex)}
     */
    private static void rangeCheckIncludeSize(int fromIndex, int toIndex, int size) {
        if (fromIndex > toIndex) {
            throw new IllegalArgumentException();
        }
        if (fromIndex < 0) {
            throw new IndexOutOfBoundsException(fromIndex);
        }
        if (toIndex > size) {
            throw new IndexOutOfBoundsException(toIndex);
        }
    }
}
