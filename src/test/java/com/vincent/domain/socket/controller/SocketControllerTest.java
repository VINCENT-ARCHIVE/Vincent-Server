package com.vincent.domain.socket.controller;

import static org.mockito.Mockito.when;

import com.vincent.apipayload.ApiResponse;
import com.vincent.config.security.principal.PrincipalDetails;
import com.vincent.domain.bookmark.service.BookmarkService;
import com.vincent.domain.building.controller.dto.BuildingResponseDto;
import com.vincent.domain.building.controller.dto.BuildingResponseDto.SpaceInfoProjection;
import com.vincent.domain.building.converter.BuildingConverter;
import com.vincent.domain.building.entity.Building;
import com.vincent.domain.building.entity.Floor;
import com.vincent.domain.building.entity.Space;
import com.vincent.domain.member.entity.Member;
import com.vincent.domain.socket.controller.dto.SocketResponseDto;
import com.vincent.domain.socket.controller.dto.SocketResponseDto.SocketInfo;
import com.vincent.domain.socket.controller.dto.SocketResponseDto.SocketPlace;
import com.vincent.domain.socket.converter.SocketConverter;
import com.vincent.domain.socket.entity.Socket;
import com.vincent.domain.socket.service.SocketService;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

@ExtendWith(MockitoExtension.class)
public class SocketControllerTest {

    @Mock
    private SocketService socketService;

    @Mock
    private BookmarkService bookmarkService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private SocketController socketController;

    private PrincipalDetails principalDetails;

    private Socket socket;

    private SocketPlace socketPlace;

    @BeforeEach
    public void setUp() {
        Member member = Member.builder().id(1L).build();
        principalDetails = new PrincipalDetails(member);
        Building building = Building.builder().id(1L).name("building1").build();
        Floor floor = Floor.builder().id(1L).building(building).build();
        Space space = Space.builder().id(1L).floor(floor).name("floor1").build();
        socket = Socket.builder().id(1L).space(space).build();
        socketPlace = SocketPlace.builder().buildingId(1L).level(1).build();
    }

    @Test
    public void 개별_콘센트_조회_성공() {
        //given
        when(authentication.getPrincipal()).thenReturn(principalDetails);
        when(bookmarkService.getBookmarkExist(1L, 1L)).thenReturn(true);
        when(socketService.getSocketInfo(1L)).thenReturn(socket);

        //when
        ApiResponse<SocketInfo> response = socketController.socketInfo(1L, authentication);

        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getIsSuccess()).isTrue();
        Assertions.assertThat(response.getCode()).isEqualTo("COMMON200");
        Assertions.assertThat(response.getMessage()).isEqualTo("성공입니다");
        Assertions.assertThat(response.getResult().getSocketId()).isEqualTo(1L);

    }

    @Test
    public void 층소켓조회_성공() {
        //given
        List<Socket> socketList = List.of(socket);
        //when
        when(socketService.getSocketList(1L, 2)).thenReturn(socketList);

        //when
        ApiResponse<SocketResponseDto.SocketLocationList> response = socketController.getSocketLocationList(
            1L, 2);

        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getIsSuccess()).isTrue();
        Assertions.assertThat(response.getCode()).isEqualTo("COMMON200");
        Assertions.assertThat(response.getMessage()).isEqualTo("성공입니다");
        Assertions.assertThat(response.getResult().getLocationList().size()).isEqualTo(1);
    }

//    @Test
//    public void 소켓장소조회_성공() {
//
//        //given
//        Long socketId = 1L;
//
//        //when
//        when(socketService.getSocketPlace(socketId)).thenReturn(socketPlace);
//
//        //then
//        ApiResponse<SocketResponseDto.SocketPlace> response = socketController.getSocketPlace(socketId);
//        SocketResponseDto.SocketPlace result = response.getResult();
//        SocketResponseDto.SocketPlace expected = SocketConverter.toSocketPlace(
//            socketPlace);
//
//        Assertions.assertThat(response).isNotNull();
//        Assertions.assertThat(response.getCode()).isEqualTo("COMMON200");
//        Assertions.assertThat(response.getMessage()).isEqualTo("성공입니다");
//        Assertions.assertThat(result.getBuildingId()).isEqualTo(expected.getBuildingId());
//        Assertions.assertThat(result.getLevel()).isEqualTo(expected.getLevel());
//
//    }

}
