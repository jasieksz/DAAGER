---
title: Contributing to AgE
---

# Contributing to AgE

Thank you for your interest in improving AgE3.

## Contributing code

1. Preferably, [create an issue](https://gitlab.com/age-agh/age3/issues) describing what you plan to do. 
2. Fork the project as described [here](https://docs.gitlab.com/ee/gitlab-basics/fork-project.html).
3. Create your own, separate branch, based on **develop**, if you are creating a new feature, or on **master** or an
   appropriate release branch, if you are fixing a bug.
4. Work on your code.
5. Push your changes and create a merge request to the source branch.
   - The branch used for merge request need to be **rebased** on the current commit in the source branch.
   - Your commit messages should reference your issue ID using `refs #id` or `fixes #id` (see
     [Gitlab docs](https://docs.gitlab.com/ee/user/project/issues/automatic_issue_closing.html)).
 
When writing commit messages, please follow [these guidelines](http://chris.beams.io/posts/git-commit/).
 
For recommendations on the code style see [coding guidelines](coding-guidelines.md). We also suggest using the IDE
configuration that is provided in the `.ide` directory in the main repository directory.

## Issues

We use the following component labels:

- ~Compute
- ~Core
- ~Documentation

We use the following types of issues that should be assigned to issues other than ~Documentation:

- ~bug
- ~feature
- ~maintenance

## Contributing documentation

As documentation is located in the same repository, follow the usual contributing guidelines for code.
 
When writing Markdown docs, please follow [this style guide](http://www.cirosantilli.com/markdown-style-guide/).
