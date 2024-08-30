// GitHub GraphQL 클라이언트와 타입 정의 모듈 가져와서 데이터 처리 및 타입 관리 수행
import * as client from './github-graphql';
import * as type from './type';
import * as core from '@actions/core';
import * as aggregate from './aggregate-user-info';
import * as template from './color-template';
import * as create from './create-svg';
import * as f from './file-writer';
import * as r from './settings-reader';
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
    console.log('response:', response); //response 값 확인

    const user = response.data.user;
    const calendar = user.contributionsCollection.contributionCalendar.weeks
        .flatMap((week) => week.contributionDays)
        .map((day) => ({
            contributionCount: day.contributionCount,
            contributionLevel: toNumberContributionLevel(day.contributionLevel),
            date: new Date(day.date),
        }));

   const contributesLanguage: { [language: string]: { language: string, color: string, contributions: number } } = {};

   // `contributionCalendar`의 기여 정보를 활용하여 기여도 계산
   user.contributionsCollection.contributionCalendar.weeks
       .flatMap((week) => week.contributionDays)
       .forEach((day) => {
           const language = 'Unknown'; // 레포지토리 언어 데이터가 없을 경우 기본값 사용
           const color = OTHER_COLOR;  // 언어 색상 정보가 없을 경우 기본 색상 사용
           const contributions = day.contributionCount; // 날짜별 기여도 정보를 직접 사용

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

   console.log(contributesLanguage);
    // 총 기여도 계산
    const totalContributions = user.contributionsCollection.contributionCalendar.weeks
        .flatMap((week) => week.contributionDays)
        .reduce((total, day) => total + day.contributionCount, 0);

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
        totalContributions: totalContributions,
        totalCommitContributions: user.contributionsCollection.totalCommitContributions,
        totalIssueContributions: user.contributionsCollection.totalIssueContributions,
        totalPullRequestContributions: user.contributionsCollection.totalPullRequestContributions,
        totalPullRequestReviewContributions: user.contributionsCollection.totalPullRequestReviewContributions,
        totalRepositoryContributions: user.contributionsCollection.totalRepositoryContributions,
        totalForkCount: totalForkCount,
        totalStargazerCount: totalStargazerCount,
    };
        console.log('isHalloween:', user.contributionsCollection.contributionCalendar.isHalloween);
        console.log('calendar:', calendar);
        console.log('languages:', languages);
        console.log('totalContributions:', totalContributions);
        console.log('totalCommitContributions:', user.contributionsCollection.totalCommitContributions);
        console.log('totalIssueContributions:', user.contributionsCollection.totalIssueContributions);
        console.log('totalPullRequestContributions:', user.contributionsCollection.totalPullRequestContributions);
        console.log('totalPullRequestReviewContributions:', user.contributionsCollection.totalPullRequestReviewContributions);
        console.log('totalRepositoryContributions:', user.contributionsCollection.totalRepositoryContributions);
        console.log('totalForkCount:', totalForkCount);
        console.log('totalStargazerCount:', totalStargazerCount);
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

        // 깃허브 데이터 확인 로그
        //console.log('GitHub Data:', githubData);

        // JSON 데이터를 파싱
        const parsedData = JSON.parse(githubData);

        // 파싱 후 데이터 확인
        //console.log('Parsed Data Structure:', JSON.stringify(parsedData, null, 2));

        // aggregateUserInfo 함수 호출하여 사용자 정보 집계
        const userInfo = aggregateUserInfo(parsedData);
        console.log('Aggregated User Info:', userInfo); // 집계된 사용자 정보 출력

                if (process.env.SETTING_JSON) {
                    const settingFile = r.readSettingJson(process.env.SETTING_JSON);
                    const settingInfos =
                        'length' in settingFile ? settingFile : [settingFile];
                    for (const settingInfo of settingInfos) {
                        const fileName =
                            settingInfo.fileName || 'profile-customize.svg';
                        f.writeFile(
                            fileName,
                            create.createSvg(userInfo, settingInfo, false)
                        );
                    }
                } else {
                    const settings = userInfo.isHalloween
                        ? template.HalloweenSettings
                        : template.NormalSettings;

                    f.writeFile(
                        'profile-green-animate.svg',
                        create.createSvg(userInfo, settings, true)
                    );
                    f.writeFile(
                        'profile-green.svg',
                        create.createSvg(userInfo, settings, false)
                    );

                    // Northern hemisphere
                    f.writeFile(
                        'profile-season-animate.svg',
                        create.createSvg(userInfo, template.NorthSeasonSettings, true)
                    );
                    f.writeFile(
                        'profile-season.svg',
                        create.createSvg(userInfo, template.NorthSeasonSettings, false)
                    );

                    // Southern hemisphere
                    f.writeFile(
                        'profile-south-season-animate.svg',
                        create.createSvg(userInfo, template.SouthSeasonSettings, true)
                    );
                    f.writeFile(
                        'profile-south-season.svg',
                        create.createSvg(userInfo, template.SouthSeasonSettings, false)
                    );

                    f.writeFile(
                        'profile-night-view.svg',
                        create.createSvg(userInfo, template.NightViewSettings, true)
                    );

                    f.writeFile(
                        'profile-night-green.svg',
                        create.createSvg(userInfo, template.NightGreenSettings, true)
                    );

                    f.writeFile(
                        'profile-night-rainbow.svg',
                        create.createSvg(userInfo, template.NightRainbowSettings, true)
                    );

                    f.writeFile(
                        'profile-gitblock.svg',
                        create.createSvg(userInfo, template.GitBlockSettings, true)
                    );
                }

    } catch (error) {
        console.error('Error:', error);
    }
};

main(); // main 함수 실행
