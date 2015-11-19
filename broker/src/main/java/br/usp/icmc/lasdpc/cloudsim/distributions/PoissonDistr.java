package br.usp.icmc.lasdpc.cloudsim.distributions;

import org.apache.commons.math3.distribution.PoissonDistribution;

public class PoissonDistr implements DiscreteDistribution {
	/** The num gen. */
	private final PoissonDistribution numGen;
	
	/**
	 * Creates a new poisson number generator.
	 * 
	 * @param seed the seed to be used.
	 * @param mean the mean for the distribution.
	 */
	public PoissonDistr(long seed, long mean) {
		this(mean);
		numGen.reseedRandomGenerator(seed);
	}

	/**
	 * Creates a new exponential number generator.
	 * 
	 * @param mean the mean for the distribution.
	 */
	public PoissonDistr(long mean) {
		numGen = new PoissonDistribution(mean);
	}

	/**
	 * Generate a new random number.
	 * 
	 * @return the next random number in the sequence
	 */
	@Override
	public long sample() {
		return numGen.sample();
	}

}
