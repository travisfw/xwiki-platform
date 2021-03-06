/*
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.xwiki.rest.internal.resources.wikis;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.UriBuilder;

import org.xwiki.component.annotation.Component;
import org.xwiki.query.QueryException;
import org.xwiki.rest.internal.Utils;
import org.xwiki.rest.internal.resources.BaseSearchResult;
import org.xwiki.rest.model.jaxb.SearchResults;

import com.xpn.xwiki.XWikiException;

@Component("org.xwiki.rest.internal.resources.wikis.WikiSearchQueryResource")
@Path("/wikis/{wikiName}/query")
public class WikiSearchQueryResource extends BaseSearchResult
{
    @GET
    public SearchResults search(@PathParam("wikiName") String wikiName, @QueryParam("q") String query,
        @QueryParam("type") String queryTypeString, @QueryParam("number") @DefaultValue("-1") Integer number,
        @QueryParam("start") @DefaultValue("0") Integer start,
        @QueryParam("distinct") @DefaultValue("true") Boolean distinct,
        @QueryParam("orderfield") @DefaultValue("") String orderField,
        @QueryParam("order") @DefaultValue("asc") String order,
        @QueryParam("prettynames") @DefaultValue("false") Boolean withPrettyNames,
        @QueryParam("classname") @DefaultValue("") String className) throws QueryException, XWikiException
    {
        SearchResults searchResults = objectFactory.createSearchResults();
        searchResults.setTemplate(String.format("%s?%s",
            UriBuilder.fromUri(uriInfo.getBaseUri()).path(WikiSearchQueryResource.class).build(wikiName).toString(),
            QUERY_TEMPLATE_INFO));

        searchResults.getSearchResults().addAll(
            searchQuery(query, queryTypeString, wikiName, null, Utils.getXWiki(componentManager).getRightService()
                .hasProgrammingRights(Utils.getXWikiContext(componentManager)), orderField, order, distinct, number,
                start, withPrettyNames, className));

        return searchResults;
    }
}
