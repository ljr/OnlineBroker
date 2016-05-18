package br.usp.icmc.lasdpc.cloudsim.examples.DynamicController.test;

import java.util.List;

import org.cloudbus.cloudsim.CloudletSchedulerTimeShared;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.core.CloudSimTags;

import br.usp.icmc.lasdpc.cloudsim.aux.Ack;
import br.usp.icmc.lasdpc.cloudsim.examples.DynamicController.RealisticVmManager;
import junit.framework.TestCase;

public class RealisticVmManagerTest extends TestCase {

	private int numberOfVms;
	int toBoot;
	double bootTime;
	private RealisticVmManager man;
	
	protected void setUp() {
		numberOfVms = 10;
		toBoot = 8;
		bootTime = 10;
		
		man = new RealisticVmManager();
		

	}
	
	protected void tearDown() {
		
	}
	
	public void testLifeCycle() {
		// NEW
		for (int i = 0; i < numberOfVms; i++) {
			man.newVm(i, 1, 1, 1, 1, 1, 1, "test", new CloudletSchedulerTimeShared());
		}
		assertEquals(numberOfVms, man.getVms().size());
		
		// TO BOOT
		for (int i = 0; i < toBoot; i++) {
			man.newToBooting(i, bootTime);
		}
		assertEquals(toBoot, man.getBooting().size());
		for (int i = 0; i < toBoot; i++) {
			assertEquals(bootTime, man.getBooting().get(i).getDeadline());	
		}
		
		// VM_CREATE_ACK
		double noNeedToCreate = bootTime + 1;
		double creationRequired = bootTime - 1;
		List<Vm> toCreate;
		toCreate =  man.vmsToBeCreated(noNeedToCreate);
		assertEquals(0, toCreate.size());
		toCreate =  man.vmsToBeCreated(creationRequired);
		assertEquals(toBoot, toCreate.size());
		
		// TO RUNNING
		for (int i = 0; i < toBoot/2; i++) {
			man.bootingToRunning(new Ack(1, i, CloudSimTags.TRUE));
		}
	}

}
