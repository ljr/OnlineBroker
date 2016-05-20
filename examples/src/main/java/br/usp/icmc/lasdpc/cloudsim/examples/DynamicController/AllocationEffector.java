package br.usp.icmc.lasdpc.cloudsim.examples.DynamicController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.CloudSimTags;

import br.usp.icmc.lasdpc.cloudsim.Effector;
import br.usp.icmc.lasdpc.cloudsim.OnlineBroker;
import br.usp.icmc.lasdpc.cloudsim.aux.Event;
import br.usp.icmc.lasdpc.cloudsim.aux.MetaVm;

public class AllocationEffector extends Effector {


	private static final double DEFAULT_VM_START_DELAY = 40;
	
	private int dcRR;
	private int vmRR;
	private PerformanceMonitor mon;
	
	public AllocationEffector() {
		dcRR = vmRR = 0;
	}
	
	@Override
	public void setMybroker(OnlineBroker mybroker) {
		super.setMybroker(mybroker);
		mon = (PerformanceMonitor) mybroker.getMonitor();
	}
	
	private List<Event> vmStateMachine(List<Event> cap) {
		int ri = 0;
		int []rem = new int[mon.getVmManager().getBleeding().size()];
		
		for (int i = 0; i < cap.size(); i++) {
			Event e = cap.get(i);
			
			
			switch (e.getTag()) {
			case CloudSimTags.VM_CREATE:
			case CloudSimTags.VM_CREATE_ACK:
				Vm vm = (Vm) e.getData();
				
				if (mon.getVmManager().hasBleeding()) { // restore from bleeding
					mon.getVmManager().bleedingToRunning(vm.getId());
					rem[ri++] = i;
				} else { // set as starting
					mon.getVmManager().newToBooting(vm.getId(), DEFAULT_VM_START_DELAY);
					cap.get(i).setDelay(DEFAULT_VM_START_DELAY);
				}
				
				break;
				
			case Tags.BLEED:
				long howManyVms = (long) e.getData(); // howManyVms ALWAYS will be a positive value bigger than zero.
				long left = howManyVms - mon.getVmManager().getBleeding().size();
				if (left > 0) {
					for (int b = 0; b < left; b++) {
						if (mon.getVmManager().hasBooting()) {
							mon.getVmManager().bootingToCanceled(vmId)
							rem[ri++] = i;
						} else { // set to bleed
							vm = getNextToBleed();
							double howLongToFinish = vm.getCurrentRequestedTotalMips()/60; 
							mon.getVmManager().getBleeding().put(vm.getId(), new MetaVm(howLongToFinish, vm));
							//cap.get(i).setDelay(howLongToFinish);
							//cap.get(i).setTag(CloudSimTags.VM_DESTROY_ACK);
						}
					}
				} else if (left < 0) {
					Log.printLine("Invalid transition. `howMany' has assumed a negative value. This MUST BE a VM_CREATE transition. left: " + left);
					finishExecution();
				}
				
				break;

			default:
				break;
			}
		}
	
		for (int i = 0; i < ri; i++) {
			cap.remove(rem[i]);
		}
		
	
		for (MetaVm v : starting.values()) {
			if (CloudSim.clock() >= v.getDeadline()) {
				//cap.add(new Event(0, CloudSimTags.VM_CREATE_ACK, v.getVm()));
				starting.remove(v.getVm().getId());
			}
		}

		for (MetaVm v: bleeding.values()) {
			cap.add(new Event(0, v.getDeadline(), CloudSimTags.VM_DESTROY_ACK, v.getVm()));
			bleeding.remove(v.getVm().getId());
		}
		
		
		return cap;
	}
	
	private Vm getNextToBleed() {
		Vm result = null;
		
		int min = -1;
		for (Vm vm : mybroker.getMonitor().getVmManager().getCreatedList()) {
			int q = vm.getCloudletScheduler().getCloudletExecList().size();
			if (min > q) {
				min = q;
				result = vm;
			}
		}
		
		if (result == null) {
			result = mybroker.getMonitor().getVmManager().getCreatedList().get(0);
		}
		
		return result;
	}

	@Override
	public void set(List<Event> cap, List<Event> dem) {
		
		
		if (CloudSim.clock() > (2 * mon.getChangeTime())) {
			finishExecution();
		}
		
		for (Event e : vmStateMachine(cap)) {
			try {
				e.setDest(getTargetDc());
				mybroker.sendEvent(e);
			} catch (Exception e1) {
				Log.printLine(">>> CLOCK" + CloudSim.clock());
				e1.printStackTrace();
				finishExecution();
			}
		}
		
		for (Event e : dem) {
			try {
				Vm vm;
				Cloudlet cloudlet = (Cloudlet) e.getData();
				
				if (cloudlet.getVmId() == -1) { // schedule to a VM
					vm = getTargetVm();
					Log.printLine(">>>> vm.getId: " + vm.getId());
					cloudlet.setVmId(vm.getId());
				} else { // the cloudlet already bound to a VM 
					vm = mon.getVmManager().getById(cloudlet.getVmId());
				}
				
				e.setDest(vm.getHost().getDatacenter().getId());
				//Log.printLine("e: " + e);
				//Log.printLine("e.getVM(): " + ((Cloudlet) e.getData()).getVmId());
				mybroker.sendEvent(e);
			} catch (Exception e1) {
				e1.printStackTrace();
				finishExecution();
			}
		}
		
	}


	private Vm getTargetVm() {
		List<Vm> vms = new ArrayList<>(mon.getVmManager().getCreatedMap().values());
		return vms.get(vmRR++ % vms.size());
	}

	private int getTargetDc() {
		//Log.printLine("DCs(0): " + mybroker.getDcs().get(0));
		//Log.printLine("dcRR % DCs: " + mybroker.getDcs().get(dcRR++ % mybroker.getDcs().size()));
		return mybroker.getDcs().get(dcRR++ % mybroker.getDcs().size());
	}

	private void finishExecution() {
		double delay = 0;
		Log.printLine(">>> Ending simulation.");
		mybroker.sendEvent(new Event(delay, CloudSimTags.END_OF_SIMULATION));
	}

}
