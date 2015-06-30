package br.usp.icmc.lasdpc.cloudsim;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import br.usp.icmc.lasdpc.cloudsim.lists.VmList;


public abstract class Monitor {
	
	protected Map<Integer, List<Object>> values;
	protected VmList vmList;

	public Monitor() {
		values = new HashMap<Integer, List<Object>>();
		vmList = new VmList();
	}
	
	public abstract void get();

	
	public Map<Integer, List<Object>> getValues() {
		return values;
	}

	
	public void clearValues() {
		values.clear();
	}

	public void add(int tag, Object value) {
		if (!values.containsKey(tag)) {
			values.put(tag, new LinkedList<Object>());
		}
		
		values.get(tag).add(value);
	}
	
	public List<Object> getValueFromTag(int tag) {
		return values.get(tag);
	}

	public VmList getVmList() {
		return vmList;
	}

	public void setVmList(VmList vmList) {
		this.vmList = vmList;
	}
}
