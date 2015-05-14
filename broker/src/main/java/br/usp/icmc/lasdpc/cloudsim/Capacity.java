package br.usp.icmc.lasdpc.cloudsim;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class Capacity {
	
	protected OnlineBroker mybroker;
	protected List<Integer> vmIds;
	protected List<Event> events;
	
	
	public Capacity() {
		events = new ArrayList<Event>();
		vmIds = new ArrayList<Integer>();
	}

	public void setMyBroker(OnlineBroker mybroker) {
		this.mybroker = mybroker;
	}
	
	public OnlineBroker getMyBroker() {
		return mybroker;
	}
	
	public abstract List<Event> update(Map<Integer, List<Object>> values);

}
