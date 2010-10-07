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
package org.xwiki.rendering.internal.parser;

import org.wikimodel.wem.IWikiParser;
import org.wikimodel.wem.mediawiki.MediaWikiParser;
import org.xwiki.component.annotation.Component;
import org.xwiki.component.annotation.Requirement;
import org.xwiki.rendering.parser.ImageParser;
import org.xwiki.rendering.parser.ResourceReferenceParser;
import org.xwiki.rendering.syntax.Syntax;
import org.xwiki.rendering.internal.parser.wikimodel.AbstractWikiModelParser;

/**
 * @version $Id$
 * @since 1.5M2
 */
@Component("mediawiki/1.0")
public class WikiModelMediaWikiParser extends AbstractWikiModelParser
{
    /**
     * @see #getResourceReferenceParser()
     */
    @Requirement
    private ResourceReferenceParser referenceParser;

    /**
     * @see #getImageParser()
     */
    @Requirement
    private ImageParser imageParser;

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.rendering.parser.Parser#getSyntax()
     */
    public Syntax getSyntax()
    {
        return Syntax.MEDIAWIKI_1_0;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.rendering.internal.parser.wikimodel.AbstractWikiModelParser#createWikiModelParser()
     */
    @Override
    public IWikiParser createWikiModelParser()
    {
        return new MediaWikiParser();
    }
    
    /**
     * {@inheritDoc}
     * @see AbstractWikiModelParser#getImageParser()
     */
    @Override
    public ImageParser getImageParser()
    {
        return this.imageParser;
    }

    /**
     * {@inheritDoc}
     * @see AbstractWikiModelParser#getResourceReferenceParser()
     */
    @Override
    public ResourceReferenceParser getResourceReferenceParser()
    {
        return this.referenceParser;
    }
}
