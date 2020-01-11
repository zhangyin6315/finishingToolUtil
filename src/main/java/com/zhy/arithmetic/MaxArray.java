package com.zhy.arithmetic;

public class MaxArray {
//穷举法
public static int methodOfExhaustion(int a[]){
	int x=0,y=0,sum=0,max=0;
	for(int i=0;i<a.length;i++) {
		for(int j=i;j<a.length;j++) {
			//所有组合
			sum=0;
			for(int k=i;k<=j;k++) {
				/* 计算i-j项 */
				sum+=a[k];
			}
			max=Math.max(sum,max);
		}
	}
	//System.out.println("第"+x+"到"+y+"个");
	return max;
}

//分而治之
public static int divideAndRule(int a[],int low,int high){
	if(low==high) {
		return a[high];
	}
	int x=0,y=0;
	int middle;
	middle=(low+high)/2;
	int m1=divideAndRule(a,low,middle);
	int m2=divideAndRule(a,middle+1,high);
	int i=0,left=a[middle],now=a[middle];
	for(i=middle-1;i>low;i--) {
		now+=a[i];
		left=Math.max(now,left);
	}
	int right=a[middle+1];
	now=a[middle+1];
	for(i=middle+2;i<high;i++) {
		now+=a[i];
		right=Math.max(now,right);
	}
	int m3=left+right;
	return Math.max(Math.max(m1,m2),m3);
}
//动态规划
public static int dynamicPlanning (int a[]){
	
	int x=a[0];
	int sum=a[0];
	for(int i=1;i<a.length;i++) {
		if(sum>0) {
			sum+=a[i];
		}else {
			sum=a[i];
		}
		x=Math.max(sum,x);
	}
	return x;
}

}
