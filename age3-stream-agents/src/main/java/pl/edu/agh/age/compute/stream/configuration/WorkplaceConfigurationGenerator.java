package pl.edu.agh.age.compute.stream.configuration;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

import pl.edu.agh.age.compute.stream.AfterStepAction;
import pl.edu.agh.age.compute.stream.Agent;
import pl.edu.agh.age.compute.stream.BeforeStepAction;
import pl.edu.agh.age.compute.stream.Step;
import pl.edu.agh.age.compute.stream.emas.PopulationGenerator;

import java.util.List;
import java.util.stream.Collectors;

import javaslang.collection.Stream;

public final class WorkplaceConfigurationGenerator<T extends Agent> {

	private final int workplacesCount;

	private final PopulationGenerator<T> generator;

	private final BeforeStepAction<T> beforeStep;

	private final Step<T> step;

	private final AfterStepAction<T, ?> afterStep;

	public WorkplaceConfigurationGenerator(final int workplacesCount, final PopulationGenerator<T> generator,
	                                       final Step<T> step, final AfterStepAction<T, ?> afterStep,
	                                       final BeforeStepAction<T> beforeStep) {
		checkArgument(workplacesCount > 0, "Workplaces count must be grater than zero");
		this.workplacesCount = workplacesCount;
		this.generator = requireNonNull(generator);
		this.beforeStep = requireNonNull(beforeStep);
		this.step = requireNonNull(step);
		this.afterStep = requireNonNull(afterStep);
	}

	public List<WorkplaceConfiguration<T>> generateConfigurations() {
		return Stream.range(0, workplacesCount).map(i -> createConfiguration()).collect(Collectors.toList());
	}

	private WorkplaceConfiguration<T> createConfiguration() {
		return new WorkplaceConfiguration<>(generator.createPopulation(), step, afterStep, beforeStep);
	}

}
