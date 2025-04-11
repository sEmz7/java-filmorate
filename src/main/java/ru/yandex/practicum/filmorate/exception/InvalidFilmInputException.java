package ru.yandex.practicum.filmorate.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidFilmInputException extends RuntimeException {
  public InvalidFilmInputException(String message) {
    super(message);
  }
}
