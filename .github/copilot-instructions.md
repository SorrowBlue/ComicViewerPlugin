# ComicViewerPlugin - Kotlin Multiplatform Compose Project

**Always follow these instructions first and only fallback to additional search and context gathering if the information here is incomplete or found to be in error.**

## Project Overview

ComicViewerPlugin is a Kotlin Multiplatform project using Jetpack Compose that targets Desktop (JVM) and potentially Android platforms. The project demonstrates a multiplatform Comic Viewer PDF plugin with custom build logic and static analysis.

## Working Effectively

### Prerequisites
- **Java 21**: REQUIRED. Set `JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64`
- **Network Access**: Some environments may have network restrictions preventing builds

### Initial Setup (Desktop-only Build)
```bash
# Set Java 21 (CRITICAL)
export JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64

# Make gradlew executable
chmod +x gradlew

# Check available tasks
./gradlew tasks
```

### Gradle Commands
```bash
# In UNRESTRICTED environments, these would work:
./gradlew tasks --all
./gradlew composeApp:build
./gradlew composeApp:run
./gradlew composeApp:createDistributable
./gradlew composeApp:packageDistributionForCurrentOS

# IMPORTANT: In sandbox/restricted environments, ALL Gradle commands fail due to:
# - Cannot resolve host dl.google.com  
# - Cannot resolve host central.sonatype.com
# - Missing plugin dependencies
# - Corrupted dependency caches
```

### **CRITICAL NETWORK LIMITATIONS** 
- **CONFIRMED: Cannot access dl.google.com or central.sonatype.com** in sandboxed environments
- **Android builds FAIL** due to network restrictions 
- **Compose lifecycle dependencies FAIL** in restricted environments
- **Gradle plugin resolution FAILS** for some plugins (foojay-resolver-convention)
- **NEVER CANCEL builds** - they may take 45+ minutes in some environments
- **If build fails with network errors, document the limitation and focus on available functionality**

### Working Commands (Verified)
```bash
# File and code analysis (GUARANTEED TO WORK)
find . -name "*.kt" -o -name "*.gradle*" -o -name "*.toml"
cat gradle/libs.versions.toml
grep -r "Platform" composeApp/src/

# Java setup verification (GUARANTEED TO WORK)  
export JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64
$JAVA_HOME/bin/java -version
ls /usr/lib/jvm/

# Gradle commands (MAY FAIL due to network restrictions)
./gradlew help                    # Will likely fail in sandbox
./gradlew tasks                   # Will likely fail in sandbox  
./gradlew composeApp:build        # Will likely fail in sandbox
```

### Testing
```bash
# Run unit tests -- takes 5-10 seconds. NEVER CANCEL.
./gradlew composeApp:test

# Run Detekt static analysis -- takes 10-15 seconds
./gradlew composeApp:detektDesktopMain

# Run all Detekt checks
./gradlew composeApp:detektDesktopAll
```

### Code Quality
```bash
# Format version catalog
./gradlew formatVersionCatalog

# Run Detekt with autocorrect (if available)
./gradlew composeApp:detektDesktopMain --auto-correct

# Always run before committing
./gradlew composeApp:detektDesktopAll
```

## Project Structure

### Key Directories
- **`composeApp/`**: Main multiplatform Compose application
  - `src/commonMain/kotlin/`: Shared Kotlin code
  - `src/desktopMain/kotlin/`: Desktop-specific code
  - `src/androidMain/kotlin/`: Android-specific code (may not build due to network issues)
- **`build-logic/`**: Custom Gradle plugins and build conventions
- **`gradle/libs.versions.toml`**: Version catalog for dependencies

### Important Files
- **`composeApp/build.gradle.kts`**: Main application build configuration
- **`build-logic/build.gradle.kts`**: Custom plugin definitions
- **`settings.gradle.kts`**: Project structure and plugin management
- **`gradle.properties`**: Gradle and JVM configuration

### Main Classes
- **`org.example.project.main.kt`**: Desktop application entry point
- **`org.example.project.App.kt`**: Main Compose UI
- **`org.example.project.Greeting.kt`**: Platform-specific greeting logic

## **Build Time Expectations**
- **Initial Gradle setup**: 10-15 seconds
- **Clean build**: 30-60 seconds. **NEVER CANCEL - set timeout to 90+ minutes**
- **Incremental builds**: 5-10 seconds
- **Detekt analysis**: 10-15 seconds
- **Test execution**: 5-10 seconds
- **Package creation**: 45-90 seconds. **NEVER CANCEL - set timeout to 120+ minutes**

