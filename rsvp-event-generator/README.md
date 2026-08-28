# rsvp-event-generator

## 개요
Meetup 스트리밍 RSVP API 서버를 대체하기 위해 구현한 원천 데이터 생성 프로젝트이다.  

Meetup 스트리밍 RSVP는 Meetup에서 사용자가 특정 모임이나 이벤트에 대해 "참석(Yes)" 또는 "불참(No)"으로 RSVP(참석 여부를 회신하는 것)하면, 해당 행동이 하나의 이벤트 데이터로 생성되어 스트리밍되는 방식이다. 이 프로젝트에서는 이러한 RSVP 이벤트를 모방하여 스트리밍 데이터로 생성하고 제공한다.  

아래는 스트리밍 데이터의 예시이다.  
* http://localhost:8080/rsvp (SSE)
* ws://localhost:8080/rsvp

```json
{
  "member": {
    "member_id": 1001,
    "member_name": "김민수"
  },
  "event": {
    "event_id": "2001",
    "event_name": "Spring Boot 스터디"
  },
  "group": {
    "group_name": "서울 개발자 커뮤니티",
    "group_city": "서울"
  },
  "venue": {
    "venue_name": "강남역 스터디룸",
    "lat": 37.4979,
    "lon": 127.0276
  },
  "response": "yes",
  "guests": 0,
  "rsvp_id": 5001,
  "mtime": 1756368126069
}
```