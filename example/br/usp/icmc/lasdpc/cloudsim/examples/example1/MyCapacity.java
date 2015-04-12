package br.usp.icmc.lasdpc.cloudsim.examples.example1;

import java.util.List;

import br.usp.icmc.lasdpc.cloudsim.Capacity;
import br.usp.icmc.lasdpc.cloudsim.Event;

public class MyCapacity extends Capacity {

	@Override
	public List<Event> update(List<Double> values) {
		System.out.println("MyCapacity.update() invoked.");
		return null;
	}

}
