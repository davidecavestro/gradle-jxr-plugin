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

import org.apache.maven.jxr.JXR
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.Task
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

/**
 * JXR invocation task.
 *
 * @author Davide Cavestro
 */
class JxrTask extends DefaultTask {
    @Input Collection<File> sourceDirs
    @Input String templatesDir
    @OutputDirectory File outputDir
    @Input String windowTitle
    @Input String docTitle
    @Input String footer
    @Input boolean linkToJavadoc
    
    @Input String outputEncoding
    @Input String inputEncoding
    
    boolean validProject

    JxrTask() {
        def javaProject = project.plugins.findPlugin('java')
        def groovyProject = project.plugins.findPlugin('groovy')
        validProject = javaProject || groovyProject

        if (validProject) {
            
            def candidates = []
            if (javaProject) {
                candidates.addAll project.sourceSets*.java.srcDirs
            }
            if (groovyProject) {
                candidates.addAll project.sourceSets*.groovy.srcDirs
            }
            sourceDirs = candidates.collect {it.path}.flatten().findAll {new File (it).exists ()}
            logger.debug ("JXR sourceDirs: $sourceDirs")
            outputDir = new File(project.buildDir, 'jxr')
            templatesDir = "templates"
            windowTitle = "$project.name sources documentation"
            docTitle = "$project.name sources documentation"
            footer = 'Produced by Gradle JXR plugin'
            
            outputEncoding = 'utf-8'
            inputEncoding = 'utf-8'
            
            def copyJxrResources = project.task ('copyJxrResources') {
                def styleSheetOutput = new File (outputDir, 'stylesheet.css')
                def cssPath = '/net/davidecavestro/gradle/jxr/stylesheet.css'
                
                JarURLConnection connection = (JarURLConnection) getClass().getResource (cssPath).openConnection();
                File file = new File(connection.getJarFileURL().toURI())
                
                inputs.file file //declare as input the jar containing the css
                outputs.file styleSheetOutput
                
                doLast {
                    outputDir.mkdirs()
                    //copies css contents
                    styleSheetOutput << JXR.class.getResourceAsStream(cssPath).text
                }
            }
            
            this.dependsOn copyJxrResources
        }
    }


    @TaskAction
    void generate() {
        if (!validProject) {
            throw new GradleException("The project is not configured with 'java' or 'groovy' plugin")
        }

        try {
            JXR jxr = new JXR ()
            jxr.setDest (outputDir.path)
            jxr.setLog (new JxrLog (logger: logger))
            
            //FIXME detect a unique encoding for sources
//            jxr.setOutputEncoding(outputEncoding);
//            jxr.setInputEncoding(inputEncoding);
            
            if (linkToJavadoc) {
                jxr.setJavadocLinkDir(footer)
            }

            jxr.xref (sourceDirs, templatesDir, windowTitle, docTitle, footer);
        }
        catch (Exception e) {
            logger.error('Error running jxr', e)
            throw new GradleException('Error running jxr', e)
        }

        logger.info ("JXR documentation generated at $outputDir")
    }
}
