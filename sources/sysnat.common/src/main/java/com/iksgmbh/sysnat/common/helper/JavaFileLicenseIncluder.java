/*
 * Copyright 2018 IKS Gesellschaft fuer Informations- und Kommunikationssysteme mbH
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
package com.iksgmbh.sysnat.common.helper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.iksgmbh.sysnat.common.utils.SysNatFileUtil;

/**
 * Finds java files in sources without licence declarations
 * 
 * @author Reik Oberrrath
 */
public class JavaFileLicenseIncluder 
{
	final static String DEFAULT_SRC_DIR = "..";
	final static String LICENSE_FILE = "./src/main/resources/License.txt";

	private String sourceDir;
	//private String licenseJavaComment;
	
	
	public JavaFileLicenseIncluder(String sourceDir) throws IOException {
		this.sourceDir = sourceDir;
		//licenseJavaComment = SysNatFileUtil.readTextFileToString(new File(LICENSE_FILE));
	}


	public static void main(String[] args) throws Exception {
		args = checkArguments(args);
		new JavaFileLicenseIncluder(args[0]).doYourJob();
	}
	
	
	private void doYourJob() throws Exception 
	{
		final List<String> subdirsToIgnore = new ArrayList<String>();
		subdirsToIgnore.add("\\target\\");
		subdirsToIgnore.add("\\cpsuite\\");
		final List<File> result = 
				FileFinder.findFiles(new File(sourceDir), null, 
						             subdirsToIgnore, "java", null, 
						             null);
				
		System.out.println("Mainfolder: " + sourceDir);
		System.out.println("Number of java files found:" + result.size());
		System.out.println("License has been included in following files:");
		int counter = 0;
		for (File file : result) 
		{
			String oldFileContent = SysNatFileUtil.readTextFileToString(file);
			if (! oldFileContent.contains("Copyright 2018 IKS Gesellschaft fuer Informations- und Kommunikationssysteme mbH")) {
				/*
				final String newFileContent = licenseJavaComment
						                      + System.getProperty("line.separator")
						                      + SysNatFileUtil.readTextFileToString(file);
				SysNatFileUtil.writeFile(file, newFileContent);
				*/
				System.out.println(file.getCanonicalPath());
				counter++;
			}
		}
		System.out.println("Number of modified java files:" + counter);
	}

	private static String[] checkArguments(String[] args) 
	{
		if (args.length > 1) {
			final String msg = "Illegal number of arguments. Only one argument is expected!";
			System.err.println(msg);
			throw new RuntimeException(msg);
		}
		
		if (args.length == 0)
		{
			args = new String[1];
			args[0] = DEFAULT_SRC_DIR;
		}
		
		return args;
	}
	
}