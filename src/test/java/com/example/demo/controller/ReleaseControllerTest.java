package com.example.demo.controller;

import com.example.demo.dto.ReleaseDTO;
import com.example.demo.service.ReleaseService;
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

@WebMvcTest(ReleaseController.class)
class ReleaseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReleaseService releaseService;

    @Test
    @DisplayName("GET /api/releases returns all releases")
    void testGetAllReleases() throws Exception {
        ReleaseDTO dto1 = new ReleaseDTO(1L, "v1.0", LocalDate.now());
        ReleaseDTO dto2 = new ReleaseDTO(2L, "v2.0", LocalDate.now().plusDays(1));
        Mockito.when(releaseService.getAllReleases()).thenReturn(Arrays.asList(dto1, dto2));

        mockMvc.perform(get("/api/releases"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].version").value("v1.0"))
                .andExpect(jsonPath("$[1].id").value(2L));
    }

    @Test
    @DisplayName("GET /api/releases/{id} returns release if found")
    void testGetReleaseByIdFound() throws Exception {
        ReleaseDTO dto = new ReleaseDTO(1L, "v1.0", LocalDate.now());
        Mockito.when(releaseService.getReleaseById(1L)).thenReturn(Optional.of(dto));

        mockMvc.perform(get("/api/releases/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.version").value("v1.0"));
    }

    @Test
    @DisplayName("GET /api/releases/{id} returns 404 if not found")
    void testGetReleaseByIdNotFound() throws Exception {
        Mockito.when(releaseService.getReleaseById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/releases/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /api/releases creates release")
    void testCreateRelease() throws Exception {
        ReleaseDTO input = new ReleaseDTO(null, "v1.0", LocalDate.now());
        ReleaseDTO saved = new ReleaseDTO(1L, "v1.0", LocalDate.now());
        Mockito.when(releaseService.createRelease(any(ReleaseDTO.class))).thenReturn(saved);

        String json = "{"id":null,"version":"v1.0","releaseDate":"" + LocalDate.now() + ""}";
        mockMvc.perform(post("/api/releases")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.version").value("v1.0"));
    }

    @Test
    @DisplayName("PUT /api/releases/{id} updates release if found")
    void testUpdateReleaseFound() throws Exception {
        ReleaseDTO input = new ReleaseDTO(null, "v2.0", LocalDate.now());
        ReleaseDTO updated = new ReleaseDTO(1L, "v2.0", LocalDate.now());
        Mockito.when(releaseService.updateRelease(eq(1L), any(ReleaseDTO.class))).thenReturn(Optional.of(updated));

        String json = "{"id":null,"version":"v2.0","releaseDate":"" + LocalDate.now() + ""}";
        mockMvc.perform(put("/api/releases/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.version").value("v2.0"));
    }

    @Test
    @DisplayName("PUT /api/releases/{id} returns 404 if not found")
    void testUpdateReleaseNotFound() throws Exception {
        Mockito.when(releaseService.updateRelease(eq(1L), any(ReleaseDTO.class))).thenReturn(Optional.empty());
        String json = "{"id":null,"version":"v2.0","releaseDate":"" + LocalDate.now() + ""}";
        mockMvc.perform(put("/api/releases/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /api/releases/{id} deletes release if found")
    void testDeleteReleaseFound() throws Exception {
        Mockito.when(releaseService.deleteRelease(1L)).thenReturn(true);
        mockMvc.perform(delete("/api/releases/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /api/releases/{id} returns 404 if not found")
    void testDeleteReleaseNotFound() throws Exception {
        Mockito.when(releaseService.deleteRelease(1L)).thenReturn(false);
        mockMvc.perform(delete("/api/releases/1"))
                .andExpect(status().isNotFound());
    }
}
