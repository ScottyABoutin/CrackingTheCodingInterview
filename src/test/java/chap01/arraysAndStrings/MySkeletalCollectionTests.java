package chap01.arraysAndStrings;

import static org.junit.jupiter.api.Assertions.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class MySkeletalCollectionTests {
    
    static class BasicImmutableCollection extends MySkeletalCollection<String> {
        private String[] strings;
        
        BasicImmutableCollection(String... strings) {
            this.strings = Arrays.copyOf(strings, strings.length);
        }
        
        @Override
        public Iterator<String> iterator() {
            return new Iterator<String>() {
                int index = 0;
                
                @Override
                public boolean hasNext() {
                    return index < strings.length;
                }
                
                @Override
                public String next() {
                    if (!hasNext()) {
                        throw new NoSuchElementException();
                    }
                    return strings[index++];
                }
                
            };
        }
        
        @Override
        public int size() {
            return strings.length;
        }
        
    }
    
    @Nested
    class BasicImmutableCollectionTests {
        
        BasicImmutableCollection emptyImmutableCollection;
        BasicImmutableCollection singleElementImmutableCollection;
        BasicImmutableCollection twoElementImmutableCollection;
        BasicImmutableCollection tenElementImmutableCollection;
        
        BasicImmutableCollection singleNullImmutableCollection;
        BasicImmutableCollection manyNullsImmutableCollection;
        
        @BeforeEach
        void setUp() {
            emptyImmutableCollection = new BasicImmutableCollection();
            singleElementImmutableCollection = new BasicImmutableCollection("");
            twoElementImmutableCollection = new BasicImmutableCollection("first", "second");
            tenElementImmutableCollection = new BasicImmutableCollection("Aaa", "Bbb", null, "Ddd", "Eee", null, "Ggg",
                    "Hhh", null, "Jjj");
            
            singleNullImmutableCollection = new BasicImmutableCollection((String)null);
            manyNullsImmutableCollection = new BasicImmutableCollection(null, null, null, null, null);
        }
        
        @Test
        void testNoArgConstructor() {
            // Constructor is tested by the setUp succeeding in creating classes
            assertNotNull(emptyImmutableCollection);
            assertNotNull(singleElementImmutableCollection);
            assertNotNull(twoElementImmutableCollection);
            assertNotNull(tenElementImmutableCollection);
            
            assertNotNull(singleNullImmutableCollection);
            assertNotNull(manyNullsImmutableCollection);
        }
        
        @Nested
        class IteratorTests {
            Iterator<String> emptyIterator;
            Iterator<String> tenElementIterator;
            ForEachCollector collector;
            
            @BeforeEach
            void setUp() {
                emptyIterator = emptyImmutableCollection.iterator();
                tenElementIterator = tenElementImmutableCollection.iterator();
                collector = new ForEachCollector();
            }
            
            @Test
            void emptyIteratorMethods() {
                assertFalse(emptyIterator.hasNext());
                assertThrows(NoSuchElementException.class, emptyIterator::next);
                assertThrows(UnsupportedOperationException.class, emptyIterator::remove);
                emptyIterator.forEachRemaining(collector);
                assertEquals(0, collector.count);
                assertTrue(collector.strings.isEmpty());
                assertThrows(NullPointerException.class, () -> emptyIterator.forEachRemaining(null));
                assertFalse(emptyIterator.hasNext());
            }
            
            @Test
            void tenElementIteratorTests() {
                for (int i = 0; i < 3; i++) {
                    for (int j = 0; j < 2; j++) {
                        assertTrue(tenElementIterator.hasNext());
                        assertNotNull(tenElementIterator.next());
                        assertThrows(UnsupportedOperationException.class, tenElementIterator::remove);
                    }
                    assertTrue(tenElementIterator.hasNext());
                    assertNull(tenElementIterator.next());
                    assertThrows(UnsupportedOperationException.class, tenElementIterator::remove);
                }
                assertTrue(tenElementIterator.hasNext());
                tenElementIterator.forEachRemaining(collector);
                assertEquals(1, collector.count);
                assertTrue(collector.strings.contains("Jjj"));
                assertFalse(tenElementIterator.hasNext());
                assertThrows(NoSuchElementException.class, tenElementIterator::next);
                
                Iterator<String> anotherTenElementIterator = tenElementImmutableCollection.iterator();
                assertNotSame(tenElementIterator, anotherTenElementIterator);
                assertTrue(anotherTenElementIterator.hasNext());
                anotherTenElementIterator.forEachRemaining(collector);
                assertEquals(11, collector.count);
                assertTrue(collector.strings.containsAll(Arrays.asList(null, "Aaa", "Jjj", "Bbb", "Eee")));
                assertFalse(anotherTenElementIterator.hasNext());
                assertThrows(NoSuchElementException.class, anotherTenElementIterator::next);
            }
        }
        
        @Test
        void testSize() {
            assertEquals(0, emptyImmutableCollection.size());
            assertEquals(10, tenElementImmutableCollection.size());
        }
        
        // Testing size() == 0
        @Test
        void testIsEmpty() {
            assertTrue(emptyImmutableCollection.isEmpty());
            assertFalse(tenElementImmutableCollection.isEmpty());
        }
        
        @Test
        void testContains() {
            String[] testStrings = { "Hello", "Aaa", "AAA", null, "true", "Jjj" };
            for (String string : testStrings) {
                assertFalse(emptyImmutableCollection.contains(string));
                if (string == null || string.equals("Aaa") || string.equals("Jjj")) {
                    assertTrue(tenElementImmutableCollection.contains(string));
                } else {
                    assertFalse(tenElementImmutableCollection.contains(string));
                }
            }
        }
        
        @Test
        void testToArray() {
            Object[] expectedEmptyArray = new Object[0];
            Object[] expectedTenElementArray = { "Aaa", "Bbb", null, "Ddd", "Eee", null, "Ggg", "Hhh", null, "Jjj" };
            
            Object[] emptyArray = emptyImmutableCollection.toArray();
            assertArrayEquals(expectedEmptyArray, emptyArray);
            
            Object[] tenElementArray = tenElementImmutableCollection.toArray();
            assertArrayEquals(expectedTenElementArray, tenElementArray);
            
            // Verify that the returned array can change and the collection still contains its
            // original elements
            Object[] tenElementArrayCopy = Arrays.copyOf(tenElementArray, 10);
            Arrays.setAll(tenElementArray, $ -> null);
            assertEquals(7, Arrays.stream(tenElementArrayCopy).filter(Objects::nonNull).count());
            assertTrue(tenElementImmutableCollection.containsAll(Arrays.asList(tenElementArrayCopy)));
        }
        
        @Test
        void testToArrayTArray() {
            String[] expectedEmptyArray = new String[0];
            CharSequence[] expectedEmptyArrayCharSequence = new CharSequence[0];
            Serializable[] expectedEmptyArraySerializable = new Serializable[0];
            
            // Using new objects each time to avoid an == comparison succeeding
            String[] expectedTenElementArray = { "Aaa", "Bbb", null, "Ddd", "Eee", null, "Ggg", "Hhh", null, "Jjj" };
            CharSequence[] tenElementArrayAsCharSequence = { "Aaa", "Bbb", null, "Ddd", "Eee", null, "Ggg", "Hhh", null,
                    "Jjj" };
            Serializable[] tenElementArrayAsSerializable = { "Aaa", "Bbb", null, "Ddd", "Eee", null, "Ggg", "Hhh", null,
                    "Jjj" };
            
            String[] emptyArray = emptyImmutableCollection.toArray(new String[0]);
            assertArrayEquals(expectedEmptyArray, emptyArray);
            emptyArray = emptyImmutableCollection.toArray(expectedEmptyArray);
            assertSame(expectedEmptyArray, emptyArray);
            
            CharSequence[] emptyArrayCharSequence = emptyImmutableCollection.toArray(new CharSequence[0]);
            assertArrayEquals(expectedEmptyArrayCharSequence, emptyArrayCharSequence);
            emptyArrayCharSequence = emptyImmutableCollection.toArray(expectedEmptyArrayCharSequence);
            assertSame(expectedEmptyArrayCharSequence, emptyArrayCharSequence);
            
            Serializable[] emptyArraySerializable = emptyImmutableCollection.toArray(new Serializable[0]);
            assertArrayEquals(expectedEmptyArraySerializable, emptyArraySerializable);
            emptyArraySerializable = emptyImmutableCollection.toArray(expectedEmptyArraySerializable);
            assertSame(expectedEmptyArraySerializable, emptyArraySerializable);
            
            assertThrows(NullPointerException.class, () -> emptyImmutableCollection.toArray((String[]) null));
            /*
             * This might look like it should throw an ArrayStoreException: however, the text of the
             * JavaDoc for throwing that expected reads: "if the runtime type of any element in this
             * collection is not assignable to the runtime component type of the specified array".
             * Since there are no elements in this array, there is no issue with this "conversion".
             * This is shown to be especially true in a "nulls only" array, which is assignable to
             * any array type.
             * 
             */
            assertDoesNotThrow(() -> emptyImmutableCollection.toArray(new Integer[0]));
            
            // tenElementTests
            fail("do tenElementTests");
            
            BasicImmutableCollection immutableNullsOnlyCollection = new BasicImmutableCollection(null, null, null, null,
                    null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
            assertDoesNotThrow(() -> immutableNullsOnlyCollection.toArray(new String[0]));
            assertDoesNotThrow(() -> immutableNullsOnlyCollection.toArray(new Integer[0]));
            assertDoesNotThrow(() -> immutableNullsOnlyCollection.toArray(new BigDecimal[0]));
            assertDoesNotThrow(() -> immutableNullsOnlyCollection.toArray(new Optional<?>[0]));
        }
        
        @Test
        void testAdd() {
            fail("Not yet implemented");
        }
        
        @Test
        void testRemove() {
            fail("Not yet implemented");
        }
        
        @Test
        void testContainsAll() {
            fail("Not yet implemented");
        }
        
        @Test
        void testAddAll() {
            fail("Not yet implemented");
        }
        
        @Test
        void testRemoveAll() {
            fail("Not yet implemented");
        }
        
        @Test
        void testRetainAll() {
            fail("Not yet implemented");
        }
        
        @Test
        void testClear() {
            fail("Not yet implemented");
        }
        
        @Test
        void testToString() {
            fail("Not yet implemented");
        }
        
        @Test
        void testForEach() {
            fail("Not yet implemented");
        }
        
        @Test
        void testToArrayIntFunctionOfT() {
            fail("Not yet implemented");
        }
        
        @Test
        void testRemoveIf() {
            fail("Not yet implemented");
        }
        
        @Test
        void testSpliterator() {
            fail("Not yet implemented");
        }
        
        @Test
        void testStream() {
            fail("Not yet implemented");
        }
        
        @Test
        void testParallelStream() {
            fail("Not yet implemented");
        }
    }
    
    static class BasicMutableCollection {
        
    }
    
    @Nested
    class BasicMutableCollectionTests {
        
        @Test
        void testNoArgConstructor() {
            // Constructor is tested by the setUp succeeding in creating classes
//            assertNotNull(emptyMutableCollection);
//            assertNotNull(mutableCollectionWith10Elements);
            fail("Not yet implemented");
        }
        
        @Test
        void testMySkeletalCollection() {
            fail("Not yet implemented");
        }
        
        @Test
        void testIterator() {
            fail("Not yet implemented");
        }
        
        @Test
        void testSize() {
            fail("Not yet implemented");
        }
        
        @Test
        void testIsEmpty() {
            fail("Not yet implemented");
        }
        
        @Test
        void testContains() {
            fail("Not yet implemented");
        }
        
        @Test
        void testToArray() {
            fail("Not yet implemented");
        }
        
        @Test
        void testToArrayTArray() {
            fail("Not yet implemented");
        }
        
        @Test
        void testAdd() {
            fail("Not yet implemented");
        }
        
        @Test
        void testRemove() {
            fail("Not yet implemented");
        }
        
        @Test
        void testContainsAll() {
            fail("Not yet implemented");
        }
        
        @Test
        void testAddAll() {
            fail("Not yet implemented");
        }
        
        @Test
        void testRemoveAll() {
            fail("Not yet implemented");
        }
        
        @Test
        void testRetainAll() {
            fail("Not yet implemented");
        }
        
        @Test
        void testClear() {
            fail("Not yet implemented");
        }
        
        @Test
        void testToString() {
            fail("Not yet implemented");
        }
        
        @Test
        void testForEach() {
            fail("Not yet implemented");
        }
        
        @Test
        void testToArrayIntFunctionOfT() {
            fail("Not yet implemented");
        }
        
        @Test
        void testRemoveIf() {
            fail("Not yet implemented");
        }
        
        @Test
        void testSpliterator() {
            fail("Not yet implemented");
        }
        
        @Test
        void testStream() {
            fail("Not yet implemented");
        }
        
        @Test
        void testParallelStream() {
            fail("Not yet implemented");
        }
    }
    
}
