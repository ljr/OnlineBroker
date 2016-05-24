package br.usp.icmc.lasdpc.cloudsim.aux;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.cloudbus.cloudsim.CloudletScheduler;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Vm;


/**
 * Intended to be a base implementation of Virtual Machines for a CloudSim 
 * simulation. As <tt>Vm</tt> is created, it is added to a list containing all 
 * <tt>Vms</tt> in the simulation. This class provides a create VM function 
 * and a minimal set of lists to manage Virtual Machine:
 * 
 * <ul>
 *   <li><b>vms</b>: list of all VMs in the simulation;</li>
 *   <li><b>failures</b>: VMs that failures in creation;</li>
 *   <li><b>running</b>: ready and working instances;</li>
 *   <li><b>destroyed</b>: disposed instances.</li>
 * </ul>
 * 
 * It implements a minimal life cycle of a Virtual Machine in a CloudSim 
 * simulation.
 * 
 * In the case of sending a <tt>VM_CREATE_ACK</tt> to a <tt>Datacenter</tt>. 
 * When the <tt>Ack</tt> is received by the broker it is processed, causing a 
 * state changing in the VM, if the <tt>Vm</tt> was successfully created in 
 * <tt>Datacenter</tt>, is is added to <tt>running</tt> list, otherwise to 
 * <tt>failures</tt> list.  
 * 
 * In the case of sending a <tt>VM_DESTROY_ACK</tt> to a <tt>Datacenter</tt>. 
 * A state changing occurs, setting the VM from running to destroyed. This 
 * implies in removing it from <tt>running</tt> list and putting it in 
 * <tt>destroyed</tt> list.
 * 
 * For more detailed state transition, it also implements a realistic Virtual 
 * Machine (VM) state machine. When a new VM is created it is put into 
 * <tt>booting</tt> state and still there until the time to finish the boot 
 * is over. By checking <tt>CloudSim.clock()</tt>, those VMs that already 
 * finished the boot can be sent to a <tt>Datacenter</tt> to be allocated. 
 * When the <tt>VM_CREATE_ACK</tt> arrives in the Broker, a VM can be set to 
 * <tt>running</tt> state. When a VM must be destroyed, it is set to 
 * <tt>bleeding</tt> state and continues to process its <tt>Cloudlets</tt>, 
 * however stops receiving new ones. Whether new VMs are needed, those in 
 * <tt>bleeding</tt> state can be turned into <tt>running</tt> again. By 
 * checking <tt>CloudSim.clock()</tt>, those VMs that already finish to bleed 
 * can be put into <tt>destroyed</tt> state. 
 * 
 * 
 * @author ljr
 *
 */
public class VmManager {
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
	 * List of all VMs destroyed. VMs here were running somewhere in the past.
	 */
	protected Map<Integer, Vm> destroyed;
	
	/**
	 * VMs in <tt>booting</tt> state.
	 */
	private TreeMap<Integer, MetaVm> booting;
	
	/**
	 * VMs <tt>canceled</tt> during the <tt>booting</tt> state.
	 */
	private Map<Integer, MetaVm> canceled;
	
	/**
	 * VMs in <tt>bleeding</tt> state.
	 */
	private Map<Integer, MetaVm> bleeding;

	
	/**
	 * Create a new manager and instantiate all lists.
	 */
	public VmManager() {
		this.vms = new HashMap<Integer, Vm>();
		this.failures = new HashMap<Integer, Vm>();
		this.running = new HashMap<Integer, Vm>();
		this.destroyed = new HashMap<Integer, Vm>();
		this.booting = new TreeMap<Integer, MetaVm>();
		this.canceled = new HashMap<Integer, MetaVm>();
		this.bleeding = new HashMap<Integer, MetaVm>();
	}
	
