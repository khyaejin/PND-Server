import * as client from './github-graphql';
import * as type from './type';

const OTHER_COLOR = '#444444'; // 기본 색상 설정, 언어 색상이 없는 경우 사용

// 기여 레벨을 숫자로 변환하는 함수
const calculateContributionLevel = (changedFiles: number): number => {
    if (changedFiles >= 50) return 4;
    if (changedFiles >= 20) return 3;
    if (changedFiles >= 10) return 2;
    if (changedFiles >= 5) return 1;
    return 0;
};

export const aggregateRepositoryInfo = (
    repositoryData: any // GraphQL 쿼리로 가져온 레포지토리 데이터
): type.RepositoryInfo => {
    const repository = repositoryData.data.repository;

    const target = repository.defaultBranchRef?.target;
    console.log('Target content:', JSON.stringify(target, null, 2));

    const contributions: type.Contribution[] = target.history.edges.map(
        (edge: any) => ({
            date: new Date(edge.node.committedDate),
            count: edge.node.additions - edge.node.deletions,
            level: calculateContributionLevel(edge.node.changedFiles),
            contributionCount: edge.node.additions - edge.node.deletions,
            contributionLevel: calculateContributionLevel(edge.node.changedFiles)
        })
    );

    const totalContributions = contributions.reduce(
        (total, contribution) => total + contribution.count,
        0
    );

    const languages: type.LanguageInfo[] = repository.languages.edges.map(
        (langEdge: any) => ({
            language: langEdge.node.name,
            color: langEdge.node.color || OTHER_COLOR,
            contributions: langEdge.size, // 해당 언어로 작성된 코드의 양
        })
    ).sort((a: type.LanguageInfo, b: type.LanguageInfo) => b.contributions - a.contributions);

    const repositoryInfo: type.RepositoryInfo = {
        name: repository.name,
        forkCount: repository.forkCount,
        stargazerCount: repository.stargazerCount,
        primaryLanguage: repository.primaryLanguage,
        contributions: contributions,
        languages: languages,
        totalContributions: totalContributions,
        totalCommitContributions: contributions.length,
        totalIssueContributions: 0,
        totalPullRequestContributions: 0,
        totalPullRequestReviewContributions: 0,
        totalRepositoryContributions: totalContributions,
    };

    return repositoryInfo;
};
