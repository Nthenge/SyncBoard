package com.eclectics.collaboration.Tool.controller;

import com.eclectics.collaboration.Tool.dto.InviteRequestDTO;
import com.eclectics.collaboration.Tool.dto.WorkSpaceRequestDTO;
import com.eclectics.collaboration.Tool.dto.WorkSpaceResponseDTO;
import com.eclectics.collaboration.Tool.model.User;
import com.eclectics.collaboration.Tool.model.WorkSpace;
import com.eclectics.collaboration.Tool.response.ResponseHandler;
import com.eclectics.collaboration.Tool.security.CustomUserDetails;
import com.eclectics.collaboration.Tool.service.EmailService;
import com.eclectics.collaboration.Tool.service.InvitationService;
import com.eclectics.collaboration.Tool.service.WorkSpaceService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/workspace")
public class WorkSpaceController {

    @Autowired
    private final WorkSpaceService workSpaceService;
    private final EmailService emailService;
    private final InvitationService invitationService;
    private final HttpServletRequest request;

    @PostMapping("/create")
    public ResponseEntity<Object> createWorkSpace(
            @RequestBody WorkSpaceRequestDTO requestDTO,
            @AuthenticationPrincipal CustomUserDetails userDetails){
        User user = userDetails.getUser();
        WorkSpace response = workSpaceService.createWorkspace(user,requestDTO);
        return ResponseHandler.generateResponse("Work space Created", HttpStatus.CREATED,response, request.getRequestURI());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteWorkSpace(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        User user = userDetails.getUser();

        workSpaceService.deleteWorkspace(id, user);

        return ResponseHandler.generateResponse("Workspace deleted successfully", HttpStatus.OK, null, request.getRequestURI());
    }

    @PostMapping("/invite")
    public ResponseEntity<Object> inviteWorkmates(
            @RequestBody InviteRequestDTO inviteRequest,
            @AuthenticationPrincipal CustomUserDetails userDetails) throws AccessDeniedException {

        User owner = userDetails.getUser();

        emailService.inviteUsers(owner, inviteRequest);

        String message = "Invitations sent successfully to: " + String.join(", ", inviteRequest.getEmails());

        return ResponseHandler.generateResponse(
                message,
                HttpStatus.OK,
                null,
                request.getRequestURI()
        );
    }

    @PostMapping("/accept-invite")
    public ResponseEntity<Object> acceptInvite(
            @RequestParam String token,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        User invitee = userDetails.getUser();

        invitationService.acceptInvite(token, invitee);

        return ResponseHandler.generateResponse("Successfully joined the workspace",HttpStatus.ACCEPTED,null, request.getRequestURI());
    }

    @GetMapping("/my-workspaces")
    public ResponseEntity<Object> getMyWorkspaces() {
        List<WorkSpaceResponseDTO> workspaces = workSpaceService.myWorkspaces();
        return ResponseHandler.generateResponse("Work spaces for logged in user", HttpStatus.OK,workspaces,request.getRequestURI());
    }

}
