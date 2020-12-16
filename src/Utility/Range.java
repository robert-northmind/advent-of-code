package Utility;

public class Range<T extends Comparable<T>> {
    public T low;
    public T high;

    public Range(T low, T high) {
        this.low = low;
        this.high = high;
    }

    public boolean contains(T value) {
        return value.compareTo(low) >= 0 && value.compareTo(high) <= 0;
    }

    public String toString() {
        return "{ low: " + low + ", hight: " + high + " }";
    }
}
