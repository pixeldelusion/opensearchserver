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

package com.jaeksoft.opensearchserver.front.accounts.indexes;

import com.jaeksoft.opensearchserver.Components;
import com.jaeksoft.opensearchserver.front.Message;
import com.jaeksoft.opensearchserver.front.accounts.AccountTransaction;
import com.jaeksoft.opensearchserver.model.AccountRecord;
import com.jaeksoft.opensearchserver.services.IndexService;
import com.jaeksoft.opensearchserver.services.IndexesService;
import com.qwazr.search.index.IndexStatus;
import com.qwazr.utils.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class InfosTransaction extends AccountTransaction {

    private final static String TEMPLATE = "accounts/indexes/infos.ftl";

    private final IndexesService indexesService;
    private final String indexName;

    public InfosTransaction(final Components components, final AccountRecord accountRecord, final String indexName,
        final HttpServletRequest request, final HttpServletResponse response) {
        super(components, accountRecord, request, response);
        this.indexesService = components.getIndexesService();
        this.indexName = indexName;
    }

    @Override
    protected String getTemplate() {
        return TEMPLATE;
    }

    public String delete() {
        final String indexName = request.getParameter("indexName");
        if (StringUtils.isBlank(indexName))
            return null;
        if (indexName.equals(this.indexName)) {
            indexesService.deleteIndex(accountRecord.id, indexName);
            addMessage(Message.Css.success, null, "Index \"" + indexName + "\" deleted");
            return StringUtils.EMPTY;
        }
        addMessage(Message.Css.warning, null, "Please confirm the name of the index to delete");
        return null;
    }

    @Override
    public void doGet() throws IOException, ServletException {
        request.setAttribute("indexName", indexName);
        final IndexService indexService = indexesService.getIndex(accountRecord.id, indexName);
        final IndexStatus status = indexService.getIndexStatus();
        request.setAttribute("indexSize", status.segmentsSize);
        request.setAttribute("indexCount", status.numDocs);
        request.setAttribute("crawlStatusCount", indexService.getCrawlStatusCount(null, null));
        request.setAttribute("indexStatusCount", indexService.getIndexStatusCount(null, null));
        super.doGet();
    }

}
