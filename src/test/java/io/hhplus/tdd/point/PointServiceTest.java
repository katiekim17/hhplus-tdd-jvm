package io.hhplus.tdd.point;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.logging.Logger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class PointServiceTest {

    private static final long ANY_UPDATE_MILLIS = 1L;
    private static final long ANY_ID = 1L;


    @Test
    @DisplayName("특정 유저의 포인트를 조회하는 기능 테스트")
    void getUserPoint() {
        //given - 아이디는 아무거나 줄것이므로 ANY_ID 사용

        //when - 아이디 빈값인지 아닌지 확인
        UserPoint userPoint = UserPoint.empty(ANY_ID);
        Logger.getLogger("userPoint: "+ userPoint);

        //then - 아이디가 빈값일 때, 충전금액이 0원인 userPoint객체를 만든다, 아니라면 기존 유저 정보 가져옴
        assertThat(userPoint).isEqualTo(new UserPoint(ANY_ID, 2000L, ANY_UPDATE_MILLIS));
    }

    @Test
    void getPointHistory() {
    }

    @Test
    void setCharge() {

    }

    @Test
    void setUsePoint() {
    }
}