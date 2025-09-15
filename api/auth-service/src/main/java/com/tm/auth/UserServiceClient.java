package com.tm.auth;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "user-service")
public interface UserServiceClient {
    
    @PostMapping("/api/users/validate")
    Boolean validateUser(@RequestBody ValidateUserRequest request);
    
    class ValidateUserRequest {
        private String username;
        private String password;
        
        public ValidateUserRequest() {}
        
        public ValidateUserRequest(String username, String password) {
            this.username = username;
            this.password = password;
        }
        
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }
}