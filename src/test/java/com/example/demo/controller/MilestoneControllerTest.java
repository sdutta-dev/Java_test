package com.example.demo.controller;

import com.example.demo.dto.MilestoneDTO;
import com.example.demo.service.MilestoneService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MilestoneController.class)
class MilestoneControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MilestoneService milestoneService;

    @Test
    @DisplayName("GET /api/milestones returns all milestones")
    void testGetAllMilestones() throws Exception {
        MilestoneDTO dto1 = new MilestoneDTO(1L, "Milestone 1", LocalDate.now());
        MilestoneDTO dto2 = new MilestoneDTO(2L, "Milestone 2", LocalDate.now().plusDays(1));
        Mockito.when(milestoneService.getAllMilestones()).thenReturn(Arrays.asList(dto1, dto2));

        mockMvc.perform(get("/api/milestones"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Milestone 1"))
                .andExpect(jsonPath("$[1].id").value(2L));
    }

    @Test
    @DisplayName("GET /api/milestones/{id} returns milestone if found")
    void testGetMilestoneByIdFound() throws Exception {
        MilestoneDTO dto = new MilestoneDTO(1L, "Milestone 1", LocalDate.now());
        Mockito.when(milestoneService.getMilestoneById(1L)).thenReturn(Optional.of(dto));

        mockMvc.perform(get("/api/milestones/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Milestone 1"));
    }

    @Test
    @DisplayName("GET /api/milestones/{id} returns 404 if not found")
    void testGetMilestoneByIdNotFound() throws Exception {
        Mockito.when(milestoneService.getMilestoneById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/milestones/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /api/milestones creates milestone")
    void testCreateMilestone() throws Exception {
        MilestoneDTO input = new MilestoneDTO(null, "Milestone 1", LocalDate.now());
        MilestoneDTO saved = new MilestoneDTO(1L, "Milestone 1", LocalDate.now());
        Mockito.when(milestoneService.createMilestone(any(MilestoneDTO.class))).thenReturn(saved);

        String json = "{"id":null,"name":"Milestone 1","dueDate":"" + LocalDate.now() + ""}";
        mockMvc.perform(post("/api/milestones")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Milestone 1"));
    }

    @Test
    @DisplayName("PUT /api/milestones/{id} updates milestone if found")
    void testUpdateMilestoneFound() throws Exception {
        MilestoneDTO input = new MilestoneDTO(null, "Updated", LocalDate.now());
        MilestoneDTO updated = new MilestoneDTO(1L, "Updated", LocalDate.now());
        Mockito.when(milestoneService.updateMilestone(eq(1L), any(MilestoneDTO.class))).thenReturn(Optional.of(updated));

        String json = "{"id":null,"name":"Updated","dueDate":"" + LocalDate.now() + ""}";
        mockMvc.perform(put("/api/milestones/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated"));
    }

    @Test
    @DisplayName("PUT /api/milestones/{id} returns 404 if not found")
    void testUpdateMilestoneNotFound() throws Exception {
        Mockito.when(milestoneService.updateMilestone(eq(1L), any(MilestoneDTO.class))).thenReturn(Optional.empty());
        String json = "{"id":null,"name":"Updated","dueDate":"" + LocalDate.now() + ""}";
        mockMvc.perform(put("/api/milestones/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /api/milestones/{id} deletes milestone if found")
    void testDeleteMilestoneFound() throws Exception {
        Mockito.when(milestoneService.deleteMilestone(1L)).thenReturn(true);
        mockMvc.perform(delete("/api/milestones/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /api/milestones/{id} returns 404 if not found")
    void testDeleteMilestoneNotFound() throws Exception {
        Mockito.when(milestoneService.deleteMilestone(1L)).thenReturn(false);
        mockMvc.perform(delete("/api/milestones/1"))
                .andExpect(status().isNotFound());
    }
}
