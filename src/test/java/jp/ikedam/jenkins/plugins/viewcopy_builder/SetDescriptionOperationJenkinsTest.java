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

import org.junit.Rule;
import org.junit.Test;

/**
 *
 */
public class SetDescriptionOperationJenkinsTest
{
    @Rule
    public MyJenkinsRule j = new MyJenkinsRule();
    
    @Test
    public void testPerformAppend() throws Exception
    {
        String newDescription = "This\nis\na\n<test>";
        ListView srcView = new ListView("SrcView");
        String destViewName = "DestView";
        j.jenkins.addView(srcView);
        
        assertNull(srcView.getDescription());
        
        FreeStyleProject copier = j.createFreeStyleProject();
        copier.getBuildersList().add(new ViewcopyBuilder(
                srcView.getViewName(),
                destViewName,
                true,
                Arrays.<ViewcopyOperation>asList(
                        new SetDescriptionOperation(newDescription)
                )
        ));
        
        j.assertBuildStatusSuccess(copier.scheduleBuild2(0));
        ListView destView = (ListView)j.jenkins.getView(destViewName);
        assertNotNull(destView);
        assertEquals(newDescription, destView.getDescription());
    }
    
    @Test
    public void testPerformModify() throws Exception
    {
        String oldDescription = "hogehoge";
        String newDescription = "This\nis\na\n<test>";
        ListView src1View = new ListView("Src1View");
        String src2ViewName = "Src2View";
        String destViewName = "DestView";
        j.jenkins.addView(src1View);
        
        /* There is no way to set description to Views other than copying...*/
        FreeStyleProject copier1 = j.createFreeStyleProject();
        copier1.getBuildersList().add(new ViewcopyBuilder(
                src1View.getViewName(),
                src2ViewName,
                true,
                Arrays.<ViewcopyOperation>asList(
                        new SetDescriptionOperation(oldDescription)
                )
        ));
        
        j.assertBuildStatusSuccess(copier1.scheduleBuild2(0));
        ListView src2View = (ListView)j.jenkins.getView(src2ViewName);
        assertNotNull(src2View);
        assertEquals(oldDescription, src2View.getDescription());
        
        FreeStyleProject copier2 = j.createFreeStyleProject();
        copier2.getBuildersList().add(new ViewcopyBuilder(
                src2View.getViewName(),
                destViewName,
                true,
                Arrays.<ViewcopyOperation>asList(
                        new SetDescriptionOperation(newDescription)
                )
        ));
        
        j.assertBuildStatusSuccess(copier2.scheduleBuild2(0));
        ListView destView = (ListView)j.jenkins.getView(destViewName);
        assertNotNull(destView);
        assertEquals(newDescription, destView.getDescription());
    }
    
    @SuppressWarnings("deprecation")
    @Test
    public void testPerformWithVariables() throws Exception
    {
        String newDescription = "This\nis\na\n<test>";
        ListView srcView = new ListView("SrcView");
        String destViewName = "DestView";
        j.jenkins.addView(srcView);
        
        FreeStyleProject copier = j.createFreeStyleProject();
        copier.getBuildersList().add(new ViewcopyBuilder(
                srcView.getViewName(),
                destViewName,
                true,
                Arrays.<ViewcopyOperation>asList(
                        new SetDescriptionOperation("${var}")
                )
        ));
        
        j.assertBuildStatusSuccess(copier.scheduleBuild2(
                0,
                new Cause.UserCause(),
                new ParametersAction(
                        new StringParameterValue("var", newDescription)
                )
        ));
        ListView destView = (ListView)j.jenkins.getView(destViewName);
        assertNotNull(destView);
        assertEquals(newDescription, destView.getDescription());
    }
    
    @SuppressWarnings("deprecation")
    @Test
    public void testWorkForBlankValues() throws Exception
    {
        ListView srcView = new ListView("SrcView");
        String destViewName = "DestView";
        j.jenkins.addView(srcView);
        
        {
            FreeStyleProject copier = j.createFreeStyleProject();
            copier.getBuildersList().add(new ViewcopyBuilder(
                    srcView.getViewName(),
                    destViewName,
                    true,
                    Arrays.<ViewcopyOperation>asList(
                            new SetDescriptionOperation(null)
                    )
            ));
            j.assertBuildStatusSuccess(copier.scheduleBuild2(0));
            ListView destView = (ListView)j.jenkins.getView(destViewName);
            assertNotNull(destView);
            assertEquals("", destView.getDescription());
        }
        
        {
            FreeStyleProject copier = j.createFreeStyleProject();
            copier.getBuildersList().add(new ViewcopyBuilder(
                    srcView.getViewName(),
                    destViewName,
                    true,
                    Arrays.<ViewcopyOperation>asList(
                            new SetDescriptionOperation("")
                    )
            ));
            j.assertBuildStatusSuccess(copier.scheduleBuild2(0));
            ListView destView = (ListView)j.jenkins.getView(destViewName);
            assertNotNull(destView);
            assertEquals("", destView.getDescription());
        }
        
        {
            FreeStyleProject copier = j.createFreeStyleProject();
            copier.getBuildersList().add(new ViewcopyBuilder(
                    srcView.getViewName(),
                    destViewName,
                    true,
                    Arrays.<ViewcopyOperation>asList(
                            new SetDescriptionOperation("  ")
                    )
            ));
            j.assertBuildStatusSuccess(copier.scheduleBuild2(0));
            ListView destView = (ListView)j.jenkins.getView(destViewName);
            assertNotNull(destView);
            assertEquals("", destView.getDescription());
        }
        
        {
            FreeStyleProject copier = j.createFreeStyleProject();
            copier.getBuildersList().add(new ViewcopyBuilder(
                    srcView.getViewName(),
                    destViewName,
                    true,
                    Arrays.<ViewcopyOperation>asList(
                            new SetDescriptionOperation("${var}")
                    )
            ));
            
            j.assertBuildStatusSuccess(copier.scheduleBuild2(
                    0,
                    new Cause.UserCause(),
                    new ParametersAction(
                            new StringParameterValue("var", "   ")
                    )
            ));
            ListView destView = (ListView)j.jenkins.getView(destViewName);
            assertNotNull(destView);
            assertEquals("", destView.getDescription());
        }
    }
    
    @Test
    public void testDescriptor() throws Exception
    {
        assertNotNull(j.jenkins.getDescriptor(SetDescriptionOperation.class));
    }
}
