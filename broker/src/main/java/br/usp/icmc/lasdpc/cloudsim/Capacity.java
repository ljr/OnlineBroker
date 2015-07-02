package br.usp.icmc.lasdpc.cloudsim;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import br.usp.icmc.lasdpc.cloudsim.aux.Event;

public abstract class Capacity {
	
	protected OnlineBroker mybroker;
	protected List<Event> events;
	
	
	public Capacity() {
		events = new ArrayList<Event>();
	}

	public void setMyBroker(OnlineBroker mybroker) {
		this.mybroker = mybroker;
	}
	
	public OnlineBroker getMyBroker() {
		return mybroker;
	}
	
	public abstract List<Event> update(Map<Integer, List<Object>> values);
	
	
}
