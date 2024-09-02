import * as client from './github-graphql';
import * as type from './type';
import * as core from '@actions/core';
import * as aggregateRepo from './aggregate-repo-info';
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

// main 함수
const main = async () => {
    try {
        const githubData = process.env.GITHUB_DATA;
        const username = process.env.USERNAME;
        const token = process.env.GITHUB_TOKEN;

        if (!githubData) {
            throw new Error("GITHUB_DATA 환경 변수가 설정되지 않았습니다.");
        }

        console.log('githubData:', githubData);

        // JSON 데이터를 파싱
        const parsedData = JSON.parse(githubData);

        console.log('Parsed Data:', parsedData);

        // 각 레포지토리별 사용자 정보 집계
        const repositoryInfos = aggregateRepo.aggregateRepositoryInfo(parsedData);
        console.log('Aggregated Repository Info:', repositoryInfos); // 집계된 레포지토리 정보 출력

        repositoryInfos.forEach(repoInfo => {
            if (process.env.SETTING_JSON) {
                const settingFile = r.readSettingJson(process.env.SETTING_JSON);
                const settingInfos =
                    'length' in settingFile ? settingFile : [settingFile];
                for (const settingInfo of settingInfos) {
                    const fileName =
                        settingInfo.fileName || `profile-${repoInfo.name}-customize.svg`;
                    f.writeFile(
                        fileName,
                        create.createSvg(repoInfo, settingInfo, false)
                    );
                }
            } else {
                const settings = repoInfo.name.includes("Halloween")
                    ? template.HalloweenSettings
                    : template.NormalSettings;

                f.writeFile(
                    `profile-${repoInfo.name}-green-animate.svg`,
                    create.createSvg(repoInfo, settings, true)
                );
                f.writeFile(
                    `profile-${repoInfo.name}-green.svg`,
                    create.createSvg(repoInfo, settings, false)
                );

                // Northern hemisphere
                f.writeFile(
                    `profile-${repoInfo.name}-season-animate.svg`,
                    create.createSvg(repoInfo, template.NorthSeasonSettings, true)
                );
                f.writeFile(
                    `profile-${repoInfo.name}-season.svg`,
                    create.createSvg(repoInfo, template.NorthSeasonSettings, false)
                );

                // Southern hemisphere
                f.writeFile(
                    `profile-${repoInfo.name}-south-season-animate.svg`,
                    create.createSvg(repoInfo, template.SouthSeasonSettings, true)
                );
                f.writeFile(
                    `profile-${repoInfo.name}-south-season.svg`,
                    create.createSvg(repoInfo, template.SouthSeasonSettings, false)
                );

                f.writeFile(
                    `profile-${repoInfo.name}-night-view.svg`,
                    create.createSvg(repoInfo, template.NightViewSettings, true)
                );

                f.writeFile(
                    `profile-${repoInfo.name}-night-green.svg`,
                    create.createSvg(repoInfo, template.NightGreenSettings, true)
                );

                f.writeFile(
                    `profile-${repoInfo.name}-night-rainbow.svg`,
                    create.createSvg(repoInfo, template.NightRainbowSettings, true)
                );

                f.writeFile(
                    `profile-${repoInfo.name}-gitblock.svg`,
                    create.createSvg(repoInfo, template.GitBlockSettings, true)
                );
            }
        });

    } catch (error) {
        console.error('Error:', error);
    }
};

main(); // main 함수 실행
