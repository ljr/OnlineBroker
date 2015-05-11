package br.usp.icmc.lasdpc.cloudsim;

import java.util.ArrayList;
import java.util.List;

import org.cloudbus.cloudsim.core.SimEntity;


public abstract class Effector {
	
	protected OnlineBroker mybroker;
	protected Monitor monitor;
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
	
	public SimEntity getMybroker() {
		return mybroker;
	}

	public void setMybroker(OnlineBroker mybroker) {
		this.mybroker = mybroker;
	}
	
	public void setMonitor(Monitor monitor) {
		this.monitor = monitor;
	}
	
}
