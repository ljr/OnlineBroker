package br.usp.icmc.lasdpc.cloudsim.examples.example1;

import java.util.List;
import java.util.Map;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.UtilizationModel;
import org.cloudbus.cloudsim.UtilizationModelFull;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.CloudSimTags;

import br.usp.icmc.lasdpc.cloudsim.Demand;
import br.usp.icmc.lasdpc.cloudsim.aux.Ack;
import br.usp.icmc.lasdpc.cloudsim.aux.Event;

public class MyDemand extends Demand {

	@Override
	public List<Event> update(Map<Integer, List<Object>> values) {
		
		cloudlets.clear();
		
		for (int k : values.keySet()) {
			switch (k) {
			case CloudSimTags.EXPERIMENT:
				
				setDemand((double) values.get(MonitorValues.CLOCK).get(0));
				
				break;
				
			case CloudSimTags.CLOUDLET_SUBMIT_ACK:
				
				processCloudletAck(values.get(CloudSimTags.CLOUDLET_SUBMIT_ACK));
				
				break;

			case CloudSimTags.CLOUDLET_RETURN:
				
				processCloudletReturn(values.get(CloudSimTags.CLOUDLET_RETURN));
				
				break;
				
			default:
				break;
			}
		}

		return cloudlets;
	}

	private void processCloudletReturn(List<Object> cls) {
		@SuppressWarnings("unchecked")
		List<Cloudlet> cll = (List<Cloudlet>)(Object) cls;
		for (Cloudlet c : cll) {
			switch (c.getStatus()) {
			case Cloudlet.SUCCESS:
				Log.printConcatLine(CloudSim.clock(), " [CLOUDLET] success executed.");
				break;

			case Cloudlet.FAILED:
			case Cloudlet.FAILED_RESOURCE_UNAVAILABLE:
				Log.printConcatLine(CloudSim.clock(), " [CLOUDLET] Failed at cloudlet creation.");
				break;
				
			default:
				
				break;
			}
		}
	}

	private void processCloudletAck(List<Object> acks) {
		@SuppressWarnings("unchecked")
		List<Ack> lacks = (List<Ack>)(Object) acks;
		for (Ack va : lacks) {
			va.setDesc("CLOUDLET");
			Log.printConcatLine(CloudSim.clock(), ": ", va);
		}
	}

	private void setDemand(double clock) {
		if (clock == 20) {
			Event e = new Event();
			e.setTag(CloudSimTags.CLOUDLET_SUBMIT_ACK);
			e.setData(newCloudlet());
			cloudlets.add(e);
		}
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
		
		return cloudlet;
	}
	

	
}
