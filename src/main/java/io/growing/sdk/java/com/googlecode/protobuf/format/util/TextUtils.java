package io.growing.sdk.java.com.googlecode.protobuf.format.util;

/**
 * Copyright 2000-2011 NeuStar, Inc. All rights reserved.
 * NeuStar, the Neustar logo and related names and logos are registered
 * trademarks, service marks or tradenames of NeuStar, Inc. All other
 * product names, company names, marks, logos and symbols may be trademarks
 * of their respective owners.
 *
 * The TextUtils implementation is referenced from protobuf-java-format
 * https://code.google.com/archive/p/protobuf-java-format/
 */

import java.math.BigInteger;
import java.util.regex.Pattern;

/**
 * Utilities for coercing types
 * largely follows google/protobuf/text_format.cc.
 */
public class TextUtils {
    private static final Pattern DIGITS =
            Pattern.compile("[0-9]", Pattern.CASE_INSENSITIVE);

    /**
     * Convert an unsigned 64-bit integer to a string.
     */
    public static String unsignedToString(final long value) {
        if (value >= 0) {
            return Long.toString(value);
        } else {
            // Pull off the most-significant bit so that BigInteger doesn't think
            // the number is negative, then set it again using setBit().
            return BigInteger.valueOf(value & 0x7FFFFFFFFFFFFFFFL).setBit(63).toString();
        }
    }

    /**
     * Convert an unsigned 32-bit integer to a string.
     */
    public static String unsignedToString(final int value) {
        if (value >= 0) {
            return Integer.toString(value);
        } else {
            return Long.toString((value) & 0x00000000FFFFFFFFL);
        }
    }

    /**
     * Is this a hex digit?
     */
    public static boolean isHex(final char c) {
        return ('0' <= c && c <= '9') ||
                ('a' <= c && c <= 'f') ||
                ('A' <= c && c <= 'F');
    }

    /**
     * Is this an octal digit?
     */
    public static boolean isOctal(final char c) {
        return '0' <= c && c <= '7';
    }

    /**
     * Interpret a character as a digit (in any base up to 36) and return the
     * numeric value.  This is like {@code Character.digit()} but we don't accept
     * non-ASCII digits.
     */
    public static int digitValue(final char c) {
        if ('0' <= c && c <= '9') {
            return c - '0';
        } else if ('a' <= c && c <= 'z') {
            return c - 'a' + 10;
        } else {
            return c - 'A' + 10;
        }
    }

    public static boolean isDigits(final String text) {
        return DIGITS.matcher(text).matches();
    }
}

