package cz.quantumleap.core.web.template;

import cz.quantumleap.core.web.template.ResizableImageTagProcessor.ResizeCrop;
import cz.quantumleap.core.web.template.ResizableImageTagProcessor.ResizeLimit;
import org.junit.Assert;
import org.junit.Test;

public class ResizableImageTagProcessorTest {

    @Test
    public void resizeLimitTest() {
        ResizeLimit resizeLimit = new ResizeLimit(100, 50, 50, 50);
        Assert.assertEquals(50, resizeLimit.getCalculatedWidth());
        Assert.assertEquals(25, resizeLimit.getCalculatedHeight());
        resizeLimit = new ResizeLimit(50, 100, 50, 50);
        Assert.assertEquals(25, resizeLimit.getCalculatedWidth());
        Assert.assertEquals(50, resizeLimit.getCalculatedHeight());
        resizeLimit = new ResizeLimit(10, 5, 50, 50);
        Assert.assertEquals(50, resizeLimit.getCalculatedWidth());
        Assert.assertEquals(25, resizeLimit.getCalculatedHeight());
        resizeLimit = new ResizeLimit(5, 10, 50, 50);
        Assert.assertEquals(25, resizeLimit.getCalculatedWidth());
        Assert.assertEquals(50, resizeLimit.getCalculatedHeight());
        resizeLimit = new ResizeLimit(100, 10, 50, 50);
        Assert.assertEquals(50, resizeLimit.getCalculatedWidth());
        Assert.assertEquals(5, resizeLimit.getCalculatedHeight());
    }

    @Test
    public void resizeCropTest() {
        ResizeCrop resizeCrop = new ResizeCrop(100, 50, 50, 50);
        Assert.assertEquals(25, resizeCrop.getX());
        Assert.assertEquals(0, resizeCrop.getY());
        Assert.assertEquals(50, resizeCrop.getCropWidth());
        Assert.assertEquals(50, resizeCrop.getCropHeight());
        resizeCrop = new ResizeCrop(50, 100, 50, 50);
        Assert.assertEquals(0, resizeCrop.getX());
        Assert.assertEquals(25, resizeCrop.getY());
        Assert.assertEquals(50, resizeCrop.getCropWidth());
        Assert.assertEquals(50, resizeCrop.getCropHeight());
        resizeCrop = new ResizeCrop(10, 5, 50, 50);
        Assert.assertEquals(2, resizeCrop.getX());
        Assert.assertEquals(0, resizeCrop.getY());
        Assert.assertEquals(5, resizeCrop.getCropWidth());
        Assert.assertEquals(5, resizeCrop.getCropHeight());
        resizeCrop = new ResizeCrop(5, 10, 50, 50);
        Assert.assertEquals(0, resizeCrop.getX());
        Assert.assertEquals(2, resizeCrop.getY());
        Assert.assertEquals(5, resizeCrop.getCropWidth());
        Assert.assertEquals(5, resizeCrop.getCropHeight());
    }
}