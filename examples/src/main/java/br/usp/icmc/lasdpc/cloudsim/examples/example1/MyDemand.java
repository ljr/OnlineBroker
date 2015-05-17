package br.usp.icmc.lasdpc.cloudsim.examples.example1;

import java.util.List;
import java.util.Map;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.UtilizationModel;
import org.cloudbus.cloudsim.UtilizationModelFull;
import org.cloudbus.cloudsim.core.CloudSimTags;

import br.usp.icmc.lasdpc.cloudsim.Demand;
import br.usp.icmc.lasdpc.cloudsim.Event;

public class MyDemand extends Demand {

	@Override
	public List<Event> update(Map<Integer, List<Object>> values) {
		
		// TODO: always clear events before calling it.
		events.clear();
		
		for (int k : values.keySet()) {
			switch (k) {
			case CloudSimTags.EXPERIMENT:
				double clock = (Double) values.get(k).get(0);
				if (clock == 20) {
					events.add(new Event(-1, CloudSimTags.CLOUDLET_SUBMIT_ACK, newCloudlet()));
				}
				break;

			default:
				break;
			}
		}

		return events;
	}

	private Cloudlet newCloudlet() {
		// Cloudlet properties
		int id = 0;
		long length = 400000;
		long fileSize = 300;
		long outputSize = 300;
		int pesNumber = 1; // number of cpus
		UtilizationModel utilizationModel = new UtilizationModelFull();

		Cloudlet cloudlet = new Cloudlet(id, length, pesNumber, fileSize, 
				outputSize, utilizationModel, utilizationModel, 
				utilizationModel);
		cloudlet.setUserId(mybroker.getId());
		//cloudlet.setVmId(vmid); // TODO: where to set vm to execute this task?
		
		return cloudlet;
	}
	
	
}
