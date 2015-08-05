package br.usp.icmc.lasdpc.cloudsim.examples.DCBroker;

import java.util.List;

import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.CloudSimTags;

import br.usp.icmc.lasdpc.cloudsim.Effector;
import br.usp.icmc.lasdpc.cloudsim.aux.Event;

public class DCEffector extends Effector {

	@Override
	public void set(List<Event> cap, List<Event> dem) {

		if (cap.size() > 0) {
			for (Event e : cap) {
				e.setDest(getDCId());
			}
			mybroker.sendEvents(cap);
		}
		
		
		if (CloudSim.clock() == 100) {
			double delay = 0;
			mybroker.sendEvent(new Event(delay, CloudSimTags.END_OF_SIMULATION));
		}

	}
	
	
	private int getDCId() {
		return mybroker.getDcs().get(0);
	}

}
