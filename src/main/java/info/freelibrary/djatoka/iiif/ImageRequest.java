
package info.freelibrary.djatoka.iiif;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import info.freelibrary.util.StringUtils;

/**
 * An image request from FreeLib-Djatoka's IIIF interface.
 *
 * @author <a href="mailto:ksclarke@gmail.com">Kevin S. Clarke</a>
 */
public class ImageRequest implements IIIFRequest {

    private static final Logger LOGGER = LoggerFactory.getLogger(ImageRequest.class);

    private final String myPrefix;

    private final String myIdentifier;

    private String myExtension;

    private final Quality myQuality;

    private float myRotation;

    private final Region myRegion;

    private final Size mySize;

    /**
     * Returns a <code>ImageRequest</code> for the supplied {@link URL}.
     *
     * @param aURL A {@link URL} representing the <code>ImageRequest</code>
     * @throws IIIFException If there is a problem creating the request
     */
    public ImageRequest(final URL aURL) throws IIIFException {
        this(aURL, null);
    }

    /**
     * Returns a <code>ImageRequest</code> for the supplied {@link URL}.
     *
     * @param aURL A {@link URL} representing the <code>ImageRequest</code>
     * @param aPrefix A pre-configured prefix to use in parsing the request
     * @throws IIIFException If there is a problem creating the request
     */
    public ImageRequest(final URL aURL, final String aPrefix) throws IIIFException {
        String path = aURL.getPath();
        String[] parts;

        myPrefix = Builder.checkServicePrefix(aPrefix);

        if (myPrefix != null) {
            final int start = path.indexOf(myPrefix) + myPrefix.length() + 1;
            path = path.substring(start);
        } else {
            path = path.substring(1);
        }

        // Check for valid image extensions
        if ((path.endsWith(".jpg") || path.endsWith(".gif") || path.endsWith(".jp2") || path.endsWith(".pdf") ||
                path.endsWith(".tif") || path.endsWith(".png")) &&
                path.length() > 4) {
            final String extension = path.substring(path.length() - 3);

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Setting extension to: {}", extension);
            }

            myExtension = extension;

            // If we've set the extension, remove it from the path
            path = path.substring(0, path.length() - 4);
        }

        parts = path.split("/");

        if (parts.length != 5) {
            if (LOGGER.isErrorEnabled()) {
                LOGGER.error("Request path '{}' contains '{}' parts instead of 5", StringUtils.toString(parts, ' '),
                        parts.length);
            }

            throw new IIIFException("Request doesn't contain correct number of parts: " + path);
        }

        myIdentifier = decode(parts[0]);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Setting image identifier: {}", myIdentifier);
        }

        myRegion = new Region(decode(parts[1]));

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Setting requested region: {}", myRegion.toString());
        }

        mySize = new Size(decode(parts[2]));

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Setting requested size: {}", mySize.toString());
        }

        try {
            myRotation = Float.parseFloat(decode(parts[3]));

            if (LOGGER.isWarnEnabled()) {
                if (myRotation != 0 && myRotation != 90 && myRotation != 180 && myRotation != 270) {
                    LOGGER.warn("{}° rotation not supported", myRotation);
                }
            }

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Setting requested rotation: {}", myRotation);
            }
        } catch (final NumberFormatException details) {
            throw new IIIFException("Rotation value isn't a float: " + parts[3]);
        }

        myQuality = new Quality(decode(parts[4]));

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Setting requested quality: {}", myQuality);
        }
    }

    /**
     * Gets the extension for the request.
     *
     * @return The extension for the request
     */
    @Override
    public String getExtension() {
        return myExtension;
    }

    /**
     * Returns true if the request has an extension; else, false.
     *
     * @return True if the request has an extension; else, false
     */
    @Override
    public boolean hasExtension() {
        return myExtension != null;
    }

    /**
     * Gets the identifier for the request.
     *
     * @return The identifier for the request
     */
    @Override
    public String getIdentifier() {
        return myIdentifier;
    }

    /**
     * Returns the region of the <code>ImageRequest</code>.
     *
     * @return The region of the <code>ImageRequest</code>
     */
    public Region getRegion() {
        return myRegion;
    }

    /**
     * Returns the size of the <code>ImageRequest</code>.
     *
     * @return The size of the <code>ImageRequest</code>
     */
    public Size getSize() {
        return mySize;
    }

    /**
     * Returns the rotation of the <code>ImageRequest</code>.
     *
     * @return The rotation of the <code>ImageRequest</code>
     */
    public float getRotation() {
        return myRotation;
    }

    /**
     * Returns the quality of the <code>ImageRequest</code>.
     *
     * @return The quality of the <code>ImageRequest</code>
     */
    public Quality getQuality() {
        return myQuality;
    }

    /**
     * Returns the service prefix of the request.
     *
     * @return The service prefix of the request
     */
    @Override
    public String getServicePrefix() {
        return myPrefix;
    }

    /**
     * Returns true if the request has a service prefix; else, false.
     *
     * @return True if the request has a service prefix; else, false
     */
    @Override
    public boolean hasServicePrefix() {
        return myPrefix != null;
    }

    private String decode(final String aString) {
        String string;

        try {
            string = URLDecoder.decode(aString, "UTF-8");
            return URLDecoder.decode(string, "UTF-8");
        } catch (final UnsupportedEncodingException details) {
            throw new RuntimeException(details); // every JVM supports UTF-8
        }
    }
}
