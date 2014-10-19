package com.box.boxjavalibv2.dao;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;

public class BoxObject extends BoxBase implements IBoxParcelable {

    private final Map<String, Object> extraMap = new BoxHashMap<String, Object>();

    private final Map<String, Object> map = new BoxHashMap<String, Object>();

    private static HashSet<Class<?>> primitiveWrapperSet = new HashSet<Class<?>>();
    static {
        primitiveWrapperSet.add(Boolean.class);
        primitiveWrapperSet.add(Byte.class);
        primitiveWrapperSet.add(Character.class);
        primitiveWrapperSet.add(Short.class);
        primitiveWrapperSet.add(Integer.class);
        primitiveWrapperSet.add(Long.class);
        primitiveWrapperSet.add(Double.class);
        primitiveWrapperSet.add(Float.class);
    }

    public BoxObject() {
    }

    /**
     * Instantiate the object from a map. Each entry in the map reflects to a field.
     * 
     * @param map
     */
    public BoxObject(Map<String, Object> map) {
        cloneMap(this.properties(), map);
    }

    /**
     * Whether the two objects are equal. This strictly compares all the fields in the two objects, if any fields are different this returns false.
     * 
     * @param obj
     * @return Whether the two objects are equal.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof BoxObject)) {
            return false;
        }

        if (this == obj) {
            return true;
        }

        BoxObject bObj = (BoxObject) obj;
        return new EqualsBuilder().append(properties(), bObj.properties()).append(extraProperties(), bObj.extraProperties()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(properties()).append(extraProperties()).toHashCode();
    }

    /**
     * Copy constructor, this does deep copy for all the fields.
     * 
     * @param obj
     */
    public BoxObject(BoxObject obj) {
        cloneMap(properties(), obj.properties());
        cloneMap(extraProperties(), obj.extraProperties());
    }

    protected void put(String key, Object value) {
        properties().put(key, value);
    }

    public Object getValue(String key) {
        return properties().get(key);
    }

    /**
     * Get extra data. This could be extra unknown data passed back from api responses, the data is put in a Map.
     * 
     * @param key
     * @return extra object
     */
    public Object getExtraData(String key) {
        return extraProperties().get(key);
    }

    @JsonAnyGetter
    protected Map<String, Object> extraProperties() {
        return extraMap;
    }

    protected Map<String, Object> properties() {
        return map;
    }

    /**
     * Use this method to check whether the object contains certain field at all. This helps differentiate the case when the field is not returned from server
     * at all, or is returned from server but value is null. For the first case, this method returns false, the later case returns true.
     * 
     * @return whether the field exists
     */
    public boolean contains(String key) {
        return properties().containsKey(key) || extraProperties().containsKey(key);
    }

    @JsonAnySetter
    protected void handleUnknown(String key, Object value) {
        // For unknown fields, only put in String or primitive wrappers to avoid potential serialization/deserialization problem.
        if (canBeHandledAsUnknown(value)) {
            extraProperties().put(key, value);
        }
    }

    protected boolean canBeHandledAsUnknown(Object value) {
        if (value instanceof String) {
            // Allow String.
            return true;
        } else if ((value != null && primitiveWrapperSet.contains(value.getClass()))) {
            // Allow primitive wrappers(e.g., Integer, Boolean...).
            return true;
        } else if (value != null && value instanceof ArrayList) {
            // Allow ArrayLists of primitive or ArrayLists or primitive wrappers.
            ArrayList<?> list = (ArrayList<?>) value;
            if (!list.isEmpty()) {
                Object component = list.get(0);
                if (component instanceof String) {
                    return true;
                } else {
                    Class<?> componentClass = component.getClass();
                    return componentClass.isPrimitive() || primitiveWrapperSet.contains(componentClass);
                }
            }
        }
        return false;
    }

    @Override
    public void writeToParcel(IBoxParcelWrapper parcelWrapper, int flags) {
        parcelWrapper.writeMap(properties());
        parcelWrapper.writeMap(extraProperties());
    }

    public BoxObject(IBoxParcelWrapper in) {
        in.readMap(properties());
        in.readMap(extraProperties());
    }

    /**
     * Clone a map to another
     * 
     * @param destination
     * @param source
     */
    @SuppressWarnings("unchecked")
    private static void cloneMap(Map<String, Object> destination, Map<String, Object> source) {
        destination.clear();
        for (Map.Entry<String, Object> entry : source.entrySet()) {
            Object value = entry.getValue();
            if (value instanceof BoxObject) {
                try {
                    destination.put(entry.getKey(), value.getClass().getConstructor(value.getClass()).newInstance(value));
                } catch (Exception e) {
                    // Exception is swallowed.
                }
            } else if (value instanceof ArrayList<?>) {
                ArrayList<Object> list = new ArrayList<Object>();
                cloneArrayList(list, (ArrayList<Object>) value);
                destination.put(entry.getKey(), list);
            } else {
                destination.put(entry.getKey(), value);
            }
        }
    }

    /**
     * Clone an arraylist.
     * 
     * @param destination
     * @param source
     */
    private static void cloneArrayList(ArrayList<Object> destination, ArrayList<Object> source) {
        for (Object obj : source) {
            if (obj instanceof BoxObject) {
                try {
                    destination.add(obj.getClass().getConstructor(obj.getClass()).newInstance(obj));
                } catch (Exception e) {
                    // Exception is swallowed.
                }
            } else {
                destination.add(obj);
            }
        }
    }

}
