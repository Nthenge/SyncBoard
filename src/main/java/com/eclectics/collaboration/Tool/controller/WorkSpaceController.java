package com.eclectics.collaboration.Tool.controller;

import com.eclectics.collaboration.Tool.dto.*;
import com.eclectics.collaboration.Tool.model.User;
import com.eclectics.collaboration.Tool.response.ResponseHandler;
import com.eclectics.collaboration.Tool.security.CustomUserDetails;
import com.eclectics.collaboration.Tool.service.EmailService;
import com.eclectics.collaboration.Tool.service.InvitationService;
import com.eclectics.collaboration.Tool.service.WorkSpaceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/workspace")
@Tag(name = "Workspaces", description = "Operations for handling shared workspaces and user invitations")
public class WorkSpaceController {

    private final WorkSpaceService workSpaceService;
    private final EmailService emailService;
    private final InvitationService invitationService;
    private final HttpServletRequest request;

    @Operation(summary = "Create a new workspace")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Workspace created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request configuration")
    })
    @PostMapping("/create")
    public ResponseEntity<Object> createWorkSpace(
            @RequestBody WorkSpaceRequestDTO requestDTO,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        User user = userDetails.getUser();
        WorkSpaceResponseDTO response = workSpaceService.createWorkspace(user, requestDTO);
        return ResponseHandler.generateResponse("Workspace created", HttpStatus.CREATED, response, request.getRequestURI());
    }

    @Operation(summary = "Delete an existing workspace by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Workspace deleted successfully"),
            @ApiResponse(responseCode = "403", description = "Forbidden - user is not the workspace owner"),
            @ApiResponse(responseCode = "404", description = "Workspace not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteWorkSpace(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        workSpaceService.deleteWorkspace(id, userDetails.getUser());
        return ResponseHandler.generateResponse("Workspace deleted successfully", HttpStatus.OK, null, request.getRequestURI());
    }

    @Operation(summary = "Invite workmates to join a specific workspace via email")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Invitations dispatched successfully"),
            @ApiResponse(responseCode = "403", description = "Forbidden - missing required permissions"),
            @ApiResponse(responseCode = "404", description = "Workspace not found")
    })
    @PostMapping("/{workspaceId}/invite")
    public ResponseEntity<Object> inviteWorkmates(
            @PathVariable Long workspaceId,
            @RequestBody @Valid InviteRequestDTO inviteRequest,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            HttpServletRequest request) throws AccessDeniedException {

        emailService.inviteUsers(userDetails.getUser(), inviteRequest, workspaceId);

        List<String> emails = inviteRequest.getInvitations().stream()
                .map(InviteRequestDTO.InviteeDTO::getEmail)
                .toList();
        String message = "Invitations sent successfully to: " + String.join(", ", emails);
        return ResponseHandler.generateResponse(message, HttpStatus.OK, null, request.getRequestURI());
    }

    @Operation(summary = "Accept a pending workspace invitation token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully accepted and joined the workspace"),
            @ApiResponse(responseCode = "400", description = "Invalid or expired invitation token")
    })
    @PostMapping("/accept-invite")
    public ResponseEntity<Object> acceptInvite(
            @RequestParam String token,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        invitationService.acceptInvite(token, userDetails.getUser());
        return ResponseHandler.generateResponse("Successfully joined the workspace", HttpStatus.OK, null, request.getRequestURI());
    }

    @Operation(summary = "Decline a pending workspace invitation token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Invitation declined successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid or expired invitation token")
    })
    @PostMapping("/reject-invite")
    public ResponseEntity<Object> rejectInvite(
            @RequestParam String token,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        invitationService.rejectInvite(token, userDetails.getUser());
        return ResponseHandler.generateResponse("Invitation declined successfully", HttpStatus.OK, null, request.getRequestURI());
    }

    @Operation(summary = "Get all workspaces belonging to the authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Workspaces retrieved successfully")
    })
    @GetMapping("/my-workspaces")
    public ResponseEntity<Object> getMyWorkspaces() {
        return ResponseHandler.generateResponse("Workspaces for logged in user", HttpStatus.OK,
                workSpaceService.myWorkspaces(), request.getRequestURI());
    }

    @Operation(summary = "Cancel or delete a sent invitation")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Invitation canceled successfully"),
            @ApiResponse(responseCode = "403", description = "Forbidden - missing tracking permissions"),
            @ApiResponse(responseCode = "404", description = "Invitation tracking ID not found")
    })

    @DeleteMapping("/invitations/{invitationId}")
    public ResponseEntity<Object> deleteInvitation(
            @PathVariable Long invitationId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        invitationService.deleteInvitation(invitationId, userDetails.getUser());
        return ResponseHandler.generateResponse("Invitation deleted successfully", HttpStatus.OK, null, request.getRequestURI());
    }

    @Operation(summary = "Get a list of all out-standing invitations sent from a specific workspace")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Workspace invitations list fetched successfully"),
            @ApiResponse(responseCode = "403", description = "Forbidden - unauthorized view profile access"),
            @ApiResponse(responseCode = "404", description = "Workspace ID not found")
    })
    @GetMapping("/{workspaceId}/invitations")
    public ResponseEntity<Object> getWorkspaceInvitations(
            @PathVariable Long workspaceId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        List<InvitationResponseDTO> invitations = invitationService.getWorkspaceInvitations(workspaceId, userDetails.getUser());
        return ResponseHandler.generateResponse("Workspace invitations fetched", HttpStatus.OK, invitations, request.getRequestURI());
    }

    @Operation(summary = "Get all pending invitations addressed to the authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pending invitations fetched successfully")
    })
    @GetMapping("/my-invitations")
    public ResponseEntity<Object> getMyInvitations(@AuthenticationPrincipal CustomUserDetails userDetails) {
        List<MyInvitationResponseDTO> invitations = invitationService.getMyInvitations(userDetails.getUser());
        return ResponseHandler.generateResponse("Your pending invitations", HttpStatus.OK, invitations, request.getRequestURI());
    }

    @Operation(summary = "Toggle star on a workspace for the current user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Star toggled successfully"),
            @ApiResponse(responseCode = "404", description = "Workspace not found")
    })
    @PostMapping("/{workspaceId}/star")
    public ResponseEntity<Object> toggleStarWorkspace(
            @PathVariable Long workspaceId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        boolean starred = workSpaceService.toggleStarWorkspace(workspaceId, userDetails.getUser().getId());
        return ResponseHandler.generateResponse(
                starred ? "Workspace starred" : "Workspace unstarred",
                HttpStatus.OK,
                java.util.Map.of("starred", starred),
                request.getRequestURI()
        );
    }

    @Operation(summary = "Get all workspaces starred by the current user")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Starred workspaces fetched successfully") })
    @GetMapping("/starred")
    public ResponseEntity<Object> getStarredWorkspaces(@AuthenticationPrincipal CustomUserDetails userDetails) {
        List<WorkSpaceResponseDTO> workspaces = workSpaceService.getStarredWorkspaces(userDetails.getUser().getId());
        return ResponseHandler.generateResponse("Starred workspaces fetched", HttpStatus.OK, workspaces, request.getRequestURI());
    }

    @Operation(summary = "Leave a workspace: owner leaving deletes the workspace; member leaving just removes their membership")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Left workspace successfully"),
            @ApiResponse(responseCode = "403", description = "Not a member of this workspace"),
            @ApiResponse(responseCode = "404", description = "Workspace not found")
    })
    @PostMapping("/{workspaceId}/leave")
    public ResponseEntity<Object> leaveWorkspace(
            @PathVariable Long workspaceId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        workSpaceService.leaveWorkspace(workspaceId, userDetails.getUser());
        return ResponseHandler.generateResponse("Left workspace successfully", HttpStatus.OK, null, request.getRequestURI());
    }
    @Operation(summary = "Update a workspace's name and/or description")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Workspace updated successfully"),
            @ApiResponse(responseCode = "403", description = "Forbidden - user is not the workspace owner"),
            @ApiResponse(responseCode = "404", description = "Workspace not found")
    })
    @PutMapping("/{workspaceId}")
    public ResponseEntity<Object> updateWorkspace(
            @PathVariable Long workspaceId,
            @RequestBody WorkSpaceRequestDTO requestDTO,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        WorkSpaceResponseDTO response = workSpaceService.updateWorkspace(workspaceId, userDetails.getUser(), requestDTO);
        return ResponseHandler.generateResponse("Workspace updated successfully", HttpStatus.OK, response, request.getRequestURI());
    }
}