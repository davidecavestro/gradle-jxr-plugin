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

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.Copy

/**
 * JXR plugin entry point.
 *
 * @author Davide Cavestro
 */
class JxrPlugin implements Plugin<Project> {
    void apply(Project project) {
        project.task('jxr', type: JxrTask, group: 'Documentation')

        project.task ('copyJxrResources', type: Copy) << {

            //URL defaultStylesheetUrl = this.getClass().getResource("/net/davidecavestro/gradle/jxr/stylesheet.css");
            //def stylesheet = new URLResource(defaultStylesheetUrl);

            from(project.file ('stylesheet.css'))
            into('target/jxr')
        }
    }
}
