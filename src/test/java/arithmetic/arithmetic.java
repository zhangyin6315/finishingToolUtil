package arithmetic;

import org.junit.Test;

import com.zhy.arithmetic.MaxArray;

public class arithmetic {
	/**
	 * <p>
	 * Title: arithmetic
	 * </p>
	 * <p>
	 * Description:最大子数组
	 * </p>
	 * 
	 * @date 2020年1月11日
	 */
	@Test
	public void testMaxArray() {
		// 定义数组
		int a[] = { 1, -2, 3, 5, -1, 2, 6, 3, -9, 5, 6, 3, 8, 2, -1, -5, -4, 9 - 3, -5, -2, 1 };
		int sum = 0;
		long startTime;
		long endTime;
		for (int i = 0; i < 10; i++) {
			startTime = System.nanoTime(); // 获取开始时间
			// 穷举方法
			MaxArray.methodOfExhaustion(a);
			endTime = System.nanoTime(); // 获取结束时间
			sum += (endTime - startTime);
		}
		System.out.println(MaxArray.methodOfExhaustion(a));
		System.out.println("穷举程序运行平均时间： " + sum / 10 + "ns");
		sum = 0;
		for (int i = 0; i < 10; i++) {
			startTime = System.nanoTime(); // 获取开始时间
			// 分而治之方法
			MaxArray.divideAndRule(a, 0, a.length - 1);
			endTime = System.nanoTime(); // 获取结束时间
			sum += (endTime - startTime);
		}
		System.out.println(MaxArray.divideAndRule(a, 0, a.length - 1));
		System.out.println("分而治之程序运行平均时间： " + sum / 10 + "ns");

		sum = 0;
		for (int i = 0; i < 10; i++) {
			/* 获取开始时间 */
			startTime = System.nanoTime();
			// 动态规划方法
			MaxArray.dynamicPlanning(a);
			// 获取结束时间
			endTime = System.nanoTime();
			sum += (endTime - startTime);

		}
		System.out.println(MaxArray.dynamicPlanning(a));
		System.out.println("动态规划程序运行平均时间： " + sum / 10 + "ns");

	}

}
