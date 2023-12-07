package com.dogukandinler.EmployeeCheckerService;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CaptchaResponseDto {
    private String macAddress;
    private int responseTimeSeconds;

}
