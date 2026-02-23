package com.traneptora.jxlatte.awt;

import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.stream.ImageInputStream;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Iterator;

// based on https://github.com/Ali-RS/jxlatte/tree/main/src/main/java/com/thebombzen/jxlatte/imageio
// based on https://git.sleeping.town/exa.mods/Yttr/src/branch/1.20.1/src/lib39/java/com/unascribed/lib39/avant/awt/JXLImageReader.java
public class JXLImageReader extends ImageReader {

	private int width, height;

	public JXLImageReader(ImageReaderSpi originatingProvider) {
		super(originatingProvider);
	}

	@Override
	public int getNumImages(boolean allowSearch) {
		return 1;
	}

	@Override
	public int getWidth(int imageIndex) {
		return width;
	}

	@Override
	public int getHeight(int imageIndex) {
		return height;
	}

	@Override
	public Iterator<ImageTypeSpecifier> getImageTypes(int imageIndex) {
		return null;
	}

	@Override
	public IIOMetadata getStreamMetadata() {
		return null;
	}

	@Override
	public IIOMetadata getImageMetadata(int imageIndex) {
		return null;
	}

	@Override
	public BufferedImage read(int imageIndex, ImageReadParam param) throws IOException {
		var iis = (ImageInputStream)input;
		var is = new InputStream() {
			@Override
			public int read() throws IOException {
				return iis.read();
			}
			
			@Override
			public int read(byte[] b, int off, int len) throws IOException {
				return iis.read(b, off, len);
			}
			
		};
		var data = ByteBuffer.wrap(is.readAllBytes());
		var width = IntBuffer.allocate(1);
		var height = IntBuffer.allocate(1);
		var out = DecodeToBufferedImage.decode(true, data, width, height, null);
		this.width = width.get(0);
		this.height = height.get(0);
		var img = new BufferedImage(this.width, this.height, BufferedImage.TYPE_INT_ARGB);
		out.asIntBuffer().get(((DataBufferInt)img.getRaster().getDataBuffer()).getData());
		return img;
	}
	
}
