package org.yang1.eapproval.user.presentation.api;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.yang1.eapproval.user.application.service.UserService;
import org.yang1.eapproval.user.presentation.dto.request.UserSaveRequest;
import org.yang1.eapproval.user.presentation.dto.response.UserResponse;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserApiController {

    private final UserService userService;



    /**
     * 사용자 단건 조회
     *
     * @param userId
     * @return
     */
    @GetMapping("/users/{userId}")
    public ResponseEntity<UserResponse> getUser(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.findUserById(userId));
    }


    /**
     * 사용자 등록
     *
     * @param request
     * @return
     */
    @PostMapping("/users")
    public ResponseEntity<UserResponse> saveUser(@RequestBody @Valid UserSaveRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.saveUser(request.toCommand()));
    }


}
