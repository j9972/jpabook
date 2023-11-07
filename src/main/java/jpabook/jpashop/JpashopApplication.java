package jpabook.jpashop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// lombok을 설치하면 설정 -> 어노테이션 검색해서 활성화 박스 체크하
@SpringBootApplication
public class JpashopApplication {

	public static void main(String[] args) {
		Hello hello = new Hello();
		hello.setData("dasd");

		String data = hello.getData();
		System.out.println(data);

		SpringApplication.run(JpashopApplication.class, args);
	}

}
