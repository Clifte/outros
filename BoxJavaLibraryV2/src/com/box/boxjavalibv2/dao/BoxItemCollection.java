package com.box.boxjavalibv2.dao;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BoxItemCollection extends BoxTypedObject {

    public static final String FIELD_NAME = "name";
    public static final String FIELD_COLLECTION_TYPE = "collection_type";

    public static final String COLLECTION_TYPE_FAVORITES = "favorites";

    /**
     * Constructor.
     */
    public BoxItemCollection() {
        setType(BoxResourceType.COLLECTION.toString());
    }

    /**
     * Copy constructor, this does deep copy for all the fields.
     * 
     * @param obj
     */
    public BoxItemCollection(BoxItemCollection obj) {
        super(obj);
    }

    /**
     * Instantiate the object from a map. Each entry in the map reflects to a field.
     * 
     * @param map
     */
    public BoxItemCollection(Map<String, Object> map) {
        super(map);
    }

    /**
     * Get the name of this collection.
     * 
     */
    @JsonProperty(FIELD_NAME)
    public String getName() {
        return (String) getValue(FIELD_NAME);
    }

    /**
     * Setter. This is only used by {@see <a href="http://jackson.codehaus.org">Jackson JSON processer</a>}.
     * 
     * @param name
     *            the name to set
     */
    @JsonProperty(FIELD_NAME)
    private void setName(String name) {
        put(FIELD_NAME, name);
    }

    /**
     * Get the system-defined type of this collection. For example, "favorites" if this is the Favorites collection.
     * 
     */
    @JsonProperty(FIELD_COLLECTION_TYPE)
    public String getCollectionType() {
        return (String) getValue(FIELD_COLLECTION_TYPE);
    }

    /**
     * Setter. This is only used by {@see <a href="http://jackson.codehaus.org">Jackson JSON processer</a>}.
     * 
     * @param collectionType
     *            the collection type to set
     */
    @JsonProperty(FIELD_COLLECTION_TYPE)
    private void setCollectionType(String collectionType) {
        put(FIELD_COLLECTION_TYPE, collectionType);
    }

    public boolean isFavoritesCollection() {
        return COLLECTION_TYPE_FAVORITES.equals(getCollectionType());
    }

    public BoxItemCollection(IBoxParcelWrapper in) {
        super(in);
    }

}
