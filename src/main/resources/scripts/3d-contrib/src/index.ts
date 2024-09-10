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
        // const githubData = process.env.GITHUB_DATA;
        const githubData = `{"data":{"repository":{"name":"YourSide-Server","forkCount":0,"stargazerCount":0,"primaryLanguage":{"name":"Java","color":"#b07219"},"defaultBranchRef":{"name":"main","target":{"history":{"edges":[{"node":{"committedDate":"2024-06-25T02:11:02Z","additions":1,"deletions":1,"changedFiles":1,"author":{"name":"김혜진"}}},{"node":{"committedDate":"2024-06-19T19:38:29Z","additions":92,"deletions":75,"changedFiles":20,"author":{"name":"김혜진"}}},{"node":{"committedDate":"2024-06-19T19:34:17Z","additions":31,"deletions":20,"changedFiles":6,"author":{"name":"khyaejin"}}},{"node":{"committedDate":"2024-06-19T19:01:49Z","additions":27,"deletions":33,"changedFiles":6,"author":{"name":"khyaejin"}}},{"node":{"committedDate":"2024-06-19T18:28:03Z","additions":18,"deletions":18,"changedFiles":6,"author":{"name":"khyaejin"}}},{"node":{"committedDate":"2024-06-19T18:06:29Z","additions":2,"deletions":3,"changedFiles":2,"author":{"name":"khyaejin"}}},{"node":{"committedDate":"2024-06-19T18:01:59Z","additions":28,"deletions":15,"changedFiles":7,"author":{"name":"khyaejin"}}},{"node":{"committedDate":"2024-05-30T16:37:07Z","additions":27,"deletions":2,"changedFiles":2,"author":{"name":"김혜진"}}},{"node":{"committedDate":"2024-05-30T16:35:00Z","additions":27,"deletions":2,"changedFiles":2,"author":{"name":"khyaejin"}}},{"node":{"committedDate":"2024-05-30T07:24:24Z","additions":3,"deletions":4,"changedFiles":3,"author":{"name":"김혜진"}}},{"node":{"committedDate":"2024-05-30T07:23:28Z","additions":3,"deletions":4,"changedFiles":3,"author":{"name":"khyaejin"}}},{"node":{"committedDate":"2024-05-29T14:51:51Z","additions":10,"deletions":9,"changedFiles":2,"author":{"name":"김혜진"}}},{"node":{"committedDate":"2024-05-29T14:50:28Z","additions":10,"deletions":9,"changedFiles":2,"author":{"name":"khyaejin"}}},{"node":{"committedDate":"2024-05-29T14:42:04Z","additions":13,"deletions":6,"changedFiles":6,"author":{"name":"김혜진"}}},{"node":{"committedDate":"2024-05-29T14:41:53Z","additions":98,"deletions":41,"changedFiles":11,"author":{"name":"김혜진"}}},{"node":{"committedDate":"2024-05-29T14:35:47Z","additions":20,"deletions":12,"changedFiles":5,"author":{"name":"khyaejin"}}},{"node":{"committedDate":"2024-05-29T06:28:11Z","additions":24,"deletions":4,"changedFiles":3,"author":{"name":"Hyun seung Lee"}}},{"node":{"committedDate":"2024-05-29T06:27:28Z","additions":24,"deletions":4,"changedFiles":3,"author":{"name":"이현승"}}},{"node":{"committedDate":"2024-05-29T05:43:16Z","additions":16,"deletions":3,"changedFiles":1,"author":{"name":"Hyun seung Lee"}}},{"node":{"committedDate":"2024-05-29T05:42:30Z","additions":16,"deletions":3,"changedFiles":1,"author":{"name":"이현승"}}},{"node":{"committedDate":"2024-05-28T18:07:11Z","additions":0,"deletions":11,"changedFiles":1,"author":{"name":"khyaejin"}}},{"node":{"committedDate":"2024-05-28T16:06:31Z","additions":3,"deletions":3,"changedFiles":3,"author":{"name":"khyaejin"}}},{"node":{"committedDate":"2024-05-28T15:49:15Z","additions":1,"deletions":0,"changedFiles":1,"author":{"name":"김혜진"}}},{"node":{"committedDate":"2024-05-28T15:21:43Z","additions":2,"deletions":2,"changedFiles":1,"author":{"name":"김혜진"}}},{"node":{"committedDate":"2024-05-28T13:58:55Z","additions":13,"deletions":7,"changedFiles":2,"author":{"name":"khyaejin"}}},{"node":{"committedDate":"2024-05-28T10:08:09Z","additions":47,"deletions":28,"changedFiles":7,"author":{"name":"김혜진"}}},{"node":{"committedDate":"2024-05-28T10:03:53Z","additions":47,"deletions":28,"changedFiles":7,"author":{"name":"khyaejin"}}},{"node":{"committedDate":"2024-05-28T09:34:54Z","additions":18,"deletions":10,"changedFiles":6,"author":{"name":"김혜진"}}},{"node":{"committedDate":"2024-05-28T09:31:11Z","additions":18,"deletions":10,"changedFiles":6,"author":{"name":"khyaejin"}}},{"node":{"committedDate":"2024-05-28T08:05:39Z","additions":1,"deletions":1,"changedFiles":1,"author":{"name":"이현승"}}},{"node":{"committedDate":"2024-05-28T07:48:50Z","additions":68,"deletions":2,"changedFiles":5,"author":{"name":"Hyun seung Lee"}}},{"node":{"committedDate":"2024-05-28T07:24:41Z","additions":65,"deletions":1,"changedFiles":5,"author":{"name":"이현승"}}},{"node":{"committedDate":"2024-05-28T07:14:07Z","additions":1,"deletions":1,"changedFiles":1,"author":{"name":"이현승"}}},{"node":{"committedDate":"2024-05-28T07:06:19Z","additions":2,"deletions":0,"changedFiles":1,"author":{"name":"이현승"}}},{"node":{"committedDate":"2024-05-28T06:54:01Z","additions":2,"deletions":10,"changedFiles":2,"author":{"name":"이현승"}}},{"node":{"committedDate":"2024-05-28T06:51:25Z","additions":1,"deletions":10,"changedFiles":2,"author":{"name":"이현승"}}},{"node":{"committedDate":"2024-05-28T06:48:37Z","additions":12,"deletions":2,"changedFiles":3,"author":{"name":"이현승"}}},{"node":{"committedDate":"2024-05-28T06:43:58Z","additions":14,"deletions":11,"changedFiles":1,"author":{"name":"Hyun seung Lee"}}},{"node":{"committedDate":"2024-05-28T06:41:07Z","additions":14,"deletions":11,"changedFiles":1,"author":{"name":"이현승"}}},{"node":{"committedDate":"2024-05-27T20:32:06Z","additions":11,"deletions":11,"changedFiles":3,"author":{"name":"Hyun seung Lee"}}},{"node":{"committedDate":"2024-05-27T20:22:37Z","additions":5,"deletions":2,"changedFiles":2,"author":{"name":"이현승"}}},{"node":{"committedDate":"2024-05-27T20:11:38Z","additions":6,"deletions":9,"changedFiles":2,"author":{"name":"이현승"}}},{"node":{"committedDate":"2024-05-27T20:07:55Z","additions":7,"deletions":16,"changedFiles":2,"author":{"name":"이현승"}}},{"node":{"committedDate":"2024-05-27T20:03:51Z","additions":26,"deletions":7,"changedFiles":3,"author":{"name":"Hyun seung Lee"}}},{"node":{"committedDate":"2024-05-27T20:02:18Z","additions":10,"deletions":1,"changedFiles":2,"author":{"name":"이현승"}}},{"node":{"committedDate":"2024-05-27T19:46:39Z","additions":4,"deletions":0,"changedFiles":1,"author":{"name":"이현승"}}},{"node":{"committedDate":"2024-05-27T19:43:22Z","additions":13,"deletions":7,"changedFiles":2,"author":{"name":"이현승"}}},{"node":{"committedDate":"2024-05-27T19:27:34Z","additions":206,"deletions":8,"changedFiles":5,"author":{"name":"Hyun seung Lee"}}},{"node":{"committedDate":"2024-05-27T19:19:13Z","additions":137,"deletions":24,"changedFiles":3,"author":{"name":"이현승"}}},{"node":{"committedDate":"2024-05-27T18:17:46Z","additions":90,"deletions":5,"changedFiles":5,"author":{"name":"이현승"}}},{"node":{"committedDate":"2024-05-27T01:09:15Z","additions":73,"deletions":43,"changedFiles":4,"author":{"name":"김혜진"}}},{"node":{"committedDate":"2024-05-27T01:04:32Z","additions":10,"deletions":17,"changedFiles":2,"author":{"name":"khyaejin"}}},{"node":{"committedDate":"2024-05-27T00:40:39Z","additions":1,"deletions":1,"changedFiles":1,"author":{"name":"khyaejin"}}},{"node":{"committedDate":"2024-05-27T00:30:23Z","additions":37,"deletions":38,"changedFiles":3,"author":{"name":"khyaejin"}}},{"node":{"committedDate":"2024-05-26T23:41:37Z","additions":17,"deletions":13,"changedFiles":1,"author":{"name":"khyaejin"}}},{"node":{"committedDate":"2024-05-26T23:35:33Z","additions":35,"deletions":1,"changedFiles":2,"author":{"name":"khyaejin"}}},{"node":{"committedDate":"2024-05-26T23:04:58Z","additions":106,"deletions":126,"changedFiles":5,"author":{"name":"김혜진"}}},{"node":{"committedDate":"2024-05-26T23:03:09Z","additions":1,"deletions":1,"changedFiles":1,"author":{"name":"khyaejin"}}},{"node":{"committedDate":"2024-05-26T22:52:19Z","additions":44,"deletions":52,"changedFiles":1,"author":{"name":"khyaejin"}}},{"node":{"committedDate":"2024-05-26T22:51:59Z","additions":66,"deletions":78,"changedFiles":5,"author":{"name":"khyaejin"}}},{"node":{"committedDate":"2024-05-26T21:44:11Z","additions":58,"deletions":1,"changedFiles":4,"author":{"name":"김혜진"}}},{"node":{"committedDate":"2024-05-26T21:41:33Z","additions":58,"deletions":1,"changedFiles":4,"author":{"name":"khyaejin"}}},{"node":{"committedDate":"2024-05-26T20:59:07Z","additions":106,"deletions":24,"changedFiles":6,"author":{"name":"김혜진"}}},{"node":{"committedDate":"2024-05-26T20:52:14Z","additions":106,"deletions":24,"changedFiles":6,"author":{"name":"khyaejin"}}},{"node":{"committedDate":"2024-05-26T20:10:26Z","additions":73,"deletions":9,"changedFiles":4,"author":{"name":"김혜진"}}},{"node":{"committedDate":"2024-05-26T20:08:11Z","additions":73,"deletions":9,"changedFiles":4,"author":{"name":"khyaejin"}}},{"node":{"committedDate":"2024-05-26T18:24:53Z","additions":378,"deletions":11,"changedFiles":11,"author":{"name":"김혜진"}}},{"node":{"committedDate":"2024-05-26T18:13:18Z","additions":378,"deletions":11,"changedFiles":11,"author":{"name":"khyaejin"}}},{"node":{"committedDate":"2024-05-26T15:56:23Z","additions":1,"deletions":1,"changedFiles":1,"author":{"name":"김혜진"}}},{"node":{"committedDate":"2024-05-26T07:42:05Z","additions":1,"deletions":1,"changedFiles":1,"author":{"name":"김혜진"}}},{"node":{"committedDate":"2024-05-25T18:29:36Z","additions":77,"deletions":12,"changedFiles":5,"author":{"name":"김혜진"}}},{"node":{"committedDate":"2024-05-25T18:23:39Z","additions":77,"deletions":12,"changedFiles":5,"author":{"name":"khyaejin"}}},{"node":{"committedDate":"2024-05-25T12:32:53Z","additions":16,"deletions":10,"changedFiles":6,"author":{"name":"이현승"}}},{"node":{"committedDate":"2024-05-25T10:29:55Z","additions":74,"deletions":5,"changedFiles":5,"author":{"name":"Hyun seung Lee"}}},{"node":{"committedDate":"2024-05-25T10:28:47Z","additions":63,"deletions":0,"changedFiles":4,"author":{"name":"이현승"}}},{"node":{"committedDate":"2024-05-25T10:18:27Z","additions":11,"deletions":5,"changedFiles":2,"author":{"name":"이현승"}}},{"node":{"committedDate":"2024-05-25T10:10:58Z","additions":40,"deletions":0,"changedFiles":3,"author":{"name":"Hyun seung Lee"}}},{"node":{"committedDate":"2024-05-25T10:10:18Z","additions":40,"deletions":0,"changedFiles":3,"author":{"name":"이현승"}}},{"node":{"committedDate":"2024-05-25T10:05:21Z","additions":16,"deletions":11,"changedFiles":7,"author":{"name":"Hyun seung Lee"}}},{"node":{"committedDate":"2024-05-25T10:03:44Z","additions":12,"deletions":11,"changedFiles":4,"author":{"name":"이현승"}}},{"node":{"committedDate":"2024-05-25T09:29:18Z","additions":4,"deletions":0,"changedFiles":4,"author":{"name":"이현승"}}},{"node":{"committedDate":"2024-05-25T09:04:05Z","additions":49,"deletions":0,"changedFiles":4,"author":{"name":"Hyun seung Lee"}}},{"node":{"committedDate":"2024-05-25T09:01:38Z","additions":1,"deletions":1,"changedFiles":1,"author":{"name":"이현승"}}},{"node":{"committedDate":"2024-05-25T08:58:42Z","additions":44,"deletions":1,"changedFiles":3,"author":{"name":"이현승"}}},{"node":{"committedDate":"2024-05-25T08:52:16Z","additions":47,"deletions":227,"changedFiles":9,"author":{"name":"김혜진"}}},{"node":{"committedDate":"2024-05-25T08:48:39Z","additions":47,"deletions":227,"changedFiles":9,"author":{"name":"khyaejin"}}},{"node":{"committedDate":"2024-05-25T08:48:28Z","additions":6,"deletions":0,"changedFiles":2,"author":{"name":"이현승"}}},{"node":{"committedDate":"2024-05-25T08:40:59Z","additions":134,"deletions":4,"changedFiles":6,"author":{"name":"Hyun seung Lee"}}},{"node":{"committedDate":"2024-05-25T08:38:29Z","additions":17,"deletions":6,"changedFiles":1,"author":{"name":"이현승"}}},{"node":{"committedDate":"2024-05-25T08:10:51Z","additions":71,"deletions":29,"changedFiles":6,"author":{"name":"이현승"}}},{"node":{"committedDate":"2024-05-25T07:55:58Z","additions":160,"deletions":7,"changedFiles":7,"author":{"name":"김혜진"}}},{"node":{"committedDate":"2024-05-25T07:47:19Z","additions":160,"deletions":7,"changedFiles":7,"author":{"name":"khyaejin"}}},{"node":{"committedDate":"2024-05-25T07:33:13Z","additions":70,"deletions":0,"changedFiles":4,"author":{"name":"이현승"}}},{"node":{"committedDate":"2024-05-25T07:22:29Z","additions":7,"deletions":0,"changedFiles":1,"author":{"name":"이현승"}}},{"node":{"committedDate":"2024-05-25T06:55:35Z","additions":60,"deletions":1,"changedFiles":4,"author":{"name":"Hyun seung Lee"}}},{"node":{"committedDate":"2024-05-25T06:53:17Z","additions":1,"deletions":2,"changedFiles":1,"author":{"name":"이현승"}}},{"node":{"committedDate":"2024-05-25T06:50:16Z","additions":34,"deletions":2,"changedFiles":2,"author":{"name":"이현승"}}},{"node":{"committedDate":"2024-05-25T06:44:44Z","additions":23,"deletions":0,"changedFiles":2,"author":{"name":"이현승"}}},{"node":{"committedDate":"2024-05-25T06:33:41Z","additions":6,"deletions":1,"changedFiles":1,"author":{"name":"이현승"}}},{"node":{"committedDate":"2024-05-25T05:43:07Z","additions":1,"deletions":1,"changedFiles":1,"author":{"name":"김혜진"}}}]}}},"languages":{"edges":[{"node":{"name":"Java","color":"#b07219"},"size":126171}]}}}}`
