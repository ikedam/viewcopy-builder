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
import hudson.model.ListView;
import hudson.model.View;
import hudson.util.FormValidation;

import java.io.PrintStream;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.xml.xpath.XPathExpressionException;

import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * Set the regular expression of the list view.
 *
 */
public class SetRegexOperation extends ViewcopyOperation
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
            return Messages.SetRegexOperation_DisplayName();
        }
        
        /**
         * Validate the regular expression
         * 
         * @param regexp
         * @return FormValidation object.
         */
        public FormValidation doCheckRegex(@QueryParameter String regex)
        {
            if(StringUtils.isBlank(regex))
            {
                return FormValidation.error(Messages.SetRegexOperation_regex_empty());
            }
            
            regex = StringUtils.trim(regex);
            
            if(regex.contains("$"))
            {
                // If variable is used, skip the validation.
                return FormValidation.ok();
            }
            
            try {
                Pattern.compile(regex);
            } catch (PatternSyntaxException e) {
                return FormValidation.error(Messages.SetRegexOperation_regex_invalid(e.getMessage()));
            }
            
            return FormValidation.ok();
        }
    }
    
    private String regex;
    
    /**
     * Returns regular expression to set. May contains variables.
     * 
     * @return the regex
     */
    public String getRegex()
    {
        return regex;
    }
    
    @DataBoundConstructor
    public SetRegexOperation(String regex)
    {
        this.regex = StringUtils.trim(regex);
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
        if(StringUtils.isEmpty(getRegex()))
        {
            logger.println("Regular expression is not specified.");
            return null;
        }
        
        String expandedRegex = StringUtils.trim(env.expand(getRegex()));
        if(StringUtils.isEmpty(expandedRegex))
        {
            logger.println("Regular expression got to empty.");
            return null;
        }
        
        try
        {
            Pattern.compile(expandedRegex);
        }
        catch(PatternSyntaxException e)
        {
            e.printStackTrace(logger);
            return null;
        }
        
        Node regexNode;
        try
        {
            regexNode = getNode(doc, "/*/includeRegex");
        }
        catch (XPathExpressionException e)
        {
            e.printStackTrace(logger);
            return null;
        }
        
        if(regexNode == null)
        {
            // includeRegex is not exist.
            // create new one.
            regexNode = doc.createElement("includeRegex");
            doc.getDocumentElement().appendChild(regexNode);
        }
        
        regexNode.setTextContent(expandedRegex);
        logger.println(String.format("Set includeRegex to %s", expandedRegex));
        
        return doc;
    }
    
    /**
     * Return true if the view is a instance of ListView.
     * 
     * @param viewType
     * @return
     * @see jp.ikedam.jenkins.plugins.viewcopy_builder.ViewcopyOperation#isApplicable(java.lang.Class)
     */
    @Override
    public boolean isApplicable(Class<? extends View> viewType)
    {
        return ListView.class.isAssignableFrom(viewType);
    }
}
