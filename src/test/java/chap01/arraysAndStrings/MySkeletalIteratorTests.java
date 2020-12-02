package chap01.arraysAndStrings;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.function.Consumer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class MySkeletalIteratorTests {
    static class EmptyIterator extends MySkeletalIterator<String> {
        public boolean hasNext() { return false; }
        public String next() { throw new NoSuchElementException(); }
    }
    
    static class ThreeElementIterator extends MySkeletalIterator<String> {
        int counter = 0;
        String[] elements = { "first", "second", "third" };
        public boolean hasNext() {
            return counter < elements.length;
        }
        public String next() {
            if (counter >= elements.length) {
                throw new NoSuchElementException();
            }
            state = State.MOVED_FORWARDS;
            return elements[counter++];
        }
    }
    
    static class Collector implements Consumer<String> {
        int count = 0;
        ArrayList<String> strings = new ArrayList<>();
        
        public void accept(String string) {
            count++;
            strings.add(string);
        }
        
    }
    
    EmptyIterator emptyIterator;
    ThreeElementIterator threeElementIterator;
    
    Collector collector;
    MySkeletalIterator.State INITIALIZED = MySkeletalIterator.State.INITIALIZED;
    MySkeletalIterator.State MOVED = MySkeletalIterator.State.MOVED_FORWARDS;
    
    @BeforeEach
    void setUp() {
        emptyIterator = new EmptyIterator();
        threeElementIterator = new ThreeElementIterator();
        collector = new Collector();
    }
    
    @Test
    void testMySkeletalIterator() {
        // Constructor is tested by the setUp succeeding in creating anonymous classes
        assertNotNull(emptyIterator);
        assertNotNull(threeElementIterator);
    }
    
    @Test
    void testRemove() {
        assertThrows(UnsupportedOperationException.class, emptyIterator::remove);
        assertThrows(UnsupportedOperationException.class, threeElementIterator::remove);
        threeElementIterator.next();
        assertThrows(UnsupportedOperationException.class, threeElementIterator::remove);
    }
    
    @Test
    void testForEachRemainin_NullConsumer() {
        assertThrows(NullPointerException.class, () -> emptyIterator.forEachRemaining(null));
        assertThrows(NullPointerException.class, () -> threeElementIterator.forEachRemaining(null));
    }
    
    @Test
    void testForEachRemaining_EmptyIterator() {
        emptyIterator.forEachRemaining(collector);
        assertTrue(collector.strings.isEmpty());
        assertEquals(0, collector.count);
    }
    
    @Test
    void testForEachRemaining_ThreeElementIterator_NoInitialNextCalls() {
        threeElementIterator.forEachRemaining(collector);
        assertEquals(3, collector.count);
        assertTrue(collector.strings.containsAll(Arrays.asList(threeElementIterator.elements)));
    }
    
    @Test
    void testForEachRemaining_ThreeElementIterator_TwoInitialNextCalls() {
        threeElementIterator.next();
        threeElementIterator.next();
        threeElementIterator.forEachRemaining(collector);
        assertEquals(1, collector.count);
        System.out.println(collector.strings);
        assertTrue(collector.strings.contains(threeElementIterator.elements[2]));
    }
    
    @Test
    void testForEachReamining_ThreeElementIterator_ThreeInitialNextCalls() {
        threeElementIterator.next();
        threeElementIterator.next();
        threeElementIterator.next();
        threeElementIterator.forEachRemaining(collector);
        assertEquals(0, collector.count);
        assertTrue(collector.strings.isEmpty());
    }
    
    @Test
    void testStateChecks() {
        assertEquals(INITIALIZED, emptyIterator.state);
        emptyIterator.hasNext();
        assertEquals(INITIALIZED, emptyIterator.state);
        
        assertEquals(INITIALIZED, threeElementIterator.state);
        threeElementIterator.hasNext();
        assertEquals(INITIALIZED, threeElementIterator.state);
        threeElementIterator.next();
        assertEquals(MOVED, threeElementIterator.state);
        threeElementIterator.hasNext();
        assertEquals(MOVED, threeElementIterator.state);
        threeElementIterator.forEachRemaining(collector);
        assertEquals(MOVED, threeElementIterator.state);
    }
    
    @Test
    void testStateChecksEdgeCaseForEach() {
        emptyIterator.forEachRemaining(collector);
        assertEquals(INITIALIZED, emptyIterator.state);
        
        threeElementIterator.forEachRemaining(collector);
        assertEquals(MOVED, threeElementIterator.state);
    }
    
    @Nested
    class StateTests {
        @Test
        void testCannotModify() {
            assertTrue(MySkeletalIterator.State.INITIALIZED.cannotModify());
            assertTrue(MySkeletalIterator.State.MODIFIED.cannotModify());
            assertFalse(MySkeletalIterator.State.MOVED_FORWARDS.cannotModify());
            assertFalse(MySkeletalIterator.State.MOVED_BACKWARDS.cannotModify());
        }
    }
    
}
