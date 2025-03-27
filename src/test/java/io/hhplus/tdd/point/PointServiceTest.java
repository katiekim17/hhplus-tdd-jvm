package io.hhplus.tdd.point;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PointServiceTest {

    /**
     * 내가 HTTP 프로토콜로 받는 인수 -> long id, long amount
     */

    static final long ANY_UPDATE_MILLIS = 1L; // 시간
    static final long ANY_ID = 1L; // 아이디
    static final long ANY_USER_ID = 1L; // 아이디

    // 테이블 가짜 객체가져오기
    @Mock
    private UserPointTable userPointTable; //협력자

    @Mock
    private PointHistoryTable pointHistoryTable; //협력자

    @InjectMocks
    private PointService pointService;

    // 초기화
//    @BeforeEach
//    void setUp(){
//        MockitoAnnotations.openMocks(this);
//    }

    /**
     * TODO - 기본과제
     * TODO - 포인트 충전, 사용에 대한 정책 추가 (잔고 부족, 최대 잔고 등)
     * TODO - 주어진 4가지 기능에 대한 단위 테스트 작성
     * given
     *      테스트를 준비하는 과정
     *      테스트에 사용하는 변수, 입력 값 등을 정의하거나, 객체를 정의하는 구문도 Given에 포함된다.
     * when
     *      실제로 테스트를 실행하는 과정
     * then
     * 		테스트를 검증하는 과정
     */

    @Test
    @DisplayName("특정 유저의 포인트를 조회하는 기능 테스트 - 성공케이스")
    void getUserPoint() {
        // 성공포인트 -> 데이터가 있을 떄, 해당 아이디를 입력하면 조회할 아이디의 포인트 정보를 얻을 수 있다

        //given 세팅영역, 데이터를 만듦, 데이터를 주면 getUserPoint를 통해 값을 준다고 설정
        UserPoint fakeUserPoint = new UserPoint(ANY_USER_ID, 100, ANY_UPDATE_MILLIS); // 가짜 데이터 만듦
        given(userPointTable.selectById(ANY_USER_ID)).willReturn(fakeUserPoint);

        //when 실행- 진짜 아이디를 주면  getUserPoint가 실행되는지 확인
        UserPoint result = pointService.getUserPoint(ANY_USER_ID);

        //then - 아이디가 빈값일 때, 충전금액이 0원인 userPoint 객체를 만든다, 아니라면 기존 유저 정보 가져옴
        assertThat(result.id()).isEqualTo(fakeUserPoint.id());
        assertThat(result.point()).isEqualTo(fakeUserPoint.point());
        assertThat(result.updateMillis()).isEqualTo(fakeUserPoint.updateMillis());

        // 실패케이스...--> 아이디 데이터 형이 다르면... 아예 안들어올텐데..?
        // 실패케이스가 있나?
    }

    @Test
    @DisplayName("특정 유저의 포인트 충전/이용 내역을 조회하는 기능 - 성공케이스")
    void getPointHistory() {
        // 성공케이스 - id 주면 내가 미리 만들어 놓은 가짜 데이터와 동일한 데이터를 준다
        // given - 가짜 pointHitoryTable값 만들기
        PointHistory fakePointUseHistory = new PointHistory(ANY_ID, ANY_USER_ID, 300L, TransactionType.USE, ANY_UPDATE_MILLIS);
        PointHistory fakePointChargeHistory = new PointHistory(ANY_ID, ANY_USER_ID, 100L, TransactionType.CHARGE, ANY_UPDATE_MILLIS);
        List<PointHistory> fakePointHistoryList = new ArrayList<>();
        fakePointHistoryList.add(fakePointUseHistory);
        fakePointHistoryList.add(fakePointChargeHistory);
        given(pointHistoryTable.selectAllByUserId(ANY_USER_ID)).willReturn(fakePointHistoryList);

        // when 실행- 진짜 아이디를 주면  selectAllByUserId를 이용해서 리스트 가져오는 지 확인
        List<PointHistory> historyListResult = pointService.getPointHistory(ANY_USER_ID);

        // Then 결과 - 내가 미리 등록한 가짜 데이터와 일치하는지 확인
        assertThat(historyListResult).isEqualTo(fakePointHistoryList);
    }

    @Test
    @DisplayName("특정 유저의 포인트를 충전하는 기능 - 성공케이스")
    void setCharge() {
        // 성공케이스 - 유저정보가 있는 상태에서, Charge 하면 기존 포인트 값(100L) + 입력한 포인트(200L) = newPoint(300L) 값이 반환됨
        // given
        long initPoint = -100L;
        long chargePoint = 300L;
        long expectedPoint = initPoint + chargePoint;
        UserPoint fakeUserPoint = new UserPoint(ANY_USER_ID, initPoint, ANY_UPDATE_MILLIS); // 가짜 데이터 만듦
        UserPoint fakeChargedUserPoint = new UserPoint(ANY_USER_ID, expectedPoint, ANY_UPDATE_MILLIS); // 가짜 데이터 만듦
        given(userPointTable.selectById(ANY_USER_ID)).willReturn(fakeUserPoint);
        given(userPointTable.insertOrUpdate(ANY_USER_ID, expectedPoint)).willReturn(fakeChargedUserPoint);

        // when - 충전 시도
        pointService.setCharge(ANY_USER_ID, chargePoint, TransactionType.CHARGE);

        // then - 400L이 되는지 확인, pointHistoryTable에 기록 들어가는지 확인
        assertThat(fakeChargedUserPoint.point()).isEqualTo(expectedPoint);
        verify(pointHistoryTable).insert(
                eq(ANY_USER_ID),
                eq(expectedPoint),
                eq(TransactionType.CHARGE),
                eq(ANY_UPDATE_MILLIS)
        );
    }


    @Test
    @DisplayName("충전시 음수 포인트를 넣었을 때 -> 예외 발생, 문구 출력")
    void setChargeFail() {
        // given
        long negativePoint = -300L;

        // when - 충전 시도
        // then - 예외발생하는 지 확인
        assertThatThrownBy(()-> {
            pointService.setCharge(ANY_USER_ID, negativePoint, TransactionType.CHARGE);
        }).isInstanceOf(IllegalArgumentException.class).hasMessage("잘못된 충전 금액을 입력하셨습니다.");

        verify(pointHistoryTable, never()).insert(ANY_USER_ID, negativePoint, TransactionType.CHARGE, ANY_UPDATE_MILLIS);
        verify(pointHistoryTable, never()).insert(ANY_USER_ID, negativePoint, TransactionType.USE, ANY_UPDATE_MILLIS);
    }

    @Test
    @DisplayName("충전시 최대값 이상을 넣으면 -> 예외 발생, 한도 초과 문구 출력")
    void checkMaxChargePoint() {
        // given
        long chargePoint = 10001L; // 충전하려는 금액
        long MAX_POINT_LIMIT = 1000L; // 충전 최대 한도

        // when - 충전 시도
        // then - 한도 초과라는 예외발생하는 지 확인, pointHistoryTable에 기록이 '안' 들어가는지 확인
        assertThatThrownBy(()-> {
            pointService.setCharge(ANY_USER_ID, chargePoint, TransactionType.CHARGE);
        }).isInstanceOf(IllegalArgumentException.class).hasMessage(String.format("최대 충전값은 %d입니다", MAX_POINT_LIMIT));

        verify(pointHistoryTable, never()).insert(ANY_USER_ID, chargePoint, TransactionType.CHARGE, ANY_UPDATE_MILLIS);
        verify(pointHistoryTable, never()).insert(ANY_USER_ID, chargePoint, TransactionType.USE, ANY_UPDATE_MILLIS);
    }


    @Test
    @DisplayName("특정 유저의 포인트를 사용 - 성공케이스")
    void setUsePoint() {
        // 성공케이스 - 유저정보가 있는 상태에서, 기존포인트 500L - 사용할 포인트 300L = newPoint(200L) 값이 반환되고, 기록이 남아야함
        // given
        long initPoint = 500L; // 초기 포인트
        long usePoint = 300L; // 사용할 포인트
        long afterUsePoint = initPoint - usePoint; // 예상 포인트

        UserPoint fakeUserPoint = new UserPoint(ANY_USER_ID, initPoint, ANY_UPDATE_MILLIS); // 가짜 데이터 만듦
        UserPoint fakeAfterUseUserPoint = new UserPoint(ANY_USER_ID, afterUsePoint, ANY_UPDATE_MILLIS); // 가짜 데이터 만듦
        given(userPointTable.selectById(ANY_USER_ID)).willReturn(fakeUserPoint);
        given(userPointTable.insertOrUpdate(ANY_USER_ID, afterUsePoint)).willReturn(fakeAfterUseUserPoint);

        // when - 사용 시도
        pointService.setUsePoint(ANY_USER_ID, usePoint, TransactionType.USE);

        // then - 정상적으로 차감되어 200L이 되는지 확인, 포인트 히스토리테이블에 결과 값 저장되어 있는 지 확인
        assertThat(fakeAfterUseUserPoint.point()).isEqualTo(afterUsePoint);
        verify(pointHistoryTable).insert(
                eq(ANY_USER_ID),
                eq(afterUsePoint),
                eq(TransactionType.USE),
                eq(ANY_UPDATE_MILLIS)
        );
    }


    @Test
    @DisplayName("사용시 음수 포인트를 넣었을 때 -> 예외 발생, 문구 출력")
    void setUseFail() {
        // given
        long negativePoint = -300L;

        // when - 충전 시도
        // then - 예외발생하는 지 확인
        assertThatThrownBy(()-> {
            pointService.setUsePoint(ANY_USER_ID, negativePoint, TransactionType.CHARGE);
        }).isInstanceOf(IllegalArgumentException.class).hasMessage("잘못된 충전 금액을 입력하셨습니다.");

        verify(pointHistoryTable, never()).insert(ANY_USER_ID, negativePoint, TransactionType.CHARGE, ANY_UPDATE_MILLIS);
        verify(pointHistoryTable, never()).insert(ANY_USER_ID, negativePoint, TransactionType.USE, ANY_UPDATE_MILLIS);
    }

    @Test
    @DisplayName("사용할 포인트가 부족할 때 -> 예외 발생, 문구 출력")
    void insufficientPointTest() {
        // given
        long initPoint = 300L; // 초기 포인트
        long reqPoint = 500L; // 사용요청한 포인트

        UserPoint fakeUserPoint = new UserPoint(ANY_USER_ID, initPoint, ANY_UPDATE_MILLIS); // 가짜 데이터 만듦
        given(userPointTable.selectById(ANY_USER_ID)).willReturn(fakeUserPoint);

        // when - 사용 시도
        // then - 예외발생하는 지 확인
        assertThatThrownBy(()-> {
            pointService.setUsePoint(ANY_USER_ID, reqPoint, TransactionType.USE);
        }).isInstanceOf(IllegalArgumentException.class).hasMessage("포인트가 부족합니다.");

        verify(pointHistoryTable, never()).insert(ANY_USER_ID, reqPoint, TransactionType.CHARGE, ANY_UPDATE_MILLIS);
        verify(pointHistoryTable, never()).insert(ANY_USER_ID, reqPoint, TransactionType.USE, ANY_UPDATE_MILLIS);
    }
}