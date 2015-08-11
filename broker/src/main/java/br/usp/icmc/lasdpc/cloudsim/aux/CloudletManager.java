package br.usp.icmc.lasdpc.cloudsim.aux;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cloudbus.cloudsim.Cloudlet;

public class CloudletManager {

		private Map<Integer, Cloudlet> cloudlets;
		private Map<Integer, Cloudlet> submitted;
		private Map<Integer, Cloudlet> received;
		
		
		
		public CloudletManager() {
			cloudlets = new HashMap<Integer, Cloudlet>();
			submitted = new HashMap<Integer, Cloudlet>();
			received = new HashMap<Integer, Cloudlet>();
		}
	
		public Collection<Cloudlet> getCloudlets() {
			return cloudlets.values();
		}
		
		public void add(Cloudlet cloudlet) {
			cloudlets.put(cloudlet.getCloudletId(), cloudlet);
		}
		
		public void add(List<Cloudlet> cloudlets) {
			for (Cloudlet cloudlet : cloudlets) {
				add(cloudlet);
			}
		}
		
		public void submit(Cloudlet cloudlet) {
			submitted.put(cloudlet.getCloudletId(), cloudlets.remove(cloudlet.getCloudletId()));
		}
		
		public void submit(List<Cloudlet> cloudlets) {
			for (Cloudlet cloudlet : cloudlets) {
				submit(cloudlet);
			}
		}
		
		public void receive(Cloudlet cloudlet) {
			received.put(cloudlet.getCloudletId(), submitted.remove(cloudlet.getCloudletId()));
		}
		
		public void receive(List<Cloudlet> cloudlets) {
			for (Cloudlet cloudlet : cloudlets) {
				receive(cloudlet);
			}
		}
		
		public boolean isThereCloudletsToBeSubmitted() {
			return cloudlets.size() > 0;
		}
		
		public boolean allCloudletsProcessed() {
			return cloudlets.size() == 0 && submitted.size() == 0; 
		}
		
		public Cloudlet getById(int cloudletId) {
			return cloudlets.get(cloudletId);
		}
}
