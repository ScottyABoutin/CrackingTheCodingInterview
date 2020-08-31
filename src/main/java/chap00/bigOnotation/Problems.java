package chap00.bigOnotation;

import java.util.Arrays;

public class Problems {
    // VI.1 - The following code computes the product of a and b. What is its runtime?
    int product(int a, int b) {
        int sum = 0;
        for (int i = 0; i < b; i++) {
            sum += a;
        }
        return sum;
    }
    
    /* Because this scales with the size of b, this is O(b). As a note, an optimization would be
     * to always have "a" be the lesser value with a check at the beginning.
     */
    
    // VI.2 - The following computes a^b. What is its runtime?
    int power(int a, int b) {
        if (b < 0) {
            return 0; // error
        } else if (b == 0) {
            return 1;
        } else {
            return a * power(a, b - 1);
        }
    }
    
    /* Even though this is recursive, there are still only b calls that go on the stack. Therefore,
     * this is O(b).
     */
    
    // VI.3 - The following code computes a % b. What is its runtime?
    int mod(int a, int b) {
        if (b <= 0) {
            return -1; // Not accurate, but whatever, book.
        }
        int div = a / b;
        return a - div * b;
    }
    
    /* This is O(1). There is only constant work done here.
     */
    
    // VI.4 - The following code performs integer division. What is its runtime (assume a and b
    //        are both positive)?
    int div(int a, int b) {
        int count = 0;
        int sum = b;
        while (sum <= a) {
            sum += b;
            count++;
        }
        return count;
    }
    
    /* This iterates a number of times proportional to a/b, so the runtime is (ironically) O(a/b).
     */
    
    // VI.5 - The following code computes the [integer] square root of a number. If the number is
    //        not a perfect square (there is no integer square root), then it returns -1. It does
    //        this by successive guessing. If n is 100, it first guesses 50. Too high? Try
    //        something lower - halfway between 1 and 50. What is its runtime?
    int sqrt(int n) {
        return sqrt_helper(n, 1, n);
    }
    
    int sqrt_helper(int n, int min, int max) {
        if (max < min) return -1; // return no square root
        
        int guess = (min + max) / 2;
        if (guess * guess == n) { // found it!
            return guess;
        } else if (guess * guess < n) { // too low
            return sqrt_helper(n, guess + 1, max); // try higher
        } else { // too high
            return sqrt_helper(n, min, guess - 1); // try lower
        }
    }
    
    /* Because this converges towards the number little by little (as a binary search), this should
     * take a logarithmic number of operations to reach the answer. Thus, this is O(log n).
     */
    
    // VI.6 - The following code computes the [integer] square root of a number. If the number is
    //        not a perfect square (there is no integer square root), then it returns -1. It does
    //        this by trying increasingly large numbers until it finds the right value (or is too
    //        high). What is its runtime?
    int sqrt2(int n) {
        for (int guess = 1; guess * guess <= n; guess++) {
            if (guess * guess == n) {
                return guess;
            }
        }
        return -1;
    }
    
    /* This will only iterate up until guess * guess > n, which means until the square root of n.
     * Thus, this is O(sqrt(n)), which is not as good as O(log n).
     */
    
    // VI.7 - If a binary search tree is not balanced, how long might it take (worst case) to find
    //        an element in it?
    
    /* Worst case, this would have to search through every element. Thus, it changes the runtime
     * from O(log n) to O(n).
     */
    
    // VI.8 - You are looking for a specific value in a binary tree, but the tree is not a binary
    //        search tree. What is the time complexity of this?
    
    /* This is O(n) as well. This has no guarantee of ordering, so it has the potential to search
     * every element in the tree.
     */
    
    // VI.9 - The appendToNew method appends a value to an array by creating a new, longer array
    //        and returning this longer array. You've used the appendToNew method to create a
    //        copyArray function that repeatedly calls appendToNew. How long does copying an array
    //        take?
    int[] copyArray(int[] array) {
        int[] copy = new int[0];
        for(int value : array) {
            copy = appendToNew(copy, value);
        }
        return copy;
    }
    
    int[] appendToNew(int[] array, int value) {
        // copy all elements over to new array
        int[] bigger = new int[array.length - 1];
        for (int i = 0; i < array.length; i++) {
            bigger[i] = array[i];
        }
        
        // add new element
        bigger[bigger.length - 1] = value;
        return bigger;
    }
    
    /* Each copy takes the size operations. Thus, each append has 1 + 2 + 3 + 4... to the size,
     * meaning that this is O(n^2).
     */
    
