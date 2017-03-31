package pl.edu.agh.age.compute.stream.emas.migration;

import static com.google.common.base.Preconditions.checkArgument;

public class MigrationParameters {

	private final long stepInterval;

	private final double migrationProbability;

	public MigrationParameters(final long stepInterval, final double migrationProbability) {
		checkArgument(stepInterval >= 0);
		checkArgument(migrationProbability >= 0 && migrationProbability <= 1,
		    "Migration probability has invalid value");
		this.stepInterval = stepInterval;
		this.migrationProbability = migrationProbability;
	}

	public long stepInterval() {
		return stepInterval;
	}

	public double migrationProbability() {
		return migrationProbability;
	}

}
