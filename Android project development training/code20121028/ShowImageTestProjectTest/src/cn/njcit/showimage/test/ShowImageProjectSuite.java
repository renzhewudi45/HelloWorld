package cn.njcit.showimage.test;

import junit.framework.Test;
import junit.framework.TestSuite;

import android.test.suitebuilder.TestSuiteBuilder;

/*
 * 测试用例集合
 */
public class ShowImageProjectSuite extends TestSuite {

	public static Test suite() {

		/*
		 * 负责将本测试项目下所有的测试用例整合 在一起。测试开始时，将自动依次执行。
		 */
		return new TestSuiteBuilder(ShowImageProjectSuite.class)
				.includeAllPackagesUnderHere().build();
	}
}
