/*******************************************************************************
 * Copyright (c) 2013 aegif.
 * 
 * This file is part of NemakiWare.
 * 
 * NemakiWare is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * NemakiWare is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with NemakiWare.
 * If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     linzhixing(https://github.com/linzhixing) - initial API and implementation
 ******************************************************************************/
package jp.aegif.nemaki.query.solr;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.aegif.nemaki.model.Content;
import jp.aegif.nemaki.query.QueryProcessor;
import jp.aegif.nemaki.repository.TypeManager;
import jp.aegif.nemaki.service.cmis.CompileObjectService;
import jp.aegif.nemaki.service.cmis.ExceptionService;
import jp.aegif.nemaki.service.cmis.PermissionService;
import jp.aegif.nemaki.service.node.ContentService;

import org.antlr.runtime.tree.Tree;
import org.apache.chemistry.opencmis.commons.data.ObjectList;
import org.apache.chemistry.opencmis.commons.definitions.TypeDefinition;
import org.apache.chemistry.opencmis.commons.enums.IncludeRelationships;
import org.apache.chemistry.opencmis.commons.server.CallContext;
import org.apache.chemistry.opencmis.server.support.query.CmisQueryWalker;
import org.apache.chemistry.opencmis.server.support.query.QueryObject;
import org.apache.chemistry.opencmis.server.support.query.QueryUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;

public class SolrQueryProcessor implements QueryProcessor {

	private ContentService contentService;
	private PermissionService permissionService;
	private CompileObjectService compileObjectService;
	private ExceptionService exceptionService;
	private SolrServer solrServer;
	private QueryObject queryObject;
	private static final Log log = LogFactory.getLog(SolrQueryProcessor.class);

	public SolrQueryProcessor() {
		solrServer = SolrUtil.getSolrServer();
	}

	public ObjectList query(TypeManager typeManager, CallContext callContext,
			String username, String id, String statement,
			Boolean searchAllVersions, Boolean includeAllowableActions,
			IncludeRelationships includeRelationships, String renditionFilter,
			BigInteger maxItems, BigInteger skipCount) {

		// queryObject includes the SQL information
		queryObject = new QueryObject(typeManager);
		QueryUtil util = new QueryUtil();
		CmisQueryWalker walker = null;

		// If statement is invalid, trhow exception
		walker = util
				.traverseStatementAndCatchExc(statement, queryObject, null);
		// "WHERE" clause to Lucene query
		String whereQueryString = "";
		Tree whereTree = walker.getWherePredicateTree();
		if (whereTree == null || whereTree.isNil()) {
			whereQueryString = "*:*";
		} else {
			SolrPredicateWalker solrPredicateWalker = new SolrPredicateWalker(
					queryObject);
			try{
				Query whereQuery = solrPredicateWalker.walkPredicate(whereTree);
				whereQueryString = whereQuery.toString();
			}catch(Exception e){
				e.printStackTrace();
				//TODO Output more detailed exception
				exceptionService.invalidArgument("Invalid CMIS SQL statement!");
			}
		}

		// "FROM" clause to Lucene query
		String fromQueryString = "";

		TypeDefinition td = queryObject.getMainFromName();
		String fromTable = td.getId();
		Term t = new Term("type", SolrUtil.getPropertyNameInSolr(fromTable));
		fromQueryString = new TermQuery(t).toString();

		// Execute query
		SolrQuery solrQuery = new SolrQuery();
		solrQuery.setQuery(whereQueryString);
		solrQuery.setFilterQueries(fromQueryString);

		QueryResponse resp = null;
		try {
			resp = solrServer.query(solrQuery);
		} catch (SolrServerException e) {
			e.printStackTrace();
		}

		// Output search results to ObjectList
		if (resp != null & resp.getResults() != null
				&& resp.getResults().getNumFound() != 0) {
			SolrDocumentList docs = resp.getResults();

			List<Content> contents = new ArrayList<Content>();
			for (SolrDocument doc : docs) {
				String docId = (String) doc.getFieldValue("id");
				Content c = contentService.getContentAsTheBaseType(docId);
				contents.add(c);
			}

			// Filter out by permissions
			List<Content> permitted = permissionService.getFiltered(callContext,
					contents);

			// Filter return value with SELECT clause
			Map<String, String>m = queryObject.getRequestedPropertiesByAlias();
			Map<String, String> aliases = new HashMap<String, String>();
			for(String alias : m.keySet()){
				aliases.put(m.get(alias), alias);
			}
			
			String filter = null;
			if(!aliases.keySet().contains("*")){
				filter = StringUtils.join(aliases.keySet(), ",");
			}
			
			return compileObjectService.compileObjectDataList(callContext, permitted, filter, includeAllowableActions, true, maxItems, skipCount);
		}else{
			return null;
		}
	}

	public void setContentService(ContentService contentService) {
		this.contentService = contentService;
	}

	public void setPermissionService(PermissionService permissionService) {
		this.permissionService = permissionService;
	}

	public void setCompileObjectService(
			CompileObjectService compileObjectService) {
		this.compileObjectService = compileObjectService;
	}

	public void setExceptionService(ExceptionService exceptionService) {
		this.exceptionService = exceptionService;
	}
}
