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
package com.iksgmbh.sysnat.domain;

import java.util.HashMap;
import java.util.Properties;

import com.iksgmbh.sysnat.ExecutionRuntimeInfo;
import com.iksgmbh.sysnat.common.exception.SysNatException;
import com.iksgmbh.sysnat.common.utils.ExceptionHandlingUtil;
import com.iksgmbh.sysnat.common.utils.PropertiesUtil;
import com.iksgmbh.sysnat.common.utils.SysNatConstants;
import com.iksgmbh.sysnat.common.utils.SysNatConstants.TargetEnv;

/**
 * Holds attributes of the application under test.
 * @author Reik Oberrath
 */
public class TestApplication 
{
   private String name;
   private String targetEnvironmentAsString;
   private String propertiesFileName;
   private boolean isWebApplication;
   private boolean withLogin;
   private Properties applicationProperties;
   private HashMap<SysNatConstants.WebLoginParameter,String> loginParameter = new HashMap<>();

   public TestApplication(final String anApplicationName,
                          final String aPropertiesFileName,
                          final String atargetEnvironmentAsString)
   {
      this.name = anApplicationName;
      this.propertiesFileName = aPropertiesFileName;
      this.targetEnvironmentAsString = atargetEnvironmentAsString;

      init();
   }

   public TestApplication(final String anApplicationName)
   {
      this.name = anApplicationName;
      this.propertiesFileName = getDefaultPropertiesFileName(anApplicationName);
      this.targetEnvironmentAsString = getDefaultEnvironmentAsString(anApplicationName);

      init();
   }

   
   
	private String getDefaultEnvironmentAsString(String anApplicationName)
	{
		TargetEnv targetEnv = ExecutionRuntimeInfo.getInstance().getTargetEnv();
		
		if (targetEnv == null) {
			SysNatException exception = new SysNatException("No (valid) target environment defined in settings.config!");
			exception.printStackTrace();
			return "<Unknown target environment>";
		}
		
		return targetEnv.name();
	}

	private String getDefaultPropertiesFileName(String anApplicationName)
	{
		String propertiesPath = ExecutionRuntimeInfo.getInstance().getPropertiesPath();
		
		if (propertiesPath == null) {
			SysNatException exception = new SysNatException("No (valid) properties.execution file defined!");
			exception.printStackTrace();
		}

		return propertiesPath + "/" + anApplicationName + ".properties";
	}

	private void init()
	{
		applicationProperties = PropertiesUtil.loadProperties(propertiesFileName);
		String result = (String) applicationProperties.get("isWebApplication");
		isWebApplication = "true".equalsIgnoreCase(result);
		result = (String) applicationProperties.get("withLogin");
		withLogin = "true".equalsIgnoreCase(result);

		if (withLogin) {
			addStartParameter(applicationProperties);
		}
	}

   public String getProperty(String key)
   {
      String propertyKey = ( name + "." +
                             targetEnvironmentAsString + "." +
                             key
                           ).toLowerCase();

      return applicationProperties.getProperty(propertyKey);
   }

   private void addStartParameter(final Properties applicationProperties)
   {
      if (isWebApplication) {
         loginParameter.put(SysNatConstants.WebLoginParameter.URL, getLoginParameter(applicationProperties, SysNatConstants.WebLoginParameter.URL));
      }
      if (withLogin) {
         loginParameter.put(SysNatConstants.WebLoginParameter.LOGINID, getLoginParameter(applicationProperties, SysNatConstants.WebLoginParameter.LOGINID));
         loginParameter.put(SysNatConstants.WebLoginParameter.PASSWORD, getLoginParameter(applicationProperties, SysNatConstants.WebLoginParameter.PASSWORD));
      }
   }

   private String getLoginParameter(final Properties applicationProperties,
                            final SysNatConstants.WebLoginParameter parameter)
   {
      String propertyKey = ( name + "." +
                             targetEnvironmentAsString +
                             ".login." +
                             parameter.name()
                           ).toLowerCase();

      String propertyValue = applicationProperties.getProperty(propertyKey);
      if (propertyValue == null) {
         ExceptionHandlingUtil.throwException("The test application " + name + 
        		               " is not configured for the current test run. "
        		               + "Please defined property '" + propertyKey + "' in '" 
        		               + propertiesFileName + "'.");
      }
      return propertyValue.trim();
   }

   public boolean isWebApplication() {
      return isWebApplication;
   }

   @Override
   public String toString() {
      return getName();
   }

   public String getName() {
      return name;
   }

   public HashMap<SysNatConstants.WebLoginParameter, String> getLoginParameter() {
      return loginParameter;
   }

    public String getStartParameterValue()
    {
        String startParameter = applicationProperties.get("StartParameter").toString();
        return getProperty(startParameter);
    }
}