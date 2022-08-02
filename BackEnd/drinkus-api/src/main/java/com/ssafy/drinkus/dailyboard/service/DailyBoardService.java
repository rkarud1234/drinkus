package com.ssafy.drinkus.dailyboard.service;

import com.ssafy.drinkus.common.*;
import com.ssafy.drinkus.common.type.YN;
import com.ssafy.drinkus.dailyboard.DailyBoard;
import com.ssafy.drinkus.dailyboard.DailyBoardRepository;
import com.ssafy.drinkus.dailyboard.request.DailyBoardCreateRequest;
import com.ssafy.drinkus.dailyboard.request.DailyBoardUpdateRequest;
import com.ssafy.drinkus.dailyboard.response.DailyBoardResponse;
import com.ssafy.drinkus.dailyboard.response.MyBoardResponse;
import com.ssafy.drinkus.user.domain.User;
import com.ssafy.drinkus.user.domain.type.UserRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DailyBoardService {

    private final DailyBoardRepository dailyBoardRepository;

    // 총 데일리 게시판 페이지 개수 반환
    public Long countByParentIdIsNull() {
        long totalCount = dailyBoardRepository.countByParentIdIsNull();
        if (totalCount == 0) {
            throw new NotExistException("게시물이 존재하지 않습니다.");
        }
        return totalCount;
    }

    // 데일리 게시판 원글 조회
    public List<DailyBoardResponse> findByParentIdIsNull(Pageable page) {
        List<DailyBoard> results = dailyBoardRepository.findByParentIdIsNull(page);
        if (results.size() == 0) {
            throw new NotExistException("해당 페이지에 게시물이 존재하지 않습니다.");
        }

        List<DailyBoardResponse> response = new ArrayList<>();
        for (DailyBoard dailyBoard : results) {
            response.add(new DailyBoardResponse(dailyBoard.getBoardId(), dailyBoard.getCreater().getUserId(), dailyBoard.getCreatedDate(), dailyBoard.getModifiedDate(), dailyBoard.getBoardContent()));
        }

        return response;
    }

    // 댓글 조회
    public List<DailyBoardResponse> findByParentId(Long parentId) {
        List<DailyBoard> results = dailyBoardRepository.findByParentId(parentId);
        if (results.size() == 0) {
            throw new NotExistException("해당 게시물에 댓글이 존재하지 않습니다.");
        }

        List<DailyBoardResponse> response = new ArrayList<>();
        for (DailyBoard dailyBoard : results) {
            response.add(new DailyBoardResponse(dailyBoard.getBoardId(), dailyBoard.getCreater().getUserId(), dailyBoard.getCreatedDate(), dailyBoard.getModifiedDate(), dailyBoard.getBoardContent()));
        }

        return response;
    }

    // 내가 쓴 글 총 페이지 개수 반환
    public Long countByCreater(User user) {
        long totalCount = dailyBoardRepository.countByCreater(user);
        if (totalCount == 0) {
            throw new NotExistException("게시물이 존재하지 않습니다.");
        }
        return totalCount;
    }

    // 내가 쓴 글 조회
    public Page<MyBoardResponse> findByCreater(User user, Pageable page) {
        List<DailyBoard> results = dailyBoardRepository.findByCreater(user, page);
        if (results.size() == 0) {
            throw new NotExistException("해당 페이지에 게시물이 존재하지 않습니다.");
        }

        List<MyBoardResponse> response = new ArrayList<>();
        for (DailyBoard dailyBoard : results) {
            response.add(new MyBoardResponse(dailyBoard.getBoardId(), dailyBoard.getBoardContent()));
        }

        return new PageImpl<>(response, page, countByCreater(user));
    }

    // 원글 작성
    @Transactional
    public void createDailyBoard(User user, DailyBoardCreateRequest request) {
        DailyBoard dailyBoard = DailyBoard.createDailyBoard(user, user, request.getBoardContent());
        dailyBoardRepository.save(dailyBoard);
    }

    // 댓글 작성
    @Transactional
    public void createComment(User user, DailyBoardCreateRequest request, Long parentId) {
        DailyBoard parentBoard = dailyBoardRepository.findByBoardId(parentId)
                .orElseThrow(() -> new NotFoundException(NotFoundException.BOARD_DAILY_NOT_FOUND));
        if (parentBoard.getParentId() != null) {
            throw new InvalidException("답글에는 답글을 작성할 수 없습니다.");
        }

        DailyBoard dailyBoard = DailyBoard.createDailyBoard(user, user, request.getBoardContent(), parentId);
        dailyBoardRepository.save(dailyBoard);
    }

    // 글 수정
    @Transactional
    public void updateDailyBoard(User user, DailyBoardUpdateRequest request, Long boardId) {
        DailyBoard dailyBoard = dailyBoardRepository.findByBoardId(boardId)
                .orElseThrow(() -> new NotFoundException(NotFoundException.BOARD_DAILY_NOT_FOUND));

        if (user.getUserRole() != UserRole.ROLE_ADMIN && !user.equals(dailyBoard.getCreater())) {
            // 원글 작성자이거나 관리자 권한일 때만 수정 가능
            throw new AuthenticationException("본인이 쓴 글만 수정 할 수 있습니다.");
        }

        dailyBoard.updateDailyBoard(user, request.getBoardContent());
    }

    // 글 삭제
    @Transactional
    public void deleteDailyBoard(User user, Long boardId) {
        DailyBoard dailyBoard = dailyBoardRepository.findByBoardId(boardId)
                .orElseThrow(() -> new NotFoundException(NotFoundException.BOARD_DAILY_NOT_FOUND));

        if (user.getUserRole() != UserRole.ROLE_ADMIN && !user.equals(dailyBoard.getCreater())) {
            // 원글 작성자이거나 관리자 권한일 때만 삭제 가능
            throw new AuthenticationException("본인이 쓴 글만 삭제 할 수 있습니다.");
        }

        dailyBoardRepository.deleteByParentId(dailyBoard.getBoardId()); // 글 삭제 시 글에 대한 답글들도 모두 삭제
        dailyBoardRepository.delete(dailyBoard); // 해당 게시물 삭제
    }

    // 매일 6시에 글 전체 삭제
    @Scheduled(cron = "0 0 6 * * *")
    @Transactional
    public void deleteAll(){
        dailyBoardRepository.deleteAll();
    }

}
