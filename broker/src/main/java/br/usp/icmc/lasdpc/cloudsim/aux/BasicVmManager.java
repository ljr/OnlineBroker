package br.usp.icmc.lasdpc.cloudsim.aux;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.cloudbus.cloudsim.Vm;

/**
 * Implement a minimal life cycle of a Virtual Machine in a CloudSim simulation.
 * 
 * In the case of sending a VM_CREATE_ACK to a Datacenter. When the ACK is 
 * received by the broker it is processed, causing a state changing in the VM, 
 * if the Vm was successfully created in Datacenter, is is added to running 
 * list, otherwise to failures list.  
 * 
 * In the case of sending a VM_DESTROY_ACK to a Datacenter. A state changing 
 * occurs, setting the VM from running to destroyed. This implies in removing it
 * from running list and putting it in destroyed list.
 * 
 * @author ljr
 *
 */
public class BasicVmManager extends BaseVmManager {


	/**
	 * Try to set VM to running state. The VM will be added to running or 
	 * failures list, as the value of ack.getSuccess() if CloudSimTags.TRUE or
	 * CloudSimTags.FALSE, respectively. 
	 * @param ack
	 */
	public void newToRunning(Ack ack) {
		if (ack.succeed()) {
			running.put(ack.getId(), vms.get(ack.getId()));
		} else {
			failures.put(ack.getId(), vms.get(ack.getId()));
		}
	}
	
	/**
	 * Remove a VM from running and put it in destroyed list.
	 * @param vmId
	 */
	public void runningToDestroyed(int vmId) {
		destroyed.put(vmId, running.remove(vmId));
	}
	
	
	public void add(Vm e) {
		create(e);
	}
	
	public Collection<Vm> getSubmitList() {
		return vms.values();
	}
	
	public void addAll(List<Vm> vms) {
		for (Vm e : vms) {
			add(e);
		}
	}
	
	public boolean allRequestedVmsDone() {
		return vms.size() == running.size();
	}
	
	public int getFailed() {
		return failures.size();
	}
	
	public boolean failed(int vmId) {
		return failures.containsKey(vmId);
	}
	
	public boolean destroyed(int vmId) {
		runningToDestroyed(vmId);
		return true;
	}
	
	public List<Vm> getByDatacenter(int dc) {
		List<Vm> r = new ArrayList<Vm>();
		
		for (Vm vm : vms.values()) {
			if (getDatacenterId(vm.getId()) == dc) {
				r.add(vm);
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
		Vm vm = getById(vmId);
		
		if (vm != null && vm.getUserId() == userId) {
			return vm;
		}
		
		return null;
	}
	
	public boolean isVmAllocated(int vmId) {
		return running.containsKey(vmId);
	}
	
	@Deprecated
	public int getCreated() {
		return running.size();
	}
	
	public List<Vm> getCreatedList() {
		return new ArrayList<Vm>(running.values());
	}
}
