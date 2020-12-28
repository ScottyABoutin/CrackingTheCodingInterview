package library;

import java.util.Arrays;
import java.util.Objects;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.IntConsumer;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

/**
 * A mutable sequence of characters.
 * <p>
 * The principal operations on a {@code MyStringBuilder} are the {@code append} and {@code insert}
 * methods, which are overloaded so as to accept data of any type. Each effectively converts a given
 * datum to a string and then appends or inserts the characters of that string to the string
 * builder. The {@code append} method always adds these characters at the end of the builder; the
 * {@code insert} method adds the characters at a specified point.
 * <p>
 * For example, if {@code z} refers to a {@code MyStringBuilder} whose current contents are
 * {@code "start"}, then the method call {@code z.append("le")} would cause the
 * {@code MyStringBuilder} to contain {@code "startle"}, whereas {@code z.insert(4, "le")} would
 * alter the {@code MyStringBuilder} to contain {@code "starlet"}.
 * <p>
 * In general, {@code sb.append(x)} has the same effect as {@code sb.insert(sb.length()}, x).
 * <p>
 * Every {@code MyStringBuilder} has a capacity. As long as the length of the character sequence
 * contained in the MyStringBuilder does not exceed the capacity, it is not necessary to allocate a
 * new internal buffer. If the internal buffer overflows, it is automatically made larger.
 * <p>
 * Instances of {@code MyStringBuilder} are not safe for use by multiple threads. If such
 * synchronization is required then it is recommended that {@code java.lang.StringBuffer} be used.
 * <p>
 * Unless otherwise noted, passing a {@code null} argument to a constructor or method in this class
 * will cause a {@code NullPointerException} to be thrown.
 * 
 * @apiNote {@code MyStringBuilder} implements {@code Comparable} but does not override
 * {@code equals}. Thus, the natural ordering of {@code MyStringBuilder} is inconsistent with
 * equals. Care should be exercised if {@code MyStringBuilder} objects are used as keys in a
 * {@code SortedMap} or elements in a {@code SortedSet}.
 */
public final class MyStringBuilder implements Appendable, CharSequence, Comparable<MyStringBuilder> {
    
    /* Simplest implementation, but could later be byte[] depending on letters */
    private char[] chars;
    
    /*
     * Array keeps track of capacity. Length keeps track of the length of the sequence. Invariant:
     * count <= letters.length
     */
    private int count;
    
    /**
     * Constructs a string builder with no characters in it and an initial capacity of 16
     * characters.
     */
    public MyStringBuilder() {
        this(16);
    }
    
    /**
     * Constructs a string builder with no characters in it and an initial capacity specified by the
     * {@code capacity} argument.
     * 
     * @param capacity the initial capacity.
     * @throws NegativeArraySizeException if the {@code capacity} argument is less than 0.
     */
    public MyStringBuilder(int capacity) {
        chars = new char[capacity];
    }
    
    /**
     * Constructs a string builder initialized to the contents of the specified string. The initial
     * capacity of the string builder is 16 plus the length of the string arugment.
     * 
     * @param str the initial contents of the buffer.
     */
    public MyStringBuilder(String str) {
        this(str.length() + 16); // Throws NullPointerException
        
        this.append(str);
    }
    
    /**
     * Constructs a string builder that contains the same characters as the specified
     * {@code CharSequence}. The initial capacity of the string builder is 16 plus the length of the
     * {@code CharSequence} argument.
     * 
     * @param seq the sequence to copy.
     */
    public MyStringBuilder(CharSequence seq) {
        this(seq.length() + 16); // Throws NullPointerException
        
        this.append(seq);
    }
    
    /**
     * Compares two {@code MyStringBuilder} instances lexicographically. The lexicographical
     * ordering of {@code MyStringBuilder} is defined as follows. Consider a {@code MyStringBuilder
     * builder} of length {@code len} to be a sequence of char values, {@code builder[0]} to {@code
     * builder[len-1]}. Suppose {@code k} is the lowest index at which the corresponding char values
     * from each sequence differ. The lexicographic ordering of the sequences is determined by a
     * numeric comparison of the char values {@code builder1[k]} with {@code builder2[k]}. If there
     * is no such index {@code k}, the shorter sequence is considered lexicographically less than
     * the other. If the sequences have the same length, the sequences are considered
     * lexicographically equal.
     * <p>
     * For finer-grained, locale-sensitive String comparison, refer to Collator.
     * 
     * @param another the {@code MyStringBuilder} to be compared with
     * @return the value {@code 0} if this {@code MyStringBuilder} contains the same character
     * sequence as that of the argument {@code MyStringBuilder}; a negative integer if this {@code
     * MyStringBuilder} is lexicographically less than the {@code MyStringBuilder} argument; or a
     * positive integer if this {@code MyStringBuilder} is lexicographically greater than the {@code
     * MyStringBuilder} argument.
     */
    @Override
    public int compareTo(MyStringBuilder another) {
        int toIterate = Math.min(count, another.count); // Implicitly throws NullPointerException
        for (int i = 0; i < toIterate; i++) {
            int diff = chars[i] - another.chars[i]; // char values cannot overflow
            if (diff != 0) {
                return diff;
            }
        }
        
        // Since all checked letters are equal, return based off of length
        return Integer.compare(count, another.count);
    }
    
    /**
     * Appends the four characters 'n' 'u' 'l' 'l' to this.
     * 
     * @return a reference to this object.
     */
    private MyStringBuilder appendNull() {
        this.ensureCapacity(count + 4);
        chars[count] = 'n';
        chars[count + 1] = 'u';
        chars[count + 2] = 'l';
        chars[count + 3] = 'l';
        count += 4;
        return this;
    }
    
    /**
     * Appends the string representation of the {@code Object} argument.
     * <p>
     * The overall effect is exactly as if the argument were converted to a string by the method
     * {@link String#valueOf(Object)}, and the characters of that string were then appended to this
     * character sequence.
     * 
     * @param obj an {@code Object}.
     * @return a reference to this object.
     */
    public MyStringBuilder append(Object obj) {
        if (obj == null) {
            return this.appendNull();
        }
        return this.append(obj.toString());
    }
    
    /**
     * Appends the specified string to this character sequence.
     * <p>
     * The characters of the {@code String} argument are appended, in order, increasing the length
     * of this sequence by the length of the argument. If {@code str} is {@code null}, then the four
     * characters {@code "null"} are appended.
     * <p>
     * Let {@code n} be the length of this character sequence just prior to execution of the
     * {@code append} method. Then the character at index {@code k} in the new character sequence is
     * equal to the character at index {@code k} in the old character sequence, if {@code k} is less
     * than {@code n}; otherwise, it is equal to the character at index {@code k-n} in the argument
     * {@code str}.
     * 
     * @param str a string.
     * @return a reference to this object.
     */
    public MyStringBuilder append(String str) {
        return this.append((CharSequence) str); // Do null check in CharSequence
    }
    
