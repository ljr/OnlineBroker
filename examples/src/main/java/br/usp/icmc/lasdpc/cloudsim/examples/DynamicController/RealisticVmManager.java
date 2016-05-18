package br.usp.icmc.lasdpc.cloudsim.examples.DynamicController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.core.CloudSimTags;

import br.usp.icmc.lasdpc.cloudsim.aux.Ack;

/**
 * Implements a realistic Virtual Machine (VM) state machine. When a new VM is
 * created it is put into <tt>booting</tt> state and still there until the time 
 * to finish the boot is over. By checking <tt>CloudSim.clock()</tt>, those 
 * VMs that already finished the boot can be sent to a <tt>Datacenter</tt> to 
 * be allocated. When the <tt>VM_CREATE_ACK</tt> arrives in the Broker, a 
 * VM can be set to <tt>running</tt> state. When a VM must be destroyed, it is
 * set to <tt>bleeding</tt> state and continues to process its 
 * <tt>Cloudlets</tt>, however stops receiving new ones. Whether new VMs are 
 * needed, those in <tt>bleeding</tt> state can be turned into <tt>running</tt> 
 * again. By checking <tt>CloudSim.clock()</tt>, those VMs that already finish 
 * to bleed can be put into <tt>destroyed</tt> state. 
 * 
 * 
 * @author ljr
 *
 */
public class RealisticVmManager extends BaseVmManager {

	/**
	 * VMs in booting state
	 */
	private Map<Integer, MetaVm> booting;
	
	/**
	 * VMs in bleeding state
	 */
	private Map<Integer, MetaVm> bleeding;

	/**
	 * Create a new manager and instantiate all lists.
	 */
	public RealisticVmManager() {
		booting = new HashMap<Integer, MetaVm>();
		bleeding = new HashMap<Integer, MetaVm>();
	}
	
	/**
	 * Set the new Vm as booting
	 * 
	 * @param vmId
	 * @param bootTime
	 */
	public void newToBooting(int vmId, double bootTime) {
		booting.put(vmId, new MetaVm(bootTime, vms.get(vmId)));
	}
	
	/**
	 * Given a certain instant of time, return those Vms that already finished
	 * the boot.
	 * 
	 * @param clock
	 * @return
	 */
	public List<Vm> vmsToBeCreated(double clock) {
		List<Vm> result = new ArrayList<Vm>();
		
		for (MetaVm vm : booting.values()) {
			if (vm.getDeadline() >= clock) {
				result.add(vm.getVm());
			}
		}
		
		return result;
	}
	
	/**
	 * After receiving the <tt>VM_CREATE_ACK</tt> in the Broker, try to set VM 
	 * to <tt>running</tt> state. The VM will be added to <tt>running</tt> or 
	 * <tt>failures</tt> list, as the value of <tt>ack.getSuccess()</tt> if 
	 * <tt>CloudSimTags.TRUE</tt> or <tt>CloudSimTags.FALSE</tt>, respectively. 
	 * 
	 * @param ack
	 */
	public void bootingToRunning(Ack ack) {
		if (ack.getSuccess() == CloudSimTags.TRUE) {
			running.put(ack.getId(), booting.remove(ack.getId()).getVm());
		} else {
			failures.put(ack.getId(), booting.remove(ack.getId()).getVm());
		}
	}
	
	/**
	 * Set a <tt>Vm</tt> into <tt>bleeding</tt> state.
	 * @param vmId
	 * @param bleedTime
	 */
	public void runningToBleeding(int vmId, double bleedTime) {
		bleeding.put(vmId, new MetaVm(bleedTime, running.remove(vmId)));
	}
	
	/**
	 * Turn a <tt>Vm</tt> into <tt>running</tt> state again. 
	 * @param vmId
	 */
	public void bleedingToRunning(int vmId) {
		running.put(vmId, bleeding.remove(vmId).getVm());
	}
	
	/**
	 * Given a certain instant of time, set those <tt>Vm</tt>s that already 
	 * finished the bleed into <tt>destroyed</tt> state. 
	 * @param clock
	 */
	public void bleedingToDestroyed(int clock) {
		for (MetaVm vm : bleeding.values()) {
			if (vm.getDeadline() >= clock) {
				destroyed.put(vm.getVm().getId(), bleeding.remove(vm.getVm().getId()).getVm());
			}
		}
	}

	public Map<Integer, MetaVm> getBooting() {
		return booting;
	}

	public void setBooting(Map<Integer, MetaVm> booting) {
		this.booting = booting;
	}

	public Map<Integer, MetaVm> getBleeding() {
		return bleeding;
	}

	public void setBleeding(Map<Integer, MetaVm> bleeding) {
		this.bleeding = bleeding;
	}
	
	
}
