package net.zhuoweizhang.mcpelauncher.patch;

import java.io.*;
import java.nio.*;

import net.zhuoweizhang.mcpelauncher.MinecraftVersion;

public final class PatchUtils {

	public static MinecraftVersion minecraftVersion = null;

	public static void patch(ByteBuffer buf, com.joshuahuelsman.patchtool.PTPatch patch) {
		MinecraftVersion.PatchTranslator translator = minecraftVersion.translator;
		for(patch.count = 0; patch.count < patch.getNumPatches(); patch.count++){
			int addr = patch.getNextAddr();
			if (translator != null) addr = translator.get(addr);
			buf.position(addr);
			buf.put(patch.getNextData());
		}
	}

	public static void unpatch(ByteBuffer buf, byte[] original, com.joshuahuelsman.patchtool.PTPatch patch) {
		MinecraftVersion.PatchTranslator translator = minecraftVersion.translator;
		ByteBuffer originalBuf = ByteBuffer.wrap(original);
		for(patch.count = 0; patch.count < patch.getNumPatches(); patch.count++){
			int addr = patch.getNextAddr();
			if (translator != null) addr = translator.get(addr);
			buf.position(addr);
			originalBuf.position(addr);
			byte[] nextData = new byte[patch.getDataLength()];
			originalBuf.get(nextData);
			buf.put(nextData);
		}
	}

	public static void copy(File from, File to) throws IOException {
		InputStream is = new FileInputStream(from);
		int length = (int) from.length();
		byte[] data = new byte[length];
		is.read(data);
		is.close();
		OutputStream os = new FileOutputStream(to);
		os.write(data);
		os.close();
	}

	public static boolean canLivePatch(File file) throws IOException {
		MinecraftVersion.PatchTranslator translator = minecraftVersion.translator;
		com.joshuahuelsman.patchtool.PTPatch patch = new com.joshuahuelsman.patchtool.PTPatch();
		patch.loadPatch(file);
		for(patch.count = 0; patch.count < patch.getNumPatches(); patch.count++){
			int address = patch.getNextAddr();
			if (translator != null) address = translator.get(address);
			if (address >= minecraftVersion.libLoadOffsetBegin) {
				return false;
			}
		}
		return true;
	}
		
}
