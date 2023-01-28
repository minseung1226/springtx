package hello.springtx.propagation;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.UnexpectedRollbackException;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
@Slf4j
@SpringBootTest
class MemberServiceTest {

    @Autowired MemberService memberService;
    @Autowired MemberRepository memberRepository;
    @Autowired LogRepository logRepository;

    /**
     * MemberService    :OFF
     * MemberRepository :ON
     * logRepository    :ON
     */
    @Test
    void outerTxOff_success(){
        String username = "outerTxOff_success";

        memberService.joinV1(username);

        assertTrue(memberRepository.find(username).isPresent());
        assertTrue(logRepository.find(username).isPresent());

    }

    /**
     * MemberService    :OFF
     * MemberRepository :ON
     * logRepository    :ON Exception
     */
    @Test
    void outerTxOff_fail(){
        String username = "로그예외_outerTxOff_success";


        assertThatThrownBy(()->memberService.joinV1(username)).isInstanceOf(RuntimeException.class);

        assertTrue(memberRepository.find(username).isPresent());
        assertTrue(logRepository.find(username).isEmpty());

    }

    /**
     * MemberService    :ON
     * MemberRepository :OFF
     * logRepository    :OFF
     */
    @Test
    void single_Tx(){
        String username = "single_Tx";

        memberService.joinV1(username);

        assertTrue(memberRepository.find(username).isPresent());
        assertTrue(logRepository.find(username).isPresent());

    }

    /**
     * MemberService    :ON
     * MemberRepository :ON
     * logRepository    :ON
     */
    @Test
    void outerTx_success(){
        String username = "outerTx_success";

        memberService.joinV1(username);

        assertTrue(memberRepository.find(username).isPresent());
        assertTrue(logRepository.find(username).isPresent());

    }

    /**
     * MemberService    :ON
     * MemberRepository :ON
     * logRepository    :ON Exception
     */
    @Test
    void outerTxOn_fail(){
        String username = "로그예외_outerTxOn_success";


        assertThatThrownBy(()->memberService.joinV1(username))
                .isInstanceOf(RuntimeException.class);

        assertTrue(memberRepository.find(username).isEmpty());
        assertTrue(logRepository.find(username).isEmpty());

    }

    /**
     * MemberService    :ON
     * MemberRepository :ON
     * logRepository    :ON Exception
     */
    @Test
    void recoverException_fail(){
        String username = "로그예외_recoverException_fail";


        assertThatThrownBy(()->memberService.joinV2(username))
                .isInstanceOf(UnexpectedRollbackException.class);

        assertTrue(memberRepository.find(username).isEmpty());
        assertTrue(logRepository.find(username).isEmpty());

    }

    /**
     * MemberService    :ON
     * MemberRepository :ON
     * logRepository    :ON(REQUIRES_NEW) Exception
     */
    @Test
    void recoverException_success(){
        String username = "로그예외_recoverException_success";


        memberService.joinV2(username);

        assertTrue(memberRepository.find(username).isPresent());
        assertTrue(logRepository.find(username).isEmpty());

    }


}