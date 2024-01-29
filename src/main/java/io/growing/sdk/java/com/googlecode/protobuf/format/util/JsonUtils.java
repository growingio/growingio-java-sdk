/*
	Copyright (c) 2009, Orbitz World Wide
	All rights reserved.

	Redistribution and use in source and binary forms, with or without modification,
	are permitted provided that the following conditions are met:

		* Redistributions of source code must retain the above copyright notice,
		  this list of conditions and the following disclaimer.
		* Redistributions in binary form must reproduce the above copyright notice,
		  this list of conditions and the following disclaimer in the documentation
		  and/or other materials provided with the distribution.
		* Neither the name of the Orbitz World Wide nor the names of its contributors
		  may be used to endorse or promote products derived from this software
		  without specific prior written permission.

	THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
	"AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
	LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
	A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
	OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
	SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
	LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
	DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
	THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
	(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
	OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

	The JsonUtils implementation is referenced from protobuf-java-format
	https://code.google.com/archive/p/protobuf-java-format/
*/

package io.growing.sdk.java.com.googlecode.protobuf.format.util;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;

public class JsonUtils {
    // =================================================================
    // Utility functions
    //
    // Some of these methods are package-private because Descriptors.java uses
    // them.

    /**
     * Implements JSON string escaping as specified <a href="http://www.ietf.org/rfc/rfc4627.txt">here</a>.
     * <ul>
     *  <li>The following characters are escaped by prefixing them with a '\' : \b,\f,\n,\r,\t,\,"</li>
     *  <li>Other control characters in the range 0x0000-0x001F are escaped using the \\uXXXX notation</li>
     *  <li>UTF-16 surrogate pairs are encoded using the \\uXXXX\\uXXXX notation</li>
     *  <li>any other character is printed as-is</li>
     * </ul>
     */
    public static String escapeText(String input) {
        StringBuilder builder = new StringBuilder(input.length());
        CharacterIterator iter = new StringCharacterIterator(input);
        for (char c = iter.first(); c != CharacterIterator.DONE; c = iter.next()) {
            switch (c) {
                case '\b':
                    builder.append("\\b");
                    break;
                case '\f':
                    builder.append("\\f");
                    break;
                case '\n':
                    builder.append("\\n");
                    break;
                case '\r':
                    builder.append("\\r");
                    break;
                case '\t':
                    builder.append("\\t");
                    break;
                case '\\':
                    builder.append("\\\\");
                    break;
                case '"':
                    builder.append("\\\"");
                    break;
                default:
                    // Check for other control characters
                    if (c >= 0x0000 && c <= 0x001F) {
                        appendEscapedUnicode(builder, c);
                    } else if (Character.isHighSurrogate(c)) {
                        // Encode the surrogate pair using 2 six-character sequence (\\uXXXX\\uXXXX)
                        appendEscapedUnicode(builder, c);
                        c = iter.next();
                        if (c == CharacterIterator.DONE) {
                            throw new IllegalArgumentException("invalid unicode string: unexpected high surrogate pair value without corresponding low value.");
                        }
                        appendEscapedUnicode(builder, c);
                    } else {
                        // Anything else can be printed as-is
                        builder.append(c);
                    }
                    break;
            }
        }
        return builder.toString();
    }

    static void appendEscapedUnicode(StringBuilder builder, char ch) {
        String prefix = "\\u";
        if (ch < 0x10) {
            prefix = "\\u000";
        } else if (ch < 0x100) {
            prefix = "\\u00";
        } else if (ch < 0x1000) {
            prefix = "\\u0";
        }
        builder.append(prefix).append(Integer.toHexString(ch));
    }
}
