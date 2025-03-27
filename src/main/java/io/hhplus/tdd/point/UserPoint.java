package io.hhplus.tdd.point;

public record UserPoint(
        long id, // 유저 아이디
        long point, // 유저 포인트
        long updateMillis // 포인트 기록 업데이트 시간
) {

    static final long MAX_POINT_LIMIT = 1000L;

    public static void pointValidation(long chargeAmount){
        // 들어온 포인트가 양수인지 확인, 음수이면 안됨
        if (chargeAmount < 0) {
            throw new IllegalArgumentException("잘못된 충전 금액을 입력하셨습니다.");
        }

        // 정책이지만, 100 초과는 충전할 수 없다
        if (chargeAmount > MAX_POINT_LIMIT) {
            throw new IllegalArgumentException(String.format("최대 충전값은 %d입니다", MAX_POINT_LIMIT));
        }
    }


    public static UserPoint empty(long id) {
        return new UserPoint(id, 0, System.currentTimeMillis());
    }
}
