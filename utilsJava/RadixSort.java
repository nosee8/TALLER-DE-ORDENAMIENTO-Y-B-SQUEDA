package utilsJava;

/**
 * Radix Sort Implementation
 * Referencia: https://www.geeksforgeeks.org/radix-sort/
 */
public class RadixSort {

    static int getMax(int[] arr) {
        int mx = arr[0];
        for (int i = 1; i < arr.length; i++)
            if (arr[i] > mx)
                mx = arr[i];
        return mx;
    }

    static void countSort(int[] arr, int exp) {
        int n = arr.length;
        int[] output = new int[n];
        int[] count = new int[10];

        for (int i = 0; i < n; i++)
            count[(arr[i] / exp) % 10]++;

        for (int i = 1; i < 10; i++)
            count[i] += count[i - 1];

        for (int i = n - 1; i >= 0; i--) {
            output[count[(arr[i] / exp) % 10] - 1] = arr[i];
            count[(arr[i] / exp) % 10]--;
        }

        for (int i = 0; i < n; i++)
            arr[i] = output[i];
    }

    public static void radixSort(int[] arr) {
        int m = getMax(arr);
        for (int exp = 1; m / exp > 0; exp *= 10)
            countSort(arr, exp);
    }
}
