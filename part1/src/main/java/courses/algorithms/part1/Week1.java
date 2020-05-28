package courses.algorithms.part1;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.Arrays;

import static java.lang.Integer.parseInt;

public class Week1 {

    public static final void main(String[] args) {
        String x = args[0];
        String y = args[1];
        long start = System.currentTimeMillis();
        BigInteger xy = multiply(x.trim(),y.trim());
        System.out.println(x + " * " + y + " = " + xy);
    }

    private static BigInteger multiply(String x, String y) {
        int xLength = x.length();
        int yLength = y.length();
        if (xLength == 1 && yLength == 1) {
            int xint = parseInt(x);
            int yint = parseInt(y);
            return BigInteger.valueOf(xint * yint);
        }
        if (xLength > yLength) {
            y = fillWithTrailingZeros(y, xLength - yLength);
            yLength = xLength;
        } else if (yLength > xLength) {
            x = fillWithTrailingZeros(x, yLength - xLength);
            xLength = yLength;
        }
        if (xLength % 2 ==0) {
            return multiplyKaratsuba(x, y);
        } else {
            return multiplySchoolMethod(x, y);
        }
    }

    private static BigInteger multiplySchoolMethod(String x, String y) {
        return new BigInteger(x).multiply(new BigInteger(y));
    }

    private static String fillWithTrailingZeros(String s, int numOf0) {
        char[] array = new char[numOf0];
        Arrays.fill(array, '0');
        return String.valueOf(array).concat(s);
    }

    private static BigInteger multiplyKaratsuba(String x, String y) {
        System.out.println("x: " + x);
        System.out.println("y: " + y);
        int xLength = x.length();
        int yLength = y.length();
        int xHalf = halfLength(xLength);
        int yHalf = halfLength(yLength);
        //assuming x=ab, y=cd
        String a = x.substring(0, xHalf);
        String b = x.substring(xHalf, xLength);
        String c = y.substring(0, yHalf);
        String d = y.substring(yHalf, yLength);
        //Step 1
        BigInteger ac = multiply(a, c);
        //Step 2
        BigInteger bd = multiply(b, d);
        //Step 3
        BigInteger apbcpd = multiply(
                new BigInteger(a).add(new BigInteger(b)).toString(),
                new BigInteger(c).add(new BigInteger(d)).toString()
        );
        //Step 4
        BigInteger adpbc = apbcpd.add(ac.negate()).add(bd.negate());
        //System.out.println("adpbc: " + adpbc);
        //Step 5
        BigInteger result = BigInteger.valueOf(10).pow(xLength).multiply(ac)
                .add(
                        BigInteger.valueOf(10).pow(xHalf).multiply(adpbc)
                )
                .add(
                        bd
                );
        BigInteger expectedResult = new BigInteger(x).multiply(new BigInteger(y));
        if (!expectedResult.equals(result)) {
            System.out.println("x: " + x);
            System.out.println("y: " + y);
            System.out.println("result: " + result);
            System.out.println("expected result: " + expectedResult);
        }
        return result;
    }

    private static int halfLength(int length) {
        return new BigDecimal((double)length / 2)
                .setScale(0, RoundingMode.HALF_UP)
                .intValue();
    }
}
