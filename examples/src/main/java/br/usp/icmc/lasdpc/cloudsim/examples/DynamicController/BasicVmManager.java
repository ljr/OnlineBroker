package br.usp.icmc.lasdpc.cloudsim.examples.DynamicController;

import org.cloudbus.cloudsim.core.CloudSimTags;

import br.usp.icmc.lasdpc.cloudsim.aux.Ack;

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
		if (ack.getSuccess() == CloudSimTags.TRUE) {
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

}
