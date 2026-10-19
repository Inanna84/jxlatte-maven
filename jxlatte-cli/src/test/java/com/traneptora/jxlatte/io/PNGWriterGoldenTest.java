package com.traneptora.jxlatte.io;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;
import java.util.zip.Deflater;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import com.traneptora.jxlatte.JXLDecoder;
import com.traneptora.jxlatte.JXLImage;
import com.traneptora.jxlatte.JXLOptions;

class PNGWriterGoldenTest {

    /** Fixed writer knobs so golden PNGs stay byte-stable across runs. */
    static final int DEFLATE_LEVEL = Deflater.DEFAULT_COMPRESSION;
    static final int PEAK_DETECT = JXLOptions.PEAK_DETECT_AUTO;

    static final String REGEN_PROPERTY = "jxlatte.regenGoldens";

    static Stream<String> goldenSamples() {
        return Stream.of(
                "art.jxl",
                "bbb.jxl",
                "bench.jxl",
                "blendmodes_5.jxl",
                "george-tiled.jxl",
                "lenna.jxl",
                "patches-lossless.jxl",
                "quilt.jxl",
                "sollevante-hdr.jxl",
                "wb-rainbow.jxl",
                "white.jxl");
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("goldenSamples")
    void pngMatchesGolden(String sampleName) throws IOException {
        String baseName = sampleName.substring(0, sampleName.lastIndexOf('.'));
        byte[] actual = encodePng(sampleName);

        if (Boolean.parseBoolean(System.getProperty(REGEN_PROPERTY))) {
            Path goldenPath = resolveGoldenDir().resolve(baseName + ".png");
            Files.createDirectories(goldenPath.getParent());
            Files.write(goldenPath, actual);
            assumeFalse(true, "regenerated " + goldenPath + "; re-run without -D" + REGEN_PROPERTY);
        }

        String goldenResource = "/golden/" + baseName + ".png";
        try (InputStream goldenIn = PNGWriterGoldenTest.class.getResourceAsStream(goldenResource)) {
            assertNotNull(goldenIn, "missing golden " + goldenResource
                    + "; generate with mvn -pl jxlatte-cli test -D" + REGEN_PROPERTY + "=true");
            byte[] expected = goldenIn.readAllBytes();
            assertArrayEquals(expected, actual,
                    "PNG mismatch for " + sampleName
                            + "; if intentional, regenerate with -D" + REGEN_PROPERTY + "=true");
        }
    }

    private static byte[] encodePng(String sampleName) throws IOException {
        String resource = "/samples/" + sampleName;
        try (InputStream in = PNGWriterGoldenTest.class.getResourceAsStream(resource)) {
            assertNotNull(in, "missing classpath resource " + resource);
            try (JXLDecoder decoder = new JXLDecoder(in)) {
                JXLImage image = decoder.decode();
                // Match JXLatte --png-hdr=auto: HDR images force 16-bit PNG.
                boolean hdr = image.isHDR();
                int bitDepth = hdr ? 16 : -1;
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                new PNGWriter(image, bitDepth, DEFLATE_LEVEL, hdr, PEAK_DETECT).write(baos);
                return baos.toByteArray();
            }
        }
    }

    private static Path resolveGoldenDir() {
        Path fromRoot = Path.of("jxlatte-cli", "src", "test", "resources", "golden");
        if (Files.isRegularFile(Path.of("jxlatte-cli", "pom.xml"))) {
            return fromRoot;
        }
        return Path.of("src", "test", "resources", "golden");
    }
}
