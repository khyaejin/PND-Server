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
console.log(events)

export const main = async (): Promise<void> => {
    try {
        const token = process.env.GITHUB_TOKEN;
        if (!token) {
            core.setFailed('GITHUB_TOKEN is empty');
            return;
        }

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
