package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Member;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MemberRepositoryTest {

    @Autowired MemberRepository memberRepository;

    // TODO : java.io.FileNotFoundException: Could not open ServletContext resource [/application.yml] 해결
    @Test
    @Transactional  // 트랜잭션안에서 save같이 데이터 변경이 있어야 한다
    @Rollback(value = false)
    public void testMember() throws Exception {

        // given
        Member member = new Member();
        //member.setUsername("memberA");

        // when
        //Long savedId = memberRepository.save(member);
        //Member findMember = memberRepository.find(savedId);

        // then
        //Assertions.assertThat(findMember.getId()).isEqualTo(member.getId());
        //Assertions.assertThat(findMember.getUsername()).isEqualTo(member.getUsername());

        /*
        findMember == member 비교 라고 보면 된다.
        transaction 내에서 같은 영속성 컨텍스트를 갖을때, 같은 id[식별자[를 갖으면 같은 엔티티이므로 == 이 true가 나온다
         */
        //Assertions.assertThat(findMember).isEqualTo(member);

    }


}