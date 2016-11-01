# Coding Guidelines for AgE3

We base our guidelines loosely on [Google Java Guidelines](https://google.github.io/styleguide/javaguide.html).
Notes below are extensions to them.

## Formatting

* Use tabs for indentation.
* No missing braces - even one-liners should have braces.
* Annotations for fields should be placed in the same line.
* Annotations for methods - chopped down if more than one or two.

## Mutability, `final`

Decrease mutability and prefer immutable collections (from Javaslang or Guava).

Put the `final` keyword everywhere where you can - even on local variables.

## Naming conventions

* For read-only values we drop "get" from method names.

## Javadocs

Use Markdown in Javadocs, not raw HTML.

## Annotations

* Nullability of a field, a return value, a parameter, etc. should be annotated using the Checker Framework annotation:
  org.checkerframework.checker.nullness.qual.Nullable;
* Sometimes it’s worthy to annotate local variables and generics too.
* Nullability annotations should be placed in position closest to the type (i.e. `@Override @NonNull` and not
  `@NonNull @Override`).
* **Immutable**, **ThreadSafe** and **GuardedBy** are recommended when a class has required properties.
* We use annotations from `org.checkerframework.checker` if possible.
* Other annotations from Checker Framework are encouraged.

## Assertions

* Fail fast: use as much precondition methods (`requireNonNull`, `checkArgument`, `checkState`, etc.) as possible
  (in public methods).
* For non-public methods use assertions (possibly with description). Assertions are enabled by default in Gradle
  configuration and should always be.

## Checker Framework

We use [Checker Framework](http://types.cs.washington.edu/checker-framework/) for analysing some nullness-related
issues. If you want to check your code using it, you can execute `compileJava` task with the additional `withChecker`
property:

```bash
./gradlew compileJava -PwithChecker
```
