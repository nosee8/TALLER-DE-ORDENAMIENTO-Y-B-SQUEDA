package utilsJava;

/**
 * Ternary Search Implementation
 * Referencia: https://www.geeksforgeeks.org/ternary-search/
 */
public class TernarySearch {

    public static int ternarySearch(int[] arr, int low, int high, int x) {
        if (high >= low) {
            int mid1 = low + (high - low) / 3;
            int mid2 = high - (high - low) / 3;

            if (arr[mid1] == x) return mid1;
            if (arr[mid2] == x) return mid2;

            if (x < arr[mid1])
                return ternarySearch(arr, low, mid1 - 1, x);

            if (x > arr[mid2])
                return ternarySearch(arr, mid2 + 1, high, x);

            return ternarySearch(arr, mid1 + 1, mid2 - 1, x);
        }
        return -1;
    }
}