    /**
     * Appends the specified {@code StringBuffer} to this sequence.
     * <p>
     * The characters of the {@code StringBuffer} argument are appended, in order, to this sequence,
     * increasing the length of this sequence by the length of the argument. If {@code sb} is
     * {@code null}, then the four characters {@code "null"} are appended to this sequence.
     * <p>
     * Let {@code n} be the length of this character sequence just prior to execution of the
     * {@code append} method. Then the character at index {@code k} in the new character sequence is
     * equal to the character at index {@code k} in the old character sequence, if {@code k} is less
     * than {@code n}; otherwise, it is equal to the character at index {@code k-n} in the argument
     * {@code sb}.
     * 
     * @param sb the {@code StringBuffer} to append.
     * @return a reference to this object.
     */
    public MyStringBuilder append(StringBuffer sb) {
        if (sb == null) { // Necessary before synchronization
            return this.appendNull();
        } else {
            synchronized (sb) {
                return this.append((CharSequence) sb);
            }
        }
    }
    
    /**
     * Appends the specified character sequence to this {@code Appendable}.
     * <p>
     * Depending on which class implements the character sequence {@code csq}, the entire sequence
     * may not be appended. For instance, if {@code csq} is a {@code CharBuffer} then the
     * subsequence to append is defined by the buffer's position and limit.
     * 
     * @param s The character sequence to append. If {@code csq} is {@code null}, then the four
     *     characters {@code "null"} are appended to this {@code Appendable}.
     * @return A reference to this {@code Appendable}
     */
    public MyStringBuilder append(CharSequence s) {
        if (s == null) {
            return this.appendNull();
        }
        this.ensureCapacity(count + s.length());
        for (int i = 0; i < s.length(); i++) {
            chars[i + count] = s.charAt(i);
        }
        count += s.length();
        return this;
    }
    
    /**
     * Appends a subsequence of the specified {@code CharSequence} to this sequence.
     * <p>
     * Characters of the argument {@code s}, starting at index {@code start}, are appended, in
     * order, to the contents of this sequence up to the (exclusive) index {@code end}. The length
     * of this sequence is increased by the value of {@code end - start}.
     * <p>
     * Let {@code n} be the length of this character sequence just prior to execution of the
     * {@code append} method. Then the character at index {@code k} in this character sequence
     * becomes equal to the character at index {@code k} in this sequence, if {@code k} is less than
     * {@code n}; otherwise, it is equal to the character at index {@code k+start-n} in the argument
     * {@code s}.
     * <p>
     * If {@code s} is {@code null}, then this method appends characters as if the {@code s}
     * parameter was a sequence containing the four characters {@code "null"}.
     * 
     * @param s the sequence to append.
     * @param start the starting index of the subsequence to be appended.
     * @param end the end index of the subsequence to be appended.
     * @return a reference to this object.
     * @throws IndexOutOfBoundsException if {@code start} is negative, or {@code start} is greater
     *     than {@code end} or {@code end} is greater than {@code s.length()}
     */
    public MyStringBuilder append(CharSequence s, int start, int end) {
        if (s == null) {
            s = "null";
        }
        return this.append(s.subSequence(start, end)); // Throws IndexOutOfBoundsException
    }
    
    /**
     * Appends the string representation of the {@code char} array argument to this sequence.
     * <p>
     * The characters of the array argument are appended, in order, to the contents of this
     * sequence. The length of this sequence increases by the length of the argument.
     * <p>
     * The overall effect is exactly as if the argument were converted to a string by the method
     * {@link String#valueOf(char[])}, and the characters of that string were then appended to this
     * character sequence.
     * 
     * @param str the characters to be appended.
     * @return a reference to this object.
     */
    public MyStringBuilder append(char[] str) {
        return this.append(str, 0, str.length); // throws NullPointerException
    }
    
    /**
     * Appends the string representation of a subarray of the {@code char} array argument to this
     * sequence.
     * <p>
     * Characters of the {@code char} array {@code str}, starting at index {@code offset}, are
     * appended, in order, to the contents of this sequence. The length of this sequence increases
     * by the value of {@code len}.
     * <p>
     * The overall effect is exactly as if the arguments were converted to a string by the method
     * {@link String#copyValueOf(char[], int, int)}, and the characters of that string were then
     * appended to this character sequence.
     * 
     * @param str the characters to be appended.
     * @param offset the index of the first {@code char} to append.
     * @param len the number of {@code char}s to append.
     * @return a reference to this object.
     * @throws IndexOutOfBoundsException if {@code offset < 0} or {@code len < 0} or
     *     {@code offset+len > str.length}
     */
    public MyStringBuilder append(char[] str, int offset, int len) {
        Objects.checkFromIndexSize(offset, len, count);
        Objects.requireNonNull(str); // Do this before ensureCapacity
        this.ensureCapacity(count + len);
        System.arraycopy(str, offset, chars, count, len);
        count += len;
        return this;
    }
    
    /**
     * Appends the string representation of the {@code boolean} argument to the sequence
     * <p>
     * The overall effect is exactly as if the argument were converted to a string by the method
     * {@link String#valueOf(boolean)}, and the characters of that string were then appended to this
     * character sequence.
     * 
     * @param b a {@code boolean}.
     * @return a reference to this object.
     */
    public MyStringBuilder append(boolean b) {
        this.ensureCapacity(count + (b ? 4 : 5));
        if (b) {
            chars[count] = 't';
            chars[count + 1] = 'r';
            chars[count + 2] = 'u';
            chars[count + 3] = 'e';
            count += 4;
        } else {
            chars[count] = 'f';
            chars[count + 1] = 'a';
            chars[count + 2] = 'l';
            chars[count + 3] = 's';
            chars[count + 4] = 'e';
            count += 5;
        }
        return this;
    }
    
    /**
     * Appends the string representation of the {@code char} argument to this sequence.
     * <p>
     * The argument is appended to the contents of this sequence. The length of this sequence
     * increases by {@code 1}.
     * <p>
     * The overall effect is exactly as if the argument were converted to a string by the method
     * {@link String#valueOf(char)}, and the character in that string were then appended to this
     * character sequence.
     * 
     * @param c a {@code char}.
     * @return a reference to this object.
     */
    public MyStringBuilder append(char c) {
        this.ensureCapacity(count + 1);
        chars[count] = c;
        count++;
        return this;
    }
    
    /**
     * Appends the string representation of the {@code int} argument to this sequence
     * <p>
     * The overall effect is exactly as if the argument were converted to a string by the method
     * {@link String#valueOf(int)}, and the characters of that string were then appended to this
     * character sequence.
     * 
     * @param i an {@code int}.
     * @return a reference to this object.
     */
    public MyStringBuilder append(int i) {
        return this.append(String.valueOf(i));
    }
    
    /**
     * Appends the string representation of the {@code long} argument to this sequence
     * <p>
     * The overall effect is exactly as if the argument were converted to a string by the method
     * {@link String#valueOf(long)}, and the characters of that string were then appended to this
     * character sequence.
     * 
     * @param lon a {@code long}.
     * @return a reference to this object.
     */
    public MyStringBuilder append(long lng) {
        return this.append(String.valueOf(lng));
    }
    
