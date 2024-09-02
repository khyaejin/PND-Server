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
    return num2 - num1; // 내림차순 정렬
};

export const aggregateRepositoryInfo = (
    response: client.ResponseType
): type.RepositoryInfo[] => {
    if (!response.data) {
        if (response.errors && response.errors.length) {
            throw new Error(response.errors[0].message);
        } else {
            throw new Error('JSON\n' + JSON.stringify(response, null, 2));
        }
    }

    const user = response.data.user;
    const repositories = user.repositories.nodes;

    const repositoryInfos: type.RepositoryInfo[] = repositories.map(repo => {
        const calendar = repo.contributionsCollection.contributionCalendar.weeks
            .flatMap((week: any) => week.contributionDays)
            .map((day: any) => ({
                contributionCount: day.contributionCount,
                contributionLevel: toNumberContributionLevel(day.contributionLevel),
                date: new Date(day.date),
            }));

        const contributesLanguage: { [language: string]: type.LangInfo } = {};

        repo.contributionsCollection.commitContributionsByRepository
            .filter((contribution: any) => contribution.repository.primaryLanguage)
            .forEach((contribution: any) => {
                const language = contribution.repository.primaryLanguage?.name || 'Unknown';
                const color = contribution.repository.primaryLanguage?.color || OTHER_COLOR;
                const contributions = contribution.contributions.totalCount;

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

        const totalContributions = calendar.reduce((total, day) => total + day.contributionCount, 0);
        const totalForkCount = repo.forkCount;
        const totalStargazerCount = repo.stargazerCount;

        return {
            forkCount: totalForkCount,
            stargazerCount: totalStargazerCount,
            primaryLanguage: repo.primaryLanguage,
            contributionsCollection: {
                contributionCalendar: {
                    weeks: repo.contributionsCollection.contributionCalendar.weeks
                },
                totalCommitContributions: repo.contributionsCollection.totalCommitContributions,
                totalIssueContributions: repo.contributionsCollection.totalIssueContributions,
                totalPullRequestContributions: repo.contributionsCollection.totalPullRequestContributions,
                totalPullRequestReviewContributions: repo.contributionsCollection.totalPullRequestReviewContributions,
                totalRepositoryContributions: repo.contributionsCollection.totalRepositoryContributions
            }
        };
    });

    return repositoryInfos;
};
