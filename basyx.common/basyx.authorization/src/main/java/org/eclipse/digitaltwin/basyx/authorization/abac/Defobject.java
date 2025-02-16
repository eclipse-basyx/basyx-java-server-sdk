
package org.eclipse.digitaltwin.basyx.authorization.abac;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "name",
    "objects",
    "USEOBJECTS"
})
@Generated("jsonschema2pojo")
public class Defobject {

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("name")
    private String name;
    @JsonProperty("objects")
    private List<ObjectItem> objects = new ArrayList<ObjectItem>();
    @JsonProperty("USEOBJECTS")
    private List<String> useobjects = new ArrayList<String>();

    /**
     * No args constructor for use in serialization
     * 
     */
    public Defobject() {
    }

    /**
     * 
     * @param useobjects
     * @param objects
     * @param name
     */
    public Defobject(String name, List<ObjectItem> objects, List<String> useobjects) {
        super();
        this.name = name;
        this.objects = objects;
        this.useobjects = useobjects;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("name")
    public String getName() {
        return name;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("objects")
    public List<ObjectItem> getObjects() {
        return objects;
    }

    @JsonProperty("objects")
    public void setObjects(List<ObjectItem> objects) {
        this.objects = objects;
    }

    @JsonProperty("USEOBJECTS")
    public List<String> getUseobjects() {
        return useobjects;
    }

    @JsonProperty("USEOBJECTS")
    public void setUseobjects(List<String> useobjects) {
        this.useobjects = useobjects;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(Defobject.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("name");
        sb.append('=');
        sb.append(((this.name == null)?"<null>":this.name));
        sb.append(',');
        sb.append("objects");
        sb.append('=');
        sb.append(((this.objects == null)?"<null>":this.objects));
        sb.append(',');
        sb.append("useobjects");
        sb.append('=');
        sb.append(((this.useobjects == null)?"<null>":this.useobjects));
        sb.append(',');
        if (sb.charAt((sb.length()- 1)) == ',') {
            sb.setCharAt((sb.length()- 1), ']');
        } else {
            sb.append(']');
        }
        return sb.toString();
    }

    @Override
    public int hashCode() {
        int result = 1;
        result = ((result* 31)+((this.name == null)? 0 :this.name.hashCode()));
        result = ((result* 31)+((this.useobjects == null)? 0 :this.useobjects.hashCode()));
        result = ((result* 31)+((this.objects == null)? 0 :this.objects.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof Defobject) == false) {
            return false;
        }
        Defobject rhs = ((Defobject) other);
        return ((((this.name == rhs.name)||((this.name!= null)&&this.name.equals(rhs.name)))&&((this.useobjects == rhs.useobjects)||((this.useobjects!= null)&&this.useobjects.equals(rhs.useobjects))))&&((this.objects == rhs.objects)||((this.objects!= null)&&this.objects.equals(rhs.objects))));
    }

}
