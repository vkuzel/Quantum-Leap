package cz.quantumleap.core.view.template;

import cz.quantumleap.core.view.template.ResizableImageTagProcessor.ResizeCrop;
import cz.quantumleap.core.view.template.ResizableImageTagProcessor.ResizeLimit;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ResizableImageTagProcessorTest {

    @ParameterizedTest
    @MethodSource("provideArgumentsForLimitTest")
    public void limitDimensionsAreCorrect(int originalWidth, int originalHeight, int widht, int height,
                                          int expectedWidth, int expectedHeight) {
        ResizeLimit resizeLimit = new ResizeLimit(originalWidth, originalHeight, widht, height);

        assertEquals(expectedWidth, resizeLimit.getCalculatedWidth());
        assertEquals(expectedHeight, resizeLimit.getCalculatedHeight());
    }

    private static Stream<Arguments> provideArgumentsForLimitTest() {
        return Stream.of(
                createLimitTestArguments(100, 50, 50, 50, 50, 25),
                createLimitTestArguments(50, 100, 50, 50, 25, 50),
                createLimitTestArguments(10, 5, 50, 50, 50, 25),
                createLimitTestArguments(5, 10, 50, 50, 25, 50),
                createLimitTestArguments(100, 10, 50, 50, 50, 5)
        );
    }

    private static Arguments createLimitTestArguments(int originalWidth, int originalHeight, int widht, int height,
                                                      int expectedWidth, int expectedHeight) {
        return Arguments.of(originalWidth, originalHeight, widht, height, expectedWidth, expectedHeight);
    }

    @ParameterizedTest
    @MethodSource("provideArgumentsForCropTest")
    public void cropDimensionsAreCorrect(int originalWidth, int originalHeight, int width, int height,
                                         int expectedX, int expectedY, int expectedWidth, int expectedHeight) {
        ResizeCrop resizeCrop = new ResizeCrop(originalWidth,originalHeight, width, height);

        assertEquals(expectedX, resizeCrop.getX());
        assertEquals(expectedY, resizeCrop.getY());
        assertEquals(expectedWidth, resizeCrop.getCropWidth());
        assertEquals(expectedHeight, resizeCrop.getCropHeight());
    }

    private static Stream<Arguments> provideArgumentsForCropTest() {
        return Stream.of(
                createCropTestArguments(100, 50, 50, 50, 25, 0, 50, 50),
                createCropTestArguments(50, 100, 50, 50, 0, 25, 50, 50),
                createCropTestArguments(10, 5, 50, 50, 2, 0, 5, 5),
                createCropTestArguments(5, 10, 50, 50, 0, 2, 5, 5)
        );
    }

    private static Arguments createCropTestArguments(int originalWidth, int originalHeight, int width, int height,
                                                     int expectedX, int expectedY, int expectedWidth, int expectedHeight) {
        return Arguments.of(originalWidth, originalHeight, width, height,
                expectedX, expectedY, expectedWidth, expectedHeight);
    }
}
