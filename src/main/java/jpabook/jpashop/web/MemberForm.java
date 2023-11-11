package jpabook.jpashop.web;


import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
public class MemberForm {
    private String name;

    private String city;
    private String street;
    private String zipcode;
}
