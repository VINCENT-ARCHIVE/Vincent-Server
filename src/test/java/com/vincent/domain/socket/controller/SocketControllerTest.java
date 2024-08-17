package com.vincent.domain.socket.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vincent.domain.bookmark.service.BookmarkService;
import com.vincent.domain.building.entity.Building;
import com.vincent.domain.building.entity.Floor;
import com.vincent.domain.building.entity.Space;
import com.vincent.domain.socket.controller.SocketController;
import com.vincent.domain.socket.controller.dto.SocketResponseDto;
import com.vincent.domain.socket.controller.dto.SocketResponseDto.SocketInfo;
import com.vincent.domain.socket.converter.SocketConverter;
import com.vincent.domain.socket.entity.Socket;
import com.vincent.domain.socket.service.SocketService;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.multipart.MultipartFile;

@WebMvcTest(controllers = SocketController.class)
@MockBean(JpaMetamodelMappingContext.class)
public class SocketControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SocketService socketService;

    @MockBean
    private BookmarkService bookmarkService;

    @MockBean
    private SocketConverter socketConverter;

    @Autowired
    private ObjectMapper objectMapper;

    Long socketId = 1L;
    Long memberId = 1L;

    @Test
    @WithMockUser
    public void 개별_콘센트_조회_성공() throws Exception {

        Boolean isBookmarkExist = true;

        Building building = Mockito.mock(Building.class);
        when(building.getName()).thenReturn("s1_building1");

        Floor floor = Mockito.mock(Floor.class);
        when(floor.getBuilding()).thenReturn(building);

        Space space = Mockito.mock(Space.class);
        when(space.getFloor()).thenReturn(floor);
        when(space.getName()).thenReturn("s1_floor1");

        Socket socket = Mockito.mock(Socket.class);
        when(socket.getId()).thenReturn(socketId);
        when(socket.getName()).thenReturn("s1");
        when(socket.getImage()).thenReturn("s1_image");
        when(socket.getSpace()).thenReturn(space);

        SocketResponseDto.SocketInfo socketInfo = new SocketInfo(
            socketId, "s1", "s1_image", "s1_building", " s1_floor1", true);

        when(bookmarkService.getBookmarkExist(eq(socketId), eq(memberId))).thenReturn(isBookmarkExist);
        when(socketService.getSocketInfo(eq(socketId))).thenReturn(socket);

        try (MockedStatic<SocketConverter> mockedConverter = Mockito.mockStatic(SocketConverter.class)) {
            mockedConverter.when(() -> SocketConverter.toSocketInfoResponse(socket, isBookmarkExist))
                .thenReturn(socketInfo);

            ResultActions resultActions = mockMvc.perform(get("/v1/socket/1")
                .contentType(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.csrf()));

            resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.code").value("COMMON200"))
                .andExpect(jsonPath("$.message").value("성공입니다"))
                .andExpect(jsonPath("$.result.socketId").value(socketId))
                .andExpect(jsonPath("$.result.socketName").value("s1"))
                .andExpect(jsonPath("$.result.socketImage").value("s1_image"))
                .andExpect(jsonPath("$.result.buildingName").value("s1_building1"))
                .andExpect(jsonPath("$.result.spaceName").value("s1_floor1"))
                .andExpect(jsonPath("$.result.isBookmarkExist").value(isBookmarkExist));


        }

    }

    @Test
    void 층소켓조회_성공() throws Exception {

        List<Socket> mockSocketList = new ArrayList<>();


        when(socketService.getSocketList(anyLong(), anyInt())).thenReturn(mockSocketList);
        when(socketConverter.toSocketLocationList(mockSocketList)).thenReturn(new SocketResponseDto.SocketLocationList());


        mockMvc.perform(get("/socket")
                .param("buildingId", "1")
                .param("level", "2"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("SUCCESS"));


    }
}
