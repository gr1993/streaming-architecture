package com.example.generator.service;

import com.example.generator.model.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class RandomRsvpGeneratorService {
    private final Random random = new Random();
    private final AtomicLong rsvpIdGenerator = new AtomicLong(1000);
    private final AtomicLong memberIdGenerator = new AtomicLong(1000);

    private final List<String> memberNames = List.of("김민수", "이영희", "박지훈", "최수진", "정성호", "강지연", "조민재", "윤서연", "장동건", "임수정");
    private final List<Event> events = List.of(
            new Event("2001", "Spring Boot 스터디"),
            new Event("2002", "React 기초 세미나"),
            new Event("2003", "AWS 클라우드 아키텍처 모임"),
            new Event("2004", "데이터 사이언스 입문"),
            new Event("2005", "프론트엔드 개발자 네트워킹")
    );
    private final List<Group> groups = List.of(
            new Group("서울 개발자 커뮤니티", "서울"),
            new Group("판교 IT 모임", "성남"),
            new Group("부산 코딩 클럽", "부산"),
            new Group("제주 노마드 모임", "제주")
    );
    private final List<Venue> venues = List.of(
            new Venue("강남역 스터디룸", 37.4979, 127.0276),
            new Venue("판교 테크노밸리 회의실", 37.4005, 127.1066),
            new Venue("부산 서면 카페", 35.1530, 129.0596),
            new Venue("제주 애월 공유오피스", 33.4623, 126.3200)
    );
    private final List<String> responses = List.of("yes", "no", "yes", "yes", "maybe");

    public RsvpEvent generateRandomEvent() {
        Member member = new Member(
                memberIdGenerator.incrementAndGet(),
                memberNames.get(random.nextInt(memberNames.size()))
        );
        Event event = events.get(random.nextInt(events.size()));
        Group group = groups.get(random.nextInt(groups.size()));
        Venue venue = venues.get(random.nextInt(venues.size()));
        String response = responses.get(random.nextInt(responses.size()));
        int guests = random.nextInt(3);
        long rsvpId = rsvpIdGenerator.incrementAndGet();
        long mtime = System.currentTimeMillis();

        return new RsvpEvent(member, event, group, venue, response, guests, rsvpId, mtime);
    }
}
