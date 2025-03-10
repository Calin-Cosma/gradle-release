# gradle-release plugin

[![Build Status](https://github.com/researchgate/gradle-release/actions/workflows/tests.yaml/badge.svg)](https://github.com/researchgate/gradle-release/actions/workflows/tests.yaml)
[Gradle Plugin page](https://plugins.gradle.org/plugin/com.calincosma.release)

## Introduction

The gradle-release plugin is designed to work similar to the Maven release plugin.
The `gradle release` task defines the following as the default release process:

* The plugin checks for any un-committed files (Added, modified, removed, or un-versioned).
* Checks for any incoming or outgoing changes.
* Checkout to the release branch and merge from the working branch (optional, for GIT only, with `pushReleaseVersionBranch`)
* Removes the SNAPSHOT flag on your project's version (If used)
* Prompts you for the release version.
* Checks if your project is using any SNAPSHOT dependencies
* Will `build` your project.
* Commits the project if SNAPSHOT was being used.
* Creates a release tag with the current version.
* Checkout to the working branch (optional, for GIT only, with `pushReleaseVersionBranch`)
* Prompts you for the next version.
* Commits the project with the new version.

Current SCM support: [Bazaar](http://bazaar.canonical.com/en/), [Git](http://git-scm.com/) (1.7.2 or newer), [Mercurial](http://mercurial.selenic.com/), and [Subversion](http://subversion.apache.org/)

## Installation

The gradle-release plugin will work with Gradle 6.0 and beyond

### Legacy plugin application

```groovy
buildscript {
  repositories {
    maven {
      url 'https://plugins.gradle.org/m2/'
    }
  }
  dependencies {
    classpath 'com.calincosma:gradle-release:3.0.1'
  }
}

apply plugin: 'com.calincosma.release'
```

### Plugin DSL

```groovy
plugins {
  id 'com.calincosma.release' version '3.0.1'
}
```

Please refer to the [Gradle DSL PluginDependenciesSpec](http://www.gradle.org/docs/current/dsl/org.gradle.plugin.use.PluginDependenciesSpec.html) to
understand the behavior and limitations when using the new syntax to declare plugin dependencies.

## Usage

After you have your `build.gradle` file configured, simply run: `gradle release` and follow the on-screen instructions.

### Configuration

As described above, the plugin will check for un-committed files and SNAPSHOT dependencies.
By default the plugin will fail when any un-committed, or SNAPSHOT dependencies are found.

Below are some properties of the Release Plugin Convention that can be used to make your release process more lenient

<table border="0">
	<tr>
		<th>Name</th>
		<th>Default value</th>
		<th>Description</th>
	</tr>
	<tr>
		<td>failOnCommitNeeded</td>
		<td>true</td>
		<td>Fail the release process when there are un-committed changes. Will commit files automatically if set to false.</td>
	</tr>
	<tr>
		<td>failOnPublishNeeded</td>
		<td>true</td>
		<td>Fail when there are local commits that haven't been published upstream (DVCS support)</td>
	</tr>
	<tr>
		<td>failOnSnapshotDependencies</td>
		<td>true</td>
		<td>Fail when the project has dependencies on SNAPSHOT versions unless those SNAPSHOT dependencies have been defined as <i>'ignoredSnapshotDependencies'</i> using the syntax '$group:$name'</td>
	</tr>
	<tr>
		<td>failOnUnversionedFiles</td>
		<td>true</td>
		<td>Fail when files are found that are not under version control</td>
	</tr>
	<tr>
		<td>failOnUpdateNeeded</td>
		<td>true</td>
		<td>Fail when the source needs to be updated, or there are changes available upstream that haven't been pulled</td>
	</tr>
	<tr>
		<td>revertOnFail</td>
		<td>true</td>
		<td>When a failure occurs should the plugin revert it's changes to gradle.properties?</td>
	</tr>
	<tr>
		<td>pushReleaseVersionBranch</td>
		<td>null</td>
		<td>(GIT only) If set to the name of a branch, the `release` task will commit the release on this branch, and the next version on the working branch.</td>
	</tr>
</table>

Below are some properties of the Release Plugin Convention that can be used to customize the build<br>
<table>
	<tr>
		<th>Name</th>
		<th>Default value</th>
		<th>Description</th>
	</tr>
	<tr>
		<td>tagTemplate</td>
		<td>$version</td>
		<td>The string template which is used to generate the tag name. Possible variables are $version and $name. Example: '$name-$version' will result in "myproject-1.1.0". (Always ensure to use single-quotes, otherwise `$` is interpreted already in your build script)</td>
	</tr>
	<tr>
		<td>preCommitText</td>
		<td></td>
		<td>This will be prepended to all commits done by the plugin. A good place for code review, or ticket numbers</td>
	</tr>
	<tr>
		<td>preTagCommitMessage</td>
		<td>[Gradle Release Plugin] - pre tag commit: </td>
		<td>The commit message used to commit the non-SNAPSHOT version if SNAPSHOT was used</td>
	</tr>
	<tr>
		<td>tagCommitMessage</td>
		<td>[Gradle Release Plugin] - creating tag: </td>
		<td>The commit message used when creating the tag. Not used with BZR projects</td>
	</tr>
	<tr>
		<td>newVersionCommitMessage</td>
		<td>[Gradle Release Plugin] - new version commit:</td>
		<td>The commit message used when committing the next version</td>
	</tr>
	<tr>
		<td>snapshotSuffix</td>
		<td>-SNAPSHOT</td>
		<td>The version suffix used by the project's version (If used)</td>
	</tr>
</table>

Below are some properties of the Release Plugin Convention that are specific to version control.<br>
<table>
	<tr>
		<th>VCS</th>
		<th>Name</th>
		<th>Default value</th>
		<th>Description</th>
	</tr>
	<tr>
		<td>Git</td>
		<td>requireBranch</td>
		<td>main</td>
		<td>Defines the branch which releases must be done off of. Eg. set to `release` to require releases are done on the `release` branch (or use a regular expression to allow releases from multiple branches, e.g. `/release|main/`). Set to empty string "" to ignore.</td>
	</tr>
	<tr>
		<td>Git</td>
		<td>commitOptions</td>
		<td>{empty}</td>
		<td>Defines an array of options to add to the git adapter during a commit.  Example `commitOptions = ["-s"]`</td>
	</tr>
	<tr>
		<td>Git</td>
		<td>pushOptions</td>
		<td>{empty}</td>
		<td>Defines an array of options to add to the git adapter during a push.  This could be useful to have the vc hooks skipped during a release. Example `pushOptions = ["--no-verify"]`</td>
	</tr>
	<tr>
	    <td>Git</td>
	    <td>signTag</td>
	    <td>false</td>
	    <td>Adds `-s` parameter to the tag command</td>
	</tr>
</table>

To set any of these properties to false, add a "release" configuration to your project's ```build.gradle``` file. Eg. To ignore un-versioned files, you would add the following to your ```build.gradle``` file:

```
release {
  failOnUnversionedFiles = false
}
```

Eg. To ignore upstream changes, change 'failOnUpdateNeeded' to false:

```
release {
  failOnUpdateNeeded = false
}
```

This are all possible configuration options and its default values:

``` build.gradle
release {
    failOnCommitNeeded = true
    failOnPublishNeeded = true
    failOnSnapshotDependencies = true
    failOnUnversionedFiles = true
    failOnUpdateNeeded = true
    revertOnFail = true
    preCommitText = ''
    preTagCommitMessage = '[Gradle Release Plugin] - pre tag commit: '
    tagCommitMessage = '[Gradle Release Plugin] - creating tag: '
    newVersionCommitMessage = '[Gradle Release Plugin] - new version commit: '
    tagTemplate = '${version}'
    versionPropertyFile = 'gradle.properties'
    versionProperties = []
    snapshotSuffix = '-SNAPSHOT'
    buildTasks = []
    ignoredSnapshotDependencies = []
    versionPatterns = [
        /(\d+)([^\d]*$)/: { Matcher m, Project p -> m.replaceAll("${(m[0][1] as int) + 1}${m[0][2]}") }
    ]
    pushReleaseVersionBranch = null
    scmAdapters = [
        com.calincosma.release.GitAdapter,
        com.calincosma.release.SvnAdapter,
        com.calincosma.release.HgAdapter,
        com.calincosma.release.BzrAdapter
    ]

    git {
        requireBranch.set('main')
        pushToRemote.set('origin')
        pushToBranchPrefix.set('')
        commitVersionFileOnly.set(false)
        signTag.set(false)
    }

    svn {
        username.set(null)
        password.set(null)
        pinExternals.set(false)   // allows to pin the externals when tagging, requires subversion client >= 1.9.0
    }
}
```

### Kotlin DSL Example

``` build.gradle.kts
import com.calincosma.release.ReleaseExtension
repositories {
    maven {
      url 'https://plugins.gradle.org/m2/'
    }
  }
  dependencies {
    classpath 'com.calincosma:gradle-release:3.0.1'
  }

apply(plugin = "base")
apply(plugin = "com.calincosma.release")

configure<ReleaseExtension> {
    ignoredSnapshotDependencies.set(listOf("com.calincosma:gradle-release"))
    with(git) {
        requireBranch.set("master")
        // to disable branch verification: requireBranch.set(null as String?)
    }
}
```

### Custom release steps

To add a step to the release process is very easy. Gradle provides a very nice mechanism for [manipulating existing tasks](http://gradle.org/docs/current/userguide/tutorial_using_tasks.html#N102B2). There are two available hooks provided: `beforeReleaseBuild` which runs before build and `afterReleaseBuild` which runs afterwards.

For example, if we wanted to make sure `uploadArchives` is called and succeeds after the build with the release version has finished, we would just add the `uploadArchives` task as a dependency of the `afterReleaseBuild` task:

```groovy
afterReleaseBuild.dependsOn uploadArchives
```

### Multi-Project Builds

Support for [multi-project builds](http://gradle.org/docs/current/userguide/multi_project_builds.html) isn't complete, but will work given some assumptions. The gradle-release plugin assumes and expects that only one version control system is used by both root and sub projects.

Apply the plugin separately to each subproject that you wish to release. Release using a qualified task name, e.g.:

```bash
./gradlew :sub:release # release a subproject named "sub"
./gradlew :release # release the root project
```

### Working in Continuous Integration

In a continuous integration environment like Jenkins or Hudson, you don't want to have an interactive release process. To avoid having to enter any information manually during the process, you can tell the plugin to automatically set and update the version number.

You can do this by setting the `release.useAutomaticVersion` property on the command line, or in Jenkins when you execute gradle. The version to release and the next version can be optionally defined using the properties `release.releaseVersion` and `release.newVersion`.

```bash
gradle release -Prelease.useAutomaticVersion=true -Prelease.releaseVersion=1.0.0 -Prelease.newVersion=1.1.0-SNAPSHOT
```

## Getting Help

To ask questions please use stackoverflow or github issues.

* GitHub Issues: [https://github.com/researchgate/gradle-release/issues/new](https://github.com/researchgate/gradle-release/issues/new)
* Stack Overflow: [http://stackoverflow.com/questions/tagged/gradle-release-plugin](http://stackoverflow.com/questions/tagged/gradle-release-plugin)

To report bugs, please use the GitHub project.

* Project Page: [https://github.com/researchgate/gradle-release](https://github.com/researchgate/gradle-release)
* Reporting Bugs: [https://github.com/researchgate/gradle-release/issues](https://github.com/researchgate/gradle-release/issues)
