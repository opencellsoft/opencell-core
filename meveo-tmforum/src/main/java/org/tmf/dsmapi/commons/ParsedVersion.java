package org.tmf.dsmapi.commons;

import java.io.Serializable;
import java.text.DecimalFormat;

/**
 *
 * @author bahman.barzideh
 *
 */
public class ParsedVersion implements Serializable {
    private final static long serialVersionUID = 1L;

    public static final ParsedVersion ROOT_CATALOG_VERSION = new ParsedVersion("", "");

    private static final int MAX_MAJOR_VERSION = 999999;
    private static final DecimalFormat majorVersionInternalFormat = new DecimalFormat("000000");

    private static final int MAX_MINOR_VERSION = 999;
    private static final DecimalFormat minorVersionInternalFormat = new DecimalFormat("000");

    private Integer majorVersion;
    private Integer minorVersion;

    private String externalView;
    private String internalView;

    private boolean valid;

    private ParsedVersion(String internalView, String externalView) {
        this.majorVersion = null;
        this.minorVersion = null;

        this.externalView = externalView;
        this.internalView = internalView;

        valid = true;
    }

    public ParsedVersion(String version) throws IllegalArgumentException {
        initialize_();
        if (load_(version) == false) {
            throw new IllegalArgumentException("'" + version + "' is not a valid version.");
        }

        if (majorVersion > MAX_MAJOR_VERSION) {
            throw new IllegalArgumentException ("Major version, " + majorVersion + ", is too large; maximum value=" + MAX_MAJOR_VERSION);
        }

        if (minorVersion > MAX_MINOR_VERSION) {
            throw new IllegalArgumentException ("Minor version, " + minorVersion + ", is too large; maximum value=" + MAX_MINOR_VERSION);
        }

        this.externalView = createExternalView_();
        this.internalView = createInternalView_();

        valid = true;
    }

    public Integer getMajorVersion() {
        return majorVersion;
    }

    public Integer getMinorVersion() {
        return minorVersion;
    }

    public String getExternalView() {
        return externalView;
    }

    public String getInternalView() {
        return internalView;
    }

    public boolean isValid() {
        return valid;
    }

    @Override
    public int hashCode() {
        int hash = 7;

        hash = 89 * hash + (this.majorVersion != null ? this.majorVersion.hashCode() : 0);
        hash = 89 * hash + (this.minorVersion != null ? this.minorVersion.hashCode() : 0);
        hash = 89 * hash + (this.externalView != null ? this.externalView.hashCode() : 0);
        hash = 89 * hash + (this.internalView != null ? this.internalView.hashCode() : 0);
        hash = 89 * hash + (this.valid == true ? 1 : 0);

        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) {
            return false;
        }

        final ParsedVersion other = (ParsedVersion) object;
        if (Utilities.areEqual(this.majorVersion, other.majorVersion) == false) {
            return false;
        }

        if (Utilities.areEqual(this.minorVersion, other.minorVersion) == false) {
            return false;
        }

        if (Utilities.areEqual(this.externalView, other.externalView) == false) {
            return false;
        }

        if (Utilities.areEqual(this.internalView, other.internalView) == false) {
            return false;
        }

        if (this.valid == other.valid) {
            return false;
        }

        return true;
    }

    @Override
    public String toString() {
        return "ParsedVersion{" + "majorVersion=" + majorVersion + ", minorVersion=" + minorVersion + ", externalView=" + externalView + ", internalView=" + internalView + ", valid=" + valid + '}';
    }

    public boolean isGreaterThan(ParsedVersion other) {
        if (this.isValid() == false) {
            throw new IllegalArgumentException ("invalid version object");
        }

        if (other == null || other.isValid() == false) {
            throw new IllegalArgumentException ("invalid other version object");
        }

        if (this == ROOT_CATALOG_VERSION) {
            return false;
        }

        if (other == ROOT_CATALOG_VERSION) {
            return true;
        }

        int compare = this.majorVersion.compareTo(other.majorVersion);
        if (compare < 0) {
            return false;
        }

        if (compare > 0) {
            return true;
        }

        return (this.minorVersion.compareTo (other.minorVersion) > 0) ? true : false;
    }

    private String createInternalView_() {
        return (majorVersionInternalFormat.format(majorVersion) + "." + minorVersionInternalFormat.format(minorVersion));
    }

    private String createExternalView_() {
        return (majorVersion + "." + minorVersion);
    }

    private boolean load_(String input) {
        if (input == null) {
            return false;
        }

        String parts [] = input.split ("\\.");
        if (parts.length != 2) {
            return false;
        }

        try {
            majorVersion = Integer.parseInt(parts [0]);
        }
        catch (Exception ex) {
            return false;
        }

        try {
            minorVersion = Integer.parseInt(parts [1]);
        }
        catch (Exception ex) {
            initialize_();
            return false;
        }

        return true;
    }

    private void initialize_() {
        majorVersion = null;
        minorVersion = null;

        externalView = null;
        internalView = null;

        valid = false;
    }

}
