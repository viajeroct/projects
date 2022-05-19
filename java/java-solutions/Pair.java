public class Pair {
	private IntList dt;
	private int stringNumber;
	private int index;
	private int total;
	
	public Pair(int stringNumber) {
		dt = new IntList();
		this.stringNumber = stringNumber;
		index = 1;
		total = 0;
	}
	
	public void setStr(int stringNumber) {
		this.stringNumber = stringNumber;
	}
	
	public int getStringNumber() {
		return stringNumber;
	}
	
	public void setIndex(int index) {
		this.index = index;
	}
	
	public int getIndex() {
		return index;
	}
	
	public void incrIndex() {
		index++;
	}
	
	public void incrTotal() {
		total++;
	}
	
	public IntList getDt() {
		return dt;
	}
	
	public void add(int x) {
		dt.add(x);
	}
	
	public int getTotal() {
		return total;
	}
}
