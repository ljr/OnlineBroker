package br.usp.icmc.lasdpc.cloudsim.examples.DCBroker;

import java.util.List;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.core.CloudSimTags;

import br.usp.icmc.lasdpc.cloudsim.Effector;
import br.usp.icmc.lasdpc.cloudsim.OnlineBroker;
import br.usp.icmc.lasdpc.cloudsim.aux.Event;

public class DCEffector extends Effector {

	private DatacenterBroker broker;
	
	public void setBroker(OnlineBroker broker) {
		this.broker = (DatacenterBroker) broker;
	}
	
	@Override
	public void set(List<Event> cap, List<Event> dem) {

		for (Event e : cap) {
			try {
				e.setDest(broker.getTargetDc());
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
					vm = broker.getTargetVm();
					cloudlet.setVmId(vm.getId());
				} else { // user already bound the cloudlet to a VM 
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
	
	private void finishExecution() {
		double delay = 0;
		mybroker.sendEvent(new Event(delay, CloudSimTags.END_OF_SIMULATION));
	}

}
