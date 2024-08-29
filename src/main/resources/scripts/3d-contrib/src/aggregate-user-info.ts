// 사용자 정보를 집계하는 모듈

import * as type from './type'; // 타입 정의 모듈 가져와서 타입 관리 수행

const OTHER_COLOR = '#444444'; // 기본 색상 설정

const compare = (num1: number, num2: number): number => { // 두 숫자 비교 함수 정의하여 결과 반환
    return num1 - num2; // 첫 번째 숫자에서 두 번째 숫자 뺀 결과 반환
};

export const aggregateUserInfo = (
    events: any[] // 이벤트 배열을 매개변수로 받음
): type.UserInfo => { // UserInfo 타입 반환

    /* // event 전달 확인을 위한 테스트 코드 --------
    console.log(events); // events의 상태 확인
    if (!events || !Array.isArray(events)) { // events가 유효한 배열인지 확인하여 오류 처리
        throw new Error("Invalid 'events' data"); // 유효하지 않으면 오류 발생
    }
    // ------------------------------- */

    const contributionCalendar = events.map(event => { // 이벤트를 기여 정보로 매핑하여 처리
        console.error("Invalid events data:", events); // 이벤트가 비어있을 경우 테스트 출력 수행

        return { // 기여 정보 객체 반환
            contributionCount: event.type === 'PushEvent' ? event.payload.commits.length : 1, // 푸시 이벤트인 경우 커밋 수, 아니면 1로 처리
            contributionLevel: 1,  // 기여 수준 간단히 1로 설정
            date: new Date(event.created_at), // 이벤트 생성 날짜 저장
        };
    });

    const languages: Array<type.LangInfo> = []; // 언어 정보 배열 초기화

    const totalCommitContributions = events.filter(event => event.type === 'PushEvent').length; // 푸시 이벤트 수 집계
    const totalIssueContributions = events.filter(event => event.type === 'IssuesEvent').length; // 이슈 이벤트 수 집계
    const totalPullRequestContributions = events.filter(event => event.type === 'PullRequestEvent').length; // 풀 리퀘스트 이벤트 수 집계
    const totalPullRequestReviewContributions = 0;  // 리뷰 기여 데이터 처리 필요할 수 있음

    const userInfo: type.UserInfo = { // 사용자 정보 객체 생성하여 필요한 값 할당
        isHalloween: false,  // 할로윈 여부 판단할 데이터 필요
        contributionCalendar: contributionCalendar, // 기여 캘린더 정보 할당
        contributesLanguage: languages,  // 언어 정보 추가 처리 필요할 수 있음
        totalContributions: contributionCalendar.length, // 총 기여 수 계산
        totalCommitContributions: totalCommitContributions, // 총 커밋 기여 수 계산
        totalIssueContributions: totalIssueContributions, // 총 이슈 기여 수 계산
        totalPullRequestContributions: totalPullRequestContributions, // 총 풀 리퀘스트 기여 수 계산
        totalPullRequestReviewContributions: totalPullRequestReviewContributions, // 총 풀 리퀘스트 리뷰 기여 수 할당
        totalRepositoryContributions: 0,  // 필요시 추가 계산할 총 리포지토리 기여 수
        totalForkCount: 0,  // 포크 수 추가 데이터 처리 필요
        totalStargazerCount: 0,  // 스타 개수도 추가 계산 필요시 처리
    };

    return userInfo; // 사용자 정보 반환
};
