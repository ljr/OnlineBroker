package br.usp.icmc.lasdpc.cloudsim.examples.DCBroker;

import java.util.List;
import java.util.Map;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.core.CloudSimTags;

import br.usp.icmc.lasdpc.cloudsim.Demand;
import br.usp.icmc.lasdpc.cloudsim.aux.Event;

public class BatchDemand extends Demand {

	@Override
	public List<Event> update(Map<Integer, List<Object>> values) {
		boolean isAllVmsCreated = (boolean) values.get(DCMonitor.VMS_STATUS).get(0);

		if (isAllVmsCreated) {
			double delay = 0;
			int tag = CloudSimTags.CLOUDLET_SUBMIT_ACK;
			for (Cloudlet c : mybroker.getMonitor().getCloudletList()) {
				cloudlets.add(new Event(delay, tag, c));
			}
		}
		
		return cloudlets;
	}

}
