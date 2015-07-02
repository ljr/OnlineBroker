package br.usp.icmc.lasdpc.cloudsim;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import br.usp.icmc.lasdpc.cloudsim.aux.Event;

public abstract class Demand {
	
	protected OnlineBroker mybroker;
	protected List<Event> cloudlets;
	
	public Demand() {
		cloudlets = new ArrayList<Event>();
	}
	
	
	public abstract List<Event> update(Map<Integer, List<Object>> values);
	
	
	public void setMyBroker(OnlineBroker mybroker) {
		this.mybroker = mybroker;
	}
	
	public OnlineBroker getMyBroker() {
		return mybroker;
	}
	

}
