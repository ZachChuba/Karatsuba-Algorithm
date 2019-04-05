
public class EfficientMultiplication {

	public static void main(String[] args) {
		System.out.println("133645588249210855608431216237767767169932655252887752594".equals("133645588249210855608431216237767767169932655252887752594"));
		System.out.println(multiply("12874013750135780183103481034", "10381035071350713507327080341"));
	}

	// Assume all values are + integers represented as strings
	public static String multiply(String factor1, String factor2) {
		// base case of length = 1, for odd factors 1 length = 1 and 1 length = 2
		if (factor1.length() == 1 || factor2.length() == 1) {
			if (factor1.length() == 1) {
				return smallNumberMultiplication(factor1, factor2);
			}
			return smallNumberMultiplication(factor2, factor1);
		}

		// define the length of the smaller half of the numbers to be the 
		// floored average length of the numbers
		int smallerHalfLength = (factor1.length()) / 4;
		
		// divide both numbers such that the smaller half is of length smallerHalfLength
		String[] tempSplitString = splitString(factor1, smallerHalfLength);
		String f1Large = tempSplitString[0], f1Small = tempSplitString[1];
		tempSplitString = splitString(factor2, smallerHalfLength);
		String f2Large = tempSplitString[0], f2Small = tempSplitString[1];
		
		// large factor1 * large factor2
		String multiplyLargestNumbers = multiply(f1Large, f2Large);
		// (f1Large + f1Small)*(f2Large + f2Small)
		String multiplyMiddleNumbers = multiply(addTwoValues(f1Large, f1Small), addTwoValues(f2Large, f2Small));
		// small factor1 * small factor2
		String multiplySmallestNumbers = multiply(f1Small, f2Small);

		// add trailing zeros to values (multiply by 10^smallerHalfLength)
		String finalHighestNumber = addTrailingZeros(multiplyLargestNumbers, smallerHalfLength + smallerHalfLength);
		String finalMiddleNumber = addTrailingZeros(
				subtractTwoValues(subtractTwoValues(multiplyMiddleNumbers, multiplyLargestNumbers),
						multiplySmallestNumbers),
				smallerHalfLength);
		String finalSmallestNumber = multiplySmallestNumbers;
		
		// add each part together
		return removeLeadingZeros(addTwoValues(addTwoValues(finalHighestNumber, finalMiddleNumber), finalSmallestNumber));
	}
	
	/**
	 * 
	 * @param factor1 String of base10 number of max length 2
	 * @param factor2 String of base10 number of max length 2
	 * @return the product of factor1 and factor2
	 */
	private static String smallNumberMultiplication(String factor1, String factor2) {
		int fac1Int = Integer.parseInt(factor1), fac2Int = Integer.parseInt(factor2);
		int total = 0;
		for (int i = 0; i < fac1Int; i++) {
			total += fac2Int;
		}
		return Integer.toString(total);
	}
	
	/**
	 * 
	 * given a string and the middle index for it to be split
	 *  returns two strings, one from 0 -> smallerHalfLength (excluded)
	 *  the second one from smallerHalfLength -> end
	 */
	private static String[] splitString(String factor, int smallerHalfLength) {
		// if this happens, there is bound to be a stack overflow
		if (factor.length() == 1) {
			throw new UnsupportedOperationException();
		}
		
		String largerHalf, smallerHalf;
		largerHalf = factor.substring(0, factor.length() - smallerHalfLength);
		smallerHalf = factor.substring(factor.length() - smallerHalfLength);
		
		assert(smallerHalfLength == smallerHalf.length());
		
		return new String[] { largerHalf, smallerHalf };
	}
	
	/**
	 * 
	 * @param inputtedNumber string representation of number
	 * @param numberZeros number of zeros to add trailing it
	 * @return inputtedNumber + "0" * numberZeros
	 */
	private static String addTrailingZeros(String inputtedNumber, int numberZeros) {
		StringBuilder baseNumber = new StringBuilder(inputtedNumber);
		for (int i = 0; i < numberZeros; i++) {
			baseNumber.append("0");
		}
		return baseNumber.toString();
	}
	
