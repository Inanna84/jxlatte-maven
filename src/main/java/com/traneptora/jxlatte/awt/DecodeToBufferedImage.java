package com.traneptora.jxlatte.awt;

import com.traneptora.jxlatte.JXLDecoder;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

// Based on https://git.sleeping.town/exa.mods/Yttr/src/branch/1.20.1/src/lib39/java/com/unascribed/lib39/avant/Lib39Avant.java
public class DecodeToBufferedImage {

	private DecodeToBufferedImage()
	{
	}
	
	public static ByteBuffer decode(boolean swizzle, ByteBuffer buffer, IntBuffer x, IntBuffer y, IntBuffer channels_in_file) throws IOException {
		try (JXLDecoder dec = new JXLDecoder(new ByteBufferInputStream(buffer))) {
			var img = dec.decode();
			var bufs = img.getBuffer(false);
			var outSize = img.getWidth()*img.getHeight()*4;
			ByteBuffer out = ByteBuffer.allocate(outSize);
			if (channels_in_file != null) {
				channels_in_file.put(0, 4);
			}
			x.put(0, img.getWidth());
			y.put(0, img.getHeight());
			
			var stride = img.getWidth();
			for (int ch = 0; ch < 4; ch++) {
				int idx = 0;
				if (ch == 3) {
					if (img.hasAlpha()) {
						idx = img.getColorChannelCount()+img.getAlphaIndex();
					} else {
						idx = -1;
					}
				} else if (img.getColorChannelCount() == 3) {
					idx = ch;
				} else {
					idx = 0;
				}
				var buf = idx >= 0 && idx < bufs.length ? bufs[idx] : null;
				if (buf != null && !buf.isInt()) buf.castToInt(8);
				var w = (buf == null ? img.getWidth() : buf.width);
				var h = (buf == null ? img.getHeight() : buf.height);
				for (int iy = 0; iy < h; iy++) {
					for (int ix = 0; ix < w; ix++) {
						int ofs = ch;
						if (swizzle) {
							if (ch == 3) {
								ofs = 0;
							} else {
								ofs = ch+1;
							}
						}
						var col = (buf == null ? 0xFF : buf.getIntBuffer()[iy][ix]);
						if (buf != null) {
							int depth = img.getTaggedBitDepth(idx);
							if (depth < 8) {
								if (depth == 1) {
									col *= 255;
								} else if (depth == 4) {
									col *= 2;
								}
							}
						}
						out.put((((iy*stride)+ix)*4)+ofs, (byte)col);
					}
				}
			}
			return out;
		}
	}
}
