package com.eclectics.collaboration.Tool.controller;

import com.eclectics.collaboration.Tool.dto.RecentBoardResponseDTO;
import com.eclectics.collaboration.Tool.response.ResponseHandler;
import com.eclectics.collaboration.Tool.service.Impl.UserRecentBoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/recent-boards")
@RequiredArgsConstructor
public class UserRecentBoardController {

    private final UserRecentBoardService recentBoardService;

    @GetMapping
    public ResponseEntity<Object> getRecentBoards(
            @RequestHeader("Authorization") String tokenHeader,
            @RequestParam(defaultValue = "5") int limit
    ) {
        List<RecentBoardResponseDTO> response = recentBoardService.getRecentBoards(tokenHeader, limit);
        return ResponseHandler.generateResponse("Recent boards retrieved", HttpStatus.OK, response, "/api/v1/user/recent-boards");
    }

    @PostMapping("/{boardId}")
    public ResponseEntity<Object> trackBoardAccess(
            @RequestHeader("Authorization") String tokenHeader,
            @PathVariable Long boardId,
            @RequestParam(required = false) Long listId,
            @RequestParam(required = false) Long cardId
    ) {
        recentBoardService.trackActivity(tokenHeader, boardId, listId, cardId);
        return ResponseHandler.generateResponse("Activity recorded", HttpStatus.OK, null, "/api/v1/user/recent-boards/" + boardId);
    }
}
