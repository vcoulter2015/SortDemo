/*
 * Author: Vi Coulter
 *  Sept. 2015
 *
 * See jdk1.8.0_60/sample/forkjoin/mergesort/MergeDemo.java for some of this code.
 */

import java.util.Random;

public class SortDemo {
    // TODO in future - maybe pass in random # seed as second command line parameter
    private static final Random random = new Random(759123742856L);
    // This setting controls how often the sort prints out the array while it's being sorted.
    // 1 = every pass, 2 = every other pass, etc.
    private static int progressIncrement = 1;
    // N.B. If I just say dataSorted = dataOriginal, then they become pointers to the same array (data not copied.)
    private static int[] dataOriginal;
    private static int[] dataSorted;

    /* Fill the dataOriginal array with random integers. */
    public static void generateData(int setSize) {
        int i = 0;

        dataOriginal = new int[setSize];

        for (i = 0; i < dataOriginal.length; i++)
            dataOriginal[i] = random.nextInt(9999);
    }

    /*
    * Sort the array using selection sort.
    * Prerequisite: generateData() must have been called.
    * This is the whole process,
    * The array is printed out at intervals to show progress,
    * but not necessarily when it's sorted.
    */
    public static void selectSort() {
        // the element on the "hot seat" will (probably) be swapped at the end of each pass.
        int hotSeatIndex = 0;
        int hotSeatValue;
        int j = 0;
        // the smallest value, at the end of each pass, will be swapped with the hot seat
        // (unless it's equal to the value on the hot seat).
        int smallestIndex = 0;
        int smallestValue;

        // Check if generateData has been called by checking dataOriginal.
        // Throw IllegalStateException if dataOriginal hasn't been initialized.
        if (null == dataOriginal)
            throw new IllegalStateException("Data not generated before selection sort.");

        // Initialize data to be sorted.
        dataSorted = dataOriginal.clone();

        /* Starting condition: 0 is the hot seat index.
         * The smallest value is initialized to the first value in the array. */
        for ( hotSeatIndex = 0; hotSeatIndex < dataSorted.length; hotSeatIndex++ ) {

            smallestIndex = hotSeatIndex;
            hotSeatValue = dataSorted[hotSeatIndex];
            smallestValue = dataSorted[smallestIndex];

            for ( j = hotSeatIndex; j < dataSorted.length; j++ ) {
                if (smallestValue > dataSorted[j]) {
                    smallestIndex = j;
                    smallestValue = dataSorted[j];
                } // end if a smaller value found
            } // end for j

            // Now we've identified the smallest value on this pass.
            if ( smallestValue < hotSeatValue ) {
                dataSorted[hotSeatIndex] = smallestValue;
                dataSorted[smallestIndex] = hotSeatValue;
            } // end if we need to swap

            // Print out what we have so far if desired.
            if (1 == progressIncrement || 0 == hotSeatIndex % progressIncrement) {
                System.out.println(printSortedArray());
            } // end if printout desired

        } // end for hotSeatIndex
    }

    /* ************************************ */

    /*
    * Sort the array using quick sort (recursive).
    * Prerequisite: generateData() must have been called.
    * This is the whole process,
    * The array is printed out at intervals to show progress,
    * but not necessarily when it's sorted.
    */
    public static void quickSort() {

        if (null == dataOriginal)
            throw new IllegalStateException("Data not generated before quicksort.");

        // Initialize data to be sorted.
        dataSorted = dataOriginal.clone();

        quickSortRange(0, dataSorted.length - 1, 0);
    }

