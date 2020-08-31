package chap00.bigOnotation;

public class Exercises {
    
    // Example 1 - What is the runtime of the below code?
    void foo(int[] array) {
        int sum = 0;
        int product = 1;
        for (int i = 0; i < array.length; i++) {
            sum += array[i];
        }
        for (int i = 0; i < array.length; i++) {
            product *= array[i];
        }
        System.out.println(sum + ", " + product);
    }
    
    /* This will take O(n) time. Even though we iterate through the array twice, leaving 2n
     * operations, we reduce the constant factor and have an O(n) runtime.
     */
    
    // Example 2 - What is the runtime of the below code?
    void printPairs(int[] array) {
        for (int i = 0; i < array.length; i++) {
            for (int j = 0; j < array.length; j++) {
                System.out.println(array[i] + "," + array[j]);
            }
        }
    }
    
    /* This will take O(n^2) time. For each item in the array, we pair it with each other element
     * of the array, including itself. This means that we have a "grid" of those combinations, with
     * n rows and n columns, which gives us n*n elements. Or, since the inner loop has n operations
     * and runs n times, we have n*n total operations.
     */
    
    // Example 3 - This is very similar code to the above example, but now the inner for loop
    //             starts at i + 1.
    void printUnorderedPairs(int[] array) {
        for (int i = 0; i < array.length; i++) {
            for (int j = i + 1; j < array.length; j++) {
                System.out.println(array[i] + "," + array[j]);
            }
        }
    }
    
    /* Each inner loop takes successively 1 less operation. Thus, for an array of 5 elements, we
     * print out 4, then 3, then 2, then 1, then 0. Putting all of this together, we end up with
     * sum(0,n-1) operations total. This evaluates to (n-1)(n-2)/2, which generalizes to O(n^2)
     * runtime. This obviously runs faster than example 2, but the asymptotic analysis means that
     * both grow in a similar rate: that is, O(n^2).
     * 
     * From a visualization perspective, you would end up with roughly half of an n x n matrix.
     * This is n^2/2, which factors to O(n^2).
     * 
     * Or, you can look at the average case of this. Since the average line printed is n/2 items
     * long (the inner loop) and the outer loop does n operations, this evaluates to n^2/2, or
     * O(n^2).
     */
    
    // Example 4 - This is similar to the above, but now we have two arrays.
    void printUnorderedPairs(int[] arrayA, int[] arrayB) {
        for (int i = 0; i < arrayA.length; i++) {
            for (int j = 0; j < arrayB.length; j++) {
                if (arrayA[i] < arrayB[j]) {
                    System.out.println(arrayA[i] + "," + arrayB[j]);
                }
            }
        }
    }
    
    /* Firstly, the if statement in these nested loops will not add to the time complexity, as it
     * is an O(1) operation. Thus, if a = arrayA.length and b = arrayB.length, for a iterations,
     * we have b operations. Thus, the time complexity is O(ab). Note that there are two sources of
     * data, and the complexity is affected by the combination of both: it is NOT O(n^2). If a
     * grows and b doesn't, that is a different rate of growth from an O(n^2) function.
     */
    
    // Example 5 - What about this strange bit of code?
    void printUnorderedPairs2(int[] arrayA, int[] arrayB) {
        for (int i = 0; i < arrayA.length; i++) {
            for (int j = 0; j < arrayB.length; j++) {
                for (int k = 0; k < 100_000; k++) {
                    System.out.println(arrayA[i] + "," + arrayB[j]);
                }
            }
        }
    }
    
    /* While the innermost (k) loop is obviously expensive, it is also a constant-time expression,
     * and will not change based off of the input array lengths. Thus, this is effectively the same
     * problem as the example above. For each item a in arrayA, we process each b item in arrayB,
     * pairs are printed out 100_000 times. This simplifies to O(ab).
     */
    
    // Example 6 - The following code reverses an array. What is its runtime?
    void reverse(int[] array) {
        for (int i = 0; i < array.length / 2; i++) {
            int other = array.length - i - 1;
            int temp = array[i];
            array[i] = array[other];
            array[other] = temp;
        }
    }
    
    /* This has ~n/2 iterations of the loop, followed by a constant time in the 4 operations. Thus,
     * this parses out to be O(n) time.
     */
    
    // Example 7 - Which of the following are equivalent to O(N)? Why?
    
