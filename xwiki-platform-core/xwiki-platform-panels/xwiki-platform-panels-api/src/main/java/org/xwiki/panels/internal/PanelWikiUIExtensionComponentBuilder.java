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
package org.xwiki.panels.internal;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.xwiki.bridge.DocumentAccessBridge;
import org.xwiki.component.annotation.Component;
import org.xwiki.component.manager.ComponentLookupException;
import org.xwiki.component.manager.ComponentManager;
import org.xwiki.component.wiki.WikiComponent;
import org.xwiki.component.wiki.WikiComponentBuilder;
import org.xwiki.component.wiki.WikiComponentException;
import org.xwiki.context.Execution;
import org.xwiki.model.reference.DocumentReference;
import org.xwiki.query.Query;
import org.xwiki.query.QueryManager;
import org.xwiki.rendering.block.XDOM;
import org.xwiki.rendering.parser.ParseException;
import org.xwiki.rendering.parser.Parser;
import org.xwiki.rendering.syntax.Syntax;

import com.xpn.xwiki.XWikiContext;

/**
 * Allows to build {@link PanelWikiUIExtension} components.
 *
 * @version $Id$
 * @since 4.3M1
 */
@Component
@Singleton
@Named("panels")
public class PanelWikiUIExtensionComponentBuilder implements WikiComponentBuilder
{
    /**
     * The execution context.
     */
    @Inject
    private Execution execution;

    /**
     * The query manager, used to search for documents defining panels.
     */
    @Inject
    private QueryManager queryManager;

    /**
     * The component manager.
     */
    @Inject
    private ComponentManager componentManager;

    /**
     * Document access bridge.
     */
    @Inject
    private DocumentAccessBridge documentAccessBridge;

    @Override
    public List<DocumentReference> getDocumentReferences()
    {
        List<DocumentReference> references = new ArrayList<DocumentReference>();

        try {
            Query query =
                queryManager.createQuery("select doc.space, doc.name from Document doc, doc.object(Panels.PanelClass) "
                    + "as panel", Query.XWQL);
            List<Object[]> results = query.execute();
            for (Object[] result : results) {
                references.add(
                    new DocumentReference(getXWikiContext().getDatabase(), (String) result[0], (String) result[1]));
            }
        } catch (Exception e) {
            // Fail "silently"
            e.printStackTrace();
        }

        return references;
    }

    @Override
    public List<WikiComponent> buildComponents(DocumentReference reference) throws WikiComponentException
    {
        List<WikiComponent> components = new ArrayList<WikiComponent>();
        DocumentReference panelXClass = new DocumentReference(getXWikiContext().getDatabase(), "Panels", "PanelClass");
        String content = (String) documentAccessBridge.getProperty(reference, panelXClass, "content");
        Syntax syntax = null;

        try {
            syntax = documentAccessBridge.getDocument(reference).getSyntax();
        } catch (Exception e) {
            String.format("Failed to retrieve panel document [{}]", reference);
        }

        try {
            Parser parser = componentManager.getInstance(Parser.class, syntax.toIdString());

            try {
                XDOM xdom = parser.parse(new StringReader(content));
                components.add(new PanelWikiUIExtension(reference, xdom, syntax, componentManager));
            } catch (ParseException e) {
                throw new WikiComponentException(
                    String.format("Failed to find parse content of panel [{}]", reference));
            }
        } catch (ComponentLookupException e) {
            throw new WikiComponentException(String.format("Failed to find a parser for syntax [{}]", syntax));
        }

        return components;
    }

    /**
     * Utility method for accessing XWikiContext.
     *
     * @return the XWikiContext.
     */
    private XWikiContext getXWikiContext()
    {
        return (XWikiContext) this.execution.getContext().getProperty("xwikicontext");
    }
}