package com.traneptora.jxlatte.awt;

import javax.imageio.ImageReader;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.stream.ImageInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Locale;

// based on https://github.com/Ali-RS/jxlatte/tree/main/src/main/java/com/thebombzen/jxlatte/imageio
public class JXLImageReaderSpi extends ImageReaderSpi {
	private static final String vendorName = "Lib39 Avant";
	private static final String version = "1.0";
	private static final String[] names = { "jxl", "JXL" };
	private static final String[] suffixes = { "jxl" };
	private static final String[] MIMETypes = { "image/jxl" };
	private static final String readerClassName = JXLImageReaderSpi.class.getName();
	private static final String[] writerSpiNames = {};
	private static final byte[] LONG_HEADER = new byte[] { 0x00, 0x00, 0x00, 0x0C, 0x4A, 0x58, 0x4C, 0x20 };

	public JXLImageReaderSpi() {
		super(vendorName,
				version,
				names,
				suffixes,
				MIMETypes,
				readerClassName,
				new Class<?>[] { ImageInputStream.class },
				writerSpiNames,
				false,
				null, null,
				null, null,
				true,
				"javax_imageio_jxl_1.0",
				"com.sun.imageio.plugins.png.PNGMetadataFormat",
				null, null);
	}

	@Override
	public boolean canDecodeInput(Object source) throws IOException {
		byte[] header = null;
		if (source instanceof ImageInputStream ) {
			ImageInputStream is = (ImageInputStream) source;
			is.mark();
			header = new byte[8];
			is.readFully(header);
			is.reset();
		}
		if (header != null) {
			return (header[0] == (byte)0xFF && header[1] == (byte)0x0A) || Arrays.equals(header, LONG_HEADER);
		}
		return false;
	}

	@Override
	public ImageReader createReaderInstance(Object extension) {
		return new JXLImageReader(this);
	}

	@Override
	public String getDescription(Locale locale) {
		return "Loads JPEG-XL files (.jxl)";
	}
}
