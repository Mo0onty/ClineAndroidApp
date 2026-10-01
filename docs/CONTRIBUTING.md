# Contributing to Cline Android

We welcome contributions from the community! This guide explains how to contribute to Cline Android.

## 🎯 Ways to Contribute

There are many ways to contribute:

- **Reporting Bugs**: Help us identify and fix issues
- **Suggesting Features**: Share your ideas for new features
- **Code Contributions**: Submit pull requests with bug fixes or features
- **Documentation**: Improve and expand documentation
- **Testing**: Help test new features and bug fixes
- **Translation**: Help translate the app to other languages
- **Community Support**: Help others in discussions and issues

## 🐛 Reporting Bugs

### Before Reporting

1. **Check existing issues**: Search [GitHub Issues](https://github.com/your-org/cline-android/issues) to see if the bug has already been reported
2. **Update to latest version**: Ensure you're using the latest version of the app
3. **Check documentation**: Review [README](../README.md) and [DEVELOPMENT](DEVELOPMENT.md)

### How to Report

Create a new issue with the following information:

1. **Title**: Clear and descriptive title
2. **Description**: Detailed description of the bug
3. **Steps to Reproduce**: Clear steps to reproduce the issue
4. **Expected Behavior**: What you expected to happen
5. **Actual Behavior**: What actually happened
6. **Screenshots/Video**: If applicable, include visual evidence
7. **Device Information**:
   - Device model
   - Android version
   - App version
   - Build number
8. **Logs**: Include relevant log output

### Bug Report Template

```markdown
## Description

A clear and concise description of what the bug is.

## Steps to Reproduce

1. Go to '...'
2. Click on '....'
3. Scroll down to '....'
4. See error

## Expected Behavior

A clear description of what you expected to happen.

## Actual Behavior

A clear description of what actually happened.

## Screenshots

If applicable, add screenshots to help explain your problem.

## Device Information

- Device: [e.g. Pixel 6]
- Android Version: [e.g. Android 14]
- App Version: [e.g. 1.0.0]
- Build Number: [e.g. 1]

## Additional Context

Add any other context about the problem here.

## Logs

```
[PASTE RELEVANT LOG OUTPUT HERE]
```
```

## 💡 Suggesting Features

### Before Suggesting

1. **Check existing issues**: Search for similar feature requests
2. **Review roadmap**: Check [Feature Roadmap](../README.md#-feature-roadmap) in README
3. **Consider scope**: Ensure the feature aligns with the project's goals

### How to Suggest

Create a new issue with:

1. **Title**: Clear feature name or description
2. **Description**: Detailed description of the feature
3. **Use Case**: Why this feature would be useful
4. **Proposed Solution**: Your ideas for implementation (optional)
5. **Alternatives**: Alternative solutions you've considered (optional)

### Feature Request Template

```markdown
## Feature Request

### Is your feature request related to a problem? Please describe.

A clear and concise description of what the problem is. Ex. I'm always frustrated when [...]

### Describe the solution you'd like

A clear description of what you want to happen.

### Describe alternatives you've considered

A clear description of any alternative solutions or features you've considered.

### Additional Context

Add any other context or screenshots about the feature request here.

### Priority

- [ ] Low
- [ ] Medium
- [ ] High

### Complexity

- [ ] Simple
- [ ] Medium
- [ ] Complex
```

## 👨‍💻 Code Contributions

### Before Contributing

1. **Read the documentation**:
   - [README](../README.md)
   - [Architecture](ARCHITECTURE.md)
   - [Development Guide](DEVELOPMENT.md)
   - [Build Guide](BUILD.md)

2. **Check existing issues and PRs**: Ensure your contribution doesn't duplicate existing work

3. **Discuss large changes**: Open a discussion for major features or architectural changes

### Getting Started

1. **Fork the repository**:
   ```bash
   git clone https://github.com/your-org/cline-android.git
   cd cline-android
   ```

2. **Set up development environment**: Follow [Build Guide](BUILD.md)

3. **Create a feature branch**:
   ```bash
   git checkout -b feature/your-feature
   ```

### Development Workflow

1. **Make changes**: Implement your feature or bug fix
2. **Follow coding guidelines**: See [Development Guide](DEVELOPMENT.md#-coding-guidelines)
3. **Write tests**: Add tests for new functionality
4. **Update documentation**: Update relevant documentation
5. **Test locally**: Ensure everything works as expected
6. **Commit changes**: Use [Conventional Commits](https://www.conventionalcommits.org/)
7. **Push to fork**:
   ```bash
   git push origin feature/your-feature
   ```
8. **Create Pull Request**: Go to GitHub and create a PR

### Pull Request Guidelines

1. **Use the PR template**: Fill out all required information
2. **Keep PRs focused**: One feature or bug fix per PR
3. **Write clear descriptions**: Explain what the PR does and why
4. **Include screenshots**: For UI changes, include before/after screenshots
5. **Reference issues**: Link to relevant issues using `Fixes #123` or `Closes #456`

### Pull Request Template

```markdown
## Description

Please include a summary of the change and which issue is fixed. Please also include relevant motivation and context.

Fixes # (issue)

## Type of Change

- [ ] Bug fix
- [ ] New feature
- [ ] Breaking change
- [ ] Documentation update
- [ ] Code refactoring
- [ ] Performance improvement
- [ ] Test addition/fix
- [ ] Other (please specify): _______

## Changes Made

- [ ] Change 1
- [ ] Change 2
- [ ] Change 3

## Testing

- [ ] Unit tests pass (`./gradlew :app:testStandardDebugUnitTest`)
- [ ] UI tests pass
- [ ] Manual testing completed
- [ ] All existing tests pass

## Screenshots (if applicable)

| Before | After |
|--------|-------|
| ![Before](url) | ![After](url) |

## Checklist

- [ ] My code follows the code style of this project
- [ ] I have performed a self-review of my code
- [ ] I have commented my code, particularly in hard-to-understand areas
- [ ] I have made corresponding changes to the documentation
- [ ] My changes generate no new warnings
- [ ] New and existing unit tests pass locally with my changes
- [ ] Any dependent changes have been merged
- [ ] I have read the [CONTRIBUTING.md](CONTRIBUTING.md)
```

### Code Review Process

1. **Initial Review**: Maintainers will review your PR within a few days
2. **Feedback**: You may receive requests for changes or clarifications
3. **CI Checks**: All CI checks must pass
   - Build must succeed
   - All tests must pass
   - Lint must pass
4. **Approval**: At least one maintainer must approve your PR
5. **Merge**: Maintainer will merge your PR, or you can merge if you have write access

### Common Review Comments

| Comment | Meaning | How to Fix |
|---------|---------|------------|
| "Please use DesignTokens" | Hardcoded colors/sizes | Replace with `DesignTokens.Colors.Primary` |
| "Add tests" | Missing test coverage | Add unit/UI tests |
| "Use ViewModel" | Business logic in Composable | Move logic to ViewModel |
| "Add documentation" | Missing docs | Update relevant docs |
| "Follow Kotlin conventions" | Style issues | Follow [Kotlin Style Guide](https://kotlinlang.org/docs/coding-conventions.html) |

## 📚 Contribution Areas

### Good First Contributions

These are great for new contributors:

- [ ] Fix typos in documentation
- [ ] Add missing documentation
- [ ] Write unit tests for existing code
- [ ] Fix lint warnings
- [ ] Improve error messages
- [ ] Add translations
- [ ] Update dependencies

### Intermediate Contributions

- [ ] Add new custom components
- [ ] Implement new screens
- [ ] Add new features to existing functionality
- [ ] Improve performance
- [ ] Fix bugs

### Advanced Contributions

- [ ] Architectural changes
- [ ] New subsystems
- [ ] Major refactoring
- [ ] Performance optimizations
- [ ] Security improvements

## 🎨 Design Contributions

### Design System

Help improve the design system:

- **New Components**: Add reusable Compose components
- **Design Tokens**: Add new tokens (colors, spacing, etc.)
- **Themes**: Improve theme system
- **Animations**: Add new animations

### Guidelines

1. **Follow Material Design 3**: All components should follow [MD3 Guidelines](https://m3.material.io/)
2. **Use DesignTokens**: All colors, spacing, typography should use tokens
3. **Accessibility**: Ensure components are accessible
4. **Responsive**: Components should work on all screen sizes
5. **Consistent**: Follow existing patterns and conventions

## 🧪 Testing Contributions

### Types of Tests

1. **Unit Tests**: Test individual functions and classes
2. **UI Tests**: Test Composable functions
3. **Instrumentation Tests**: Test on real devices
4. **Integration Tests**: Test component interactions

### How to Help

- **Add missing tests**: Write tests for untested code
- **Improve test coverage**: Add more test cases
- **Fix flaky tests**: Make tests more reliable
- **Improve test performance**: Speed up tests
- **Add test utilities**: Create helper functions for testing

### Test Guidelines

1. **Test one thing per test**: Each test should verify one specific behavior
2. **Use descriptive names**: Test names should describe what they test
3. **Keep tests fast**: Tests should run quickly
4. **Keep tests isolated**: Tests shouldn't depend on each other
5. **Test edge cases**: Include tests for error cases and edge conditions

## 🌍 Translation Contributions

### Adding a New Language

1. **Create language file**: Add `strings.xml` in `app/src/main/res/values-XX/` (where XX is language code)
2. **Translate all strings**: Translate all string resources
3. **Add language support**: Update `SystemLanguage.kt` if needed
4. **Test**: Verify the translation works in the app

### Translation Guidelines

1. **Be accurate**: Ensure translations are accurate and natural
2. **Be consistent**: Use consistent terminology
3. **Follow conventions**: Follow language-specific conventions
4. **Test in context**: Verify translations look good in the UI

## 📖 Documentation Contributions

### Areas Needing Documentation

- [ ] Architecture decisions
- [ ] API documentation
- [ ] Tutorials and guides
- [ ] Examples and code samples
- [ ] Troubleshooting guides
- [ ] FAQ

### Documentation Guidelines

1. **Be clear**: Use clear, simple language
2. **Be concise**: Get to the point quickly
3. **Use examples**: Include code examples where helpful
4. **Keep updated**: Update documentation when code changes
5. **Use consistent formatting**: Follow existing documentation style

## 🤝 Community Contributions

### Helping Others

- **Answer questions**: Help others in [GitHub Discussions](https://github.com/your-org/cline-android/discussions)
- **Review PRs**: Review and comment on other contributors' PRs
- **Triage issues**: Help categorize and prioritize issues
- **Test PRs**: Test other contributors' changes

### Community Guidelines

1. **Be respectful**: Always be respectful and professional
2. **Be helpful**: Try to help others solve their problems
3. **Be patient**: Remember that everyone is learning
4. **Be constructive**: Provide constructive feedback
5. **Follow Code of Conduct**: See [CODE_OF_CONDUCT.md](CODE_OF_CONDUCT.md)

## 🎓 Learning Resources

### Kotlin
- [Kotlin Documentation](https://kotlinlang.org/docs/home.html)
- [Kotlin for Beginners](https://kotlinlang.org/docs/getting-started.html)
- [Kotlin Koans](https://kotlinlang.org/docs/koans.html)

### Android
- [Android Developer Documentation](https://developer.android.com/docs)
- [Android Basics](https://developer.android.com/courses/android-basics-kotlin/course)
- [Android Architecture](https://developer.android.com/topic/architecture)

### Jetpack Compose
- [Compose Documentation](https://developer.android.com/jetpack/compose/documentation)
- [Compose Tutorials](https://developer.android.com/jetpack/compose/tutorial)
- [Compose Samples](https://github.com/android/compose-samples)

### Material Design
- [Material Design 3 Guidelines](https://m3.material.io/)
- [Material Design Components](https://m3.material.io/components)
- [Material Design for Android](https://developer.android.com/guide/topics/ui/look-and-feel)

### Coroutines and Flow
- [Kotlin Coroutines Guide](https://kotlinlang.org/docs/coroutines-guide.html)
- [Kotlin Flow Documentation](https://kotlinlang.org/docs/flow.html)
- [Coroutines Best Practices](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/-coroutine-scope/index.html)

## 🙏 Recognition

All contributions are appreciated and recognized:

- **Code Contributors**: Listed in [CONTRIBUTORS.md](CONTRIBUTORS.md)
- **Bug Reporters**: Mentioned in release notes
- **Feature Requesters**: Credited in implementation PRs
- **Testers**: Thanked in PR comments
- **Documentation Contributors**: Credited in docs

## 📞 Support

For questions about contributing:

1. **Check documentation**: Review this guide and other docs
2. **Search discussions**: Look for similar questions in [Discussions](https://github.com/your-org/cline-android/discussions)
3. **Ask a question**: Create a new discussion
4. **Contact maintainers**: Reach out via email or GitHub

## 📜 Code of Conduct

By participating in this project, you agree to abide by our [Code of Conduct](CODE_OF_CONDUCT.md). We expect all contributors to:

- Be respectful and inclusive
- Be professional and courteous
- Focus on what is best for the community
- Be open to different viewpoints and experiences
- Gracefully accept constructive criticism
- Follow the guidelines outlined in this document

## 📄 License

By contributing to Cline Android, you agree that your contributions will be licensed under the [MIT License](../LICENSE).

---

Thank you for contributing to Cline Android! Your contributions help make this project better for everyone.

*Last updated: $(date)*
