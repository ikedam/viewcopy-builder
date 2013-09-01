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

import java.io.PrintStream;
import java.util.Arrays;
import java.util.Collections;

import jp.ikedam.jenkins.plugins.viewcopy_builder.ViewcopyBuilder.DescriptorImpl;

import hudson.EnvVars;
import hudson.model.Cause;
import hudson.model.FreeStyleProject;
import hudson.model.ListView;
import hudson.model.ParametersAction;
import hudson.model.StringParameterValue;
import hudson.model.Result;
import hudson.util.ComboBoxModel;
import hudson.util.FormValidation;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.w3c.dom.Document;

/**
 *
 */
public class ViewcopyBuilderJenkinsTest
{
    @Rule
    public MyJenkinsRule j = new MyJenkinsRule();
    
    @Before
    public void setUp() throws Exception
    {
        j.jenkins.addView(new ListView("SrcView"));
        j.jenkins.addView(new ListView("ExistingView"));
    }
    
    @Test
    public void testPerformSimple() throws Exception
    {
        FreeStyleProject p = j.createFreeStyleProject();
        ListView srcView = (ListView)j.jenkins.getView("SrcView");
        srcView.add(p);
        
        assertTrue(srcView.contains(p));
        
        FreeStyleProject copier = j.createFreeStyleProject();
        copier.getBuildersList().add(new ViewcopyBuilder(
                "SrcView",
                "DestView",
                false,
                Collections.<ViewcopyOperation>emptyList()
        ));
        copier.save();
        
        j.assertBuildStatusSuccess(copier.scheduleBuild2(0));
        
        ListView destView = (ListView)j.jenkins.getView("DestView");
        assertNotNull(destView);
        assertEquals("DestView", destView.getViewName());
        assertTrue(destView.contains(p));
    }
    
    
    @Test
    public void testPerformSimpleOverwrite() throws Exception
    {
        FreeStyleProject p = j.createFreeStyleProject();
        ListView srcView = (ListView)j.jenkins.getView("SrcView");
        srcView.add(p);
        
        assertTrue(srcView.contains(p));
        {
            ListView destView = (ListView)j.jenkins.getView("ExistingView");
            assertFalse(destView.contains(p));
        }
        FreeStyleProject copier = j.createFreeStyleProject();
        copier.getBuildersList().add(new ViewcopyBuilder(
                "SrcView",
                "ExistingView",
                true,
                Collections.<ViewcopyOperation>emptyList()
        ));
        copier.save();
        
        j.assertBuildStatusSuccess(copier.scheduleBuild2(0));
        
        ListView destView = (ListView)j.jenkins.getView("ExistingView");
        assertNotNull(destView);
        assertEquals("ExistingView", destView.getViewName());
        assertTrue(destView.contains(p));
    }
    
    @SuppressWarnings("deprecation")
    @Test
    public void testPerformSimpleWithVariables() throws Exception
    {
        FreeStyleProject p = j.createFreeStyleProject();
        ListView srcView = (ListView)j.jenkins.getView("SrcView");
        srcView.add(p);
        
        assertTrue(srcView.contains(p));
        
        FreeStyleProject copier = j.createFreeStyleProject();
        copier.getBuildersList().add(new ViewcopyBuilder(
                "${src}",
                "${dest}",
                false,
                Collections.<ViewcopyOperation>emptyList()
        ));
        copier.save();
        
        j.assertBuildStatusSuccess(copier.scheduleBuild2(
                0,
                new Cause.UserCause(),
                new ParametersAction(
                        new StringParameterValue("src", "SrcView"),
                        new StringParameterValue("dest", "DestView")
                )
        ));
        
        ListView destView = (ListView)j.jenkins.getView("DestView");
        assertNotNull(destView);
        assertEquals("DestView", destView.getViewName());
        assertTrue(destView.contains(p));
    }
    
