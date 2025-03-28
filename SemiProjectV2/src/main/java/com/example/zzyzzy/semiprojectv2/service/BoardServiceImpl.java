package com.example.zzyzzy.semiprojectv2.service;

import com.example.zzyzzy.semiprojectv2.domain.*;
import com.example.zzyzzy.semiprojectv2.repository.BoardRepository;
import com.example.zzyzzy.semiprojectv2.repository.ReplyRepository;
import com.example.zzyzzy.semiprojectv2.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService {

    private final BoardRepository boardRepository;
    private final ReplyRepository replyRepository;
    @Value("${board.pagesize}") private int pageSize;

    @Override
    public Board newBoard(Board board) {

        return boardRepository.save(board);
    }

    @Override
    public BoardListDTO readBoard(int cpg) {
        Pageable pageable = PageRequest.of(cpg - 1, pageSize, Sort.Direction.DESC, "bno");

        Page<BoardDTO> pageboards = boardRepository.findBy(pageable);
        List<BoardDTO> boards = pageboards.getContent();
        int totalItems = (int) pageboards.getTotalElements();
        int cntpg = pageboards.getTotalPages();

        return new BoardListDTO(cpg, totalItems, pageSize, boards);
    }

    @Override
    public BoardListDTO findBoard(int cpg, String findtype, String findkey) {
        Pageable pageable = PageRequest.of(cpg - 1, pageSize, Sort.Direction.DESC, "bno");
        Page<BoardDTO> pageboards = null;

        switch (findtype) {
            case "title":
                pageboards = boardRepository.findByTitleContains(pageable, findkey); break;
            case "contents":
                pageboards = boardRepository.findByContentsContains(pageable, findkey); break;
            case "userid":
                pageboards = boardRepository.findByUseridContains(pageable, findkey); break;
            case "titconts":
                pageboards = boardRepository.findByTitleContainsOrContentsContains(pageable, findkey, findkey); break;
        }

        List<BoardDTO> boards = pageboards.getContent();
        int totalItems = (int) pageboards.getTotalElements();

        return new BoardListDTO(cpg, totalItems, pageSize, boards);
    }

    @Override
    public Page<BoardDTO> testReadBoard(int cpg) {
        Pageable pageable = PageRequest.of(cpg - 1, pageSize, Sort.Direction.DESC, "bno");

        Page<BoardDTO> pageboards = boardRepository.findBy(pageable);

        return pageboards;
    }

    @Override
    public BoardReplyDTO readOneBoardReply(Long bno) {
        Board board = boardRepository.findByBno(bno);
        List<Reply> replies = replyRepository.findByPnoOrderByRef(bno);

        return new BoardReplyDTO(board, replies);
    }

}
