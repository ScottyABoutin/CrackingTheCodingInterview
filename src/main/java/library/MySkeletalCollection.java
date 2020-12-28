package library;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.StringJoiner;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * This class provides a skeletal implementation of the {@code Collection} interface, to minimize
 * the effort required to implement this interface.
 * <p>
 * To implement an unmodifiable collection, the programmer needs only to extend this class and
 * provide implementations for the {@code iterator} and {@code size} methods.
 * <p>
 * To implement a modifiable collection, the programmer must additionally override this class's
 * {@code add} method (which otherwise throws an {@code UnsupportedOperationException}), and the
 * iterator returned by the {@code iterator} method must additionally implement its {@code remove}
 * method (which otherwise throws an {@code UnsupportedOperationException}).
 * <p>
 * The programmer should generally provide a no argument and {@code Collection} constructor, as per
 * the recommendation in the {@code Collection} interface specification.
 * <p>
 * The documentation for each non-abstract method in this class describes its implementation in
 * detail. Each of these methods may be overridden if the collection being implemented admits a more
 * efficient implementation.
 *
 * @param <E> the type of elements in this collection
 */
public abstract class MySkeletalCollection<E> implements Collection<E> {
    
    /**
     * Sole constructor. (For invocation by subclass constructors, typically implicit.)
     */
    protected MySkeletalCollection() {
    }
    
    /**
     * Returns an iterator over the elements contained in this collection.
     * 
     * @return an iterator over the elements contained in this collection
     */
    @Override
    public abstract Iterator<E> iterator();
    
    /**
     * Returns the number of elements in this collection. If this collection contains more than
     * {@code Integer.MAX_VALUE} elements, returns {@code Integer.MAX_VALUE}.
     *
     * @return the number of elements in this collection
     */
    @Override
    public abstract int size();
    
    /**
     * Returns {@code true} if this collection contains no elements.
     * 
     * @implSpec This implementation returns {@code size() == 0}.
     * @return {@code true} if this collection contains no elements
     */
    @Override
    public boolean isEmpty() {
        return this.size() == 0;
    }
    
