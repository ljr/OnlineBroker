package br.usp.icmc.lasdpc.cloudsim.aux.test;

import java.util.List;

import org.cloudbus.cloudsim.CloudletSchedulerTimeShared;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.core.CloudSimTags;

import br.usp.icmc.lasdpc.cloudsim.aux.Ack;
import br.usp.icmc.lasdpc.cloudsim.aux.VmManager;
import junit.framework.TestCase;

public class VmManagerTest extends TestCase {

	private int numberOfVms;
	int toBoot;
	int toCanceled;
	int toFailure;
	int toRun;
	int toBleed;
	int toDestroy;
	double bootTime;
	double bleedTime;
	private VmManager vmm;
	
	protected void setUp() {
		// use this to scale the number of VM in the operations.
		int scalar = 1;
		
		numberOfVms = 10 * scalar;
		
		toBoot = 9 * scalar;
		toCanceled = 1 * scalar;
		toFailure = 4 * scalar;
		toRun = 4 * scalar;
		toBleed = 2 * scalar;
		toDestroy = 2 * scalar;
		
		bootTime = 10 * scalar;
		bleedTime = 10 * scalar;
		
		vmm = new VmManager();

	}
	
	protected void tearDown() {
		
	}
	
	public void testLifeCycle() {
		// NEW 
		for (int i = 0; i < numberOfVms; i++) {
			try {
				vmm.newVm(i, 1, 1, 1, 1, 1, 1, "test", new CloudletSchedulerTimeShared());
			} catch (Exception e) {
				fail(e.getMessage());
			}
		}
		for (int i = 0; i < numberOfVms; i++) {
			try {
				vmm.newVm(i, 1, 1, 1, 1, 1, 1, "test", new CloudletSchedulerTimeShared());
				fail("Accepting to add repeated Vm.");
			} catch (Exception e) {
				
			}
		}
		
		// STATE: {vms: 10, failures:0, running: 0, destroyed: 0, booting: 0, canceled: 0, bleeding: 0}
		assertEquals(numberOfVms, vmm.getVms().size());
		assertEquals(0, vmm.getFailures().size());
		assertEquals(0, vmm.getRunning().size());
		assertEquals(0, vmm.getDestroyed().size());
		assertEquals(0, vmm.getBooting().size());
		assertEquals(0, vmm.getCanceled().size());
		assertEquals(0, vmm.getBleeding().size());
		
		
		// TO BOOT
		for (int i = 0; i < toBoot; i++) {
			if (!vmm.newToBooting(i, bootTime)) {
				fail("Could not put an existing Vm to boot.");
			}
		}
		if (vmm.newToBooting(toBoot + 1, bootTime)) {
			fail("Put a not existing machine into booting.");
		}
		assertEquals(toBoot, vmm.getBooting().size());
		for (int i = 0; i < toBoot; i++) {
			assertEquals(bootTime, vmm.getBooting().get(i).getDeadline());	
		}
		// STATE: {vms: 10, failures:0, running: 0, destroyed: 0, booting: 9, canceled: 0, bleeding: 0}
		assertEquals(numberOfVms, vmm.getVms().size());
		assertEquals(0, vmm.getFailures().size());
		assertEquals(0, vmm.getRunning().size());
		assertEquals(0, vmm.getDestroyed().size());
		assertEquals(toBoot, vmm.getBooting().size());
		assertEquals(0, vmm.getCanceled().size());
		assertEquals(0, vmm.getBleeding().size());
		
		
		// VM_CREATE_ACK
		double noNeedToCreate = bootTime - 1;
		double creationRequired = bootTime + 1;
		List<Vm> toCreate;
		toCreate =  vmm.vmsToBeCreated(noNeedToCreate);
		assertEquals(0, toCreate.size());
		toCreate =  vmm.vmsToBeCreated(creationRequired);
		assertEquals(toBoot, toCreate.size());
		// STATE: {vms: 10, failures:0, running: 0, destroyed: 0, booting: 9, canceled: 0, bleeding: 0}
		assertEquals(numberOfVms, vmm.getVms().size());
		assertEquals(0, vmm.getFailures().size());
		assertEquals(0, vmm.getRunning().size());
		assertEquals(0, vmm.getDestroyed().size());
		assertEquals(toBoot, vmm.getBooting().size());
		assertEquals(0, vmm.getCanceled().size());
		assertEquals(0, vmm.getBleeding().size());
		
		// TO CANCELED
		if (!vmm.bootingToCanceled(toCanceled)) {
			fail("Could not cancel this amount of Vms: " + toCanceled);
		}
		if (vmm.bootingToCanceled(0)) {
			fail("Must refuse to cancel Vm boot of vmId: " + (toBoot - 1));
		}
		// STATE: {vms: 10, failures:0, running: 0, destroyed: 0, booting: 8, canceled: 1, bleeding: 0}
		assertEquals(numberOfVms, vmm.getVms().size());
		assertEquals(0, vmm.getFailures().size());
		assertEquals(0, vmm.getRunning().size());
		assertEquals(0, vmm.getDestroyed().size());
		assertEquals(toBoot - toCanceled, vmm.getBooting().size());
		assertEquals(toCanceled, vmm.getCanceled().size());
		assertEquals(0, vmm.getBleeding().size());
		
		// TO RUNNING
		int dcId = 1;
		for (int i = 0; i < toRun; i++) {
			if (!vmm.bootingToRunning(new Ack(dcId, i, CloudSimTags.TRUE))) {
				fail("Could not put a booting Vm into running.");
			}
		}
		if (vmm.bootingToRunning(new Ack(dcId, 0, CloudSimTags.TRUE))) {
			fail("Error: put a running Vm from boot to running.");
		}
		assertEquals(toRun, vmm.getRunning().size());
		assertEquals(toBoot - toCanceled - toRun, vmm.getBooting().size());
		// TO FAILURES
		for (int i = toRun; i < toBoot - toCanceled; i++) {
			if (!vmm.bootingToRunning(new Ack(dcId, i, CloudSimTags.FALSE))) {
				fail("Could not put a booting Vm into failures.");
			}
		}
		if (vmm.bootingToRunning(new Ack(dcId, toRun, CloudSimTags.FALSE))) {
			fail("Error: put a running Vm from boot to failures.");
		}
		// STATE: {vms: 10, failures:4, running: 4, destroyed: 0, booting: 0, canceled: 1, bleeding: 0}
		assertEquals(numberOfVms, vmm.getVms().size());
		assertEquals(toFailure, vmm.getFailures().size());
		assertEquals(toRun, vmm.getRunning().size());
		assertEquals(0, vmm.getDestroyed().size());
		assertEquals(0, vmm.getBooting().size());
		assertEquals(toCanceled, vmm.getCanceled().size());
		assertEquals(0, vmm.getBleeding().size());
		
		
		// RUNNING TO BLEEDING
		// ALL RUNNING VM TO BLEEDING
		for (int i = 0; i < toRun; i++) {
			if (!vmm.runningToBleeding(i, bleedTime)) {
				fail("Could not put a running Vm into bleed.");
			}
		}
		if (vmm.runningToBleeding(0, bleedTime)) {
			fail("Error: put a bleeding Vm from running to bleeding.");
		}
		// STATE: {vms: 10, failures:4, running: 0, destroyed: 0, booting: 0, canceled: 1, bleeding: 4}
		assertEquals(numberOfVms, vmm.getVms().size());
		assertEquals(toFailure, vmm.getFailures().size());
		assertEquals(0, vmm.getRunning().size());
		assertEquals(0, vmm.getDestroyed().size());
		assertEquals(0, vmm.getBooting().size());
		assertEquals(toCanceled, vmm.getCanceled().size());
		assertEquals(toRun, vmm.getBleeding().size());
		
		// BLEEDING TO RUNNING
		// RECOVERY HALF OF THE BLEEDING VMS
		// RECOVERY THOSE WITH BIGGER VM ID TO MAKE EASY IMPLEMENT NEXT LOOPS
		for (int i = toRun - 1; i >= toRun - toBleed; i--) {
			if (!vmm.bleedingToRunning(i)) {
				fail("Cloud not put a bleeding Vm into running.");
			}
		}
		if (vmm.bleedingToRunning(toRun)) {
			fail("Error: put a running Vm from bleeding to running.");
		}
		// STATE: {vms: 10, failures:4, running: 2, destroyed: 0, booting: 0, canceled: 1, bleeding: 2}
		assertEquals(numberOfVms, vmm.getVms().size());
		assertEquals(toFailure, vmm.getFailures().size());
		assertEquals(toRun - toBleed, vmm.getRunning().size());
		assertEquals(0, vmm.getDestroyed().size());
		assertEquals(0, vmm.getBooting().size());
		assertEquals(toCanceled, vmm.getCanceled().size());
		assertEquals(toBleed, vmm.getBleeding().size());

		// BLEEDING TO DESTROYED
		// NO VM TO BE DESTROYED YET
		if (vmm.bleedingToDestroyed(bleedTime - 1)) {
			fail("A still bleeding Vm has been destroyed. Must be "
					+ "bleeding before clock: " + (bleedTime - 1));
		}
		// STATE: {vms: 10, failures:4, running: 2, destroyed: 0, booting: 0, canceled: 1, bleeding: 2}
		assertEquals(numberOfVms, vmm.getVms().size());
		assertEquals(toFailure, vmm.getFailures().size());
		assertEquals(toRun - toBleed, vmm.getRunning().size());
		assertEquals(0, vmm.getDestroyed().size());
		assertEquals(0, vmm.getBooting().size());
		assertEquals(toCanceled, vmm.getCanceled().size());
		assertEquals(toBleed, vmm.getBleeding().size());
		
		// BLEEDING TO DESTROYED
		if (!vmm.bleedingToDestroyed(bleedTime)) { // first test clock == bleedTime
			fail("No Vm has not been destroyed.");
		}
		// STATE: {vms: 10, failures:4, running: 2, destroyed: 2, booting: 0, canceled: 1, bleeding: 0}
		assertEquals(numberOfVms, vmm.getVms().size());
		assertEquals(toFailure, vmm.getFailures().size());
		assertEquals(toRun - toBleed, vmm.getRunning().size());
		assertEquals(toDestroy, vmm.getDestroyed().size());
		assertEquals(0, vmm.getBooting().size());
		assertEquals(toCanceled, vmm.getCanceled().size());
		assertEquals(0, vmm.getBleeding().size());
	}

}
