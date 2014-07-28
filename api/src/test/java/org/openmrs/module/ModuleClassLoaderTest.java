/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.test.BaseContextSensitiveTest;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class ModuleClassLoaderTest extends BaseContextSensitiveTest {
	
	Module mockModule;
	
	Map<String, String> mockModules;
	
	@Before
	public void before() {
		mockModule = new Module("mockmodule", "mockmodule", "org.openmrs.module.mockmodule", "author", "description", "1.0");
		mockModules = new HashMap<String, String>();
	}
	
	/**
	 * @verifies return true if file matches and openmrs version matches
	 * @see ModuleClassLoader#shouldResourceBeIncluded(Module, java.net.URL, String, java.util.Map)
	 */
	@Test
	public void shouldResourceBeIncluded_shouldReturnTrueIfFileMatchesAndOpenmrsVersionMatches() throws Exception {
		ModuleConditionalResource resource = new ModuleConditionalResource();
		resource.setPath("lib/mockmodule-api-1.10.jar");
		resource.setOpenmrsVersion("1.7-1.8,1.10-1.11");
		
		mockModule.getConditionalResources().add(resource);
		
		boolean result = ModuleClassLoader.shouldResourceBeIncluded(mockModule, URI.create(
		    "file://module/mockmodule/lib/mockmodule-api-1.10.jar").toURL(), "1.10.0-SNAPSHOT", mockModules);
		
		assertThat(result, is(true));
	}
	
	/**
	 * @verifies return false if file matches but openmrs version does not
	 * @see ModuleClassLoader#shouldResourceBeIncluded(Module, java.net.URL, String, java.util.Map)
	 */
	@Test
	public void shouldResourceBeIncluded_shouldReturnFalseIfFileMatchesButOpenmrsVersionDoesNot() throws Exception {
		ModuleConditionalResource resource = new ModuleConditionalResource();
		resource.setPath("lib/mockmodule-api-1.10.jar");
		resource.setOpenmrsVersion("1.7-1.8, 1.10-1.11");
		
		mockModule.getConditionalResources().add(resource);
		
		boolean result = ModuleClassLoader.shouldResourceBeIncluded(mockModule, URI.create(
		    "file://module/mockmodule/lib/mockmodule-api-1.10.jar").toURL(), "1.12.0-SNAPSHOT", mockModules);
		
		assertThat(result, is(false));
	}
	
	/**
	 * @verifies return true if file does not match and openmrs version does not match
	 * @see ModuleClassLoader#shouldResourceBeIncluded(Module, java.net.URL, String, java.util.Map)
	 */
	@Test
	public void shouldResourceBeIncluded_shouldReturnTrueIfFileDoesNotMatchAndOpenmrsVersionDoesNotMatch() throws Exception {
		ModuleConditionalResource resource = new ModuleConditionalResource();
		resource.setPath("lib/mockmodule-api.jar");
		resource.setOpenmrsVersion("1.10-1.11");
		
		mockModule.getConditionalResources().add(resource);
		
		boolean result = ModuleClassLoader.shouldResourceBeIncluded(mockModule, URI.create(
		    "file://module/mockmodule/lib/mockmodule-api-1.10.jar").toURL(), "1.9.8-SNAPSHOT", mockModules);
		
		assertThat(result, is(true));
	}
	
	/**
	 * @verifies return true if file matches and module version matches
	 * @see ModuleClassLoader#shouldResourceBeIncluded(Module, java.net.URL, String, java.util.Map)
	 */
	@Test
	public void shouldResourceBeIncluded_shouldReturnTrueIfFileMatchesAndModuleVersionMatches() throws Exception {
		ModuleConditionalResource resource = new ModuleConditionalResource();
		resource.setPath("lib/mockmodule-api-1.10.jar");
		
		ModuleConditionalResource.ModuleAndVersion module = new ModuleConditionalResource.ModuleAndVersion();
		module.setModuleId("module");
		module.setVersion("3.0-4.0,1.0-2.0");
		resource.getModules().add(module);
		
		mockModule.getConditionalResources().add(resource);
		
		mockModules.put("module", "1.1");
		
		boolean result = ModuleClassLoader.shouldResourceBeIncluded(mockModule, URI.create(
		    "file://module/mockmodule/lib/mockmodule-api-1.10.jar").toURL(), "1.10.0-SNAPSHOT", mockModules);
		
		assertThat(result, is(true));
	}
	
	/**
	 * @verifies return false if file matches and module version does not match
	 * @see ModuleClassLoader#shouldResourceBeIncluded(Module, java.net.URL, String, java.util.Map)
	 */
	@Test
	public void shouldResourceBeIncluded_shouldReturnFalseIfFileMatchesAndModuleVersionDoesNotMatch() throws Exception {
		ModuleConditionalResource resource = new ModuleConditionalResource();
		resource.setPath("lib/mockmodule-api-1.10.jar");
		
		ModuleConditionalResource.ModuleAndVersion module = new ModuleConditionalResource.ModuleAndVersion();
		module.setModuleId("module");
		module.setVersion("1.0-2.0");
		resource.getModules().add(module);
		
		mockModule.getConditionalResources().add(resource);
		
		mockModules.put("module", "3.0");
		
		boolean result = ModuleClassLoader.shouldResourceBeIncluded(mockModule, URI.create(
		    "file://module/mockmodule/lib/mockmodule-api-1.10.jar").toURL(), "1.10.0-SNAPSHOT", mockModules);
		
		assertThat(result, is(false));
	}
	
	/**
	 * @verifies return false if file matches and openmrs version matches but module version does not match
	 * @see ModuleClassLoader#shouldResourceBeIncluded(Module, java.net.URL, String, java.util.Map)
	 */
	@Test
	public void shouldResourceBeIncluded_shouldReturnFalseIfFileMatchesAndOpenmrsVersionMatchesButModuleVersionDoesNotMatch()
	        throws Exception {
		ModuleConditionalResource resource = new ModuleConditionalResource();
		resource.setPath("lib/mockmodule-api-1.10.jar");
		resource.setOpenmrsVersion("1.10");
		
		ModuleConditionalResource.ModuleAndVersion module = new ModuleConditionalResource.ModuleAndVersion();
		module.setModuleId("module");
		module.setVersion("1.0-2.0,4.0");
		resource.getModules().add(module);
		
		mockModule.getConditionalResources().add(resource);
		
		mockModules.put("module", "3.0");
		
		boolean result = ModuleClassLoader.shouldResourceBeIncluded(mockModule, URI.create(
		    "file://module/mockmodule/lib/mockmodule-api-1.10.jar").toURL(), "1.10.0-SNAPSHOT", mockModules);
		
		assertThat(result, is(false));
	}
	
	/**
	 * @verifies return false if file matches and module not found
	 * @see ModuleClassLoader#shouldResourceBeIncluded(Module, java.net.URL, String, java.util.Map)
	 */
	@Test
	public void shouldResourceBeIncluded_shouldReturnFalseIfFileMatchesAndModuleNotFound() throws Exception {
		ModuleConditionalResource resource = new ModuleConditionalResource();
		resource.setPath("lib/mockmodule-api-1.10.jar");
		
		ModuleConditionalResource.ModuleAndVersion module = new ModuleConditionalResource.ModuleAndVersion();
		module.setModuleId("module");
		module.setVersion("1.0-2.0");
		resource.getModules().add(module);
		
		mockModule.getConditionalResources().add(resource);
		
		mockModules.put("differentModule", "1.0");
		
		boolean result = ModuleClassLoader.shouldResourceBeIncluded(mockModule, URI.create(
		    "file://module/mockmodule/lib/mockmodule-api-1.10.jar").toURL(), "1.10.0-SNAPSHOT", mockModules);
		
		assertThat(result, is(false));
	}
	
	/**
	 * @verifies return true if file does not match and module version does not match
	 * @see ModuleClassLoader#shouldResourceBeIncluded(Module, java.net.URL, String, java.util.Map)
	 */
	@Test
	public void shouldResourceBeIncluded_shouldReturnTrueIfFileDoesNotMatchAndModuleVersionDoesNotMatch() throws Exception {
		ModuleConditionalResource resource = new ModuleConditionalResource();
		resource.setPath("lib/mockmodule-api.jar");
		
		ModuleConditionalResource.ModuleAndVersion module = new ModuleConditionalResource.ModuleAndVersion();
		module.setModuleId("module");
		module.setVersion("1.0-2.0");
		resource.getModules().add(module);
		
		mockModule.getConditionalResources().add(resource);
		
		mockModules.put("module", "3.0");
		
		boolean result = ModuleClassLoader.shouldResourceBeIncluded(mockModule, URI.create(
		    "file://module/mockmodule/lib/mockmodule-api-1.10.jar").toURL(), "1.10.0-SNAPSHOT", mockModules);
		
		assertThat(result, is(true));
	}
}
