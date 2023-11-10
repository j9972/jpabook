package jpabook.jpashop.domain;

import jakarta.persistence.Embeddable;
import lombok.Getter;

@Embeddable // 내장 타입
@Getter
public class Address {

    /*
    필드가 더 많아진다면 더더욱 지저분하게 될 것입니다.
    그렇다면 어떻게하면 조금 더 읽기 쉬운 코드로 만들 수 있을까요?
    -> 하나의 객체로써 아래 3가지 필드를 사용하고 싶기에 내장 타입을 사용한 것
     */
    private String city;
    private String street;
    private String zipcode;

    protected Address() {
        // 기본 생성자가 많이 사용되는 기술들이 많기에 만들어 주는 것이 좋다.
        // protected로 설정해야 안전하다
    }

    /*
        값 같은 경우는 변경 불가능하게 처음에 데이터를 주고, setter로 변경되지 않도록 한다.
     */
    public Address(String city, String street, String zipcode) {
        this.city = city;
        this.street = street;
        this.zipcode = zipcode;
    } }
