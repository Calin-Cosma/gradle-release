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

import org.gradle.api.GradleException
import org.gradle.api.Project

abstract class BaseScmAdapter extends PluginHelper {

    BaseScmAdapter(Project project, Map<String, Object> attributes) {
        this.project = project
        this.attributes = attributes
        extension = project.extensions['release'] as ReleaseExtension
    }

    abstract boolean isSupported(File directory)

    abstract void init()

    abstract void checkCommitNeeded()

    abstract void checkUpdateNeeded()

    abstract void createReleaseTag(String message)

    abstract void add(File file)

    abstract void commit(String message)

    abstract void revert()

    void checkoutMergeToReleaseBranch() {
        throw new GradleException("Checkout and merge is supported only for GIT projects")
    }

    void checkoutMergeFromReleaseBranch() {
        throw new GradleException("Checkout and merge is supported only for GIT projects")
    }
}