    /**
     * Appends the string representation of the {@code float} argument to this sequence
     * <p>
     * The overall effect is exactly as if the argument were converted to a string by the method
     * {@link String#valueOf(float)}, and the characters of that string were then appended to this
     * character sequence.
     * 
     * @param f a {@code float}.
     * @return a reference to this object.
     */
    public MyStringBuilder append(float f) {
        return this.append(String.valueOf(f));
    }
    
    /**
     * Appends the string representation of the {@code double} argument to this sequence
     * <p>
     * The overall effect is exactly as if the argument were converted to a string by the method
     * {@link String#valueOf(double)}, and the characters of that string were then appended to this
     * character sequence.
     * 
     * @param d a {@code double}.
     * @return a reference to this object.
     */
    public MyStringBuilder append(double d) {
        return this.append(String.valueOf(d));
    }
    
    /**
     * Appends the string representation of the {@code codePoint} argument to this sequence
     * <p>
     * The argument is appended to the contents of this sequence. The length of this sequence
     * increases by {@link Character#charCount(int)}.
     * <p>
     * The overall effect is exactly as if the argument were converted to a {@code char} array by
     * the method {@link Character#toChars(int)} and the characterss in that array were then
     * appended to this character sequence.
     * 
     * @param codePoint a Unicode code point
     * @return a reference to this object.
     * @throws IllegalArgumentException if the specified {@code codePoint} is not a valid Unicode
     *     code point.
     */
    public MyStringBuilder appendCodePoint(int codePoint) {
        // Character.toChars throws the IllegalArugmentException
        return this.append(Character.toChars(codePoint));
    }
    
    /**
     * Removes the characters in a substring of this sequence. The substring begins at the specified
     * {@code start} and extends to the character at index {@code end - 1} or to the end of the
     * sequence if no such character exists. If {@code start} is equal to {@code end}, no changes
     * are made.
     * 
     * @param start The beginning index, inclusive.
     * @param end The ending index, exclusive.
     * @return This object.
     * @throws StringIndexOutOfBoundsException if {@code start} is negative, greater than
     *     {@code length()}, or greater than {@code end}.
     */
    public MyStringBuilder delete(int start, int end) {
        if (start < 0 || start > this.length() || start > end) {
            throw new StringIndexOutOfBoundsException(start);
        }
        end = Math.min(end, count);
        int diff = end - start;
        for (int i = start; i < count - diff; i++) {
            chars[i] = chars[diff + i];
        }
        count -= diff;
        return this;
    }
    
    /**
     * Removes the {@code char} at the specified position in this sequence. This sequence is
     * shortened by one {@code char}.
     * <p>
     * Note: If the character at the given index is a supplementary character, this method does not
     * remove the entire character. If correct handling of supplementary characters is required,
     * determine the number of {@code char}s to remove by calling
     * {@code Character.charCount(thisSequence.codePointAt(index))}, where {@code thisSequence} is
     * this sequence.
     * 
     * @param index Index of {@code char} to remove
     * @return This object.
     * @throws StringIndexOutOfBoundsException if the {@code index} is negative or greater than or
     *     equal to {@code length()}.
     */
    public MyStringBuilder deleteCharAt(int index) {
        if (index < 0 || index >= this.length()) {
            throw new StringIndexOutOfBoundsException(index);
        }
        for (int i = index; i < count - 1; i++) {
            chars[i] = chars[i + 1];
        }
        count--;
        return this;
    }
    
    /**
     * Replaces the characters in a substring of this sequence with characters in the specified
     * {@code String}. The substring begins at the specified {@code start} and extends to the
     * character at index {@code end - 1} or to the end of the sequence if no such character exists.
     * First the charaters in the substring are removed and then the specified {@code String} is
     * inserted at {@code start}. (This sequence will be lengthened to accomodate the specified
     * String if necessary.)
     * 
     * @param start The beginning index, inclusive.
     * @param end The ending index, exclusive.
     * @param str String that will replace previous contents.
     * @return This object.
     * @throws StringIndexOutOfBoundsException if {@code start} is negative, greater than
     *     {@code length()}, or greater than {@code end}.
     */
    public MyStringBuilder replace(int start, int end, String str) {
        if (start < 0 || start > this.length() || start > end) {
            throw new StringIndexOutOfBoundsException(start);
        }
        end = Math.min(end, count);
        int sizeChange = end - start + str.length(); // Throws NullPointerException
        if (sizeChange > 0) {
            int newCount = count + sizeChange;
            this.ensureCapacity(newCount);
            for (int i = newCount - 1; i >= end; i--) {
                chars[i] = chars[i - sizeChange];
            }
        }
        for (int i = start; i < count - sizeChange; i++) {
            chars[i] = str.charAt(i - start);
        }
        count += sizeChange;
        return this;
    }
    
    private MyStringBuilder unsafelyInsertNull(int offset) {
        this.ensureCapacity(count + 4);
        for (int i = count + 3; i >= offset + 4; i--) {
            chars[i] = chars[i - 4];
        }
        chars[offset] = 'n';
        chars[offset + 1] = 'u';
        chars[offset + 2] = 'l';
        chars[offset + 3] = 'l';
        count += 4;
        return this;
    }
    
    private MyStringBuilder unsafelyInsertCharSequence(int offset, CharSequence csq) {
        return this.unsafelyInsertCharSequence(offset, csq, 0, csq.length());
    }
    
    private MyStringBuilder unsafelyInsertCharSequence(int offset, CharSequence csq, int start, int end) {
        int numElements = end - start;
        int targetIndex = offset + numElements;
        this.ensureCapacity(count + numElements);
        for (int i = count + numElements; i >= targetIndex; i--) {
            chars[i] = chars[i - numElements];
        }
        // Both conditions should always be equivalent
        for (int i = offset, csqIndex = start; i < targetIndex && csqIndex < end; i++, csqIndex++) {
            chars[i] = csq.charAt(csqIndex);
        }
        count += numElements;
        return this;
    }
    
    private MyStringBuilder unsafelyInsertCharArray(int offset, char[] array) {
        return this.unsafelyInsertCharArray(offset, array, 0, array.length);
    }
    
    private MyStringBuilder unsafelyInsertCharArray(int offset, char[] array, int start, int end) {
        int numElements = end - start;
        int targetIndex = offset + numElements;
        this.ensureCapacity(count + numElements);
        for (int i = count + numElements; i >= targetIndex; i--) {
            chars[i] = chars[i - numElements];
        }
        // Both conditions should always be equivalent
        for (int i = offset, csqIndex = start; i < targetIndex && csqIndex < end; i++, csqIndex++) {
            chars[i] = array[csqIndex];
        }
        count += numElements;
        return this;
    }
    
