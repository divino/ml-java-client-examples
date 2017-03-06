/*
 * Copyright 2012-2016 MarkLogic Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.marklogic.example.ext;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.admin.ExtensionLibrariesManager;
import com.marklogic.client.admin.ExtensionMetadata;
import com.marklogic.client.admin.MethodType;
import com.marklogic.client.admin.ResourceExtensionsManager;
import com.marklogic.client.io.FileHandle;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.InputStreamHandle;
import com.marklogic.example.MarkLogicExamplesMain;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;


public class ResourceExtensions {

	public final static String MODULE_URI = "/ext/patch-doc.xqy";
	public final static String MODULE_FILE_PATH = "src/main/resources" + MODULE_URI;

	public static void loadModule(DatabaseClient client, String uri, String filePath) {
		ExtensionLibrariesManager libMgr =
				client.newServerConfigManager().newExtensionLibrariesManager();

		// specify metadata about the resource extension
		ExtensionMetadata metadata = new ExtensionMetadata();

		FileHandle handle =
				new FileHandle(new File(MODULE_FILE_PATH)).withFormat(Format.TEXT);

		libMgr.write(MODULE_URI, handle);
	}

	// install the resource extension on the server
	public static void installResourceExtension(DatabaseClient client, String name, String path){

		// create a manager for resource extensions
		ResourceExtensionsManager resourceMgr = client.newServerConfigManager().newResourceExtensionsManager();

		// specify metadata about the resource extension
		ExtensionMetadata metadata = new ExtensionMetadata();

		FileHandle handle =
				new FileHandle(new File(path)).withFormat(Format.TEXT);

		// write the resource extension to the database
		resourceMgr.writeServices(name, handle, metadata,
				new ResourceExtensionsManager.MethodParameters(MethodType.POST));
	}

	public static void main(String[] args) {
		MarkLogicExamplesMain main = new MarkLogicExamplesMain();
		//ResourceExtensions.loadModule(main.getDBClient(), MODULE_URI, MODULE_FILE_PATH);
		installResourceExtension(main.getDBClient(), MODULE_URI, MODULE_FILE_PATH);
	}

}