    // VI.10 - The following code sums the digits in a number. What is its big O time?
    int sumDigits(int n) {
        int sum = 0;
        while (n > 0) {
            sum += n % 10;
            n /= 10;
        }
        return sum;
    }
    
    /* Because the size of a number proportional to its number of digits is essentially a log10
     * operation, this grows at O(log n) time.
     */
    
    // VI.11 - The following code prints all strings of length k where the characters are in sorted
    //         order. It does this by generating all strings of length k and then checking if each
    //         is sorted. What is its runtime? (I have flipped prefix & remaining for readability).
    
    int numChars = 26;
    
    void printSortedStrings(int remaining) {
        printSortedStrings("", remaining);
    }
    
    void printSortedStrings(String prefix, int remaining) {
        if (remaining == 0) {
            if (isInOrder(prefix)) {
                System.out.println(prefix);
            }
        } else {
            for (int i = 0; i < numChars; i++) {
                char c = ithLetter(i);
                printSortedStrings(prefix + c, remaining - 1);
            }
        }
    }
    
    boolean isInOrder(String s) {
        for (int i = 1; i < s.length(); i++) {
            int prev = ithLetter(s.charAt(i - 1));
            int curr = ithLetter(s.charAt(i));
            if (prev > curr) {
                return false;
            }
        }
        return true;
    }
    
    char ithLetter(int i) {
        return (char) ('a' + i);
    }
    
    /* This is a doozy. What we need to do is analyze each method, and then they can be analyzed in
     * the context of each other. We'll work our way from the bottom up.
     * ithLetter: O(1). This is math.
     * isInOrder: O(s) for argument length s. This looks through each combination of adjacent
     *                  letters, using a O(1) operation twice s - 1 times (shortcutting a return
     *                  where appropriate).
     * printSortedString(int): This is the basis, and the int is String length k that we are
     *                  comparing. Return to this.
     * printSortedString(String, int): if remaining = 0 (which is only at length k), do O(k)
     *                  operation (isInOrder), then print (also O(k), likely). Thus, O(k) for end.
     *                  However, otherwise go through all 26 characters (O(1), because constants).
     *                  For each character, call this function with current k - 1, and prefix 1
     *                  larger. Thus, for each letter (26 in total), calculate 26 leaves for each,
     *                  up to size k. In total, then, we have k * 26^k.
     */
    
    // VI.12 - The following code computes the intersection (the number of elements in common) of
    //         two arrays. It assumes that neither array has duplicates. It computes the
    //         intersection by sorting on array (array b) and then iterating through a checking
    //         (via binary search) if each value is in b. What is its runtime?
    int intersection(int[] a, int[] b) {
        mergesort(b);
        int intersect = 0;
        
        for (int x : a) {
            if (binarySearch(b, x) >= 0) {
                intersect++;
            }
        }
        
        return intersect;
    }
    
    // O(n log n)
    void mergesort(int[] array) {
        mergesort(array, 0, array.length);
    }
    
    void mergesort(int[] array, int low, int high) {
        if (low + 1 < high) {
            int mid = (low + high) / 2;
            mergesort(array, low, mid);
            mergesort(array, mid, high);
            merge(array, low, mid, high);
        }
    }
    
    void merge(int[] array, int low, int mid, int high) {
        int[] leftHalf = Arrays.copyOfRange(array, low, mid);
        int[] rightHalf = Arrays.copyOfRange(array, mid, high);
        int left = 0;
        int right = 0;
        int i = low;
        while (left < leftHalf.length && right < rightHalf.length) {
            if (leftHalf[left] <= rightHalf[right]) {
                array[i] = leftHalf[left];
                left++;
            } else {
                array[i] = rightHalf[right];
                right++;
            }
            i++;
        }
        while (left < leftHalf.length) {
            array[i] = leftHalf[left];
            left++;
            i++;
        }
        while (right < rightHalf.length) {
            array[i] = rightHalf[right];
            right++;
            i++;
        }
    }
    
    // O(log n)
    int binarySearch(int[] array, int key) {
        return binarySearch(array, key, 0, array.length);
    }
    
    int binarySearch(int[] array, int key, int low, int high) {
        if (low > high) {
            return -1;
        }
        int index = (low + high) / 2;
        if (array[index] > key) {
            return binarySearch(array, key, low, index);
        } else if (array[index] < key) {
            return binarySearch(array, key, index, high);
        } else {
            return index;
        }
    }
    
    /* 
     * The first operation is mergesorting b, which is proportional to b's length: O(b log b).
     * Next, for each element in a, we do a binary search (O(log b)). Together, this is O(a log b).
     * There is no way to simplify this, so the answer is O(b log b) + O(a log b), or
     * O((a + b) log b).
     */
}
