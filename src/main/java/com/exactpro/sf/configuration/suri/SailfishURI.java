package com.exactpro.sf.configuration.suri;

import javax.lang.model.SourceVersion;

/** An extremely simple SailfishURI implementation for Shsha
 * In Shsha context it is used solely if {@code AbstractMessageFactory.createMessage(MsgMetaData metaData)} is called.
 * Shsha calls this method indirectly, and instantiating e.g. {@code RawOmnetMessageFactory}
 * (in {@code OmnetParser}), or {@code AlphaPointMessageFactory} (in {@code AlphapointParser}) requires a non-null SailfishURI.<br>
 * Constructors do the same isIdentifier() checks as the original SailfishURI.
 */
public class SailfishURI {
    public final static String URI_PARTS_SEPARATOR = ":";
    private final String pluginAlias;
    private final String resourceName;
    private final static SailfishURI NULL_URI;

    private final static String Alphapoint_PROTOCOL_ID = "alphapoint";

    static {
        NULL_URI = new SailfishURI();
    }

    private SailfishURI() {
        this.pluginAlias = "null";
        this.resourceName = null;
    }

    public SailfishURI(String pluginAlias) throws SailfishURIException {
        this.pluginAlias = pluginAlias;
        this.resourceName = null;
    }

    /** fx-core {@link com.exactprosystems.testtools.shsha.parsing.fixsbe.FixSbeParser} needs it.
     * @param classAlias is ignored
     */
    public SailfishURI(String pluginAlias, String classAlias, String resourceName) throws SailfishURIException {
        this(pluginAlias, resourceName);
    }

    private SailfishURI(String pluginAlias, String resourceName) throws SailfishURIException {
        if (!SourceVersion.isIdentifier(pluginAlias)) {
            throw new SailfishURIException(String.format("%s is not a valid pluginAlias", repr(pluginAlias)));
        }
        this.pluginAlias = pluginAlias;
        if (!SourceVersion.isIdentifier(resourceName)) {
            throw new SailfishURIException(String.format("%s is not a valid resourceName", repr(resourceName)));
        }
        this.resourceName = resourceName;
    }

    public static SailfishURI alphapoint() {
        try {
            return new SailfishURI(Alphapoint_PROTOCOL_ID);
        } catch (SailfishURIException e) {
            throw new RuntimeException(String.format("error constructing URI from pluginAlias String %s", Alphapoint_PROTOCOL_ID));
        }
    }


    public static SailfishURI parse(String uri) throws SailfishURIException {
        if (uri == null) {
            return NULL_URI;
        }
        String[] urlParts = uri.split(URI_PARTS_SEPARATOR);
        switch (urlParts.length) {
            case 1: return new SailfishURI(urlParts[0]);
            case 2: return new SailfishURI(urlParts[0], urlParts[1]);
            case 3: return new SailfishURI(urlParts[0], urlParts[1], urlParts[2]);
            default: throw new SailfishURIException("Invalid Sailfish-URI: " + uri);
        }
    }

    public String getPluginAlias() {
        return pluginAlias;
    }

    public String getResourceName() {
        return resourceName;
    }

    @Override
    public String toString() {
        return resourceName == null ? pluginAlias + URI_PARTS_SEPARATOR : pluginAlias + URI_PARTS_SEPARATOR + resourceName;
    }

    public boolean isAbsolute() {
        return false;
    }

    public String getClassAlias() {
        return null;
    }

    private static String repr(String s) {
        if (s == null) return "null";
        if (s.isEmpty()) return "empty String";
        return "\"" + s.replace("\"", "\\\"") + "\"";
    }
}