//         const token = process.env.GITHUB_TOKEN;

        if (!githubData) {
            throw new Error("GITHUB_DATA 환경 변수가 설정되지 않았습니다.");
        }

        // JSON 데이터를 파싱
        const parsedData = JSON.parse(githubData);

        console.log("ParsedData:", JSON.stringify(parsedData, null, 2));

        // 단일 레포지토리에 대한 정보 집계
        const repoInfo = aggregateRepo.aggregateRepositoryInfo(parsedData);
        console.log('Aggregated Repository Info:', repoInfo); // 집계된 레포지토리 정보 출력

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
//             f.writeFile(
//                 `profile-${repoInfo.name}-green.svg`,
//                 create.createSvg(repoInfo, settings, false)
//             );
//
//             // Northern hemisphere
//             f.writeFile(
//                 `profile-${repoInfo.name}-season-animate.svg`,
//                 create.createSvg(repoInfo, template.NorthSeasonSettings, true)
//             );
//             f.writeFile(
//                 `profile-${repoInfo.name}-season.svg`,
//                 create.createSvg(repoInfo, template.NorthSeasonSettings, false)
//             );
//
//             // Southern hemisphere
//             f.writeFile(
//                 `profile-${repoInfo.name}-south-season-animate.svg`,
//                 create.createSvg(repoInfo, template.SouthSeasonSettings, true)
//             );
//             f.writeFile(
//                 `profile-${repoInfo.name}-south-season.svg`,
//                 create.createSvg(repoInfo, template.SouthSeasonSettings, false)
//             );
//
//             f.writeFile(
//                 `profile-${repoInfo.name}-night-view.svg`,
//                 create.createSvg(repoInfo, template.NightViewSettings, true)
//             );
//
//             f.writeFile(
//                 `profile-${repoInfo.name}-night-green.svg`,
//                 create.createSvg(repoInfo, template.NightGreenSettings, true)
//             );
//
//             f.writeFile(
//                 `profile-${repoInfo.name}-night-rainbow.svg`,
//                 create.createSvg(repoInfo, template.NightRainbowSettings, true)
//             );
//
//             f.writeFile(
//                 `profile-${repoInfo.name}-gitblock.svg`,
//                 create.createSvg(repoInfo, template.GitBlockSettings, true)
//             );
        }

    } catch (error) {
        console.error('Error:', error);
    }
};

main(); // main 함수 실행
