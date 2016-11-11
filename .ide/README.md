IDE default settings
====================

This directory contains default settings for IDE-s. Configurations for IntelliJ IDEA and Eclipse are available.

IntelliJ IDEA
-------------

After creating a project, simply copy (or symlink) contents of `IDEA` directory to your `.idea` directory in the main
project dir: `cp -rfv .ide/IDEA/* .idea/`

### Contents

* copyright template,
* file templates,
* default inspection profile,
* run configurations:
  * `Node.xml` — node configuration with default classpath,
  * `Node_with_examples.xml` — node configuration with **age3-examples** in the classpath,
  * `Shell.xml` — console configuration with default classpath,
  * `Standalone_node_with_shell.xml` — standalone console configuration with default classpath,
* code style settings,
* modules configuration.

Both run and modules configurations depend on the IDEA 2016.1+ style of creating modules for Gradle projects (separate
module for each source set): https://www.jetbrains.com/idea/whatsnew/#v2016-1-gradle

Eclipse
-------

To import settings preferred by AgE to Eclipse, find: **Java / Code Style** options in Settings and import files from
the `Eclipse` directory.

There may be some differences regarding formatter behavior between Eclipse and IDEA.
