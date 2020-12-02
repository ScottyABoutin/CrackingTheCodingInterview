package chap01.arraysAndStrings;

import java.util.Iterator;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * This class provides a skeletal implementation of the {@code Iterator} interface, to minimize the
 * effort required to implement this interface. It also provides an underlying state to help manage
 * other calls.
 * <p>
 * To implement iterator, the programmer needs only to extend this class and provide implementations
 * for the {@code hasNext} and {@code next} methods.
 * <p>
 * Many implementations will also want to override this class's {@code remove} method (which
 * otherwise throws an {@code UnsupportedOperationException}).
 * <p>
 * The documentation for each non-abstract method in this class describes its implementation in
 * detail.
 *
 * @param <E> the type of elements in this collection
 */
public abstract class MySkeletalIterator<E> implements Iterator<E> {
    
    /**
     * Sole constructor. (For invocation by subclass constructors, typically implicit.)
     */
    protected MySkeletalIterator() {
    }
    
    /**
     * Removes from the underlying collection the last element returned by this iterator (optional
     * operation). This method can be called only once per call to {@code next()}.
     * <p>
     * The behavior of an iterator is unspecified if the underlying collection is modified while the
     * iteration is in progress in any way other than by calling this method, unless an overriding
     * class has specified a concurrent modification policy.
     * <p>
     * The behavior of an iterator is unspecified if this method is called after a call to the
     * {@link #forEachRemaining(Consumer)} method.
     * 
     * @implSpec This implementation throws an instance of {@code UnsupportedOperationException} and
     * performs no other action.
     * @throws UnsupportedOperationException if the {@code remove} operation is not supported by
     *     this iterator
     * @throws IllegalStateException if the {@code next} method has not yet been called, or the
     *     {@code remove} method has already been called after the last call to the {@code next}
     *     method
     */
    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
    
    /**
     * Performs the given action for each remaining element until all elements have been processed
     * or the action throws an exception. Actions are performed in the order of iteration, if that
     * order is specified. Exceptions thrown by the action are relayed to the caller.
     * <p>
     * The behavior of an iterator is unspecified if the action modifies the collection in any way
     * (even by calling the remove method or other mutator methods of {@code Iterator} subtypes),
     * unless an overriding class has specified a concurrent modification policy.
     * <p>
     * Subsequent behavior of an iterator is unspecified if the action throws an exception.
     * 
     * @implSpec The default implementation behaves as if:
     * 
     * <pre>
     * {@code 
     *     while (hasNext())
     *         action.accept(next());
     * }
     * </pre>
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
     * The current {@code State} of this iterator. This can be used by subclasses, where it can
     * updated and used to check whether the underlying "iterated" object is modifiable.
     */
    protected State state = State.INITIALIZED;
    
    /**
     * This represents the states that an {@code Iterator} or a {@code ListIterator} can be in.
     */
    public enum State {
        INITIALIZED, MODIFIED, MOVED_FORWARDS, MOVED_BACKWARDS;
        
        /**
         * Returns whether or not an iterator can be modified in the current state.
         * 
         * @return {@code true} if the iterator is in a state where it can be modified
         */
        public boolean cannotModify() {
            return this == INITIALIZED || this == MODIFIED;
        }
    }
}
