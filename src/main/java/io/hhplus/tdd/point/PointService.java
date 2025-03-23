package io.hhplus.tdd.point;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;

import java.util.List;

public class PointService {
    private UserPoint userPoint;

    // 특정 유저의 포인트를 조회하는 기능
    public UserPoint getUserPoint(long id){
        // id를 가지고 UserPointTable 에서 해당 id의 값을 조회한다
        return new UserPointTable().selectById(id);
    }

    // 특정 유저의 포인트 충전/이용 내역을 조회하는 기능
    public List<PointHistory> getPointHistory(long id) {
        return new PointHistoryTable().selectAllByUserId(id);
    }

    // 특정 유저의 포인트를 충전하는 기능
    public UserPoint setCharge(long id, long amount, TransactionType charge) {
        new PointHistoryTable().insert(id, amount, charge, userPoint.updateMillis());
        return new UserPointTable().insertOrUpdate(id, amount);
    }

    // 특정 유저의 포인트를 사용
    // 유저 정보에 값 입력, 포인트 사용 기록에도 저장
    public UserPoint setUsePoint(long id, long amount, TransactionType use) {
        // 유저의 idx 가져오기
        UserPoint userPoint = new UserPointTable().insertOrUpdate(id, amount);
        // 포인트 히스토리테이블에 결과 값 저장
        new PointHistoryTable().insert(id, amount, use, userPoint.updateMillis());
        return userPoint;
    }
}
