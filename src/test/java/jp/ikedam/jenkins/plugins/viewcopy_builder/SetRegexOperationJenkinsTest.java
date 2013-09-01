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

import java.util.Arrays;

import hudson.model.Cause;
import hudson.model.FreeStyleProject;
import hudson.model.ListView;
import hudson.model.ParametersAction;
import hudson.model.StringParameterValue;
import hudson.model.Result;

import org.junit.Rule;
import org.junit.Test;

/**
 *
 */
public class SetRegexOperationJenkinsTest
{
    @Rule
    public MyJenkinsRule j = new MyJenkinsRule();
    
    @Test
    public void testPerformAppend() throws Exception
    {
        FreeStyleProject dummyProject = j.createFreeStyleProject("dummy-test1");
        ListView srcView = new ListView("SrcView");
        String destViewName = "DestView";
        j.jenkins.addView(srcView);
        
        assertNull(srcView.getIncludeRegex());
        assertFalse(srcView.getItems().contains(dummyProject));
        
        FreeStyleProject copier = j.createFreeStyleProject();
        copier.getBuildersList().add(new ViewcopyBuilder(
                srcView.getViewName(),
                destViewName,
                true,
                Arrays.<ViewcopyOperation>asList(
                        new SetRegexOperation("dummy-.*")
                )
        ));
        
        j.assertBuildStatusSuccess(copier.scheduleBuild2(0));
        assertNotNull(j.jenkins.getView(destViewName));
        assertTrue(j.jenkins.getView(destViewName).getItems().contains(dummyProject));
    }
    
    @Test
    public void testPerformModify() throws Exception
    {
        FreeStyleProject dummyProject = j.createFreeStyleProject("dummy-test1");
        FreeStyleProject anotherProject = j.createFreeStyleProject("another-test2");
        ListView src1View = new ListView("Src1View");
        String src2ViewName = "Src2View";
        String destViewName = "DestView";
        j.jenkins.addView(src1View);
        
        /* There is no way to set regex to ListView other than copying...*/
        FreeStyleProject copier1 = j.createFreeStyleProject();
        copier1.getBuildersList().add(new ViewcopyBuilder(
                src1View.getViewName(),
                src2ViewName,
                true,
                Arrays.<ViewcopyOperation>asList(
                        new SetRegexOperation("dummy-.*")
                )
        ));
        
        j.assertBuildStatusSuccess(copier1.scheduleBuild2(0));
        ListView src2View = (ListView)j.jenkins.getView(src2ViewName);
        assertNotNull(src2View);
        assertTrue(src2View.getItems().contains(dummyProject));
        assertFalse(src2View.getItems().contains(anotherProject));
        assertNotNull(src2View.getIncludeRegex());
        
        FreeStyleProject copier2 = j.createFreeStyleProject();
        copier2.getBuildersList().add(new ViewcopyBuilder(
                src2View.getViewName(),
                destViewName,
                true,
                Arrays.<ViewcopyOperation>asList(
                        new SetRegexOperation("another-.*")
                )
        ));
        
        j.assertBuildStatusSuccess(copier2.scheduleBuild2(0));
        ListView destView = (ListView)j.jenkins.getView(destViewName);
        assertNotNull(destView);
        assertFalse(destView.getItems().contains(dummyProject));
        assertTrue(destView.getItems().contains(anotherProject));
    }
    
    @SuppressWarnings("deprecation")
    @Test
    public void testPerformWithVariables() throws Exception
    {
        FreeStyleProject dummyProject = j.createFreeStyleProject("dummy-test1");
        ListView srcView = new ListView("SrcView");
        String destViewName = "DestView";
        j.jenkins.addView(srcView);
        
        assertFalse(srcView.getItems().contains(dummyProject));
        
        FreeStyleProject copier = j.createFreeStyleProject();
        copier.getBuildersList().add(new ViewcopyBuilder(
                srcView.getViewName(),
                destViewName,
                true,
                Arrays.<ViewcopyOperation>asList(
                        new SetRegexOperation("${var}")
                )
        ));
        
        j.assertBuildStatusSuccess(copier.scheduleBuild2(
                0,
                new Cause.UserCause(),
                new ParametersAction(
                        new StringParameterValue("var", "dummy-.*")
                )
        ));
        assertNotNull(j.jenkins.getView(destViewName));
        assertTrue(j.jenkins.getView(destViewName).getItems().contains(dummyProject));
    }
    
