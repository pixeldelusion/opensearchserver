/**   
 * License Agreement for Jaeksoft SearchLib Community
 *
 * Copyright (C) 2008 Emmanuel Keller / Jaeksoft
 * 
 * http://www.jaeksoft.com
 * 
 * This file is part of Jaeksoft SearchLib Community.
 *
 * Jaeksoft SearchLib Community is free software: you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 * Jaeksoft SearchLib Community is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Jaeksoft SearchLib Community. 
 *  If not, see <http://www.gnu.org/licenses/>.
 **/

package com.jaeksoft.searchlib.index;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;

import javax.xml.xpath.XPathExpressionException;

import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.store.LockObtainFailedException;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.jaeksoft.searchlib.function.expression.SyntaxError;
import com.jaeksoft.searchlib.request.Request;
import com.jaeksoft.searchlib.result.DocumentResult;
import com.jaeksoft.searchlib.result.DocumentsGroup;
import com.jaeksoft.searchlib.result.Result;
import com.jaeksoft.searchlib.schema.Schema;
import com.jaeksoft.searchlib.util.XPathParser;

public class IndexGroup extends IndexAbstract {

	private LinkedHashMap<String, IndexAbstract> indices;

	public IndexGroup(File homeDir, XPathParser xpp, Node parentNode,
			boolean createIfNotExists) throws XPathExpressionException,
			IOException {
		super();
		indices = new LinkedHashMap<String, IndexAbstract>();
		NodeList nodes = xpp.getNodeList(parentNode, "index");
		for (int i = 0; i < nodes.getLength(); i++) {
			Node node = nodes.item(i);
			IndexConfig indexConfig = new IndexConfig(xpp, node);
			indices.put(indexConfig.getName(), new IndexLocal(homeDir,
					indexConfig, createIfNotExists));
		}
	}

	public Collection<IndexAbstract> getIndices() {
		return indices.values();
	}

	public int size() {
		return indices.size();
	}

	@Override
	public IndexAbstract get(String name) {
		return indices.get(name);
	}

	public void xmlInfo(PrintWriter writer, HashSet<String> classDetail) {
		writer.println("<indices>");
		for (IndexAbstract index : indices.values())
			index.xmlInfo(writer, classDetail);
		writer.println("</indices>");
	}

	public void optimize(String indexName, boolean forceLocal)
			throws CorruptIndexException, LockObtainFailedException,
			IOException {
		if (indexName == null || indexName.length() == 0) {
			for (IndexAbstract index : getIndices())
				index.optimize(null, forceLocal);
			return;
		}
		IndexAbstract index = get(indexName);
		if (index == null)
			return;
		index.optimize(null, forceLocal);
	}

	public IndexStatistics getStatistics() throws IOException {
		IndexStatistics stats = new IndexStatistics();
		for (IndexAbstract index : indices.values())
			stats.add(index.getStatistics());
		return stats;
	}

	public void updateDocument(Schema schema, IndexDocument document,
			boolean forceLocal) throws NoSuchAlgorithmException, IOException {
		for (IndexAbstract index : getIndices())
			index.updateDocument(schema, document, forceLocal);
	}

	public void updateDocument(String indexName, Schema schema,
			IndexDocument document, boolean forceLocal)
			throws NoSuchAlgorithmException, IOException {
		IndexAbstract index = get(indexName);
		if (index == null)
			return;
		index.updateDocument(schema, document, forceLocal);

	}

	public void updateDocuments(Schema schema,
			List<? extends IndexDocument> documents, boolean forceLocal)
			throws NoSuchAlgorithmException, IOException {
		for (IndexAbstract index : getIndices())
			index.updateDocuments(schema, documents, forceLocal);
	}

	public void updateDocuments(String indexName, Schema schema,
			List<? extends IndexDocument> documents, boolean forceLocal)
			throws NoSuchAlgorithmException, IOException {
		IndexAbstract index = get(indexName);
		if (index == null)
			return;
		index.updateDocuments(indexName, schema, documents, forceLocal);
	}

