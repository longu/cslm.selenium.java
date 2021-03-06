package com.testalliance.hrsystem.managers;

import java.io.File;
import java.util.ArrayList;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import com.testalliance.hrsystem.test.Test;
import com.testalliance.hrsystem.test.Utility;

public class TestRunner 
{
	public static void main(String[] args)
	{
		WebDriver driver = null;
		String filePath = System.getProperty("user.dir") + "/config/config.csv";
		int result = 0;
		String TEST_RESULT;
		Utility util = new Utility();
		ArrayList<String[]> al = util.getModules(filePath);
		DriverManager dm = null;
		
		for(int i = 1; i < al.size(); i++)
		{
			String[] arr_config = al.get(i);
			String driverBrowser = arr_config[0];
			String driverPath = arr_config[1];
			String browser = arr_config[2];
			
			//读取测试模块
			String modules = arr_config[3];
			modules = modules.replace("{", "").replace("}", "");
			String[] arr_modules = modules.split("\\|");
			for(int j = 0; j < arr_modules.length; j++)
			{
				File dir = new File(System.getProperty("user.dir") + "//testdata." + arr_modules[j]);
				File[] fileList = dir.listFiles();
				for(File file : fileList)
				{
					System.out.println("开始执行" + arr_modules[j] + "模块中" + file.getName() + ":");
					try
					{
						// test preparation
						dm = new DriverManager(driverBrowser, driverPath, browser);
						driver = dm.preTest();
						
						//execute test case
						Test test = new Test();
						result = test.executeTest(driver, System.getProperty("user.dir") + 
								"//testdata." + arr_modules[j] + "//" + file.getName(), 
								"Test Case", arr_modules[j]);
						
					}
					catch(Exception e)
					{
						result = 0;
						e.printStackTrace();
						TakesScreenshot screenShot = ((TakesScreenshot)driver);
						util.takeScreenshot(screenShot, driver);
					}
					finally
					{
						if(result == 0)
							TEST_RESULT = "Test Fail";
						else
							TEST_RESULT = "Test Pass";
						System.out.println("执行结果： " + TEST_RESULT);
						System.out.println("");
						
						//test finish
						dm.cleanUp();
					}
				}
			}
		}
	}
}
