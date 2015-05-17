package br.usp.icmc.lasdpc.cloudsim.examples.example1;

import java.util.List;

import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.CloudSimTags;

import br.usp.icmc.lasdpc.cloudsim.Effector;
import br.usp.icmc.lasdpc.cloudsim.Event;

public class MyEffector extends Effector {

	
	@Override
	public void set(List<Event> cap, List<Event> dem) {
		
		// TODO: always clear before processing
		clear();
		
		if (cap != null && cap.size() > 0) {
			// change delay of the events here...
			
			// set new values...
			mybroker.sendEvents(cap);
		}
		
		if (dem.size() > 0) {
			// change delay of the events here...
			
			// set new values...
			mybroker.sendEvents(dem);
		}
		
		if (CloudSim.clock() == 100) {
			double delay = 10;
			mybroker.sendEvent(new Event(delay, CloudSimTags.END_OF_SIMULATION));
		}
	}

}
