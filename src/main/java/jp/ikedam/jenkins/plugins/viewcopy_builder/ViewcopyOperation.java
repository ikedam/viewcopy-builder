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

import java.io.PrintStream;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;



import hudson.EnvVars;
import hudson.ExtensionPoint;
import hudson.DescriptorExtensionList;
import hudson.model.Descriptor;
import hudson.model.View;
import hudson.model.AbstractDescribableImpl;
import jenkins.model.Jenkins;

/**
 * Additional operations performed when "copy view" build step copies a view.
 * 
 * A new additional operation can be defined in following steps:
 * <ol>
 *    <li>Define a new class derived from ViewcopyOperation.</li>
 *    <li>Override {@link ViewcopyOperation#perform(Document, EnvVars, PrintStream)}</li>
 *    <li>Define the internal public static class named DescriptorImpl, derived from Descriptor&lt;ViewcopyOperation&gt;</li>
 *    <li>annotate the DescriptorImpl with Extension</li>
 * </ol>
 */
public abstract class ViewcopyOperation extends AbstractDescribableImpl<ViewcopyOperation> implements ExtensionPoint
{
    /**
     * Return modified XML document of the view configuration.
     * 
     * @param doc      the XML document of the view to be copied (a part of system config.xml)
     * @param env       Variables defined in the build.
     * @param logger    The output stream to log.
     * @return          modified XML string. Return null if an error occurs.
     */
    public abstract Document perform(Document doc, EnvVars env, PrintStream logger);
    
    /**
     * Return all the available ViewcopyOperation whose DescriptorImpl annotated with Extension.
     * 
     * @return
     */
    static public DescriptorExtensionList<ViewcopyOperation,Descriptor<ViewcopyOperation>> all()
    {
        return Jenkins.getInstance().<ViewcopyOperation,Descriptor<ViewcopyOperation>>getDescriptorList(ViewcopyOperation.class);
    }
    
    /****** Utility methods working with XML. Usable from subclasses. ******/
    
    /**
     * Retrieve a XML node using XPath.
     * 
     * Returns null in following cases:
     * <ul>
     *      <li>No node found.</li>
     *      <li>More than one node found.</li>
     * </ul>
     * 
     * @param doc       the XML Document object.
     * @param xpath     a XPath specifying the retrieving node.
     * @return          the retrieved node.
     * @throws XPathExpressionException
     */
    protected Node getNode(Document doc, String xpath)
        throws XPathExpressionException
    {
        NodeList nodeList = getNodeList(doc, xpath);
        
        if(nodeList.getLength() != 1)
        {
            return null;
        }
        
        return nodeList.item(0);
    }

    /**
     * Retrieve a XML node list using XPath.
     * 
     * @param doc               the XML Document object.
     * @param xpathExpression   a XPath specifying the retrieving nodes.
     * @return                  retrieved nodes in NodeList
     * @throws XPathExpressionException
     */
    protected NodeList getNodeList(Document doc, String xpathExpression)
        throws XPathExpressionException
    {
        XPathFactory factory = XPathFactory.newInstance();
        XPath xpath = factory.newXPath();
        XPathExpression expr = xpath.compile(xpathExpression);
        
        return (NodeList)expr.evaluate(doc, XPathConstants.NODESET);
    }
    
    /**
     * Retrieve a XPath expression of a node.
     * 
     * Use only for displaying purposes only.
     * For this works not so strict, 
     * the return value supposes not to work proper
     * with XPath processors.
     * 
     * @param targetNode  a node whose XPath expression is retrieved.
     * @return            XPath expression.
     */
    protected String getXpath(Node targetNode)
    {
        StringBuilder pathBuilder = new StringBuilder();
        for(Node node = targetNode; node != null && !(node instanceof Document); node = node.getParentNode())
        {
            if(node instanceof Text)
            {
                pathBuilder.insert(0, "text()");
                pathBuilder.insert(0, '/');
            }
            else
            {
                pathBuilder.insert(0, node.getNodeName());
                pathBuilder.insert(0, '/');
            }
        }
        return pathBuilder.toString();
    }
    
    /**
     * Specifies whether this operation can be applied to the View type.
     * 
     * @param viewType
     * @return
     */
    public boolean isApplicable(Class<? extends View> viewType)
    {
        return true;
    }
}

