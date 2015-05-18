package br.usp.icmc.lasdpc.cloudsim;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.cloudbus.cloudsim.Cloudlet;

public abstract class Demand {
	
	protected OnlineBroker mybroker;
	protected List<Event> events;
	protected Set<Cloudlet> cloudlets;
	protected Capacity capacity;
	
	public Demand() {
		events = new ArrayList<Event>();
		cloudlets = new LinkedHashSet<Cloudlet>();
	}
	
	public abstract List<Event> update(Map<Integer, Map<String, List<Object>>> values);
	
	
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
	
	public void create(Cloudlet cl) {
		cloudlets.add(cl);
	}

	public Set<Cloudlet> getCloudlets() {
		return cloudlets;
	}
	

}
