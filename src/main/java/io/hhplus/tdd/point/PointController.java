package io.hhplus.tdd.point;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/point")
public class PointController {

    private static final Logger log = LoggerFactory.getLogger(PointController.class);
    private final PointService pointService;

    @Autowired
    public PointController(PointService pointService) {
        this.pointService = pointService;
    }


    /**
     * TODO - 기본과제
     * TODO - 포인트 충전, 사용에 대한 정책 추가 (잔고 부족, 최대 잔고 등)
     * TODO - 주어진 4가지 기능에 대한 단위 테스트 작성
     */


    /**
     * TODO - 특정 유저의 포인트를 조회하는 기능을 작성해주세요.
     * 실패: 유저가 없음 -> 새로만들어지고 -> 포인트가 0
     */
    @GetMapping("{id}")
    public UserPoint point(
            @PathVariable long id
    ) {
        return pointService.getUserPoint(id);
    }

    /**
     * TODO - 특정 유저의 포인트 충전/이용 내역을 조회하는 기능을 작성해주세요.
     */
    @GetMapping("{id}/histories")
    public List<PointHistory> history(
            @PathVariable long id
    ) {
//        return List.of();
        return pointService.getPointHistory(id);
    }

    /**
     * TODO - 특정 유저의 포인트를 충전하는 기능을 작성해주세요.
     */
    @PatchMapping("{id}/charge")
    public UserPoint charge(
            @PathVariable long id,
            @RequestBody long amount
    ) {
//        return new UserPoint(0, 0, 0);
        return pointService.setCharge(id, amount, TransactionType.CHARGE);
    }

    /**
     * TODO - 특정 유저의 포인트를 사용하는 기능을 작성해주세요.
     */
    @PatchMapping("{id}/use")
    public UserPoint use(
            @PathVariable long id,
            @RequestBody long amount
    ) {
        return pointService.setUsePoint(id, amount, TransactionType.USE);
        //return new UserPoint(0, 0, 0);
    }
}