package fr.leogomes.http2;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.FileSystems;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.io.FileSystemUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

/**
 * Caches the images to avoid reading them every time from the disk. This is
 * totally optional for the example.
 * 
 * @author Leonardo Gomes <http://leogomes.fr>
 */
public class ImageCache {

  private static ImageCache INSTANCE;

  private final Map<String, byte[]> imageBank = new ConcurrentHashMap<String, byte[]>(200);

  private ImageCache() {
  }

  public static synchronized ImageCache instance() {
    if (INSTANCE == null) {
      INSTANCE = new ImageCache();
      INSTANCE.initImageBank();
    }
    return INSTANCE;
  }

  public static String name(int x, int y) {
    return "tile-" + y + "-" + x + ".jpeg";
  }

  public byte[] image(int x, int y) {
    return imageBank.get(name(x, y));
  }

  private void initImageBank() {
    for (int y = 0; y < 10; y++) {
      for (int x = 0; x < 20; x++) {
        try {
          String name = name(x, y);
          byte[] fileBytes = IOUtils.toByteArray(getClass().getResourceAsStream(name));
          imageBank.put(name, fileBytes);
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
  }
}
