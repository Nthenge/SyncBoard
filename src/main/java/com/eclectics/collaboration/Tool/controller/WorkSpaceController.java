package com.eclectics.collaboration.Tool.controller;

import com.eclectics.collaboration.Tool.dto.WorkSpaceRequestDTO;
import com.eclectics.collaboration.Tool.model.User;
import com.eclectics.collaboration.Tool.model.WorkSpace;
import com.eclectics.collaboration.Tool.security.CustomUserDetails;
import com.eclectics.collaboration.Tool.service.WorkSpaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/workspace")
public class WorkSpaceController {

    @Autowired
    private final WorkSpaceService workSpaceService;

    @PostMapping("/create")
    public ResponseEntity<WorkSpace> createWorkSpace(
            @RequestBody WorkSpaceRequestDTO requestDTO,
            @AuthenticationPrincipal CustomUserDetails userDetails){
        User user = userDetails.getUser();
        WorkSpace response = workSpaceService.createWorkspace(user,requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
