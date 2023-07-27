package com.simpledesign.ndms.common.cdc;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Map;


/**
 * MessageObject 클래스는 Maxwell로부터 전달받은 메시지를 나타냅니다.
 *
 * @author dev-truly
 */
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Data
public class MessageObject {
    /**
     * 이벤트가 발생한 데이터베이스의 이름
     */
    private String database;

    /**
     * 이벤트가 발생한 테이블의 이름.
     */
    private String table;

    /**
     * 변경된 데이터를 나타냅니다.
     * 'insert' 이벤트에서는 새 행의 데이터
     * 'delete' 이벤트에서는 삭제된 행의 데이터
     * 'update' 이벤트에서는 바뀐 행의 새 데이터
     */
    private Map<String, Object> data;

    /**
     * 이벤트의 타입을 나타내며 'insert', 'update', 'delete' 중 하나의 값을 가짐.
     */
    private String type;

    /**
     * 이벤트가 발생한 시간을 Unix epoch (1970년 1월 1일부터의 초) 표현
     */
    private long ts;

    /**
     * 'update' 이벤트에서만 사용되며, 변경 전의 행 데이터
     */
    private Map<String, Object> old;
}

