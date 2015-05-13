package br.usp.icmc.lasdpc.cloudsim;

import java.util.ArrayList;
import java.util.List;


public abstract class Effector {
	
	protected OnlineBroker mybroker;
	protected List<Event> cap;
	protected List<Event> dem;

	public Effector() {
		cap = new ArrayList<Event>();
		dem = new ArrayList<Event>();
	}
	
	public abstract void set(List<Event> cap, List<Event> dem);
	
	public void clear() {
		cap.clear();
		dem.clear();
	}
	
	public OnlineBroker getMybroker() {
		return mybroker;
	}

	public void setMybroker(OnlineBroker mybroker) {
		this.mybroker = mybroker;
	}
	
}
