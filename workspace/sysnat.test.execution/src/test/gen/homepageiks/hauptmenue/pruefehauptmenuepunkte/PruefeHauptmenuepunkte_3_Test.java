package homepageiks.hauptmenue.pruefehauptmenuepunkte;

import org.junit.Test;
import org.junit.After;
import org.junit.Before;

import com.iksgmbh.sysnat.testcasejavatemplate.TestCaseTemplateParent;
import com.iksgmbh.sysnat.language_templates.common.LanguageTemplatesCommon;
import com.iksgmbh.sysnat.language_templates.homepageiks.LanguageTemplatesHomePageIKSBasics;
import com.iksgmbh.sysnat.language_templates.common.LanguageTemplatesPrint;
import java.io.File;

/**
 * Executable Example for TestApplication 'HomePageIKS'.
 * Autogenerated by SysNatTesting.
 */
public class PruefeHauptmenuepunkte_3_Test extends TestCaseTemplateParent
{
	protected LanguageTemplatesCommon languageTemplatesCommon;
	protected LanguageTemplatesHomePageIKSBasics languageTemplatesHomePageIKSBasics;
	protected LanguageTemplatesPrint languageTemplatesPrint;
	
	@Before
	public void setUp() 
	{
		super.setUp();
		languageTemplatesCommon = new LanguageTemplatesCommon(this);
		languageTemplatesHomePageIKSBasics = new LanguageTemplatesHomePageIKSBasics(this);
		languageTemplatesPrint = new LanguageTemplatesPrint(this);
	}

	@After
	public void shutdown() 
	{
		if ( ! isSkipped() && executionInfo.isApplicationStarted()) {
			if (languageTemplatesHomePageIKSBasics != null) languageTemplatesHomePageIKSBasics.gotoStartPage();
		}
		languageTemplatesCommon = null;
		languageTemplatesHomePageIKSBasics = null;
		languageTemplatesPrint = null;
		super.shutdown();
	}

	@Test
	@Override
	public void executeTestCase() 
	{
		try {
			languageTemplatesCommon.declareXXGroupForBehaviour("PruefeHauptmenuepunkte");
			languageTemplatesCommon.startNewXX("PruefeHauptmenuepunkte_3");
			languageTemplatesCommon.checkFilterCategory("Smoketest");
			languageTemplatesHomePageIKSBasics.isPageVisible("Home");
			languageTemplatesHomePageIKSBasics.clickMainMenuItem("Karriere");
			languageTemplatesHomePageIKSBasics.isPageVisible("Karriere");
			
			closeCurrentTestCaseWithSuccess();
		} catch (Throwable e) {
			super.handleThrowable(e);
		}
	}
}