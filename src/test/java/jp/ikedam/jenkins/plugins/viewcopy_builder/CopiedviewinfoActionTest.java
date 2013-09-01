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
import hudson.model.FreeStyleBuild;
import hudson.model.FreeStyleProject;
import hudson.model.ListView;

import org.junit.Rule;
import org.junit.Test;

/**
 *
 */
public class CopiedviewinfoActionTest
{
    @Rule
    public MyJenkinsRule j = new MyJenkinsRule();
    
    @Test
    public void testCopiedviewInfoAction() throws Exception
    {
        ListView srcView = new ListView("SrcView");
        String destViewName = "DestView";
        j.jenkins.addView(srcView);
        
        FreeStyleProject p = j.createFreeStyleProject();
        p.getBuildersList().add(new ViewcopyBuilder(
                srcView.getViewName(),
                destViewName,
                false,
                null
        ));
        
        FreeStyleBuild build = p.scheduleBuild2(0).get();
        j.assertBuildStatusSuccess(build);
        ListView destView = (ListView)j.jenkins.getView(destViewName);
        
        CopiedviewinfoAction action = build.getAction(CopiedviewinfoAction.class);
        assertNotNull(action);
        
        assertEquals(srcView.getUrl(), action.getFromUrl());
        assertEquals(srcView.getViewName(), action.getFromViewName());
        assertEquals(destView.getUrl(), action.getToUrl());
        assertEquals(destView.getViewName(), action.getToViewName());
    }
}
