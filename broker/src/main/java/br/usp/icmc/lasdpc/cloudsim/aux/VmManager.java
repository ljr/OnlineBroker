package br.usp.icmc.lasdpc.cloudsim.aux;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.cloudbus.cloudsim.Vm;

public class VmManager {

	private Map<Integer, Vm> vms;
	private Map<Integer, Vm> createdMap;
	private Map<Integer, Vm> submitted;
	private int created;
	private int destroyed;
	
	public VmManager() {
		vms = new HashMap<Integer, Vm>();
		submitted = new HashMap<Integer, Vm>();
		setCreatedMap(new HashMap<Integer, Vm>());
		setCreated(0);
		setDestroyed(0);
	}

	public void add(Vm e) {
		vms.put(e.getId(), e);
		submitted.put(e.getId(), e);
	}
	
	public Collection<Vm> getSubmitList() {
		return submitted.values();
	}
	
	public void addAll(List<Vm> vms) {
		for (Vm e : vms) {
			add(e);
		}
	}

	public boolean allRequestedVmsDone() {
		return getCreatedMap().size() == getVms().size() - getDestroyed();
	}
	
	public boolean created(int vmId) {
		if (!incCreated()) {
			return false;
		}
		
		getCreatedMap().put(vmId, submitted.remove(vmId));
		return true;
	}
	
	public boolean destroyed(int vmId) {
		if (!incDestroyed()) {
			return false;
		}
		
		getCreatedMap().remove(vmId);
		this.created--;
		
		return true;
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

	public int getCreated() {
		return created;
	}

	public void setCreated(int created) {
		this.created = created;
	}

	public boolean incCreated() {
		this.created++;
		return created <= vms.size();
	}
	
	
	public int getDestroyed() {
		return destroyed;
	}

	public void setDestroyed(int destroyed) {
		this.destroyed = destroyed;
	}
	
	public boolean incDestroyed() {
		this.destroyed++;
		return destroyed <= vms.size();
	}

	public Map<Integer, Vm> getCreatedMap() {
		return createdMap;
	}

	public void setCreatedMap(Map<Integer, Vm> createdMap) {
		this.createdMap = createdMap;
	}
	
	public List<Vm> getCreatedList() {
		return new ArrayList<Vm>(getCreatedMap().values());
	}
	
}
