package br.usp.icmc.lasdpc.cloudsim;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.cloudbus.cloudsim.Vm;

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
	
	protected void create(Vm vm) {
		mybroker.getMonitor().getVmList().getVms().put(vm.getId(), vm);
	}
	
	public Vm getVm(int vmId) {
		return mybroker.getMonitor().getVmList().getById(vmId);
	}
	
	public Integer getDatacenter(int vmId) {
		return mybroker.getMonitor().getVmList().getDatacenter(vmId);
	}
	
	public boolean isVmActive(int vmId) {
		return getDatacenter(vmId) != null;
	}

	public Map<Integer, Vm> getVms() {
		return mybroker.getMonitor().getVmList().getVms();
	}
	
}
