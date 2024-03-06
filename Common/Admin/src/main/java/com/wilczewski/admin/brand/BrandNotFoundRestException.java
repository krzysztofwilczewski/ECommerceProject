package com.wilczewski.admin.brand;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Producent nie znaleziony")
public class BrandNotFoundRestException extends Exception{
}