    /* a. O(N + P), where P < N/2
     * b. O(2N)
     * c. O(N + log N)
     * d. O(N + M)
     * 
     * a. We have some information about P and its relationship with N. Since P is less than half
     *    of N, it is factored out. As a proof, if N were always equal to P, then O(N + P) would
     *    equal O(2N), which becomes O(N). Because P is less than a factor of N, then it can be
     *    factored out of our big O operations, and it equates to O(n).
     * b. Because the 2 is a constant, it multiplying with N lets us factor it out (similar to how
     *    100 operations every time is constant). Thus, this equates to O(n).
     * c. Similar to a., because log n is a factor of n and is being added, it can be factored out,
     *    leaving O(n).
     * d. Because there is no relationship between the values of N and M, we have no way of
     *    comparing their scaling with each other. Thus, this cannot be simplified, and remains as
     *    O(N + M).
     * 
     * Conclusions: a-c are equivalent to O(N). When data is all related to a similar factor "N",
     * it can generally be reduced in some capacity, either by removal or by combining it (e.g. by
     * multiplication).
     */
    
    // Example 8 - Suppose we had an algorithm that took in an array of strings, sorted each
    //             string, and then sorted the full array. What would the runtime be?
    
    /* Firstly, sorting, in general, have a time complexity of N log N related to the size of the
     * collection being sorted. With that in mind, we have to take into consideration that the
     * sizes of each individual string has less to do with the size of the array (although
     * determining sorting order of strings does have SOME impact). Thus, we must consider 2
     * factors: the size of the array (we'll call it 'a') and the average size of the string ('s').
     * We could also consider the largest string's size, but either should yield the same results.
     * 
     * Firstly, sorting each string in an array will take 'S log S'. Then, for 'a' items this
     * yields O(a * (s log s)). Lastly, sorting the array of strings will take O(a log a) time, but
     * each comparison is itself a factor of 's'. Thus, while there are O(a log a) comparisons,
     * this takes O(a * s log a) time. Since it is sorting each string, and then sorting the array,
     * the two operations must be added. This yields: O(a * s log s) + O(a * s log a), which
     * simplifies to O(a*s*(log a + log s)).
     */
    
    // Example 9 - The following simple code sums the values of all the nodes in a balanced binary
    //             search tree. What is its runtime?
    class Node {
        Node left;
        Node right;
        int value;
    }
    int sum(Node node) {
        if (node == null) {
            return 0;
        }
        return sum(node.left) + node.value + sum(node.right);
    }
    
    /* This will touch every node once, doing a constant amount of work. Thus, this is O(n).
     * This is misleading for two reasons: tree-like structures commonly have log n time, and
     * recursive operations can have O(2^n), or more generally, O(branches^depth). We can compare
     * the number of operations in runtime to justify this runtime.
     * 
     * Each sum call will generate a stack log n methods deep. This is related to the total depth
     * of the tree, and how searching a tree structure with have to traverse at most log n items.
     * Because we are accessing 2 branches per node, and a recursive function's runtime is
     * typically O(branches^depth), the big O is O(2^log n). A logarithm is the inverse of an
     * exponent: that is, for (log base b of n = x), b^x = n. Using substitution, we can prove:
     * 
     * Given P = 2^(log n):
     * log base 2 of P = log base 2 of N
     * P = N
     * 2^(log n) = N
     * Thus, the runtime is O(n).
     */
    
    // Example 10 - The following method checks if a number is prime by checking for divisibility
    //              on numbers less than it. It only needs to go up to the square root of n because
    //              if n is divisible by a number greater than its square root then it's similarly
    //              divisible by a factor smaller than the square root, such that their product is
    //              equal to n. For example, while 33 is divisible by 11 (greater than the square
    //              root of 33, which is a little under 6), its matching factor is 3. This will
    //              already have been eliminated as a prime number since 3 is checked first. What
    //              is the time complexity of this function?
    boolean isPrime(int n) {
        for (int x = 2; x * x <= n; x++) {
            if (n % x == 0) {
                return false;
            }
        }
        return true;
    }
    
    /* Firstly, the work inside the loop is constant. Thus, the big O is based off of the number of
     * loop iterations. The condition: (x * x < n) is equivalent to: (x <= sqrt(n)). Thus, the
     * number of iterations is dependent on the square root of n, which cannot be factored down.
     * The answer is O(sqrt(n)).
     */
    
    // Example 11 - The following code computes n! (n factorial). What is its time complexity?
    int factorial(int n) {
        if (n < 0) {
            return -1;
        } else if (n == 0) {
            return 1;
        } else {
            return n * factorial(n - 1);
        }
    }
    