	/**
	 * 
	 * @param factor a string representation of a base 10 number
	 * @return a string representation of the inputted base 10 number
	 * 			with extraneous leading zeros removed
	 */
	private static String removeLeadingZeros(String factor) {
		char[] factorAsCharArray = factor.toCharArray();
		int removalCount;
		for (removalCount = 0; removalCount < factorAsCharArray.length - 1; removalCount++) {
			if (factorAsCharArray[removalCount] != '0') {
				break;
			}
			factorAsCharArray[removalCount] = 'r';
		} // best case optimization 
		if (removalCount == 0) {
			return factor;
		}
		char[] cleanFactor = new char[factorAsCharArray.length - removalCount];
		int skippedCount = 0;
		for (int i = 0; i < factorAsCharArray.length; i++) {
			if (factorAsCharArray[i] == 'r') {
				skippedCount++;
				continue;
			}
			cleanFactor[i - skippedCount] = factorAsCharArray[i];
		}
		return new String(cleanFactor);
	}
	
	/**
	 * 
	 * Given two strings of base 10 values, returns string1 + string2
	 */
	private static String addTwoValues(String value1, String value2) {
		int val1PositionPointer = value1.length() - 1, val2PositionPointer = value2.length() - 1;
		char[] value1AsArray = value1.toCharArray(), value2AsArray = value2.toCharArray();
		int longerValue = largerStringLength(value1, value2);
		char[] resultantCharArray = new char[longerValue + 1];
		int currentSum = 0;
		int extra10FromPrevious = 0;

		// for each base 10 value starting at 10^0 spot, add the two values together
		for (int i = 0; i < longerValue; i++) {
			// carry the ones from the previous value's addition
			if (extra10FromPrevious != 0) {
				currentSum += extra10FromPrevious;
				extra10FromPrevious--;
			}
			// add each value in the corresponding 10^ith place together
			if (val1PositionPointer - i >= 0) {
				currentSum += value1AsArray[val1PositionPointer - i] - '0';
			}
			if (val2PositionPointer - i >= 0) {
				currentSum += value2AsArray[val2PositionPointer - i] - '0';
			}
			// If the addition of the values yields a value greater than 10
			// Carry the 1 to the next iteration, add that to the total sum
			if (currentSum > 9) {
				currentSum -= 10;
				extra10FromPrevious++;
				resultantCharArray[resultantCharArray.length - i - 1] = (char) (currentSum + '0');
			} else {
				resultantCharArray[resultantCharArray.length - i - 1] = (char) (currentSum + '0');
			}
			currentSum = 0;
		}
		// carry 1 to a 10^n place not in either of the numbers if applicable
		if (extra10FromPrevious > 0) {
			resultantCharArray[0] = (char) (extra10FromPrevious + '0');
		}
		
		// build the final number
		StringBuilder resultantValue = new StringBuilder();
		for (int i = 0; i < resultantCharArray.length; i++) {
			// Prevent leading zeros
			if (i == 0 && extra10FromPrevious == 0) {
				continue;
			}
			resultantValue.append(resultantCharArray[i]);
		}
		return resultantValue.toString();
	}
	
	/**
	 * 
	 * @param largerValue string representation of base 10 number
	 * @param smallerValue string representation of smaller in magnitude base 10 number
	 * @return base 10 largerValue - smallerValue
	 */
	private static String subtractTwoValues(String largerValue, String smallerValue) {

		int largerValuePointer = largerValue.length() - 1, smallerValuePointer = smallerValue.length() - 1;
		char[] largerValAsArray = largerValue.toCharArray(), smallerValAsArray = smallerValue.toCharArray();
		int longerValue = largerValue.length();
		char[] resultantCharArray = new char[longerValue];
		int currentResult = 0;
		int extra10FromPrevious = 0;
		
		// iterate through both numbers, starting at 10^0s place
		for (int i = 0; i < longerValue; i++) {
			// make the current number the larger number's value
			if (largerValuePointer - i >= 0) {
				currentResult = largerValAsArray[largerValuePointer - i] - '0';
			}
			// subtract any carried ones from the previous value
			if (extra10FromPrevious > 0) {
				currentResult -= extra10FromPrevious;
				extra10FromPrevious--;
			}
			// subtract the smaller number's value from larger
			if (smallerValuePointer - i >= 0) {
				currentResult -= smallerValAsArray[smallerValuePointer - i] - '0';
			}
			// If the result is -, carry a 1
			if (currentResult < 0) {
				currentResult += 10;
				extra10FromPrevious++;
				resultantCharArray[longerValue - i - 1] = (char) (currentResult + '0');

			} else {
				resultantCharArray[longerValue - i - 1] = (char) (currentResult + '0');
			}
			currentResult = 0;
		}
		return new String(resultantCharArray);
	}
	
	/**
	 * 
	 * @param str1
	 * @param str2
	 * @return the length of the larger string
	 */
	private static int largerStringLength(String str1, String str2) {
		if (str1.length() > str2.length()) {
			return str1.length();
		}
		return str2.length();
	}

}
