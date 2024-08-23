import * as type from './type';

const OTHER_COLOR = '#444444';

const compare = (num1: number, num2: number): number => {
    return num1 - num2;
};

export const aggregateUserInfo = (
    events: any[]
): type.UserInfo => {

    /* // event 전달 확인을 위한 테스트 코드 --------
    console.log(events); // events의 상태 확인
    if (!events || !Array.isArray(events)) {
        throw new Error("Invalid 'events' data");
    }
    // ------------------------------- */

    const contributionCalendar = events.map(event => {
        console.error("Invalid events data:", events); // 이벤트가 비어있다고 해서 넣은 테스트 코드

        return {
            contributionCount: event.type === 'PushEvent' ? event.payload.commits.length : 1,
            contributionLevel: 1,  // 기여 수준을 단순화하여 사용
            date: new Date(event.created_at),
        };
    });

    const languages: Array<type.LangInfo> = [];

    const totalCommitContributions = events.filter(event => event.type === 'PushEvent').length;
    const totalIssueContributions = events.filter(event => event.type === 'IssuesEvent').length;
    const totalPullRequestContributions = events.filter(event => event.type === 'PullRequestEvent').length;
    const totalPullRequestReviewContributions = 0;  // 리뷰 기여를 처리할 데이터가 필요할 수 있음

    const userInfo: type.UserInfo = {
        isHalloween: false,  // 할로윈 여부를 결정할 데이터 필요
        contributionCalendar: contributionCalendar,
        contributesLanguage: languages,  // 언어 정보는 추가 처리가 필요할 수 있음
        totalContributions: contributionCalendar.length,
        totalCommitContributions: totalCommitContributions,
        totalIssueContributions: totalIssueContributions,
        totalPullRequestContributions: totalPullRequestContributions,
        totalPullRequestReviewContributions: totalPullRequestReviewContributions,
        totalRepositoryContributions: 0,  // 필요시 추가 계산
        totalForkCount: 0,  // 포크 수는 추가 데이터 처리 필요
        totalStargazerCount: 0,  // 스타 개수도 마찬가지로 필요시 추가 계산
    };

    return userInfo;
};
