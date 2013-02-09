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

import java.io.Serializable;

import hudson.model.Action;
import hudson.model.View;

/**
 * Action holds the information of the views that the build copied from and to.
 * 
 * the information will be shown in the build's Summary page,
 * using summary.jelly.
 * 
 */
public class CopiedviewinfoAction implements Action, Serializable
{
    private static final long serialVersionUID = 5614855468029426720L;
    
    private String fromViewName;
    
    /**
     * Returns the name of the view copied from.
     * 
     * @return the name of the view copied from
     */
    public String getFromViewName()
    {
        return this.fromViewName;
    }
    
    public String fromUrl;
    
    /**
     * Returns the URI (path) of the view copied from.
     * 
     * This URI might be lost,
     * in the case that the view is removed or renamed.
     * 
     * @return the URI (path) of the view copied from.
     */
    public String getFromUrl()
    {
        return this.fromUrl;
    }
    
    public String toViewName;
    
    /**
     * Returns the name of the view copied to.
     * 
     * @return the name of the view copied to
     */
    public String getToViewName()
    {
        return this.toViewName;
    }
    
    public String toUrl;
    
    /**
     * Returns the URI (path) of the view copied to.
     * 
     * This URI might be lost,
     * in the case that the view is removed or renamed.
     * 
     * @return the URI (path) of the view copied to.
     */
    public String getToUrl()
    {
        return this.toUrl;
    }
    
    /**
     * 
     * constructor.
     * 
     * @param fromView  view that was copied from.
     * @param toView    view that was copied to.
     */
    public CopiedviewinfoAction(View fromView, View toView)
    {
        this.fromViewName = fromView.getViewName();
        this.fromUrl = fromView.getUrl();
        this.toViewName = toView.getViewName();
        this.toUrl = toView.getUrl();
    }
    
    /**
     * Returns null not for being displayed in the link list.
     * 
     * @return null
     * @see hudson.model.Action#getIconFileName()
     */
    @Override
    public String getIconFileName()
    {
       return null;
    }
    
    /**
     * Returns null not for being displayed in the link list.
     * 
     * @return null
     * @see hudson.model.Action#getUrlName()
     */
    @Override
    public String getUrlName()
    {
        return null;
    }
    
    /**
     * Returns the display name.
     * 
     * This will be never used, for not displayed in the link list.
     * 
     * @return the display name.
     * @see hudson.model.Action#getDisplayName()
     */
    @Override
    public String getDisplayName()
    {
        return Messages.CopiedviewinfoAction_DisplayName();
    }
}