    @Test
    public void testPerformWithOperations() throws Exception
    {
        FreeStyleProject p = j.createFreeStyleProject();
        ListView srcView = (ListView)j.jenkins.getView("SrcView");
        srcView.add(p);
        FreeStyleProject dummy = j.createFreeStyleProject("dummy-project");
        assertTrue(srcView.contains(p));
        assertFalse(srcView.contains(dummy));
        
        
        // operationList is null
        {
            assertNull(j.jenkins.getView("DestView"));
            FreeStyleProject copier = j.createFreeStyleProject();
            copier.getBuildersList().add(new ViewcopyBuilder(
                    "SrcView",
                    "DestView",
                    false,
                    null
            ));
            copier.save();
            
            j.assertBuildStatusSuccess(copier.scheduleBuild2(0));
            
            ListView destView = (ListView)j.jenkins.getView("DestView");
            assertNotNull(destView);
            assertTrue(destView.contains(p));
            j.jenkins.deleteView(destView);
        }
        
        // operationList is empty
        {
            assertNull(j.jenkins.getView("DestView"));
            FreeStyleProject copier = j.createFreeStyleProject();
            copier.getBuildersList().add(new ViewcopyBuilder(
                    "SrcView",
                    "DestView",
                    false,
                    Collections.<ViewcopyOperation>emptyList()
            ));
            copier.save();
            
            j.assertBuildStatusSuccess(copier.scheduleBuild2(0));
            
            ListView destView = (ListView)j.jenkins.getView("DestView");
            assertNotNull(destView);
            assertTrue(destView.contains(p));
            j.jenkins.deleteView(destView);
        }
        
        // operationList contains one operation
        {
            assertNull(j.jenkins.getView("DestView"));
            assertFalse(srcView.getItems().contains(dummy));
            FreeStyleProject copier = j.createFreeStyleProject();
            copier.getBuildersList().add(new ViewcopyBuilder(
                    "SrcView",
                    "DestView",
                    false,
                    Arrays.<ViewcopyOperation>asList(
                            new SetRegexOperation("dummy-.*")
                    )
            ));
            copier.save();
            
            j.assertBuildStatusSuccess(copier.scheduleBuild2(0));
            
            ListView destView = (ListView)j.jenkins.getView("DestView");
            assertNotNull(destView);
            assertTrue(destView.contains(p));
            assertTrue(destView.getItems().contains(dummy));
            j.jenkins.deleteView(destView);
        }
        
        
        // operationList contains two operation
        {
            assertNull(j.jenkins.getView("DestView"));
            FreeStyleProject copier = j.createFreeStyleProject();
            copier.getBuildersList().add(new ViewcopyBuilder(
                    "SrcView",
                    "DestView",
                    false,
                    Arrays.<ViewcopyOperation>asList(
                            new SetRegexOperation("dummy-.*"),
                            new SetDescriptionOperation("testtesttest")
                    )
            ));
            copier.save();
            
            j.assertBuildStatusSuccess(copier.scheduleBuild2(0));
            
            ListView destView = (ListView)j.jenkins.getView("DestView");
            assertNotNull(destView);
            assertTrue(destView.contains(p));
            assertTrue(destView.getItems().contains(dummy));
            assertEquals("testtesttest", destView.getDescription());
            j.jenkins.deleteView(destView);
        }
    }
    
    
    @Test
    public void testSelfCopy() throws Exception
    {
        ListView srcView = (ListView)j.jenkins.getView("SrcView");
        
        FreeStyleProject p = j.createFreeStyleProject("dummy-test");
        assertFalse(srcView.getItems().contains(p));
        
        FreeStyleProject copier = j.createFreeStyleProject();
        copier.getBuildersList().add(new ViewcopyBuilder(
                "SrcView",
                "SrcView",
                true,
                Arrays.<ViewcopyOperation>asList(
                        new SetRegexOperation("dummy-.*")
                )
        ));
        copier.save();
        
        j.assertBuildStatusSuccess(copier.scheduleBuild2(0));
        
        srcView = (ListView)j.jenkins.getView("SrcView");
        assertNotNull(srcView);
        assertTrue(srcView.getItems().contains(p));
    }
    
