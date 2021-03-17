import java.io.*;
import java.util.*;

public class Last {
	public static void main(String[] args) throws FileNotFoundException {
		int mode = Integer.parseInt(args[0]);// taking the mode from arguments
		String inputFileName = args[1];// name of the input file from arguments
		File input = new File(inputFileName);// to scan the file i declared a file
		Scanner s = new Scanner(input);// i used scanner to read the file
		String imageFormat = s.next();// image format from the header of the file
		int columns = s.nextInt();// number of columns from the header of the file
		int rows = s.nextInt();// number of rows from the header of the file
		int max = s.nextInt();// maximum number can be given to a number in the image array
		int[][][] image = new int[rows][columns][3];// image array to keep the numbers
		// this nested loop fills the array with the values
		for (int r = 0; r < rows; r++) {
			for (int c = 0; c < columns; c++) {
				image[r][c][0] = s.nextInt();// to fill the red layer
				image[r][c][1] = s.nextInt();// to fill the green layer
				image[r][c][2] = s.nextInt();// to fill the blue layer
			}
		}
		// this part calls the proper methods
		if (mode == 0) {
			mode0(rows, columns, max, image, imageFormat);// calling mode0 method
		} else if (mode == 1) {
			mode1(rows, columns, max, image, imageFormat);// calling mode1 method
		} else if (mode == 2) {
			String filterName = args[2];// filter name from the arguments
			File f = new File(filterName);// a new file for the filter
			Scanner s1 = new Scanner(f);// scanner to read the filter from the file
			String filterR = s1.nextLine();// header of the filter file
			int filterRow = Integer.parseInt(filterR.substring(0, filterR.indexOf("x")));// to get the row length number out of the header as long as it has "x" in it
			int filterColumn = filterRow;// column length which is always equal to filter row length but i wanted my code to be more readable
			int[][] filter = new int[filterRow][filterColumn];// filter array to keep the filter values
			// this nested loop fills the filter array
			for (int i = 0; i < filterRow; i++) {
				for (int j = 0; j < filterColumn; j++) {
					filter[i][j] = s1.nextInt();
				}
			}
			s1.close();
			mode2(rows, columns, max, image, imageFormat, filter, filterRow);// calling mode2 method
		} else if (mode == 3) {
			int range = Integer.parseInt(args[2]);// range number from arguments
			mode3(rows, columns, max, image, imageFormat, range);// calling mode3 method
		}
		s.close();
	}

	// this method prints the array (part 2)
	public static void mode0(int rows, int columns, int max, int[][][] image, String imageFormat)
			throws FileNotFoundException {
		String outputName = "output.ppm";// name of the output file
		int[][][] output = new int[rows][columns][3];// array for the output
		// this part fills the output array with image arrays values
		for (int r = 0; r < rows; r++) {
			for (int c = 0; c < columns; c++) {
				output[r][c][0] = image[r][c][0];
				output[r][c][1] = image[r][c][1];
				output[r][c][2] = image[r][c][2];
			}
		}
		printFile(output, outputName, rows, columns, max, imageFormat);// calls printFile method to print to file
	}

	// this method makes turns the image to black and white (part 3)
	public static void mode1(int rows, int columns, int max, int[][][] image, String imageFormat)
			throws FileNotFoundException {
		String outputName = "black-and-white.ppm";// name of the output file
		int[][][] output = new int[rows][columns][3];// array for the output
		// this nested loop finds the average of the three values in red green and blue and assigns average value to all three
		for (int i = 0; i < image.length; i++) {
			for (int j = 0; j < image[i].length; j++) {
				int average = (image[i][j][0] + image[i][j][1] + image[i][j][2]) / 3;// average value
				output[i][j][0] = average;
				output[i][j][1] = average;
				output[i][j][2] = average;
			}
		}
		printFile(output, outputName, rows, columns, max, imageFormat);// calls printFile method to print to file
	}

