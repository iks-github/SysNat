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
package com.iksgmbh.sysnat.common.helper.pdftooling;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.iksgmbh.sysnat.common.exception.SysNatTestDataException;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;
import com.testautomationguru.utility.PDFUtil;

/**
 * Provides methods to analyze differences between two PDF files.
 * 
 * @author Reik Oberrath
 */
public class PdfComparer
{
	private static final String PAGE_IDENTIFIER = "Seite";

	private static final PDFUtil pdfUtil = new PDFUtil();

	private static final int MAX_DIFFERENCES_IN_REPORT = 100; // set to 10 after end of test phase
	
	private String pdfFileName;

	public PdfComparer(String aPdfFileName) {
		this.pdfFileName = aPdfFileName;
	}


	public int getPageNumber() {
		try {
			return pdfUtil.getPageCount(pdfFileName);
		} catch (IOException e) {
			e.printStackTrace();
			return -1;
		}
	}
	
	public boolean doesPageContain(int pageNumber, String toSearch) 
	{
		if (pageNumber < 1 || pageNumber > getPageNumber() ) {
			throw new IllegalArgumentException("Page number " + pageNumber + " out of range!");
		}
		
		try {
			final String page = pdfUtil.getText(pdfFileName, pageNumber, pageNumber+1);
			return page.contains(toSearch);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} 
	}

	public List<Integer> getDifferingPages(String anotherPdf) throws IOException 
	{
		int pageCount1 = pdfUtil.getPageCount(pdfFileName);
		int pageCount2 = pdfUtil.getPageCount(anotherPdf);
		if (pageCount1 != pageCount2) {
			return null;
		}
		
		final List<Integer> diffPages = new ArrayList<>();
		for (int i = 0; i < pageCount1; i++) {
			int page = i + 1;
			if ( ! pdfUtil.compare(pdfFileName, anotherPdf, page, page) ) {
				diffPages.add(page);
			}
		}
		
		return diffPages;
	}
	
	public String getDifferingPagesAsString(String anotherPdf) throws IOException 
	{
		List<Integer> diffPages = getDifferingPages(anotherPdf);
		String toReturn = differingPagesToReportLine(diffPages);
		return toReturn;
	}


	private String differingPagesToReportLine(List<Integer> diffPages) 
	{
		if (diffPages == null) {
			return "Die PDFs unterscheiden sich in der Seitenzahl.";			
		}
		String toReturn = "";
		if (diffPages.size() == 1) {
			toReturn = "PDFs unterscheiden sich auf Seite " + diffPages.get(0) + ".";
		} else {
			toReturn = "PDFs unterscheiden sich auf den Seiten ";
			for (int i = 0; i < diffPages.size()-2; i++) {
				toReturn += diffPages.get(i) + ", ";
			}
			toReturn += diffPages.get(diffPages.size()-2);
			toReturn += " und " + diffPages.get(diffPages.size()-1) + ".";
		}
		return toReturn;
	}

