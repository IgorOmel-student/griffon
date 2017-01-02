/*
 * Copyright 2008-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.codehaus.griffon.gradle.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

/**
 * @author Andres Almiray
 */
class AggregateCoberturaMergeTask extends DefaultTask {
    static final TASK_NAME = 'aggregateCoberturaMerge'

    private static final COBERTURA_MAIN_CLASS_NAME = 'net.sourceforge.cobertura.merge.Main'

    AggregateCoberturaExtension extension

    @TaskAction
    void merge() {
        List<String> args = []
        args << '--datafile'
        args << extension.coverageOutputDatafile.absolutePath
        args << '--basedir'
        args << project.projectDir.absolutePath
        project.subprojects.each { prj ->
            // if (prj.name.endsWith('-compile')) return
            if (prj.plugins.hasPlugin(AggregateCoberturaExtension.COBERTURA_NAME)) {
                File coverageOutputDatafile = prj.extensions.findByName(AggregateCoberturaExtension.COBERTURA_NAME).coverageOutputDatafile
                if (coverageOutputDatafile.exists()) {
                    args << "${coverageOutputDatafile.absolutePath - project.projectDir.absolutePath}"
                }
            }
        }

        Class mainClass = Thread.currentThread().contextClassLoader.loadClass(COBERTURA_MAIN_CLASS_NAME)
        mainClass.main(args as String[])
    }
}