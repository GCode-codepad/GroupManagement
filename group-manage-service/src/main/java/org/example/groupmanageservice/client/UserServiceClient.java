package org.example.groupmanageservice.client;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.example.groupmanageservice.enums.Roles;

public class UserServiceClient {

    public static class User {
        private String id;
        private String name;
        private List<Roles> roles;

        public User(String id, String name, List<Roles> roles) {
            this.id = id;
            this.name = name;
            this.roles = roles;
        }
        // ...getter and setter...
        public String getId() { return id; }
        public String getName() { return name; }
        public List<Roles> getRoles() { return roles; }
    }

    private Map<String, User> users = new HashMap<>();

    public UserServiceClient(){
        users.put("748f10de-d090-423a-874a-a0f31b408459", new User("748f10de-d090-423a-874a-a0f31b408459", "user1", List.of(Roles.SYSTEM_ADMIN)));
        users.put("aaec1914-f756-4667-99b4-7eba0ef95a61", new User("aaec1914-f756-4667-99b4-7eba0ef95a61", "user2", List.of(Roles.HOSTER)));
        users.put("112c9e1c-ce81-47e0-b817-d50a205809b4", new User("112c9e1c-ce81-47e0-b817-d50a205809b4", "user3", List.of(Roles.REGULAR_USER)));
        users.put("62674e84-4e68-4416-88b6-ef09e7352d07", new User("62674e84-4e68-4416-88b6-ef09e7352d07", "user4", List.of(Roles.HOSTER, Roles.SYSTEM_ADMIN)));
        users.put("3db25323-cb2e-4c29-beff-5e462514a76b", new User("3db25323-cb2e-4c29-beff-5e462514a76b", "user5", List.of(Roles.REGULAR_USER)));
        users.put("76c34155-9e4f-4c38-a080-fc4c84d0706c", new User("76c34155-9e4f-4c38-a080-fc4c84d0706c", "user6", List.of(Roles.REGULAR_USER)));
        users.put("78ea6b11-2d7d-44d1-8d57-93b336e4561f", new User("78ea6b11-2d7d-44d1-8d57-93b336e4561f", "user7", List.of(Roles.REGULAR_USER)));
        users.put("0d81e2ae-86ad-4ec2-8116-2a9064326ef9", new User("0d81e2ae-86ad-4ec2-8116-2a9064326ef9", "user8", List.of(Roles.REGULAR_USER)));
    }


    public User getUserById(String userId) {
        // 模拟获取用户信息，返回带有角色信息的用户数据
        if(userId == null || userId.trim().isEmpty() || users.containsKey(userId) == false){
            return null;
        }

        User user = users.get(userId);
        
        return user;
    }
}
