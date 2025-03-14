/*
 * This file is part of the gradle-release plugin.
 *
 * (c) Eric Berry
 * (c) ResearchGate GmbH
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package com.calincosma.release

import com.calincosma.release.tasks.CheckSnapshotDependencies
import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.plugins.BasePlugin
import org.gradle.api.plugins.GroovyPlugin
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

public class ReleasePluginCheckSnapshotDependenciesTests extends Specification {

    Project project

    def setup() {
        project = ProjectBuilder.builder().withName("ReleasePluginTest").build()
        project.plugins.apply(BasePlugin.class)
        project.plugins.apply(GroovyPlugin.class)
        ReleasePlugin releasePlugin = project.plugins.apply(ReleasePlugin.class)
        project.extensions.release.scmAdapters = [TestAdapter]

        releasePlugin.createScmAdapter()
    }

    def 'when no deps then no exception'() {
        when:
        (project.tasks.checkSnapshotDependencies as CheckSnapshotDependencies).checkSnapshotDependencies()
        then:
        notThrown GradleException
    }

    def 'when no SNAPSHOT deps then no exception'() {
        given:
        project.configurations { custom }
        project.dependencies {
            compile 'my1:my1:1.1.1'
            custom 'my2:my2:1.1.1'
        }
        project.buildscript.dependencies {
            classpath 'my1:my1:1.1.1'
            classpath 'my2:my2:1.1.1'
        }
        when:
        (project.tasks.checkSnapshotDependencies as CheckSnapshotDependencies).checkSnapshotDependencies()
        then:
        notThrown GradleException
    }

    def 'when SNAPSHOT in plugin provide cfg then exception'() {
        given:
        project.dependencies { compile 'my:my:1.1.1-SNAPSHOT' }
        when:
        (project.tasks.checkSnapshotDependencies as CheckSnapshotDependencies).checkSnapshotDependencies()
        then:
        GradleException ex = thrown()
        ex.message =~ /my:my:1.1.1-SNAPSHOT/
    }

    def 'when SNAPSHOT in buildscript cfg then exception'() {
        given:
        project.buildscript.dependencies { classpath 'my:my:1.1.1-SNAPSHOT' }
        when:
        (project.tasks.checkSnapshotDependencies as CheckSnapshotDependencies).checkSnapshotDependencies()
        then:
        GradleException ex = thrown()
        ex.message =~ /my:my:1.1.1-SNAPSHOT/
    }

    def 'when SNAPSHOT in custom deps then exception'() {
        given:
        project.configurations { custom }
        project.dependencies { custom 'my:my:1.1.1-SNAPSHOT' }
        when:
        (project.tasks.checkSnapshotDependencies as CheckSnapshotDependencies).checkSnapshotDependencies()
        then:
        GradleException ex = thrown()
        ex.message =~ /my:my:1.1.1-SNAPSHOT/
    }

    def 'when SNAPSHOT in custom buildscript deps then exception'() {
        given:
        project.configurations { custom }
        project.buildscript.dependencies { classpath 'my:my:1.1.1-SNAPSHOT' }
        when:
        (project.tasks.checkSnapshotDependencies as CheckSnapshotDependencies).checkSnapshotDependencies()
        then:
        GradleException ex = thrown()
        ex.message =~ /my:my:1.1.1-SNAPSHOT/
    }

    def 'when SNAPSHOT in subprojects then exception'() {
        given:
        def proj1 = ProjectBuilder.builder().withParent(project).withName("proj1").build()
        proj1.apply plugin: 'java'
        proj1.dependencies { compile 'my1:my1:1.1.1-SNAPSHOT' }

        def proj2 = ProjectBuilder.builder().withParent(project).withName("proj2").build()
        proj2.apply plugin: 'java'
        proj2.dependencies { compile 'my2:my2:1.1.1' }

        when:
        (project.tasks.checkSnapshotDependencies as CheckSnapshotDependencies).checkSnapshotDependencies()
        then:
        GradleException ex = thrown()
        ex.message.contains 'proj1: [my1:my1:1.1.1-SNAPSHOT]'
        !ex.message.contains('my2:my2:1.1.1')
    }

    def 'when SNAPSHOT in subprojects buildscript then exception'() {
        given:
        def proj1 = ProjectBuilder.builder().withParent(project).withName("proj1").build()
        proj1.apply plugin: 'java'
        proj1.dependencies { compile 'my1:my1:1.1.1' }
        proj1.buildscript.dependencies { classpath 'my2:my2:1.1.1-SNAPSHOT' }

        def proj2 = ProjectBuilder.builder().withParent(project).withName("proj2").build()
        proj2.apply plugin: 'java'
        proj2.dependencies { compile 'my3:my3:1.1.1-SNAPSHOT' }
        proj2.buildscript.dependencies { classpath 'my4:my4:1.1.1-SNAPSHOT' }

        when:
        (project.tasks.checkSnapshotDependencies as CheckSnapshotDependencies).checkSnapshotDependencies()
        then:
        GradleException ex = thrown()
        ex.message.contains 'proj1: [my2:my2:1.1.1-SNAPSHOT]'
        ex.message.contains 'proj2: [my3:my3:1.1.1-SNAPSHOT, my4:my4:1.1.1-SNAPSHOT]'
        !ex.message.contains('my1:my1:1.1.1')
    }

    def 'when same SNAPSHOT in several configurations then show one in exception'() {
        given:
        project.dependencies {
            compile 'my:my:1.1.1-SNAPSHOT'
            runtime 'my:my:1.1.1-SNAPSHOT'
        }
        when:
        (project.tasks.checkSnapshotDependencies as CheckSnapshotDependencies).checkSnapshotDependencies()
        then:
        GradleException ex = thrown()
        ex.message.contains '[my:my:1.1.1-SNAPSHOT]'
    }

    def 'when same SNAPSHOT in several configurations - including buildscript - then show one in exception'() {
        given:
        project.dependencies {
            compile 'my:my:1.1.1-SNAPSHOT'
            runtime 'my:my:1.1.1-SNAPSHOT'
        }
        project.buildscript.dependencies {
            classpath 'my:my:1.1.1-SNAPSHOT'
        }
        when:
        (project.tasks.checkSnapshotDependencies as CheckSnapshotDependencies).checkSnapshotDependencies()
        then:
        GradleException ex = thrown()
        ex.message.contains '[my:my:1.1.1-SNAPSHOT]'
    }

    def 'when few SNAPSHOT deps in several configurations then show all in exception'() {
        given:
        project.dependencies {
            compile 'my1:my1:1.1.1-SNAPSHOT'
            runtime 'my2:my2:1.1.1-SNAPSHOT'
        }
        when:
        (project.tasks.checkSnapshotDependencies as CheckSnapshotDependencies).checkSnapshotDependencies()
        then:
        GradleException ex = thrown()
        ex.message.contains '[my1:my1:1.1.1-SNAPSHOT, my2:my2:1.1.1-SNAPSHOT]'
    }

    def 'when a SNAPSHOT dep is ignored then no exception'() {
        given:
        project.configurations { custom }
        project.dependencies { custom 'my:my:1.1.1-SNAPSHOT' }
        project.buildscript.dependencies {
            classpath 'my2:my2:1.1.1-SNAPSHOT'
        }
        project.extensions.release.ignoredSnapshotDependencies.set(['my:my', 'my2:my2'])
        when:
        (project.tasks.checkSnapshotDependencies as CheckSnapshotDependencies).checkSnapshotDependencies()
        then:
        notThrown GradleException
    }

  def 'when few SNAPSHOT deps in several configurations - including buildscript - then show all in exception'() {
        given:
        project.dependencies {
            compile 'my1:my1:1.1.1-SNAPSHOT'
            runtime 'my2:my2:1.1.1-SNAPSHOT'
        }
        project.buildscript.dependencies {
            classpath 'my3:my3:1.1.1-SNAPSHOT'
        }
        when:
        (project.tasks.checkSnapshotDependencies as CheckSnapshotDependencies).checkSnapshotDependencies()
        then:
        GradleException ex = thrown()
        ex.message.contains '[my1:my1:1.1.1-SNAPSHOT, my2:my2:1.1.1-SNAPSHOT, my3:my3:1.1.1-SNAPSHOT]'
    }
}
