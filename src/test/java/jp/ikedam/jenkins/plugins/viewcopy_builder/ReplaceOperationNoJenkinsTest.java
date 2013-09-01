package jp.ikedam.jenkins.plugins.viewcopy_builder;

import static org.junit.Assert.*;
import hudson.util.FormValidation;
import jp.ikedam.jenkins.plugins.viewcopy_builder.ReplaceOperation.DescriptorImpl;

import org.junit.Test;

public class ReplaceOperationNoJenkinsTest
{
    private DescriptorImpl getDescriptor()
    {
        return new DescriptorImpl();
    }
    
    @Test
    public void testReplaceOperation()
    {
        {
            String fromString = "from";
            boolean isExpandFrom = false;
            String toString = "to";
            boolean isExpandTo = false;
            
            ReplaceOperation target = new ReplaceOperation(
                    fromString,
                    isExpandFrom,
                    toString,
                    isExpandTo
            );
            
            assertEquals(fromString, target.getFromStr());
            assertFalse(target.isExpandFromStr());
            assertEquals(toString, target.getToStr());
            assertFalse(target.isExpandToStr());
        }
        {
            String fromString = "from";
            boolean isExpandFrom = false;
            String toString = null;
            boolean isExpandTo = true;
            
            ReplaceOperation target = new ReplaceOperation(
                    fromString,
                    isExpandFrom,
                    toString,
                    isExpandTo
            );
            
            assertEquals(fromString, target.getFromStr());
            assertFalse(target.isExpandFromStr());
            assertNull(target.getToStr());
            assertTrue(target.isExpandToStr());
        }
        {
            String fromString = null;
            boolean isExpandFrom = true;
            String toString = "to";
            boolean isExpandTo = false;
            
            ReplaceOperation target = new ReplaceOperation(
                    fromString,
                    isExpandFrom,
                    toString,
                    isExpandTo
            );
            
            assertNull(target.getFromStr());
            assertTrue(target.isExpandFromStr());
            assertEquals(toString, target.getToStr());
            assertFalse(target.isExpandToStr());
        }
        {
            String fromString = "    ";
            boolean isExpandFrom = false;
            String toString = "  to  ";
            boolean isExpandTo = false;
            
            ReplaceOperation target = new ReplaceOperation(
                    fromString,
                    isExpandFrom,
                    toString,
                    isExpandTo
            );
            
            assertEquals(fromString, target.getFromStr());
            assertFalse(target.isExpandFromStr());
            assertEquals(toString, target.getToStr());
            assertFalse(target.isExpandToStr());
        }
    }
    
    public void testDescriptor_doCheckFromStrOk()
    {
        DescriptorImpl descriptor = getDescriptor();
        
        assertEquals(FormValidation.Kind.OK, descriptor.doCheckFromStr("somevalue", false));
    }
    
    public void testDescriptor_doCheckFromStrWarning()
    {
        DescriptorImpl descriptor = getDescriptor();
        
        assertEquals(FormValidation.Kind.WARNING, descriptor.doCheckFromStr("  somevalue  ", false));
        assertEquals(FormValidation.Kind.WARNING, descriptor.doCheckFromStr("    ", false));
    }
    public void testDescriptor_doCheckFromStrError()
    {
        DescriptorImpl descriptor = getDescriptor();
        
        assertEquals(FormValidation.Kind.ERROR, descriptor.doCheckFromStr(null, false));
        assertEquals(FormValidation.Kind.ERROR, descriptor.doCheckFromStr("", false));
    }
    
}
