package vn.hoidanit.jobhunter.util.exception;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import vn.hoidanit.jobhunter.domain.response.RestResponse;

@RestControllerAdvice
public class GlobalException {

    @ExceptionHandler(value = {
            UsernameNotFoundException.class,
            BadCredentialsException.class,
            IdInvalidException.class
    })
    public ResponseEntity<RestResponse<Object>> handleIdException(Exception ex) {
        RestResponse<Object> restResponse = new RestResponse<Object>();
        restResponse.setStatusCode(HttpStatus.BAD_REQUEST.value());
        restResponse.setError(ex.getMessage());
        restResponse.setMessage("Exception Occurs ...");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(restResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class) // lỗi từ @Valid khi nhận dữ liệu từ RequestBody
    public ResponseEntity<RestResponse<Object>> validationError(MethodArgumentNotValidException ex) {
        BindingResult result = ex.getBindingResult();
        final List<FieldError> fieldErrors = result.getFieldErrors();

        RestResponse<Object> res = new RestResponse<>();
        res.setStatusCode(HttpStatus.BAD_REQUEST.value());
        res.setError(ex.getBody().getDetail());

        List<String> errors = fieldErrors.stream().map(f -> f.getDefaultMessage()).collect(Collectors.toList());
        res.setMessage(errors.size() > 1 ? errors : errors.get(0));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
    }

    @ExceptionHandler(value = {
            NoResourceFoundException.class
    })
    public ResponseEntity<RestResponse<Object>> notFoundException(Exception ex) {
        RestResponse<Object> response = new RestResponse<>();
        response.setStatusCode(HttpStatus.NOT_FOUND.value());
        response.setError(ex.getMessage());
        response.setMessage("404 not found. Url may not exist");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(value = {
            StorageException.class
    })
    public ResponseEntity<RestResponse<Object>> handleFileUploadException(Exception ex) {
        RestResponse<Object> restResponse = new RestResponse<Object>();
        restResponse.setStatusCode(HttpStatus.BAD_REQUEST.value());
        restResponse.setError(ex.getMessage());
        restResponse.setMessage("Exception Upload File ...");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(restResponse);
    }

    @ExceptionHandler(value = {
            PermissionException.class
    })
    public ResponseEntity<RestResponse<Object>> handlePermissionException(Exception ex) {
        RestResponse<Object> restResponse = new RestResponse<Object>();
        restResponse.setStatusCode(HttpStatus.FORBIDDEN.value());
        restResponse.setError(ex.getMessage());
        restResponse.setMessage("Forbidden");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(restResponse);
    }

}