	// this method makes convolution to image (part 4)
	public static void mode2(int rows, int columns, int max, int[][][] image, String imageFormat, int[][] filter,
			int filterRow) throws FileNotFoundException {
		String outputName = "convolution.ppm";// name of the output file
		int filterColumn = filterRow;// filters column length (row length is given to the method)
		int[][][] output = new int[rows - (filterRow + 1) / 2][columns - (filterRow + 1) / 2][3];// new output array with smaller lengths
		// this part calculates the multiple and sum of the filter and image array and writes the result to output array
		for (int i = 0; i < rows - (filterRow + 1) / 2; i++) {
			for (int j = 0; j < columns - (filterColumn + 1) / 2; j++) {
				int sumR = 0, sumG = 0, sumB = 0;// individual sums of layers
				for (int j2 = 0; j2 < filterRow; j2++) {
					for (int k = 0; k < filterColumn; k++) {
						sumR += image[i + j2][j + k][0] * filter[j2][k];// applies the filter to red layer
						sumG += image[i + j2][j + k][1] * filter[j2][k];// applies the filter to green layer
						sumB += image[i + j2][j + k][2] * filter[j2][k];// applies the filter to blue layer
					}
				}
				if (sumR <= 0)
					sumR = 0;// changes the value to zero if it is below zero
				else if (sumR >= max)
					sumR = max;// changes the value to max if it is over maximum
				if (sumG <= 0)
					sumG = 0;// changes the value to zero if it is below zero
				else if (sumG >= max)
					sumG = max;// changes the value to max if it is over maximum
				if (sumB <= 0)
					sumB = 0;// changes the value to zero if it is below zero
				else if (sumB >= max)
					sumB = max;// changes the value to max if it is over maximum
				int average = (sumB + sumG + sumR) / 3;// to make the image black and white i took the average of three layers & changed them to average
				output[i][j][0] = average;
				output[i][j][1] = average;
				output[i][j][2] = average;
			}
		}
		// calls the printFile method to print to file
		printFile(output, outputName, rows - (filterRow + 1) / 2, columns - (filterRow + 1) / 2, max, imageFormat);
	}

	// this method performs color quantization by calling a recursive method several times (part 5)
	public static void mode3(int rows, int columns, int max, int[][][] image, String imageFormat, int range)
			throws FileNotFoundException {
		String outputName = "quantized.ppm";// name of the output file
		int[][][] output = new int[rows][columns][3];// array for output
		boolean[][][] keepTrack = new boolean[rows][columns][3];// array for keeping the track of changes
		// this part calls recursive method the number of numbers times to quantize every pixel
		for (int k = 0; k < image[0][0].length; k++) {
			for (int i = 0; i < image.length; i++) {
				for (int j = 0; j < image[0].length; j++) {
					recursivePart(image, keepTrack, i, j, k, range, image[i][j][k], output);// where everything happens:)
				}
			}
		}
		printFile(output, outputName, rows, columns, max, imageFormat);// calls the printFile method to print to file
	}

	// this part calls itself recursively to look at the six neighbors of the pixel
	public static void recursivePart(int[][][] image, boolean[][][] keepTrack, int x, int y, int z, int range,
			int numberStarted, int[][][] output) {
		// checks the numbers to not to go out of the size of the array
		if ((image.length > x && x >= 0) && (image[0].length > y && y >= 0) && (image[0][0].length > z && z >= 0)) {
			if (keepTrack[x][y][z] == true) {
				return;// if we have changed the number in that pixel before returns back (true in the keepTrack array means it has been changed)
			} else if ((int) Math.abs(image[x][y][z] - numberStarted) <= range) {// if it is not changed before and it is in the range given
				output[x][y][z] = numberStarted;// we change the number to the number we have started with
				keepTrack[x][y][z] = true;// means we changed the value
				recursivePart(image, keepTrack, x, y + 1, z, range, numberStarted, output);// calls itself by incrementing y to look right neighbor of the pixel							
				recursivePart(image, keepTrack, x, y - 1, z, range, numberStarted, output);// calls itself by decrementing y to look left neighbor of the pixel								

				recursivePart(image, keepTrack, x + 1, y, z, range, numberStarted, output);// calls itself by decrementing x to look top neighbor of the pixel
				recursivePart(image, keepTrack, x - 1, y, z, range, numberStarted, output);// calls itself by decrementing x to look lower neighbor of the pixel

				recursivePart(image, keepTrack, x, y, z + 1, range, numberStarted, output);// calls itself by incrementing z to look at deeper layer neighbor of the pixel
				recursivePart(image, keepTrack, x, y, z - 1, range, numberStarted, output);// calls itself by decrementing z  to look at shallow layer neighbor of the pixel
			} else {
				return;// if it is not changed before and out of the range given returns back
			}
		} else {
			return;// if numbers are out of bounds of the array returns back
		}
	}

	// this method prints the given arrays to a file whose name is given too
	public static void printFile(int[][][] output, String outputName, int rows, int columns, int max,
			String imageFormat) throws FileNotFoundException {
		PrintStream p = new PrintStream(new File(outputName));// creates a new file and starts to print on it
		p.println(imageFormat + "\n" + columns + " " + rows + "\n" + max);// prints the header of the "ppm" file
		// this part prints the array to file in a proper way
		for (int i = 0; i < output.length; i++) {
			for (int j = 0; j < output[i].length; j++) {
				for (int k = 0; k < output[i][j].length; k++) {
					p.print(output[i][j][k] + " ");
				}
				p.print("	");
			}
			p.println();
		}
	}
}
