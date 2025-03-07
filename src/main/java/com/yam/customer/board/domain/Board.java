package com.yam.customer.board.domain;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 회원 게시판 엔티티
 * DB 테이블: BOARD
 */
@Entity
@Getter
@Setter
@ToString
@Table(name = "board")
public class Board {

    /**
     * 게시글 번호 (PK)
     * - board_number
     * - auto-increment 사용 (IDENTITY)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long boardNumber;

    /**
     * 게시판 종류 (자유, 불만, 추천 등)
     * - board_category (NVARCHAR2(30))
     * - NOT NULL
     */
    @Column(nullable = false, length = 30)
    private String boardCategory;

    /**
     * 게시글 제목
     * - board_title (NVARCHAR2(30))
     * - NOT NULL
     */
    @Column(nullable = false, length = 30)
    private String boardTitle;

    /**
     * 게시글 내용
     * - board_content (TEXT/CLOB)
     * - NOT NULL
     */
    @Lob
    @Column(nullable = false)
    private String boardContent;

    /**
     * 첨부파일 경로
     * - board_file
     */
    private String boardFile;

    /**
     * 추천 여부
     * - like_char (boolean 처리)
     */
    @Column(nullable = false)
    private int likeChar;

    /**
     * 작성일
     * - create_board (DATETIME)
     */
    private LocalDateTime createBoard;

    /**
     * 수정일
     * - board_update (TIMESTAMP)
     */
    private LocalDateTime boardUpdate; 

    /**
     * 회원 ID
     * - customer_id (VARCHAR2(18))
     * - NOT NULL
     */
    @Column(nullable = false, length = 18)
    private String customerId;
    
 
}


