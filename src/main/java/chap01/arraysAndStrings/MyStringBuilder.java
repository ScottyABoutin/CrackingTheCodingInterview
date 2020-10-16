package chap01.arraysAndStrings;

import java.util.Arrays;
import java.util.stream.IntStream;

/* A mutable sequence of characters.
 * The principal operations on a MyStringBuilder are the append and insert methods, which are
 * overloaded so as to accept data of any type. Each effectively converts a given datum to a string
 * and then appends or inserts the characters of that string to the string builder. The append
 * method always adds these characters at the end of the builder; the insert method adds the
 * characters at a specified point.
 * 
 * For example, if z refers to a MyStringBuilder whose current contents are "start", then the
 * method call z.append("le") would cause the MyStringBuilder to contain "startle", whereas
 * z.insert(4, "le") would alter the MyStringBuilder to contain "starlet".
 * 
 * In general, sb.append(x) has the same effect as sb.insert(sb.length(), x).
 * 
 * Every MyStringBuilder has a capacity. As long as the length of the character sequence contained
 * in the MyStringBuilder does not exceed the capacity, it is not necessary to allocate a new
 * internal buffer. If the internal buffer overflows, it is automatically made larger.
 * 
 * Instances of MyStringBuilder are not safe for use by multiple threads. If such synchronization is
 * required then it is recommended that java.lang.StringBuffer be used.
 * 
 * Unless otherwise noted, passing a null argument to a constructor or method in this class will
 * cause a NullPointerException to be thrown.
 */
public final class MyStringBuilder implements Appendable, CharSequence, Comparable<MyStringBuilder> {
    
    /* Simplest implementation, but could later be byte[] depending on letters */
    private char[] chars;
    
    /*
     * Array keeps track of capacity. Length keeps track of the length of the sequence. Invariant:
     * count <= letters.length
     */
    private int count;
    
    /* No characters, initial capacity of 16 characters */
    public MyStringBuilder() {
        this(16);
    }
    
    /*
     * No characters, initial capacity as specified.
     * 
     * @param capacity the initial capacity.
     * 
     * @throws NegativeArraySizeException if the capacity argument is less than 0.
     */
    public MyStringBuilder(int capacity) {
        chars = new char[capacity];
    }
    
    /*
     * Initializes to the contents of the provided string. Initial capacity is length of the string
     * arugment + 16.
     * 
     * @param str the initial contents
     */
    public MyStringBuilder(String str) {
        this(str.length() + 16);
        
        this.append(str);
    }
    
    /*
     * Initializes to the contents of the provided CharSequence. Initial capacity is the length of
     * the sequence + 16.
     * 
     * @param seq the initial contents
     */
    public MyStringBuilder(CharSequence seq) {
        this(seq.length() + 16);
        
        this.append(seq);
    }
    
    /*
     * Compares two StringBuilder instances lexicographically. The lexicographical ordering of
     * {@code CharSequence} is defined as follows. Consider a StringBuilder builder of length len to
     * be a sequence of char values, builder[0] to builder[len-1]. Suppose k is the lowest index at
     * which the corresponding char values from each sequence differ. The lexicographic ordering of
     * the sequences is determined by a numeric comparison of the char values builder1[k] with
     * builder2[k]. If there is no such index k, the shorter sequence is considered
     * lexicographically less than the other. If the sequences have the same length, the sequences
     * are considered lexicographically equal.
     * 
     * @param another the StringBuilder to be compared with
     * 
     * @return the value 0 if this StringBuilder contains the same character sequence as that of the
     * argument StringBuilder a negative integer if this {@code StringBuilder} is lexicographically
     * less than the {@code StringBuilder} argument; or a positive integer if this {@code
     * StringBuilder} is lexicographically greater than the {@code StringBuilder} argument.
     */
    @Override
    public int compareTo(MyStringBuilder another) {
        int toIterate = Math.min(count, another.count);
        for (int i = 0; i < toIterate; i++) {
            if (chars[i] < another.chars[i]) {
                return -1;
            } else if (chars[i] > another.chars[i]) {
                return 1;
            }
        }
        // Since all checked letters are equal, return based off of length
        if (count < another.count) {
            return -1;
        } else if (count > another.count) {
            return 1;
        } else {
            return 0;
        }
        
    }
    