## **Validation Scenarios**

After making changes, ALWAYS test:

1. **Basic Build Validation**:
   ```bash
   ./gradlew composeApp:build
   ```

2. **Desktop Application Test**:
   ```bash
   ./gradlew composeApp:run
   ```
   - Verify application window opens
   - Click "Click me!" button
   - Verify animated content appears with greeting text

3. **Static Analysis**:
   ```bash
   ./gradlew composeApp:detektDesktopMain
   ```

4. **Package Creation Test**:
   ```bash
   ./gradlew composeApp:createDistributable
   ```

## **Network Troubleshooting**

### **CONFIRMED Network Restrictions**
The sandbox environment **CANNOT ACCESS**:
- `dl.google.com` (Google Maven repository)
- `central.sonatype.com` (Sonatype Maven repository)

### Diagnostic Commands:
```bash
# Test network connectivity (will fail in sandbox)
curl -I https://dl.google.com/dl/android/maven2/      # Expected: Could not resolve host
curl -I https://central.sonatype.com/                 # Expected: Could not resolve host

# Check current Java version
java -version                                          # Shows: Java 17 (default)
$JAVA_HOME/bin/java -version                          # Shows: Java 21 (when set correctly)
```

### Workarounds for Network Issues:
1. **Document build failures** with exact error messages
2. **Focus on code analysis** and structure understanding
3. **Use help commands** that don't require network:
   ```bash
   ./gradlew help
   ./gradlew tasks --help
   ```
4. **Examine source code directly** instead of building

## Common Issues and Solutions

### "Could not resolve host" Errors
**Problem**: Network restrictions preventing repository access
**Error Examples**:
```
Could not resolve host: dl.google.com
Could not resolve host: central.sonatype.com
```
**Solution**: Document the limitation. Focus on source code analysis instead of builds.

### "Java 17 JVM" Error
**Problem**: Build requires Java 21 but finds Java 17
**Solution**: 
```bash
export JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64
./gradlew --stop  # Stop existing daemons
./gradlew help    # Test with correct Java
```

### "Plugin not found" Errors  
**Problem**: Network restrictions preventing plugin downloads
**Error Example**:
```
Plugin [id: 'org.gradle.toolchains.foojay-resolver-convention'] was not found
```
**Solution**: Document as network limitation. Cannot be resolved in restricted environments.

### "Configuration cache" Errors
**Problem**: Gradle configuration cache issues with network dependencies
**Solution**: Expected in restricted environments. Focus on available functionality.

### "Could not GET" Repository Errors
**Problem**: Cannot download dependencies from Maven repositories
**Solution**: This confirms network restrictions. Document and work with available code.

## Build Logic

### Custom Plugins (in build-logic/)
- **`DetektConventionPlugin`**: Configures Detekt static analysis
- **`EmptyPlugin`**: Placeholder plugin (TODO implementation)

### Version Catalog (gradle/libs.versions.toml)
- **Kotlin**: 2.2.0
- **Compose Multiplatform**: 1.8.2
- **AGP**: 8.2.0 (for Android, if network allows)
- **Detekt**: 1.23.8

## **Development Workflow for Restricted Environments**

**REALITY CHECK**: In sandbox environments, Gradle builds will likely fail completely due to network restrictions.

### Alternative Approach - Code Analysis and Structure Understanding

1. **ALWAYS set Java 21** (even though Gradle may not work):
   ```bash
   export JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64
   ```

2. **Focus on code structure analysis**:
   ```bash
   # Examine project structure
   find . -name "*.kt" | head -20
   
   # Analyze key files  
   cat gradle/libs.versions.toml
   cat composeApp/build.gradle.kts
   cat settings.gradle.kts
   ```

3. **Manual code review**:
   ```bash
   # Find main application entry points
   grep -r "fun main" composeApp/src/
   
   # Check for platform-specific code
   find composeApp/src -name "*Platform*"
   
   # Look for Compose UI components
   grep -r "@Composable" composeApp/src/
   ```

4. **Document what you learn** instead of attempting builds

5. **Provide instructions for unrestricted environments** where builds would work

## **Timeout Recommendations**

