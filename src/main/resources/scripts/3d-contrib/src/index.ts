// GitHub GraphQL 클라이언트와 타입 정의 모듈 가져와서 데이터 처리 및 타입 관리 수행
import * as client from './github-graphql';
import * as type from './type';

const OTHER_COLOR = '#444444'; // 기본 색상 설정, 언어 색상이 없는 경우 사용

const toNumberContributionLevel = (level: type.ContributionLevel): number => {
    switch (level) {
        case 'NONE': return 0;
        case 'FIRST_QUARTILE': return 1;
        case 'SECOND_QUARTILE': return 2;
        case 'THIRD_QUARTILE': return 3;
        case 'FOURTH_QUARTILE': return 4;
    }
};

const compare = (num1: number, num2: number): number => {
    return num1 - num2; // 비교 결과 반환 (오름차순 정렬)
};

export const aggregateUserInfo = (
    response: client.ResponseType // GitHub GraphQL API 응답 타입 매개변수로 받음
): type.UserInfo => {
    if (!response.data) {
        if (response.errors && response.errors.length) {
            throw new Error(response.errors[0].message);
        } else {
            throw new Error('JSON\n' + JSON.stringify(response, null, 2));
        }
    }

    const user = response.data.user;
    const calendar = user.contributionsCollection.contributionCalendar.weeks
        .flatMap((week) => week.contributionDays)
        .map((day) => ({
            contributionCount: day.contributionCount,
            contributionLevel: toNumberContributionLevel(day.contributionLevel),
            date: new Date(day.date),
        }));

    const contributesLanguage: { [language: string]: type.LangInfo } = {};
    user.contributionsCollection.commitContributionsByRepository
        .filter((repo) => repo.repository.primaryLanguage)
        .forEach((repo) => {
            const language = repo.repository.primaryLanguage?.name || '';
            const color = repo.repository.primaryLanguage?.color || OTHER_COLOR;
            const contributions = repo.contributions.totalCount;

            const info = contributesLanguage[language];
            if (info) {
                info.contributions += contributions;
            } else {
                contributesLanguage[language] = {
                    language: language,
                    color: color,
                    contributions: contributions,
                };
            }
        });

    const languages: Array<type.LangInfo> = Object.values(contributesLanguage)
        .sort((obj1, obj2) => -compare(obj1.contributions, obj2.contributions));

    const totalForkCount = user.repositories.nodes
        .map((node) => node.forkCount)
        .reduce((num1, num2) => num1 + num2, 0);

    const totalStargazerCount = user.repositories.nodes
        .map((node) => node.stargazerCount)
        .reduce((num1, num2) => num1 + num2, 0);

    const userInfo: type.UserInfo = {
        isHalloween: user.contributionsCollection.contributionCalendar.isHalloween,
        contributionCalendar: calendar,
        contributesLanguage: languages,
        totalContributions: user.contributionsCollection.contributionCalendar.totalContributions,
        totalCommitContributions: user.contributionsCollection.totalCommitContributions,
        totalIssueContributions: user.contributionsCollection.totalIssueContributions,
        totalPullRequestContributions: user.contributionsCollection.totalPullRequestContributions,
        totalPullRequestReviewContributions: user.contributionsCollection.totalPullRequestReviewContributions,
        totalRepositoryContributions: user.contributionsCollection.totalRepositoryContributions,
        totalForkCount: totalForkCount,
        totalStargazerCount: totalStargazerCount,
    };

    return userInfo;
};

const main = async () => {
    try {
        const githubData = process.env.GITHUB_DATA;
        const username = process.env.USERNAME;
        const token = process.env.GITHUB_TOKEN;

        if (!githubData) {
            throw new Error("GITHUB_DATA 환경 변수가 설정되지 않았습니다.");
        }

        // JSON 데이터를 파싱합니다.
        const parsedData = JSON.parse(githubData);

        console.log('GitHub Data:', parsedData);

        // aggregateUserInfo 함수 호출하여 사용자 정보 집계
        const userInfo = aggregateUserInfo(parsedData);
        console.log('Aggregated User Info:', userInfo); // 집계된 사용자 정보 출력

    } catch (error) {
        console.error('Error:', error);
    }
};

main(); // main 함수 실행
