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

import org.junit.Test;

/**
 *
 */
public class SetDescriptionOperationNoJenkinsTest
{
    @Test
    public void testSetDescriptionOperation()
    {
        {
            String description = "a\nb\nc";
            SetDescriptionOperation target = new SetDescriptionOperation(description);
            assertEquals(description, target.getDescription());
        }
        {
            String description = "  a\nb\nc  ";
            SetDescriptionOperation target = new SetDescriptionOperation(description);
            assertEquals("a\nb\nc", target.getDescription());
        }
        {
            String description = " \n \na\nb\nc \n \n \n";
            SetDescriptionOperation target = new SetDescriptionOperation(description);
            assertEquals("a\nb\nc", target.getDescription());
        }
        {
            String description = "  ";
            SetDescriptionOperation target = new SetDescriptionOperation(description);
            assertEquals("", target.getDescription());
        }
        {
            String description = "";
            SetDescriptionOperation target = new SetDescriptionOperation(description);
            assertEquals(description, target.getDescription());
        }
        {
            String description = null;
            SetDescriptionOperation target = new SetDescriptionOperation(description);
            assertNull(target.getDescription());
        }
    }
}
