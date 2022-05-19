import java.util.Arrays;

public class IntList {
    private int size;
    private int[] dt;

    public IntList() {
        size = 0;
        dt = new int[1];
    }

    public int get(int index) {
        return dt[index];
    }

    public void add(int value) {
        if (size >= dt.length) {
            dt = Arrays.copyOf(dt, dt.length * 2);
        }
        dt[size] = value;
        size++;
    }

    public int size() {
        return size;
    }
    
    public int last() {
    	return dt[size - 1];
    }
}
