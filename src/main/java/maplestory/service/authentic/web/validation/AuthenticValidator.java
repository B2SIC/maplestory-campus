package maplestory.service.authentic.web.validation;

import maplestory.service.authentic.domain.Authentic;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class AuthenticValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return Authentic.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Authentic authentic = (Authentic) target;

        // 입력 값이 들어오지 않은 경우
        if (authentic.getCerniumCount() == null) {
            errors.rejectValue("cerniumCount", "blank");
        }
        if (authentic.getArcsCount() == null) {
            errors.rejectValue("arcsCount", "blank");
        }
        if (authentic.getOdiumCount() == null) {
            errors.rejectValue("odiumCount", "blank");
        }

        // 음수 값이 들어온 경우
        if (authentic.getCerniumCount() < 0) {
            errors.rejectValue("cerniumCount", "negative");
        }
        if (authentic.getArcsCount() < 0) {
            errors.rejectValue("arcsCount", "negative");
        }
        if (authentic.getOdiumCount() < 0) {
            errors.rejectValue("odiumCount", "negative");
        }

        // 경험치 범위 제한 초과
        if (authentic.getCerniumCount() != null && authentic.getCerniumCount() >= 4565) {
            errors.rejectValue("cerniumCount", "max");
        }
        if (authentic.getArcsCount() != null && authentic.getArcsCount() >= 4565) {
            errors.rejectValue("arcsCount", "max");
        }
        if (authentic.getOdiumCount() != null && authentic.getOdiumCount() >= 4565) {
            errors.rejectValue("odiumCount", "max");
        }

        // 현재 어센틱 포스 계산
        int currentAuthenticForce = 0;
        currentAuthenticForce += 10 * (authentic.getCerniumLevel());
        currentAuthenticForce += 10 * (authentic.getArcsLevel());
        currentAuthenticForce += 10 * (authentic.getOdiumLevel());

        // 최대 가능 포스 계산 (레벨 1이상이면 보유 중인 심볼)
        int possibleAuthentic = 0;
        if (authentic.getCerniumLevel() >= 1) possibleAuthentic += 110;
        if (authentic.getArcsLevel() >= 1) possibleAuthentic += 110;
        if (authentic.getOdiumLevel() >= 1) possibleAuthentic += 110;

        /*
          계산이 필요 없는 케이스
          1. 이미 목표 포스 이상의 포스를 보유중인 경우
          2. 보유 심볼(레벨 1이상)을 모두 최대 레벨 달성해도 목표 포스에 도달할 수 없는 경우
          3. 보유 심볼이 없는 경우(모두 레벨 0)
         */
        if (currentAuthenticForce >= authentic.getGoalAuthentic()) {
            errors.rejectValue("goalAuthentic", "needless");
        }

        if (possibleAuthentic < authentic.getGoalAuthentic() ||
                (authentic.getCerniumLevel() == 0 && authentic.getArcsLevel() == 0 && authentic.getOdiumLevel() == 0)) {
            errors.rejectValue("goalAuthentic", "impossible");
        }
    }
}