	/**
	 * Add a VM to the list of VMs in the simulation.
	 * 
	 * @param vm
	 * @return 
	 */
	protected boolean create(Vm vm) {
		return vms.put(vm.getId(), vm) == null;
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
	 * @throws Exception 
	 */
	public Vm newVm(int id, int userId, double mips, int numberOfPes, 
			int ram, long bw, long size, String vmm, 
			CloudletScheduler cloudletScheduler) throws Exception {
		
		Vm vm = new Vm(id, userId, mips, numberOfPes, ram, bw, size, vmm, cloudletScheduler);
		
		if (!create(vm)) {
			throw new Exception("Vm already created. ID: " + vm.getId());
		}
		
		return vm;
	}


	/**
	 * Set the new Vm as <tt>booting</tt>.
	 * 
	 * @param vmId
	 * @param bootTime
	 * @return
	 */
	public boolean newToBooting(int vmId, double bootTime) {
		if (!vms.containsKey(vmId)) {
			return false;
		}
			
		try {
			return booting.put(vmId, new MetaVm(bootTime, vms.get(vmId))) == null;
		} catch (Exception e) {
			return false;
		}
	}
	
	/**
	 * Set the Vm into <tt>canceled</tt> state. The Vm was booting and for some
	 * reason it need to be canceled.
	 * @param howMany
	 */
	public boolean bootingToCanceled(long howMany) {
		if (howMany < 1) {
			return false;
		}
		
		int s = booting.size();
		for (int i = s - 1; i >= s - howMany; i--) {
			if (!booting.isEmpty()) {
				Integer vmId = booting.lastKey();
				canceled.put(vmId, booting.remove(vmId));	
			}
		}
		
		return true;
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
			if (clock >= vm.getDeadline()) {
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
	 * @return
	 */
	public boolean bootingToRunning(Ack ack) {
		if (!booting.containsKey(ack.getId())) {
			return false;
		}
		
		MetaVm mvm = booting.remove(ack.getId());
		if (ack.succeed()) {
			return running.put(ack.getId(), mvm.getVm()) == null;
		} else {
			return failures.put(ack.getId(), mvm.getVm()) == null;
		}
	}
	
	/**
	 * Set a <tt>Vm</tt> into <tt>bleeding</tt> state.
	 * @param vmId
	 * @param bleedTime
	 * @return
	 */
	public boolean runningToBleeding(int vmId, double bleedTime) {
		try {
			return runningToBleeding(new MetaVm(bleedTime, running.get(vmId)));
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Set a <tt>MetaVm</tt> into <tt>bleeding</tt> state. It is mainly used by
	 * runningToBleeding(howMany).
	 * @param mvm
	 * @return
	 */
	protected boolean runningToBleeding(MetaVm mvm) {
		if (!running.containsKey(mvm.getId())) {
			return false;
		}
		
		running.remove(mvm.getId());
		return bleeding.put(mvm.getId(), mvm) == null;
	}
	
	/**
	 * Set an arbitrary number of <tt>Vm</tt>s into <tt>bleeding</tt> state.
	 * @param howMany
	 * @return
	 * @see getNextBleed
	 */
	public int runningToBleeding(long howMany) {
		int r = -1;
		
		if (howMany < 1) {
			return r;
		} else {
			r = 0;
		}
		
		for (int i = 0; i < howMany; i++) {
			if (runningToBleeding(getNextToBleed())) {
				r++;
			}
		}
		
		return r;
	}
	
	/**
	 * Choose the idlest <tt>Vm</tt> to bleed.
	 * @return
	 */
	protected MetaVm getNextToBleed() {
		Vm toBleed = null;
		double min = Double.MAX_VALUE;
		double bleedTime = Double.MIN_VALUE;
		
		for (Vm vm : running.values()) {
			double vmLoad = vm.getCurrentRequestedTotalMips(); 
			if (vmLoad <= min) {
				min = vmLoad;
				bleedTime = vmLoad;
				toBleed = vm;
			}
		}
		
		if (toBleed == null) {
			toBleed = (new ArrayList<Vm>(running.values())).get(0);
			bleedTime = toBleed.getCurrentRequestedTotalMips();
		}
		
		try {
			return new MetaVm(bleedTime, toBleed);
		} catch (Exception e) {
			Log.printLine("ERROR: VmManager.getNextToBleed(): it is not suppose to this exception occurs.");
			return null;
		}
	}
	
	/**
	 * Turn a <tt>Vm</tt> into <tt>running</tt> state again. 
	 * @param vmId
	 * @return
	 */
	public boolean bleedingToRunning(int vmId) {
		if (!bleeding.containsKey(vmId)) {
			return false;
		}
		
		return running.put(vmId, bleeding.remove(vmId).getVm()) == null;
	}
	
	/**
	 * Choose the idlest <tt>Vm</tt> to remove from bleeding to running.
	 * 
	 * @return
	 */
	public boolean idlestBleeding() {
			double min = Double.MAX_VALUE;
			int id = -1;
			
			for (MetaVm mvm : bleeding.values()) {
				// TODO: how this works?
				double v = mvm.getVm().getCurrentRequestedTotalMips();
				
				if (v <= min) {
					min = v;
					id = mvm.getId();
				}
			}
			
			return bleedingToRunning(id);
	}
	
	/**
	 * Turn the `howMany' idlest <tt>Vm</tt>s into <tt>running</tt> state again.
	 * @param howMany
	 * @return The number of <tt>Vm</tt>s put in <tt>running</tt>.
	 */
	public long bleedingToRunning(long howMany) {
		long r = -1;
		
		if (howMany < 1) {
			return r;
		} else {
			r = 0;
		}
		
		for (int i = 0; i < howMany; i++) {
			if (idlestBleeding()) {
				r++;
			}
		}
			
		return r;
	}
	
	/**
	 * Given a certain instant of time, set those <tt>Vm</tt>s that already 
	 * finished the bleed into <tt>destroyed</tt> state. 
	 * @param clock
	 * @return
	 */
	public boolean bleedingToDestroyed(double clock) {
		boolean r = false;
		List<Vm> toDestroy = new ArrayList<>();
		
		for (MetaVm vm : bleeding.values()) {
			if (clock >= vm.getDeadline()) {
				toDestroy.add(vm.getVm());
				// The following code launches a ConcurrentModificationException
				// due the fact we are iterating on bleeding and in the same
				// loop changing its structure.
				//destroyed.put(vm.getId(), bleeding.remove(vm.getId()).getVm());
				r = true;
			}
		}
		
		for (Vm vm : toDestroy) {
			destroyed.put(vm.getId(), bleeding.remove(vm.getId()).getVm());
		}
		
		return r;
	}
	
	public boolean hasBleeding() {
		return bleeding.size() > 0;
	}
	
	public boolean hasBooting() {
		return booting.size() > 0;
	}
	
	public int vmsInSystem() {
		return booting.size() + running.size();
	}

	/**
	 * Try to set VM to running state. The VM will be added to running or 
	 * failures list, as the value of ack.getSuccess() if CloudSimTags.TRUE or
	 * CloudSimTags.FALSE, respectively. 
	 * @param ack
	 */
	public boolean newToRunning(Ack ack) {
		if (!vms.containsKey(ack.getId())) {
			return false;
		}
		
		if (ack.succeed()) {
			return running.put(ack.getId(), vms.get(ack.getId())) == null;
		} else {
			return failures.put(ack.getId(), vms.get(ack.getId())) == null;
		}
	}
	
	/**
	 * Remove a VM from running and put it in destroyed list.
	 * @param vmId
	 */
	public boolean runningToDestroyed(int vmId) {
		if (!running.containsKey(vmId)) {
			return false;
		}
		
		
		return destroyed.put(vmId, running.remove(vmId)) == null;
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
		return runningToDestroyed(vmId);
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
		Vm vm = vms.get(vmId);
		
		if (vm == null) {
			return null;
		}
		
		return vm.getHost().getDatacenter().getId();
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
	
	
	public List<Vm> getCreatedList() {
		// TODO: not sure whether is the best thing to do.
		return new ArrayList<Vm>(running.values());
	}
	

	
	
	//===================================================================//
	//   GETTERS AND SETTERS                                             //
	//===================================================================//
	
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

	public Map<Integer, MetaVm> getBooting() {
		return booting;
	}

	public void setBooting(TreeMap<Integer, MetaVm> booting) {
		this.booting = booting;
	}

	public Map<Integer, MetaVm> getBleeding() {
		return bleeding;
	}

	public void setBleeding(Map<Integer, MetaVm> bleeding) {
		this.bleeding = bleeding;
	}

	public Map<Integer, MetaVm> getCanceled() {
		return canceled;
	}

	public void setCanceled(Map<Integer, MetaVm> canceled) {
		this.canceled = canceled;
	}
	
	

}
