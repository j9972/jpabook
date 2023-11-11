package jpabook.jpashop.service;

import jakarta.persistence.EntityManager;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional // rollback 하는 것이 기본 성
// TODO : java.io.FileNotFoundException: Could not open ServletContext resource [/application.yml] 해결
class MemberServiceTest {

    @Autowired MemberService memberService;
    @Autowired MemberRepository memberRepository;
    @Autowired EntityManager em;

    @Test
    /*
        @Rollback(false)
        이렇게 하면 insert문이 보이면서 디비에 데이터가 들어가는것을 볼 수 있다. [ 한번씩 확인용으로만 사용]
        그러나, 테스트 코드 작성할때 디비에 데이터가 남으면 안되다
     */
    public void 회원가입() throws Exception {
        // given
        Member member = new Member();
        member.setName("KIM");

        // when\
        Long savedId = memberService.join(member);

        // then
        // em.flush(); -> insert문을 볼 수 있다, 근데 테스트 코드라서 디비에 데이터가 넣어지면 안된다.
        assertEquals(member, memberRepository.findOne(savedId));
    }

    @Test
    public void 중복_회원_예외() throws Exception {
        // given
        Member member1 = new Member();
        member1.setName("kim");

        Member member2 = new Member();
        member2.setName("kim");

        // when
        memberService.join(member1);
        try {
            memberService.join(member2); // 예외가 발생해야 한다
        } catch (IllegalStateException e) {
            return;
        }


        // then
        fail("예외가 발생해야 한다");
    }

}