    @SuppressWarnings("deprecation")
    @Test
    public void testPerformFailureForConfiguration() throws Exception
    {
        FreeStyleProject dummyProject = j.createFreeStyleProject("dummy-test1");
        ListView srcView = new ListView("SrcView");
        String destViewName = "DestView";
        j.jenkins.addView(srcView);
        
        // test succeeds with a proper parameters.
        {
            FreeStyleProject copier = j.createFreeStyleProject();
            copier.getBuildersList().add(new ViewcopyBuilder(
                    srcView.getViewName(),
                    destViewName,
                    true,
                    Arrays.<ViewcopyOperation>asList(
                            new SetRegexOperation("dummy-.*")
                    )
            ));
            
            j.assertBuildStatusSuccess(copier.scheduleBuild2(0));
            assertNotNull(j.jenkins.getView(destViewName));
            assertTrue(j.jenkins.getView(destViewName).getItems().contains(dummyProject));
            j.assertBuildStatusSuccess(copier.scheduleBuild2(0));
            
            j.jenkins.deleteView(j.jenkins.getView(destViewName));
        }
        
        {
            FreeStyleProject copier = j.createFreeStyleProject();
            copier.getBuildersList().add(new ViewcopyBuilder(
                    srcView.getViewName(),
                    destViewName,
                    true,
                    Arrays.<ViewcopyOperation>asList(
                            new SetRegexOperation(null)
                    )
            ));
            
            j.assertBuildStatus(Result.FAILURE, copier.scheduleBuild2(0).get());
            assertNull(j.jenkins.getView(destViewName));
        }
        
        {
            FreeStyleProject copier = j.createFreeStyleProject();
            copier.getBuildersList().add(new ViewcopyBuilder(
                    srcView.getViewName(),
                    destViewName,
                    true,
                    Arrays.<ViewcopyOperation>asList(
                            new SetRegexOperation("")
                    )
            ));
            
            j.assertBuildStatus(Result.FAILURE, copier.scheduleBuild2(0).get());
            assertNull(j.jenkins.getView(destViewName));
        }
        
        {
            FreeStyleProject copier = j.createFreeStyleProject();
            copier.getBuildersList().add(new ViewcopyBuilder(
                    srcView.getViewName(),
                    destViewName,
                    true,
                    Arrays.<ViewcopyOperation>asList(
                            new SetRegexOperation("  ")
                    )
            ));
            
            j.assertBuildStatus(Result.FAILURE, copier.scheduleBuild2(0).get());
            assertNull(j.jenkins.getView(destViewName));
        }
        
        {
            FreeStyleProject copier = j.createFreeStyleProject();
            copier.getBuildersList().add(new ViewcopyBuilder(
                    srcView.getViewName(),
                    destViewName,
                    true,
                    Arrays.<ViewcopyOperation>asList(
                            new SetRegexOperation("*")
                    )
            ));
            
            j.assertBuildStatus(Result.FAILURE, copier.scheduleBuild2(0).get());
            assertNull(j.jenkins.getView(destViewName));
        }
        
        {
            FreeStyleProject copier = j.createFreeStyleProject();
            copier.getBuildersList().add(new ViewcopyBuilder(
                    srcView.getViewName(),
                    destViewName,
                    true,
                    Arrays.<ViewcopyOperation>asList(
                            new SetRegexOperation("${var}")
                    )
            ));
            
            j.assertBuildStatus(Result.FAILURE, copier.scheduleBuild2(
                    0,
                    new Cause.UserCause(),
                    new ParametersAction(new StringParameterValue("var", ""))
            ).get());
            assertNull(j.jenkins.getView(destViewName));
        }
        
        {
            FreeStyleProject copier = j.createFreeStyleProject();
            copier.getBuildersList().add(new ViewcopyBuilder(
                    srcView.getViewName(),
                    destViewName,
                    true,
                    Arrays.<ViewcopyOperation>asList(
                            new SetRegexOperation("${var}")
                    )
            ));
            
            j.assertBuildStatus(Result.FAILURE, copier.scheduleBuild2(
                    0,
                    new Cause.UserCause(),
                    new ParametersAction(new StringParameterValue("var", "  "))
            ).get());
            assertNull(j.jenkins.getView(destViewName));
        }
    }
    
    @Test
    public void testDescriptor() throws Exception
    {
        assertNotNull(j.jenkins.getDescriptor(SetRegexOperation.class));
    }
}