    /*
     * Appends the string representation of the Object argument. The overall effect is exactly as if
     * the argument were converted to a string by the method String.valueOf(Object), and the
     * characters of that string were then appended to this character sequence.
     * 
     * @param obj an Object.
     * 
     * @return a reference to this object.
     */
    public MyStringBuilder append(Object obj) {
        return this.append(String.valueOf(obj));
    }
    
    /*
     * Appends the specified string to this character sequence. The characters of the String
     * argument are appended, in order, increasing the length of this sequence by the length of the
     * argument. If str is null, then the four characters "null" are appended. Let n be the length
     * of this character sequence just prior to execution of the append method. Then the character
     * at index k in the new character sequence is equal to the character at index k in the old
     * character sequence, if k is less than n; otherwise, it is equal to the character at index k-n
     * in the argument str.
     * 
     * @param str a string.
     * 
     * @return a reference to this object.
     */
    public MyStringBuilder append(String str) {
        if (str == null) {
            return this.appendNull();
        }
        this.ensureCapacity(count + str.length());
        for (int i = 0; i < str.length(); i++) {
            chars[i + count] = str.charAt(i);
        }
        count += str.length();
        return this;
    }
    
    /*
     * Appends the specified StringBuffer to this sequence. The characters of the StringBuffer
     * argument are appended, in order, to this sequence, increasing the length of this sequence by
     * the length of the argument. If sb is null, then the four characters "null" are appended to
     * this sequence. Let n be the length of this character sequence just prior to execution of the
     * append method. Then the character at index k in the new character sequence is equal to the
     * character at index k in the old character sequence, if k is less than n; otherwise, it is
     * equal to the character at index k-n in the argument sb.
     * 
     * @param sb the StringBuffer to append.
     * 
     * @return a reference to this object.
     */
    public MyStringBuilder append(StringBuffer sb) {
        if (sb == null) {
            return this.appendNull();
        } else {
            synchronized (sb) {
                return this.append(sb.toString());
            }
        }
    }
    
    /*
     * Description copied from interface: Appendable Appends the specified character sequence to
     * this Appendable. Depending on which class implements the character sequence, the entire
     * sequence may not be appended. For instance, if s is a CharBuffer then the subsequence to
     * append is defined by the buffer's position and limit. Specified by: append in interface
     * Appendable
     * 
     * @param s The character sequence to append. If s is null, then the four characters "null" are
     * appended to this Appendable.
     * 
     * @return A reference to this Appendable
     */
    public MyStringBuilder append(CharSequence s) {
        return this.append((Object) s);
    }
    
    /*
     * Appends a subsequence of the specified CharSequence to this sequence. Characters of the
     * argument s, starting at index start, are appended, in order, to the contents of this sequence
     * up to the (exclusive) index end. The length of this sequence is increased by the value of end
     * - start. Let n be the length of this character sequence just prior to execution of the append
     * method. Then the character at index k in this character sequence becomes equal to the
     * character at index k in this sequence, if k is less than n; otherwise, it is equal to the
     * character at index k+start-n in the argument s. If s is null, then this method appends
     * characters as if the s parameter was a sequence containing the four characters "null".
     * Specified by: append in interface Appendable
     * 
     * @param s the sequence to append.
     * 
     * @param start the starting index of the subsequence to be appended.
     * 
     * @param end the end index of the subsequence to be appended.
     * 
     * @return a reference to this object.
     * 
     * @throws IndexOutOfBoundsException if start is negative, or start is greater than end or end
     * is greater than s.length()
     */
    public MyStringBuilder append(CharSequence s, int start, int end) {
        // TODO Objects method?
        if (start < 0 || start > end || (s != null && end > s.length())) {
            throw new IndexOutOfBoundsException("start " + start + ", end " + end + ", length " + count);
        }
        if (s == null) {
            return this.appendNull();
        } else {
            return this.append(s.subSequence(start, end));
        }
    }
    