    @SuppressWarnings("deprecation")
    @Test
    public void testPerformFailureForConfiguration() throws Exception
    {
        // Verify works for correct configuration.
        {
            FreeStyleProject p = j.createFreeStyleProject();
            p.getBuildersList().add(new ViewcopyBuilder(
                    "SrcView",
                    "DestView",
                    true,
                    Collections.<ViewcopyOperation>emptyList()
            ));
            p.save();
            
            j.assertBuildStatusSuccess(p.scheduleBuild2(0));
            j.assertBuildStatusSuccess(p.scheduleBuild2(0)); // overwrite
        }
        
        
        // fromViewName is null
        {
            FreeStyleProject p = j.createFreeStyleProject();
            p.getBuildersList().add(new ViewcopyBuilder(
                    null,
                    "DestView",
                    true,
                    Collections.<ViewcopyOperation>emptyList()
            ));
            p.save();
            
            j.assertBuildStatus(Result.FAILURE, p.scheduleBuild2(0).get());
        }
        // fromViewName is empty
        {
            FreeStyleProject p = j.createFreeStyleProject();
            p.getBuildersList().add(new ViewcopyBuilder(
                    "",
                    "DestView",
                    true,
                    Collections.<ViewcopyOperation>emptyList()
            ));
            p.save();
            
            j.assertBuildStatus(Result.FAILURE, p.scheduleBuild2(0).get());
        }
        // fromViewName is blank
        {
            FreeStyleProject p = j.createFreeStyleProject();
            p.getBuildersList().add(new ViewcopyBuilder(
                    "  ",
                    "DestView",
                    true,
                    Collections.<ViewcopyOperation>emptyList()
            ));
            p.save();
            
            j.assertBuildStatus(Result.FAILURE, p.scheduleBuild2(0).get());
        }
        
        
        // toViewName is null
        {
            FreeStyleProject p = j.createFreeStyleProject();
            p.getBuildersList().add(new ViewcopyBuilder(
                    "SrcView",
                    null,
                    true,
                    Collections.<ViewcopyOperation>emptyList()
            ));
            p.save();
            
            j.assertBuildStatus(Result.FAILURE, p.scheduleBuild2(0).get());
        }
        // toViewName is empty
        {
            FreeStyleProject p = j.createFreeStyleProject();
            p.getBuildersList().add(new ViewcopyBuilder(
                    "SrcView",
                    "",
                    true,
                    Collections.<ViewcopyOperation>emptyList()
            ));
            p.save();
            
            j.assertBuildStatus(Result.FAILURE, p.scheduleBuild2(0).get());
        }
        // toViewName is blank
        {
            FreeStyleProject p = j.createFreeStyleProject();
            p.getBuildersList().add(new ViewcopyBuilder(
                    "SrcView",
                    "  ",
                    true,
                    Collections.<ViewcopyOperation>emptyList()
            ));
            p.save();
            
            j.assertBuildStatus(Result.FAILURE, p.scheduleBuild2(0).get());
        }
        
        
        // fromViewName is empty
        {
            FreeStyleProject p = j.createFreeStyleProject();
            p.getBuildersList().add(new ViewcopyBuilder(
                    "${var}",
                    "DestView",
                    true,
                    Collections.<ViewcopyOperation>emptyList()
            ));
            p.save();
            
            j.assertBuildStatus(Result.FAILURE, p.scheduleBuild2(
                    0,
                    new Cause.UserCause(),
                    new ParametersAction(
                            new StringParameterValue("var", "")
                    )
            ).get());
        }
        // fromViewName is blank
        {
            FreeStyleProject p = j.createFreeStyleProject();
            p.getBuildersList().add(new ViewcopyBuilder(
                    "${var}",
                    "DestView",
                    true,
                    Collections.<ViewcopyOperation>emptyList()
            ));
            p.save();
            
            j.assertBuildStatus(Result.FAILURE, p.scheduleBuild2(
                    0,
                    new Cause.UserCause(),
                    new ParametersAction(
                            new StringParameterValue("var", "   ")
                    )
            ).get());
        }
        
        // toViewName is empty
        {
            FreeStyleProject p = j.createFreeStyleProject();
            p.getBuildersList().add(new ViewcopyBuilder(
                    "SrcView",
                    "${var}",
                    true,
                    Collections.<ViewcopyOperation>emptyList()
            ));
            p.save();
            
            j.assertBuildStatus(Result.FAILURE, p.scheduleBuild2(
                    0,
                    new Cause.UserCause(),
                    new ParametersAction(
                            new StringParameterValue("var", "")
                    )
            ).get());
        }
        // toViewName is blank
        {
            FreeStyleProject p = j.createFreeStyleProject();
            p.getBuildersList().add(new ViewcopyBuilder(
                    "SrcView",
                    "${var}",
                    true,
                    Collections.<ViewcopyOperation>emptyList()
            ));
            p.save();
            
            j.assertBuildStatus(Result.FAILURE, p.scheduleBuild2(
                    0,
                    new Cause.UserCause(),
                    new ParametersAction(
                            new StringParameterValue("var", "   ")
                    )
            ).get());
        }
        
        // fromViewName is not exists.
        {
            FreeStyleProject p = j.createFreeStyleProject();
            p.getBuildersList().add(new ViewcopyBuilder(
                    "NoSuchView",
                    "DestView",
                    true,
                    Collections.<ViewcopyOperation>emptyList()
            ));
            p.save();
            
            j.assertBuildStatus(Result.FAILURE, p.scheduleBuild2(0).get());
        }
        
        
        // toViewName is already exists.
        {
            FreeStyleProject p = j.createFreeStyleProject();
            p.getBuildersList().add(new ViewcopyBuilder(
                    "SrcView",
                    "ExistingView",
                    false,
                    Collections.<ViewcopyOperation>emptyList()
            ));
            p.save();
            
            j.assertBuildStatus(Result.FAILURE, p.scheduleBuild2(0).get());
        }
    }
    