    /**
     * Sort the working array in the given range using a recursive sort.
     * @param start - index of dataSorted at which to start sorting.
     * @param end - index of dataSorted at which to end sorting.
     * @param depth - Not necessary for the sort. Just used to track progress. Remove in production.
     */
    private static void quickSortRange(int start, int end, int depth) {
        int i = start;
        int j = end - 1;
        int swapValue;
        int hotSeatValue = dataSorted[end];

        // Special case: start & end are very close together.
        // TODO - check if print out array in this case.
        if (end == start) {
            System.out.println("1 element to sort, depth = " + depth + ". Array at this point:");
            System.out.println(printSortedArray());
            return;
        } // end if start & end very close together
        else if (1 == end - start) {
            if (hotSeatValue < dataSorted[start]) {
                dataSorted[end] = dataSorted[start];
                dataSorted[start] = hotSeatValue;
            }
            // Print out what we have so far if desired.
            if (1 == progressIncrement || 0 == depth % progressIncrement) {
                System.out.println("2-element range: " + start + " to " + end + ", depth = " + depth + ". Array at this point:");
                System.out.println(printSortedArray());
            } // end if printout desired
            return;
        } // end if we have parameters specifying adjacent elements

        // At this point, start to end must specify at least 3 elements.

        do {
            // Find an element in dataSorted that's greater than hotSeatValue,
            // starting from the low end.
            while (i < end && dataSorted[i] <= hotSeatValue)
                i++;
            // Find an element in dataSorted that's less than hotSeatValue,
            // starting from the high end.
            while (j > i && dataSorted[j] >= hotSeatValue)
                j--;

            /* Possible states at this point:
             *  - i == end. This indicates that dataSorted[end] is the biggest # in the range.
             *  - j >= i. This indicates that all the values checked in the j portion of the loop
             *      (if any) are >= hotSeatValue. If i > j, then after making the last swap, the
             *      remaining values checked by i were < hotSeatValue.
             *  - i < j < end, which means i & j are indices of two elements that need to be
             *      swapped to be in the right relation to hotSeatValue.
             *  If the third state is the one we're in, do a swap and try to continue the loop.
             *  (It's possible that after the swap & the loop variables are advanced, then we'll
             *  be in one of the first two states.)
             */
            if (i < j) {

                swapValue = dataSorted[j];
                dataSorted[j] = dataSorted[i];
                dataSorted[i] = swapValue;
                i++;
                j--;

            } // end if swap & continue

            // Print out what we have so far if desired.
            if (1 == progressIncrement || 0 == (depth + end) % progressIncrement) {
                System.out.println("Range: " + start + " to " + end + ". Depth = " + depth + ". Array at this point:");
                System.out.println(printSortedArray());
            } // end if printout desired

        } while (i < end && j > i);

        if (i == end) {
            // dataSorted[end] is the biggest # in the range and is thus already in the right position.
            quickSortRange(start, end - 1, depth + 1);
        } else if (i >= j) {
            // We've finished checking start thru end.
            // So swap & sort the subranges.
            dataSorted[end] = dataSorted[i];
            dataSorted[i] = hotSeatValue;
            if (i - 1 > start) {
                // This condition catches the possibility that hotSeatValue was the smallest # in the range,
                // and in that case, there's no lower section of the range.
                quickSortRange(start, i - 1, depth + 1);
            }
            if (i + 1 < end) {
                quickSortRange(i + 1, end, depth + 1);
            }
        } // end which check the loop ended with

    }  // end quickSortRange

    /* end quick sort methods section */
    /* ************************************ */
    /* Some other useful methods */

    /* N.B. At present dataSorted has only been initialized if a sort method has been called. */

    @Override
    public String toString() {
        // By default, print the dataSorted array.
        // Check that dataSorted is set.
        if (null == dataSorted)
            throw new IllegalStateException("Attempted to print null array.");
        return getFormattedArray(dataSorted);
    }

    public static String printSortedArray() {
        // Check that dataSorted is set.
        if (null == dataSorted)
            throw new IllegalStateException("Attempted to print null array.");
        return getFormattedArray(dataSorted);
    }

    public static String printOriginalArray() {
        // Check that it's been initialized first.
        if (null == dataOriginal)
            throw new IllegalStateException("Attempted to print null array.");
        return getFormattedArray(dataOriginal, 7);
    }

    /**
     * Takes an array of integers and puts its elements into a string separated by spaces and periodic line breaks.
     * @param arrayToPrint - array of integers to make into a string.
     * @return String representing array
     */
    private static String getFormattedArray(int[] arrayToPrint) {

        return getFormattedArray(arrayToPrint, 6);

    }

    /**
     * Takes an array of integers and puts its elements into a string separated by spaces and periodic line breaks.
     * @param arrayToPrint - array of integers to make into a string.
     * @param lineLength - how many numbers to print on each line.
     *                   If left out, the default is 6.
     * @return String representing array
     */
    private static String getFormattedArray(int[] arrayToPrint, int lineLength) {
        int i = 0;
        StringBuilder builder = new StringBuilder();

        for (i = 0; i < arrayToPrint.length; i++) {
            builder.append(arrayToPrint[i]).append(" ");
            if ((lineLength - 1) == i % lineLength) {
                builder.append("\n");
            }  // end if
        }  // end for
        return builder.toString();
    }

    public static void main(String[] args) {
        int size = 10;  // set to a default value

        // Do we have any arguments?
        if (0 < args.length) {
            try {
                size = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.err.println("SortDemo: First argument must be whole number of elements in array to sort.");
                System.exit(1);
            }
        }  // end if have arguments

        // Initialize the array.
        try {
            generateData(size);
        } catch (NegativeArraySizeException e) {
            System.err.println("SortDemo: First argument must be whole (greater than 0) number of elements in array to sort.");
            System.exit(1);
        }
        // Show original array.
        System.out.println("Original data: " + printOriginalArray());

        // Sort.
        //selectSort();
        quickSort();
        // Show original:
        System.out.println("Original data: " + printOriginalArray());
        // Show results.
        System.out.println("Sorted data: " + printSortedArray());

    }
}
