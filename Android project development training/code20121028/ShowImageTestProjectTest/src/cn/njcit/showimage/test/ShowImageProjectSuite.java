package cn.njcit.showimage.test;

import junit.framework.Test;
import junit.framework.TestSuite;

import android.test.suitebuilder.TestSuiteBuilder;

/*
 * ������������
 */
public class ShowImageProjectSuite extends TestSuite {

	public static Test suite() {

		/*
		 * ���𽫱�������Ŀ�����еĲ����������� ��һ�𡣲��Կ�ʼʱ�����Զ�����ִ�С�
		 */
		return new TestSuiteBuilder(ShowImageProjectSuite.class)
				.includeAllPackagesUnderHere().build();
	}
}
