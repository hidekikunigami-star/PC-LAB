package lab.exception;

import java.util.Map;

public record errorDTO(int status, String message, Map<String, String> errors) {
}
