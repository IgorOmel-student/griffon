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
package org.codehaus.griffon.runtime.util

import com.google.guiceberry.GuiceBerryModule
import com.google.guiceberry.junit4.GuiceBerryRule
import com.google.inject.AbstractModule
import griffon.core.ApplicationClassLoader
import griffon.core.env.Environment
import griffon.core.env.Metadata
import griffon.core.resources.ResourceHandler
import griffon.util.CompositeResourceBundleBuilder
import griffon.util.ConfigReader
import griffon.util.PropertiesReader
import org.codehaus.griffon.runtime.core.DefaultApplicationClassLoader
import org.codehaus.griffon.runtime.core.env.EnvironmentProvider
import org.codehaus.griffon.runtime.core.env.MetadataProvider
import org.codehaus.griffon.runtime.core.resources.DefaultResourceHandler
import org.codehaus.griffon.runtime.groovy.util.GroovyAwareCompositeResourceBundleBuilder
import org.junit.Rule
import spock.lang.Specification

import javax.inject.Inject
import javax.inject.Singleton

import static com.google.inject.util.Providers.guicify

class GroovyAwareCompositeResourceBundleBuilder2Spec extends Specification {
    @Rule
    public final GuiceBerryRule guiceBerry = new GuiceBerryRule(TestModule)

    @Inject
    private CompositeResourceBundleBuilder bundleBuilder

    void setupSpec() {
        System.setProperty(Environment.KEY, 'test')
    }

    void cleanupSpec() {
        System.setProperty(Environment.KEY, 'dev')
    }

    def "Load Groovy and properties bundles"() {
        given:
        ResourceBundle bundle = bundleBuilder.create('org.codehaus.griffon.runtime.util.GroovyBundle')

        expect:
        bundle.getString(key) == value

        where:
        key              || value
        'properties.key' || 'properties'
        'groovy.key'     || 'test'
    }

    static final class TestModule extends AbstractModule {
        @Override
        protected void configure() {
            install(new GuiceBerryModule())
            bind(ApplicationClassLoader).to(DefaultApplicationClassLoader).in(Singleton)
            bind(ResourceHandler).to(DefaultResourceHandler).in(Singleton)
            bind(PropertiesReader).toProvider(guicify(new PropertiesReader.Provider()))
            bind(ConfigReader).toProvider(guicify(new ConfigReader.Provider()))
            bind(Metadata).toProvider(guicify(new MetadataProvider()))
            bind(Environment).toProvider(guicify(new EnvironmentProvider()))
            bind(CompositeResourceBundleBuilder).to(GroovyAwareCompositeResourceBundleBuilder).in(Singleton)
        }
    }
}