Always use these minimum timeout values:
- **Initial build**: 90+ minutes
- **Package creation**: 120+ minutes  
- **Clean build**: 60+ minutes
- **Detekt analysis**: 30+ minutes
- **Simple tasks**: 10+ minutes

## **Manual Testing Checklist**

When modifying the application:
- [ ] Application starts successfully
- [ ] Main window displays with Material3 theme
- [ ] "Click me!" button is visible and clickable
- [ ] Animated content appears/disappears on button click
- [ ] Platform-specific greeting shows correct platform info
- [ ] No console errors or exceptions
- [ ] Application closes cleanly

## Repository Commands Reference

```bash
# Quick status check
export JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64
./gradlew tasks

# Full build and test cycle
./gradlew composeApp:clean composeApp:build composeApp:test

# Quality checks
./gradlew composeApp:detektDesktopAll formatVersionCatalog

# Run and validate application
./gradlew composeApp:run

# Package for distribution
./gradlew composeApp:createDistributable
```

## **Commands That Work in Restricted Environments**

These commands are guaranteed to work even with network restrictions:

### File Analysis
```bash
# View project structure
find . -name "*.kt" -o -name "*.gradle*" -o -name "*.toml" | head -20

# Examine key configuration files
cat gradle/libs.versions.toml
cat settings.gradle.kts  
cat composeApp/build.gradle.kts

# Find source files by pattern
find composeApp/src -name "*.kt" | xargs grep -l "Platform"
grep -r "TODO\|FIXME" composeApp/src/
```

### Java Setup Verification
```bash
# Check available Java versions
ls /usr/lib/jvm/

# Set and verify Java 21
export JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64
$JAVA_HOME/bin/java -version

# Gradle help (works offline)
./gradlew help
./gradlew tasks --help
```

### Manual Code Quality Checks
```bash
# Check for common Kotlin code issues
grep -r "!!" composeApp/src/                    # Unsafe operations
grep -r "lateinit" composeApp/src/               # Late initialization
grep -r "@Suppress" composeApp/src/              # Suppressed warnings
find composeApp/src -name "*.kt" -exec wc -l {} + # Line counts per file
```

## **What You Can Actually Do in Restricted Environments**

Since builds will fail, focus on these **WORKING** analysis approaches:

### Project Structure Analysis (100% Reliable)
```bash
# Find all Kotlin and config files
find . -name "*.kt" -o -name "*.gradle*" -o -name "*.toml" | head -20

# Identify project entry points
grep -r "fun main" composeApp/src/                    # Result: main() in desktopMain
find composeApp/src -name "*Platform*"                # Result: 3 platform implementations

# Analyze UI components
grep -r "@Composable" composeApp/src/                 # Result: App.kt and MainActivity.kt
grep -r "MaterialTheme\|Button\|Text" composeApp/src/ # Find UI components

# Check dependencies and versions
cat gradle/libs.versions.toml                         # Shows: Kotlin 2.2.0, Compose 1.8.2
```

### Code Quality Analysis (100% Reliable)
```bash
# Check for code quality issues
grep -r "!!" composeApp/src/                          # Unsafe operations
grep -r "TODO\|FIXME" composeApp/src/                 # Development notes
grep -r "lateinit" composeApp/src/                    # Late initialization
grep -r "@Suppress" composeApp/src/                   # Suppressed warnings

# Count lines of code
find composeApp/src -name "*.kt" -exec wc -l {} +     # Project size metrics
```

### Architecture Understanding (100% Reliable)
```bash
# Examine multiplatform structure
ls -la composeApp/src/                                # Shows: androidMain, commonMain, desktopMain
cat composeApp/src/commonMain/kotlin/org/example/project/Platform.kt  # Interface definition
cat composeApp/src/desktopMain/kotlin/org/example/project/Platform.jvm.kt  # JVM implementation

# Build configuration analysis
cat composeApp/build.gradle.kts                       # Shows: multiplatform setup, dependencies
cat build-logic/build.gradle.kts                     # Shows: custom plugins
```

### What This Tells You About the Project
- **Multiplatform**: Targets Android, Desktop (JVM) 
- **UI Framework**: Jetpack Compose with Material3
- **Architecture**: Clean separation of platform-specific and common code
- **Build System**: Gradle with custom plugins and version catalogs
- **Code Quality**: Uses Detekt for static analysis
- **Entry Point**: Desktop app starts in `main.kt` with Compose Window