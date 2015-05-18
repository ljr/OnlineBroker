package br.usp.icmc.lasdpc.cloudsim;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Demand {
	
	protected OnlineBroker mybroker;
	protected List<Event> events;
	protected Map<Integer, Object[]> cloudlets;
	protected Capacity capacity;
	
	public Demand() {
		events = new ArrayList<Event>();
		cloudlets = new HashMap<Integer, Object[]>();
	}
	
	public abstract List<Event> update(Map<Integer, List<Object>> values);
	
	
	public void setMyBroker(OnlineBroker mybroker) {
		this.mybroker = mybroker;
	}
	
	public OnlineBroker getMyBroker() {
		return mybroker;
	}

	public Capacity getCapacity() {
		return capacity;
	}

	public void setCapacity(Capacity capacity) {
		this.capacity = capacity;
	}
	
	

}