    /* This will do n operations, as it is essentially a stack-based loop multiplying from n to 1.
     * The answer is O(n).
     */
    
    // Example 12 - This code counts all permutations of a string. What is its time complexity?
    void permutation(String str) {
        permutation(str, "");
    }
    
    void permutation(String str, String prefix) {
        if (str.length() == 0) {
            System.out.println(prefix);
        } else {
            for (int i = 0; i < str.length(); i++) {
                String rem = str.substring(0, i) + str.substring(i + 1);
                permutation(rem, prefix + str.charAt(i));
            }
        }
    }
    
    /* Firstly, the code doesn't read well because the prefix comes after the string. Refactoring
     * the order of the parameters will not change the runtime, but should help us understand
     * the problem better.
     */
    
    void permutationRe(String str) {
        permutationRe("", str);
    }
    
    void permutationRe(String prefix, String str) {
        if (str.isEmpty()) {
            System.out.println(prefix);
        } else {
            for (int i = 0; i < str.length(); i++) {
                String rem = str.substring(0, i) + str.substring(i + 1);
                permutationRe(prefix + str.charAt(i), rem);
            }
        }
    }
    
    /* For this, we can see the total number of calls to permutationRe in its base case for a
     * string size n is n!, as each permutation has 1 less possibility. Before its base case,
     * there is a path of n operations, leading to n * n!. Finally, each function takes O(n) time
     * (string concatenation, etc.). At all points, prefix.length() + rem.length() == str.length().
     * Therefore, this is O(n * n * n!), which should be roughly O(n!).
     * (O((n+2)! is likely factorable to O(n!)))
     */
    
    // Example 13 - The following code computes the Nth Fibonacci number. What is its runtime
    //              complexity?
    int fib(int n) {
        if (n <= 0) return 0;
        else if (n == 1) return 1;
        return fib(n - 1) + fib(n - 2);
    }
    
    /* Since each call has roughly 2 subcalls under it and goes as deep as N, we end up with
     * roughly O(2^n) time complexity. However, since many calls are at the bottom and only have
     * one leaf rather than two, that (apparently) puts the runtime closer to O(1.6^n).
     */
    
    // Example 14 - The following code prints all Fibonacci numbers from 0 to n. What is its time
    //              complexity?
    void allFib(int n) {
        for (int i = 0; i < n; i++) {
            System.out.println(i + ": " + fib(i));
        }
    }
    
    /* While this seems to be doing n operations of 2(n), each "n" grows from 0 - n. Thus, we have
     * 2^1 + 2^2 + 2^3 + ... + 2^n. This comes out to 2^n+1, which is O(2^n).
     */
    
    // Example 15 - The following code prints all Fibonacci numbers from 0 to n. However, this
    //              time, it stores (i.e., caches) previously computed values in an integer
    //              array. If it has already been computer, it just returns the cache. What is its
    //              runtime?
    void allFibMemo(int n) {
        int[] memo = new int[n + 1];
        for (int i = 0; i < n; i++) {
            System.out.println(i + ": " + fibMemo(i, memo));
        }
    }
    
    int fibMemo(int n, int[] memo) {
        if (n <= 0) return 0;
        else if (n == 1) return 1;
        else if (memo[n] > 0) return memo[n];
        
        memo[n] = fibMemo(n - 1, memo) + fibMemo(n - 2, memo);
        return memo[n];
    }
    
    /* For each number that has not been calculated, it is calculated from constant values, and
     * then it is cached. Because of this, and because all lookups are constant time, we only do
     * work proportional to n. That is, the runtime is O(n).
     */
    
    // Example 16 - The following function prints the powers of 2 from 1 through n (inclusive).
    //              For example, if n is 4, it would print 1, 2, and 4. What is its runtime?
    int powersOf2(int n) {
        if (n < 1) {
            return 0;
        } else if (n == 1) {
            System.out.println(1);
            return 1;
        } else {
            int prev = powersOf2(n / 2);
            int curr = prev * 2;
            System.out.println(curr);
            return curr;
        }
    }
    
    /* Considering that each number is half the previous, this should only grow at a logarithmic rate
     * (inverse of exponential): O(log n).
     * 
     * We can also approach the runtime by thinking about what the code is supposed to be doing. It's supposed to be
     * computing the powers of 2 from 1 through n. Each call results in one number being printed and returned. In the
     * end, the recursive method is called the same number of times as powers of two that are printed. This number is
     * equal to the number of powers of 2 between 1 and n. There are log N powers of 2 between 1 and n: O(log n).
     */
}
