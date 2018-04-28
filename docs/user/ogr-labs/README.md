---
title: AgE3 Implementation of OGR and LABS problems
---

# AgE3 Implementation of OGR and LABS problems

This module provides an implementation of OGR (Optimal Golomb Ruler) and LABS (Low Autocorrelation Binary Sequences) problems.

## Running the code from an IDE

### IntelliJ Idea

1. Navigate to *Run > Edit configurations...* and create a new configuration under the **Application** section.
2. Fill the configuration settings as follows and then press *Apply* and *OK*:

#### NodeBootstrapper (configuration for each computation passed separately via program arguments)

```
Main class:                pl.edu.agh.age.compute.stream.bootstrapper.StreamNodeBootstrapper
Program arguments:         pl/edu/agh/age/labs/labs-config-iterative-improvement.xml,pl/edu/agh/age/labs/labs-config.properties
Working directory:         <repository path>\age3-ogr-labs
Use classpath of module:   age3-ogr-labs_main
```

#### ConsoleBootstrapper (configuration for each computation genereted from a single JavaScript file)

```
Main class:                pl.edu.agh.age.console.ConsoleBootstrapper
Program arguments:         standalone pl/edu/agh/age/labs/labs-console.js
Working directory:         <repository path>\age3-ogr-labs
Use classpath of module:   age3-ogr-labs_main
```

### Eclipse

1. Install **ANSI Escape in Console** addon from Eclipse Marketplace (*Help > Eclipse Marketplace*).
2. Navigate to *Run > Run configurations...* and create a new configuration under the **Java Application** section.
3. Enter the following settings and then press *Apply* and *Run*:

#### NodeBootstrapper (configuration for each computation passed separately via program arguments)

```
Project:                age3-ogr-labs
Main class:             pl.edu.agh.age.compute.stream.bootstrapper.StreamNodeBootstrapper
Program arguments:      pl/edu/agh/age/labs/labs-config-iterative-improvement.xml,pl/edu/agh/age/labs/labs-config.properties
```

#### ConsoleBootstrapper (configuration for each computation generated from a single JavaScript file)

```
Project:                age3-ogr-labs
Main class:             pl.edu.agh.age.console.ConsoleBootstrapper
Program arguments:      standalone pl/edu/agh/age/labs/labs-console.js
```

---

## Running the Gradle build

### IntelliJ Idea

1. Navigate to `build.gradle` file inside a desired project, e.g. `age-ogr-labs` project.
2. Open the build cofiguration creator window, using a *Create 'build'...* option from a context menu.
3. Ensure that **Module** and **Working directory** point to a location of a proper project.
4. In the **Script parameters** section, type the following command, click *OK* and run the newly generated configuration:

#### Node Task (configuration for each computation passed separately via program arguments)

```
node -PappArgs="['pl/edu/agh/age/labs/labs-config-iterative-improvement.xml,pl/edu/agh/age/labs/labs-config.properties']"
```

#### Console Task (configuration for each computation generated from a single JavaScript file)

```
console -PappArgs="['pl/edu/agh/age/labs/labs-console.js']"
```

### Eclipse
1. Install **ANSI Escape in Console** addon from Eclipse Marketplace (*Help > Eclipse Marketplace*).
2. Navigate to *Run > Run configurations...* and create a new configuration under **Gradle Project** section.
3. In **Gradle Tasks** section type ``node`` (when passing each xml configuration manually) or ``console`` (when using JavaScript configuration file).
4. Select a proper project workspace directory under **Working directory** section, e.g. location of `age-ogr-labs` project.
5. Uncheck **Show Execution View** and check **Show console** in **Build Execution** section.
5. Navigate to *Arguments* tab, type the following command in **Program Arguments** section and then click *Apply* and *Run*:

#### Node Task (configuration for each computation passed separately via program arguments)

```
-PappArgs="pl/edu/agh/age/labs/labs-config-iterative-improvement.xml,pl/edu/agh/age/labs/labs-config.properties"
```

#### Console Task (configuration for each computation genereted from a single JavaScript file)

```
-PappArgs="pl/edu/agh/age/labs/labs-console.js"
```

### Console
1. Make sure `GRADLE_HOME` and `PATH` environmental variables are set.
2. Navigate to the root directory of a project you want to run, e.g. `age-ogr-labs`.
3. Run the following command from command prompt:

#### Node Task (configuration for each computation passed separately via program arguments)

```
gradle node -PappArgs="['pl/edu/agh/age/labs/labs-config-iterative-improvement.xml,pl/edu/agh/age/labs/labs-config.properties']"
```

#### Console Task (configuration for each computation generated from a single JavaScript file)

```
gradle console -PappArgs="['pl/edu/agh/age/labs/labs-console.js']"
```