    /*
     * Appends the string representation of the char array argument to this sequence. The characters
     * of the array argument are appended, in order, to the contents of this sequence. The length of
     * this sequence increases by the length of the argument. The overall effect is exactly as if
     * the argument were converted to a string by the method String.valueOf(char[]), and the
     * characters of that string were then appended to this character sequence.
     * 
     * @param str the characters to be appended.
     * 
     * @return a reference to this object.
     */
    public MyStringBuilder append(char[] str) {
        return this.append(String.valueOf(str));
    }
    
    /*
     * Appends the string representation of a subarray of the char array argument to this sequence.
     * Characters of the char array str, starting at index offset, are appended, in order, to the
     * contents of this sequence. The length of this sequence increases by the value of len. The
     * overall effect is exactly as if the arguments were converted to a string by the method
     * String.valueOf(char[],int,int), and the characters of that string were then appended to this
     * character sequence.
     * 
     * @param str the characters to be appended.
     * 
     * @param offset the index of the first char to append.
     * 
     * @param len the number of chars to append.
     * 
     * @return a reference to this object.
     * 
     * @throws IndexOutOfBoundsException if offset < 0 or len < 0 or offset+len > str.length
     */
    public MyStringBuilder append(char[] str, int offset, int len) {
        return this.append(String.valueOf(str, offset, len));
    }
    
    private MyStringBuilder appendNull() {
        return this.append("null");
    }
    
    /*
     * Appends the string representation of the boolean argument to the sequence. The overall effect
     * is exactly as if the argument were converted to a string by the method
     * String.valueOf(boolean), and the characters of that string were then appended to this
     * character sequence.
     * 
     * @param b a boolean.
     * 
     * @return a reference to this object.
     */
    public MyStringBuilder append(boolean b) {
        return this.append(String.valueOf(b));
    }
    
    /*
     * Appends the string representation of the char argument to this sequence. The argument is
     * appended to the contents of this sequence. The length of this sequence increases by 1. The
     * overall effect is exactly as if the argument were converted to a string by the method
     * String.valueOf(char), and the character in that string were then appended to this character
     * sequence. Specified by: append in interface Appendable
     * 
     * @param c a char.
     * 
     * @return a reference to this object.
     */
    public MyStringBuilder append(char c) {
        return this.append(String.valueOf(c));
    }
    
    public MyStringBuilder append(int i) {
        return this.append(String.valueOf(i));
    }
    
    public MyStringBuilder append(long lng) {
        return this.append(String.valueOf(lng));
    }
    
    public MyStringBuilder append(float f) {
        return this.append(String.valueOf(f));
    }
    
    public MyStringBuilder append(double d) {
        return this.append(String.valueOf(d));
    }
    
    public MyStringBuilder appendCodePoint(int codePoint) {
        // TODO check interface description
        return null;
    }
    
    public MyStringBuilder delete(int start, int end) {
        // TODO check interface description
        return null;
    }
    
    public MyStringBuilder deleteCharAt(int index) {
        // TODO check interface description
        return null;
    }
    
    public MyStringBuilder replace(int start, int end, String str) {
        // TODO check interface description
        return null;
    }
    
    public String substring(int start) {
        // TODO check interface description
        return null;
    }
    
    public String substring(int start, int end) {
        // TODO check interface description
        return null;
    }
    
    public CharSequence subSequence(int start, int end) {
        // TODO check interface description
        return null;
    }
    
    public MyStringBuilder insert(int index, char[] str, int offset, int len) {
        // TODO check interface description
        return null;
    }
    
    public MyStringBuilder insert(int offset, Object obj) {
        // TODO check interface description
        return null;
    }
    
    public MyStringBuilder insert(int offset, String str) {
        // TODO check interface description
        return null;
    }
    
    public MyStringBuilder insert(int offset, char[] str) {
        // TODO check interface description
        return null;
    }
    
    public MyStringBuilder insert(int dstOffset, CharSequence s) {
        // TODO check interface description
        return null;
    }
    
    public MyStringBuilder insert(int dstOffset, CharSequence s, int start, int end) {
        // TODO check interface description
        return null;
    }
    
    public MyStringBuilder insert(int offset, boolean b) {
        // TODO check interface description
        return null;
    }
    
    public MyStringBuilder insert(int offset, char c) {
        // TODO check interface description
        return null;
    }
    
    public MyStringBuilder insert(int offset, int i) {
        // TODO check interface description
        return null;
    }
    
