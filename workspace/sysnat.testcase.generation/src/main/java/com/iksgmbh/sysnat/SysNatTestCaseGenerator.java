package com.iksgmbh.sysnat;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import com.iksgmbh.sysnat.domain.Filename;
import com.iksgmbh.sysnat.domain.JavaCommand;
import com.iksgmbh.sysnat.domain.JavaFieldData;
import com.iksgmbh.sysnat.domain.LanguageInstructionPattern;
import com.iksgmbh.sysnat.domain.LanguageTemplatePattern;
import com.iksgmbh.sysnat.domain.TestApplication;
import com.iksgmbh.sysnat.helper.JavaFileWriter;
import com.iksgmbh.sysnat.helper.LanguageInstructionCollector;
import com.iksgmbh.sysnat.helper.LanguageTemplateCollector;
import com.iksgmbh.sysnat.helper.LanguageTemplateContainerFinder;
import com.iksgmbh.sysnat.helper.TestSeriesBuilder;
import com.iksgmbh.sysnat.helper.PatternMergeJavaCommandGenerator;
import com.iksgmbh.sysnat.helper.CommandLibraryCreator;
import com.iksgmbh.sysnat.helper.JavaFileBuilder;

/**
 * Generates java test code from nlxx-files and executes it.
 * 
 * @author Reik Oberrath
 */
public class SysNatTestCaseGenerator 
{
	/**
	 * Reads natural language instruction files and
	 * transforms the instructions given in a domain language
	 * into java commands of JUnit java files.
	 */
	public static void doYourJob() {
		new SysNatTestCaseGenerator().generateJUnitTestCaseFiles();
	}

	protected void generateJUnitTestCaseFiles() 
	{
		// step 0: init
		final TestApplication testApplication = GenerationRuntimeInfo.getInstance().getTestApplication();
		
		// step 1: find languageTemplateContainer in java template
		final List<JavaFieldData> languageTemplateContainerJavaFields = 
				LanguageTemplateContainerFinder.findLanguageTemplateContainers(testApplication.getName());
		
		// step 2: read natural language patterns from LanguageTemplateContainers
		final HashMap<Filename, List<LanguageTemplatePattern>> languageTemplateCollection = 
				LanguageTemplateCollector.doYourJob(languageTemplateContainerJavaFields);
		CommandLibraryCreator.doYourJob(languageTemplateCollection);
		
		// step 3: read natural language patterns from instructions of nlxx and nls files
		final HashMap<Filename, List<LanguageInstructionPattern>> languageInstructionCollection = 
				LanguageInstructionCollector.doYourJob(testApplication.getName());
		
		// step 4: find matches between language patterns and template patterns
		//         and merge matches into java code
		final HashMap<Filename, List<JavaCommand>> javaCommandCollectionRaw = 
				PatternMergeJavaCommandGenerator.doYourJob(languageTemplateCollection, 
						                                   languageInstructionCollection,
						                                   testApplication.getName());
		
		// step 5: build test cases for Parameter-Tests
		final HashMap<Filename, List<JavaCommand>> javaCommandCollection = 
				TestSeriesBuilder.doYourJob(javaCommandCollectionRaw);
		
		// step 6: inject java commands in testCaseJavaTemplate and 
		//         create a JUnit test case
		final HashMap<File, String> javaFilesToCompile = 
				JavaFileBuilder.doYourJob(javaCommandCollection, 
						                  testApplication.getName(),
						                  languageTemplateContainerJavaFields);		

		// step 7: write JUnit test case to file in sysnat.test.execution
		JavaFileWriter.writeToTargetDir(javaFilesToCompile);  
	}
}
