package org.ubp.ent.backend.core.model.classroom;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Objects;
import org.apache.commons.lang3.StringUtils;
import org.ubp.ent.backend.core.model.classroom.equipement.RoomEquipment;
import org.ubp.ent.backend.core.model.type.ClassroomType;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Anthony on 20/11/2015.
 */
public class Classroom {

    private final String name;
    private final RoomCapacity roomCapacity;
    private final Set<RoomEquipment> equipments = new HashSet<>();
    private final Set<ClassroomType> types;
    private Long id;

    @JsonCreator
    public Classroom(@JsonProperty("name") final String name, @JsonProperty("roomCapacity") final RoomCapacity roomCapacity, @JsonProperty("types") final Set<ClassroomType> types) {
        if (StringUtils.isBlank(name)) {
            throw new IllegalArgumentException("Cannot build a " + getClass().getName() + " without a name.");
        }
        if (roomCapacity == null) {
            throw new IllegalArgumentException("Cannot build a " + getClass().getName() + " without a " + RoomCapacity.class.getName());
        }
        if (types == null || types.size() == 0) {
            throw new IllegalArgumentException("Cannot build a " + getClass().getName() + " with a null or empty list of " + ClassroomType.class.getName());
        }

        this.name = name;
        this.roomCapacity = roomCapacity;
        this.types = types;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public RoomCapacity getRoomCapacity() {
        return roomCapacity;
    }

    public Set<RoomEquipment> getEquipments() {
        return Collections.unmodifiableSet(equipments);
    }

    public Set<ClassroomType> getTypes() {
        return types;
    }

    public void addEquipment(RoomEquipment equipment) {
        if (equipment == null) {
            throw new IllegalArgumentException("Cannot add a null " + RoomEquipment.class.getName() + " to a " + getClass().getName());
        }
        this.equipments.add(equipment);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Classroom classroom = (Classroom) o;
        return Objects.equal(name, classroom.name);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name);
    }

}
