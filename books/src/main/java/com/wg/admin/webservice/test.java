package com.wg.admin.webservice;


public class test {

    public static void main(String[] args) {
//        DecimalUtils.doubleFormat(21.1234,3, BigDecimal.ROUND_DOWN);
        int arr[] = {1, 3, 4, 7, 9, 15, 18, 21, 23, 30, 58, 60, 64};
        System.out.println("x:" + halfSearch(arr, 4));

        System.out.println("result:" + bubble(arr, 16));

    }

    //二分
    public static int halfSearch(int arr[], int x) {
        int from = 0, end = arr.length;
        int mid = -1;
        while (from <= end) {
            mid = (from + end) / 2;
            if (x == arr[mid]) {
                return mid;
            } else if (x < arr[mid]) {
                end = mid - 1;
            } else if (x > arr[mid]) {
                from = mid + 1;
            }
        }
        return -1;
    }

    //冒泡
    public static boolean bubble(int arr[], int x) {
        int i = 0, j = 0;
        int len = arr.length;
        for (i = 0; i < len; i++) {
            for (j = i + 1; j < len; j++) {
                if (x == (arr[i] + arr[j])) {
                    return true;
                }
            }
        }
        return false;
    }

}
