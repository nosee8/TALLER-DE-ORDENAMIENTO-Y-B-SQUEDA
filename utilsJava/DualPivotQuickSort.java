package utilsJava;

/**
 * Dual Pivot QuickSort Implementation
 * Referencia: https://www.geeksforgeeks.org/dual-pivot-quicksort/
 */
public class DualPivotQuickSort {

    static void swap(int[] arr, int i, int j) {
        int temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }

    static int[] partition(int[] arr, int low, int high) {
        if (arr[low] > arr[high])
            swap(arr, low, high);

        int j = low + 1;
        int g = high - 1, k = low + 1;
        int p = arr[low], q = arr[high];

        while (k <= g) {
            if (arr[k] < p) {
                swap(arr, k, j);
                j++;
            } else if (arr[k] >= q) {
                while (arr[g] > q && k < g)
                    g--;
                swap(arr, k, g);
                g--;
                if (arr[k] < p) {
                    swap(arr, k, j);
                    j++;
                }
            }
            k++;
        }
        j--;
        g++;
        swap(arr, low, j);
        swap(arr, high, g);
        return new int[]{j, g};
    }

    public static void dualPivotQuickSort(int[] arr, int low, int high) {
        if (low < high) {
            int[] piv = partition(arr, low, high);
            dualPivotQuickSort(arr, low, piv[0] - 1);
            dualPivotQuickSort(arr, piv[0] + 1, piv[1] - 1);
            dualPivotQuickSort(arr, piv[1] + 1, high);
        }
    }
}