    /**
     * Inserts the string representation of a subarray of the {@code str} array argument into this
     * sequence. The subarray begins at the specified {@code offset} and extends {@code len}
     * {@code char}s. The characters of the subarray are inserted into this sequence at the position
     * indicated by {@code index}. The length of this sequence increases by {@code len} chars.
     * 
     * @param index position at which to insert subarray.
     * @param str A {@code char} array.
     * @param offset The index of the first {@code char} in subarray to be inserted.
     * @param len the number of {@code char}s in the subarray to be inserted.
     * @return This object
     * @throws StringIndexOutOfBoundsException if {@code index} is negative or greater than
     *     {@code length()}, or {@code offset} or {@code len} are negative, or {@code (offset+len)}
     *     is greater than {@code str.length}.
     */
    public MyStringBuilder insert(int index, char[] str, int offset, int len) {
        if (index < 0 || index > count) {
            throw new StringIndexOutOfBoundsException(index);
        }
        if (offset < 0) {
            throw new StringIndexOutOfBoundsException(offset);
        }
        if (len < 0 || offset + len > str.length) {
            throw new StringIndexOutOfBoundsException(len);
        }
        return this.unsafelyInsertCharArray(index, str, offset, len + offset);
    }
    
    /**
     * Inserts the string representation of the {@code Object} argument into this character
     * sequence.
     * <p>
     * The overall effect is exactly as if the second argument were converted to a string by the
     * method {@link String#valueOf(Object)}, and the characters of that string were then
     * {@code inserted} into this character sequence at the indicated offset.
     * <p>
     * The {@code offset} argument must be greater than or equal to 0, and less than or equal to the
     * length of this sequence.
     * 
     * @param offset the offset.
     * @param obj an {@code Object}.
     * @return a reference to this object.
     * @throws StringIndexOutOfBoundsException if the offset is invalid.
     */
    public MyStringBuilder insert(int offset, Object obj) {
        if (offset < 0 || offset > count) {
            throw new StringIndexOutOfBoundsException(offset);
        }
        if (obj == null) { // Need to check null before calling toString
            return unsafelyInsertNull(offset);
        } else {
            return unsafelyInsertCharSequence(offset, obj.toString());
        }
    }
    
    /**
     * Inserts the string into this character sequence.
     * <p>
     * The characters of the {@code String} argument are inserted, in order, into this sequence at
     * the indicated offset, moving up any characters originally above that position and increasing
     * the length of this sequence by the length of the argument. If {@code str} is {@code null},
     * then the four characters {@code "null"} are inserted into this sequence.
     * <p>
     * The character at index {@code k} in the new character sequence is equal to:
     * <ul>
     * <li>the character at index {@code k} in the old character sequence, if {@code k} is less than
     * {@code offset}
     * <li>the character at index {@code k-offset} in the argument {@code str}, if {@code k} is not
     * less than {@code offset} but is less than {@code offset+str.length()}
     * <li>the character at index {@code k-str.length()} in the old character sequence, if {@code k}
     * is not less than {@code offset+str.length()}
     * </ul>
     * <p>
     * The {@code offset} argument must be greater than or equal to 0, and less than or equal to the
     * length of this sequence.
     * 
     * @param offset the offset.
     * @param str a string.
     * @return a reference to this object.
     * @throws StringIndexOutOfBoundsException if the offset is invalid.
     */
    public MyStringBuilder insert(int offset, String str) {
        if (offset < 0 || offset > count) {
            throw new StringIndexOutOfBoundsException(offset);
        }
        if (str == null) {
            return unsafelyInsertNull(offset);
        } else {
            return unsafelyInsertCharSequence(offset, str);
        }
    }
    
    /**
     * Inserts the string representation of the {@code char} array into this sequence.
     * <p>
     * The characters of the array argument are inserted into the contents of this sequence at the
     * position indicated by {@code offset}. The length of this sequence increases by the length of
     * the arugment.
     * <p>
     * The overall effect is exactly as if the second argument were converted to a string by the
     * method {@link String#valueOf(char[])}, and the characters of that string were then {@code
     * inserted} into this character sequence at the indicated offset.
     * <p>
     * The {@code offset} argument must be greater than or equal to 0, and less than or equal to the
     * length of this sequence.
     * 
     * @param offset the offset.
     * @param offset the offset.
     * @param str a character array.
     * @return a reference to this object.
     * @throws StringIndexOutOfBoundsException if the offset is invalid.
     */
    public MyStringBuilder insert(int offset, char[] str) {
        if (offset < 0 || offset > count) {
            throw new StringIndexOutOfBoundsException(offset);
        }
        return unsafelyInsertCharArray(offset, Objects.requireNonNull(str));
    }
    
    /**
     * Inserts the specified {@code CharSequence} into this sequence.
     * <p>
     * The characters of the {@code CharSequence} argument are inserted, in order, into this
     * sequence at the indicated offset, moving up any characters originally above that position and
     * increasing the length of this sequence by the length of the argument {@code s}.
     * <p>
     * The result of this method is exactly the same as if it were an invocation of this object's
     * {@code insert(dstOffset, s, 0, s.length())} method.
     * <p>
     * If {@code s} is {@code null}, then the four characters "null" are inserted into this
     * sequence.
     * 
     * @param dstOffset the offset.
     * @param s the sequence to be inserted
     * @return a reference to this object.
     * @throws IndexOutOfBoundsException if the offset is invalid.
     */
    public MyStringBuilder insert(int dstOffset, CharSequence s) {
        if (dstOffset < 0 || dstOffset > count) {
            throw new IndexOutOfBoundsException(dstOffset);
        }
        if (s == null) {
            return unsafelyInsertNull(dstOffset);
        } else {
            return unsafelyInsertCharSequence(dstOffset, s);
        }
    }
    
    /**
     * Inserts a subsequence of the specified {@code CharSequence} into this sequence.
     * <p>
     * The subsequence of the argument {@code s} specified by {@code start} and {@code end} are
     * inserted, in order, into this sequence at the specified destination offset, moving up any
     * characters originally above that position. The length of this sequence is increased by
     * {@code end - start}.
     * <p>
     * The character at index {@code k} in this sequence becomes equal to:
     * <ul>
     * <li>the character at index {@code k} in this sequence, if {@code k} is less than
     * {@code dstOffset}
     * <li>the character at index {@code k+start-dstOffset} in the argument {@code s}, if {@code k}
     * is greater than or equal to {@code dstOffset} but is less than {@code dstOffset+end-start}
     * <li>the character at index {@code k - (end-start)} in this sequence, if {@code k} is greater
     * than or equal to {@code dstOffset+end-start}
     * </ul>
     * <p>
     * The {@code dstOffset} argument must be greater than or equal to 0, and less than or equal to
     * the length of this sequence.
     * <p>
     * The start argument must be nonnegative, and not greater than {@code end}.
     * <p>
     * The end argument must be greater than or equal to {@code start}, and less than or equal to
     * the length of s.
     * <p>
     * If {@code s} is {@code null}, then this method inserts characters as if the s parameter was a
     * sequence containing the four characters {@code "null"}.
     * 
     * @param dstOffset the offset in this sequence.
     * @param s the sequence to be inserted.
     * @param start the starting index of the subsequence to be inserted.
     * @param end the end index of the subsequence to be inserted.
     * @return a reference to this object.
     * @throws IndexOutOfBoundsException if {@code dstOffset} is negative or greater than
     *     {@code this.length()}, or {@code start} or {@code end} are negative, or {@code start} is
     *     greater than {@code end} or {@code end} is greater than {@code s.length()}
     */
    public MyStringBuilder insert(int dstOffset, CharSequence s, int start, int end) {
        if (s == null) {
            s = "null";
        }
        // s.subsequence throws most of the IOOBE's:
        // this.insert(int, CharSequence) throws the offset ones
        return this.insert(dstOffset, s.subSequence(start, end));
    }
    
