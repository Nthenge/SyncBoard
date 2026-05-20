package com.eclectics.collaboration.Tool.controller;

import com.eclectics.collaboration.Tool.dto.InvitationResponseDTO;
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
import jakarta.validation.Valid;
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

    private final WorkSpaceService workSpaceService;
    private final EmailService emailService;
    private final InvitationService invitationService;
    private final HttpServletRequest request;

    @PostMapping("/create")
    public ResponseEntity<Object> createWorkSpace(
            @RequestBody WorkSpaceRequestDTO requestDTO,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        User user = userDetails.getUser();
        WorkSpaceResponseDTO response = workSpaceService.createWorkspace(user, requestDTO);  // now uses DTO
        return ResponseHandler.generateResponse("Workspace created", HttpStatus.CREATED, response, request.getRequestURI());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteWorkSpace(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        workSpaceService.deleteWorkspace(id, userDetails.getUser());
        return ResponseHandler.generateResponse("Workspace deleted successfully", HttpStatus.OK, null, request.getRequestURI());
    }

    @PostMapping("/{workspaceId}/invite")
    public ResponseEntity<Object> inviteWorkmates(
            @PathVariable Long workspaceId,
            @RequestBody @Valid InviteRequestDTO inviteRequest,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            HttpServletRequest request) throws AccessDeniedException {

        emailService.inviteUsers(userDetails.getUser(), inviteRequest, workspaceId);

        String message = "Invitations sent successfully to: " + String.join(", ", inviteRequest.getEmails());
        return ResponseHandler.generateResponse(message, HttpStatus.OK, null, request.getRequestURI());
    }

    @PostMapping("/accept-invite")
    public ResponseEntity<Object> acceptInvite(
            @RequestParam String token,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        invitationService.acceptInvite(token, userDetails.getUser());
        return ResponseHandler.generateResponse("Successfully joined the workspace", HttpStatus.OK, null, request.getRequestURI());
    }

    @PostMapping("/reject-invite")
    public ResponseEntity<Object> rejectInvite(
            @RequestParam String token,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        invitationService.rejectInvite(token, userDetails.getUser());
        return ResponseHandler.generateResponse("Invitation declined successfully", HttpStatus.OK, null, request.getRequestURI());
    }

    @GetMapping("/my-workspaces")
    public ResponseEntity<Object> getMyWorkspaces() {
        return ResponseHandler.generateResponse("Workspaces for logged in user", HttpStatus.OK,
                workSpaceService.myWorkspaces(), request.getRequestURI());
    }

    @DeleteMapping("/invitations/{invitationId}")
    public ResponseEntity<Object> deleteInvitation(
            @PathVariable Long invitationId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        invitationService.deleteInvitation(invitationId, userDetails.getUser());
        return ResponseHandler.generateResponse("Invitation deleted successfully", HttpStatus.OK, null, request.getRequestURI());
    }

    @GetMapping("/{workspaceId}/invitations")           // fixed — was /workspaces/{id} inside a /workspace mapping, making the full path /workspace/workspaces/{id}
    public ResponseEntity<Object> getWorkspaceInvitations(
            @PathVariable Long workspaceId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        List<InvitationResponseDTO> invitations = invitationService.getWorkspaceInvitations(workspaceId, userDetails.getUser());
        return ResponseHandler.generateResponse("Workspace invitations fetched", HttpStatus.OK, invitations, request.getRequestURI());
    }
}
