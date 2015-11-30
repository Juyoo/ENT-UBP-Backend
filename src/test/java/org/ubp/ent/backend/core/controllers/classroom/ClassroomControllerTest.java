package org.ubp.ent.backend.core.controllers.classroom;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.ubp.ent.backend.core.dao.repository.classroom.equipment.EquipmentTypeRepository;
import org.ubp.ent.backend.core.domains.classroom.equipement.EquipmentTypeDomain;
import org.ubp.ent.backend.core.model.classroom.Classroom;
import org.ubp.ent.backend.core.model.classroom.ClassroomTest;
import org.ubp.ent.backend.core.model.classroom.equipement.EquipmentType;
import org.ubp.ent.backend.core.model.classroom.equipement.EquipmentTypeTest;
import org.ubp.ent.backend.core.model.classroom.equipement.Quantity;
import org.ubp.ent.backend.core.model.classroom.equipement.RoomEquipment;
import org.ubp.ent.backend.utils.WebIntegrationTest;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by Anthony on 27/11/2015.
 */
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ClassroomControllerTest extends WebIntegrationTest {

    @Inject
    private EquipmentTypeRepository equipmentTypeRepository;

    @Inject
    private ObjectMapper mapper;

    private Classroom createClassroom() throws Exception {
        return createClassroom(1).get(0);
    }

    private List<Classroom> createClassroom(int count) throws Exception {
        List<Classroom> created = new ArrayList<>(count);
        for (int i = 0; i < count; ++i) {
            Classroom classroom = ClassroomTest.createOne("name " + i);
            String classroomJson = mapper.writeValueAsString(classroom);
            perform(post("/classroom").content(classroomJson).contentType(MediaType.APPLICATION_JSON_UTF8))
                    .andExpect(status().isCreated())
                    .andDo(result -> {
                        String json = result.getResponse().getContentAsString();
                        Classroom createdClassroom = mapper.readValue(json, Classroom.class);
                        created.add(createdClassroom);
                    });
        }
        return created;
    }


    @Test
    public void shouldFindClassroomById() throws Exception {
        List<Classroom> classrooms = createClassroom(5);

        for (Classroom classroom : classrooms) {
            perform(get("/classroom/" + classroom.getId()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(classroom.getId().intValue())));
        }
    }

    @Test
    public void shouldThrow404NotFoundForNonExistingClassroom() throws Exception {
        perform(get("/classroom/9945"))
                .andExpect(status().isNotFound());
    }


    @Test
    public void shouldFindAll() throws Exception {
        createClassroom(2);

        perform(get("/classroom"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    public void shouldRejectCreateIfIdIsDefined() throws Exception {
        Classroom classroom = ClassroomTest.createOne("3005");
        Long definedId = 6945L;
        classroom.setId(definedId);
        String classroomJson = mapper.writeValueAsString(classroom);

        perform(post("/classroom").content(classroomJson).contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isBadRequest());
    }


    @Test
    public void shouldNotCreateTwoClassroomWithSameId() throws Exception {
        Classroom classroom = createClassroom();

        Classroom classroom2 = ClassroomTest.createOne("a classroom name");
        classroom2.setId(classroom.getId());
        String classroomJson = mapper.writeValueAsString(classroom2);

        perform(post("/classroom").content(classroomJson).contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldNotCreateWithNull() throws Exception {
        perform(post("/classroom").contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldCreateWithValidClassroom() throws Exception {
        Classroom classroom = ClassroomTest.createOne("3005");
        String classroomJson = mapper.writeValueAsString(classroom);

        perform(post("/classroom").content(classroomJson).contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()));
    }

    @Test
    public void shouldAddRoomEquipmentToClassroom() throws Exception {
        Classroom classroom = createClassroom();

        EquipmentType equipmentType = EquipmentTypeTest.createOne("Computer");
        equipmentType = equipmentTypeRepository.saveAndFlush(new EquipmentTypeDomain(equipmentType)).toModel();

        int quantity = 12;
        perform(post("/classroom/" + classroom.getId() + "/equipment-type/" + equipmentType.getId()).param("quantity", String.valueOf(quantity)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.equipmentType.id", is(equipmentType.getId().intValue())))
                .andExpect(jsonPath("$.quantity.maxQuantity", is(quantity)));
    }


    @Test
    public void shouldNotAddTwoRoomEquipmentWithTheSameEquipmentTypeToAClassroom() throws Exception {
        Classroom classroom = createClassroom();

        EquipmentType equipmentType = EquipmentTypeTest.createOne("Computer");
        equipmentType = equipmentTypeRepository.saveAndFlush(new EquipmentTypeDomain(equipmentType)).toModel();

        RoomEquipment roomEquipment = new RoomEquipment(equipmentType, new Quantity(12));
        String roomEquipmentAsJson = mapper.writeValueAsString(roomEquipment);

        perform(post("/classroom/" + classroom.getId() + "/equipment-type/" + equipmentType.getId()).param("quantity", "12"))
                .andExpect(status().isCreated());

        perform(post("/classroom/" + classroom.getId() + "/equipment-type/" + equipmentType.getId()).param("quantity", "12"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldNotAddRoomEquipmentIfClassroomDoesNotExists() throws Exception {
        Classroom classroom = ClassroomTest.createOne("SL6");
        classroom.setId(26L);

        EquipmentType equipmentType = EquipmentTypeTest.createOne("Computer");
        equipmentType = equipmentTypeRepository.saveAndFlush(new EquipmentTypeDomain(equipmentType)).toModel();

        perform(post("/classroom/" + classroom.getId() + "/equipment-type/" + equipmentType.getId()).param("quantity", "12"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldNotAddRoomEquipmentIfEquipmentTypeDoesNotExists() throws Exception {
        Classroom classroom = createClassroom();

        EquipmentType equipmentType = EquipmentTypeTest.createOne("Computer");
        equipmentType.setId(256L);

        perform(post("/classroom/" + classroom.getId() + "/equipment-type/" + equipmentType.getId()).param("quantity", "12"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }
}