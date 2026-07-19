# Contributing

Thank you for your interest in contributing to this project! We welcome issues, bug fixes, features, and constructive feedback.

By participating in this project, you agree to abide by our guidelines and ensure that your contributions conform to the project specifications.

---

## How to Contribute

### 1. Reporting Bugs
- Search existing issues to ensure the bug hasn't been reported yet.
- Use our structured [Bug Report Template](https://github.com/pug523/shelf/issues/new?template=bug_report.yaml) to file a new issue.
- Provide clear steps to reproduce, Minecraft/Fabric versions, and log files (`latest.log` / crash reports) where applicable.

### 2. Suggesting Features
- Open a [Feature Request](https://github.com/pug523/shelf/issues/new?template=feature_request.yaml) to discuss potential new options, GUI widgets, or layout engine adjustments.
- For major architectural transformations, please open an Issue to discuss the design before starting to write code.

### 3. Commit Messages & PR Titles
To keep the project history clean and scannable, we follow standard Conventional Commits. **Your pull request title should use the following format:**

`type(scope): description` *(Note: `(scope)` is optional but encouraged, e.g., `feat(gui): ...` or `fix(26.2): ...`)*

#### Allowed Types:
- `feat`: A new feature
- `fix`: A bug fix
- `perf`: A code change that improves performance
- `refactor`: A code change that neither fixes a bug nor adds a feature
- `style`: Changes that do not affect the meaning of the code (white-space, formatting, missing semi-colons, etc.)
- `test`: Adding missing tests or correcting existing tests
- `build`: Changes that affect the build system or external dependencies (e.g., Gradle dependencies, plugins)
- `ci`: Changes to our CI configuration files and scripts (e.g., GitHub Actions, Modrinth publishing scripts)
- `docs`: Documentation-only changes
- `chore`: Other changes that don't modify src or test files (e.g., updating `.gitignore`)

*Example: `feat(layout): add custom sorting widget`*

### 4. Submitting Pull Requests (PRs)
Before writing any code, please keep the following project rules in mind:

1. **Match the Title Format**: Ensure your PR title starts with one of the prefixes defined in the Conventional Commits section above.
2. **No Non-ASCII Characters in Code**: All source code files (excluding language/translation `.json` files) must be strictly restricted to ASCII characters. Do not include Japanese, emojis, or any other non-ASCII text inside source code or code comments (use standard plain English instead).
3. **Keep PRs Focused**: Do not bundle multiple unrelated fixes or features into a single PR. Create separate branches and PRs for distinct problems.
4. **Code Style Alignment**: Ensure your IDE settings match the existing formatting of the codebase (e.g., indentation, line spaces). If the project includes code formatting validation via Gradle, run verification tasks locally before pushing.

#### Development Workflow:
1. Fork the repository and create your feature branch from `main`.
2. This repository uses a multi-version Gradle subproject structure. To launch the test client and preview changes for a specific target version, run `./gradlew :26.2:runClient`.
3. Verify that your changes do not accidentally break compilation or functionality in other module versions if your code modifies shared modules.
4. Push to your fork and submit a Pull Request to the `main` branch.

## Legalities
By contributing to this repository, you agree that your contributions will be licensed under the project's current open-source license.
