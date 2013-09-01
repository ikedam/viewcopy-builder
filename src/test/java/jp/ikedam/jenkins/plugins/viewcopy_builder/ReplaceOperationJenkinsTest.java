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
import hudson.model.Cause;
import hudson.model.FreeStyleProject;
import hudson.model.ListView;
import hudson.model.ParametersAction;
import hudson.model.StringParameterValue;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

/**
 *
 */
public class ReplaceOperationJenkinsTest
{
    @Rule
    public MyJenkinsRule j = new MyJenkinsRule();
    
    private ListView srcView;
    
    @Before
    public void setUp() throws Exception
    {
        ListView templateView = new ListView("TemplateView");
        j.jenkins.addView(templateView);
        
        /* There is no way to set properties to ListView other than copying...*/
        FreeStyleProject copier1 = j.createFreeStyleProject();
        copier1.getBuildersList().add(new ViewcopyBuilder(
                templateView.getViewName(),
                "SrcView",
                true,
                Arrays.<ViewcopyOperation>asList(
                        new SetRegexOperation("template-.*"),
                        new SetDescriptionOperation("This are a test message.")
                )
        ));
        
        copier1.scheduleBuild2(0).get();
        srcView = (ListView)j.jenkins.getView("SrcView");
    }
    
    @Test
    public void testReplace() throws Exception
    {
        String destViewName = "DestView";
        FreeStyleProject copier = j.createFreeStyleProject();
        copier.getBuildersList().add(new ViewcopyBuilder(
                srcView.getViewName(),
                destViewName,
                true,
                Arrays.<ViewcopyOperation>asList(
                        new ReplaceOperation("template-", false, "template2-", false),
                        new ReplaceOperation(" are ", false, " is ", false)
                )
        ));
        
        j.assertBuildStatusSuccess(copier.scheduleBuild2(0));
        
        ListView destView = (ListView)j.jenkins.getView(destViewName);
        assertEquals("template2-.*", destView.getIncludeRegex());
        assertEquals("This is a test message.", destView.getDescription());
    }
    
    @SuppressWarnings("deprecation")
    @Test
    public void testReplaceWithVariables() throws Exception
    {
        String destViewName = "DestView";
        FreeStyleProject copier = j.createFreeStyleProject();
        copier.getBuildersList().add(new ViewcopyBuilder(
                srcView.getViewName(),
                destViewName,
                true,
                Arrays.<ViewcopyOperation>asList(
                        new ReplaceOperation("${from}", true, "${to}", true)
                )
        ));
        
        j.assertBuildStatusSuccess(copier.scheduleBuild2(
                0,
                new Cause.UserCause(),
                new ParametersAction(
                        new StringParameterValue("from", "template-"),
                        new StringParameterValue("to", "template2-")
                )
        ));
        
        ListView destView = (ListView)j.jenkins.getView(destViewName);
        assertEquals("template2-.*", destView.getIncludeRegex());
        assertEquals("This are a test message.", destView.getDescription());
    }
    
    
    @SuppressWarnings("deprecation")
    @Test
    public void testExpansionForFromStr() throws Exception
    {
        String destViewName = "DestView";
        FreeStyleProject copier = j.createFreeStyleProject();
        copier.getBuildersList().add(new ViewcopyBuilder(
                srcView.getViewName(),
                destViewName,
                true,
                Arrays.<ViewcopyOperation>asList(
                        new ReplaceOperation("${from}", false, "${to}", true)
                )
        ));
        
        j.assertBuildStatusSuccess(copier.scheduleBuild2(
                0,
                new Cause.UserCause(),
                new ParametersAction(
                        new StringParameterValue("from", "template-"),
                        new StringParameterValue("to", "template2-")
                )
        ));
        
        ListView destView = (ListView)j.jenkins.getView(destViewName);
        assertEquals("template-.*", destView.getIncludeRegex());
        assertEquals("This are a test message.", destView.getDescription());
    }
    
    @SuppressWarnings("deprecation")
    @Test
    public void testExpansionForToStr() throws Exception
    {
        String destViewName = "DestView";
        FreeStyleProject copier = j.createFreeStyleProject();
        copier.getBuildersList().add(new ViewcopyBuilder(
                srcView.getViewName(),
                destViewName,
                true,
                Arrays.<ViewcopyOperation>asList(
                        new ReplaceOperation("${from}", true, "${to}", false)
                )
        ));
        
        j.assertBuildStatusSuccess(copier.scheduleBuild2(
                0,
                new Cause.UserCause(),
                new ParametersAction(
                        new StringParameterValue("from", " are "),
                        new StringParameterValue("to", " is ")
                )
        ));
        
        ListView destView = (ListView)j.jenkins.getView(destViewName);
        assertEquals("template-.*", destView.getIncludeRegex());
        assertEquals("This${to}a test message.", destView.getDescription());
    }
}
