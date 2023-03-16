package maplestory.service.authentic.web;

import maplestory.service.authentic.domain.Authentic;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class AuthenticServiceTest {

    private final AuthenticService authenticService = new AuthenticService();

    @Test
    void dateCalculationTest() {
        Authentic authentic = new Authentic();
        authentic.setCerniumLevel(11);
        authentic.setCerniumCount(0);
        authentic.setDailyAmount(15);

        authentic.setArcsLevel(11);
        authentic.setArcsCount(0);

        authentic.setOdiumLevel(11);
        authentic.setOdiumCount(0);

        authentic.setGoalAuthentic(330);

        // 최대 포스
        List<List<Integer>> resultData = authenticService.authenticSimulation(authentic);
        assertThat(resultData.size()).isEqualTo(1);

        // 계산: 이틀 뒤 달성
        authentic.setOdiumLevel(10);
        authentic.setOdiumCount(1090);
        resultData = authenticService.authenticSimulation(authentic);
        assertThat(resultData.size()).isEqualTo(2);
    }
}