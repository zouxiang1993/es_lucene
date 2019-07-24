package edu.stanford.nlp.io;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Helper Class for various I/O related things.
 *
 * @author Kayur Patel
 * @author Teg Grenager
 * @author Christopher Manning
 */

public class IOUtils {
    /**
     * Locates this file either using the given URL, or in the CLASSPATH, or in the file system
     * The CLASSPATH takes priority over the file system!
     * This stream is buffered and gunzipped (if necessary).
     *
     * @param textFileOrUrl The String specifying the URL/resource/file to load
     * @return An InputStream for loading a resource
     * @throws IOException On any IO error
     * @throws NullPointerException Input parameter is null
     */
    public static InputStream getInputStreamFromURLOrClasspathOrFileSystem(String textFileOrUrl)
            throws IOException, NullPointerException {
        InputStream in;
        if (textFileOrUrl == null) {
            throw new NullPointerException("Attempt to open file with null name");
        } else if (textFileOrUrl.matches("https?://.*")) {
            URL u = new URL(textFileOrUrl);
            URLConnection uc = u.openConnection();
            in = uc.getInputStream();
        } else {
            try {
                in = findStreamInClasspathOrFileSystem(textFileOrUrl);
            } catch (FileNotFoundException e) {
                try {
                    // Maybe this happens to be some other format of URL?
                    URL u = new URL(textFileOrUrl);
                    URLConnection uc = u.openConnection();
                    in = uc.getInputStream();
                } catch (IOException e2) {
                    // Don't make the original exception a cause, since it is usually bogus
                    throw new IOException("Unable to open \"" +
                            textFileOrUrl + "\" as " + "class path, filename or URL"); // , e2);
                }
            }
        }

        // If it is a GZIP stream then ungzip it
        if (textFileOrUrl.endsWith(".gz")) {
            try {
                in = new GZIPInputStream(in);
            } catch (Exception e) {
                throw new RuntimeIOException("Resource or file looks like a gzip file, but is not: " + textFileOrUrl, e);
            }
        }

        // buffer this stream.  even gzip streams benefit from buffering,
        // such as for the shift reduce parser [cdm 2016: I think this is only because default buffer is small; see below]
        in = new BufferedInputStream(in);

        return in;
    }

    /**
     * Locates this file either in the CLASSPATH or in the file system. The CLASSPATH takes priority.
     * Note that this method uses the ClassLoader methods, so that classpath resources must be specified as
     * absolute resource paths without a leading "/".
     *
     * @param name The file or resource name
     * @throws FileNotFoundException If the file does not exist
     * @return The InputStream of name, or null if not found
     */
    private static InputStream findStreamInClasspathOrFileSystem(String name) throws FileNotFoundException {
        // ms 10-04-2010:
        // - even though this may look like a regular file, it may be a path inside a jar in the CLASSPATH
        // - check for this first. This takes precedence over the file system.
        InputStream is = IOUtils.class.getClassLoader().getResourceAsStream(name);
        // windows File.separator is \, but getting resources only works with /
        if (is == null) {
            is = IOUtils.class.getClassLoader().getResourceAsStream(name.replaceAll("\\\\", "/"));
            // Classpath doesn't like double slashes (e.g., /home/user//foo.txt)
            if (is == null) {
                is = IOUtils.class.getClassLoader().getResourceAsStream(name.replaceAll("\\\\", "/").replaceAll("/+", "/"));
            }
        }
        // if not found in the CLASSPATH, load from the file system
        if (is == null) {
            is = new FileInputStream(name);
        }
        return is;
    }

    public static DataInputStream getDataInputStream(String filenameUrlOrClassPath) throws IOException {
        return new DataInputStream(getInputStreamFromURLOrClasspathOrFileSystem(filenameUrlOrClassPath));
    }

    public static DataOutputStream getDataOutputStream(String filename) throws IOException {
        return new DataOutputStream(getBufferedOutputStream((filename)));
    }

    private static OutputStream getBufferedOutputStream(String path) throws IOException {
        OutputStream os = new BufferedOutputStream(new FileOutputStream(path));
        if (path.endsWith(".gz")) {
            os = new GZIPOutputStream(os);
        }
        return os;
    }

    public static BufferedReader readerFromFile(File file, String encoding) {
        InputStream is = null;
        try {
            is = inputStreamFromFile(file);
            if (encoding == null) {
                return new BufferedReader(new InputStreamReader(is));
            } else {
                return new BufferedReader(new InputStreamReader(is, encoding));
            }
        } catch (IOException ioe) {
            IOUtils.closeIgnoringExceptions(is);
            throw new RuntimeIOException(ioe);
        }
    }

    public static InputStream inputStreamFromFile(File file) throws RuntimeIOException {
        try {
            InputStream is = new BufferedInputStream(new FileInputStream(file));
            if (file.getName().endsWith(".gz")) {
                is = new GZIPInputStream(is);
            }
            return is;
        } catch (IOException e) {
            throw new RuntimeIOException(e);
        }
    }

    public static void closeIgnoringExceptions(Closeable c) {
        if (c != null) {
            try {
                c.close();
            } catch (IOException ioe) {
                // ignore
            }
        }
    }

}
