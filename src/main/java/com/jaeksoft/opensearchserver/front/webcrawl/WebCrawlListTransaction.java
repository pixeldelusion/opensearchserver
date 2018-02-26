/*
 * Copyright 2017-2018 Emmanuel Keller / Jaeksoft
 *  <p>
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *  <p>
 *  http://www.apache.org/licenses/LICENSE-2.0
 *  <p>
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.jaeksoft.opensearchserver.front.webcrawl;

import com.jaeksoft.opensearchserver.front.ServletTransaction;
import com.jaeksoft.opensearchserver.model.WebCrawlRecord;
import com.jaeksoft.opensearchserver.services.WebCrawlsService;
import com.qwazr.crawler.web.WebCrawlDefinition;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class WebCrawlListTransaction extends ServletTransaction {

	private final static String TEMPLATE_INDEX = "web_crawl/list.ftl";

	private final WebCrawlsService webCrawlsService;

	WebCrawlListTransaction(final CrawlerWebServlet servlet, final HttpServletRequest request,
			final HttpServletResponse response) {
		super(servlet.freemarker, request, response);
		webCrawlsService = servlet.webCrawlsService;
	}

	public void create() throws IOException, ServletException {
		final String crawlName = request.getParameter("crawlName");
		final String entryUrl = request.getParameter("entryUrl");
		final Integer maxDepth = getRequestParameter("maxDepth", null);
		final WebCrawlDefinition.Builder webCrawlDefBuilder =
				WebCrawlDefinition.of().setEntryUrl(entryUrl).setMaxDepth(maxDepth);
		webCrawlsService.save(getAccountSchema(),
				WebCrawlRecord.of().name(crawlName).crawlDefinition(webCrawlDefBuilder.build()).build());
		doGet();
	}

	@Override
	protected void doGet() throws IOException, ServletException {
		final int start = getRequestParameter("start", 0);
		final int rows = getRequestParameter("rows", 25);

		final List<WebCrawlRecord> webCrawlRecords = new ArrayList<>();
		final int totalCount = webCrawlsService.collect(getAccountSchema(), start, rows, webCrawlRecords::add);

		request.setAttribute("webCrawlRecords", webCrawlRecords);
		request.setAttribute("totalCount", totalCount);
		doTemplate(TEMPLATE_INDEX);
	}
}
