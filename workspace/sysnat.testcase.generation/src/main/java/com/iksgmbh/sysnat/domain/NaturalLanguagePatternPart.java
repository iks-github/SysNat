package com.iksgmbh.sysnat.domain;

/**
 * Both InstructionPatterns and TemplatePatterns are parsed into pattern parts.
 * They are used to compare InstructionPatterns and TemplatePatterns 
 * in order to find matches.
 * 
 * @author Reik Oberrath
 */
public class NaturalLanguagePatternPart 
{
	public enum NaturalLanguagePatternPartType { DEFAULT,          // static template text 
		                                         PARAM_VALUE,      // a parameter represented by a concrete value defined within the current instruction or by test data
		                                         PARAM_VARIABLE,   // a parameter represented by a variable defined by a return value of a previous instruction
		                                         RETURN_VALUE };   // a value returned from this instruction to be used from a later instruction as PARAM_VARIABLE
	
	public NaturalLanguagePatternPartType type;
	public Object value;
	
	public NaturalLanguagePatternPart(final NaturalLanguagePatternPartType aType, 
			                          final Object aValue) 
	{
		this.type = aType;
		this.value = aValue;
	}

	public boolean isIdentical(NaturalLanguagePatternPart otherPart) 
	{
		if ( type != otherPart.type ) {
			return false;
		}
		
		if (value instanceof String && otherPart.value instanceof String) {
			return value.equals(otherPart.value); 
		} else if (value instanceof Class<?> && otherPart.value instanceof Class<?>) {
			return ((Class<?>)value).getName().equals(((Class<?>)otherPart.value).getName()); 			
		}
		
		return false;
	}

	@Override
	public String toString() {
		return type.name() + ": " + value;
	}
}
