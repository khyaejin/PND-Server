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
    // 커밋 기록을 처리하여 기여 내역으로 변환
    const contributions: type.Contribution[] = repositoryData.defaultBranchRef.target.history.edges.map(
        (edge: any) => ({
            date: new Date(edge.node.committedDate),
            count: edge.node.additions - edge.node.deletions,
            level: calculateContributionLevel(edge.node.changedFiles),
        })
    );

    // 총 기여 수 계산
    const totalContributions = contributions.reduce(
        (total, contribution) => total + contribution.count,
        0
    );

    // 언어 사용 정보를 처리
    const languages: type.LanguageInfo[] = repositoryData.languages.edges.map(
        (langEdge: any) => ({
            language: langEdge.node.name,
            color: langEdge.node.color || OTHER_COLOR,
            contributions: langEdge.size, // 해당 언어로 작성된 코드의 양
        })
    ).sort((a, b) => b.contributions - a.contributions); // 기여 크기 순으로 정렬

    // 레포지토리 정보 생성
    const repositoryInfo: type.RepositoryInfo = {
        name: repositoryData.name,
        forkCount: repositoryData.forkCount,
        stargazerCount: repositoryData.stargazerCount,
        primaryLanguage: repositoryData.primaryLanguage,
        contributions: contributions,
        languages: languages,
        totalContributions: totalContributions,
        totalCommitContributions: contributions.length, // 커밋 개수로 처리
        totalIssueContributions: 0, // 이슈 데이터가 없으므로 기본값
        totalPullRequestContributions: 0, // 풀 리퀘스트 데이터가 없으므로 기본값
        totalPullRequestReviewContributions: 0, // 리뷰 데이터가 없으므로 기본값
        totalRepositoryContributions: totalContributions, // 전체 기여 계산
    };

    return repositoryInfo;
};
