package br.usp.icmc.lasdpc.cloudsim.distributions;

/**
 * Interface to be implemented by a random number generator.
 * 
 * @author Lourenço Alves Pereira Júnior
 */
public interface DiscreteDistribution {

	/**
	 * Sample the random number generator.
	 * 
	 * @return The sample
	 */
	long sample();

}
