package chap01.arraysAndStrings;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Spliterator;
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
public class MyArrayList<E> implements List<E> {
    private E[] elements;
    
    public MyArrayList() {
        
    }
    
    public MyArrayList(int initialCapacity) {
        
    }
    
    public MyArrayList(Collection<? extends E> c) {
        
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
    public void forEach(Consumer<? super E> action) {
        // TODO Auto-generated method stub
        List.super.forEach(action);
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
    public <T> T[] toArray(IntFunction<T[]> generator) {
        // TODO Auto-generated method stub
        return List.super.toArray(generator);
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
    public boolean removeIf(Predicate<? super E> filter) {
        // TODO Auto-generated method stub
        return List.super.removeIf(filter);
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

// TODO ArrayList methods? Vector methods?
