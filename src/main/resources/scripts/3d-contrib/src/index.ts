// 이벤트 데이터를 기반 사용자 정보 집계, 다양한 테마의 SVG 프로필 이미지를 생성 & 저장 기능을 수행하는 파일

import * as core from '@actions/core'; // GitHub Actions core 모듈 가져와서 오류 처리 수행
import * as aggregate from './aggregate-user-info'; // 사용자 정보 집계 모듈 가져와서 데이터 처리 수행
import * as template from './color-template'; // 다양한 테마 설정 정의 모듈 가져와서 테마 관리 수행
import * as create from './create-svg'; // SVG 파일 생성 모듈 가져와서 이미지 생성 수행
import * as f from './file-writer'; // 파일 작성 모듈 가져와서 생성된 파일 저장 수행
import * as r from './settings-reader'; // 설정 파일 읽기 모듈 가져와서 사용자 설정 처리 수행

// eventsJson, events 선언
const eventsJson = process.argv[2]; // 커맨드 라인 인자로 전달된 이벤트 데이터 JSON 가져와서 변수에 저장
const events = JSON.parse(eventsJson); // JSON 데이터를 JavaScript 객체로 변환하여 변수에 저장

// events가 전달이 잘 되었나 확인하는 테스트 코드
console.log("Events: ", events); // 이벤트 데이터 제대로 전달되었는지 콘솔에 출력하여 확인
export const main = async (): Promise<void> => { // main 함수 정의 및 비동기 작업 수행
    try {
        // 이벤트 데이터를 바탕으로 유저 정보를 집계
        const aggregatedInfo = aggregate.aggregateUserInfo(events); // 이벤트 데이터로 사용자 정보 집계 수행

        // 커스텀 설정 파일이 있는지 확인
        if (process.env.SETTING_JSON) { // 환경 변수에서 커스텀 설정 파일 경로 확인하여 처리 수행
            const settingFile = r.readSettingJson(process.env.SETTING_JSON); // 설정 파일 읽어서 객체로 저장
            // 설정 파일이 배열 형식인지 확인하여 설정 정보를 배열로 변환
            const settingInfos = 'length' in settingFile ? settingFile : [settingFile]; // 설정 파일을 배열 형태로 변환하여 처리 준비
            // 각 설정 정보를 기반으로 SVG 파일 생성
            for (const settingInfo of settingInfos) { // 각 설정 정보에 따라 SVG 파일 생성 및 저장 반복 수행
                const fileName = settingInfo.fileName || 'profile-customize.svg'; // 설정 파일에 파일명 없으면 기본 파일명 사용
                f.writeFile(fileName, create.createSvg(aggregatedInfo, settingInfo, false)); // 설정 정보로 SVG 파일 생성 후 저장 수행
            }
        } else {
            // 커스텀 설정이 없는 경우 기본 설정 사용 (할로윈 테마 또는 일반 테마)
            const settings = aggregatedInfo.isHalloween // 할로윈 여부에 따라 테마 설정 결정
                ? template.HalloweenSettings  // 할로윈 테마 사용
                : template.NormalSettings;    // 일반 테마 사용

            // 다양한 테마로 SVG 파일 생성 및 저장
            f.writeFile('profile-green-animate.svg', create.createSvg(aggregatedInfo, settings, true)); // 애니메이션 적용된 기본 테마 SVG 파일 생성 및 저장
            f.writeFile('profile-green.svg', create.createSvg(aggregatedInfo, settings, false)); // 애니메이션 없는 기본 테마 SVG 파일 생성 및 저장

            // 북반구 계절 테마
            f.writeFile('profile-season-animate.svg', create.createSvg(aggregatedInfo, template.NorthSeasonSettings, true)); // 애니메이션 적용된 북반구 계절 테마 SVG 파일 생성 및 저장
            f.writeFile('profile-season.svg', create.createSvg(aggregatedInfo, template.NorthSeasonSettings, false)); // 애니메이션 없는 북반구 계절 테마 SVG 파일 생성 및 저장

            // 남반구 계절 테마
            f.writeFile('profile-south-season-animate.svg', create.createSvg(aggregatedInfo, template.SouthSeasonSettings, true)); // 애니메이션 적용된 남반구 계절 테마 SVG 파일 생성 및 저장
            f.writeFile('profile-south-season.svg', create.createSvg(aggregatedInfo, template.SouthSeasonSettings, false)); // 애니메이션 없는 남반구 계절 테마 SVG 파일 생성 및 저장

            // 야경 테마
            f.writeFile('profile-night-view.svg', create.createSvg(aggregatedInfo, template.NightViewSettings, true)); // 야경 테마 SVG 파일 생성 및 저장
            f.writeFile('profile-night-green.svg', create.createSvg(aggregatedInfo, template.NightGreenSettings, true)); // 녹색 야경 테마 SVG 파일 생성 및 저장
            f.writeFile('profile-night-rainbow.svg', create.createSvg(aggregatedInfo, template.NightRainbowSettings, true)); // 무지개 야경 테마 SVG 파일 생성 및 저장

            // Git 블록 테마
            f.writeFile('profile-gitblock.svg', create.createSvg(aggregatedInfo, template.GitBlockSettings, true)); // Git 블록 테마 SVG 파일 생성 및 저장
        }
    } catch (error) {
        // 오류 발생 시 오류 메시지를 출력하고 작업을 실패로 표시
        console.error(error); // 오류 메시지 콘솔에 출력하여 문제 확인
        core.setFailed('error'); // GitHub Actions에서 작업 실패로 표시 수행
    }
};

void main(); // main 함수 호출하여 전체 작업 실행 수행
