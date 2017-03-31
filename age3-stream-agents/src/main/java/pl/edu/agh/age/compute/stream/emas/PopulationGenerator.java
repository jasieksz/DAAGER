package pl.edu.agh.age.compute.stream.emas;

import pl.edu.agh.age.compute.stream.Agent;

import java.util.List;

@FunctionalInterface
public interface PopulationGenerator<A extends Agent> {
	List<A> createPopulation();
}