    /**
     * Inserts the string representation of the {@code boolean} argument into this sequence.
     * <p>
     * The overall effect is exactly as if the second argument were converted to a string by the
     * method {@link String#valueOf(boolean)}, and the characters of that string were then
     * {@code inserted} into this character sequence at the indicated offset.
     * <p>
     * The {@code offset} argument must be greater than or equal to 0, and less than or equal to the
     * length of this sequence.
     * 
     * @param offset the offset.
     * @param b a {@code boolean}.
     * @return a reference to this object.
     * @throws StringIndexOutOfBoundsException if the offset is invalid.
     */
    public MyStringBuilder insert(int offset, boolean b) {
        if (offset < 0 || offset > count) {
            throw new StringIndexOutOfBoundsException(offset);
        }
        int numNewChars = b ? 4 : 5;
        this.ensureCapacity(count + numNewChars);
        for (int i = count + numNewChars; i >= offset + numNewChars; i--) {
            chars[i] = chars[i - numNewChars];
        }
        if (b) {
            chars[offset] = 't';
            chars[offset + 1] = 'r';
            chars[offset + 2] = 'u';
            chars[offset + 3] = 'e';
        } else {
            chars[offset] = 'f';
            chars[offset + 1] = 'a';
            chars[offset + 2] = 'l';
            chars[offset + 3] = 's';
            chars[offset + 4] = 'e';
        }
        count += numNewChars;
        return this;
    }
    
    /**
     * Inserts the string representation of the {@code char} argument into this sequence.
     * <p>
     * The overall effect is exactly as if the second argument were converted to a string by the
     * method {@link String#valueOf(char)}, and the characters in that string were then
     * {@code inserted} into this character sequence at the indicated offset.
     * <p>
     * The {@code offset} argument must be greater than or equal to 0, and less than or equal to the
     * length of this sequence.
     * 
     * @param offset the offset.
     * @param c a {@code char}.
     * @return a reference to this object.
     * @throws IndexOutOfBoundsException if the offset is invalid.
     */
    public MyStringBuilder insert(int offset, char c) {
        Objects.checkIndex(offset, count);
        this.ensureCapacity(count + 1);
        for (int i = count + 1; i >= offset + 1; i--) {
            chars[i] = chars[i - 1];
        }
        chars[offset] = c;
        count++;
        return this;
    }
    
    /**
     * Inserts the string representation of the second {@code int} argument into this sequence.
     * <p>
     * The overall effect is exactly as if the second argument were converted to a string by the
     * method {@link String#valueOf(int)}, and the characters of that string were then
     * {@code inserted} into this character sequence at the indicated offset.
     * <p>
     * The {@code offset} argument must be greater than or equal to 0, and less than or equal to the
     * length of this sequence.
     * 
     * @param offset the offset.
     * @param i an {@code int}.
     * @return a reference to this object.
     * @throws StringIndexOutOfBoundsException if the offset is invalid.
     */
    public MyStringBuilder insert(int offset, int i) {
        if (offset < 0 || offset > count) {
            throw new StringIndexOutOfBoundsException(offset);
        }
        return this.insert(offset, String.valueOf(i));
    }
    
    /**
     * Inserts the string representation of the {@code long} argument into this sequence.
     * <p>
     * The overall effect is exactly as if the second argument were converted to a string by the
     * method {@link String#valueOf(long)}, and the characters of that string were then
     * {@code inserted} into this character sequence at the indicated offset.
     * <p>
     * The {@code offset} argument must be greater than or equal to 0, and less than or equal to the
     * length of this sequence.
     * 
     * @param offset the offset.
     * @param l a {@code long}.
     * @return a reference to this object.
     * @throws StringIndexOutOfBoundsException if the offset is invalid.
     */
    public MyStringBuilder insert(int offset, long l) {
        if (offset < 0 || offset > count) {
            throw new StringIndexOutOfBoundsException(offset);
        }
        return this.insert(offset, String.valueOf(l));
    }
    
    /**
     * Inserts the string representation of the {@code float} argument into this sequence.
     * <p>
     * The overall effect is exactly as if the second argument were converted to a string by the
     * method {@link String#valueOf(float)}, and the characters of that string were then
     * {@code inserted} into this character sequence at the indicated offset.
     * <p>
     * The {@code offset} argument must be greater than or equal to 0, and less than or equal to the
     * length of this sequence.
     * 
     * @param offset the offset.
     * @param f a {@code float}.
     * @return a reference to this object.
     * @throws StringIndexOutOfBoundsException if the offset is invalid.
     */
    public MyStringBuilder insert(int offset, float f) {
        if (offset < 0 || offset > count) {
            throw new StringIndexOutOfBoundsException(offset);
        }
        return this.insert(offset, String.valueOf(f));
    }
    
    /**
     * Inserts the string representation of the {@code double} argument into this sequence.
     * <p>
     * The overall effect is exactly as if the second argument were converted to a string by the
     * method {@link String#valueOf(double)}, and the characters of that string were then
     * {@code inserted} into this character sequence at the indicated offset.
     * <p>
     * The {@code offset} argument must be greater than or equal to 0, and less than or equal to the
     * length of this sequence.
     * 
     * @param offset the offset.
     * @param d a {@code double}.
     * @return a reference to this object.
     * @throws StringIndexOutOfBoundsException if the offset is invalid.
     */
    public MyStringBuilder insert(int offset, double d) {
        if (offset < 0 || offset > count) {
            throw new StringIndexOutOfBoundsException(offset);
        }
        return this.insert(offset, String.valueOf(d));
    }
    
    /**
     * Returns the index within this string of the first occurrence of the specified substring.
     * <p>
     * The returned index is the smallest value {@code k} for which:
     * 
     * <pre>
     * {@code this.toString().startsWith(str, k)}
     * </pre>
     * <p>
     * If no such value of {@code k} exists, then {@code -1} is returned.
     * 
     * @param str the substring to search for.
     * @return the index of the first ocurrence of the specified substring, or {@code -1} if there
     * is no such occurrence.
     */
    public int indexOf(String str) {
        return this.indexOf(str, 0);
    }
    
