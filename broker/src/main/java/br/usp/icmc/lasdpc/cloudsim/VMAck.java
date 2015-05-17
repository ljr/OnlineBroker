package br.usp.icmc.lasdpc.cloudsim;

public class VMAck {

	private int datacenterId;
	private int vmId;
	private int success;

	
	public VMAck(int datacenterId, int vmId, int success) {
		this.datacenterId = datacenterId;
		this.vmId = vmId;
		this.success = success;
	}
	
	
	public int getDatacenterId() {
		return datacenterId;
	}

	
	public void setDatacenterId(int datacenterId) {
		this.datacenterId = datacenterId;
	}
	

	public int getVmId() {
		return vmId;
	}
	

	public void setVmId(int vmId) {
		this.vmId = vmId;
	}

	
	public int getSuccess() {
		return success;
	}

	
	public void setSuccess(int success) {
		this.success = success;
	}

	@Override
	public String toString() {
		return "[VM_ACK] {datacenterId: " + datacenterId + ", vmId: " + vmId + 
				", success: " + success + "}";
	}
	
}
