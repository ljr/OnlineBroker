package br.usp.icmc.lasdpc.cloudsim.examples.DynamicController;

import java.util.ArrayList;
import java.util.List;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.CloudSimTags;

import br.usp.icmc.lasdpc.cloudsim.Effector;
import br.usp.icmc.lasdpc.cloudsim.OnlineBroker;
import br.usp.icmc.lasdpc.cloudsim.aux.Event;

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
		if (!cap.isEmpty()) {
			// NEED TO REMOVE VMs 
			// cap will have one element containing how many Vms will be destroyed. 
			// Remove those Vms booting first and the left, move from running to 
			// bleeding.
			if (cap.get(0).getTag() == Tags.DESTROY) {
				long howMany = (long) cap.get(0).getData();
				long inBoot = mon.getVmManager().getBooting().size();
				long left = howMany - inBoot;

				/*
				 * * inBoot > 0:
				 *
				 *  howMany   howMany        toBleed
				 *      |        |              ^     
				 *      v        v        /----´ `-----\
				 *  {::::;;;;;;;;;-------|~~~~~~~~~~~~~~
				 *   \---.   ,---/     inBoot          ^
				 *        `.´                          |
				 *     toCancel                     howMany 
				 *     
				 * * inBoot = 0: toBleed(howMany)
				 * 
				 * * Possible values:
				 * 
				 * 	left = howMany - inBoot
				 * 
				 * 	howMany		inBoot		left		toBleed		toCancel
				 * 	positve		0			howMany		howMany		0
				 * 	postive		positive	
				 * 		howMany	<	inBoot	negative	0			howMany
				 * 		howMany	= 	inBoot	zero		0			howMany
				 * 		howMany	>	inBoot	positive	left		inBoot
				 * 
				 * * Therefore:
				 *  	if inBoot == zero
				 *  			toBleed = howMany
				 *  			toCancel = 0
				 *  	else if howMany > inBoot
				 *  			toBleed = left
				 *  			toCancel = inBoot
				 *  	else
				 *  			toBleed = 0
				 *  			toCancel = howMany
				 */
				if (inBoot == 0) {
					mon.getVmManager().runningToBleeding(howMany);
				} else if (howMany > inBoot) {
					mon.getVmManager().runningToBleeding(left);
					mon.getVmManager().bootingToCanceled(inBoot);
				} else {
					mon.getVmManager().bootingToCanceled(howMany);
				}

			} else {
				// NEED TO CREATE VMs
				// cap will be a list of Events with the VM_CREATE_ACK tag. The 
				// The following algorithm will change as many Vms as possible
				// from bleeding to running state. 
				/*
				 * * inBleed > 0:
				 *
				 *  howMany   howMany        toCreate
				 *      |        |              ^     
				 *      v        v        /----´ `-----\
				 *  {::::;;;;;;;;;-------|~~~~~~~~~~~~~~
				 *   \---.   ,---/     inBleed         ^
				 *        `.´                          |
				 *       toRun                      howMany 
				 *     
				 * * inBleed = 0: toCreate(howMany)
				 * 
				 * * Possible values:
				 * 
				 * 	left = howMany - inBleed
				 * 
				 * 	howMany		inBleed		left		toCreate	toRun
				 * 	positve		0			howMany		howMany		0
				 * 	postive		positive	
				 * 		howMany	<	inBleed	negative	0			howMany
				 * 		howMany	= 	inBleed	zero		0			howMany
				 * 		howMany	>	inBleed	positive	left		inBleed
				 * 
				 * * Therefore:
				 *  	if inBleed == zero
				 *  			toCreate = howMany
				 *  	else if howMany > inBoot
				 *  			toCreate = left
				 *  			toRun = inBleed
				 *  	else
				 *  			toRun = howMany
				 */
				long howMany = (long) cap.size();
				long inBleed = mon.getVmManager().getBleeding().size();
				long left = howMany - inBleed;
				if (howMany > inBleed) {
					//				Log.printLine(">>> Here. howMany: " + howMany + ", inBleed: " + inBleed);
					mon.getVmManager().bleedingToRunning(inBleed);

					// remove inBleed events for creating, as Vms in bleeding can be
					// reused.
					for (int i = 0; i < inBleed; i++) {
						cap.remove(i);
					}

					// just check it.
					if (cap.size() != left) {
						Log.printLine("ERROR: Something wrong. "
								+ "`cap' MUST HAVE the same size of `left'");
						finishExecution();
					}
				} else {
					mon.getVmManager().bleedingToRunning(howMany);
					cap.clear();
				}

				// set Vms to boot.
				for (Event e : cap) {
					//				Log.printLine("e.getData(): " + e.getData());
					mon.getVmManager().newToBooting(/*vmId=*/ (int) e.getData(), 
							DEFAULT_VM_START_DELAY);	
				}
			}
		}
		
		// after booting Vms -> send events to Datacenter.
		cap.clear();
		for (Vm vm : mon.getVmManager().vmsToBeCreated(CloudSim.clock())) {
			cap.add(new Event(/*delay=*/ 0, CloudSimTags.VM_CREATE_ACK, vm));
		}
		
		return cap;
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
		List<Vm> vms = new ArrayList<>(mon.getVmManager().getRunning().values());
		Log.printLine("vms.size(): " + vms.size());
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
