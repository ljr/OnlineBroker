package br.usp.icmc.lasdpc.cloudsim.examples.example1;

import java.util.List;
import java.util.Map;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.UtilizationModel;
import org.cloudbus.cloudsim.UtilizationModelFull;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.CloudSimTags;

import br.usp.icmc.lasdpc.cloudsim.Ack;
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
				
				setDemand((double) values.get(k).get(0));
				
				break;
				
			case CloudSimTags.CLOUDLET_SUBMIT_ACK:
				
				processCloudletAck(values.get(k));
				
				break;

			case CloudSimTags.CLOUDLET_RETURN:
				
				processCloudletReturn(values.get(k));
				
				break;
				
			default:
				break;
			}
		}

		return events;
	}

	private void processCloudletReturn(List<Object> cls) {
		for (Object c : cls) {
			Cloudlet cl = (Cloudlet) c;
			
			switch (cl.getStatus()) {
			case Cloudlet.SUCCESS:
				Log.printConcatLine(CloudSim.clock(), " [CLOUDLET] success executed.");
				break;

			case Cloudlet.FAILED:
			case Cloudlet.FAILED_RESOURCE_UNAVAILABLE:
				Log.printConcatLine(CloudSim.clock(), " [CLOUDLET] Failed at cloudlet creation.");
				submitCloudlet(cl);
				break;
				
			default:
				break;
			}
		}
	}

	private void processCloudletAck(List<Object> acks) {
		for (Object v : acks) {
			Ack va = (Ack) v;

			Log.printConcatLine(CloudSim.clock(), ": ", va);
		}
	}

	private void setDemand(double clock) {
		if (clock == 20) {
			submitCloudlet(newCloudlet());
		}
	}
	
	private void submitCloudlet(Cloudlet cl) {
		create(cl);
		events.add(new Event(capacity.getDatacenter(cl.getVmId()), 
				CloudSimTags.CLOUDLET_SUBMIT_ACK, cl));
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
		cloudlet.setVmId(chooseVm()); // TODO: where to set vm to execute this task?
		
		return cloudlet;
	}
	
	
	/**
	 * Pick the first active VM.
	 * 
	 * @return the chosen one.
	 */
	private int chooseVm() {
		for (int vmId : capacity.getVms().keySet()) {
			if (capacity.isVmActive(vmId)) {
				return vmId;
			}
		}
		
		return -1;
	}
	
}
