package br.usp.icmc.lasdpc.cloudsim.aux;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.cloudbus.cloudsim.Vm;

public class VmList {

	private Map<Integer, Vm> vms;
	
	public VmList() {
		vms = new HashMap<Integer, Vm>();
	}
	
	public void add(Vm e) {
		vms.put(e.getId(), e);
	}
	
	public void addAll(List<Vm> vms) {
		for (Vm e : vms) {
			add(e);
		}
	}
	
	
	public List<Vm> getByDatacenter(int dc) {
		List<Vm> r = new ArrayList<Vm>();
		
		for (Entry<Integer, Vm> e : vms.entrySet()) {
			if (getDatacenterId(e.getValue().getId()) == dc) {
				r.add(e.getValue());
			}
		}
		
		return r;
	}
	
	
	public Integer getDatacenterId(int vmId) {
		return vms.get(vmId).getHost().getDatacenter().getId();
	}
	
	public Vm getById(int vmId) {
		return vms.get(vmId);
	}
	
	public Vm getByIdAndUserId(int vmId, int userId) {
		Vm vm = vms.get(vmId);
		
		if (vm != null && vm.getUserId() == userId) {
			return vm;
		}
		
		return null;
	}

	public boolean isVmAllocated(int vmId) {
		return !vms.get(vmId).isBeingInstantiated();
	}
	
	public Map<Integer, Vm> getVms() {
		return vms;
	}

	public void setVms(Map<Integer, Vm> vms) {
		this.vms = vms;
	}
}
