import * as core from '@actions/core';
import axios from 'axios';
import * as aggregate from './aggregate-user-info';
import * as template from './color-template';
import * as create from './create-svg';
import * as f from './file-writer';
import * as r from './settings-reader';

export const main = async (): Promise<void> => {
    try {
        const token = process.env.GITHUB_TOKEN;
        if (!token) {
            core.setFailed('GITHUB_TOKEN is empty');
            return;
        }
        const userName = process.argv.length >= 3 ? process.argv[2] : process.env.USERNAME;
        if (!userName) {
            core.setFailed('USERNAME is empty');
            return;
        }
        const maxRepos = process.env.MAX_REPOS ? Number(process.env.MAX_REPOS) : 100;
        if (Number.isNaN(maxRepos)) {
            core.setFailed('MAX_REPOS is NaN');
            return;
        }

        // GitHub API로 데이터 가져오기
        const headers = {
            Authorization: `token ${token}`,
            'Content-Type': 'application/json',
        };

        const userInfoResponse = await axios.get(
            `https://api.github.com/users/${userName}`,
            { headers }
        );
        const userInfo = userInfoResponse.data;

        const reposResponse = await axios.get(
            `https://api.github.com/users/${userName}/repos?per_page=${maxRepos}`,
            { headers }
        );
        const repos = reposResponse.data;

        const aggregatedInfo = aggregate.aggregateUserInfo({ userInfo, repos });

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