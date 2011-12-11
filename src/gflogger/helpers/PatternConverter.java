/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *	  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package gflogger.helpers;

import gflogger.LogEntryItem;

import java.nio.CharBuffer;


/**
 * <p>
 * PatternConverter is an abtract class that provides the formatting
 * functionality that derived classes need.
 * 
 * <p>
 * Conversion specifiers in a conversion patterns are parsed to individual
 * PatternConverters. Each of which is responsible for converting a logging
 * event in a converter specific manner.
 * 
 * @author <a href="mailto:cakalijp@Maritz.com">James P. Cakalic</a>
 * @author Ceki G&uuml;lc&uuml;
 * @since 0.8.2
 */
public abstract class PatternConverter {
	public PatternConverter next;
	int					 min	   = -1;
	int					 max	   = 0x7FFFFFFF;
	boolean				 leftAlign = false;

	protected PatternConverter() {
	}

	protected PatternConverter(FormattingInfo fi) {
		min = fi.min;
		max = fi.max;
		leftAlign = fi.leftAlign;
	}

	abstract public void format(CharBuffer buffer, LogEntryItem entryImpl);

	static String[] SPACES = { 
			" ",				// 1 space 
			"  ",			   // 2 spaces
			"	",			 // 4 spaces
			"		",		 // 8 spaces
			"				", // 16 spaces
			"								" }; // 32 spaces

	/**
	 * Fast space padding method.
	 */
	public void spacePad(CharBuffer buffer, int length) {
		while (length >= 32) {
			buffer.append(SPACES[5]);
			length -= 32;
		}

		for (int i = 4; i >= 0; i--) {
			if ((length & (1 << i)) != 0) {
				buffer.append(SPACES[i]);
			}
		}
	}
}
