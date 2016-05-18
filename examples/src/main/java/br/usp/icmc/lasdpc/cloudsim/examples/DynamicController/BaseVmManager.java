package br.usp.icmc.lasdpc.cloudsim.examples.DynamicController;

import java.util.HashMap;
import java.util.Map;

import org.cloudbus.cloudsim.CloudletScheduler;
import org.cloudbus.cloudsim.Vm;

/**
 * Intended to be a base implementation of Virtual Machines for a CloudSim 
 * simulation. As Vm is created, it is added to a list containing all Vms in 
 * the simulation. This class provides a create VM function and a minimal set 
 * of lists to manage Virtual Machine:
 * 
 * <ul>
 *   <li><b>vms</b>: list of all VMs in the simulation;</li>
 *   <li><b>failures</b>: VMs that failures in creation;</li>
 *   <li><b>running</b>: ready and working instances;</li>
 *   <li><b>destroyed</b>: disposed instances.</li>
 * </ul>
 * 
 * @author ljr
 *
 */
public abstract class BaseVmManager {
	/**
	 * List of all VMs created in the simulation.
	 */
	protected Map<Integer, Vm> vms;
	
	/**
	 * List of all VMs that failed to create.
	 */
	protected Map<Integer, Vm> failures;

	/**
	 * List of all VMs running, ready to receive cloudlets.
	 */
	protected Map<Integer, Vm> running;

	/**
	 * List of all VMs destroyed. VMs here was running somewhere in the past.
	 */
	protected Map<Integer, Vm> destroyed;
	
	/**
	 * Create a new manager and instantiate all lists.
	 */
	public BaseVmManager() {
		vms = new HashMap<Integer, Vm>();
		failures = new HashMap<Integer, Vm>();
		running = new HashMap<Integer, Vm>();
		destroyed = new HashMap<Integer, Vm>();
	}
	
	/**
	 * Add a VM to the list of VMs in the simulation.
	 * 
	 * @param vm 
	 */
	private void create(Vm vm) {
		vms.put(vm.getId(), vm);
	}
	
	/**
	 * Helper function that creates a VM and added it to the vms list.
	 * 
	 * @param id
	 * @param userId
	 * @param mips
	 * @param numberOfPes
	 * @param ram
	 * @param bw
	 * @param size
	 * @param vmm
	 * @param cloudletScheduler
	 * @return
	 */
	public Vm newVm(int id, int userId, double mips, int numberOfPes, 
			int ram, long bw, long size, String vmm, 
			CloudletScheduler cloudletScheduler) {
		Vm vm = new Vm(id, userId, mips, numberOfPes, ram, bw, size, vmm, cloudletScheduler);
		create(vm);
		return vm;
	}

	
	public Map<Integer, Vm> getVms() {
		return vms;
	}

	public void setVms(Map<Integer, Vm> vms) {
		this.vms = vms;
	}

	public Map<Integer, Vm> getFailures() {
		return failures;
	}

	public void setFailures(Map<Integer, Vm> failures) {
		this.failures = failures;
	}

	public Map<Integer, Vm> getRunning() {
		return running;
	}

	public void setRunning(Map<Integer, Vm> running) {
		this.running = running;
	}

	public Map<Integer, Vm> getDestroyed() {
		return destroyed;
	}

	public void setDestroyed(Map<Integer, Vm> destroyed) {
		this.destroyed = destroyed;
	}
	
	
	
}
