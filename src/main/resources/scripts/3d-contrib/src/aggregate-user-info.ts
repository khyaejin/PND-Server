// GitHub GraphQL 클라이언트와 타입 정의 모듈 가져와서 데이터 처리 및 타입 관리 수행
import * as client from './github-graphql';
import * as type from './type';

const OTHER_COLOR = '#444444'; // 기본 색상 설정, 언어 색상이 없는 경우 사용

const toNumberContributionLevel = (level: type.ContributionLevel): number => { // 기여 수준을 숫자 값으로 변환하는 함수 정의
    switch (level) { // 기여 수준에 따른 숫자 반환
        case 'NONE': // 기여가 없는 경우
            return 0;
        case 'FIRST_QUARTILE': // 기여 수준이 첫 번째 사분위
            return 1;
        case 'SECOND_QUARTILE': // 기여 수준이 두 번째 사분위
            return 2;
        case 'THIRD_QUARTILE': // 기여 수준이 세 번째 사분위
            return 3;
        case 'FOURTH_QUARTILE': // 기여 수준이 네 번째 사분위
            return 4;
    }
};

const compare = (num1: number, num2: number): number => { // 두 숫자 비교하여 정렬에 사용
    if (num1 < num2) { // 첫 번째 숫자가 더 작은 경우
        return -1; // 오름차순 정렬을 위한 -1 반환
    } else if (num1 > num2) { // 첫 번째 숫자가 더 큰 경우
        return 1; // 오름차순 정렬을 위한 1 반환
    } else { // 두 숫자가 같은 경우
        return 0; // 변경 없음
    }
};

export const aggregateUserInfo = (
    response: client.ResponseType // GitHub GraphQL API 응답 타입 매개변수로 받음
): type.UserInfo => { // UserInfo 타입 반환
    if (!response.data) { // 응답 데이터가 없는 경우 오류 처리
        if (response.errors && response.errors.length) { // 응답에 오류 메시지가 있는지 확인
            throw new Error(response.errors[0].message); // 첫 번째 오류 메시지로 예외 발생
        } else { // 오류 메시지가 없는 경우
            throw new Error('JSON\n' + JSON.stringify(response, null, 2)); // 응답 전체를 문자열로 변환하여 예외 발생
        }
    }

    const user = response.data.user; // 응답 데이터에서 사용자 정보 추출
    const calendar = user.contributionsCollection.contributionCalendar.weeks // 기여 캘린더에서 주 단위 데이터를 추출하여 평탄화
        .flatMap((week) => week.contributionDays) // 각 주의 기여 일자를 평탄화하여 배열로 변환
        .map((week) => ({ // 기여 일자를 필요한 정보로 매핑
            contributionCount: week.contributionCount, // 기여 횟수 할당
            contributionLevel: toNumberContributionLevel( // 기여 수준을 숫자로 변환하여 할당
                week.contributionLevel
            ),
            date: new Date(week.date), // 날짜 문자열을 Date 객체로 변환하여 할당
        }));

    const contributesLanguage: { [language: string]: type.LangInfo } = {}; // 기여한 언어 정보를 객체로 저장
    user.contributionsCollection.commitContributionsByRepository // 각 리포지토리별 커밋 기여 정보 반복 처리
        .filter((repo) => repo.repository.primaryLanguage) // 주요 언어가 있는 리포지토리만 필터링
        .forEach((repo) => { // 각 리포지토리에 대해 반복 처리
            const language = repo.repository.primaryLanguage?.name || ''; // 언어 이름 가져오기, 없으면 빈 문자열
            const color = repo.repository.primaryLanguage?.color || OTHER_COLOR; // 언어 색상 가져오기, 없으면 기본 색상 사용
            const contributions = repo.contributions.totalCount; // 리포지토리의 총 기여 횟수 가져오기

            const info = contributesLanguage[language]; // 해당 언어의 기여 정보 가져오기
            if (info) { // 이미 기여 정보가 있는 경우
                info.contributions += contributions; // 기존 기여 횟수에 현재 기여 횟수를 추가
            } else { // 새로운 언어인 경우
                contributesLanguage[language] = { // 새로운 언어 정보 생성하여 저장
                    language: language, // 언어 이름 저장
                    color: color, // 언어 색상 저장
                    contributions: contributions, // 기여 횟수 저장
                };
            }
        });

    const languages: Array<type.LangInfo> = Object.values( // 언어 정보 객체를 배열로 변환하여 정렬
        contributesLanguage
    ).sort((obj1, obj2) => -compare(obj1.contributions, obj2.contributions)); // 기여 횟수 기준으로 내림차순 정렬

    const totalForkCount = user.repositories.nodes // 리포지토리 노드에서 포크 수 계산
        .map((node) => node.forkCount) // 각 노드의 포크 수 추출
        .reduce((num1, num2) => num1 + num2, 0); // 포크 수 합산

    const totalStargazerCount = user.repositories.nodes // 리포지토리 노드에서 스타 개수 계산
        .map((node) => node.stargazerCount) // 각 노드의 스타 개수 추출
        .reduce((num1, num2) => num1 + num2, 0); // 스타 개수 합산

    const userInfo: type.UserInfo = { // 최종 사용자 정보 객체 생성하여 필요한 값 할당
        isHalloween: // 할로윈 여부 데이터 할당
            user.contributionsCollection.contributionCalendar.isHalloween,
        contributionCalendar: calendar, // 기여 캘린더 데이터 할당
        contributesLanguage: languages, // 기여한 언어 정보 할당
        totalContributions: // 총 기여 횟수 할당
            user.contributionsCollection.contributionCalendar.totalContributions,
        totalCommitContributions: // 총 커밋 기여 횟수 할당
            user.contributionsCollection.totalCommitContributions,
        totalIssueContributions: // 총 이슈 기여 횟수 할당
            user.contributionsCollection.totalIssueContributions,
        totalPullRequestContributions: // 총 풀 리퀘스트 기여 횟수 할당
            user.contributionsCollection.totalPullRequestContributions,
        totalPullRequestReviewContributions: // 총 풀 리퀘스트 리뷰 기여 횟수 할당
            user.contributionsCollection.totalPullRequestReviewContributions,
        totalRepositoryContributions: // 총 리포지토리 기여 횟수 할당
            user.contributionsCollection.totalRepositoryContributions,
        totalForkCount: totalForkCount, // 총 포크 수 할당
        totalStargazerCount: totalStargazerCount, // 총 스타 개수 할당
    };

    return userInfo; // 사용자 정보 반환
};