    public static class NonapplicableViewcopyOperation extends ViewcopyOperation
    {
        private static final long serialVersionUID = -947480713290315774L;
        
        @Override
        public Document perform(Document doc, EnvVars env,
                PrintStream logger)
        {
            return doc;
        }
        
        public boolean isApplicable(java.lang.Class<? extends hudson.model.View> viewType)
        {
            return false;
        };
    }
    
    @Test
    public void testPerformFailureForNonapplicable() throws Exception
    {
        FreeStyleProject p = j.createFreeStyleProject();
        p.getBuildersList().add(new ViewcopyBuilder(
                "SrcView",
                "DestView",
                true,
                Arrays.<ViewcopyOperation>asList(
                        new NonapplicableViewcopyOperation()
                )
        ));
        p.save();
        
        j.assertBuildStatus(Result.FAILURE, p.scheduleBuild2(0).get());
        assertNull(j.jenkins.getView("DestView"));
    }
    
    
    public static class FailureViewcopyOperation extends ViewcopyOperation
    {
        private static final long serialVersionUID = 7667173556518546609L;
        
        @Override
        public Document perform(Document doc, EnvVars env,
                PrintStream logger)
        {
            return null;
        }
    }
    
    @Test
    public void testPerformFailureForOperation() throws Exception
    {
        FreeStyleProject p = j.createFreeStyleProject();
        p.getBuildersList().add(new ViewcopyBuilder(
                "SrcView",
                "DestView",
                true,
                Arrays.<ViewcopyOperation>asList(
                        new FailureViewcopyOperation()
                )
        ));
        p.save();
        
        j.assertBuildStatus(Result.FAILURE, p.scheduleBuild2(0).get());
        assertNull(j.jenkins.getView("DestView"));
    }
    
    private DescriptorImpl getDescriptor()
    {
        return (DescriptorImpl)j.jenkins.getDescriptor(ViewcopyBuilder.class);
    }
    
