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
	int toFailure;
	int toRun;
	int toBleed;
	int toDestroy;
	double bootTime;
	double bleedTime;
	private RealisticVmManager man;
	
	protected void setUp() {
		// use this to scale the number of VM in the operations.
		int scalar = 1;
		
		numberOfVms = 10 * scalar;
		
		toBoot = 8 * scalar;
		toFailure = 4 * scalar;
		toRun = 4 * scalar;
		toBleed = 2 * scalar;
		toDestroy = 2 * scalar;
		
		bootTime = 10 * scalar;
		bleedTime = 10 * scalar;
		
		man = new RealisticVmManager();

	}
	
	protected void tearDown() {
		
	}
	
	public void testLifeCycle() {
		// NEW 
		for (int i = 0; i < numberOfVms; i++) {
			man.newVm(i, 1, 1, 1, 1, 1, 1, "test", new CloudletSchedulerTimeShared());
		}
		// STATE: {vms: 10, failures:0, running: 0, destroyed: 0, booting: 0, bleeding: 0}
		assertEquals(numberOfVms, man.getVms().size());
		assertEquals(0, man.getFailures().size());
		assertEquals(0, man.getRunning().size());
		assertEquals(0, man.getDestroyed().size());
		assertEquals(0, man.getBooting().size());
		assertEquals(0, man.getBleeding().size());
		
		
		// TO BOOT
		for (int i = 0; i < toBoot; i++) {
			man.newToBooting(i, bootTime);
		}
		assertEquals(toBoot, man.getBooting().size());
		for (int i = 0; i < toBoot; i++) {
			assertEquals(bootTime, man.getBooting().get(i).getDeadline());	
		}
		// STATE: {vms: 10, failures:0, running: 0, destroyed: 0, booting: 8, bleeding: 0}
		assertEquals(numberOfVms, man.getVms().size());
		assertEquals(0, man.getFailures().size());
		assertEquals(0, man.getRunning().size());
		assertEquals(0, man.getDestroyed().size());
		assertEquals(toBoot, man.getBooting().size());
		assertEquals(0, man.getBleeding().size());
		
		
		// VM_CREATE_ACK
		double noNeedToCreate = bootTime - 1;
		double creationRequired = bootTime + 1;
		List<Vm> toCreate;
		toCreate =  man.vmsToBeCreated(noNeedToCreate);
		assertEquals(0, toCreate.size());
		toCreate =  man.vmsToBeCreated(creationRequired);
		assertEquals(toBoot, toCreate.size());
		// STATE: {vms: 10, failures:0, running: 0, destroyed: 0, booting: 8, bleeding: 0}
		assertEquals(numberOfVms, man.getVms().size());
		assertEquals(0, man.getFailures().size());
		assertEquals(0, man.getRunning().size());
		assertEquals(0, man.getDestroyed().size());
		assertEquals(toBoot, man.getBooting().size());
		assertEquals(0, man.getBleeding().size());
		
		
		// TO RUNNING
		// STATE: {vms: 10, failures:4, running: 4, destroyed: 0, booting: 0, bleeding: 0}
		for (int i = 0; i < toRun; i++) {
			man.bootingToRunning(new Ack(1, i, CloudSimTags.TRUE));
		}
		assertEquals(toRun, man.getRunning().size());
		assertEquals(toBoot - toRun, man.getBooting().size());
		// TO FAILURES
		for (int i = toRun; i < toBoot; i++) {
			man.bootingToRunning(new Ack(1, i, CloudSimTags.FALSE));
		}
		// STATE: {vms: 10, failures:4, running: 4, destroyed: 0, booting: 0, bleeding: 0}
		assertEquals(numberOfVms, man.getVms().size());
		assertEquals(toFailure, man.getFailures().size());
		assertEquals(toRun, man.getRunning().size());
		assertEquals(0, man.getDestroyed().size());
		assertEquals(0, man.getBooting().size());
		assertEquals(0, man.getBleeding().size());
		
		
		// RUNNING TO BLEEDING
		// ALL RUNNING VM TO BLEEDING
		for (int i = 0; i < toRun; i++) {
			man.runningToBleeding(i, bleedTime);
		}
		// STATE: {vms: 10, failures:4, running: 0, destroyed: 0, booting: 0, bleeding: 4}
		assertEquals(numberOfVms, man.getVms().size());
		assertEquals(toFailure, man.getFailures().size());
		assertEquals(0, man.getRunning().size());
		assertEquals(0, man.getDestroyed().size());
		assertEquals(0, man.getBooting().size());
		assertEquals(toRun, man.getBleeding().size());
		
		// BLEEDING TO RUNNING
		// RECOVERY HALF OF THE BLEEDING VMS
		// RECOVERY THOSE WITH BIGGER VM ID TO MAKE EASY IMPLEMENT NEXT LOOPS
		for (int i = toRun - 1; i >= toRun - toBleed; i--) {
			man.bleedingToRunning(i);
		}
		// STATE: {vms: 10, failures:4, running: 2, destroyed: 0, booting: 0, bleeding: 2}
		assertEquals(numberOfVms, man.getVms().size());
		assertEquals(toFailure, man.getFailures().size());
		assertEquals(toRun - toBleed, man.getRunning().size());
		assertEquals(0, man.getDestroyed().size());
		assertEquals(0, man.getBooting().size());
		assertEquals(toBleed, man.getBleeding().size());

		// BLEEDING TO DESTROYED
		// NO VM TO BE DESTROYED YET
		for (int i = 0; i < toDestroy; i++) {
			man.bleedingToDestroyed(bleedTime - 1);
		}
		// STATE: {vms: 10, failures:4, running: 2, destroyed: 0, booting: 0, bleeding: 2}
		assertEquals(numberOfVms, man.getVms().size());
		assertEquals(toFailure, man.getFailures().size());
		assertEquals(toRun - toBleed, man.getRunning().size());
		assertEquals(0, man.getDestroyed().size());
		assertEquals(0, man.getBooting().size());
		assertEquals(toBleed, man.getBleeding().size());
		
		// BLEEDING TO DESTROYED
		for (int i = 0; i < toDestroy; i++) {
			man.bleedingToDestroyed(bleedTime + i); // first test clock == bleedTime
		}
		// STATE: {vms: 10, failures:4, running: 2, destroyed: 1, booting: 0, bleeding: 0}
		assertEquals(numberOfVms, man.getVms().size());
		assertEquals(toFailure, man.getFailures().size());
		assertEquals(toRun - toBleed, man.getRunning().size());
		assertEquals(toDestroy, man.getDestroyed().size());
		assertEquals(0, man.getBooting().size());
		assertEquals(0, man.getBleeding().size());
	}

}
