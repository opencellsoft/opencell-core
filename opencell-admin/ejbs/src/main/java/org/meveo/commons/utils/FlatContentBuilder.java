package org.meveo.commons.utils;

/**
 * A builder class to get a flat content based on a position and length.
 */
public class  FlatContentBuilder {
    
    /** The Constant SPACE_CHAR. */
    private static final char SPACE_CHAR = ' ';

    /** The line. */
    private final StringBuffer line;
    
    /**
     * Instantiates a new flat content builder.
     */
    private FlatContentBuilder () {
        this.line = new StringBuffer();
    }
    
    /**
     * New instance.
     *
     * @return the flat content builder
     */
    public static FlatContentBuilder newInstance() {
        return new FlatContentBuilder();
    }
    
    /**
     * Append an item to the current line, based on string value , expected length , expected alignment and char to fill.
     *
     * @param value the value
     * @param alineLeft the aline left
     * @param length the length
     * @param charToFill the char to fill
     * @return the flat content builder
     */
    public  FlatContentBuilder addItem (final String value, boolean alineLeft, int length, char charToFill) {
        
        String itemValue = value != null ? value : "";
        int valueLength = itemValue.length();
        
        if (valueLength < length) {
            if (alineLeft) {
                this.line.append(this.alineLeft(itemValue, length - valueLength, charToFill));
            } else {
                this.line.append(this.alineRight(itemValue, length - valueLength, charToFill));
            }
        } else {
            this.line.append(itemValue.substring(0, length));
        }
        return this;
    }
    
    /**
     * Adds the item.
     *
     * @param value the value
     * @param alineLeft the aline left
     * @param length the length
     * @return the flat content builder
     */
    public FlatContentBuilder addItem(final String value, boolean alineLeft, int length) {
        return this.addItem(value, alineLeft, length, SPACE_CHAR);
    }
    
    /**
     * Adds the item.
     *
     * @param value the value
     * @return the flat content builder
     */
    public  FlatContentBuilder addItem (final String value) {
        this.line.append(value);
        return this;
    }
    
    /**
     * Adds the item.
     *
     * @param value the value
     * @param length the length
     * @return the flat content builder
     */
    public  FlatContentBuilder addItem (final String value, int length) {
        return this.addItem(value, true, length, SPACE_CHAR);
    }
    
    /**
     * Adds the blank.
     *
     * @param length the length
     * @return the flat content builder
     */
    public  FlatContentBuilder addBlank (int length) {
        this.addItem("", false, length,SPACE_CHAR);
        return this;
    }
    
    /**
     * Aline right.
     *
     * @param value the value
     * @param length the length
     * @param charToFill the char to fill
     * @return the string
     */
    private String alineRight(String value, int length, char charToFill) {
        for (int i = 0; i < length ; i++) {
            value = charToFill + value; 
        }
        return value;
    }
    
    /**
     * Aline left.
     *
     * @param value the value
     * @param length the length
     * @param charToFill the char to fill
     * @return the string
     */
    private String alineLeft(String value, int length, char charToFill) {
        for (int i = 0; i < length ; i++) {
            value += charToFill; 
        }
        return value;
    }

    /**
     * Buid.
     *
     * @return the line content previously built.
     */
    public String buid() {
        return this.line.toString();
    }
}
