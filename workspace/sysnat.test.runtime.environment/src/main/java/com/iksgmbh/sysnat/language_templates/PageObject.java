package com.iksgmbh.sysnat.language_templates;

import java.util.Locale;
import java.util.ResourceBundle;

import com.iksgmbh.sysnat.common.utils.SysNatConstants.GuiType;
import com.iksgmbh.sysnat.helper.ExceptionHandler;

/**
 * Contains general methods needed by page objects.
 * 
 * @author Reik Oberrath
 */
public interface PageObject
{
	public static ResourceBundle BUNDLE = ResourceBundle.getBundle("bundles/PageObjectApi", Locale.getDefault());
	
	public default void throwUnsupportedGuiEventException(GuiType guiElementType, String elementName) 
	{
		String elementType = BUNDLE.getString(guiElementType.name());
		if (elementType == null) {
			elementType = guiElementType.name();
		}
		String errorMessage = BUNDLE.getString("UnsupportedGuiEventExceptionMessage")
				                    .replace("xx", elementName)
				                    .replace("yy", elementType)
				                    .replace("zz", getPageName());
				
		throw ExceptionHandler.createNewUnsupportedGuiEventException(errorMessage);
	}

	
	public String getPageName();
	public void clickButton(String buttonName);
	public void enterTextInField(String fieldName, String value);
	public void chooseForCombobox(String fieldName, String value);
	public String getText(String identifierOfGuiElementToRead);
	public boolean isCurrentlyDisplayed();
}
