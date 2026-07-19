<!-- 
- Please search for existing Pull Requests before submitting yours to avoid duplicates.
- Make sure your PR title strictly follows the format: "<type>: <description>" or "<type>(scope): <description>"
  (e.g., "fix: fix layout sorting glitch" or "feat(gui): add dynamic scaling option")
-->

## Description
Provide a concise summary of the changes introduced by this Pull Request, explaining what problem it solves, what feature it implements, or why this refactor/optimization is necessary.

## Related Issues
Fixes # (issue number) or Closes # (issue number)

## Type of Change
Please check the option that matches this PR (Ensure this aligns with your PR title prefix):
- [ ] `feat`: A new feature
- [ ] `fix`: A bug fix
- [ ] `perf`: A performance improvement / code optimization
- [ ] `refactor`: A code change that neither fixes a bug nor adds a feature
- [ ] `style`: Changes that do not affect the meaning of the code (formatting, missing semi-colons, etc.)
- [ ] `test`: Adding missing tests or correcting existing tests
- [ ] `build`: Changes to the build system or external dependencies (Gradle scripts, plugins, etc.)
- [ ] `ci`: Changes to our CI configuration files and automation scripts (GitHub Actions, publishing scripts)
- [ ] `docs`: Documentation updates
- [ ] `chore`: Housekeeping tasks (updating config files, `.gitignore`, etc.)

## Checklist
Before checking the boxes below, please make sure your environment is fully compiled and tested locally.

- [ ] PR title strictly follows the format `<type>: <description>` (or includes an optional subproject/module scope).
- [ ] I have verified that all source code and code comments are strictly restricted to **ASCII characters** (no localizations/emojis outside of translation files).
- [ ] My code adheres to the existing formatting, indentation rules, and overall code style of the project.
- [ ] For multi-version workspace setups, I have verified that my changes do not accidentally break compilation or runtime features in neighboring version subprojects.
- [ ] I have updated the README or associated documentation if my changes introduced new configuration options, commands, or user-facing adjustments.
