import * as core from '@actions/core';
import * as aggregate from './aggregate-user-info';
import * as template from './color-template';
import * as create from './create-svg';
import * as f from './file-writer';
import * as r from './settings-reader';

// eventsJson, events 선언
const eventsJson = process.argv[2];
const events = JSON.parse(eventsJson);

// events가 전달이 잘 되었나 확인하는 테스트 코드
console.log("Events: ", events);
export const main = async (): Promise<void> => {
    try {
        // 이벤트 데이터를 바탕으로 유저 정보를 집계
        const aggregatedInfo = aggregate.aggregateUserInfo(events);

        // 커스텀 설정 파일이 있는지 확인
        if (process.env.SETTING_JSON) {
            const settingFile = r.readSettingJson(process.env.SETTING_JSON);
            // 설정 파일이 배열 형식인지 확인하여 설정 정보를 배열로 변환
            const settingInfos = 'length' in settingFile ? settingFile : [settingFile];
            // 각 설정 정보를 기반으로 SVG 파일 생성
            for (const settingInfo of settingInfos) {
                const fileName = settingInfo.fileName || 'profile-customize.svg';
                f.writeFile(fileName, create.createSvg(aggregatedInfo, settingInfo, false));
            }
        } else {
            // 커스텀 설정이 없는 경우 기본 설정 사용 (할로윈 테마 또는 일반 테마)
            const settings = aggregatedInfo.isHalloween
                ? template.HalloweenSettings  // 할로윈 테마 사용
                : template.NormalSettings;    // 일반 테마 사용

            // 다양한 테마로 SVG 파일 생성 및 저장
            f.writeFile('profile-green-animate.svg', create.createSvg(aggregatedInfo, settings, true));
            f.writeFile('profile-green.svg', create.createSvg(aggregatedInfo, settings, false));

            // 북반구 계절 테마
            f.writeFile('profile-season-animate.svg', create.createSvg(aggregatedInfo, template.NorthSeasonSettings, true));
            f.writeFile('profile-season.svg', create.createSvg(aggregatedInfo, template.NorthSeasonSettings, false));

            // 남반구 계절 테마
            f.writeFile('profile-south-season-animate.svg', create.createSvg(aggregatedInfo, template.SouthSeasonSettings, true));
            f.writeFile('profile-south-season.svg', create.createSvg(aggregatedInfo, template.SouthSeasonSettings, false));

            // 야경 테마
            f.writeFile('profile-night-view.svg', create.createSvg(aggregatedInfo, template.NightViewSettings, true));
            f.writeFile('profile-night-green.svg', create.createSvg(aggregatedInfo, template.NightGreenSettings, true));
            f.writeFile('profile-night-rainbow.svg', create.createSvg(aggregatedInfo, template.NightRainbowSettings, true));

            // Git 블록 테마
            f.writeFile('profile-gitblock.svg', create.createSvg(aggregatedInfo, template.GitBlockSettings, true));
        }
    } catch (error) {
        // 오류 발생 시 오류 메시지를 출력하고 작업을 실패로 표시
        console.error(error);
        core.setFailed('error');
    }
};

void main();
