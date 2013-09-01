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

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Collection;

import javax.servlet.ServletException;

import hudson.model.Item;
import hudson.model.TopLevelItem;
import hudson.model.AllView;
import hudson.model.Descriptor.FormException;
import hudson.model.ListView;
import hudson.model.View;
import hudson.util.FormValidation;
import jp.ikedam.jenkins.plugins.viewcopy_builder.SetRegexOperation.DescriptorImpl;

import org.junit.Test;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;
import org.kohsuke.stapler.export.Exported;

/**
 *
 */
public class SetRegexOperationNoJenkinsTest
{
    private DescriptorImpl getDescriptor()
    {
        return new DescriptorImpl();
    }
    
    @Test
    public void testSetRegexOperation() throws Exception
    {
        {
            String regex = "test-.*";
            SetRegexOperation target = new SetRegexOperation(regex);
            assertEquals(regex, target.getRegex());
        }
        
        {
            String regex = "   test-.*    ";
            SetRegexOperation target = new SetRegexOperation(regex);
            assertEquals("test-.*", target.getRegex());
        }
        
        {
            String regex = "       ";
            SetRegexOperation target = new SetRegexOperation(regex);
            assertEquals("", target.getRegex());
        }
        
        {
            String regex = "";
            SetRegexOperation target = new SetRegexOperation(regex);
            assertEquals(regex, target.getRegex());
        }
        
        {
            String regex = null;
            SetRegexOperation target = new SetRegexOperation(regex);
            assertNull(target.getRegex());
        }
    }
    
    @Test
    public void testDescriptor_doCheckRegexOk() throws Exception
    {
        DescriptorImpl descriptor = getDescriptor();
        
        assertEquals(FormValidation.Kind.OK, descriptor.doCheckRegex(".*").kind);
        assertEquals(FormValidation.Kind.OK, descriptor.doCheckRegex("  .*  ").kind);
        assertEquals(FormValidation.Kind.OK, descriptor.doCheckRegex("${var}").kind);
    }
    
    @Test
    public void testDescriptor_doCheckRegexError() throws Exception
    {
        DescriptorImpl descriptor = getDescriptor();
        
        assertEquals(FormValidation.Kind.ERROR, descriptor.doCheckRegex(null).kind);
        assertEquals(FormValidation.Kind.ERROR, descriptor.doCheckRegex("").kind);
        assertEquals(FormValidation.Kind.ERROR, descriptor.doCheckRegex("  ").kind);
        assertEquals(FormValidation.Kind.ERROR, descriptor.doCheckRegex("*").kind);
        assertEquals(FormValidation.Kind.ERROR, descriptor.doCheckRegex(" *").kind);
    }
    
    @Test
    public void testIsApplicable()
    {
        SetRegexOperation target = new SetRegexOperation("dummy-.*");
        assertTrue(target.isApplicable(ListView.class));
        assertFalse(target.isApplicable(AllView.class));
        assertTrue(target.isApplicable(DerivedListView.class));
    }
    
    public static class DerivedListView extends ListView
    {
        public DerivedListView(String name)
        {
            super(name);
        }
    }
}
