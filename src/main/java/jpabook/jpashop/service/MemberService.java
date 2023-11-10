package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
/**
 *     이렇게 class에 @Transactional 을 넣어도 되고, 메소드에 넣어도 된다
 *     읽기 메소드 같은 경우에는 readOnly = true 를 넣어서 성능 향상 시켜주기
 */
public class MemberService {

    private final MemberRepository memberRepository;

    /**
     * 회원가입
     */

    @Transactional
    public Long join(Member member) {
        validateDuplicateMember(member); // 중복 회원 체크
        memberRepository.save(member);
        return member.getId();
    }

    private void validateDuplicateMember(Member member) {
        /**
         * 같은 이름의 회원이 멀트쓰레드 상황에서 동시에 저장되는 것을 막기 위해서 name을 unique제약 조건을 걸어서 막기
         */
        List<Member> findMembers = memberRepository.findByName(member.getName());

        if(!findMembers.isEmpty()) {
            throw new IllegalStateException("이미 존재하는 회원");
        }
    }

    /**
     *전체 회원 조회
     */
    public List<Member> findMembers() {
        return memberRepository.findAll();
    }

    public Member findOne(Long memberId) {
        return memberRepository.findOne(memberId);
    }
}
