package com.pado.calculator.api;

import com.pado.calculator.account.Account;
import com.pado.calculator.account.AccountRepository;
import com.pado.calculator.account.AccountService;
import com.pado.calculator.account.SignUpForm;
import com.pado.calculator.common.ErrorResult;
import com.pado.calculator.common.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class AccountRestController {

    private final AccountService accountService;
    private final AccountRepository accountRepository;


    @ExceptionHandler
    public ResponseEntity handleException(MethodArgumentNotValidException e) {
        final List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
        List<ErrorResult.FieldError> errors =
                fieldErrors.stream()
                        .map(error -> new ErrorResult.FieldError(
                                error.getField(),
                                error.getDefaultMessage(),
                                error.getRejectedValue()
                        ))
                        .collect(Collectors.toList());

        return new ResponseEntity<>(fieldErrors, HttpStatus.BAD_REQUEST);
    }
    @GetMapping("/api/test")
    public ResponseEntity test(){
        return new ResponseEntity(HttpStatus.OK);
    }

    @PostMapping("/api/sign-up")
    public Result<Account> signUp(@Valid @RequestBody SignUpForm signUpForm){

        // todo : Result 부분 생성자로 리팩토링. 지네릭 다시공부하기
        if(accountRepository.existsByEmail(signUpForm.getEmail()))
            throw new AccountCannotCreateException("이미 존재하는 회원입니다");

        Account account = accountService.createAccount(signUpForm);

        Result<Account> result = Result.<Account>builder()
                .status(HttpStatus.OK)
                .message("정상응답")
                .data(account).build();

        return result;
    }
}
