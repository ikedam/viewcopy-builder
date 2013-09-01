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
import java.util.Collections;
import java.util.List;

import org.junit.Test;

/**
 *
 */
public class ViewcopyBuilderNoJenkinsTest
{
    @Test
    public void testViewcopyBuilder()
    {
        {
            List<ViewcopyOperation> viewcopyOperationList = Arrays.<ViewcopyOperation>asList(
                    new SetRegexOperation("test-.*")
            );
            ViewcopyBuilder builder = new ViewcopyBuilder(
                    "FromView",
                    "ToView",
                    true,
                    viewcopyOperationList
            );
            
            assertEquals("FromView", builder.getFromViewName());
            assertEquals("ToView", builder.getToViewName());
            assertTrue(builder.isOverwrite());
            assertEquals(viewcopyOperationList, builder.getViewcopyOperationList());
        }
        {
            ViewcopyBuilder builder = new ViewcopyBuilder(
                    "   FromView   ",
                    " ToView ",
                    false,
                    Collections.<ViewcopyOperation>emptyList()
            );
            
            assertEquals("FromView", builder.getFromViewName());
            assertEquals("ToView", builder.getToViewName());
            assertFalse(builder.isOverwrite());
            assertEquals(Collections.emptyList(), builder.getViewcopyOperationList());
        }
        {
            ViewcopyBuilder builder = new ViewcopyBuilder(
                    null,
                    "  ",
                    true,
                    null
            );
            
            assertNull(builder.getFromViewName());
            assertEquals("", builder.getToViewName());
            assertTrue(builder.isOverwrite());
            assertNull(builder.getViewcopyOperationList());
        }
        {
            ViewcopyBuilder builder = new ViewcopyBuilder(
                    "  ",
                    null,
                    true,
                    null
            );
            
            assertEquals("", builder.getFromViewName());
            assertNull(builder.getToViewName());
            assertTrue(builder.isOverwrite());
            assertNull(builder.getViewcopyOperationList());
        }
    }
}
