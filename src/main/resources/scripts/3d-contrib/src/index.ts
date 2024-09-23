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
    try {
        switch (level) {
            case 'NONE': return 0;
            case 'FIRST_QUARTILE': return 1;
            case 'SECOND_QUARTILE': return 2;
            case 'THIRD_QUARTILE': return 3;
            case 'FOURTH_QUARTILE': return 4;
            default:
                throw new Error('Invalid contribution level');
        }
    } catch (error) {
        console.error('Error in toNumberContributionLevel:', error);
        return -1; // 예외 발생 시 안전하게 처리
    }
};

const compare = (num1: number, num2: number): number => {
    return num1 - num2; // 비교 결과 반환 (오름차순 정렬)
};

// main 함수
const main = async () => {
    try {
        // Node.js 및 환경 정보 확인
        console.log(`Node.js 버전: ${process.version}`);
        console.log(`운영체제: ${process.platform}`);
        console.log(`현재 경로: ${process.cwd()}`);

        // deploy
        const githubData = process.env.GITHUB_DATA;
//         테스트 용도
//         const githubData = `{"data":{"repository":{"name":"P-ND","forkCount":0,"stargazerCount":0,"primaryLanguage":{"name":"Java","color":"#b07219"},"defaultBranchRef":{"name":"main","target":{"history":{"edges":[{"node":{"committedDate":"2024-06-25T02:11:02Z","additions":1,"deletions":1,"changedFiles":1,"author":{"name":"김혜진"}}},{"node":{"committedDate":"2024-06-19T19:38:29Z","additions":92,"deletions":75,"changedFiles":20,"author":{"name":"김혜진"}}},{"node":{"committedDate":"2024-06-19T19:34:17Z","additions":31,"deletions":20,"changedFiles":6,"author":{"name":"khyaejin"}}},{"node":{"committedDate":"2024-06-19T19:01:49Z","additions":27,"deletions":33,"changedFiles":6,"author":{"name":"khyaejin"}}},{"node":{"committedDate":"2024-06-19T18:28:03Z","additions":18,"deletions":18,"changedFiles":6,"author":{"name":"khyaejin"}}},{"node":{"committedDate":"2024-06-19T18:06:29Z","additions":2,"deletions":3,"changedFiles":2,"author":{"name":"khyaejin"}}},{"node":{"committedDate":"2024-06-19T18:01:59Z","additions":28,"deletions":15,"changedFiles":7,"author":{"name":"khyaejin"}}},{"node":{"committedDate":"2024-05-30T16:37:07Z","additions":27,"deletions":2,"changedFiles":2,"author":{"name":"김혜진"}}},{"node":{"committedDate":"2024-05-30T16:35:00Z","additions":27,"deletions":2,"changedFiles":2,"author":{"name":"khyaejin"}}},{"node":{"committedDate":"2024-05-30T07:24:24Z","additions":3,"deletions":4,"changedFiles":3,"author":{"name":"김혜진"}}},{"node":{"committedDate":"2024-05-30T07:23:28Z","additions":3,"deletions":4,"changedFiles":3,"author":{"name":"khyaejin"}}},{"node":{"committedDate":"2024-05-29T14:51:51Z","additions":10,"deletions":9,"changedFiles":2,"author":{"name":"김혜진"}}},{"node":{"committedDate":"2024-05-29T14:50:28Z","additions":10,"deletions":9,"changedFiles":2,"author":{"name":"khyaejin"}}},{"node":{"committedDate":"2024-05-29T14:42:04Z","additions":13,"deletions":6,"changedFiles":6,"author":{"name":"김혜진"}}},{"node":{"committedDate":"2024-05-29T14:41:53Z","additions":98,"deletions":41,"changedFiles":11,"author":{"name":"김혜진"}}},{"node":{"committedDate":"2024-05-29T14:35:47Z","additions":20,"deletions":12,"changedFiles":5,"author":{"name":"khyaejin"}}},{"node":{"committedDate":"2024-05-29T06:28:11Z","additions":24,"deletions":4,"changedFiles":3,"author":{"name":"Hyun seung Lee"}}},{"node":{"committedDate":"2024-05-29T06:27:28Z","additions":24,"deletions":4,"changedFiles":3,"author":{"name":"이현승"}}},{"node":{"committedDate":"2024-05-29T05:43:16Z","additions":16,"deletions":3,"changedFiles":1,"author":{"name":"Hyun seung Lee"}}},{"node":{"committedDate":"2024-05-29T05:42:30Z","additions":16,"deletions":3,"changedFiles":1,"author":{"name":"이현승"}}},{"node":{"committedDate":"2024-05-28T18:07:11Z","additions":0,"deletions":11,"changedFiles":1,"author":{"name":"khyaejin"}}},{"node":{"committedDate":"2024-05-28T16:06:31Z","additions":3,"deletions":3,"changedFiles":3,"author":{"name":"khyaejin"}}},{"node":{"committedDate":"2024-05-28T15:49:15Z","additions":1,"deletions":0,"changedFiles":1,"author":{"name":"김혜진"}}},{"node":{"committedDate":"2024-05-28T15:21:43Z","additions":2,"deletions":2,"changedFiles":1,"author":{"name":"김혜진"}}},{"node":{"committedDate":"2024-05-28T13:58:55Z","additions":13,"deletions":7,"changedFiles":2,"author":{"name":"khyaejin"}}},{"node":{"committedDate":"2024-05-28T10:08:09Z","additions":47,"deletions":28,"changedFiles":7,"author":{"name":"김혜진"}}},{"node":{"committedDate":"2024-05-28T10:03:53Z","additions":47,"deletions":28,"changedFiles":7,"author":{"name":"khyaejin"}}},{"node":{"committedDate":"2024-05-28T09:34:54Z","additions":18,"deletions":10,"changedFiles":6,"author":{"name":"김혜진"}}},{"node":{"committedDate":"2024-05-28T09:31:11Z","additions":18,"deletions":10,"changedFiles":6,"author":{"name":"khyaejin"}}},{"node":{"committedDate":"2024-05-28T08:05:39Z","additions":1,"deletions":1,"changedFiles":1,"author":{"name":"이현승"}}},{"node":{"committedDate":"2024-05-28T07:48:50Z","additions":68,"deletions":2,"changedFiles":5,"author":{"name":"Hyun seung Lee"}}},{"node":{"committedDate":"2024-05-28T07:24:41Z","additions":65,"deletions":1,"changedFiles":5,"author":{"name":"이현승"}}},{"node":{"committedDate":"2024-05-28T07:14:07Z","additions":1,"deletions":1,"changedFiles":1,"author":{"name":"이현승"}}},{"node":{"committedDate":"2024-05-28T07:06:19Z","additions":2,"deletions":0,"changedFiles":1,"author":{"name":"이현승"}}},{"node":{"committedDate":"2024-05-28T06:54:01Z","additions":2,"deletions":10,"changedFiles":2,"author":{"name":"이현승"}}},{"node":{"committedDate":"2024-05-28T06:51:25Z","additions":1,"deletions":10,"changedFiles":2,"author":{"name":"이현승"}}},{"node":{"committedDate":"2024-05-28T06:48:37Z","additions":12,"deletions":2,"changedFiles":3,"author":{"name":"이현승"}}},{"node":{"committedDate":"2024-05-28T06:43:58Z","additions":14,"deletions":11,"changedFiles":1,"author":{"name":"Hyun seung Lee"}}},{"node":{"committedDate":"2024-05-28T06:41:07Z","additions":14,"deletions":11,"changedFiles":1,"author":{"name":"이현승"}}},{"node":{"committedDate":"2024-05-27T20:32:06Z","additions":11,"deletions":11,"changedFiles":3,"author":{"name":"Hyun seung Lee"}}},{"node":{"committedDate":"2024-05-27T20:22:37Z","additions":5,"deletions":2,"changedFiles":2,"author":{"name":"이현승"}}},{"node":{"committedDate":"2024-05-27T20:11:38Z","additions":6,"deletions":9,"changedFiles":2,"author":{"name":"이현승"}}},{"node":{"committedDate":"2024-05-27T20:07:55Z","additions":7,"deletions":16,"changedFiles":2,"author":{"name":"이현승"}}},{"node":{"committedDate":"2024-05-27T20:03:51Z","additions":26,"deletions":7,"changedFiles":3,"author":{"name":"Hyun seung Lee"}}},{"node":{"committedDate":"2024-05-27T20:02:18Z","additions":10,"deletions":1,"changedFiles":2,"author":{"name":"이현승"}}},{"node":{"committedDate":"2024-05-27T19:46:39Z","additions":4,"deletions":0,"changedFiles":1,"author":{"name":"이현승"}}},{"node":{"committedDate":"2024-05-27T19:43:22Z","additions":13,"deletions":7,"changedFiles":2,"author":{"name":"이현승"}}},{"node":{"committedDate":"2024-05-27T19:27:34Z","additions":206,"deletions":8,"changedFiles":5,"author":{"name":"Hyun seung Lee"}}},{"node":{"committedDate":"2024-05-27T19:19:13Z","additions":137,"deletions":24,"changedFiles":3,"author":{"name":"이현승"}}},{"node":{"committedDate":"2024-05-27T18:17:46Z","additions":90,"deletions":5,"changedFiles":5,"author":{"name":"이현승"}}},{"node":{"committedDate":"2024-05-27T01:09:15Z","additions":73,"deletions":43,"changedFiles":4,"author":{"name":"김혜진"}}},{"node":{"committedDate":"2024-05-27T01:04:32Z","additions":10,"deletions":17,"changedFiles":2,"author":{"name":"khyaejin"}}},{"node":{"committedDate":"2024-05-27T00:40:39Z","additions":1,"deletions":1,"changedFiles":1,"author":{"name":"khyaejin"}}},{"node":{"committedDate":"2024-05-27T00:30:23Z","additions":37,"deletions":38,"changedFiles":3,"author":{"name":"khyaejin"}}},{"node":{"committedDate":"2024-05-26T23:41:37Z","additions":17,"deletions":13,"changedFiles":1,"author":{"name":"khyaejin"}}},{"node":{"committedDate":"2024-05-26T23:35:33Z","additions":35,"deletions":1,"changedFiles":2,"author":{"name":"khyaejin"}}},{"node":{"committedDate":"2024-05-26T23:04:58Z","additions":106,"deletions":126,"changedFiles":5,"author":{"name":"김혜진"}}},{"node":{"committedDate":"2024-05-26T23:03:09Z","additions":1,"deletions":1,"changedFiles":1,"author":{"name":"khyaejin"}}},{"node":{"committedDate":"2024-05-26T22:52:19Z","additions":44,"deletions":52,"changedFiles":1,"author":{"name":"khyaejin"}}},{"node":{"committedDate":"2024-05-26T22:51:59Z","additions":66,"deletions":78,"changedFiles":5,"author":{"name":"khyaejin"}}},{"node":{"committedDate":"2024-05-26T21:44:11Z","additions":58,"deletions":1,"changedFiles":4,"author":{"name":"김혜진"}}},{"node":{"committedDate":"2024-05-26T21:41:33Z","additions":58,"deletions":1,"changedFiles":4,"author":{"name":"khyaejin"}}},{"node":{"committedDate":"2024-05-26T20:59:07Z","additions":106,"deletions":24,"changedFiles":6,"author":{"name":"김혜진"}}},{"node":{"committedDate":"2024-05-26T20:52:14Z","additions":106,"deletions":24,"changedFiles":6,"author":{"name":"khyaejin"}}},{"node":{"committedDate":"2024-05-26T20:10:26Z","additions":73,"deletions":9,"changedFiles":4,"author":{"name":"김혜진"}}},{"node":{"committedDate":"2024-05-26T20:08:11Z","additions":73,"deletions":9,"changedFiles":4,"author":{"name":"khyaejin"}}},{"node":{"committedDate":"2024-05-26T18:24:53Z","additions":378,"deletions":11,"changedFiles":11,"author":{"name":"김혜진"}}},{"node":{"committedDate":"2024-05-26T18:13:18Z","additions":378,"deletions":11,"changedFiles":11,"author":{"name":"khyaejin"}}},{"node":{"committedDate":"2024-05-26T15:56:23Z","additions":1,"deletions":1,"changedFiles":1,"author":{"name":"김혜진"}}},{"node":{"committedDate":"2024-05-26T07:42:05Z","additions":1,"deletions":1,"changedFiles":1,"author":{"name":"김혜진"}}},{"node":{"committedDate":"2024-05-25T18:29:36Z","additions":77,"deletions":12,"changedFiles":5,"author":{"name":"김혜진"}}},{"node":{"committedDate":"2024-05-25T18:23:39Z","additions":77,"deletions":12,"changedFiles":5,"author":{"name":"khyaejin"}}},{"node":{"committedDate":"2024-05-25T12:32:53Z","additions":16,"deletions":10,"changedFiles":6,"author":{"name":"이현승"}}},{"node":{"committedDate":"2024-05-25T10:29:55Z","additions":74,"deletions":5,"changedFiles":5,"author":{"name":"Hyun seung Lee"}}},{"node":{"committedDate":"2024-05-25T10:28:47Z","additions":63,"deletions":0,"changedFiles":4,"author":{"name":"이현승"}}},{"node":{"committedDate":"2024-05-25T10:18:27Z","additions":11,"deletions":5,"changedFiles":2,"author":{"name":"이현승"}}},{"node":{"committedDate":"2024-05-25T10:10:58Z","additions":40,"deletions":0,"changedFiles":3,"author":{"name":"Hyun seung Lee"}}},{"node":{"committedDate":"2024-05-25T10:10:18Z","additions":40,"deletions":0,"changedFiles":3,"author":{"name":"이현승"}}},{"node":{"committedDate":"2024-05-25T10:05:21Z","additions":16,"deletions":11,"changedFiles":7,"author":{"name":"Hyun seung Lee"}}},{"node":{"committedDate":"2024-05-25T10:03:44Z","additions":12,"deletions":11,"changedFiles":4,"author":{"name":"이현승"}}},{"node":{"committedDate":"2024-05-25T09:29:18Z","additions":4,"deletions":0,"changedFiles":4,"author":{"name":"이현승"}}},{"node":{"committedDate":"2024-05-25T09:04:05Z","additions":49,"deletions":0,"changedFiles":4,"author":{"name":"Hyun seung Lee"}}},{"node":{"committedDate":"2024-05-25T09:01:38Z","additions":1,"deletions":1,"changedFiles":1,"author":{"name":"이현승"}}},{"node":{"committedDate":"2024-05-25T08:58:42Z","additions":44,"deletions":1,"changedFiles":3,"author":{"name":"이현승"}}},{"node":{"committedDate":"2024-05-25T08:52:16Z","additions":47,"deletions":227,"changedFiles":9,"author":{"name":"김혜진"}}},{"node":{"committedDate":"2024-05-25T08:48:39Z","additions":47,"deletions":227,"changedFiles":9,"author":{"name":"khyaejin"}}},{"node":{"committedDate":"2024-05-25T08:48:28Z","additions":6,"deletions":0,"changedFiles":2,"author":{"name":"이현승"}}},{"node":{"committedDate":"2024-05-25T08:40:59Z","additions":134,"deletions":4,"changedFiles":6,"author":{"name":"Hyun seung Lee"}}},{"node":{"committedDate":"2024-05-25T08:38:29Z","additions":17,"deletions":6,"changedFiles":1,"author":{"name":"이현승"}}},{"node":{"committedDate":"2024-05-25T08:10:51Z","additions":71,"deletions":29,"changedFiles":6,"author":{"name":"이현승"}}},{"node":{"committedDate":"2024-05-25T07:55:58Z","additions":160,"deletions":7,"changedFiles":7,"author":{"name":"김혜진"}}},{"node":{"committedDate":"2024-05-25T07:47:19Z","additions":160,"deletions":7,"changedFiles":7,"author":{"name":"khyaejin"}}},{"node":{"committedDate":"2024-05-25T07:33:13Z","additions":70,"deletions":0,"changedFiles":4,"author":{"name":"이현승"}}},{"node":{"committedDate":"2024-05-25T07:22:29Z","additions":7,"deletions":0,"changedFiles":1,"author":{"name":"이현승"}}},{"node":{"committedDate":"2024-05-25T06:55:35Z","additions":60,"deletions":1,"changedFiles":4,"author":{"name":"Hyun seung Lee"}}},{"node":{"committedDate":"2024-05-25T06:53:17Z","additions":1,"deletions":2,"changedFiles":1,"author":{"name":"이현승"}}},{"node":{"committedDate":"2024-05-25T06:50:16Z","additions":34,"deletions":2,"changedFiles":2,"author":{"name":"이현승"}}},{"node":{"committedDate":"2024-05-25T06:44:44Z","additions":23,"deletions":0,"changedFiles":2,"author":{"name":"이현승"}}},{"node":{"committedDate":"2024-05-25T06:33:41Z","additions":6,"deletions":1,"changedFiles":1,"author":{"name":"이현승"}}},{"node":{"committedDate":"2024-05-25T05:43:07Z","additions":1,"deletions":1,"changedFiles":1,"author":{"name":"김혜진"}}}]}}},"languages":{"edges":[{"node":{"name":"Java","color":"#b07219"},"size":126171}]}}}}`
        const githubData = ` {"data":{"repository":{"name":"All-together-front","forkCount":2,"stargazerCount":10,"primaryLanguage":{"name":"Python","color":"#3572A5"},"defaultBranchRef":{"name":"main","target":{"history":{"edges":[{"node":{"committedDate":"2023-08-17T18:32:36Z","additions":69,"deletions":94,"changedFiles":3,"author":{"name":"1123ksd1"}}},{"node":{"committedDate":"2023-08-17T17:51:44Z","additions":145,"deletions":19,"changedFiles":3,"author":{"name":"1123ksd1"}}},{"node":{"committedDate":"2023-08-17T16:41:17Z","additions":8,"deletions":8,"changedFiles":3,"author":{"name":"1123ksd1"}}},{"node":{"committedDate":"2023-08-17T16:41:14Z","additions":598,"deletions":10,"changedFiles":6,"author":{"name":"1123ksd1"}}},{"node":{"committedDate":"2023-08-17T13:27:00Z","additions":7,"deletions":5,"changedFiles":2,"author":{"name":"yebin"}}},{"node":{"committedDate":"2023-08-17T13:26:34Z","additions":7,"deletions":5,"changedFiles":2,"author":{"name":"benniejung"}}},{"node":{"committedDate":"2023-08-17T13:12:06Z","additions":5,"deletions":7,"changedFiles":4,"author":{"name":"yebin"}}},{"node":{"committedDate":"2023-08-17T13:11:18Z","additions":5,"deletions":7,"changedFiles":4,"author":{"name":"benniejung"}}},{"node":{"committedDate":"2023-08-17T13:03:10Z","additions":37,"deletions":2,"changedFiles":5,"author":{"name":"1123ksd1"}}},{"node":{"committedDate":"2023-08-17T12:34:02Z","additions":131,"deletions":113,"changedFiles":9,"author":{"name":"1123ksd1"}}},{"node":{"committedDate":"2023-08-17T12:33:55Z","additions":612,"deletions":57,"changedFiles":11,"author":{"name":"1123ksd1"}}},{"node":{"committedDate":"2023-08-17T12:31:50Z","additions":0,"deletions":1,"changedFiles":1,"author":{"name":"MinseoKangQ"}}},{"node":{"committedDate":"2023-08-17T06:25:56Z","additions":111,"deletions":100,"changedFiles":5,"author":{"name":"yebin"}}},{"node":{"committedDate":"2023-08-17T06:25:31Z","additions":111,"deletions":100,"changedFiles":5,"author":{"name":"benniejung"}}},{"node":{"committedDate":"2023-08-17T04:46:52Z","additions":20,"deletions":12,"changedFiles":4,"author":{"name":"Minseo Kang"}}},{"node":{"committedDate":"2023-08-17T04:07:19Z","additions":20,"deletions":12,"changedFiles":4,"author":{"name":"benniejung"}}},{"node":{"committedDate":"2023-08-16T20:37:13Z","additions":135,"deletions":103,"changedFiles":5,"author":{"name":"1123ksd1"}}},{"node":{"committedDate":"2023-08-16T20:37:08Z","additions":743,"deletions":0,"changedFiles":9,"author":{"name":"1123ksd1"}}},{"node":{"committedDate":"2023-08-16T20:07:59Z","additions":135,"deletions":103,"changedFiles":5,"author":{"name":"Minseo Kang"}}},{"node":{"committedDate":"2023-08-16T18:59:31Z","additions":27,"deletions":7,"changedFiles":5,"author":{"name":"1123ksd1"}}},{"node":{"committedDate":"2023-08-16T18:20:03Z","additions":324,"deletions":314,"changedFiles":3,"author":{"name":"1123ksd1"}}},{"node":{"committedDate":"2023-08-16T16:44:04Z","additions":611,"deletions":244,"changedFiles":17,"author":{"name":"1123ksd1"}}},{"node":{"committedDate":"2023-08-16T16:41:02Z","additions":583,"deletions":404,"changedFiles":12,"author":{"name":"1123ksd1"}}},{"node":{"committedDate":"2023-08-16T16:07:57Z","additions":135,"deletions":103,"changedFiles":5,"author":{"name":"benniejung"}}},{"node":{"committedDate":"2023-08-16T14:20:25Z","additions":3,"deletions":11,"changedFiles":2,"author":{"name":"MinseoKangQ"}}},{"node":{"committedDate":"2023-08-16T14:14:46Z","additions":6,"deletions":6,"changedFiles":3,"author":{"name":"MinseoKangQ"}}},{"node":{"committedDate":"2023-08-16T14:14:23Z","additions":0,"deletions":0,"changedFiles":0,"author":{"name":"MinseoKangQ"}}},{"node":{"committedDate":"2023-08-16T13:55:52Z","additions":3,"deletions":7,"changedFiles":1,"author":{"name":"MinseoKangQ"}}},{"node":{"committedDate":"2023-08-16T13:50:56Z","additions":6,"deletions":6,"changedFiles":3,"author":{"name":"benniejung"}}},{"node":{"committedDate":"2023-08-16T13:45:47Z","additions":126,"deletions":63,"changedFiles":4,"author":{"name":"MinseoKangQ"}}},{"node":{"committedDate":"2023-08-16T13:39:53Z","additions":376,"deletions":175,"changedFiles":10,"author":{"name":"MinseoKangQ"}}},{"node":{"committedDate":"2023-08-16T13:25:10Z","additions":32,"deletions":39,"changedFiles":3,"author":{"name":"MinseoKangQ"}}},{"node":{"committedDate":"2023-08-16T13:24:09Z","additions":550,"deletions":312,"changedFiles":21,"author":{"name":"benniejung"}}},{"node":{"committedDate":"2023-08-16T13:18:27Z","additions":122,"deletions":125,"changedFiles":3,"author":{"name":"benniejung"}}},{"node":{"committedDate":"2023-08-16T12:14:19Z","additions":223,"deletions":14,"changedFiles":7,"author":{"name":"benniejung"}}},{"node":{"committedDate":"2023-08-16T12:13:52Z","additions":61,"deletions":52,"changedFiles":5,"author":{"name":"benniejung"}}},{"node":{"committedDate":"2023-08-16T11:57:56Z","additions":223,"deletions":48,"changedFiles":4,"author":{"name":"MinseoKangQ"}}},{"node":{"committedDate":"2023-08-16T11:40:29Z","additions":2,"deletions":4,"changedFiles":3,"author":{"name":"MinseoKangQ"}}},{"node":{"committedDate":"2023-08-16T10:10:41Z","additions":197,"deletions":146,"changedFiles":15,"author":{"name":"MinseoKangQ"}}},{"node":{"committedDate":"2023-08-16T10:08:16Z","additions":37,"deletions":46,"changedFiles":5,"author":{"name":"benniejung"}}},{"node":{"committedDate":"2023-08-16T09:58:22Z","additions":3,"deletions":2,"changedFiles":1,"author":{"name":"MinseoKangQ"}}},{"node":{"committedDate":"2023-08-16T08:00:28Z","additions":198,"deletions":147,"changedFiles":15,"author":{"name":"benniejung"}}},{"node":{"committedDate":"2023-08-16T06:42:50Z","additions":46,"deletions":32,"changedFiles":2,"author":{"name":"MinseoKangQ"}}},{"node":{"committedDate":"2023-08-16T03:03:54Z","additions":21,"deletions":17,"changedFiles":5,"author":{"name":"MinseoKangQ"}}},{"node":{"committedDate":"2023-08-16T02:26:29Z","additions":222,"deletions":22,"changedFiles":11,"author":{"name":"Minseo Kang"}}},{"node":{"committedDate":"2023-08-15T23:35:43Z","additions":127,"deletions":107,"changedFiles":16,"author":{"name":"benniejung"}}},{"node":{"committedDate":"2023-08-15T23:33:20Z","additions":107,"deletions":17,"changedFiles":7,"author":{"name":"benniejung"}}},{"node":{"committedDate":"2023-08-15T19:47:06Z","additions":80,"deletions":39,"changedFiles":12,"author":{"name":"1123ksd1"}}},{"node":{"committedDate":"2023-08-15T18:08:41Z","additions":1384,"deletions":229,"changedFiles":35,"author":{"name":"1123ksd1"}}},{"node":{"committedDate":"2023-08-15T18:05:58Z","additions":59,"deletions":77,"changedFiles":9,"author":{"name":"1123ksd1"}}},{"node":{"committedDate":"2023-08-15T17:09:18Z","additions":119,"deletions":9,"changedFiles":6,"author":{"name":"benniejung"}}},{"node":{"committedDate":"2023-08-15T15:15:50Z","additions":1384,"deletions":226,"changedFiles":35,"author":{"name":"yebin"}}},{"node":{"committedDate":"2023-08-15T15:06:30Z","additions":656,"deletions":97,"changedFiles":14,"author":{"name":"yebin"}}},{"node":{"committedDate":"2023-08-15T15:05:03Z","additions":131,"deletions":40,"changedFiles":3,"author":{"name":"benniejung"}}},{"node":{"committedDate":"2023-08-15T14:52:10Z","additions":658,"deletions":97,"changedFiles":14,"author":{"name":"1123ksd1"}}},{"node":{"committedDate":"2023-08-15T13:32:57Z","additions":285,"deletions":113,"changedFiles":10,"author":{"name":"benniejung"}}},{"node":{"committedDate":"2023-08-15T08:18:40Z","additions":436,"deletions":80,"changedFiles":17,"author":{"name":"benniejung"}}},{"node":{"committedDate":"2023-08-15T00:56:52Z","additions":1059,"deletions":382,"changedFiles":17,"author":{"name":"benniejung"}}},{"node":{"committedDate":"2023-08-14T19:27:34Z","additions":896,"deletions":275,"changedFiles":14,"author":{"name":"1123ksd1"}}},{"node":{"committedDate":"2023-08-14T17:25:26Z","additions":219,"deletions":29,"changedFiles":7,"author":{"name":"benniejung"}}},{"node":{"committedDate":"2023-08-14T13:47:18Z","additions":191,"deletions":128,"changedFiles":5,"author":{"name":"1123ksd1"}}},{"node":{"committedDate":"2023-08-14T12:07:26Z","additions":2304,"deletions":1,"changedFiles":43,"author":{"name":"benniejung"}}},{"node":{"committedDate":"2023-08-14T11:24:27Z","additions":628,"deletions":518,"changedFiles":3,"author":{"name":"1123ksd1"}}},{"node":{"committedDate":"2023-08-14T10:38:28Z","additions":419,"deletions":61,"changedFiles":18,"author":{"name":"benniejung"}}},{"node":{"committedDate":"2023-08-13T15:47:50Z","additions":2193,"deletions":0,"changedFiles":41,"author":{"name":"1123ksd1"}}},{"node":{"committedDate":"2023-08-13T13:54:04Z","additions":867130,"deletions":0,"changedFiles":6932,"author":{"name":"yebin"}}},{"node":{"committedDate":"2023-08-13T12:11:17Z","additions":236,"deletions":0,"changedFiles":6,"author":{"name":"benniejung"}}},{"node":{"committedDate":"2023-08-12T15:25:50Z","additions":511,"deletions":86,"changedFiles":10,"author":{"name":"benniejung"}}},{"node":{"committedDate":"2023-08-11T15:48:48Z","additions":168,"deletions":23,"changedFiles":10,"author":{"name":"benniejung"}}},{"node":{"committedDate":"2023-08-11T09:34:13Z","additions":637,"deletions":11,"changedFiles":11,"author":{"name":"benniejung"}}},{"node":{"committedDate":"2023-08-10T13:10:53Z","additions":502,"deletions":175,"changedFiles":13,"author":{"name":"benniejung"}}},{"node":{"committedDate":"2023-08-10T03:00:26Z","additions":865371,"deletions":0,"changedFiles":6903,"author":{"name":"benniejung"}}},{"node":{"committedDate":"2023-08-07T09:21:48Z","additions":0,"deletions":0,"changedFiles":1,"author":{"name":"benniejung"}}},{"node":{"committedDate":"2023-08-07T09:15:20Z","additions":0,"deletions":0,"changedFiles":1,"author":{"name":"benniejung"}}},{"node":{"committedDate":"2023-08-05T18:22:45Z","additions":9,"deletions":0,"changedFiles":1,"author":{"name":"MinseoKangQ"}}},{"node":{"committedDate":"2023-08-05T18:22:03Z","additions":6,"deletions":1,"changedFiles":1,"author":{"name":"MinseoKangQ"}}},{"node":{"committedDate":"2023-08-05T18:19:50Z","additions":47,"deletions":1,"changedFiles":10,"author":{"name":"MinseoKangQ"}}},{"node":{"committedDate":"2023-08-05T17:35:12Z","additions":1,"deletions":0,"changedFiles":1,"author":{"name":"Minseo Kang"}}}]}}},"languages":{"edges":[{"node":{"name":"HTML","color":"#e34c26"},"size":271428},{"node":{"name":"CSS","color":"#563d7c"},"size":207768},{"node":{"name":"JavaScript","color":"#f1e05a"},"size":131641},{"node":{"name":"Python","color":"#3572A5"},"size":15626298},{"node":{"name":"PowerShell","color":"#012456"},"size":25697},{"node":{"name":"Batchfile","color":"#C1F12E"},"size":1376}]}}}}`

        if (!githubData) {
            throw new Error("GITHUB_DATA 환경 변수가 설정되지 않았습니다.");
        }

        // 회고 데이터
        // deploy
        let retrospect = process.env.GITHUB_DATA_RETRO;
//         let retrospect =
//         `<h1>🔄 All Together 프론트엔드 프로젝트 회고가이드 🔄</h1>
//
//          <h2>🌈 회고 방법 추천: <strong>KPT (Keep, Problem, Try)</strong></h2>
//          <p>All Together 프로젝트는 프론트엔드에서 유지보수성과 확장성에 중점을 두었기 때문에 <strong>KPT</strong> 방식을 추천드립니다. KPT는 현재 잘되고 있는 부분(Keep), 문제점(Problem), 시도해볼 점(Try)을 구분하여, 각 기능에 대한 피드백을 명확히 할 수 있습니다. 이 방식은 프론트엔드의 디테일한 피드백을 제공하는 데 효과적입니다.</p>
//
//          <h3>📢 회고 방법 설명</h3>
//          <p>KPT는 유지할 점, 문제점, 시도할 점을 나눠서 피드백을 작성합니다. 팀원들과 회의를 통해 개선 방안을 도출하고 구체적인 시도할 항목을 정의합니다.</p>
//
//          <h3>💡 프로젝트 개요</h3>
//          <p>All Together 프로젝트는 사용자 친화적인 웹 애플리케이션을 목표로 개발되었습니다.</p>
//
//          <h3>✅ AI 한줄 평가</h3>
//          <h4>👏 잘된 점</h4>
//          <ul>
//              <li>**모듈화된 컴포넌트 구조**: 유지보수와 확장성이 뛰어났습니다.</li>
//              <li>**협업 도구 사용**: GitHub의 Pull Request와 코드 리뷰로 효율성을 높였습니다.</li>
//          </ul>
//
//          <h4>🔧 개선할 점</h4>
//          <ul>
//              <li>**로딩 속도 최적화**: 성능 개선이 필요합니다.</li>
//              <li>**테스트 부족**: 더 많은 테스트 케이스가 필요합니다.</li>
//          </ul>
//
//          <h4>🚀 주요 교훈</h4>
//          <ul>
//              <li>**모듈화 중요성**: 재사용 가능하고 유지보수가 용이한 코드 구조를 유지하는 것이 핵심임을 배웠습니다.</li>
//          </ul>
//
//          <h3>💡 회고하면 좋을 것들</h3>
//          <ul>
//              <li>UI/UX 피드백 반영</li>
//              <li>기술 부채 관리</li>
//              <li>CI/CD 파이프라인 구축 경험</li>
//          </ul>`;

        // JSON 데이터를 파싱
        let parsedData;
        try {
            parsedData = JSON.parse(githubData);
            // console.log("ParsedData:", JSON.stringify(parsedData, null, 2));
        } catch (parseError) {
            console.error("Error parsing GITHUB_DATA JSON:", parseError);
            throw new Error("GITHUB_DATA JSON 파싱 중 오류 발생");
        }

        // 단일 레포지토리에 대한 정보 집계
        let repoInfo;
        try {
            repoInfo = aggregateRepo.aggregateRepositoryInfo(parsedData);
            // console.log('Aggregated Repository Info:', repoInfo); // 집계된 레포지토리 정보 출력
        } catch (repoError) {
            console.error('Error aggregating repository info:', repoError);
            throw new Error("레포지토리 정보 집계 중 오류 발생");
        }

        if (process.env.SETTING_JSON) {
            try {
                const settingFile = r.readSettingJson(process.env.SETTING_JSON);
                const settingInfos = 'length' in settingFile ? settingFile : [settingFile];
                for (const settingInfo of settingInfos) {
                    const fileName = settingInfo.fileName || `${repoInfo.name}-customize.svg`;
                    f.writeFile(
                        fileName,
                        create.createSvg(repoInfo, retrospect, settingInfo, false)
                    );
                }
            } catch (settingError) {
                console.error('Error reading or processing SETTING_JSON:', settingError);
                throw new Error("SETTING_JSON 처리 중 오류 발생");
            }
        } else {
            const settings = repoInfo.name.includes("Halloween")
                ? template.HalloweenSettings
                : template.NormalSettings;

            try {
                const generatedFileNameGreen = `${repoInfo.name}-green.svg`;
                f.writeFile(
                    generatedFileNameGreen,
                    create.createSvg(repoInfo, retrospect, settings, true)
                );
                console.log(generatedFileNameGreen);

                const generatedFileNameSeason = `${repoInfo.name}-season-animate.svg`;
                f.writeFile(
                    generatedFileNameSeason,
                    create.createSvg(repoInfo, retrospect, template.NorthSeasonSettings, true)
                );
                console.log(generatedFileNameSeason);

                const generatedFileNameNorthSeason = `${repoInfo.name}-north-season-animate.svg`;
                f.writeFile(
                    generatedFileNameNorthSeason,
                    create.createSvg(repoInfo, retrospect, template.NorthSeasonSettings, true)
                );
                console.log(generatedFileNameNorthSeason);

                const generatedFileNameSouthSeason = `${repoInfo.name}-south-season-animate.svg`;
                f.writeFile(
                    generatedFileNameSouthSeason,
                    create.createSvg(repoInfo, retrospect, template.SouthSeasonSettings, true)
                );
                console.log(generatedFileNameSouthSeason);

                const generatedFileNameNightView = `${repoInfo.name}-night-view.svg`;
                f.writeFile(
                    generatedFileNameNightView,
                    create.createSvg(repoInfo, retrospect, template.NightViewSettings, true)
                );
                console.log(generatedFileNameNightView);

                const generatedFileNameNightGreen = `${repoInfo.name}-night-green.svg`;
                f.writeFile(
                    generatedFileNameNightGreen,
                    create.createSvg(repoInfo, retrospect, template.NightGreenSettings, true)
                );
                console.log(generatedFileNameNightGreen);

                const generatedFileNameNighRainbow = `${repoInfo.name}-night-rainbow.svg`;
                f.writeFile(
                    generatedFileNameNighRainbow,
                    create.createSvg(repoInfo, retrospect, template.NightRainbowSettings, true)
                );
                console.log(generatedFileNameNighRainbow);

                const generatedFileNameGitblock = `${repoInfo.name}-gitblock.svg`;
                f.writeFile(
                    generatedFileNameGitblock,
                    create.createSvg(repoInfo, retrospect, template.GitBlockSettings, true)
                );
                console.log(generatedFileNameGitblock);
            } catch (writeError) {
                console.error('Error writing SVG files:', writeError);
                throw new Error("SVG 파일 생성 중 오류 발생");
            }
        }

    } catch (error) {
        console.error('Node.js 스크립트 오류:', error);
        console.log("Node.js 버전:", process.version);
        console.log("운영체제:", process.platform);
        console.log("현재 작업 경로:", process.cwd());
    }
};

main(); // main 함수 실행
