package br.usp.icmc.lasdpc.cloudsim;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cloudbus.cloudsim.Vm;

public abstract class Capacity {
	
	protected OnlineBroker mybroker;
	/** It represents a bunch of virtual machines instances held by a Data-center. */
	protected Map<Integer, Object[]> vms;
	protected List<Event> events;
	
	
	public Capacity() {
		events = new ArrayList<Event>();
		vms = new HashMap<Integer, Object[]>();
	}

	public void setMyBroker(OnlineBroker mybroker) {
		this.mybroker = mybroker;
	}
	
	public OnlineBroker getMyBroker() {
		return mybroker;
	}
	
	public abstract List<Event> update(Map<Integer, List<Object>> values);
	
	protected void create(Vm vm) {
		vms.put(vm.getId(), new Object[] {vm, null});
	}
	
	protected void setVMDatacenter(int vmId, Integer datacenterId) {
		vms.put(vmId, new Object[] {vms.get(vmId)[0], datacenterId});
	}
	
	public Vm getVm(int vmId) {
		return (Vm) getValue(vmId, 0);
	}
	
	public Integer getDatacenter(int vmId) {
		return (Integer) getValue(vmId, 1);
	}
	
	private Object getValue(int vmId, int i) {
		return vms.get(vmId)[i];
	}

	public boolean isVmActive(int vmId) {
		return getDatacenter(vmId) != null;
	}

	public Map<Integer, Object[]> getVms() {
		return vms;
	}
	
}
