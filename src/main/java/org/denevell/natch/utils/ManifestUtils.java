package org.denevell.natch.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import javax.servlet.ServletContext;

public class ManifestUtils {

	public static Attributes getManifest(ServletContext ctx) throws IOException {
		InputStream inputStream = ctx.getResourceAsStream("/META-INF/MANIFEST.MF");
		Manifest manifest = new Manifest(inputStream);	
		return manifest.getMainAttributes();
	}

}