    @Test
    public void testDescriptor_doFillFromViewNameItems() throws Exception
    {
        DescriptorImpl descriptor = getDescriptor();
        
        ComboBoxModel c = descriptor.doFillFromViewNameItems();
        assertEquals(2, c.size());
        assertTrue(c.contains("SrcView"));
        assertTrue(c.contains("ExistingView"));
    }
    
    @Test
    public void testDescriptor_doCheckFromViewNameOk() throws Exception
    {
        DescriptorImpl descriptor = getDescriptor();
        
        assertEquals(FormValidation.Kind.OK, descriptor.doCheckFromViewName("${var}").kind);
        assertEquals(FormValidation.Kind.OK, descriptor.doCheckFromViewName("  $var  ").kind);
        assertEquals(FormValidation.Kind.OK, descriptor.doCheckFromViewName("SrcView").kind);
        assertEquals(FormValidation.Kind.OK, descriptor.doCheckFromViewName("  SrcView  ").kind);
    }
    
    @Test
    public void testDescriptor_doCheckFromViewNameWarning() throws Exception
    {
        DescriptorImpl descriptor = getDescriptor();
        
        assertEquals(FormValidation.Kind.WARNING, descriptor.doCheckFromViewName("NoSuchView").kind);
    }
    
    @Test
    public void testDescriptor_doCheckFromViewNameError() throws Exception
    {
        DescriptorImpl descriptor = getDescriptor();
        
        assertEquals(FormValidation.Kind.ERROR, descriptor.doCheckFromViewName(null).kind);
        assertEquals(FormValidation.Kind.ERROR, descriptor.doCheckFromViewName("").kind);
        assertEquals(FormValidation.Kind.ERROR, descriptor.doCheckFromViewName("   ").kind);
    }
    
    @Test
    public void testDescriptor_doCheckToViewNameOk() throws Exception
    {
        DescriptorImpl descriptor = getDescriptor();
        
        assertEquals(FormValidation.Kind.OK, descriptor.doCheckToViewName("${var}", false).kind);
        assertEquals(FormValidation.Kind.OK, descriptor.doCheckToViewName("  $var  ", false).kind);
        assertEquals(FormValidation.Kind.OK, descriptor.doCheckToViewName("NoSuchView", false).kind);
        assertEquals(FormValidation.Kind.OK, descriptor.doCheckToViewName("  NoSuchView  ", false).kind);
        
        assertEquals(FormValidation.Kind.OK, descriptor.doCheckToViewName("${var}", true).kind);
        assertEquals(FormValidation.Kind.OK, descriptor.doCheckToViewName("  $var  ", true).kind);
        assertEquals(FormValidation.Kind.OK, descriptor.doCheckToViewName("NoSuchView", true).kind);
        assertEquals(FormValidation.Kind.OK, descriptor.doCheckToViewName("  NoSuchView  ", true).kind);
        assertEquals(FormValidation.Kind.OK, descriptor.doCheckToViewName("ExistingView", true).kind);
    }
    
    @Test
    public void testDescriptor_doCheckToViewNameWarning() throws Exception
    {
        DescriptorImpl descriptor = getDescriptor();
        
        assertEquals(FormValidation.Kind.WARNING, descriptor.doCheckToViewName("ExistingView", false).kind);
    }
    
    @Test
    public void testDescriptor_doCheckToViewNameError() throws Exception
    {
        DescriptorImpl descriptor = getDescriptor();
        
        assertEquals(FormValidation.Kind.ERROR, descriptor.doCheckToViewName(null, false).kind);
        assertEquals(FormValidation.Kind.ERROR, descriptor.doCheckToViewName("", false).kind);
        assertEquals(FormValidation.Kind.ERROR, descriptor.doCheckToViewName("   ", false).kind);
        
        assertEquals(FormValidation.Kind.ERROR, descriptor.doCheckToViewName(null, true).kind);
        assertEquals(FormValidation.Kind.ERROR, descriptor.doCheckToViewName("", true).kind);
        assertEquals(FormValidation.Kind.ERROR, descriptor.doCheckToViewName("   ", true).kind);
    }
}