    /**
     * Returns {@code true} if this collection contains the specified element. More formally,
     * returns {@code true} if and only if this collection contains at least one element {@code e}
     * such that {@code Objects.equals(o, e)}.
     * 
     * @implSpec This implementation iterates over the elements in the collection, checking each
     * element in turn for equality with the specified element.
     * @param o element whose presence in this collection is to be tested
     * @return {@code true} if this collection contains the specified element
     * @throws ClassCastException if the type of the specified element is incompatible with this
     *     collection (optional)
     * @throws NullPointerException if the specified element is {@code null} and this collection
     *     does not permit null elements (optional)
     */
    @Override
    public boolean contains(Object o) {
        for (E element : this) {
            if (Objects.equals(o, element)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Returns an array containing all of the elements in this collection. If this collection makes
     * any guarantees as to what order its elements are returned by its iterator, this method must
     * return the elements in the same order.
     * <p>
     * The returned array's runtime component type is {@code Object}. The returned array will be
     * "safe" in that no references to it are maintained by this collection. (In other words, this
     * method must allocate a new array even if this collection is backed by an array). The caller
     * is thus free to modify the returned array.
     * 
     * @implSpec This implementation returns an array containing all the elements returned by this
     * collection's iterator, in the same order, stored in consecutive elements of the array,
     * starting with index {@code 0}. The length of the returned array is equal to the number of
     * elements returned by the iterator, even if the size of this collection changes during
     * iteration, as might happen if the collection permits concurrent modification during
     * iteration. The {@code size} method is called only as an optimization hint; the correct result
     * is returned even if the iterator returns a different number of elements.
     * <p>
     * This method is equivalent to:
     * 
     * <pre>
     * {@code
     * List<E> list = new ArrayList<E>(size());
     * for (E e : this)
     *     list.add(e);
     * return list.toArray();
     * }
     * </pre>
     * 
     * @return an array, whose runtime component type is {@code Object}, containing all of the
     * elements in this collection
     */
    @Override
    public Object[] toArray() {
        List<E> list = new ArrayList<>(this.size());
        for (E element : this) {
            list.add(element);
        }
        return list.toArray();
    }
    
    /**
     * Returns an array containing all of the elements in this collection; the runtime type of the
     * returned array is that of the specified array.
     * <p>
     * If the collection fits in the specified array, it is returned therein. Otherwise, a new array
     * is allocated with the runtime type of the specified array and the size of this collection. If
     * this collection fits in the specified array with room to spare (i.e., the array has more
     * elements than this collection), the element in the array immediately following the end of the
     * collection is set to {@code null}. (This is useful in determining the length of this
     * collection <i>only</i> if the caller knows that this collection does not contain any
     * {@code null} elements.)
     * <p>
     * If this collection makes any guarantees as to what order its elements are returned by its
     * iterator, this method must return the elements in the same order.
     * 
     * @implSpec This implementation returns an array containing all the elements returned by this
     * collection's iterator in the same order, stored in consecutive elements of the array,
     * starting with index 0. If the number of elements returned by the iterator is too large to fit
     * into the specified array, then the elements are returned in a newly allocated array with
     * length equal to the number of elements returned by the iterator, even if the size of this
     * collection changes during iteration, as might happen if the collection permits concurrent
     * modification during iteration. The {@code size} method is called only as an optimization
     * hint; the correct result is returned even if the iterator returns a different number of
     * elements.
     * <p>
     * This method is equivalent to:
     * 
     * <pre>
     * {@code List<E> list = new ArrayList<E>(size());
     * for (E e : this)
     *     list.add(e);
     * return list.toArray(a);
     * }
     * </pre>
     * 
     * @param <T> the component type of the array to contain the collection
     * @param a the array into which the elements of this collection are to be stored, if it is big
     *     enough; otherwise, a new array of the same runtime type is allocated for this purpose
     * @return an array containing all of the elements in this collection
     * @throws ArrayStoreException if the runtime type of any element in this collection is not
     *     assignable to the runtime component type of the specified array
     * @throws NullPointerException if the specified array is null
     */
    @Override
    public <T> T[] toArray(T[] a) {
        List<E> list = new ArrayList<>(this.size());
        for (E element : this) {
            list.add(element);
        }
        return list.toArray(a);
    }
    
    /**
     * Ensures that this collection contains the specified element (optional operation). Returns
     * {@code true} if this collection changed as a result of the call. (Returns {@code false} if
     * this collection does not permit duplicates and already contains the specified element.)
     * <p>
     * Collections that support this operation may place limitations on what elements may be added
     * to this collection. In particular, some collections will refuse to add {@code null} elements,
     * and others will impose restrictions on the type of elements that may be added. Collection
     * classes should clearly specify in their documentation any restrictions on what elements may
     * be added.
     * <p>
     * If a collection refuses to add a particular element for any reason other than that it already
     * contains the element, it <i>must</i> throw an exception (rather than returning
     * {@code false}). This preserves the invariant that a collection always contains the specified
     * element after this call returns.
     * 
     * @implSpec This implementation always throws an {@code UnsupportedOperationException}.
     * @param e element whose presence in this collection is to be ensured
     * @return {@code true} if this collection changed as a result of the call
     * @throws UnsupportedOperationException if the add operation is not supported by this
     *     collection
     * @throws ClassCastException if the class of the specified element prevents it from being added
     *     to this collection
     * @throws NullPointerException if the specified element is null and this collection does not
     *     permit null elements
     * @throws IllegalArgumentException if some property of the element prevents it from being added
     *     to this collection
     * @throws IllegalStateException if the element cannot be added at this time due to insertion
     *     restrictions
     */
    @Override
    public boolean add(E e) {
        throw new UnsupportedOperationException();
    }
    
    /**
     * Removes a single instance of the specified element from this collection, if it is present
     * (optional operation). More formally, removes an element {@code e} such that
     * {@code Objects.equals(o, e)}, if this collection contains one or more such elements. Returns
     * {@code true} if this collection contained the specified element (or equivalently, if this
     * collection changed as a result of the call).
     * 
     * @implSpec This implementation iterates over the collection looking for the specified element.
     * If it finds the element, it removes the element from the collection using the iterator's
     * remove method.
     * <p>
     * Note that this implementation throws an {@code UnsupportedOperationException} if the iterator
     * returned by this collection's iterator method does not implement the remove method and this
     * collection contains the specified object.
     * @param o element to be removed from this collection, if present
     * @return {@code true} if an element was removed as a result of this call
     * @throws UnsupportedOperationException if the {@code remove} operation is not supported by
     *     this collection
     * @throws ClassCastException if the type of the specified element is incompatible with this
     *     collection (optional)
     * @throws NullPointerException if the specified element is null and this collection does not
     *     permit null elements (optional)
     */
    @Override
    public boolean remove(Object o) {
        for (Iterator<E> iterator = this.iterator(); iterator.hasNext();) {
            E e = iterator.next();
            if (Objects.equals(o, e)) {
                iterator.remove();
                return true;
            }
        }
        return false;
    }
    
    /**
     * Returns {@code true} if this collection contains all of the elements in the specified
     * collection.
     * 
     * @implSpec This implementation iterates over the specified collection, checking each element
     * returned by the iterator in turn to see if it's contained in this collection. If all elements
     * are so contained {@code true} is returned, otherwise {@code false}.
     * @param c collection to be checked for containment in this collection
     * @return {@code true} if this collection contains all of the elements in the specified
     * collection
     * @throws ClassCastException if the types of one or more elements in the specified collection
     *     are incompatible with this collection (optional)
     * @throws NullPointerException if the specified collection contains one or more null elements
     *     and this collection does not permit null elements (optional), or if the specified
     *     collection is null.
     * @see #contains(Object)
     */
    @Override
    public boolean containsAll(Collection<?> c) {
        for (Object o : c) { // throws NullPointerException
            if (!this.contains(o)) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Adds all of the elements in the specified collection to this collection (optional operation).
     * The behavior of this operation is undefined if the specified collection is modified while the
     * operation is in progress. (This implies that the behavior of this call is undefined if the
     * specified collection is this collection, and this collection is nonempty.)
     * 
     * @implSpec This implementation iterates over the specified collection, and adds each object
     * returned by the iterator to this collection, in turn.
     * <p>
     * Note that this implementation will throw an {@code UnsupportedOperationException} unless
     * {@code add} is overridden (assuming the specified collection is non-empty).
     * @param c collection containing elements to be added to this collection
     * @return {@code true} if this collection changed as a result of the call
     * @throws UnsupportedOperationException if the addAll operation is not supported by this
     *     collection
     * @throws ClassCastException if the class of an element of the specified collection prevents it
     *     from being added to this collection
     * @throws NullPointerException if the specified collection contains a null element and this
     *     collection does not permit null elements, or if the specified collection is null
     * @throws IllegalArgumentException if some property of an element of the specified collection
     *     prevents it from being added to this collection
     * @throws IllegalStateException if not all the elements can be added at this time due to
     *     insertion restrictions
     * @see #add(Object)
     */
    @Override
    public boolean addAll(Collection<? extends E> c) {
        if (c.isEmpty()) {
            return false;
        }
        for (E e : c) {
            this.add(e);
        }
        return true;
    }
    
    /**
     * Removes all of this collection's elements that are also contained in the specified collection
     * (optional operation). After this call returns, this collection will contain no elements in
     * common with the specified collection.
     * 
     * @implSpec This implementation iterates over this collection, checking each element returned
     * by the iterator in turn to see if it's contained in the specified collection. If it's so
     * contained, it's removed from this collection with the iterator's {@code remove} method.
     * <p>
     * Note that this implementation will throw an {@code UnsupportedOperationException} if the
     * iterator returned by the {@code iterator} method does not implement the remove method and
     * this collection contains one or more elements in common with the specified collection.
     * @param c collection containing elements to be removed from this collection
     * @return {@code true} if this collection changed as a result of the call
     * @throws UnsupportedOperationException if the {@code removeAll} method is not supported by
     *     this collection
     * @throws ClassCastException if the types of one or more elements in this collection are
     *     incompatible with the specified collection (optional)
     * @throws NullPointerException if this collection contains one or more null elements and the
     *     specified collection does not support null elements (optional), or if the specified
     *     collection is null
     * @see #remove(Object)
     * @see #contains(Object)
     */
    @Override
    public boolean removeAll(Collection<?> c) {
        Objects.requireNonNull(c);
        boolean changed = false;
        for (Iterator<E> iterator = this.iterator(); iterator.hasNext();) {
            E e = iterator.next();
            if (c.contains(e)) {
                iterator.remove();
                changed = true;
            }
        }
        return changed;
    }
    
    /**
     * Retains only the elements in this collection that are contained in the specified collection
     * (optional operation). In other words, removes from this collection all of its elements that
     * are not contained in the specified collection.
     * 
     * @implSpec This implementation iterates over this collection, checking each element returned
     * by the iterator in turn to see if it's contained in the specified collection. If it's not so
     * contained, it's removed from this collection with the iterator's {@code remove} method.
     * <p>
     * Note that this implementation will throw an {@code UnsupportedOperationException} if the
     * iterator returned by the {@code iterator} method does not implement the {@code remove} method
     * and this collection contains one or more elements not present in the specified collection.
     * @param c collection containing elements to be retained in this collection
     * @return {@code true} if this collection changed as a result of the call
     * @throws UnsupportedOperationException if the {@code retainAll} operation is not supported by
     *     this collection
     * @throws ClassCastException if the types of one or more elements in this collection are
     *     incompatible with the specified collection (optional)
     * @throws NullPointerException - if this collection contains one or more null elements and the
     *     specified collection does not permit null elements (optional), or if the specified
     *     collection is null
     * @see #remove(Object)
     * @see #contains(Object)
     */
    @Override
    public boolean retainAll(Collection<?> c) {
        Objects.requireNonNull(c);
        boolean changed = false;
        for (Iterator<E> iterator = this.iterator(); iterator.hasNext();) {
            E e = iterator.next();
            if (!c.contains(e)) {
                iterator.remove();
                changed = true;
            }
        }
        return changed;
    }
    
    /**
     * Removes all of the elements from this collection (optional operation). The collection will be
     * empty after this method returns.
     * 
     * @implSpec This implementation iterates over this collection, removing each element using the
     * {@code Iterator.remove} operation. Most implementations will probably choose to override this
     * method for efficiency.
     * <p>
     * Note that this implementation will throw an {@code UnsupportedOperationException} if the
     * iterator returned by this collection's {@code iterator} method does not implement the
     * {@code remove} method and this collection is non-empty.
     * @throws UnsupportedOperationException if the {@code clear} operation is not supported by this
     *     collection
     */
    @Override
    public void clear() {
        Iterator<E> iterator = this.iterator();
        while (iterator.hasNext()) {
            iterator.next();
            iterator.remove();
        }
    }
    
    /**
     * Returns a string representation of this collection. The string representation consists of a
     * list of the collection's elements in the order they are returned by its iterator, enclosed in
     * square brackets ({@code "[]"}). Adjacent elements are separated by the characters
     * {@code ", "} (comma and space). Elements are converted to strings as by
     * {@link String#valueOf(Object)}.
     * 
     * @return a string representation of this collection
     */
    @Override
    public String toString() {
        StringJoiner joiner = new StringJoiner(", ", "[", "]");
        this.forEach((Object o) -> joiner.add(String.valueOf(o)));
        return joiner.toString();
    }
    
    // Default methods implemented here for visibility
    
    /**
     * Performs the given action for each element of the {@code Iterable} until all elements have
     * been processed or the action throws an exception. Actions are performed in the order of
     * iteration, if that order is specified. Exceptions thrown by the action are relayed to the
     * caller.
     * <p>
     * The behavior of this method is unspecified if the action performs side-effects that modify
     * the underlying source of elements, unless an overriding class has specified a concurrent
     * modification policy.
     * 
     * @implSpec This behaves as if:
     * 
     * <pre>
     * {@code 
     *     for (T t : this)
     *         action.accept(t);
     * }
     * </pre>
     * 
     * @param action The action to be performed for each element
     * @throws NullPointerException if the specified action is null
     */
    @Override
    public void forEach(Consumer<? super E> action) {
        Objects.requireNonNull(action);
        for (E e : this) {
            action.accept(e);
        }
    }
    
    /**
     * Returns an array containing all of the elements in this collection, using the provided
     * {@code generator} function to allocate the returned array.
     * <p>
     * If this collection makes any guarantees as to what order its elements are returned by its
     * iterator, this method must return the elements in the same order.
     * 
     * @apiNote This method acts as a bridge between array-based and collection-based APIs. It
     * allows creation of an array of a particular runtime type. Use {@link #toArray()} to create an
     * array whose runtime type is {@code Object[]}, or use {@link #toArray(Object[])} to reuse an
     * existing array.
     * <p>
     * Suppose {@code x} is a collection known to contain only strings. The following code can be
     * used to dump the collection into a newly allocated array of {@code String}:
     * 
     * <pre>
     * {@code String[] y = x.toArray(String[]::new);}
     * </pre>
     * 
     * @implSpec This implementation calls the generator function with zero and then passes the
     * resulting array to #toArray(Object[]).
     * @param T the component type of the array to contain the collection
     * @param generator a function which produces a new array of the desired type and the provided
     *     length Returns: an array containing all of the elements in this collection
     * @throws ArrayStoreException if the runtime type of any element in this collection is not
     *     assignable to the runtime component type of the generated array
     * @throws NullPointerException if the generator function is null
     */
    @Override
    public <T> T[] toArray(IntFunction<T[]> generator) {
        return this.toArray(generator.apply(0));
    }
    
    /**
     * Removes all of the elements of this collection that satisfy the given predicate. Errors or
     * runtime exceptions thrown during iteration or by the predicate are relayed to the caller.
     * 
     * @implSpec The default implementation traverses all elements of the collection using its
     * {@link #iterator()}. Each matching element is removed using {@link Iterator#remove()}. If the
     * collection's iterator does not support removal then an {@code UnsupportedOperationException}
     * will be thrown on the first matching element.
     * @param filter a predicate which returns {@code true} for elements to be removed
     * @return {@code true} if any elements were removed
     * @throws NullPointerException if the specified filter is null
     * @throws UnsupportedOperationException if elements cannot be removed from this collection.
     *     Implementations may throw this exception if a matching element cannot be removed or if,
     *     in general, removal is not supported.
     */
    @Override
    public boolean removeIf(Predicate<? super E> filter) {
        Objects.requireNonNull(filter);
        
        boolean changed = false;
        for (Iterator<E> iterator = this.iterator(); iterator.hasNext();) {
            E e = iterator.next();
            if (filter.test(e)) {
                iterator.remove();
                changed = true;
            }
        }
        return changed;
    }
    
    /**
     * Creates a {@link Spliterator} over the elements in this collection. Implementations should
     * document characteristic values reported by the spliterator. Such characteristic values are
     * not required to be reported if the spliterator reports {@link Spliterator#SIZED} and this
     * collection contains no elements.
     * <p>
     * The default implementation should be overridden by subclasses that can return a more
     * efficient spliterator. In order to preserve expected laziness behavior for the
     * {@link #stream()} and {@link #parallelStream()} methods, spliterators should either have the
     * characteristic of IMMUTABLE or CONCURRENT, or be <em>late-binding</em>. If none of these is
     * practical, the overriding class should describe the spliterator's documented policy of
     * binding and structural interference, and should override the {@link #stream()} and
     * {@link #parallelStream()} methods to create streams using a {@code Supplier} of the
     * spliterator, as in:
     * 
     * <pre>
     * {@code
     *     Stream<E> s = StreamSupport.stream(() -> spliterator(), spliteratorCharacteristics)
     * }
     * </pre>
     * 
     * These requirements ensure that streams produced by the {@link #stream()} and
     * {@link #parallelStream()} methods will reflect the contents of the collection as of
     * initiation of the terminal stream operation.
     * 
     * @implSpec The default implementation creates a <em>late-binding</em> spliterator from the
     * collection's {@code Iterator}. The spliterator inherits the <em>fail-fast</em> properties of
     * the collection's iterator.
     * <p>
     * The created {@code Spliterator} reports {@link java.util.Spliterator#SIZED}.
     * @implNote The created {@code Spliterator} additionally reports
     * {@link java.util.Spliterator#SUBSIZED}.
     * <p>
     * If a spliterator covers no elements then the reporting of additional characteristic values,
     * beyond that of SIZED and SUBSIZED, does not aid clients to control, specialize or simplify
     * computation. However, this does enable shared use of an immutable and empty spliterator
     * instance (see {@link java.util.Spliterators#emptySpliterator()}) for empty collections, and
     * enables clients to determine if such a spliterator covers no elements.
     * @return a {@code Spliterator} over the elements in this collection
     */
    @Override
    public Spliterator<E> spliterator() {
        return Spliterators.spliterator(this, 0); // This factory method supplies SIZED and SUBSIZED
    }
    
    /**
     * Returns a sequential {@code Stream} with this collection as its source.
     * <p>
     * This method should be overridden when the {@link #spliterator()} method cannot return a
     * spliterator that is IMMUTABLE, CONCURRENT, or <em>late-binding</em>. (See
     * {@link #spliterator()} for details.)
     * 
     * @implSpec The default implementation creates a sequential {@code Stream} from the
     * collection's {@code Spliterator}.
     * @return a sequential Stream over the elements in this collection
     */
    @Override
    public Stream<E> stream() {
        return StreamSupport.stream(this.spliterator(), false);
    }
    
    /**
     * Returns a possibly parallel {@code Stream} with this collection as its source. It is
     * allowable for this method to return a sequential stream.
     * <p>
     * This method should be overridden when the {@link #spliterator()} method cannot return a
     * spliterator that is IMMUTABLE, CONCURRENT, or <em>late-binding</em>. (See
     * {@link #spliterator()} for details.)
     * 
     * @implSpec The default implementation creates a parallel {@code Stream} from the collection's
     * {@code Spliterator}.
     * @return a possibly parallel Stream over the elements in this collection
     */
    @Override
    public Stream<E> parallelStream() {
        return StreamSupport.stream(this.spliterator(), true);
    }
    
}