    public MyStringBuilder insert(int offset, long l) {
        // TODO check interface description
        return null;
    }
    
    public MyStringBuilder insert(int offset, float f) {
        // TODO check interface description
        return null;
    }
    
    public MyStringBuilder insert(int offset, double d) {
        // TODO check interface description
        return null;
    }
    
    public int indexOf(String str) {
        // TODO check interface description
        return -1;
    }
    
    public int indexOf(String str, int fromIndex) {
        // TODO check interface description
        return -1;
    }
    
    public int lastIndexOf(String str) {
        // TODO check interface description
        return -1;
    }
    
    public int lastIndexOf(String str, int fromIndex) {
        // TODO check interface description
        return -1;
    }
    
    public MyStringBuilder reverse() {
        // TODO check interface description
        return null;
    }
    
    /*
     * Returns the length (character count).
     *
     * @return the length of the sequence of characters currently represented by this object
     */
    public int length() {
        return count;
    }
    
    /*
     * Returns the current capacity. The capacity is the amount of storage available for newly
     * inserted characters, beyond which an allocation will occur.
     * 
     * @return the current capacity
     */
    public int capacity() {
        return chars.length;
    }
    
    /*
     * Ensures that the capacity is at least equal to the specified minimum. If the current capacity
     * is less than the argument, then a new internal array is allocated with greater capacity. The
     * new capacity is the larger of: 1. The minimumCapacity argument. 2. Twice the old capacity,
     * plus 2. If the minimumCapacity argument is nonpositive, this method takes no action and
     * simply returns. Note that subsequent operations on this object can reduce the actual capacity
     * below that requested here.
     * 
     * @param minimumCapacity the minimum desired capacity.
     */
    public void ensureCapacity(int minimumCapacity) {
        if (minimumCapacity > 0) {
            if (minimumCapacity - chars.length > 0) {
                ; // TODO
            }
        }
    }
    
    /*
     * Attempts to reduce storage used for the character sequence. If the storage is larger than
     * necessary to hold its current sequence of characters, then it may be resized to become more
     * space efficient. Calling this method may, but is not required to, affect the value returned
     * by a subsequent call to the capacity() method.
     */
    public void trimToSize() {
        // TODO trim
        if (count < chars.length) {
            chars = Arrays.copyOf(chars, count);
        }
    }
    
    /*
     * Sets the length of the character sequence. The sequence is changed to a new character
     * sequence whose length is specified by the argument. For every nonnegative index k less than
     * newLength, the character at index k in the new character sequence is the same as the
     * character at index k in the old sequence if k is less than the length of the old character
     * sequence; otherwise, it is the null character {@code '\u005Cu0000'}.
     *
     * In other words, if the {@code newLength} argument is less than the current length, the length
     * is changed to the specified length. <p> If the {@code newLength} argument is greater than or
     * equal to the current length, sufficient null characters ({@code '\u005Cu0000'}) are appended
     * so that length becomes the {@code newLength} argument. <p> The {@code newLength} argument
     * must be greater than or equal to {@code 0}.
     *
     * @param newLength the new length
     * 
     * @throws IndexOutOfBoundsException if the {@code newLength} argument is negative.
     */
    public void setLength(int newLength) {
        // TODO check interface description
    }
    
    public char charAt(int index) {
        // TODO check interface description
        return '\u0000';
    }
    
    public int codePointAt(int index) {
        // TODO check interface description
        return -1;
    }
    
    public int codePointBefore(int index) {
        // TODO check interface description
        return -1;
    }
    
    public int codePointCount(int beginIndex, int endIndex) {
        // TODO check interface description
        return -1;
    }
    
    public int offsetByCodePoints(int index, int codePointOffset) {
        // TODO check interface description
        return -1;
    }
    
    public void getChars(int srcBegin, int srcEnd, char[] dst, int dstBegin) {
        // TODO check interface description
    }
    
    public void setCharAt(int index, char ch) {
        // TODO check interface description
    }
    
    @Override
    public String toString() {
        // TODO check interface description
        return null;
    }
    
    @Override
    public IntStream chars() {
        // TODO check interface description
        return null;
    }
    
    @Override
    public IntStream codePoints() {
        // TODO check interface description
        return null;
    }
}
