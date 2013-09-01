/*
 * The MIT License
 * 
 * Copyright (c) 2013 IKEDA Yasuyuki
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package jp.ikedam.jenkins.plugins.viewcopy_builder;

import hudson.EnvVars;
import hudson.Extension;
import hudson.model.Descriptor;

import java.io.PrintStream;

import javax.xml.xpath.XPathExpressionException;

import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.DataBoundConstructor;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * Set the description of the view.
 *
 */
public class SetDescriptionOperation extends ViewcopyOperation
{
    private static final long serialVersionUID = -3090214188252961833L;
    
    /**
     * The internal class to work with views.
     * 
     * The following files are used (put in main/resource directory in the source tree).
     * <dl>
     *     <dt>config.jelly</dt>
     *         <dd>shown in the job configuration page, as an additional view to a Viewcopy build step.</dd>
     * </dl>
     */
    @Extension
    public static class DescriptorImpl extends Descriptor<ViewcopyOperation>
    {
        /**
         * Returns the string to be shown in a job configuration page,
         * in the dropdown of &quot;Add Copy Operation&quot;.
         * 
         * @return the display name
         * @see hudson.model.Descriptor#getDisplayName()
         */
        @Override
        public String getDisplayName()
        {
            return Messages.SetDescriptionOperation_DisplayName();
        }
    }
    
    private String description;
    
    /**
     * Returns the description to set. May contains variables.
     * 
     * @return the description
     */
    public String getDescription()
    {
        return description;
    }
    
    @DataBoundConstructor
    public SetDescriptionOperation(String description)
    {
        this.description = StringUtils.trim(description);
    }
    
    /**
     * @param doc
     * @param env
     * @param logger
     * @return
     * @see jp.ikedam.jenkins.plugins.viewcopy_builder.ViewcopyOperation#perform(org.w3c.dom.Document, hudson.EnvVars, java.io.PrintStream)
     */
    @Override
    public Document perform(Document doc, EnvVars env, PrintStream logger)
    {
        Node descNode;
        try
        {
            descNode = getNode(doc, "/*/description");
        }
        catch (XPathExpressionException e)
        {
            e.printStackTrace(logger);
            return null;
        }
        
        if(descNode == null)
        {
            // includeRegex is not exist.
            // create new one.
            descNode = doc.createElement("description");
            doc.getDocumentElement().appendChild(descNode);
        }
        
        String description = (getDescription() != null)?StringUtils.trim(env.expand(getDescription())):"";
        
        descNode.setTextContent(description);
        logger.println(String.format("Set description to:\n%s", description));
        
        return doc;
    }
}
