---
title: Coding Guidelines for AgE3
---

# Coding Guidelines for AgE3

We base our guidelines loosely on [Google Java Guidelines](https://google.github.io/styleguide/javaguide.html).
Notes below are extensions to them.

## Formatting

- Use tabs for indentation.
- No missing braces - even one-liners should have braces.
- Annotations for fields should be placed in the same line.
- Annotations for methods - chopped down if more than one or two.

If in doubt – refer to the provided IntelliJ IDEA formatter (`.ide/IDEA` directory).

## Mutability, `final`

Decrease mutability and prefer immutable collections (from Vavr or Guava).

Put the `final` keyword everywhere where you can – even on local variables.

## Naming conventions

- For read-only values we drop "get" from method names.

## Javadocs

Use Markdown in Javadocs, not raw HTML.

## Annotations

- Nullability of a field, a return value, a parameter, etc. should be annotated using the Checker Framework annotation:
  `org.checkerframework.checker.nullness.qual.Nullable`.
- Sometimes it’s worthy to annotate local variables and generics too.
- Nullability annotations should be placed in position closest to the type (i.e. `@Override final @NonNull` and not
  `@NonNull @Override final`).
- **Immutable**, **ThreadSafe** and **GuardedBy** are recommended when a class has required properties.
- We use annotations compatible with Error Prone.
  It is suggested to use at least following (beside required `Nullable` as mentioned before):
  - `javax.annotation.concurrent.GuardedBy`
  - `com.google.errorprone.annotations.Immutable`
  - `javax.annotation.concurrent.ThreadSafe`

## Assertions

- Fail fast: use as much precondition methods (`requireNonNull`, `checkArgument`, `checkState`, etc.) as possible
  (in public methods).
- For non-public methods use assertions (possibly with description). Assertions are enabled by default in Gradle
  configuration and should always be.

## Analysis tools

We use [Error Prone](http://http://errorprone.info) for additional analysis of code.
It is automatically enabled in Gradle build.

Additionally, it is recommended to use built-in IntelliJ or Eclipse code analysis.
For IntelliJ IDEA, the suggested inspection profiles are located in the `.ide/IDEA/inspectionProfiles` directory.
This directory need to be copied to your `.idea` subdirectory in the project. 