    /**
     * Returns the index within this string of the first occurrence of the specified substring,
     * starting at the specified index.
     * <p>
     * The returned index is the smallest value {@code k} for which:
     * 
     * <pre>
     * {@code
     *     k >= Math.min(fromIndex, this.length()) &&
     *                   this.toString().startsWith(str, k)
     * }
     * </pre>
     * <p>
     * If no such value of {@code k} exists, then {@code -1} is returned.
     * 
     * @param str the substring to search for.
     * @param fromIndex the index from which to start the search.
     * @return the index of the first occurrence of the specified substring, starting at the
     * specified index, or {@code -1} if there is no such occurrrence.
     */
    public int indexOf(String str, int fromIndex) {
        /*
         * Initial implementation: look for first letter, and on finding, search all characters in
         * str until a match. More efficient methods definitely exist (simultaneous search should be
         * quicker, with more storage necessary)
         */
        if (fromIndex < 0) {
            fromIndex = 0;
        } else if (fromIndex > count) {
            fromIndex = count;
        }
        if (str.isEmpty()) { // throws NullPointerException
            return fromIndex;
        }
        char first = str.charAt(0);
        int maxIndex = count - str.length();
        for (int i = fromIndex; i < maxIndex; i++) {
            if (chars[i] == first) {
                // Search string for the rest of the letters
                int stringIndex = 1;
                while (stringIndex < str.length() && chars[i + stringIndex] == str.charAt(stringIndex)) {
                    stringIndex++; // Does this handle Integer.MAX_VALUE length string?
                }
                if (stringIndex == str.length()) {
                    return i;
                }
            }
        }
        return -1;
    }
    
    /**
     * Returns the index within this string of the last occurrence of the specified substring. The
     * last occurrence if the empty string {@code ""} is considered to occur at the index value
     * {@code this.length()}.
     * <p>
     * The returned index is the largest {@code k} for which:
     * 
     * <pre>
     * {@code this.toString().startsWith(str, k)}
     * </pre>
     * <p>
     * If no such value of {@code k} exists, then {@code -1} is returned.
     * 
     * @param str the substring to search for.
     * @return the index of the last occurrence of the specified substring, or {@code -1} if there
     * is no such occurrence.
     */
    public int lastIndexOf(String str) {
        return this.lastIndexOf(str, count);
    }
    
    /**
     * Returns the index within this string of the last occurrence of the specified substring,
     * searching backward starting at the specified index.
     * <p>
     * The returned index is the largest value {@code k} for which:
     * 
     * <pre>
     * {@code
     *     k <= Math.min(fromIndex, this.length()) &&
     *                   this.toString().startsWith(str, k)
     * }
     * </pre>
     * <p>
     * If no such value of {@code k} exists, then {@code -1} is returned.
     * 
     * @param str the substring to search for.
     * @param fromIndex the index to start the search from.
     * @return the index of the last occurrence of the specified substring, searching backward from
     * the specified index, or {@code -1} if there is no such occurrrence.
     */
    public int lastIndexOf(String str, int fromIndex) {
        /*
         * Initial implementation: look for first letter, and on finding, search all characters in
         * str until a match. More efficient methods definitely exist (simultaneous search should be
         * quicker, with more storage necessary)
         */
        if (fromIndex < 0) {
            fromIndex = 0;
        } else if (fromIndex > count - str.length()) { // throws NullPointerException
            fromIndex = count;
        }
        if (str.isEmpty()) {
            return fromIndex;
        }
        char first = str.charAt(0);
        int maxIndex = this.count - str.length() - 1;
        for (int i = maxIndex; i >= 0; i--) {
            if (chars[i] == first) {
                // Search string for the rest of the letters
                int stringIndex = 1;
                while (stringIndex < str.length() && chars[i + stringIndex] == str.charAt(stringIndex)) {
                    stringIndex++; // Does this handle Integer.MAX_VALUE length string?
                }
                if (stringIndex == str.length()) {
                    return i;
                }
            }
        }
        return -1;
    }
    
    /**
     * Causes this character sequence to be replaced by the reverse of the sequence. If there are
     * any surrogate pairs included in the sequence, these are treated as single characters for the
     * reverse operation. Thus, the order of the high-low surrogates is never reversed. Let
     * {@code n} be the character length of this character length (not the length in {@code char}
     * values) just prior to execution of the {@code reverse} method. Then the character at index
     * {@code k} in the new character sequence is equal to the character at index {@code n-k-1} in
     * the old character sequence.
     * <p>
     * Note that the reverse operation may result in producing surrogate pairs that were unpaired
     * low-surrogates and high-surrogates before the operation. For example, reversing
     * "\u005CuDC00\u005CuD800" produces "\u005CuD800\u005CuDC00" which is a valid surrogate pair.
     * 
     * @return a reference to this object.
     */
    public MyStringBuilder reverse() {
        boolean hasSurrogates = false;
        for (int i = 0, j = count - 1; i < j; i++, j--) {
            char iChar = chars[i];
            char jChar = chars[j];
            chars[i] = jChar;
            chars[j] = iChar;
            if (Character.isSurrogate(iChar) || Character.isSurrogate(jChar)) {
                hasSurrogates = true;
            }
        }
        if (hasSurrogates) {
            this.reverseSurrogatePairs();
        }
        return this;
    }
    
    private void reverseSurrogatePairs() {
        for (int i = 0; i < count - 1; i++) {
            char possibleLowSurrogate = chars[i];
            if (Character.isLowSurrogate(possibleLowSurrogate)) {
                char possibleHighSurrogate = chars[i + 1];
                if (Character.isHighSurrogate(possibleHighSurrogate)) {
                    chars[i] = possibleHighSurrogate;
                    chars[i + 1] = possibleLowSurrogate;
                    i++; // Do not check the reversed high-low pair
                }
            }
        }
    }
    
    /**
     * Returns the length (character count).
     *
     * @return the length of the sequence of characters currently represented by this object
     */
    @Override
    public int length() {
        return count;
    }
    
    /**
     * Returns the current capacity. The capacity is the amount of storage available for newly
     * inserted characters, beyond which an allocation will occur.
     * 
     * @return the current capacity
     */
    public int capacity() {
        return chars.length;
    }
    
    /**
     * Ensures that the capacity is at least equal to the specified minimum. If the current capacity
     * is less than the argument, then a new internal array is allocated with greater capacity. The
     * new capacity is the larger of:
     * <ul>
     * <li>The {@code minimumCapacity} argument.
     * <li>Twice the old capacity plus {@code 2}.
     * </ul>
     * <p>
     * If the {@code minimumCapacity} argument is nonpositive, this method takes no action and
     * simply returns. Note that subsequent operations on this object can reduce the actual capacity
     * below that requested here.
     * 
     * @param minimumCapacity the minimum desired capacity.
     */
    public void ensureCapacity(int minimumCapacity) {
        if (minimumCapacity > 0 && minimumCapacity - chars.length > 0) {
            int newCapacity = Math.max(minimumCapacity, chars.length * 2 + 2);
            chars = Arrays.copyOf(chars, newCapacity);
        }
    }
    
    /**
     * Attempts to reduce storage used for the character sequence. If the buffer is larger than
     * necessary to hold its current sequence of characters, then it may be resized to become more
     * space efficient. Calling this method may, but is not required to, affect the value returned
     * by a subsequent call to the {@link #capacity()} method.
     */
    public void trimToSize() {
        if (count < chars.length) {
            chars = Arrays.copyOf(chars, count);
        }
    }
    
