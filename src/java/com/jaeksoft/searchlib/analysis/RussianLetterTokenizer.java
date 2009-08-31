/**   
 * License Agreement for Jaeksoft OpenSearchServer
 *
 * Copyright (C) 2008 Emmanuel Keller / Jaeksoft
 * 
 * http://www.open-search-server.com
 * 
 * This file is part of Jaeksoft OpenSearchServer.
 *
 * Jaeksoft OpenSearchServer is free software: you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 * Jaeksoft OpenSearchServer is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Jaeksoft OpenSearchServer. 
 *  If not, see <http://www.gnu.org/licenses/>.
 **/

package com.jaeksoft.searchlib.analysis;

import java.io.IOException;
import java.io.Reader;

import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.ru.RussianCharsets;
import org.w3c.dom.Node;

import com.jaeksoft.searchlib.util.XPathParser;

public class RussianLetterTokenizer extends TokenizerFactory {

	protected char[] charset = null;

	@Override
	public void setParams(XPathParser xpp, Node node) throws IOException {
		String cs = XPathParser.getAttributeString(node, "charset");
		if ("cp1251".equalsIgnoreCase(cs))
			charset = RussianCharsets.CP1251;
		else if ("koi8".equalsIgnoreCase(cs))
			charset = RussianCharsets.KOI8;
		else
			charset = RussianCharsets.UnicodeRussian;
	}

	@Override
	public Tokenizer create(Reader reader) {
		return new org.apache.lucene.analysis.ru.RussianLetterTokenizer(reader,
				charset);
	}

	@Override
	public String getDescription() {
		return "Chinese tokenizer";
	}

}
