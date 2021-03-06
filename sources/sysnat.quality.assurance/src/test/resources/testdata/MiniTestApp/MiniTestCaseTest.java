package com.iksgmbh.sysnat.test.integration.testcase;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.iksgmbh.sysnat.ExecutionRuntimeInfo;
import com.iksgmbh.sysnat.language_templates.common.LanguageTemplatesCommon;
import com.iksgmbh.sysnat.testcasejavatemplate.SysNatTestCase;

public class MiniTestCaseTest extends SysNatTestCase
{
	private LanguageTemplatesCommon languageTemplatesCommon;
	
	@Before
	public void setup() {
		languageTemplatesCommon = new LanguageTemplatesCommon(this);
		executionInfo.setTestApplicationName("com");
		super.setUp();
	}
	
	@After
	public void shutdown() 
	{
		super.shutdown();
	}
	
	
	@Override
	@Test
	public void executeTestCase() 
	{
		String testCaseName = this.getClass().getSimpleName();
		languageTemplatesCommon.startNewXX(testCaseName);
		addReportMessage("Start executing " + testCaseName + "...");
		languageTemplatesCommon.executeScript("MiniTestScript");
		addCommentToReport("PictureProof: " +
				executionInfo.getReportFolder() + "/" + getPictureProofName());
		addReportMessage("Done executing " + testCaseName + "!");
		
		closeCurrentTestCaseWithSuccess();
	}
}