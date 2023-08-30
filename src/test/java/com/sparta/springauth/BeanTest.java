package com.sparta.springauth;

import com.sparta.springauth.food.Food;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Primary;

@SpringBootTest
public class BeanTest {

//    @Autowired
//    Food food;
    // 자동 주입(Autowired)할 수 없다 Food타입의 Bean 객체가 하나 이싱이여서 주입할 수 없다.

    // 해결방법 1 등록할 이름을 정확이 명시해준다.
//    @Autowired
//    Food pizza;
//
//    @Autowired
//    Food chicken;
//
//    @Test
//    @DisplayName("테스트")
//    void test1() {
//        pizza.eat();
//        chicken.eat();
//    }

    // 해결방법 2 : 추가할 객체에 @Primary 써주기
//    @Autowired
//    Food food;
//
//    @Test
//    @DisplayName("테스트")
//    void test1() {
//        food.eat();
//    }

    // 해결방법 3 : 추가하고 싶은 객체와 객체 이름을 가지고 @Qualifier 써주기
    @Autowired
    @Qualifier("pizza")
    Food food;

    @Test
    @DisplayName("테스트")
    void test1() {
        food.eat();
    }
}