    /**
     * Sets the length of the character sequence. The sequence is changed to a new character
     * sequence whose length is specified by the argument. For every nonnegative index {@code k}
     * less than {@code newLength}, the character at index {@code k} in the new character sequence
     * is the same as the character at index {@code k} in the old sequence if {@code k} is less than
     * the length of the old character sequence; otherwise, it is the null character
     * {@code '\u005Cu0000'}. In other words, if the {@code newLength} argument is less than the
     * current length, the length is changed to the specified length.
     * <p>
     * If the {@code newLength} argument is greater than or equal to the current length, sufficient
     * null characters ({@code '\u005Cu0000'}) are appended so that length becomes the
     * {@code newLength} argument.
     * <p>
     * The {@code newLength} argument must be greater than or equal to {@code 0}.
     *
     * @param newLength the new length
     * @throws IndexOutOfBoundsException if the {@code newLength} argument is negative.
     */
    public void setLength(int newLength) {
        if (newLength < 0) {
            throw new IndexOutOfBoundsException(newLength);
        }
        if (newLength > count) {
            // No guarantee that higher-set chars have been zero'd out.
            for (int i = newLength; i < chars.length; i++) {
                chars[i] = '\u0000';
            }
            this.ensureCapacity(newLength);
        }
        count = newLength;
    }
    
    /**
     * Returns the {@code char} value in this sequence at the specified index. The first
     * {@code char} value is at index {@code 0}, the next at index {@code 1}, and so on, as in array
     * indexing.
     * <p>
     * The index argument must be greater than or equal to {@code 0}, and less than the length of
     * this sequence.
     * <p>
     * If the {@code char} value specified by the index is a surrogate, the surrogate value is
     * returned.
     * 
     * @param index the index of the desired {@code char} value.
     * @return the {@code char} value at the specified index.
     * @throws IndexOutOfBoundsException if {@code index} is negative or greater than or equal to
     *     {@code length()}.
     */
    @Override
    public char charAt(int index) {
        Objects.checkIndex(index, count);
        return chars[index];
    }
    
    /**
     * Returns the character (Unicode code point) at the specified index. The index refers to
     * {@code char} values (Unicode code units) and ranges from {@code 0} to
     * {@code CharSequence.length() - 1}.
     * <p>
     * If the {@code char} value specified at the given index is in the high-surrogate range, the
     * following index is less than the length of this sequence, and the {@code char} value at the
     * following index is in the low-surrogate range, then the supplementary code point
     * corresponding to this surrogate pair is returned. Otherwise, the {@code char} value at the
     * given index is returned.
     * 
     * @param index the index to the {@code char} values
     * @return the code point value of the character at the {@code index}
     * @throws IndexOutOfBoundsException if the {@code index} argument is negative or not less than
     *     the length of this sequence.
     */
    public int codePointAt(int index) {
        Objects.checkIndex(index, count); // Necessary since count might be less than the capacity
        return Character.codePointAt(chars, index);
    }
    
    /**
     * Returns the character (Unicode code point) before the specified index. The index refers to
     * {@code char} values (Unicode code units) and ranges from {@code 1} to
     * {@code CharSequence.length()}.
     * <p>
     * If the {@code char} value at {@code (index - 1)} is in the low-surrogate range,
     * {@code (index - 2)} is not negative, and the {@code char} value at {@code (index - 2)} is in
     * the high-surrogate range, then the supplementary code point value of the surrogate pair is
     * returned. If the {@code char} value at {@code index - 1} is an unpaired low-surrogate or a
     * high-surrogate, the surrogate value is returned.
     * 
     * @param index the index following the code point that should be returned
     * @return the Unicode code point value before the given index.
     * @throws IndexOutOfBoundsException if the {@code index} argument is less than 1 or greater
     *     than the length of this sequence.
     */
    public int codePointBefore(int index) {
        Objects.checkIndex(index, count); // Necessary since count might be less than the capacity
        return Character.codePointBefore(chars, index);
    }
    
    /**
     * Returns the number of Unicode code points in the specified text range of this sequence. The
     * text range begins at the specified {@code beginIndex} and extends to the {@code char} at
     * index {@code endIndex - 1}. Thus the length (in {@code char}s) of the text range is
     * {@code endIndex-beginIndex}. Unpaired surrogates within this sequence count as one code point
     * each.
     * 
     * @param beginIndex the index to the first {@code char} of the text range.
     * @param endIndex the index after the last {@code char} of the text range.
     * @return the number of Unicode code points in the specified text range
     * @throws IndexOutOfBoundsException if the {@code beginIndex} is negative, or {@code endIndex}
     *     is larger than the length of this sequence, or {@code beginIndex} is larger than
     *     {@code endIndex}.
     */
    public int codePointCount(int beginIndex, int endIndex) {
        Objects.checkFromToIndex(beginIndex, endIndex, count);
        return Character.codePointCount(chars, beginIndex, endIndex - beginIndex);
    }
    
    /**
     * Returns the index within this sequence that is offset from the given {@code index} by
     * {@code codePointOffset} code points. Unpaired surrogates within the text range given by
     * {@code index} and {@code codePointOffset} count as one code point each.
     * 
     * @param index the index to be offset
     * @param codePointOffset the offset in code points
     * @return the index within this sequence
     * @throws IndexOutOfBoundsException if {@code index} is negative or larger than the length of
     *     this sequence, or if {@code codePointOffset} is positive and the subsequence starting
     *     with {@code index} has fewer than {@code codePointOffset} code points, or if
     *     {@code codePointOffset} is negative and the subsequence before {@code index} has fewer
     *     than the absolute value of {@code codePointOffset} code points.
     */
    public int offsetByCodePoints(int index, int codePointOffset) {
        return Character.offsetByCodePoints(chars, 0, count, index, codePointOffset);
    }
    
    /**
     * Characters are copied from this sequence into the destination character array {@code dst}.
     * The first character to be copied is at index {@code srcBegin}; the last character to be
     * copied is at index {@code srcEnd-1}. The total number of characters to be copied is
     * {@code srcEnd-srcBegin}. The characters are copied into the subarray of {@code dst} starting
     * at index {@code dstBegin} and ending at index:
     * 
     * <pre>
     * {@code dstBegin + (srcEnd-srcBegin) - 1}
     * </pre>
     * 
     * @param srcBegin start copying at this offset.
     * @param srcEnd stop copying at this offset.
     * @param dst the array to copy the data into.
     * @param dstBegin offset into {@code dst}.
     * @throws IndexOutOfBoundsException if any of the following is true:
     *     <ul>
     *     <li>{@code srcBegin} is negative
     *     <li>{@code dstBegin} is negative
     *     <li>the {@code srcBegin} argument is greater than the {@code srcEnd} argument.
     *     <li>{@code srcEnd} is greater than {@code this.length()}.
     *     <li>{@code dstBegin+srcEnd-srcBegin} is greater than {@code dst.length}
     *     </ul>
     */
    public void getChars(int srcBegin, int srcEnd, char[] dst, int dstBegin) {
        Objects.checkFromToIndex(srcBegin, srcEnd, count);
        Objects.checkFromIndexSize(dstBegin, srcEnd - srcBegin, dst.length);
        
        System.arraycopy(chars, srcBegin, dst, dstBegin, srcEnd - srcBegin);
    }
    
