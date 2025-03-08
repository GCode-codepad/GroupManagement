package org.example.groupmanageservice.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*; 
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*; 
import static org.hamcrest.Matchers.*;

import org.aspectj.lang.annotation.Before;
import org.example.groupmanageservice.GroupManageServiceApplication; 
import org.example.groupmanageservice.domain.Room; 
import org.example.groupmanageservice.dto.NewRoomReq; 
import org.example.groupmanageservice.dto.RoomDTO; 
import org.example.groupmanageservice.service.RoomService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach; 
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gs.fw.common.mithra.test.ConnectionManagerForTests;
import com.gs.fw.common.mithra.test.MithraTestResource;

import java.util.Arrays; 
import java.util.List;

@SpringBootTest(classes = GroupManageServiceApplication.class) 
@AutoConfigureMockMvc 
@ActiveProfiles("test") 
public class RoomControllerIntegrationTest {

  @Autowired
  private MockMvc mockMvc;
  
  @Autowired
  private ObjectMapper objectMapper;
  
  // // 改为真实 Bean，Reladomo 测试时无需模拟 RoomService
  // @Autowired
  // private RoomService roomService;
  
  // // 如果有 UserServiceClient 需要真实交互，则也注入真实 Bean
  // @Autowired
  // private /*真实类型*/ Object userServiceClient;

  @BeforeAll
  static void setUpAll() {
    // 读取测试配置文件
    MithraTestResource testResource = new MithraTestResource("reladomo/TestRuntimeConfiguration.xml");
    ConnectionManagerForTests connectionManager = ConnectionManagerForTests.getInstance("test_db");
    testResource.createSingleDatabase(connectionManager, "reladomo/TestData.txt");
    testResource.setUp();
  }
  
  private String validHosterId;
  private String validUserId;
  // 在 Reladomo 测试中，Room ID 通常由持久化逻辑生成，不再固定
  @BeforeEach
  void setUp() {
      validHosterId = "aaec1914-f756-4667-99b4-7eba0ef95a61";
      validUserId = "112c9e1c-ce81-47e0-b817-d50a205809b4";
  }
  
  @Test
  void createRoom_WithValidHoster_ShouldReturnCreated() throws Exception {
      // Arrange
      NewRoomReq request = new NewRoomReq();
      request.setHosterUserId(validHosterId);
  
      // Act & Assert
      mockMvc.perform(post("/api/v1/rooms")
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(request)))
              .andDo((r) -> {
                System.out.print(r);
                System.out.println("createRoom_WithValidHoster_ShouldReturnCreated: " + r);
              })
          .andExpect(status().isCreated())
          .andExpect(jsonPath("$.roomId", notNullValue()))
          // 断言 joinPassword 非空，可根据实际返回格式更改校验
          .andExpect(jsonPath("$.joinPassword", notNullValue()));
  }
  
  @Test
  void getRoom_WithValidRoomId_ShouldReturnRoom() throws Exception {
      // 先创建一个房间，获取真实 roomId
      NewRoomReq request = new NewRoomReq();
      request.setHosterUserId(validHosterId);
      String responseBody = mockMvc.perform(post("/api/v1/rooms")
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isCreated())
          .andReturn().getResponse().getContentAsString();
  
      // 解析返回的 roomId
      String createdRoomId = objectMapper.readTree(responseBody).get("roomId").asText();
  
      // Act & Assert 获取房间详情
      mockMvc.perform(get("/api/v1/rooms/{roomId}", createdRoomId))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.roomId", is(createdRoomId)))
          .andExpect(jsonPath("$.hosterUserId", is(validHosterId)));
  }
  
  @Test
  void closeRoom_WithValidHoster_ShouldReturnOk() throws Exception {
      // 先创建一个房间
      NewRoomReq request = new NewRoomReq();
      request.setHosterUserId(validHosterId);
      String responseBody = mockMvc.perform(post("/api/v1/rooms")
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isCreated())
          .andReturn().getResponse().getContentAsString();
  
      String createdRoomId = objectMapper.readTree(responseBody).get("roomId").asText();
  
      // Arrange
      String requestBody = "{\"requestingUserId\": \"" + validHosterId + "\"}";
  
      // Act & Assert
      mockMvc.perform(post("/api/v1/rooms/{roomId}/close", createdRoomId)
              .contentType(MediaType.APPLICATION_JSON)
              .content(requestBody))
          .andExpect(status().isOk());
  }
  
  @Test
  void listRooms_ShouldReturnRoomsList() throws Exception {
      // 先创建两间房间
      NewRoomReq req1 = new NewRoomReq();
      req1.setHosterUserId(validHosterId);
      NewRoomReq req2 = new NewRoomReq();
      req2.setHosterUserId(validUserId);
  
      mockMvc.perform(post("/api/v1/rooms")
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(req1)))
          .andExpect(status().isCreated());
      mockMvc.perform(post("/api/v1/rooms")
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(req2)))
          .andExpect(status().isCreated());
  
      // Act & Assert
      mockMvc.perform(get("/api/v1/rooms"))
          .andExpect(status().isOk())
          // 根据测试数据可能返回更多记录，以至少两个为准
          .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(2))));
  }
  
  @Test
  void joinRoom_WithValidCredentials_ShouldReturnOk() throws Exception {
      // 先创建房间
      NewRoomReq request = new NewRoomReq();
      request.setHosterUserId(validHosterId);
      String responseBody = mockMvc.perform(post("/api/v1/rooms")
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isCreated())
          .andReturn().getResponse().getContentAsString();
      String createdRoomId = objectMapper.readTree(responseBody).get("roomId").asText();
  
      // Arrange
      String requestBody = "{\"userId\": \"" + validUserId + "\", \"joinPassword\": \"test-password\"}";
  
      // Act & Assert
      mockMvc.perform(post("/api/v1/rooms/{roomId}/join", createdRoomId)
              .contentType(MediaType.APPLICATION_JSON)
              .content(requestBody))
          .andExpect(status().isOk());
  }
  
  @Test
  void joinRoom_WithInvalidPassword_ShouldReturnBadRequest() throws Exception {
      // 先创建房间
      NewRoomReq request = new NewRoomReq();
      request.setHosterUserId(validHosterId);
      String responseBody = mockMvc.perform(post("/api/v1/rooms")
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isCreated())
          .andReturn().getResponse().getContentAsString();
      String createdRoomId = objectMapper.readTree(responseBody).get("roomId").asText();
  
      // Arrange
      String requestBody = "{\"userId\": \"" + validUserId + "\", \"joinPassword\": \"wrong-password\"}";
  
      // Act & Assert
      mockMvc.perform(post("/api/v1/rooms/{roomId}/join", createdRoomId)
              .contentType(MediaType.APPLICATION_JSON)
              .content(requestBody))
          .andExpect(status().isBadRequest());
  }
}