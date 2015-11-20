package br.usp.icmc.lasdpc.cloudsim.examples.DynamicController;

import java.util.List;
import java.util.Map;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.CloudSimTags;

import br.usp.icmc.lasdpc.cloudsim.Effector;
import br.usp.icmc.lasdpc.cloudsim.aux.Event;
import br.usp.icmc.lasdpc.cloudsim.examples.DynamicController.PerformanceMonitor.MetaVm;

public class AllocationEffector extends Effector {


	private static final double DEFAULT_VM_START_DELAY = 40;
	
	private int dcRR;
	private int  vmRR;
	private Map<Integer, MetaVm> bleeding;
	private Map<Integer, MetaVm> starting;
	
	public AllocationEffector() {
		PerformanceMonitor mon = (PerformanceMonitor) mybroker.getMonitor();
		bleeding = mon.getBleeding();
		starting = mon.getStarting();
		dcRR = vmRR = 0;
	}
	
	private List<Event> vmStateMachine(List<Event> cap) {
		int ri = 0;
		int []rem = new int[bleeding.size()];
		
		for (int i = 0; i < cap.size(); i++) {
			Event e = cap.get(i);
			Vm vm = (Vm) e.getData();
			
			switch (e.getTag()) {
			case CloudSimTags.VM_CREATE:
			case CloudSimTags.VM_CREATE_ACK:
				
				if (bleeding.size() > 0) { // restore from bleeding
					bleeding.remove(0);
					rem[ri++] = i;
				} else { // set as starting
					starting.put(vm.getId(), 
							((PerformanceMonitor) mybroker.getMonitor()).new 
							MetaVm(DEFAULT_VM_START_DELAY, vm));
				}
				
				break;
				
			case Tags.BLEED:
				
				if (starting.size() > 0) { // remove from starting
					starting.remove(0);
				} else { // set to bleed
					double howLongToFinish = vm.getCurrentRequestedTotalMips()/60; 
					bleeding.put(vm.getId(), 
							((PerformanceMonitor) mybroker.getMonitor()).new
							MetaVm(howLongToFinish, vm));
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
				cap.add(new Event(0, CloudSimTags.VM_CREATE_ACK, v.getVm()));
				starting.remove(v.getVm().getId());
			}
		}

		for (MetaVm v: bleeding.values()) {
			if (CloudSim.clock() >= v.getDeadline()) {
				cap.add(new Event(0, CloudSimTags.VM_DESTROY_ACK, v.getVm()));
				bleeding.remove(v.getVm().getId());
			}
		}
		
		
		return cap;
	}
	
	@Override
	public void set(List<Event> cap, List<Event> dem) {
		for (Event e : vmStateMachine(cap)) {
			try {
				e.setDest(getTargetDc());
				mybroker.sendEvent(e);
			} catch (Exception e1) {
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
					cloudlet.setVmId(vm.getId());
				} else { // the cloudlet already bound to a VM 
					vm = mybroker.getMonitor().getVmManager().getById(cloudlet.getVmId());
				}
				
				e.setDest(vm.getHost().getDatacenter().getId());
				mybroker.sendEvent(e);
			} catch (Exception e1) {
				e1.printStackTrace();
				finishExecution();
			}
		}
		
	}


	private Vm getTargetVm() {
		List<Vm> vms = mybroker.getMonitor().getVmManager().getCreatedList(); 
		return vms.get(vmRR++ % vms.size());
	}

	private int getTargetDc() {
		return mybroker.getDcs().get(dcRR++ % mybroker.getDcs().size());
	}

	private void finishExecution() {
		double delay = 0;
		mybroker.sendEvent(new Event(delay, CloudSimTags.END_OF_SIMULATION));
	}

}
