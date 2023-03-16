package maplestory.service.authentic.web;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import maplestory.service.authentic.domain.Authentic;
import maplestory.service.authentic.web.validation.AuthenticValidator;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/authentic")
@RequiredArgsConstructor
public class AuthenticController {

    private final AuthenticService authenticService;
    private final AuthenticValidator authenticValidator;

    @InitBinder
    public void init(WebDataBinder dataBinder) {
        dataBinder.addValidators(authenticValidator);
    }

    @GetMapping
    public String authentic(Model model) {
        model.addAttribute("authentic", new Authentic());
        return "authentic/count";
    }

    @PostMapping
    public String calcAuthentic(@Validated @ModelAttribute Authentic authentic, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "authentic/count";
        }

        List<List<Integer>> resultData = authenticService.authenticSimulation(authentic);
        Integer days = resultData.get(resultData.size() - 1).get(0);
        log.info("계산된 일수={}", days);
        String goalDate = authenticService.dateCalculation(days);
        log.info("계산된 날짜={}", goalDate);

        model.addAttribute("resultData", resultData);
        model.addAttribute("goalDate", goalDate);
        return "authentic/result";
    }
}
