package br.usp.icmc.lasdpc.cloudsim.examples.example1;

import java.util.List;
import java.util.Map.Entry;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.CloudSimTags;

import br.usp.icmc.lasdpc.cloudsim.Effector;
import br.usp.icmc.lasdpc.cloudsim.aux.Event;

public class MyEffector extends Effector {

	
	@Override
	public void set(List<Event> cap, List<Event> dem) {
		if (cap != null && cap.size() > 0) {
			// change delay of the events here...
			for (Event e : cap) {
				e.setDest(chooseDC());
			}
			
			
			// set new values...
			mybroker.sendEvents(cap);
		}
		
		if (dem.size() > 0) {
			/*
			 * To send a Cloudlet to be executed, it must be specified:
			 *   1. DatacenterId of Host where the VM is instantiated;
			 *   2. Set the vmId where the Cloudlet will be executed;
			 */
			Vm vm;
			for (Event e : dem) {
				// TODO: do the cloudlets biding to VMs.
				vm = chooseVM();
				((Cloudlet) e.getData()).setVmId(vm.getId());

				e.setDest(vm.getHost().getDatacenter().getId());
				
				// change delay of the events here...
				e.setDelay(0);
			}
			
			// set new values...
			mybroker.sendEvents(dem);
		}
		
		if (CloudSim.clock() == 1000) {
			double delay = 10;
			mybroker.sendEvent(new Event(delay, CloudSimTags.END_OF_SIMULATION));
		}
	}

	private Vm chooseVM() {
		// TODO SCHEDULE A CLOUDLET TO A VM.
		Vm vm = null;
		
		for (Entry<Integer, Vm> e : getMybroker().getMonitor().getVmList().getVms().entrySet()) {
			vm = e.getValue();
		}
		
		return vm;
	}

	
	private int chooseDC() {
		int dc = -1;
		
		for (int d : getMybroker().getDcs()) {
			dc = d;
		}
		
		return dc;
	}
	
	
}
