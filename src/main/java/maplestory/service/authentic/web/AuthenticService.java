package maplestory.service.authentic.web;

import lombok.extern.slf4j.Slf4j;
import maplestory.service.authentic.domain.Authentic;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@Service
public class AuthenticService {
    private static final Map<Integer, Integer> growthTable = new HashMap<>();
    private static final int dailyArcsAmount = 10;
    private static final int dailyOdiumAmount = 5;

    public AuthenticService() {
        for (int i = 1; i <= 10; i++) {
            growthTable.put(i, (9 * (int) Math.pow(i, 2) + (20 * i)));
        }
    }

    public String dateCalculation(int days) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.DATE, days);

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        return df.format(cal.getTime());
    }

    /**
     * 도시락이 쌓여있는 경우 0일차에 처리한다.
     * @param authentic: 어센틱 심볼 정보
     * @return Authentic 도시락을 해제한 어센틱 심볼
     */
    public Authentic authenticUnzip(Authentic authentic) {
        while (1 <= authentic.getCerniumLevel() && authentic.getCerniumLevel() < 11
                && authentic.getCerniumCount() >= growthTable.get(authentic.getCerniumLevel())) {
            authentic.setCerniumCount(
                    authentic.getCerniumCount() - growthTable.get(authentic.getCerniumLevel())
            );
            authentic.setCerniumLevel(authentic.getCerniumLevel() + 1);
        }
        while (1 <= authentic.getArcsLevel() && authentic.getArcsLevel() < 11
                && authentic.getArcsCount() >= growthTable.get(authentic.getArcsLevel())) {
            authentic.setArcsCount(
                    authentic.getArcsCount() - growthTable.get(authentic.getArcsLevel())
            );
            authentic.setArcsLevel(authentic.getArcsLevel() + 1);
        }
        while (1 <= authentic.getOdiumLevel() && authentic.getOdiumLevel() < 11
                && authentic.getOdiumCount() >= growthTable.get(authentic.getOdiumLevel())) {
            authentic.setOdiumCount(
                    authentic.getOdiumCount() - growthTable.get(authentic.getOdiumLevel())
            );
            authentic.setOdiumLevel(authentic.getOdiumLevel() + 1);
        }
        return authentic;
    }

    /**
     * 어센틱 심볼 계산 시뮬레이터 실행
     * @param authentic: 어센틱 심볼 정보
     * @return List<List<Integer>>: 시뮬레이션 결과가 담긴 중첩 리스트
     */
    public List<List<Integer>> authenticSimulation(Authentic authentic) {
        List<List<Integer>> result = new ArrayList<>();
        int days = 0; // 목표치 달성을 위해 필요한 일 수

        authentic = authenticUnzip(authentic);
        log.info("도시락 계산결과={}", authentic.toString());

        // 현재 포스 정보
        int currentCerniumLevel = authentic.getCerniumLevel();
        int currentCerniumCount = authentic.getCerniumCount();
        int currentArcsLevel = authentic.getArcsLevel();
        int currentArcsCount = authentic.getArcsCount();
        int currentOdiumLevel = authentic.getOdiumLevel();
        int currentOdiumCount = authentic.getOdiumCount();

        // 현재 어센틱 포스 계산
        int currentAuthenticForce = 0;
        currentAuthenticForce += 10 * (authentic.getCerniumLevel());
        currentAuthenticForce += 10 * (authentic.getArcsLevel());
        currentAuthenticForce += 10 * (authentic.getOdiumLevel());

        // 이미 목표치 이상일 경우 결과 반환
        if (currentAuthenticForce >= authentic.getGoalAuthentic()) {
            result.add(Arrays.asList(days, currentCerniumLevel, currentCerniumCount, currentArcsLevel, currentArcsCount, currentOdiumLevel, currentOdiumCount));
            return result;
        }

        // 계산 심볼 타겟 지정 (세르니움: 0, 아르크스: 1, 오디움: 2)
        List<List<Integer>> calcTarget = new ArrayList<>();
        if (1 <= currentCerniumLevel && currentCerniumLevel < 11) {
            calcTarget.add(Arrays.asList(0, currentCerniumLevel, currentCerniumCount, authentic.getDailyAmount()));
        }
        if (1 <= currentArcsLevel && currentArcsLevel < 11) {
            calcTarget.add(Arrays.asList(1, currentArcsLevel, currentArcsCount, dailyArcsAmount));
        }
        if (1 <= currentOdiumLevel && currentOdiumLevel < 11) {
            calcTarget.add(Arrays.asList(2, currentOdiumLevel, currentOdiumCount, dailyOdiumAmount));
        }

        // 시뮬레이션 실행
        while (!calcTarget.isEmpty() && currentAuthenticForce < authentic.getGoalAuthentic()) {
            for (List<Integer> authenticInfo : calcTarget) {
                // 11 레벨 이상은 제외
                Integer currentLevel = authenticInfo.get(1);
                if (currentLevel >= 11) {
                    continue;
                }

                Integer currentCount = authenticInfo.get(2) + authenticInfo.get(3);
                if (currentCount >= growthTable.get(currentLevel)) {
                    currentCount -= growthTable.get(currentLevel);
                    currentLevel += 1;
                    currentAuthenticForce += 10;
                    authenticInfo.set(1, currentLevel);
                }
                authenticInfo.set(2, currentCount);

                int cityIdx = authenticInfo.get(0);
                if (cityIdx == 0){
                    currentCerniumLevel = authenticInfo.get(1);
                    currentCerniumCount = authenticInfo.get(2);
                } else if (cityIdx == 1){
                    currentArcsLevel = authenticInfo.get(1);
                    currentArcsCount = authenticInfo.get(2);
                } else if (cityIdx == 2) {
                    currentOdiumLevel = authenticInfo.get(1);
                    currentOdiumCount = authenticInfo.get(2);
                }
            }
            days += 1;
            result.add(Arrays.asList(days, currentCerniumLevel, currentCerniumCount, currentArcsLevel, currentArcsCount, currentOdiumLevel, currentOdiumCount));
        }
        return result;
    }
}
