import * as core from '@actions/core';
import * as aggregate from './aggregate-user-info';
import * as template from './color-template';
import * as create from './create-svg';
import * as f from './file-writer';
import * as r from './settings-reader';

// Java에서 전달된 JSON 문자열을 받아 처리
const eventsJson = process.argv[2];
const events = JSON.parse(eventsJson);

// 이 데이터를 활용해 그래프 생성 로직을 수행
const aggregatedInfo = aggregate.aggregateUserInfo(events);

export const main = async (): Promise<void> => {
    try {
        if (process.env.SETTING_JSON) {
            const settingFile = r.readSettingJson(process.env.SETTING_JSON);
            const settingInfos = 'length' in settingFile ? settingFile : [settingFile];
            for (const settingInfo of settingInfos) {
                const fileName = settingInfo.fileName || 'profile-customize.svg';
                f.writeFile(fileName, create.createSvg(aggregatedInfo, settingInfo, false));
            }
        } else {
            const settings = aggregatedInfo.isHalloween
                ? template.HalloweenSettings
                : template.NormalSettings;

            f.writeFile('profile-green-animate.svg', create.createSvg(aggregatedInfo, settings, true));
            f.writeFile('profile-green.svg', create.createSvg(aggregatedInfo, settings, false));

            // Northern hemisphere
            f.writeFile('profile-season-animate.svg', create.createSvg(aggregatedInfo, template.NorthSeasonSettings, true));
            f.writeFile('profile-season.svg', create.createSvg(aggregatedInfo, template.NorthSeasonSettings, false));

            // Southern hemisphere
            f.writeFile('profile-south-season-animate.svg', create.createSvg(aggregatedInfo, template.SouthSeasonSettings, true));
            f.writeFile('profile-south-season.svg', create.createSvg(aggregatedInfo, template.SouthSeasonSettings, false));

            f.writeFile('profile-night-view.svg', create.createSvg(aggregatedInfo, template.NightViewSettings, true));
            f.writeFile('profile-night-green.svg', create.createSvg(aggregatedInfo, template.NightGreenSettings, true));
            f.writeFile('profile-night-rainbow.svg', create.createSvg(aggregatedInfo, template.NightRainbowSettings, true));
            f.writeFile('profile-gitblock.svg', create.createSvg(aggregatedInfo, template.GitBlockSettings, true));
        }
    } catch (error) {
        console.error(error);
        core.setFailed('error');
    }
};

void main();
