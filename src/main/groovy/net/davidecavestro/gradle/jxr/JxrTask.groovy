/*
 * Copyright 2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.davidecavestro.gradle.jxr

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.InvalidUserDataException
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.apache.maven.jxr.JXR

/**
 * JXR invocation task.
 *
 * @author Davide Cavestro
 */
class JxrTask extends DefaultTask {
    @Input Collection<File> sourceDirs
    @Input File templatesDir
    @OutputDirectory File outputDir
    @Input String windowTitle
    @Input String docTitle
    @Input String footer
    
    boolean validProject

    JxrTask() {
        validProject = project.plugins.findPlugin('java')

        if (validProject) {
            sourceDirs = project.sourceSets*.java.srcDirs.collect {it.path}
            outputDir = new File(project.buildDir, 'jxr')
            windowTitle = "$project.name sources documentation"
            docTitle = "$project.name sources documentation"
            footer = 'Produced by Gradle JXR plugin'
        }
    }


    @TaskAction
    void generate() {
        if (!validProject) {
            throw new GradleException("The project is not configured with 'java' plugin")
        }

        try {
            JXR jxr = new JXR ()
            jxr.setDest (outputDir.path)
            jxr.setLog (new JxrLog (logger: logger));

            jxr.xref (sourceDirs, templatesDir.absolutePath, windowTitle, docTitle, footer);
        }
        catch (Exception e) {
            logger.error('Error running jxr', e)
            throw new GradleException('Error running jxr', e)
        }
    }
}