    /**
     * The character at the specified index is set to {@code ch}. This sequence is altered to
     * represent a new character sequence that is identical to the old character sequence, except
     * that it contains the character {@code ch} at position {@code index}.
     * <p>
     * The index argument must be greater than or equal to {@code 0}, and less than the length of
     * this sequence.
     * 
     * @param index the index of the character to modify.
     * @param ch the new character.
     * @throws IndexOutOfBoundsException if {@code index} is negative or greater than or equal to
     *     {@code length()}.
     */
    public void setCharAt(int index, char ch) {
        Objects.checkIndex(index, count);
        chars[index] = ch;
    }
    
    /**
     * Returns a new {@code String} that contains a subsequence of characters currently contained in
     * this character sequence. The substring begins at the specified index and extends to the end
     * of this sequence.
     * 
     * @param start The beginning index, inclusive.
     * @return The new string.
     * @throws StringIndexOutOfBoundsException if {@code start} is less than zero, or greater than
     *     the length of this object.
     */
    public String substring(int start) {
        return this.substring(start, count);
    }
    
    /**
     * Returns a new character sequence that is a subsequence of this sequence.
     * <p>
     * An invocation of this method of the form
     * 
     * <pre>
     * {@code sb.subSequence(begin, end)}
     * </pre>
     * 
     * behaves in exactly the same way as the invocation
     * 
     * <pre>
     * {@code sb.substring(begin, end)}
     * </pre>
     * 
     * This method is provided so that this class can implement the {@code CharSequence} interface.
     * 
     * @param start the start index, inclusive.
     * @param end the end index, exclusive.
     * @return the specified subsequence.
     * @throws IndexOutOfBoundsException if {@code start} or {@code end} are negative, if
     *     {@code end} is greater than {@code length()}, or if {@code start} is greater than
     *     {@code end}
     */
    @Override
    public CharSequence subSequence(int start, int end) {
        return this.substring(start, end);
    }
    
    /**
     * Returns a new {@code String} that contains a subsequence of characters currently contained in
     * this sequence. The substring begins at the specified {@code start} and extends to the
     * character at index {@code end - 1}.
     * 
     * @param start The beginning index, inclusive.
     * @param end The ending index, exclusive.
     * @return The new string.
     * @throws StringIndexOutOfBoundsException if {@code start} or {@code end} are negative or
     *     greater than {@code length()}, or {@code start} is greater than {@code end}.
     */
    public String substring(int start, int end) {
        if (start < 0 || start > end) {
            throw new StringIndexOutOfBoundsException(start);
        }
        if (end > count) {
            throw new StringIndexOutOfBoundsException(end);
        }
        return String.valueOf(chars, start, end - start);
    }
    
    /**
     * Returns a stream of {@code int} zero-extending the {@code char} values from this sequence.
     * Any {@code char} which maps to a surrogate code point is passed through uninterpreted.
     * <p>
     * The stream binds to this sequence when the terminal stream operation commences (specifically,
     * for mutable sequences the spliterater for the stream is <em>late-binding</em>). If the
     * sequence is modified during that operation then the result is undefined.
     * 
     * @return an {@code IntStream} of {@code char} values from this sequence
     */
    @Override
    public IntStream chars() {
        final int characteristics = Spliterator.ORDERED | Spliterator.SIZED | Spliterator.SUBSIZED
                | Spliterator.NONNULL;
        final class CharacterSpliterator extends Spliterators.AbstractIntSpliterator implements Spliterator.OfInt {
            int currentIndex = 0;
            
            CharacterSpliterator() {
                super(count, characteristics);
            }
            
            /**
             * If a remaining element exists, performs the given action on it, returning
             * {@code true}; else returns {@code false}. If this Spliterator is
             * {@link java.util.Spliterator#ORDERED} the action is performed on the next element in
             * encounter order. Exceptions thrown by the action are relayed to the caller.
             * 
             * @param action The action
             * @return {@code false} if no remaining elements existed upon entry to this method,
             * else {@code true}.
             * @throws NullPointerException if the specified action is null
             */
            @Override
            public boolean tryAdvance(IntConsumer action) {
                Objects.requireNonNull(action);
                
                if (currentIndex < count) {
                    action.accept(chars[currentIndex]);
                    return true;
                } else {
                    return false;
                }
            }
            
        }
        // TODO check interface description
        return StreamSupport.intStream(CharacterSpliterator::new, characteristics, false);
    }
    
    /**
     * Returns a stream of code point values from this sequence. Any surrogate pairs encountered in
     * the sequence are combined as if by {@code Character.toCodePoint} and the result is passed to
     * the stream. Any other code units, including ordinary BMP characters, unpaired surrogates, and
     * undefined code units, are zero-extended to {@code int} values which are then passed to the
     * stream.
     * <p>
     * The stream binds to this sequence when the terminal stream operation commences (specifically,
     * for mutable sequences the spliterater for the stream is <em>late-binding</em>). If the
     * sequence is modified during that operation then the result is undefined.
     * 
     * @return an {@code IntStream} of Unicode code points from this sequence
     */
    @Override
    public IntStream codePoints() {
        
        final int characteristics = Spliterator.ORDERED | Spliterator.SIZED | Spliterator.SUBSIZED
                | Spliterator.NONNULL;
        final class CodePointSpliterator extends Spliterators.AbstractIntSpliterator implements Spliterator.OfInt {
            int currentIndex = 0;
            
            CodePointSpliterator() {
                super(MyStringBuilder.this.codePointCount(0, count), characteristics);
            }
            
            /**
             * If a remaining element exists, performs the given action on it, returning
             * {@code true}; else returns {@code false}. If this Spliterator is
             * {@link java.util.Spliterator#ORDERED} the action is performed on the next element in
             * encounter order. Exceptions thrown by the action are relayed to the caller.
             * 
             * @param action The action
             * @return {@code false} if no remaining elements existed upon entry to this method,
             * else {@code true}.
             * @throws NullPointerException if the specified action is null
             */
            @Override
            public boolean tryAdvance(IntConsumer action) {
                Objects.requireNonNull(action);
                if (currentIndex < count) {
                    char c1 = chars[currentIndex];
                    if (Character.isHighSurrogate(c1) && currentIndex < count - 1
                            && Character.isLowSurrogate(chars[currentIndex + 1])) {
                        int codePoint = Character.toCodePoint(c1, chars[currentIndex + 1]);
                        action.accept(codePoint);
                    } else {
                        action.accept(c1);
                    }
                    currentIndex++;
                    return true;
                } else {
                    return false;
                }
            }
            
        }
        // TODO check interface description
        // TODO implement char spliterator to create a stream from, using "supplier" for
        // late-binding
        return StreamSupport.intStream(CodePointSpliterator::new, characteristics, false);
    }
    
    /**
     * Returns a string representing the data in this sequence. A new {@code String} object is
     * allocated and initialized to contain the character sequence currently represented by this
     * object. This {@code String} is then returned. Subsequent changes to this sequence do not
     * affect the contents of the {@code String}.
     * 
     * @return a string representation of this sequence of characters.
     */
    @Override
    public String toString() {
        return String.valueOf(chars, 0, count);
    }
}
// TODO consolidate exception handling (especially StringIOOBE)
// TODO tests, tests, tests!
// TODO Serializable?
