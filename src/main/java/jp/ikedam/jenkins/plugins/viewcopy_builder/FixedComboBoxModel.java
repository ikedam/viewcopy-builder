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

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;

import jenkins.model.Jenkins;

import net.sf.json.JSONArray;

import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;
import org.kohsuke.stapler.export.Flavor;

import hudson.util.ComboBoxModel;
import hudson.util.VersionNumber;

/**
 *
 */
public class FixedComboBoxModel extends ComboBoxModel
{
    private static final long serialVersionUID = -4067648513694996560L;
    
    public static ComboBoxModel createComboBoxModel()
    {
        if(Jenkins.getVersion().isOlderThan(new VersionNumber("1.494")))
        {
            return new FixedComboBoxModel();
        }
        
        return new ComboBoxModel();
    }
    
    /**
     * In Jenkins < 1.494, there is a problem that ComboBoxModel causes JavaScript error.
     * See https://github.com/jenkinsci/jenkins/commit/b6ce03878ed7523878ceffdb69e699c19a941bfc for details.
     * 
     * @param req
     * @param rsp
     * @param node
     * @throws IOException
     * @throws ServletException
     * @see hudson.util.ComboBoxModel#generateResponse(org.kohsuke.stapler.StaplerRequest, org.kohsuke.stapler.StaplerResponse, java.lang.Object)
     */
    @Override
    public void generateResponse(StaplerRequest req, StaplerResponse rsp, Object node)
            throws IOException, ServletException
    {
        rsp.setContentType(Flavor.JSON.contentType);
        PrintWriter w = rsp.getWriter();
        // w.print('(');
        JSONArray.fromObject(this).write(w);
        // w.print(')');
    }
}
