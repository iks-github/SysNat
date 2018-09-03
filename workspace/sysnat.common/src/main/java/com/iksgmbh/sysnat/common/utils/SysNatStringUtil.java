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
package com.iksgmbh.sysnat.common.utils;

import static com.iksgmbh.sysnat.common.utils.SysNatConstants.*;

import java.util.ArrayList;
import java.util.List;

public class SysNatStringUtil 
{
	public static String replaceSpacesByUnderscore(final String s) {
		return s.replaceAll(" ", "_");
	}
	
	public static String firstCharToLowerCase(final String s) {
		final String firstChar = "" + s.charAt(0);
		return firstChar.toLowerCase() + s.substring(1);
	}
	
	public static List<String> getTestCategoriesAsList(String testCategoriesAsString, String xxid) 
	{
		List<String> toReturn = new ArrayList<>();
		
		if (testCategoriesAsString != null)  
		{
			String[] splitResult = testCategoriesAsString.split(",");
			if (splitResult.length == 1 && splitResult[0].trim().length() == 0) {				
				toReturn.add(NO_FILTER);
				testCategoriesAsString = NO_FILTER;
			} else 
			{				
				for (String category : splitResult) 
				{
					category = category.replace('_', ' ');
					if (SysNatLocaleConstants.FROM_PACKAGE.equals(category)) 
					{
						String[] splitResult2 = xxid.split("_");
						for (String category2 : splitResult2) {
							toReturn.add(category2);
						}
					} else {
						toReturn.add(category);
					}
				}
			}
		}
		
		return toReturn;
	}

    public static String cutPrefix(String s, String prefix) {
		return s.substring(prefix.length());
	}

	public static String firstCharTuUpper(String s) {
		char[] stringArray = s.toCharArray();
		stringArray[0] = Character.toUpperCase(stringArray[0]);
		return new String(stringArray);
	}

	public static int countNumberOfOccurrences(String s, 
			                                   final String searchSubString) 
	{
		int countToReturn = 0;
		
		while (s.contains(searchSubString)) {
			countToReturn++;
			int pos = s.indexOf(searchSubString);
			s = s.substring(pos + searchSubString.length());
		}
		
		return countToReturn;
	}

	public static String getLeadingWhiteSpace(String line) 
	{
		String toReturn = "";
		
		while (line.length() > 0) 
		{
			if (line.charAt(0) == ' ') {
				toReturn += " ";
				line = line.substring(1);
			} else if (line.charAt(0) == '\t') {
					toReturn += "\t";
					line = line.substring(1);
			} else {
				line = "";
			}
		}
		
		return toReturn;
	}

	public static String cutExtension(String filename) 
	{
		int pos = filename.lastIndexOf(".");
		if (pos == -1) {
			return filename;
		}
		return filename.substring(0, pos);
	}

	public static String replaceGermanUmlauts(final String toReturn) 
	{
		return toReturn.replaceAll("ü", "ue")
				       .replaceAll("ä", "ae")
				       .replaceAll("ö", "oe")
				       .replaceAll("Ä", "Ae")
				       .replaceAll("Ö", "Oe")
				       .replaceAll("Ü", "Ue")
				       .replaceAll("ß", "ss");
	}

	public static List<String> findDifferingElements(final List<String> bigList,
			                                  final List<String> smallList) 
	{
		final List<String> toReturn = new ArrayList<>();
		for (String bigElement : bigList) 
		{
			boolean isDifference = true;
			for (String smallElement : smallList) {
				if ( smallElement.equals(bigElement) ) {
					isDifference = false;
				}
			}
			if (isDifference) {
				toReturn.add(bigElement);
			}
		}
		return toReturn;
	}

	public static String cutTrailingDigits(final String text) 
	{
		String toReturn = text;
		while (Character.isDigit( toReturn.charAt(toReturn.length()-1)) ) {
			toReturn = toReturn.substring(0, toReturn.length()-1);
		}
		return toReturn;
	}

	public static String cutTrailingChars(final String text, 
			                              final char[] charsToCut) 
	{
		String toReturn = text;
		boolean goOn = true;
		while (goOn)  
		{
			for (int i = 0; i < charsToCut.length; i++) 
			{
				goOn = false;
				if (toReturn.endsWith("" + charsToCut[i])) 
				{
					toReturn = toReturn.substring(0, toReturn.length()-1);
					goOn = true;
					break;
				}
			}
		}
		return toReturn;
	}

	public static String toFileName(String s) {
		return replaceGermanUmlauts(s)
				.replaceAll(" ", "")
				.replaceAll("[\\W]&&[^.]", "");
	}

	public static String extraxtTrailingDigits(String s) 
	{
		String toReturn = "";
		char[] charArray = s.toCharArray();
		char[] charsToExtract = {'0','1','2','3','4','5','6','7','8','9' };
		for (int i = charArray.length-1; i >= 0; i--) 
		{
			boolean charIsDigit = false;
			for (int j = 0; j < charsToExtract.length; j++)
			{
				
				if (charArray[i] == charsToExtract[j]) {
					toReturn = charArray[i] + toReturn;
					charIsDigit = true;
					break;
				}
			}
			
			if ( ! charIsDigit) {
				break;
			}
		}

		return toReturn;
	}
}