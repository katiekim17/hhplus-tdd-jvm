package io.hhplus.tdd.point;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PointService {

    private final UserPointTable userPointTable;
    private final PointHistoryTable pointHistoryTable;

    public PointService(UserPointTable userPointTable, PointHistoryTable pointHistoryTable) {
        this.userPointTable = userPointTable;
        this.pointHistoryTable = pointHistoryTable;
    }

    // 특정 유저의 포인트를 조회하는 기능
    public UserPoint getUserPoint(long id){
        // 1. id가 존재 하는지 DB에서 확인
        // 2. id가 없으면 포인트가 0인 UserPoint 생성 (-> UserPointTable에 기능 있음)
        // 2-2. id를 가지고 UserPointTable 에서 해당 id의 값을 조회한다
        return userPointTable.selectById(id);
    }

    // 특정 유저의 포인트 충전/이용 내역을 조회하는 기능
    public List<PointHistory> getPointHistory(long id) {
        return pointHistoryTable.selectAllByUserId(id);
    }

    // 특정 유저의 포인트를 충전하는 기능
    public UserPoint setCharge(long id, long amount, TransactionType charge) {
        // 컨트롤러를 통해 들어온 포인트의 유효성 검사를 한다
        UserPoint.pointValidation(amount);
        // 1. 컨트롤러에서 받은 유저 정보값을 이용하여 유저를 조회한다
        UserPoint userPoint = this.getUserPoint(id);
        // 2. 특정 유저의 포인트를 가져온다
        // 3. 특정 유저의 현재 포인트와 충전할 포인트를 계산한다
        long newPoint = userPoint.point() + amount;
        // 4. UserPointTable에 유저와, 새로운 포인트값을 등록한다
        userPoint = userPointTable.insertOrUpdate(id, newPoint);
        // 5. PointHistoryTable 아이디, 최종 포인트, 거래타입, 충전시간을 등록한다
        pointHistoryTable.insert(userPoint.id(), userPoint.point(), charge, userPoint.updateMillis());
        // 6. UserPoint 객체로 담아서 컨트롤러로 넘겨준다.
        return userPoint;
    }

    // 특정 유저의 포인트를 사용
    // 유저 정보에 값 입력, 포인트 사용 기록에도 저장
    public UserPoint setUsePoint(long id, long amount, TransactionType use) {
        // 유저의 idx 가져오기
        UserPoint userPoint = this.getUserPoint(id);
        userPoint = userPointTable.insertOrUpdate(id, amount);
        // 포인트 히스토리테이블에 결과 값 저장
        pointHistoryTable.insert(id, amount, use, userPoint.updateMillis());
        return userPoint;
    }
}