	public boolean contentEquals(String anotherPdf) {
		try {
			return pdfUtil.compare(pdfFileName, anotherPdf);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public boolean doesPageContainIgnoreWhiteSpace(int pageNumber, String toSearch) 
	{
		if (pageNumber < 1 || pageNumber > getPageNumber() ) {
			throw new IllegalArgumentException("Page number " + pageNumber + " out of range!");
		}
		
		try {
			final String pageCompressed = compress( pdfUtil.getText(pdfFileName, pageNumber) );
			final String searchStringCompressed = compress( toSearch );
			return pageCompressed.contains(searchStringCompressed);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}	
	
	private String compress(String text) 
	{
		char[] charArray = text.toCharArray();
		final StringBuffer sb = new StringBuffer();
		
		for (char c : charArray) 
		{
			if (c != ' ' && c > 30) {
				sb.append(c);
			}
		}
		
		return sb.toString();
	}


	public String getFirstDifference(String anotherPdf) throws IOException 
	{
		int pageCount1 = pdfUtil.getPageCount(anotherPdf);
		for (int i = 0; i < pageCount1; i++) {
			int page = i + 1;
			if ( ! pdfUtil.compare(pdfFileName, anotherPdf, page, page+1) ) {
				return getFirstDifferenceOnPage(page, anotherPdf);
			}
		}
		System.out.println("No difference found.");
		return "";
	}


	private String getFirstDifferenceOnPage(int page, String anotherPdf) throws IOException 
	{
		String[] text1 = pdfUtil.getText(pdfFileName, page, page).split(" ");
		String[] text2 = pdfUtil.getText(anotherPdf, page, page).split(" ");
		
		int maxWords = text1.length;
		if (text2.length < maxWords) {
			maxWords = text2.length;
		}
		
		for (int i = 0; i < maxWords; i++) 
		{
			if ( ! text2[i].equals(text1[i]) ) {
				return "On page " + page + " is '" + text2[i] + "' not equal to '" + text1[i] + "'.";
			}
			
		}
		return "ungleich";
	}

	public List<String> getAllDifferingWordsOnPage(int page, String anotherPdf) throws IOException 
	{
		String[] text1 = pdfUtil.getText(anotherPdf, page, page).split(" ");
		String[] text2 = pdfUtil.getText(pdfFileName, page, page).split(" ");
		
		int maxWords = text2.length;
		if (text1.length < maxWords) {
			maxWords = text1.length;
		}
		
		List<String> toReturn = new ArrayList<>();
		for (int i = 0; i < maxWords; i++) 
		{
			if ( ! text1[i].equals(text2[i]) ) {
				toReturn.add("On page " + page + " is '" + text1[i] + "' not equal to '" + text2[i] + "'.");
				System.out.println("On page " + page + " is '" + text1[i] + "' not equal to '" + text2[i] + "'.");
			}
			
		}
		return toReturn;
	}

	public List<String> getDifferingLinesOnPage(final String anotherPdf, 
                                                final int pageNo, 
                                                final PdfCompareIgnoreConfig ignoreConfig) 
	{
		PdfPageContentAnalyser cut = new PdfPageContentAnalyser( pdfFileName );
		List<String> linesA = cut.getLinesOfPage(pageNo);
		linesA = ignoreConfig.removeLinesToIgnore(linesA);
		
		cut = new PdfPageContentAnalyser( anotherPdf );
		List<String> linesB = cut.getLinesOfPage(pageNo);
		linesB = ignoreConfig.removeLinesToIgnore(linesB);
		
		return getDifferingLines(linesA, linesB, pageNo);
	}
	
	List<String> getDifferingLines(final List<String> linesA, 
			                       final List<String> linesB,
			                       final int page) 
	{
		List<String> toReturn = new ArrayList<>();
		int numberOfLines = linesA.size(); 
		if (linesB.size() > numberOfLines) {
			numberOfLines = linesB.size(); 
		}

		for (int i = 0; i < numberOfLines; i++) 
		{
			if (i > linesA.size()-1) {
				toReturn.add("Seite " + page + ", Zeile " + (i+1) + ": [] # [" + linesB.get(i) + "]");
			}
			else if (i > linesB.size()-1) {
				toReturn.add("Seite " + page + ", Zeile " + (i+1) + ": [" +linesA.get(i) + "] # []");
			}
			else if ( ! linesA.get(i).trim().equals(linesB.get(i).trim()) ) {
				toReturn.add("Seite " + page + ", Zeile " + (i+1) + ": [" +linesA.get(i) + "] # [" + linesB.get(i) + "]");
			}
		}
		
		return toReturn;
	}


	/**
	 * Detects all difference with applying any Ignore-Filter.
	 * 
	 * @param anotherPdf
	 * @return Result
	 * @throws IOException
	 */
	public String getFullDifferenceReport(final String anotherPdf) throws IOException 
	{
		return getDifferenceReport(anotherPdf,  new PdfCompareIgnoreConfig(null, null, null, null));
	}
	
	/**
	 * Lines that are different between the PDFs but contain one (or more) of the substrings to ignore
	 * will be ignored by the difference report. 
	 */
	public String getDifferenceReport(final String anotherPdf, 
			                          final PdfCompareIgnoreConfig ignoreConfig) throws IOException 
	{
		// old way with PdfUtils (ignoreConfig wird nachträglich angewandt)
		// final List<Integer> diffPages = getDifferingPages(anotherPdf);  
		// final List<String> differingLines = getDifferingLines(anotherPdf, diffPages, ignoreConfig);  
		
		final Map<Integer, List<String>> differences = getDifferences(anotherPdf, ignoreConfig); // new way

		@SuppressWarnings({ "unchecked", "rawtypes" })
		final List<Integer> differingPages = new ArrayList(differences.keySet());
		final List<String> differingLines = new ArrayList<>();
		
		differences.forEach((pageNo,diffLines) -> differingLines.addAll(diffLines));
		return getDifferenceReport(anotherPdf, differingPages , differingLines); 
	}

	public Map<Integer, List<String>> getDifferences(String anotherPdf, 
			                                         PdfCompareIgnoreConfig ignoreConfig) 
			                                         throws IOException 
	{
		final Map<Integer, List<String>> toReturn = new HashMap<>();
		
		int pageCount1 = pdfUtil.getPageCount(pdfFileName);
		int pageCount2 = pdfUtil.getPageCount(anotherPdf);
		if (pageCount1 != pageCount2) {
			toReturn.put(Integer.valueOf(-1), new ArrayList<>());
			return toReturn;
		}
		
		final PdfReader pdfReader1 = new PdfReader(pdfFileName);
		final PdfReader pdfReader2 = new PdfReader(anotherPdf);

		for (int i = 1; i <= pageCount1; i++) 
		{
			String page1 = PdfTextExtractor.getTextFromPage(pdfReader1, i);
			page1 = ignoreConfig.applyIgnoreConfig(page1, pdfFileName);
			
			String page2 = PdfTextExtractor.getTextFromPage(pdfReader2, i);
			page2 = ignoreConfig.applyIgnoreConfig(page2, anotherPdf);

			if ( ! page1.equals(page2) ) {
				String[] splitResult = page1.split("\\r?\\n");
				final List<String> linesA = Arrays.asList(splitResult);
				splitResult = page2.split("\\r?\\n");
				final List<String> linesB = Arrays.asList(splitResult);
				toReturn.put(new Integer(i), getDifferingLines(linesA, linesB, i));
			}
		}
		
		return toReturn;
	}

	public String getDifferenceReport(final String anotherPdf,
			                          final List<Integer> diffPages,
			                          final List<String> differingLines) throws IOException 
	{
		StringBuffer report = new StringBuffer("Unterschiede zwischen");
		report.append(System.getProperty("line.separator"));
		report.append(anotherPdf).append(" (Seitenzahl: " + pdfUtil.getPageCount(anotherPdf) + ")");
		report.append(System.getProperty("line.separator"));
		report.append("und");
		report.append(System.getProperty("line.separator"));
		report.append(pdfFileName).append(" (Seitenzahl: " + pdfUtil.getPageCount(pdfFileName) + ")");
		report.append(System.getProperty("line.separator"));
		report.append("--------------------------------------------------------------------");
		
		
		if (diffPages == null) {
			report.append(System.getProperty("line.separator"));
			return report.toString() + "Die PDFs unterscheiden sich in der Seitenzahl.";			
		}

		if (diffPages.size() == 0) {
			return "";
		}
		
		report.append(System.getProperty("line.separator"));
		if (differingLines.size() < MAX_DIFFERENCES_IN_REPORT) {
			differingLines.forEach(line -> report.append(line).append(System.getProperty("line.separator")));
		} else {
			report.append( differingPagesToReportLine(diffPages)).append(System.getProperty("line.separator"));
			report.append( "Es gibt insgesamt " + differingLines.size() + " unterschiedliche Zeilen. ");
			report.append( "Die ersten sind: ").append(System.getProperty("line.separator"));
			for (int i = 0; i < MAX_DIFFERENCES_IN_REPORT; i++) {
				report.append(differingLines.get(i)).append(System.getProperty("line.separator"));
			}
		}
		
		return report.toString().trim(); 
	}

	
	Integer getPageNumberFromLine(String line) 
	{
		int pos1 = line.indexOf(PAGE_IDENTIFIER) + PAGE_IDENTIFIER.length();
		int pos2 = line.indexOf(":");
		
		if (pos1 == -1 || pos2 == -1) {
			throw new SysNatTestDataException("Cannot parse line number from <b>" + line + "</b>.");
		}
		
		int pos3 = line.indexOf(",");
		
		if (pos3 !=-1 && pos2 > pos3) {
			pos2 = pos3;
		}
		String returnCandidate = line.substring(pos1, pos2).trim();
				
		try {
			return Integer.valueOf(returnCandidate);
		} catch (Exception e) {
			throw new SysNatTestDataException("Cannot parse line number from <b>" + line + "</b>.");
		}
	}
	
}