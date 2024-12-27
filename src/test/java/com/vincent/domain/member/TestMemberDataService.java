package com.vincent.domain.member;

import com.vincent.domain.member.service.data.MemberDataService;

public class TestMemberDataService extends MemberDataService {

    public TestMemberDataService() {
        super(new TestMemberRepository());
    }
}
