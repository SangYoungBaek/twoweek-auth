package com.sparta.springauth.food;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class Pizza implements Food {
    @Override
    @Qualifier("pizza")
    public void eat() {
        System.out.println("피자를 먹습니다.");
    }
}