	public void deleteDocuments(Schema schema, String uniqueField,
			boolean forceLocal) throws CorruptIndexException,
			LockObtainFailedException, IOException {
		for (IndexAbstract index : getIndices())
			index.deleteDocuments(schema, uniqueField, forceLocal);
	}

	public void deleteDocuments(String indexName, Schema schema,
			String uniqueField, boolean forceLocal)
			throws CorruptIndexException, LockObtainFailedException,
			IOException {
		IndexAbstract index = get(indexName);
		if (index == null)
			return;
		index.deleteDocuments(schema, uniqueField, forceLocal);
	}

	public void deleteDocuments(Schema schema, List<String> uniqueFields,
			boolean forceLocal) throws CorruptIndexException,
			LockObtainFailedException, IOException {
		for (IndexAbstract index : getIndices())
			index.deleteDocuments(schema, uniqueFields, forceLocal);
	}

	public void deleteDocuments(String indexName, Schema schema,
			List<String> uniqueFields, boolean forceLocal)
			throws CorruptIndexException, LockObtainFailedException,
			IOException {
		IndexAbstract index = get(indexName);
		if (index == null)
			return;
		index.deleteDocuments(schema, uniqueFields, forceLocal);
	}

	public int getDocFreq(Term term) throws IOException {
		int r = 0;
		for (IndexAbstract index : getIndices())
			r += index.getDocFreq(term);
		return r;
	}

	public DocumentResult documents(Request request)
			throws CorruptIndexException, IOException, ParseException {
		DocumentsGroup documentsGroup = new DocumentsGroup(request);
		return documentsGroup.documents();
	}

	public void reload(String indexName) throws IOException {
		for (IndexAbstract index : getIndices())
			index.reload(indexName);
	}

	public void swap(String indexName, long version, boolean deleteOld)
			throws IOException {
		for (IndexAbstract index : getIndices())
			index.swap(indexName, version, deleteOld);
	}

	public Result search(Request request) throws IOException, ParseException,
			SyntaxError {
		if (indices.size() == 1)
			request.setReader(indices.values().iterator().next());
		if (request.getReader() != null)
			return request.getReader().search(request);
		SearchGroup searchGroup = new SearchGroup(this);
		return searchGroup.search(request);
	}

	public boolean sameIndex(ReaderInterface reader) {
		return reader == this;
	}

	@Override
	public void receive(String indexName, long version, String fileName,
			InputStream inputStream) throws IOException {
		IndexAbstract idx = get(indexName);
		if (idx == null)
			return;
		idx.receive(indexName, version, fileName, inputStream);
	}

	@Override
	public void push(String indexName, URI dest) throws URISyntaxException,
			IOException {
		IndexAbstract index = get(indexName);
		if (index != null) {
			index.push(indexName, dest);
			return;
		}
		for (IndexAbstract idx : getIndices())
			idx.push(idx.getName(), dest);
	}

	@Override
	public boolean isOnline(String indexName) {
		IndexAbstract index = get(indexName);
		if (index == null)
			return false;
		return index.isOnline(indexName);
	}

	@Override
	public boolean isReadOnly(String indexName) {
		IndexAbstract index = get(indexName);
		if (index == null)
			return false;
		return index.isReadOnly(indexName);
	}

	@Override
	public void setOnline(String indexName, boolean v) {
		IndexAbstract index = get(indexName);
		if (index == null)
			return;
		index.setOnline(indexName, v);
	}

	@Override
	public void setReadOnly(String indexName, boolean v) {
		IndexAbstract index = get(indexName);
		if (index == null)
			return;
		index.setReadOnly(indexName, v);
	}

	public long getVersion(String indexName) {
		IndexAbstract index = get(indexName);
		if (index == null)
			return 0;
		return index.getVersion(indexName);
	}
}
