package br.usp.icmc.lasdpc.cloudsim.lists;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.cloudbus.cloudsim.Vm;

public class VmList {

	private Map<Integer, Vm> vms;
	
	public VmList() {
		vms = new HashMap<Integer, Vm>();
	}
	
	public List<Vm> getByDatacenter(int dc) {
		List<Vm> r = new LinkedList<Vm>();
		
		for (Entry<Integer, Vm> e : vms.entrySet()) {
			if (getDatacenter(e.getValue().getId()) == dc) {
				r.add(e.getValue());
			}
		}
		
		return r;
	}
	
	public Integer getDatacenter(int vmId) {
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
		
		return vm;
	}

	public Map<Integer, Vm> getVms() {
		return vms;
	}

	public void setVms(Map<Integer, Vm> vms) {
		this.vms = vms;
	}
}
