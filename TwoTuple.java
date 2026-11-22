/*
 * Haider Ali
 *
 * TwoTuple.java
 */

import java.io.Serializable;

/**
 * Stores a pair of related objects.
 *
 * @param <A> type of the first object
 * @param <B> type of the second object
 */
public class TwoTuple<A, B> implements Serializable {

    public final A first;
    public final B second;

    /**
     * Creates a tuple containing two objects.
     *
     * @param a first object
     * @param b second object
     */
    public TwoTuple(A a, B b) {
        first = a;
        second = b;
    }

    /**
     * Returns a string representation of the tuple.
     *
     * @return tuple contents
     */
    @Override
    public String toString() {
        return "(" + first + ", " + second + ")";
    }
}