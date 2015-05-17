package br.usp.icmc.lasdpc.cloudsim;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Demand {
	
	protected OnlineBroker mybroker;
	protected List<Event> events;
	protected Map<Integer, Object[]> cloudlets;
	
	public Demand() {
		events = new ArrayList<Event>();
		cloudlets = new HashMap<Integer, Object[]>();
	}
	
	public void setMyBroker(OnlineBroker mybroker) {
		this.mybroker = mybroker;
	}
	
	public OnlineBroker getMyBroker() {
		return mybroker;
	}
	
	public abstract List<Event> update(Map<Integer, List<Object>> values);

}
