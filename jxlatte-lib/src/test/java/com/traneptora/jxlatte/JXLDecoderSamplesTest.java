package com.traneptora.jxlatte;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.traneptora.jxlatte.util.ImageBuffer;

class JXLDecoderSamplesTest {

    /** sampleName, expectedWidth, expectedHeight, expectedColorChannels */
    static Stream<Arguments> samples() {
        return Stream.of(
                Arguments.of("ants-lossless.jxl", 3264, 2448, 3),
                Arguments.of("ants.jxl", 3264, 2448, 3),
                Arguments.of("art.jxl", 128, 128, 3),
                Arguments.of("bbb.jxl", 1280, 720, 3),
                Arguments.of("bench.jxl", 606, 500, 3),
                Arguments.of("blendmodes_5.jxl", 1024, 1024, 3),
                Arguments.of("george-tiled.jxl", 3840, 2160, 3),
                Arguments.of("lenna.jxl", 512, 512, 3),
                Arguments.of("patches-lossless.jxl", 1600, 1096, 3),
                Arguments.of("quilt.jxl", 1024, 1024, 3),
                Arguments.of("sollevante-hdr.jxl", 3840, 2160, 3),
                Arguments.of("wb-rainbow.jxl", 2048, 1152, 3),
                Arguments.of("white.jxl", 320, 240, 3));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("samples")
    void decodesSample(String sampleName, int expectedWidth, int expectedHeight, int expectedColorChannels)
            throws IOException {
        String resource = "/samples/" + sampleName;
        try (InputStream in = JXLDecoderSamplesTest.class.getResourceAsStream(resource)) {
            assertNotNull(in, "missing classpath resource " + resource);
            try (JXLDecoder decoder = new JXLDecoder(in)) {
                JXLImage image = decoder.decode();
                assertNotNull(image);
                assertEquals(expectedWidth, image.getWidth(), "width");
                assertEquals(expectedHeight, image.getHeight(), "height");
                assertEquals(expectedColorChannels, image.getColorChannelCount(), "color channels");

                ImageBuffer[] buffers = image.getBuffer(false);
                assertNotNull(buffers);
                assertTrue(buffers.length >= expectedColorChannels, "buffer length vs color channels");
                for (ImageBuffer buffer : buffers) {
                    assertNotNull(buffer);
                    assertEquals(expectedWidth, buffer.width);
                    assertEquals(expectedHeight, buffer.height);
                }
            }
        }
    }
}
