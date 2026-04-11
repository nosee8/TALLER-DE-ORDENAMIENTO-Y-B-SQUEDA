# Ternary Search Implementation
# Referencia: https://www.geeksforgeeks.org/ternary-search/
#
# Divide el arreglo ordenado en tres partes y determina
# en cuál de ellas puede estar el elemento buscado.

import sys
sys.setrecursionlimit(10000)

def ternarySearch(arr, low, high, x):
    if high >= low:
        mid1 = low + (high - low) // 3
        mid2 = high - (high - low) // 3

        # Check if x is present at mid1
        if arr[mid1] == x:
            return mid1

        # Check if x is present at mid2
        if arr[mid2] == x:
            return mid2

        # x is in left third
        if x < arr[mid1]:
            return ternarySearch(arr, low, mid1 - 1, x)

        # x is in right third
        if x > arr[mid2]:
            return ternarySearch(arr, mid2 + 1, high, x)

        # x is in middle third
        return ternarySearch(arr, mid1 + 1, mid2 - 1, x)

    # Element not present
    return -1
