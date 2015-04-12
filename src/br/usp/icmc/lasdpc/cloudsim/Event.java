package br.usp.icmc.lasdpc.cloudsim;

public class Event {
	private int dest;
	private double delay;
	private int tag;
	private Object data;
	
	public Event(int dest, double delay, int tag, Object data) {
		this.dest = dest;
		this.delay = delay;
		this.tag = tag;
		this.data = data;
	}
	
	public Event(double delay, int tag, Object data) {
		this(-1, delay, tag, data);
	}
	
	public Event(double delay, int tag) {
		this(-1, delay, tag, null);
	}

	public int getDest() {
		return dest;
	}

	public void setDest(int dest) {
		this.dest = dest;
	}

	public double getDelay() {
		return delay;
	}

	public void setDelay(double delay) {
		this.delay = delay;
	}

	public int getTag() {
		return tag;
	}

	public void setTag(int tag) {
		this.tag = tag;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

}
